/*==========================================================================*\
 |  $Id: EntityResourceRequestHandler.java,v 1.5 2011/03/03 19:52:44 aallowat Exp $
 |*-------------------------------------------------------------------------*|
 |  Copyright (C) 2010 Virginia Tech
 |
 |  This file is part of Web-CAT.
 |
 |  Web-CAT is free software; you can redistribute it and/or modify
 |  it under the terms of the GNU Affero General Public License as published
 |  by the Free Software Foundation; either version 3 of the License, or
 |  (at your option) any later version.
 |
 |  Web-CAT is distributed in the hope that it will be useful,
 |  but WITHOUT ANY WARRANTY; without even the implied warranty of
 |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 |  GNU General Public License for more details.
 |
 |  You should have received a copy of the GNU Affero General Public License
 |  along with Web-CAT; if not, see <http://www.gnu.org/licenses/>.
\*==========================================================================*/

package org.webcat.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;
import org.webcat.core.Application;
import org.webcat.core.EntityResourceRequestHandler;
import org.webcat.core.EntityResourceHandler;
import org.webcat.core.Session;
import org.apache.log4j.Logger;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WORequestHandler;
import com.webobjects.appserver.WOResponse;
import com.webobjects.appserver.WOSession;
import com.webobjects.eoaccess.EOEntity;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.eocontrol.EOEnterpriseObject;
import com.webobjects.eocontrol.EOFetchSpecification;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSData;
import com.webobjects.foundation.NSMutableDictionary;
import er.extensions.eof.ERXEOAccessUtilities;
import er.extensions.eof.ERXQ;

//-------------------------------------------------------------------------
/**
 * <p>
 * A request handler that allows subsystems to make file-system resources that
 * they generate and associate with EOs directly visible on the web and allow
 * resources associated with the same EO to be relatively linked.
 * </p><p>
 * URLs should be of the form:
 * <pre>http://server/Web-CAT.wo/er/[session id]/[EO type]/[EO id]/[path/to/the/file]</pre>
 * where "er" is this request handler's key, "session id" is the session
 * identifier (which is optional), "EO type" is the name of the entity
 * whose resources are being requested, "EO id" is the numeric ID of the
 * entity, and "path/to/the/file" is the path to the resource, relative to
 * whatever file-system location the particular entity deems fit for its
 * related resources.
 * </p>
 *
 * @author  Tony Allevato
 * @version $Id: EntityResourceRequestHandler.java,v 1.5 2011/03/03 19:52:44 aallowat Exp $
 */
public class EntityResourceRequestHandler extends WORequestHandler
{
    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a relative URL to access an entity-related resource.
     *
     * @param context the request context
     * @param eo the EO whose resource should be accessed
     * @param relativePath the entity-relative path to the resource
     * @return the URL to the resource
     */
    public static String urlForEntityResource(WOContext context,
            EOEnterpriseObject eo, String relativePath)
    {
        String entityName = eo.entityName();
        Number id = (Number) eo.valueForKey("id");

        StringBuffer buffer = new StringBuffer();
        String sessionString = null;

        if (context.session().storesIDsInURLs())
        {
            sessionString = "?wosid=" + context.session().sessionID();
        }

        buffer.append(entityName);
        buffer.append("/");
        buffer.append(id);

        if (relativePath != null && relativePath.length() > 0)
        {
            buffer.append("/");
            buffer.append(relativePath);
        }

        return context.urlWithRequestHandlerKey(REQUEST_HANDLER_KEY,
                buffer.toString(), sessionString);
    }


    // ----------------------------------------------------------
    /**
     * Registers an entity resource handler with the request handler.
     *
     * @param entityClass the Java class of the EO
     * @param handler a resource handler
     */
    public static void registerHandler(Class<?> entityClass,
                                       EntityResourceHandler handler)
    {
        resourceHandlers.setObjectForKey(handler, entityClass);
    }


    // ----------------------------------------------------------
    /**
     * Finds the entity-related resource from the given request and creates a
     * response containing its data.
     *
     * @param request the request
     * @return the response
     */
    @Override
    public WOResponse handleRequest(WORequest request)
    {
        WOContext context =
            Application.application().createContextForRequest(request);
        WOResponse response =
            Application.application().createResponseInContext(context);

        try
        {
            _handleRequest(request, context, response);
        }
        catch (Exception e)
        {
            response.setContent("");
            response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
        }

        return response;
    }


