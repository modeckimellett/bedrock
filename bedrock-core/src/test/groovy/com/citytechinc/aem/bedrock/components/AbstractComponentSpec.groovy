/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.components

import com.citytechinc.aem.bedrock.content.request.ComponentRequest
import com.citytechinc.aem.bedrock.specs.ComponentSpec

class AbstractComponentSpec extends ComponentSpec {

    class UselessComponent extends AbstractComponent {

        @Override
        void init(ComponentRequest request) {

        }
    }

    class AnotherUselessComponent extends AbstractComponent {

        @Override
        void init(ComponentRequest request) {

        }
    }

    def "get component from path"() {
        setup:
        def component = init(UselessComponent) {

        }

        expect:
        component.getComponent("/", AnotherUselessComponent)
    }

    def "get component from component node"() {
        setup:
        def component = init(UselessComponent) {

        }

        def componentNode = getComponentNode("/")

        expect:
        component.getComponent(componentNode, AnotherUselessComponent)
    }
}
