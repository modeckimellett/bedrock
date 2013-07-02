/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.node.ComponentNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import static com.citytechinc.cq.library.constants.ComponentConstants.DEFAULT_IMAGE_NAME;
import static com.google.common.base.CharMatcher.DIGIT;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Render an image source path for the current component.
 */
public final class ImageSourceTag extends AbstractComponentTag {

    private static final Logger LOG = LoggerFactory.getLogger(ImageTag.class);

    private static final long serialVersionUID = 1L;

    private String defaultValue = "";

    private String name;

    private String width;

    @Override
    public int doEndTag() throws JspTagException {
        final ComponentNode componentNode = getComponentNode();

        checkArgument(DIGIT.matchesAllOf(width), "width attribute must be numeric");

        final String name = isNullOrEmpty(this.name) ? DEFAULT_IMAGE_NAME : this.name;
        final int width = Integer.valueOf(this.width);

        final String imageSource;

        if (isInherit()) {
            imageSource = componentNode.getImageSourceInherited(name, width).or(defaultValue);
        } else {
            imageSource = componentNode.getImageSource(name, width).or(defaultValue);
        }

        try {
            pageContext.getOut().write(imageSource);
        } catch (IOException e) {
            LOG.error("error writing image source = " + imageSource, e);

            throw new JspTagException(e);
        }

        return EVAL_PAGE;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(final String width) {
        this.width = width;
    }
}
