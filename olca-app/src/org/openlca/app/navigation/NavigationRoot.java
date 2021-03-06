package org.openlca.app.navigation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.PlatformObject;
import org.openlca.app.db.Database;
import org.openlca.app.db.DatabaseList;
import org.openlca.app.db.DerbyConfiguration;
import org.openlca.app.db.MySQLConfiguration;
import org.openlca.app.rcp.Workspace;
import org.openlca.util.Dirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Root element of the navigation tree: shows the database configurations.
 */
public class NavigationRoot extends PlatformObject implements
		INavigationElement<NavigationRoot> {

	private List<INavigationElement<?>> childs;

	@Override
	public NavigationRoot getContent() {
		return this;
	}

	@Override
	public void update() {
		childs = null;
	}

	@Override
	public List<INavigationElement<?>> getChildren() {
		if (childs == null)
			childs = loadChilds();
		return childs;
	}

	@Override
	public INavigationElement<?> getParent() {
		return null;
	}

	private List<INavigationElement<?>> loadChilds() {
		var childs = new ArrayList<INavigationElement<?>>();

		// add database elements
		var dbs = Database.getConfigurations();
		for (var config : dbs.getLocalDatabases()) {
			childs.add(new DatabaseElement(this, config));
		} for (var config : dbs.getRemoteDatabases()) {
			childs.add(new DatabaseElement(this, config));
		}

		// add a script folder if scripts are stored
		// in the workspace
		var scriptRoot = new File(Workspace.getDir(), "scripts");
		if (scriptRoot.exists() && !Dirs.isEmpty(scriptRoot)) {
			childs.add(new ScriptElement(this, scriptRoot));
		}
		return childs;
	}

}
