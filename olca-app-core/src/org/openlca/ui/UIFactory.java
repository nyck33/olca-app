/*******************************************************************************
 * Copyright (c) 2007 - 2010 GreenDeltaTC. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Mozilla
 * Public License v1.1 which accompanies this distribution, and is available at
 * http://www.openlca.org/uploads/media/MPL-1.1.html
 * 
 * Contributors: GreenDeltaTC - initial API and implementation
 * www.greendeltatc.com tel.: +49 30 4849 6030 mail: gdtc@greendeltatc.com
 ******************************************************************************/

package org.openlca.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.openlca.core.application.Messages;
import org.openlca.core.application.navigation.INavigationElement;
import org.openlca.core.application.navigation.NavigationRoot;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.modelprovider.IModelComponent;
import org.openlca.ui.dnd.IDropHandler;
import org.openlca.ui.dnd.TextDropComponent;
import org.openlca.ui.dnd.ViewerDropComponent;
import org.openlca.ui.viewer.ModelComponentTreeViewer;

/**
 * A factory for basic UI components in the openLCA framework.
 */
public final class UIFactory {

	private UIFactory() {
	}

	/**
	 * Creates an empty label and a check button with the given text (use in 2
	 * column grid layout)
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The form toolkit
	 * @param text
	 *            The text of the check button
	 * @return A new check button
	 */
	public static Button createButton(final Composite parent,
			final FormToolkit toolkit, final String text) {
		toolkit.createLabel(parent, "", SWT.NONE);

		final Button button = toolkit.createButton(parent, text, SWT.CHECK);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		button.setLayoutData(gd);
		return button;
	}

	/**
	 * Creates an empty label and a check button with the given text (use in 2
	 * column grid layout)
	 * 
	 * @param parent
	 *            The parent composite
	 * @param text
	 *            The text of the check button
	 * @return A new check button
	 */
	public static Button createButton(final Composite parent, final String text) {
		new Label(parent, SWT.NONE);
		final Button button = new Button(parent, SWT.CHECK);
		final GridData gd_embeddedCheck = new GridData();
		button.setLayoutData(gd_embeddedCheck);
		button.setText(text);
		return button;
	}

	/**
	 * Creates the category section for model component editors
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The form toolkit
	 * @return The category section
	 */
	public static Section createCategorySection(final Composite parent,
			final FormToolkit toolkit) {
		// category label + section composite
		toolkit.createLabel(parent, Messages.UIFactory_CategoryLabel, SWT.NONE);
		final Composite categoryComposite = toolkit.createComposite(parent,
				SWT.NONE);
		categoryComposite.setLayout(new GridLayout());
		final GridData gd_categoryComposite = new GridData(SWT.FILL, SWT.FILL,
				true, false);
		categoryComposite.setLayoutData(gd_categoryComposite);
		toolkit.paintBordersFor(categoryComposite);

		// create category section
		final Section categorySection = toolkit.createSection(
				categoryComposite, ExpandableComposite.TWISTIE);
		categorySection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		return categorySection;
	}

	/**
	 * Creates a tree viewer for displaying openLCA categories
	 * 
	 * @param section
	 *            The section in which the tree viewer should be added
	 * @param toolkit
	 *            The form toolkit
	 * @param input
	 *            The input of the viewer
	 * @param clazz
	 *            The model component class to display
	 * @return The category tree viewer
	 */
	public static TreeViewer createCategoryTreeViewer(final Section section,
			final FormToolkit toolkit, final INavigationElement input,
			final Class<? extends IModelComponent> clazz) {

		// create the section client
		final Composite categoryTreeComp = toolkit.createComposite(section);
		categoryTreeComp.setLayout(new GridLayout());
		categoryTreeComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		section.setClient(categoryTreeComp);

		// create category viewer
		final TreeViewer categoryViewer = new ModelComponentTreeViewer(
				categoryTreeComp, false, true, input, clazz);
		final GridData treeGridData = new GridData(SWT.FILL, SWT.FILL, true,
				false);
		treeGridData.minimumHeight = 100;
		treeGridData.heightHint = 200;
		categoryViewer.getTree().setLayoutData(treeGridData);
		return categoryViewer;
	}

