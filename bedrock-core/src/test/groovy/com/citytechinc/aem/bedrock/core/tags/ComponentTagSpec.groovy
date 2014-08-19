package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.components.TestComponent
import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import spock.lang.Unroll

import javax.servlet.jsp.PageContext

import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME

@Unroll
class ComponentTagSpec extends BedrockJspTagSpec {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content" {
                    component()
                }
            }
        }
    }

    def "get component instance"() {
        setup:
        def tag = new ComponentTag()

        tag.className = TestComponent.class.name
        tag.name = "testComponent"

        def bindings = new BindingsBuilder(resourceResolver).build {
            path = "/content/home/jcr:content/component"
        }

        def jspTag = init(tag, [(DEFAULT_BINDINGS_NAME): bindings])

        when:
        tag.doEndTag()

        then:
        jspTag.pageContext.getAttribute("testComponent") instanceof TestComponent
    }

    def "get component instance with scope"() {
        setup:
        def tag = new ComponentTag()

        tag.className = TestComponent.class.name
        tag.name = "testComponent"
        tag.scope = scope

        def bindings = new BindingsBuilder(resourceResolver).build {
            path = "/content/home/jcr:content/component"
        }

        def jspTag = init(tag, [(DEFAULT_BINDINGS_NAME): bindings])

        when:
        tag.doEndTag()

        then:
        jspTag.pageContext.getAttribute("testComponent", scopeValue) instanceof TestComponent

        where:
        scope         | scopeValue
        "page"        | PageContext.PAGE_SCOPE
        "request"     | PageContext.REQUEST_SCOPE
        "session"     | PageContext.SESSION_SCOPE
        "application" | PageContext.APPLICATION_SCOPE
    }
}
