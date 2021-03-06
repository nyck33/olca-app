package org.openlca.app.results.grouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.openlca.app.M;
import org.openlca.app.components.ResultFlowCombo;
import org.openlca.app.results.contributions.ContributionChart;
import org.openlca.app.util.Labels;
import org.openlca.app.util.UI;
import org.openlca.app.viewers.BaseLabelProvider;
import org.openlca.app.viewers.combo.AbstractComboViewer;
import org.openlca.app.viewers.combo.ImpactCategoryViewer;
import org.openlca.core.matrix.IndexFlow;
import org.openlca.core.model.descriptors.ImpactCategoryDescriptor;
import org.openlca.core.results.Contribution;
import org.openlca.core.results.ContributionResult;
import org.openlca.core.results.Contributions;
import org.openlca.core.results.GroupingContribution;
import org.openlca.core.results.ProcessGrouping;

class GroupResultSection {

	private final int FLOW = 0;
	private final int IMPACT = 1;
	private int resultType = 0;

	private List<ProcessGrouping> groups;
	private ContributionResult result;
	private ResultFlowCombo flowViewer;
	private ImpactCategoryViewer impactViewer;
	private ContributionChart chart;
	private GroupResultTable table;

	public GroupResultSection(List<ProcessGrouping> groups, ContributionResult result) {
		this.groups = groups;
		this.result = result;
	}

	public void update() {
		Object selection;
		String unit;
		if (resultType == FLOW) {
			IndexFlow flow = flowViewer.getSelected();
			unit = Labels.refUnit(flow);
			selection = flow;
		} else {
			ImpactCategoryDescriptor impact = impactViewer.getSelected();
			unit = impact.referenceUnit;
			selection = impact;
		}
		updateResults(selection, unit);
	}

	private void updateResults(Object selection, String unit) {
		if (selection != null && table != null) {
			List<Contribution<ProcessGrouping>> items = calculate(selection);
			Contributions.sortDescending(items);
			table.setInput(items, unit);
			List<Contribution<?>> chartData = new ArrayList<>();
			chartData.addAll(items);
		}
	}

	private List<Contribution<ProcessGrouping>> calculate(Object o) {
		GroupingContribution calc = new GroupingContribution(result, groups);
		if (o instanceof IndexFlow)
			return calc.calculate((IndexFlow) o);
		if (o instanceof ImpactCategoryDescriptor)
			return calc.calculate((ImpactCategoryDescriptor) o);
		return Collections.emptyList();
	}

	public void render(Composite parent, FormToolkit tk) {
		Section section = UI.section(parent, tk, M.Results);
		UI.gridData(section, true, true);
		Composite comp = UI.sectionClient(section, tk);
		UI.gridLayout(comp, 1);
		createCombos(tk, comp);
		table = new GroupResultTable(comp);
		chart = ContributionChart.create(comp, tk);
		chart.setLabel(new BaseLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((ProcessGrouping) element).name;
			}
		});
		update();
	}

	private void createCombos(FormToolkit toolkit, Composite client) {
		Composite composite = toolkit.createComposite(client);
		UI.gridData(composite, true, false);
		UI.gridLayout(composite, 2);
		createFlowViewer(toolkit, composite);
		if (result.hasImpactResults())
			createImpact(toolkit, composite);
	}

	private void createFlowViewer(FormToolkit toolkit, Composite parent) {
		Button flowsCheck = toolkit.createButton(parent, M.Flows, SWT.RADIO);
		flowsCheck.setSelection(true);
		flowViewer = new ResultFlowCombo(parent);
		List<IndexFlow> flows = result.getFlows();
		flowViewer.setInput(flows);
		flowViewer.addSelectionChangedListener(e -> update());
		if (flows.size() > 0) {
			flowViewer.select(flows.get(0));
		}
		new ResultTypeCheck(flowViewer, flowsCheck, FLOW);
	}

	private void createImpact(FormToolkit toolkit, Composite parent) {
		Button impactCheck = toolkit.createButton(parent, M.ImpactCategories, SWT.RADIO);
		impactViewer = new ImpactCategoryViewer(parent);
		impactViewer.setEnabled(false);
		List<ImpactCategoryDescriptor> impacts = result.getImpacts();
		impactViewer.setInput(impacts);
		impactViewer.addSelectionChangedListener((e) -> update());
		if (impacts.size() > 0) {
			impactViewer.select(impacts.get(0));
		}
		new ResultTypeCheck(impactViewer, impactCheck, IMPACT);
	}

	private class ResultTypeCheck implements SelectionListener {

		private AbstractComboViewer<?> viewer;
		private Button check;
		private int type;

		public ResultTypeCheck(AbstractComboViewer<?> viewer, Button check, int type) {
			this.viewer = viewer;
			this.check = check;
			this.type = type;
			check.addSelectionListener(this);
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (check.getSelection()) {
				viewer.setEnabled(true);
				resultType = this.type;
				update();
			} else
				viewer.setEnabled(false);
		}
	}
}
