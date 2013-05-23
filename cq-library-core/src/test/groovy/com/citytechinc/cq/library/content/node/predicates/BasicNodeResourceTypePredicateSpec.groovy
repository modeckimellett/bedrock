/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.predicates

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.node.impl.DefaultBasicNode

class BasicNodeResourceTypePredicateSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        nodeBuilder.sabbath("sling:resourceType": "black") {
            paranoid()
        }
    }

    def "exception thrown when resource type is null"() {
        when:
        new BasicNodeResourceTypePredicate(null)

        then:
        thrown(NullPointerException)
    }

    def "exception thrown when node is null"() {
        setup:
        def predicate = new BasicNodeResourceTypePredicate("black")

        when:
        predicate.apply(null)

        then:
        thrown(NullPointerException)
    }

    def "node with matching resource type is included"() {
        setup:
        def resource = resourceResolver.getResource("/sabbath")
        def node = new DefaultBasicNode(resource)
        def predicate = new BasicNodeResourceTypePredicate("black")

        expect:
        predicate.apply(node)
    }

    def "node with non-matching resource type is not included"() {
        setup:
        def resource = resourceResolver.getResource("/sabbath")
        def node = new DefaultBasicNode(resource)
        def predicate = new BasicNodeResourceTypePredicate("purple")

        expect:
        !predicate.apply(node)
    }

    def "node with no resource type is not included"() {
        setup:
        def resource = resourceResolver.getResource("/sabbath/paranoid")
        def node = new DefaultBasicNode(resource)
        def predicate = new BasicNodeResourceTypePredicate("purple")

        expect:
        !predicate.apply(node)
    }
}
