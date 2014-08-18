package com.citytechinc.aem.bedrock.core.tags

import com.google.common.base.Joiner
import com.google.common.collect.Lists
import groovy.util.logging.Slf4j

import javax.servlet.jsp.JspException

import static javax.servlet.jsp.PageContext.REQUEST_SCOPE

@Slf4j("LOG")
final class IncludeDeferredClientLibTag extends AbstractDeferredClientLibTag {

    private static final def JOINER = Joiner.on(',')

    @Override
    public int doEndTag() throws JspException {
        def categories = Lists.newArrayList(requestCategories)

        categories.addAll(this.categories)

        LOG.debug "client libraries for request = {}", categories

        pageContext.setAttribute(ATTR_CATEGORIES, JOINER.join(categories), REQUEST_SCOPE)

        EVAL_PAGE
    }
}
