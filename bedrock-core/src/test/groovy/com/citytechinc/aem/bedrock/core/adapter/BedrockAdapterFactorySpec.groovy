package com.citytechinc.aem.bedrock.core.adapter

import com.citytechinc.aem.bedrock.api.node.BasicNode
import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.api.page.PageDecorator
import com.citytechinc.aem.bedrock.api.page.PageManagerDecorator
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class BedrockAdapterFactorySpec extends BedrockSpec {

    @Shared adapterFactory = new BedrockAdapterFactory()

    def "get resource adapter for node decorators returns non-null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        adapterFactory.getAdapter(resource, type)

        where:
        type << [ComponentNode, BasicNode]
    }

    def "resource adapt to page"() {
        setup:
        pageBuilder.content {
            home()
        }

        def resource = resourceResolver.getResource("/content/home")

        expect:
        adapterFactory.getAdapter(resource, PageDecorator)
    }

    def "resource adapt to page returns null for non-page node"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, PageDecorator)
    }

    def "get resource adapter for invalid type returns null"() {
        setup:
        def resource = resourceResolver.getResource("/")

        expect:
        !adapterFactory.getAdapter(resource, String)
    }

    def "get resource resolver adapter for valid type returns non-null"() {
        expect:
        adapterFactory.getAdapter(resourceResolver, PageManagerDecorator)
    }

    def "get resource resolver adapter for invalid type returns null"() {
        expect:
        !adapterFactory.getAdapter(resourceResolver, String)
    }

    def "get invalid adapter returns null"() {
        expect:
        !adapterFactory.getAdapter("", String)
    }
}
