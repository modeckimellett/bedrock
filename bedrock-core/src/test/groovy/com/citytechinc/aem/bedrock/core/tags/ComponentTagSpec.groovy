package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings
import com.citytechinc.aem.bedrock.core.components.TestComponent
import com.citytechinc.aem.bedrock.core.components.TestModelComponent
import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import com.citytechinc.aem.prosper.builders.BindingsBuilder
import org.apache.sling.api.resource.Resource
import spock.lang.Unroll

import javax.servlet.jsp.PageContext

import static com.citytechinc.aem.bedrock.core.tags.DefineObjectsTag.ATTR_COMPONENT_BINDINGS

@Unroll
class ComponentTagSpec extends BedrockJspTagSpec<ComponentTag> {

    @Override
    Map<Class, Closure> addResourceAdapters() {
        def adapters = [:]

        adapters[TestModelComponent] = { Resource resource -> new TestModelComponent(true) }

        return adapters
    }

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

    def "get component instance for modeled component"() {
        setup:
        tag.className = TestModelComponent.class.name
        tag.name = "testModelComponent"

        def bindings = new BindingsBuilder(resourceResolver).build {
            path = "/content/home/jcr:content/component"
        }

        tag.pageContext.setAttribute ATTR_COMPONENT_BINDINGS, new ComponentBindings(bindings)

        when:
        tag.doEndTag()

        then:
        tag.pageContext.getAttribute("testModelComponent") instanceof TestModelComponent
        ((TestModelComponent) tag.pageContext.getAttribute("testModelComponent")).adaptedTo
    }
}
