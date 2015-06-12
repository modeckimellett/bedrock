package com.citytechinc.aem.bedrock.core.models.impl

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.api.resource.ValueMap
import spock.lang.Shared
import spock.lang.Unroll

import javax.jcr.Node
import javax.jcr.Session

@Unroll
class ResourceContextInjectorSpec extends BedrockSpec {

    @Shared
    ResourceContextInjector injector

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content" {
                    component("jcr:title": "Testing Component")
                }
            }
        }

        injector = new ResourceContextInjector()
    }

    def "inject for valid type returns non-null value"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def value = injector.inject(resource, null, type, null, null)

        expect:
        value

        where:
        type << [Resource, ResourceResolver, ValueMap, Node, Session, BasicNode, ComponentNode, PageDecorator, PageManagerDecorator]
    }

    def "inject for invalid type returns null value"() {
        setup:
        def resource = getResource("/content/citytechinc/jcr:content/component")
        def value = injector.inject(resource, null, String, null, null)

        expect:
        !value
    }
}
