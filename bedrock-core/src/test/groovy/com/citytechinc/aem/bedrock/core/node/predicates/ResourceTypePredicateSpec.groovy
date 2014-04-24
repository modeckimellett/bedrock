package com.citytechinc.aem.bedrock.core.node.predicates

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import spock.lang.Unroll

@Unroll
class ResourceTypePredicateSpec extends BedrockSpec {

    def setupSpec() {
        nodeBuilder.content {
            citytechinc("sling:resourceType": "page")
        }
    }

    def "resource with matching resource type is included"() {
        setup:
        def resource = resourceResolver.getResource("/content/citytechinc")
        def predicate = new ResourceTypePredicate(resourceType)

        expect:
        predicate.apply(resource) == result

        where:
        resourceType | result
        "page"       | true
        "node"       | false
    }


}
