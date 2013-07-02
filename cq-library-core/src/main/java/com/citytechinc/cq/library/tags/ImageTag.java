/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.foundation.Image;
import org.apache.sling.api.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Draw an HTML image tag for the current component.
 */
public final class ImageTag extends AbstractComponentTag {

    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

    private static final long serialVersionUID = 1L;

    private String name;

    @Override
    public int doEndTag() throws JspTagException {
        final Resource resource = getComponentNode().getResource();

        final Image image;

        if (isNullOrEmpty(name)) {
            image = new Image(resource);
        } else {
            image = new Image(resource, name);
        }

        if (image.hasContent()) {
            try {
                image.draw(pageContext.getOut());
            } catch (IOException ioe) {
                LOG.error("error writing image tag for name = " + name, ioe);

                throw new JspTagException(ioe);
            }
        }

        return EVAL_PAGE;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }
}
