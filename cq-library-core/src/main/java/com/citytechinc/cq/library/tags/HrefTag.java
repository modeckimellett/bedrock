/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public final class HrefTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(HrefTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        final Optional<String> optionalHref;

        if (isInherit()) {
            optionalHref = componentNode.getAsHrefInherited(propertyName);
        } else {
            optionalHref = componentNode.getAsHref(propertyName);
        }

        if (optionalHref.isPresent()) {
            final String href = optionalHref.get();

            try {
                pageContext.getOut().write(href);
            } catch (IOException ioe) {
                LOG.error("error writing href = " + href, ioe);

                throw new JspTagException(ioe);
            }
        }

        return EVAL_PAGE;
    }
}
