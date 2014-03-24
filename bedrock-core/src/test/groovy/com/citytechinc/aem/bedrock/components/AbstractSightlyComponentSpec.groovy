/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.components

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.testing.specs.SightlyComponentSpec

class AbstractSightlyComponentSpec extends SightlyComponentSpec {

    class UselessSightlyComponent extends AbstractSightlyComponent {

        @Override
        void init(ComponentRequest request) {

        }
    }

    class AnotherUselessSightlyComponent extends AbstractSightlyComponent {

        @Override
        void init(ComponentRequest request) {

        }
    }

    def "get component from path"() {
        setup:
        def request = componentRequestBuilder.build()
        def component = getComponent(request, UselessSightlyComponent)

        expect:
        component.getComponent("/", AnotherUselessSightlyComponent)
    }

    def "get component from component node"() {
        setup:
        def request = componentRequestBuilder.build()
        def component = getComponent(request, UselessSightlyComponent)
        def componentNode = getComponentNode("/")

        expect:
        component.getComponent(componentNode, AnotherUselessSightlyComponent)
    }
}
