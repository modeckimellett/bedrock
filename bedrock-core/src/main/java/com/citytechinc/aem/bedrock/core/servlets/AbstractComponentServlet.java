package com.citytechinc.aem.bedrock.core.servlets;

import com.citytechinc.aem.bedrock.api.request.ComponentRequest;
import com.citytechinc.aem.bedrock.api.request.ComponentServletRequest;
import com.citytechinc.aem.bedrock.core.components.AbstractComponent;
import com.citytechinc.aem.bedrock.core.request.impl.DefaultComponentRequest;
import com.citytechinc.aem.bedrock.core.request.impl.DefaultComponentServletRequest;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.commons.WCMUtils;
import com.google.common.base.Optional;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.scripting.core.ScriptHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Bindings;
import javax.script.SimpleBindings;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.citytechinc.aem.bedrock.core.bindings.ComponentBindings.COMPONENT_NODE;
import static com.citytechinc.aem.bedrock.core.bindings.ComponentBindings.COMPONENT_REQUEST;

import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_COMPONENT_CONTEXT;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_COMPONENT;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_CURRENT_DESIGN;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_DESIGNER;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_CURRENT_STYLE;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_CURRENT_PAGE;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_EDIT_CONTEXT;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_PROPERTIES;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_PAGE_MANAGER;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_RESOURCE_PAGE;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_PAGE_PROPERTIES;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_RESOURCE_DESIGN;
import static com.day.cq.wcm.core.impl.WCMBindingsValuesProvider.NAME_XSSAPI;
import static org.apache.sling.api.scripting.SlingBindings.SLING;

/**
 * Proxy servlet that wraps the Sling request in a "component" request for access to convenience accessor methods.
 */
public abstract class AbstractComponentServlet extends AbstractJsonResponseServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractComponentServlet.class);

    private SlingScriptHelper slingScriptHelper;

    protected final void activate(org.osgi.service.component.ComponentContext context) {
        slingScriptHelper = new ScriptHelper(context.getBundleContext(), null);

        processActivate(context);
    }

    /**
     * To be implemented by extending Servlets in cases where Servlets need to provide some manner of
     * activation logic.  Extending servlets should add activation logic via this method and should <strong>never</strong>
     * annotate a separate method as the activator.
     *
     * @param context OSGI Component Context
     */
    protected void processActivate(org.osgi.service.component.ComponentContext context) {

    }

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
     * Produces a Bindings using logic similar to that found in com.day.cq.wcm.core.impl.WCMBindingsValuesProvider then
     * uses these bindings to initialize an instance of the specified component class.
     *
     * @param request component request
     * @param type the type of component to instantiate and initialize
     * @param <T> the type of component to instantiate and initialize
     * @return an optional of the initialized component, absent if initialization fails
     */
    protected <T extends AbstractComponent> Optional<T> getComponent(ComponentServletRequest request, final Class<T> type) {


        Bindings componentBindings = new SimpleBindings();

        componentBindings.put(COMPONENT_NODE, request.getComponentNode());

        ComponentContext componentContext = WCMUtils.getComponentContext(request.getSlingRequest());
        Component component = componentContext == null ? null : componentContext.getComponent();
        EditContext editContext = componentContext == null ? null : componentContext.getEditContext();

        Designer designer = request.getResourceResolver().adaptTo(Designer.class);
        Page currentPage = componentContext == null ? null : componentContext.getPage();
        Design currentDesign = designer == null ? null : designer.getDesign(currentPage);
        Style currentStyle = componentContext == null || currentDesign == null ? null : currentDesign.getStyle(componentContext.getCell());

        ValueMap properties = request.getResource() == null ? null : request.getResource().adaptTo(ValueMap.class);

        componentBindings.put(NAME_PROPERTIES, properties);
        componentBindings.put(NAME_COMPONENT_CONTEXT, componentContext);
        componentBindings.put(NAME_EDIT_CONTEXT, editContext);
        componentBindings.put(NAME_COMPONENT, component);
        componentBindings.put(NAME_DESIGNER, designer);
        componentBindings.put(NAME_CURRENT_DESIGN, currentDesign);
        componentBindings.put(NAME_CURRENT_STYLE, currentStyle);
        componentBindings.put(SLING, slingScriptHelper);

        ComponentRequest componentRequest = new DefaultComponentRequest(request, componentBindings);

        componentBindings.put(COMPONENT_REQUEST, componentRequest);

        T instance = null;
        try {
            instance = type.newInstance();

            instance.init(componentBindings);
        } catch (InstantiationException e) {
            LOG.error("error instantiating component for type = " + type, e);
        } catch (IllegalAccessException e) {
            LOG.error("error instantiating component for type = " + type, e);
        }

        return Optional.fromNullable(instance);

    }
}
