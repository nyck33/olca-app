package org.openlca.app.util.viewers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Combo;
import org.openlca.app.util.tables.Tables;
import org.openlca.app.util.trees.Trees;
import org.openlca.app.viewers.BaseLabelProvider;
import org.openlca.app.viewers.BaseNameSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Viewers {

	private final static Logger log = LoggerFactory.getLogger(Viewers.class);

	/** Get the first selected element from the given viewer. */
	public static <T> T getFirstSelected(StructuredViewer viewer) {
		if (viewer == null)
			return null;
		ISelection selection = viewer.getSelection();
		return getFirst(selection);
	}

	/** Get the first element from the given selection. */
	public static <T> T getFirst(ISelection selection) {
		if (selection == null || selection.isEmpty()
				|| !(selection instanceof IStructuredSelection))
			return null;
		IStructuredSelection structSelection = (IStructuredSelection) selection;
		try {
			// caller has to assign the right class
			@SuppressWarnings("unchecked")
			T obj = (T) structSelection.getFirstElement();
			return obj;
		} catch (ClassCastException e) {
			log.error("Error casting obj of type "
					+ structSelection.getFirstElement().getClass()
							.getCanonicalName(), e);
			return null;
		}
	}

	/** Get all selected elements from the given viewer. */
	public static <T> List<T> getAllSelected(StructuredViewer viewer) {
		if (viewer == null)
			return Collections.emptyList();
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		return getAll(selection);
	}

	/** Get all elements from the given selection. */
	public static <T> List<T> getAll(IStructuredSelection selection) {
		if (selection == null || selection.isEmpty())
			return Collections.emptyList();
		List<T> list = new ArrayList<>();
		Iterator<?> it = selection.iterator();
		while (it.hasNext()) {
			Object o = it.next();
			try {
				// caller has to assign to right class
				@SuppressWarnings("unchecked")
				T obj = (T) o;
				list.add(obj);
			} catch (ClassCastException e) {
				log.error("Error casting obj of type "
						+ o.getClass().getCanonicalName(), e);
			}
		}
		return list;
	}

	public static ComboViewer createBaseViewer(Combo combo) {
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setSorter(new BaseNameSorter());
		viewer.setLabelProvider(new BaseLabelProvider());
		viewer.setUseHashlookup(true);
		return viewer;
	}

	public static <T> void sortByString(ColumnViewer viewer,
			Function<T, String> fn, int col) {
		StringSorter<T> s = new StringSorter<>(col, fn);
		addSorter(viewer, s);
	}

	public static <T> void sortByDouble(ColumnViewer viewer,
			Function<T, Double> fn, int col) {
		DoubleSorter<T> s = new DoubleSorter<>(col, fn);
		addSorter(viewer, s);
	}

	public static <T> void sortByDouble(ColumnViewer viewer,
			ITableLabelProvider labelProvider, int... cols) {
		for (int i = 0; i < cols.length; i++) {
			LabelSorter<T> s = new LabelSorter<>(cols[i], labelProvider);
			s.asNumbers = true;
			addSorter(viewer, s);
		}
	}

	public static <T> void sortByLabels(ColumnViewer viewer,
			ITableLabelProvider labelProvider, int... cols) {
		for (int i = 0; i < cols.length; i++) {
			LabelSorter<T> s = new LabelSorter<>(cols[i], labelProvider);
			addSorter(viewer, s);
		}
	}

	private static void addSorter(ColumnViewer viewer, Sorter<?> sorter) {
		if (viewer instanceof TableViewer)
			Tables.addSorter((TableViewer) viewer, sorter);
		else if (viewer instanceof TreeViewer)
			Trees.addSorter((TreeViewer) viewer, sorter);
	}
}