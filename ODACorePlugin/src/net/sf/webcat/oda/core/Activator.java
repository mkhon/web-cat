package net.sf.webcat.oda.core;

import net.sf.webcat.oda.IWebCATResultSetProvider;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Tony Allevato (Virginia Tech Computer Science)
 */
public class Activator extends Plugin
{
	// === Methods ============================================================

	// ------------------------------------------------------------------------
	/**
	 * Called when the plug-in is activated.
	 */
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		emptyAppContextProvider = null;
	}


	// ------------------------------------------------------------------------
	/**
	 * Called when the plug-in is deactivated.
	 */
	public void stop(BundleContext context) throws Exception
	{
		plugin = null;
		super.stop(context);
	}


	// ------------------------------------------------------------------------
	/**
	 * Returns the shared instance of the plug-in activator class.
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}


	// ------------------------------------------------------------------------
	/**
	 * Returns the result set provider that should be used when the app context
	 * is empty.
	 * 
	 * @return the IWebCATResultSetProvider to use with empty app contexts
	 */
	public IWebCATResultSetProvider getResultSetProviderForEmptyAppContext()
			throws CoreException
	{
		if(emptyAppContextProvider == null)
		{
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(PLUGIN_ID + ".emptyAppContextHandlers");
			IConfigurationElement[] elements = extensionPoint
					.getConfigurationElements();

			for(int i = 0; i < elements.length; i++)
			{
				IConfigurationElement element = elements[i];
				Object executable = element.createExecutableExtension("class");

				if(executable instanceof IWebCATResultSetProvider)
				{
					emptyAppContextProvider = (IWebCATResultSetProvider)executable;
					break;
				}
			}
		}

		return emptyAppContextProvider;
	}


	// ------------------------------------------------------------------------
	/**
	 * 
	 */
	public void refreshResultSetProviderForEmptyAppContext()
	{
		emptyAppContextProvider = null;
	}


	// === Static Variables ===================================================

	/**
	 * The unique identifier of the plug-in.
	 */
	public static final String PLUGIN_ID = "net.sf.webcat.oda";

	/**
	 * The singleton instance of the plug-in activator class.
	 */
	private static Activator plugin;

	// === Instance Variables =================================================

	/**
	 * The result set provider that should be used in cases where the app
	 * context is empty (that is, when previewing a report).
	 */
	private IWebCATResultSetProvider emptyAppContextProvider;
}
