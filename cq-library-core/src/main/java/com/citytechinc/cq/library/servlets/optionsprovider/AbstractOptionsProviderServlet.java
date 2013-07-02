/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.optionsprovider;

import com.citytechinc.cq.library.content.request.ComponentServletRequest;
import com.citytechinc.cq.library.servlets.AbstractComponentServlet;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to
 * be rendered in a selection box.  The implementing class determines how these options are retrieved from the
 * repository or third-party provider.
 */
public abstract class AbstractOptionsProviderServlet extends AbstractComponentServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Get a list of "options" (text/value pairs) for rendering in an authoring dialog.  Building the list of options is
     * handled by the implementing class and will vary depending on the requirements for the component dialog calling
     * this servlet.
     *
     * @param request component servlet request
     * @return list of options as determined by the implementing class
     */
    protected abstract List<Option> getOptions(final ComponentServletRequest request);

    /**
     * @param request component servlet request
     * @return Optional name of root JSON object containing options
     */
    protected abstract Optional<String> getOptionsRoot(final ComponentServletRequest request);

    protected final void processGet(final ComponentServletRequest request) throws ServletException, IOException {
        final List<Option> result = getOptions(request);

        final Optional<String> optionsRoot = getOptionsRoot(request);

        final SlingHttpServletResponse slingResponse = request.getSlingResponse();

        if (optionsRoot.isPresent()) {
            writeJsonResponse(slingResponse, ImmutableMap.of(optionsRoot.get(), result));
        } else {
            writeJsonResponse(slingResponse, result);
        }
    }
}
