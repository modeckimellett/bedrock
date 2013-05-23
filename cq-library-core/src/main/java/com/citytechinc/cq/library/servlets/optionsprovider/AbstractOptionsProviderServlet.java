/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.servlets.optionsprovider;

import com.citytechinc.cq.library.servlets.AbstractJsonResponseServlet;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Base class for providing a list of "options" to a component dialog widget.  An option is simply a text/value pair to
 * be rendered in a selection box.  The implementing class determines how these options are retrieved from the
 * repository or third-party provider.
 */
public abstract class AbstractOptionsProviderServlet extends AbstractJsonResponseServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected final void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
        throws ServletException, IOException {
        final List<Option> result = getOptions(request);

        final Optional<String> optionsRoot = getOptionsRoot(request);

        if (optionsRoot.isPresent()) {
            writeJsonResponse(response, ImmutableMap.of(optionsRoot.get(), result));
        } else {
            writeJsonResponse(response, result);
        }
    }

    /**
     * Get a list of "options" (text/value pairs) for rendering in an authoring dialog.  Building the list of options is
     * handled by the implementing class and will vary depending on the requirements for the component dialog calling
     * this servlet.
     *
     * @param request servlet request
     * @return list of options as determined by the implementing class
     */
    protected abstract List<Option> getOptions(final SlingHttpServletRequest request);

    /**
     * @param request servlet request
     * @return Optional name of root JSON object containing options
     */
    protected abstract Optional<String> getOptionsRoot(final SlingHttpServletRequest request);
}
