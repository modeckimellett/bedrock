package com.citytechinc.aem.bedrock.core.components

class TestComponent extends AbstractComponent {

    def getTitle() {
        get("jcr:title", "")
    }
}
