/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node.predicates

import com.citytechinc.aem.bedrock.testing.specs.BedrockSpec

class ComponentNodeResourceTypePredicateSpec extends BedrockSpec {

    def setupSpec() {
        nodeBuilder.sabbath("sling:resourceType": "black") {
            paranoid()
        }
    }

    def "exception thrown when resource type is null"() {
        when:
        new ComponentNodeResourceTypePredicate(null)

        then:
        thrown(NullPointerException)
    }

    def "exception thrown when node is null"() {
        setup:
        def predicate = new ComponentNodeResourceTypePredicate("black")

        when:
        predicate.apply(null)

        then:
        thrown(NullPointerException)
    }

    def "node with matching resource type is included"() {
        setup:
        def node = getComponentNode("/sabbath")
        def predicate = new ComponentNodeResourceTypePredicate("black")

        expect:
        predicate.apply(node)
    }

    def "node with non-matching resource type is not included"() {
        setup:
        def node = getComponentNode("/sabbath")
        def predicate = new ComponentNodeResourceTypePredicate("purple")

        expect:
        !predicate.apply(node)
    }

    def "node with no resource type is not included"() {
        setup:
        def node = getComponentNode("/sabbath/paranoid")
        def predicate = new ComponentNodeResourceTypePredicate("purple")

        expect:
        !predicate.apply(node)
    }
}
