package org.openlca.app.components;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.openlca.app.Messages;
import org.openlca.app.util.Colors;
import org.openlca.app.util.Error;
import org.openlca.app.util.Labels;
import org.openlca.app.util.UI;
import org.openlca.core.model.Uncertainty;
import org.openlca.core.model.UncertaintyType;
import org.openlca.expressions.FormulaInterpreter;
import org.openlca.expressions.Scope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Dialog for editing uncertainty informations . */
public class UncertaintyDialog extends Dialog {

	private Logger log = LoggerFactory.getLogger(getClass());

	private FormToolkit toolkit;
	private UncertaintyType[] types = UncertaintyType.values();
	private Combo combo;
	private StackLayout stackLayout;

	private UncertaintyPanel[] clients;
	private UncertaintyPanel selectedClient;
	private Scope interpreterScope;

	private Uncertainty uncertainty;
	private double defaultMean;

	public UncertaintyDialog(Shell parentShell, Uncertainty initial) {
		super(parentShell);
		toolkit = new FormToolkit(parentShell.getDisplay());
		this.uncertainty = initial.clone();
		if (uncertainty.getParameter1Value() != null)
			defaultMean = uncertainty.getParameter1Value();
	}

	public Uncertainty getUncertainty() {
		return uncertainty;
	}

	public void setInterpreter(FormulaInterpreter interpreter, long scope) {
		if (!interpreter.hasScope(scope))
			this.interpreterScope = interpreter.getGlobalScope();
		else
			this.interpreterScope = interpreter.getScope(scope);
	}

	@Override
	protected void okPressed() {
		uncertainty = selectedClient.fetchUncertainty();
		super.okPressed();
	}

	@Override
	protected Control createDialogArea(Composite root) {
		getShell().setText(Messages.Uncertainty);
		toolkit.adapt(root);
		Composite area = (Composite) super.createDialogArea(root);
		toolkit.adapt(area);
		Composite container = toolkit.createComposite(area);
		UI.gridData(container, true, true);
		UI.gridLayout(container, 1);
		createCombo(container);
		createCompositeStack(container);
		initComposite();
		getShell().pack();
		UI.center(getParentShell(), getShell());
		return area;
	}

