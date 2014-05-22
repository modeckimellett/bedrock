package com.citytechinc.aem.bedrock.core.request.impl

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec

class DefaultComponentResourceRequestSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            home {
                "jcr:content" {
                    component()
                }
            }
        }
    }

    def "getters return non-null values"() {
        setup:
        def resource = getResource("/content/home/jcr:content/component")
        def request = new DefaultComponentResourceRequest(resource)

        expect:
        request.componentNode
        request.currentNode
        request.currentPage
        request.pageManager
        request.properties
        request.resource
        request.resourceResolver
        request.session
        request.pageProperties
    }
}