	/**
	 * Creates a label with the given text and a combo viewer
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The toolkit
	 * @param labelText
	 *            The text of the label
	 * @param contentProvider
	 *            The content provider of the combo viewer
	 * @param labelProvider
	 *            The label provider of the combo viewer
	 * @param sorter
	 *            The sorter of the combo viewer
	 * @return A new combo viewer
	 */
	public static ComboViewer createComboViewerWithLabel(
			final Composite parent, final FormToolkit toolkit,
			final String labelText, final IContentProvider contentProvider,
			final IBaseLabelProvider labelProvider, final ViewerSorter sorter) {
		if (labelText != null) {
			toolkit.createLabel(parent, labelText);
		}

		final Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final ComboViewer comboViewer = new ComboViewer(combo);
		if (labelProvider != null) {
			comboViewer.setContentProvider(contentProvider);
		}
		if (contentProvider != null) {
			comboViewer.setLabelProvider(labelProvider);
		}
		if (sorter != null) {
			comboViewer.setSorter(sorter);
		}
		return comboViewer;
	}

	/**
	 * Creates a label with the given text and a combo viewer
	 * 
	 * @param parent
	 *            The parent composite
	 * @param labelText
	 *            The text of the label
	 * @return A new combo viewer
	 */
	public static ComboViewer createComboViewerWithLabel(
			final Composite parent, final String labelText) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);

		final Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final ComboViewer comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new BaseLabelProvider());
		comboViewer.setSorter(new BaseNameSorter());
		return comboViewer;
	}

	/**
	 * Creates a label and a combo widget
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The form toolkit
	 * @param text
	 *            The text of the label
	 * @param input
	 *            The input of the combo
	 * @param firstSelection
	 *            The start selection index of the combo
	 * @return A new combo
	 */
	public static Combo createComboWithLabel(final Composite parent,
			final FormToolkit toolkit, final String text, final String[] input,
			final Integer firstSelection) {
		toolkit.createLabel(parent, text, SWT.NONE);
		final Combo combo = new Combo(parent, SWT.READ_ONLY);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		if (input != null) {
			combo.setItems(input);
		}
		if (firstSelection != null) {
			combo.select(firstSelection);
		}
		combo.setLayoutData(gd);
		return combo;
	}

	/**
	 * Creates a label and a combo widget
	 * 
	 * @param parent
	 *            The parent composite
	 * @param text
	 *            The text of the label
	 * @param input
	 *            The input of the combo
	 * @param firstSelection
	 *            The start selection index of the combo
	 * @return A new combo
	 */
	public static Combo createComboWithLabel(final Composite parent,
			final String text, final String[] input,
			final Integer firstSelection) {
		new Label(parent, SWT.NONE).setText(text);
		final Combo combo = new Combo(parent, SWT.READ_ONLY);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		if (input != null) {
			combo.setItems(input);
		}
		if (firstSelection != null) {
			combo.select(firstSelection);
		}
		combo.setLayoutData(gd);
		return combo;
	}

	/**
	 * Creates a composite with a 2 column grid layout
	 * 
	 * @param parent
	 *            The parent composite
	 * @return A new composite
	 */
	public static Composite createContainer(final Composite parent) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(createGridLayout(2, false, 10));
		return container;
	}

	/**
	 * Creates a composite with the given layout
	 * 
	 * @param parent
	 *            The parent composite
	 * @param layout
	 *            The layout of the composite
	 * @return A new composite
	 */
	public static Composite createContainer(final Composite parent,
			final Layout layout) {
		final Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
		return container;
	}

	/**
	 * Creates a label and a drop component
	 * 
	 * @param parent
	 *            The parent composite
	 * @param labelText
	 *            The text of the label
	 * @param toolkit
	 *            The form toolkit
	 * @param modelComponent
	 *            The content of the drop component
	 * @param objectClass
	 *            The class that is allowed as content
	 * @param necessary
	 *            Indicates if there has to be set content in the drop component
	 *            or if it can be empty
	 * @param database
	 *            The database
	 * @param root
	 *            The navigation root
	 * @return A new drop component
	 */
	public static TextDropComponent createDropComponent(final Composite parent,
			final String labelText, final FormToolkit toolkit,
			final IModelComponent modelComponent,
			final Class<? extends IModelComponent> objectClass,
			final boolean necessary, final IDatabase database,
			final NavigationRoot root) {
		if (labelText != null && toolkit != null)
			toolkit.createLabel(parent, labelText, SWT.NONE);
		final TextDropComponent dropComponent = new TextDropComponent(parent,
				toolkit, objectClass, modelComponent, necessary, database, root);
		dropComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));
		return dropComponent;
	}

	/**
	 * Creates a new fill layout with spacing 10, margin width 10 and margin
	 * height 10
	 * 
	 * @return The fill layout
	 */
	public static Layout createFillLayout() {
		final FillLayout layout = new FillLayout();
		layout.spacing = 10;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		return layout;
	}

	/**
	 * Creates a new grid layout with vertical spacing 10, margin width 10 and
	 * margin height 10 and the given number of columns
	 * 
	 * @param numColumns
	 *            the number of columns of the layout
	 * @return The grid layout
	 */
	public static Layout createGridLayout(final int numColumns) {
		final GridLayout layout = new GridLayout(numColumns, false);
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		layout.marginHeight = 10;
		layout.horizontalSpacing = 10;
		return layout;
	}

	/**
	 * Creates a new grid layout
	 * 
	 * @param numColumns
	 *            the number of columns of the layout
	 * @param spacing
	 *            The vertical and horizontal spacing
	 * @param margin
	 *            The margin left/right and top/bottom
	 * @return The grid layout
	 */
	public static Layout createGridLayout(final int numColumns, int spacing,
			int margin) {
		final GridLayout layout = new GridLayout(numColumns, false);
		layout.verticalSpacing = spacing;
		layout.marginWidth = margin;
		layout.marginHeight = margin;
		layout.horizontalSpacing = spacing;
		return layout;
	}

	/**
	 * Creates a new grid layout with the given vertical spacing and the given
	 * number of columns
	 * 
	 * @param numColumns
	 *            the number of columns of the layout
	 * @param vSpacing
	 *            The vertical spacing value
	 * @param makeColumnsEqual
	 *            Indicates if the columns should have equal size
	 * @return The grid layout
	 */
	public static Layout createGridLayout(final int numColumns,
			final boolean makeColumnsEqual, final int vSpacing) {
		final GridLayout layout = new GridLayout(numColumns, makeColumnsEqual);
		layout.verticalSpacing = vSpacing;
		return layout;
	}

	/**
	 * Creates a new section
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The form toolkit
	 * @param sectionText
	 *            The text of the section
	 * @param fillHorizontal
	 *            Indicates if the section should grab existing space horizontal
	 * @param fillVertical
	 *            Indicates if the section should grab existing space vertical
	 * @return A new section
	 */
	public static Section createSection(final Composite parent,
			final FormToolkit toolkit, final String sectionText,
			final boolean fillHorizontal, final boolean fillVertical) {
		final Section section = toolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.FOCUS_TITLE
						| ExpandableComposite.EXPANDED);
		section.setLayoutData(new GridData(GridData.FILL, GridData.FILL,
				fillHorizontal, fillVertical));
		section.setLayout(new GridLayout());
		section.setText(sectionText);
		return section;
	}

	/**
	 * Creates a composite as a child of the given section
	 * 
	 * @param section
	 *            The parent of the composite
	 * @param toolkit
	 *            The form toolkit
	 * @param layout
	 *            The layout for the composite
	 * @return A new composite adapted by the toolkit
	 */
	public static Composite createSectionComposite(final Section section,
			final FormToolkit toolkit, final Layout layout) {
		final Composite composite = new Composite(section, SWT.NONE);
		composite.setLayout(layout);
		section.setClient(composite);
		toolkit.adapt(composite);
		return composite;

	}

	public static TableViewer createTableViewer(Composite parent,
			Class<? extends IModelComponent> allowedDropClass,
			IDropHandler handler, FormToolkit toolkit, String[] PROPERTIES,
			IDatabase database) {
		TableViewer tableViewer = null;
		if (allowedDropClass == null || handler == null) {
			tableViewer = new TableViewer(parent, SWT.BORDER
					| SWT.FULL_SELECTION | SWT.MULTI);
		} else {
			tableViewer = new ViewerDropComponent(parent, allowedDropClass,
					handler, database);
		}
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setSorter(new BaseNameSorter());

		// create the table
		final Table table = tableViewer.getTable();
		if (toolkit != null) {
			toolkit.adapt(table, true, true);
		}
		if (PROPERTIES == null || PROPERTIES.length < 2) {
			table.setLinesVisible(false);
			table.setHeaderVisible(false);
		} else {
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			for (final String p : PROPERTIES) {
				final TableColumn c = new TableColumn(table, SWT.NULL);
				c.setText(p);
			}
			for (final TableColumn c : table.getColumns()) {
				c.pack();
			}
		}

		if (PROPERTIES != null && PROPERTIES.length >= 2) {
			tableViewer.setColumnProperties(PROPERTIES);
		}
		if (toolkit != null) {
			toolkit.paintBordersFor(parent);
		}
		return tableViewer;
	}

	/**
	 * Creates a new table wrap layout with vertical spacing 10, each margin 10,
	 * 2 columns and h/v spacing of 10
	 * 
	 * @return A new table wrap layout
	 */
	public static Layout createTableWrapLayout() {
		final TableWrapLayout layout = new TableWrapLayout();
		layout.verticalSpacing = 10;
		layout.topMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = 2;
		layout.leftMargin = 10;
		layout.horizontalSpacing = 10;
		layout.bottomMargin = 10;
		return layout;
	}

	/**
	 * Creates a new table wrap layout with vertical spacing 10, each margin 10,
	 * and h/v spacing of 10
	 * 
	 * @param numColumns
	 *            the number of columns of the layout
	 * @return A new table wrap layout
	 */
	public static Layout createTableWrapLayout(final int numColumns) {
		final TableWrapLayout layout = new TableWrapLayout();
		layout.verticalSpacing = 10;
		layout.topMargin = 10;
		layout.rightMargin = 10;
		layout.numColumns = numColumns;
		layout.leftMargin = 10;
		layout.horizontalSpacing = 10;
		layout.bottomMargin = 10;
		return layout;
	}

	/**
	 * Creates a label and a text widget
	 * 
	 * @param parent
	 *            The parent composite
	 * @param toolkit
	 *            The form toolkit
	 * @param labelText
	 *            The text of the label
	 * @param multiLine
	 *            Indicates if the text widget is multi line
	 * @return A new text widget
	 */
	public static Text createTextWithLabel(final Composite parent,
			final FormToolkit toolkit, final String labelText,
			final boolean multiLine) {
		toolkit.createLabel(parent, labelText, SWT.NONE);

		final Text widget = toolkit.createText(parent, null,
				multiLine ? SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.MULTI
						: SWT.BORDER);
		final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		if (multiLine) {
			gd.minimumHeight = 100;
			gd.heightHint = 100;
			gd.widthHint = 100;
		}
		widget.setLayoutData(gd);
		return widget;
	}

	/**
	 * Creates a label and a text widget
	 * 
	 * @param parent
	 *            The parent composite
	 * @param labelText
	 *            The text of the label
	 * @param multiLine
	 *            Indicates if the text widget is multi line
	 * @return A new text widget
	 */
	public static Text createTextWithLabel(final Composite parent,
			final String labelText, final boolean multiLine) {
		return createTextWithLabel(parent, labelText, multiLine, SWT.NONE);
	}

	/**
	 * Creates a label and a text widget
	 * 
	 * @param parent
	 *            The parent composite
	 * @param labelText
	 *            The text of the label
	 * @param multiLine
	 *            Indicates if the text widget is multi line
	 * @param style
	 *            The style of the text widget
	 * @return A new text widget
	 */
	public static Text createTextWithLabel(final Composite parent,
			final String labelText, final boolean multiLine, final int style) {
		final Label nameLabel = new Label(parent, style);
		nameLabel.setText(labelText);
		return createText(parent, multiLine);
	}

	public static Text createText(Composite parent, boolean multiLine) {
		final Text text = new Text(parent, multiLine ? SWT.BORDER
				| SWT.V_SCROLL | SWT.WRAP | SWT.MULTI : SWT.BORDER);
		GridData gd_text = null;
		if (multiLine) {
			gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false);
			gd_text.heightHint = 75;
		} else {
			gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false);
		}
		text.setLayoutData(gd_text);
		return text;
	}

}
