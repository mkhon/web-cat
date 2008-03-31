package net.sf.webcat.reporter;

import java.io.IOException;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSDictionary;

/**
 * The Reporter subsystem class manages a set of objects that implement this
 * IReportRenderingMethod interface. Each of these objects implements a
 * rendering method that can be selected by the user at report generation time
 * to control the format in which a report is displayed.
 *
 * @author aallowat
 */
public interface IRenderingMethod
{
	/**
	 * The value of this key passed to renderReport() specifies the resource
	 * action URL used to request external content displayed on the generated
	 * report page.
	 */
	public static final String OPTION_ACTION_URL = "actionURL";

	/**
	 * Gets the internal name of the rendering method. This is the name stored
	 * in the database and transient page state to determine which rendering
	 * method to use for a particular report.
	 *
	 * @return a String containing the internal name of the rendering method
	 */
	String methodName();

	/**
	 * Gets a human-readable name of the rendering method. This is the text
	 * that is presented to the user in the UI when they are asked to choose
	 * a rendering method for a report.
	 *
	 * @return a String containing the human-readable name of the rendering
	 *     method
	 */
	String displayName();

	/**
	 * Sets up the necessary resources to render the specified report using
	 * this rendering method. No rendering is performed upon completion of this
	 * method; to actually render the report, call the render() method on the
	 * Controller object that is rendered by this method.
	 *
	 * @param report the report to render
	 * @param options additional options to pass to the renderer
	 * @return an object that implements the IRenderingMethod.Controller
	 *     interface, which is used to start and stop the rendering process
	 */
	Controller prepareToRender(GeneratedReport report, NSDictionary options);

	/**
	 * Appends the rendered report content to the specified response object.
	 * The nature of this content can differ based on the rendering method;
	 * for example, the HTML renderer appends the content directly to the
	 * response, while the CSV renderer appends hyperlinks to direct actions
	 * that can be used to download the generated CSV files.
	 *
	 * @param report the report whose content should be appended
	 * @param response the response to which the content should be appended
     * @param context the context of the response
	 * @throws IOException if there is an error generating the content
	 */
	void appendContentToResponse(GeneratedReport report, WOResponse response,
			WOContext context) throws IOException;

	/**
	 * An object that implements this interface is returned by the
	 * renderReport() method of each rendering method to control the rendering
	 * process, such as starting it or canceling it before it is complete.
	 */
	public interface Controller
	{
		/**
		 * Renders the report that this controller object was created in
		 * preparation for.
         * @throws Exception if something goes wrong
		 */
		void render() throws Exception;

		/**
		 * Cancels the rendering of the report that this controller object was
		 * created in preparation for.
		 */
		void cancel();
	}
}
