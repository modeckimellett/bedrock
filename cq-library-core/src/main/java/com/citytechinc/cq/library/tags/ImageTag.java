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

public final class ImageTag extends AbstractPropertyTag {

    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

    private static final long serialVersionUID = 1L;

    @Override
    public int doEndTag() throws JspTagException {
        final Resource resource = getComponentNode().getResource();

        final Image image;

        if (hasPropertyName()) {
            image = new Image(resource, propertyName);
        } else {
            image = new Image(resource);
        }

        if (image.hasContent()) {
            try {
                image.draw(pageContext.getOut());
            } catch (IOException ioe) {
                LOG.error("error writing tag for image name = " + propertyName, ioe);

                throw new JspTagException(ioe);
            }
        }

        return EVAL_PAGE;
    }
}
