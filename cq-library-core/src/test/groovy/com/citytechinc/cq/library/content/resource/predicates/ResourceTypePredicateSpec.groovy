/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.resource.predicates

import com.citytechinc.cq.library.testing.specs.AbstractCqSpec
import spock.lang.Unroll

class ResourceTypePredicateSpec extends AbstractCqSpec {

    def setupSpec() {
        nodeBuilder.content {
            citytechinc("sling:resourceType": "page")
        }
    }

    @Unroll
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
