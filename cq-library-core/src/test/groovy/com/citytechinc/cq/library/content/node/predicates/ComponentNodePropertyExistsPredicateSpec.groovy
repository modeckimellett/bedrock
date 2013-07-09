/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.predicates

import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode
import com.citytechinc.cq.library.testing.specs.AbstractCqSpec
import org.apache.sling.api.resource.NonExistingResource

class ComponentNodePropertyExistsPredicateSpec extends AbstractCqSpec {

    def setupSpec() {
        nodeBuilder.content {
            citytechinc("jcr:title": "CITYTECH, Inc.")
        }
    }

    def "node where property exists is included"() {
        setup:
        def node = getComponentNode("/content/citytechinc")
        def predicate = new ComponentNodePropertyExistsPredicate("jcr:title")

        expect:
        predicate.apply(node)
    }

    def "node where property does not exist is not included"() {
        setup:
        def node = getComponentNode("/content/citytechinc")
        def predicate = new ComponentNodePropertyExistsPredicate("jcr:description")

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
