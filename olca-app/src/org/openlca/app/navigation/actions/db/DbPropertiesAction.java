package org.openlca.app.navigation.actions.db;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.openlca.app.M;
import org.openlca.app.db.Database;
import org.openlca.app.db.DatabasePropertiesDialog;
import org.openlca.app.db.IDatabaseConfiguration;
import org.openlca.app.navigation.DatabaseElement;
import org.openlca.app.navigation.INavigationElement;
import org.openlca.app.navigation.actions.INavigationAction;
import org.openlca.app.rcp.images.Icon;

/**
 * Shows the database properties in a window.
 */
public class DbPropertiesAction extends Action implements
		INavigationAction {

	private IDatabaseConfiguration config;

	public DbPropertiesAction() {
		setText(M.Properties);
		setImageDescriptor(Icon.INFO.descriptor());
	}

	@Override
	public boolean accept(INavigationElement<?> element) {
		if (!(element instanceof DatabaseElement))
			return false;
		DatabaseElement dbElement = (DatabaseElement) element;
		config = dbElement.getContent();
		return true;
	}

	@Override
	public boolean accept(List<INavigationElement<?>> elements) {
		return false;
	}

	@Override
	public void run() {
		if (config == null) {
			config = Database.getActiveConfiguration();
			if (config == null)
				return;
		}
		new DatabasePropertiesDialog(config).open();
	}
}