    // ----------------------------------------------------------
    private void _handleRequest(WORequest request, WOContext context,
            WOResponse response)
    {
        String handlerPath = request.requestHandlerPath();

        // Get the user's session, if possible. We'll use it later for
        // resources that require logins (for user validation) in order to be
        // accessed.

        Session session = null;
        String sessionId = request.sessionID();

        if (sessionId != null)
        {
            try
            {
                session = (Session)
                    Application.application().restoreSessionWithID(
                        sessionId, context);
            }
            catch (Exception e)
            {
                // Do nothing; will be handled below.
            }
        }

        // Parse the request path into its entity, object ID, and resource
        // path.

        EntityRequest entityRequest = EntityRequest.fromRequestHandlerPath(
                handlerPath);

        if (entityRequest != null)
        {
            EOEditingContext ec = Application.newPeerEditingContext();

            try
            {
                ec.lock();

                EntityResourceHandler handler = handlerForEntityNamed(
                        entityRequest.entityName(), ec);

                if (handler != null)
                {
                    if (handler.requiresLogin() && session == null)
                    {
                        log.warn("No session found with id " + sessionId);
                        response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
                    }

                    EOFetchSpecification fspec = new EOFetchSpecification(
                            entityRequest.entityName(),
                            ERXQ.is("id", entityRequest.id()), null);

                    NSArray<? extends EOEnterpriseObject> objects =
                        ec.objectsWithFetchSpecification(fspec);

                    if (objects != null && objects.count() > 0)
                    {
                        EOEnterpriseObject object = objects.objectAtIndex(0);

                        boolean canAccess = !handler.requiresLogin()
                            || (session != null &&
                                    (session.user().hasAdminPrivileges()
                                            || handler.userCanAccess(object,
                                                    session.user())));

                        if (canAccess)
                        {
                            generateResponse(response, handler, object,
                                    entityRequest.resourcePath());
                        }
                        else
                        {
                            String userName = (session != null
                                    ? session.user().userName() : "<null>");

                            log.warn("User " + userName + " tried to access "
                                    + "entity resource without permission");
                            response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
                        }
                    }
                    else
                    {
                        log.warn("Attempted to access entity resource for "
                                + "an object that does not exist: "
                                + entityRequest.entityName()
                                + ":" + entityRequest.id());

                        response.setStatus(WOResponse.HTTP_STATUS_NOT_FOUND);
                    }
                }
                else
                {
                    log.warn("No entity request handler was found for "
                            + entityRequest.entityName());

                    response.setStatus(WOResponse.HTTP_STATUS_NOT_FOUND);
                }
            }
            finally
            {
                ec.unlock();
                Application.releasePeerEditingContext(ec);
            }
        }
        else
        {
            response.setStatus(WOResponse.HTTP_STATUS_NOT_FOUND);
        }

        if (session != null)
        {
            Application.application().saveSessionForContext(context);
        }
    }


    // ----------------------------------------------------------
    /**
     * Gets the {@link EntityResourceHandler} for the entity with the specified
     * name. This method returns null if the handler could not be found (either
     * because there is no entity with that name or because no handler was
     * registered for that entity).
     *
     * @param entityName the entity name
     * @param ec an editing context
     *
     * @return an entity resource handler, or null
     */
    private EntityResourceHandler handlerForEntityNamed(String entityName,
            EOEditingContext ec)
    {
        EOEntity ent = EOUtilities.entityNamed(ec, entityName);
        Class<?> entityClass = null;

        if (ent != null)
        {
            try
            {
                entityClass = Class.forName(ent.className());
            }
            catch (ClassNotFoundException e)
            {
                // Do nothing; error will be handled below.
            }
        }

        if (entityClass != null)
        {
            return resourceHandlers.objectForKey(entityClass);
        }
        else
        {
            return null;
        }
    }


