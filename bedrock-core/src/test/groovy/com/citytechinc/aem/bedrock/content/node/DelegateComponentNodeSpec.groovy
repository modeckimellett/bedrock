/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node

import com.citytechinc.aem.bedrock.specs.BedrockSpec

class DelegateComponentNodeSpec extends BedrockSpec {

    class TestNode extends DelegateComponentNode {

        TestNode(ComponentNode componentNode) {
            super(componentNode)
        }

        def getTitle() {
            get("jcr:title", "")
        }
    }

    def setupSpec() {
        pageBuilder.content {
            home("Home")
        }
    }

    def "delegate"() {
        setup:
        def node = getComponentNode("/content/home/jcr:content")
        def delegatingNode = new TestNode(node)

        expect:
        delegatingNode.title == "Home"
    }
}
