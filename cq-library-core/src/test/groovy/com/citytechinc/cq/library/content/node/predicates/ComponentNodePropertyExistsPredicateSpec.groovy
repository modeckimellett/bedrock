/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.predicates

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.node.impl.DefaultBasicNode
import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode
import org.apache.sling.api.resource.NonExistingResource

class ComponentNodePropertyExistsPredicateSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        nodeBuilder.content {
            citytechinc("jcr:title": "CITYTECH, Inc.")
        }
    }

    def "node where property exists is included"() {
        setup:
        def resource = resourceResolver.getResource("/content/citytechinc")
        def node = new DefaultComponentNode(resource)
        def predicate = new ComponentNodePropertyExistsPredicate("jcr:title")

        expect:
        predicate.apply(node)
    }

    def "node where property does not exist is not included"() {
        setup:
        def resource = resourceResolver.getResource("/content/citytechinc")
        def node = new DefaultBasicNode(resource)
        def predicate = new BasicNodeResourceTypePredicate("jcr:description")

        expect:
        !predicate.apply(node)
    }

    def "node for non-existing resource is not included"() {
        setup:
        def resource = new NonExistingResource(resourceResolver, "/content/non-existing")
        def node = new DefaultComponentNode(resource)
        def predicate = new ComponentNodePropertyExistsPredicate("propertyName")

        expect:
        !predicate.apply(node)
    }
}
