package com.citytechinc.aem.bedrock.core.servlets;

import com.citytechinc.aem.bedrock.api.request.ComponentRequest;
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest;
import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings;
import com.citytechinc.aem.bedrock.core.components.AbstractComponent;
import com.citytechinc.aem.bedrock.core.request.impl.DefaultComponentRequest;
import com.citytechinc.aem.bedrock.core.request.impl.DefaultComponentServletRequest;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Proxy servlet that wraps the Sling request in a "component" request for access to convenience accessor methods.
 */
public abstract class AbstractComponentServlet extends AbstractJsonResponseServlet {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentServlet.class);

    private static final long serialVersionUID = 1L;

    @Override
    protected final void doDelete(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        processDelete(new DefaultComponentServletRequest(request, response));
    }

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        processGet(new DefaultComponentServletRequest(request, response));
    }

    @Override
    protected final void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        processPost(new DefaultComponentServletRequest(request, response));
    }

    @Override
    protected final void doPut(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        processPut(new DefaultComponentServletRequest(request, response));
    }

    /**
     * Process a DELETE request.  Extending classes can optionally override this method to implement desired
     * functionality.
     *
     * @param request component request
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void processDelete(final ComponentServletRequest request) throws ServletException, IOException {
        handleMethodNotImplemented(request.getSlingRequest(), request.getSlingResponse());
    }

    /**
     * Process a GET request.  Extending classes can optionally override this method to implement desired
     * functionality.
     *
     * @param request component request
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void processGet(final ComponentServletRequest request) throws ServletException, IOException {
        handleMethodNotImplemented(request.getSlingRequest(), request.getSlingResponse());
    }

    /**
     * Process a POST request.  Extending classes can optionally override this method to implement desired
     * functionality.
     *
     * @param request component request
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void processPost(final ComponentServletRequest request) throws ServletException, IOException {
        handleMethodNotImplemented(request.getSlingRequest(), request.getSlingResponse());
    }

    /**
     * Process a PUT request.  Extending classes can optionally override this method to implement desired
     * functionality.
     *
     * @param request component request
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    protected void processPut(final ComponentServletRequest request) throws ServletException, IOException {
        handleMethodNotImplemented(request.getSlingRequest(), request.getSlingResponse());
    }

    /**
     * Get a component instance with the specified type for the current request.
     *
     * @param request current servlet request
     * @param type component class
     * @param <T> type of component class
     * @return component instance for the current request context or absent <code>Optional</code> if
     * @throws ServletException if error occurs when instantiating component
     */
    protected <T extends AbstractComponent> T getComponent(final ComponentServletRequest request, final Class<T> type)
        throws ServletException {
        final ComponentRequest componentRequest = new DefaultComponentRequest(request);
        final Bindings bindings = new ComponentBindings(componentRequest);

        final T instance;

        try {
            instance = type.newInstance();

            instance.init(bindings);
        } catch (InstantiationException e) {
            LOG.error("error instantiating component for type = " + type, e);

            throw new ServletException(e);
        } catch (IllegalAccessException e) {
            LOG.error("error instantiating component for type = " + type, e);

            throw new ServletException(e);
        }

        return instance;
    }
}
