package com.citytechinc.aem.bedrock.core.tags

import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspTagException

import static com.citytechinc.aem.bedrock.core.constants.ComponentConstants.DEFAULT_IMAGE_NAME
import static com.google.common.base.CharMatcher.DIGIT
import static com.google.common.base.Preconditions.checkArgument

/**
 * Render an image source path for the current component.
 */
@Slf4j("LOG")
final class ImageSourceTag extends AbstractComponentTag {

    String defaultValue = ""

    String name

    String width

    @Override
    int doEndTag() {
        checkArgument(DIGIT.matchesAllOf(width), "invalid width attribute = %s, must be numeric", width)

        def width = this.width as Integer

        def imageSource

        if (inherit) {
			if(this.name){
				imageSource = componentNode.getImageSourceInherited(name, width).or(defaultValue)
			}else{
			 	imageSource = componentNode.getImageSourceInherited(width).or(defaultValue)
			}
        } else {
			if(this.name){
				imageSource = componentNode.getImageSource(name, width).or(defaultValue)
			}else{
			imageSource = componentNode.getImageSource(width).or(defaultValue)
			}
        }

        try {
            pageContext.out.write(imageSource)
        } catch (IOException e) {
            LOG.error "error writing image source = $imageSource", e

            throw new JspTagException(e)
        }

        EVAL_PAGE
    }
}
