package com.citytechinc.aem.bedrock.core.tags;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.google.common.escape.Escaper;
import com.google.common.xml.XmlEscapers;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

/**
 * Render a page or component property value.
 */
public final class PropertyTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(PropertyTag.class);

    private static final long serialVersionUID = 1L;

    private static final Escaper ESCAPER = XmlEscapers.xmlContentEscaper();

    /**
     * Default value if property does not exist.
     */
    private String defaultValue = "";

    /**
     * Should XML be escaped? Defaults to true.
     */
    private String escapeXml;

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        final String value;

        if (isInherit()) {
            value = componentNode.getInherited(propertyName, defaultValue);
        } else {
            value = componentNode.get(propertyName, defaultValue);
        }

        final boolean escapeXml = StringUtils.isEmpty(this.escapeXml) ? true : Boolean.valueOf(this.escapeXml);

        try {
            if (escapeXml) {
                pageContext.getOut().write(ESCAPER.escape(value));
            } else {
                pageContext.getOut().write(value);
            }
        } catch (IOException ioe) {
            LOG.error("error writing property value = " + value + " for name = " + propertyName, ioe);

            throw new JspTagException(ioe);
        }

        return EVAL_PAGE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getEscapeXml() {
        return escapeXml;
    }

    public void setEscapeXml(final String escapeXml) {
        this.escapeXml = escapeXml;
    }
}
