package org.openlca.app.navigation.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.handlers.IHandlerService;
import org.openlca.app.M;
import org.openlca.app.db.Database;
import org.openlca.app.navigation.CategoryElement;
import org.openlca.app.navigation.DatabaseElement;
import org.openlca.app.navigation.INavigationElement;
import org.openlca.app.navigation.ModelElement;
import org.openlca.app.navigation.ModelTypeElement;
import org.openlca.app.rcp.images.Icon;
import org.openlca.app.util.MsgBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportAction extends Action implements INavigationAction {

	private Logger log = LoggerFactory.getLogger(getClass());

	public ExportAction() {
		setText(M.Export);
		setImageDescriptor(Icon.EXPORT.descriptor());
	}

	@Override
	public boolean accept(INavigationElement<?> element) {
		if (element instanceof DatabaseElement) {
			DatabaseElement dbElement = (DatabaseElement) element;
			return Database.isActive(dbElement.getContent());
		} else {
			return (element instanceof ModelTypeElement)
					|| (element instanceof CategoryElement)
					|| (element instanceof ModelElement);
		}
	}

	@Override
	public boolean accept(List<INavigationElement<?>> elements) {
		for (INavigationElement<?> e : elements) {
			if (accept(e))
				return true;
		}
		return false;
	}

	@Override
	public void run() {
		if (Database.get() == null) {
			MsgBox.info("No opened database",
					"You need to open the database for the export first.");
			return;
		}
		try {
			IHandlerService service = PlatformUI
					.getWorkbench().getService(IHandlerService.class);
			service.executeCommand(ActionFactory.EXPORT.getCommandId(), null);
		} catch (Exception e) {
			log.error("Failed to open export wizard", e);
		}
	}

}
