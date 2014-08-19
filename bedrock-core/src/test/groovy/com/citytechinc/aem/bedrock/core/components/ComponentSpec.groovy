package com.citytechinc.aem.bedrock.core.components

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.day.cq.wcm.api.designer.Designer

class ComponentSpec extends BedrockSpec {

    class UselessComponent extends AbstractComponent {

    }

    class AnotherUselessComponent extends AbstractComponent {

    }

    @Override
    Map<Class, Closure> addResourceResolverAdapters() {
        [(Designer): {
            [getDesign: { null }] as Designer
        }]
    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }
    }

    def "exception thrown when component is not properly initialized"() {
        setup:
        def component = new UselessComponent()
        def componentNode = getComponentNode("/content/citytechinc/jcr:content")

        when:
        component.getService(String)

        then:
        thrown(NullPointerException)

        when:
        component.getServices(String, null)

        then:
        thrown(NullPointerException)

        when:
        component.get("", String)

        then:
        thrown(NullPointerException)

        when:
        component.getComponent("", UselessComponent)

        then:
        thrown(NullPointerException)

        when:
        component.getComponent(componentNode, UselessComponent)

        then:
        thrown(NullPointerException)

        when:
        component.componentRequest

        then:
        thrown(NullPointerException)

        when:
        component.currentPage

        then:
        thrown(NullPointerException)
    }

    def "get component from path"() {
        setup:
        def component = init(UselessComponent) {

        }

        expect:
        component.getComponent("/", AnotherUselessComponent)
    }

    def "get component from component node"() {
        setup:
        def component = init(UselessComponent) {

        }

        def componentNode = getComponentNode("/")

        expect:
        component.getComponent(componentNode, AnotherUselessComponent)
    }

    def "get title from component"() {
        setup:
        def component = init(TestComponent) {
            path = "/content/citytechinc/jcr:content/component"
        }

        expect:
        component.title == "Testing Component"
    }
}
