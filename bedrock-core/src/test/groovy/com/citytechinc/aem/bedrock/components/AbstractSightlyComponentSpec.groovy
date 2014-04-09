/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.components

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.specs.ComponentSpec

class AbstractSightlyComponentSpec extends ComponentSpec {

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
        def component = init(UselessSightlyComponent) {

        }

        expect:
        component.getComponent("/", AnotherUselessSightlyComponent)
    }

    def "get component from component node"() {
        setup:
        def component = init(UselessSightlyComponent) {

        }

        def componentNode = getComponentNode("/")

        expect:
        component.getComponent(componentNode, AnotherUselessSightlyComponent)
    }
}
