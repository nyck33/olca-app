package org.openlca.app;

/**
 * Contains the configuration values of the application. Some of these values
 * can be overwritten via command line arguments (see {@link CommandArgument}).
 */
public final class Config {

	private Config() {
	}

	/**
	 * The version that is shown in the application to the user. Note that this
	 * can be another version then the internal build version.
	 */
	public static final String VERSION = "1.4";

	/**
	 * The name of the application that is shown to the user.
	 */
	public static final String APPLICATION_NAME = "openLCA - demo";

	/**
	 * The name of the workspace folder. This folder is located in the user
	 * directory (Java property "user.home"). The name of this folder is
	 * "openLCA-data", but it can be renamed in development versions (so that
	 * the users can run multiple versions of openLCA in parallel).
	 */
	public static final String WORK_SPACE_FOLDER_NAME = "openLCA-data-demo-1.4";

	/**
	 * The name of default folder where the local databases are stored. This
	 * folder is located in the workspace directory.
	 */
	public static final String DATABASE_FOLDER_NAME = "databases";

}