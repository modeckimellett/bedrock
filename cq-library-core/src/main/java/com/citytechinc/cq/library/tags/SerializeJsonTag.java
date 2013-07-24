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

import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_COMPONENT_REQUEST;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Serializes a component as JSON.  The component should be annotated using Jackson annotations (e.g.
 * <code>JsonGetter</code>, <code>JsonProperty</code>) to indicate which fields or methods should be serialized.
 */
public final class SerializeJsonTag extends AbstractScopedTag {

    private static final Logger LOG = LoggerFactory.getLogger(SerializeJsonTag.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final long serialVersionUID = 1L;

    /**
     * Component class to instantiate.
     */
    private String className;

    /**
     * Name of existing component object in page context.  "className" attribute is checked first.
     */
    private String instanceName;

    /**
     * Optional name to set in pageContext for the component instance.  This only applies when 'className' is used.
     */
    private String name;

    @Override
    public int doEndTag() throws JspException {
        checkArgument(!isNullOrEmpty(className) || !isNullOrEmpty(instanceName),
            "className or instanceName is required");

        checkScopeAttribute();

        try {
            final Object component;

            if (isNullOrEmpty(className)) {
                LOG.debug("doEndTag() serializing JSON for instance name = {}", instanceName);

                component = pageContext.getAttribute(instanceName, getScopeValue());
            } else {
                LOG.debug("doEndTag() serializing JSON for class name = {}", className);

                final ComponentRequest request = (ComponentRequest) pageContext.getAttribute(ATTR_COMPONENT_REQUEST);

                component = Class.forName(className).getConstructor(ComponentRequest.class).newInstance(request);

                if (!isNullOrEmpty(name)) {
                    pageContext.setAttribute(name, component, getScopeValue());
                }
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
