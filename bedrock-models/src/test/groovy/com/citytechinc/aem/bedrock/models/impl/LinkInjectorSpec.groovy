package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.citytechinc.aem.bedrock.models.annotations.LinkInject
import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.models.annotations.Model

class LinkInjectorSpec extends BedrockModelSpec {

    @Model(adaptables = Resource)
    static class Component {

        @LinkInject
        Link link1

        @LinkInject(titleProperty = "jcr:title")
        Link link2

        @LinkInject(inherit = true)
        Link link3
    }

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component",
                        link1: "/content/citytechinc",
                        link2: "/content/citytechinc",
                        link3: "/content/citytechinc")
                }
                child {
                    "jcr:content" {
                        component()
                    }
                }
            }
        }
    }

    def "link is null if component node is null"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/jcr:content/component/sub")
        def component = resource.adaptTo(Component)

        expect:
        !component.link1
    }

    def "link has correct path value"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link1.path == "/content/citytechinc"
    }

    def "link has correct path value and title"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link2.path == "/content/citytechinc"
        component.link2.title == "Testing Component"
    }

    def "inherited link has correct path value"() {
        setup:
        def resource = getResource("/content/citytechinc/child/jcr:content/component")
        def component = resource.adaptTo(Component)

        expect:
        component.link3.path == "/content/citytechinc"
    }
}
