package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import spock.lang.Unroll

import javax.servlet.jsp.JspTagException
import javax.servlet.jsp.PageContext

@Unroll
class AbstractScopedTagSpec extends BedrockJspTagSpec<ScopedTag> {

    static final def ATTRIBUTE_NAME = "scopedAttribute"

    static final def ATTRIBUTE_VALUE = "attributeValue"

    class ScopedTag extends AbstractScopedTag {

        @Override
        int doEndTag(int scope) throws JspTagException {
            pageContext.setAttribute ATTRIBUTE_NAME, ATTRIBUTE_VALUE, scope

            EVAL_PAGE
        }
    }

    @Override
    ScopedTag createTag() {
        new ScopedTag()
    }

    def "set attribute in scope"() {
        setup:
        tag.scope = scope

        when:
        tag.doEndTag()

        then:
        tag.pageContext.getAttribute(ATTRIBUTE_NAME, scopeValue) == ATTRIBUTE_VALUE

        where:
        scope         | scopeValue
        "page"        | PageContext.PAGE_SCOPE
        "request"     | PageContext.REQUEST_SCOPE
        "session"     | PageContext.SESSION_SCOPE
        "application" | PageContext.APPLICATION_SCOPE
    }

    def "invalid scope throws exception"() {
        setup:
        tag.scope = "invalid"

        when:
        tag.doEndTag()

        then:
        thrown(IllegalArgumentException)
    }
}
