package com.citytechinc.aem.bedrock.core.servlets

import com.citytechinc.aem.bedrock.api.request.ComponentRequest
import com.citytechinc.aem.bedrock.core.components.AbstractComponent

class SimpleComponent extends AbstractComponent {

    boolean initialized

    @Override
    public void init(final ComponentRequest request) {
        initialized = true
    }

}