    // ----------------------------------------------------------
    /**
     * A somewhat brainless check to make sure that the path provided does not
     * go higher up into the file-system than it should. We verify that it is
     * a relative path and that it does not contain any parent directory
     * references that would move it above its origin.
     */
    private static boolean validatePath(String path)
    {
        File file = new File(path);

        if (file.isAbsolute())
        {
            log.warn("Attempted to access absolute path (" + path + ") in "
                    + "entity resource handler");
            return false;
        }
        else
        {
            int level = 0;

            String[] components = file.getPath().split(File.separator);
            for (String component : components)
            {
                if (component.equals(".."))
                {
                    level--;
                }
                else if (!component.equals("."))
                {
                    level++;
                }

                if (level < 0)
                {
                    log.warn("Attempted to access bad relative path (" + path
                            + ") in entity resource handler");
                    return false;
                }
            }
        }

        return true;
    }


    // ----------------------------------------------------------
    private void generateResponse(WOResponse response,
                                  EntityResourceHandler handler,
                                  EOEnterpriseObject object,
                                  String path)
    {
        File absolutePath = handler.pathForResource(object, path);

        log.debug("Path to resource on file system: " + absolutePath);

        if (absolutePath.exists())
        {
            if (!absolutePath.isFile())
            {
                response.setStatus(WOResponse.HTTP_STATUS_FORBIDDEN);
            }
            else
            {
                FileInputStream stream = null;

                try
                {
                    stream = new FileInputStream(absolutePath);
                    response.setContent(new NSData(stream, 0));
                    response.setStatus(WOResponse.HTTP_STATUS_OK);
                }
                catch (IOException e)
                {
                    response.setStatus(WOResponse.HTTP_STATUS_NOT_FOUND);
                }
                finally
                {
                    if (stream != null)
                    {
                        try
                        {
                            stream.close();
                        }
                        catch (IOException e)
                        {
                            // Do nothing.
                        }
                    }
                }
            }
        }
        else
        {
            response.setStatus(WOResponse.HTTP_STATUS_NOT_FOUND);
        }
    }


    //~ Inner classes .........................................................

    // ----------------------------------------------------------
    /**
     * A small class that bundles up the information about an entity resource
     * request (the entity name, the object ID, and the resource path).
     */
    private static class EntityRequest
    {
        //~ Constructors ......................................................

        // ----------------------------------------------------------
        private EntityRequest()
        {
            // Do nothing.
        }


        //~ Methods ...........................................................

        // ----------------------------------------------------------
        /**
         * Parses the specified handler path and returns an
         * {@code EntityRequest} containing the request information. This
         * method returns null if the request path was malformed or invalid.
         *
         * @param handlerPath the full handler path
         *
         * @return an instance of {@code EntityRequest} with the desired
         *     information, or null
         */
        public static EntityRequest fromRequestHandlerPath(String handlerPath)
        {
            EntityRequest request = new EntityRequest();

            Scanner scanner = new Scanner(handlerPath);
            scanner.useDelimiter("/");

            if (scanner.hasNext())
            {
                request.entityName = scanner.next();
            }

            if (scanner.hasNext())
            {
                String idString = scanner.next();

                try
                {
                    request.id = Long.valueOf(idString);
                }
                catch (NumberFormatException e)
                {
                    request.id = 0;
                }
            }

            if (scanner.hasNext())
            {
                scanner.skip("/");
            }

            scanner.useDelimiter("\0");

            if (scanner.hasNext())
            {
                String path = scanner.next();

                if (!validatePath(path))
                {
                    path = null;
                }

                request.resourcePath = path;
            }

            if (request.entityName == null || request.id == 0
                    || request.resourcePath == null)
            {
                return null;
            }
            else
            {
                return request;
            }
        }


        // ----------------------------------------------------------
        /**
         * Gets the entity name of the request.
         *
         * @return the entity name of the request
         */
        public String entityName()
        {
            return entityName;
        }


        // ----------------------------------------------------------
        /**
         * Gets the object ID of the request.
         *
         * @return the object ID of the request
         */
        public long id()
        {
            return id;
        }


        // ----------------------------------------------------------
        /**
         * Gets the path to the resource in the requested object.
         *
         * @return the path to the resource
         */
        public String resourcePath()
        {
            return resourcePath;
        }


        //~ Static/instance variables .........................................

        private String entityName;
        private long id;
        private String resourcePath;
    }


    //~ Static/instance variables .............................................

    public static final String REQUEST_HANDLER_KEY = "er";

    private static final NSMutableDictionary<Class<?>, EntityResourceHandler>
        resourceHandlers = new NSMutableDictionary<Class<?>, EntityResourceHandler>();

    private static final Logger log = Logger.getLogger(
            EntityResourceRequestHandler.class);
}
