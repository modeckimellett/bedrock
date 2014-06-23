package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings
import com.citytechinc.aem.bedrock.core.components.TestComponent
import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import org.apache.sling.api.scripting.SlingBindings
import spock.lang.Unroll

import javax.servlet.jsp.PageContext

import static com.citytechinc.aem.bedrock.core.tags.DefineObjectsTag.ATTR_COMPONENT_BINDINGS
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_BINDINGS_NAME

@Unroll
class ComponentTagSpec extends BedrockJspTagSpec<ComponentTag> {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content" {
                    component()
                }
            }
        }
    }

    @Override
    ComponentTag createTag() {
        new ComponentTag()
    }

    @Override
    Map<String, Object> addPageContextAttributes() {
        [(DEFAULT_BINDINGS_NAME): new SlingBindings()]
    }

    def "get component instance"() {
        setup:
        tag.className = TestComponent.class.name
        tag.name = "testComponent"

        def bindings = new BindingsBuilder(resourceResolver).build {
            path = "/content/home/jcr:content/component"
        }

        tag.pageContext.setAttribute ATTR_COMPONENT_BINDINGS, new ComponentBindings(bindings)

        when:
        tag.doEndTag()

        then:
        tag.pageContext.getAttribute("testComponent") instanceof TestComponent
    }

    def "get component instance with scope"() {
        setup:
        tag.className = TestComponent.class.name
        tag.name = "testComponent"
        tag.scope = scope

        def bindings = new BindingsBuilder(resourceResolver).build {
            path = "/content/home/jcr:content/component"
        }

        tag.pageContext.setAttribute ATTR_COMPONENT_BINDINGS, new ComponentBindings(bindings)

        when:
        tag.doEndTag()

        then:
        tag.pageContext.getAttribute("testComponent", scopeValue) instanceof TestComponent

        where:
        scope         | scopeValue
        "page"        | PageContext.PAGE_SCOPE
        "request"     | PageContext.REQUEST_SCOPE
        "session"     | PageContext.SESSION_SCOPE
        "application" | PageContext.APPLICATION_SCOPE
    }
}
