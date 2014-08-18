package com.citytechinc.aem.bedrock.core.tags

import com.day.cq.wcm.foundation.Image
import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

/**
 * Draw an HTML image tag for the current component.
 */
@Slf4j("LOG")
final class ImageTag extends AbstractComponentTag {

    String alt

    String name

    String title

    @Override
    int doEndTag() throws JspTagException {
        def resource = componentNode.resource

        def image

        if (name) {
            image = new Image(resource, name)
        } else {
            image = new Image(resource)
        }

        if (alt) {
            image.alt = alt
        }

        if (title) {
            image.title = title
        }

        image.href = ""

        if (image.hasContent()) {
            try {
                image.draw(pageContext.out)
            } catch (IOException ioe) {
                LOG.error "error writing image tag for name = $name", ioe

                throw new JspTagException(ioe)
            }
        }

        EVAL_PAGE
    }
}
