package org.openlca.app.rcp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.openlca.app.OlcaPlugin;
import org.openlca.app.Preferences;
import org.openlca.app.Workspace;
import org.openlca.app.logging.Console;
import org.openlca.app.logging.LoggerConfig;
import org.openlca.eigen.NativeLibrary;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class RcpActivator extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "olca-app";

	private org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(this
			.getClass());
	private static RcpActivator plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RcpActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns the file found at the specified path as an input stream
	 * 
	 * @param path
	 *            The path to the file to load an input stream for (relative to
	 *            the plugin)
	 * @return The file found at the specified path as an input stream
	 */
	public static InputStream getStream(final String path) {
		try {
			return FileLocator.openStream(plugin.getBundle(), new Path(path),
					false);
		} catch (final IOException e) {
			return null;
		}
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		File workspace = Workspace.init();
		log.trace("Workspace initialised at {}", workspace);
		LoggerConfig.setUp();
		log.trace("Start application. Workspace: {}.", Platform.getLocation());
		log.trace("Bundle {} started", PLUGIN_ID);
		log.trace("Try init olca-eigen");
		NativeLibrary.loadFromDir(workspace);
		log.trace("olca-eigen loaded: {}", NativeLibrary.isLoaded());
		Preferences.init();
		startOlcaPlugins();
	}

	private OlcaPlugin[] getOlcaPlugins() throws CoreException {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry
				.getConfigurationElementsFor("org.openlca.app.plugin");
		List<OlcaPlugin> plugins = new ArrayList<>();
		for (IConfigurationElement elem : elements) {
			plugins.add((OlcaPlugin) elem
					.createExecutableExtension("pluginClass"));
		}
		return plugins.toArray(new OlcaPlugin[plugins.size()]);
	}

	private void startOlcaPlugins() throws CoreException {
		for (OlcaPlugin plugin : getOlcaPlugins())
			plugin.start();
	}

	private void stopOlcaPlugins() throws CoreException {
		for (OlcaPlugin plugin : getOlcaPlugins())
			plugin.stop();
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		log.trace("Stop bundle {}", PLUGIN_ID);
		stopOlcaPlugins();
		Console.dispose();
		plugin = null;
		super.stop(context);
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}