	private void createCombo(Composite container) {
		Composite comboComposite = toolkit.createComposite(container);
		UI.gridData(comboComposite, true, false);
		UI.gridLayout(comboComposite, 2);
		combo = UI.formCombo(comboComposite, toolkit,
				Messages.UncertaintyDistribution);
		String[] items = new String[types.length];
		int idx = 0;
		for (int i = 0; i < items.length; i++) {
			UncertaintyType type = types[i];
			items[i] = Labels.uncertaintyType(type);
			if (uncertainty != null
					&& uncertainty.getDistributionType() == type)
				idx = i;
		}
		combo.setItems(items);
		combo.select(idx);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				initComposite();
			}
		});
	}

	private void initComposite() {
		int item = combo.getSelectionIndex();
		if (item == -1)
			return;
		selectedClient = clients[item];
		stackLayout.topControl = selectedClient.composite;
		getShell().layout(true, true);
		getShell().pack();
	}

	private void createCompositeStack(Composite container) {
		Composite stack = toolkit.createComposite(container);
		UI.gridData(stack, true, true);
		stackLayout = new StackLayout();
		stack.setLayout(stackLayout);
		clients = new UncertaintyPanel[types.length];
		for (int i = 0; i < types.length; i++) {
			Composite composite = toolkit.createComposite(stack);
			UI.gridLayout(composite, 2);
			UncertaintyPanel client = new UncertaintyPanel(composite, types[i]);
			clients[i] = client;
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		toolkit.adapt(parent);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
		createButton(parent, IDialogConstants.HELP_ID, "Test", false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, false);
		getShell().pack();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 300);
	}

	@Override
	public boolean close() {
		if (toolkit != null)
			toolkit.dispose();
		return super.close();
	}

	private class UncertaintyPanel {

		private Composite composite;
		private Uncertainty _uncertainty;
		private Text[] texts;

		UncertaintyPanel(Composite composite, UncertaintyType type) {
			this.composite = composite;
			if (type == uncertainty.getDistributionType())
				_uncertainty = uncertainty;
			else
				_uncertainty = createUncertainty(type);
			if (type != UncertaintyType.NONE)
				createTextFields();
			else {
				Label label = toolkit.createLabel(composite,
						Messages.NoDistribution);
				label.setForeground(Colors.getDarkGray());
			}
		}

		private void createTextFields() {
			String[] labels = getLabels();
			texts = new Text[labels.length];
			for (int param = 1; param <= 3; param++) {
				if (!hasParameter(param))
					continue;
				String label = labels[param - 1];
				Text text = UI.formText(composite, toolkit, label);
				text.setText(initialValue(param));
				texts[param - 1] = text;
			}
		}

		private String initialValue(int param) {
			switch (param) {
			case 1:
				return initialValue(_uncertainty.getParameter1Value(),
						_uncertainty.getParameter1Formula());
			case 2:
				return initialValue(_uncertainty.getParameter2Value(),
						_uncertainty.getParameter2Formula());
			case 3:
				return initialValue(_uncertainty.getParameter3Value(),
						_uncertainty.getParameter3Formula());
			default:
				return "";
			}

		}

		private String initialValue(Double value, String formula) {
			if (formula != null && !formula.isEmpty())
				return formula;
			if (value == null)
				return "";
			else
				return Double.toString(value);
		}

		private String[] getLabels() {
			switch (_uncertainty.getDistributionType()) {
			case LOG_NORMAL:
				return new String[] { Messages.GeometricMean,
						Messages.GeometricStandardDeviation };
			case NONE:
				return new String[0];
			case NORMAL:
				return new String[] { Messages.Mean, Messages.StandardDeviation };
			case TRIANGLE:
				return new String[] { Messages.Minimum, Messages.Mode,
						Messages.Maximum };
			case UNIFORM:
				return new String[] { Messages.Minimum, Messages.Maximum };
			default:
				return new String[0];
			}
		}

		private Uncertainty createUncertainty(UncertaintyType type) {
			switch (type) {
			case LOG_NORMAL:
				return Uncertainty.logNormal(defaultMean, 1);
			case NONE:
				return Uncertainty.none(defaultMean);
			case NORMAL:
				return Uncertainty.normal(defaultMean, 1);
			case TRIANGLE:
				return Uncertainty.triangle(defaultMean, defaultMean,
						defaultMean);
			case UNIFORM:
				return Uncertainty.uniform(defaultMean, defaultMean);
			default:
				return null;
			}
		}

		private boolean hasParameter(int parameter) {
			switch (_uncertainty.getDistributionType()) {
			case LOG_NORMAL:
				return parameter == 1 || parameter == 2;
			case NONE:
				return false;
			case NORMAL:
				return parameter == 1 || parameter == 2;
			case TRIANGLE:
				return parameter == 1 || parameter == 2 || parameter == 3;
			case UNIFORM:
				return parameter == 1 || parameter == 2;
			default:
				return false;
			}
		}

		Uncertainty fetchUncertainty() {
			if (texts == null)
				return _uncertainty;
			for (int i = 0; i < texts.length; i++) {
				String s = texts[i].getText();
				int param = i + 1;
				try {
					if (isValidNumber(s))
						set(param, Double.parseDouble(s), null);
					else if (isValidFormula(s))
						set(param, interpreterScope.eval(s), s);
				} catch (Exception e) {
					log.error("failed to set uncertainty value", e);
				}
			}
			return _uncertainty;
		}

		private void set(int param, double val, String s) {
			switch (param) {
			case 1:
				_uncertainty.setParameter1Formula(s);
				_uncertainty.setParameter1Value(val);
				break;
			case 2:
				_uncertainty.setParameter2Formula(s);
				_uncertainty.setParameter2Value(val);
				break;
			case 3:
				_uncertainty.setParameter3Formula(s);
				_uncertainty.setParameter3Value(val);
				break;
			default:
				break;
			}
		}

		private boolean isValidNumber(String s) {
			try {
				Double.parseDouble(s);
				return true;
			} catch (Exception e) {
				if (interpreterScope == null)
					Error.showBox(s + " is not a valid number");
				return false;
			}
		}

		private boolean isValidFormula(String s) {
			if (interpreterScope == null)
				return false;
			try {
				interpreterScope.eval(s);
				return true;
			} catch (Exception e) {
				Error.showBox("Formula evaluation of " + s + " failed");
				return false;
			}
		}

	}

}
