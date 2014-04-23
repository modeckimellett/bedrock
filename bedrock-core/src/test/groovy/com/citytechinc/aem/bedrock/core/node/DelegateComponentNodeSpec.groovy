package com.citytechinc.aem.bedrock.core.node

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.core.specs.BedrockSpec

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
