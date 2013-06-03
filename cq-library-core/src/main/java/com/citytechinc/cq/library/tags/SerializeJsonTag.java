/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.request.ComponentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public final class SerializeJsonTag extends TagSupport {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(SerializeJsonTag.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Component class to instantiate.
     */
    private String className;

    /**
     * Name of existing component object in page context.  "className" is checked first.
     */
    private String instanceName;

    @Override
    public int doEndTag() throws JspException {
        checkArgument(!isNullOrEmpty(className) || !isNullOrEmpty(instanceName),
            "className or instanceName is required");

        final ComponentRequest request = (ComponentRequest) pageContext.getAttribute(
            DefineObjectsTag.ATTR_COMPONENT_REQUEST);

        try {
            final Object component;

            if (isNullOrEmpty(className)) {
                component = pageContext.getAttribute(instanceName);
            } else {
                component = Class.forName(className).getConstructor(ComponentRequest.class).newInstance(request);
            }

            pageContext.getOut().write(MAPPER.writeValueAsString(component));
        } catch (Exception e) {
            LOG.error("error serializing component JSON", e);

            throw new JspTagException(e);
        }

        return EVAL_PAGE;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(final String instanceName) {
        this.instanceName = instanceName;
    }
}
