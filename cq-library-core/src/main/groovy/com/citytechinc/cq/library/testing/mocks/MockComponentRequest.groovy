/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.mocks

import com.citytechinc.cq.library.content.request.ComponentRequest
import com.citytechinc.cq.library.content.request.ComponentServletRequest
import com.day.cq.wcm.api.components.Component
import com.day.cq.wcm.api.components.ComponentContext
import com.day.cq.wcm.api.components.EditContext
import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.api.designer.Designer
import com.day.cq.wcm.api.designer.Style

class MockComponentRequest implements ComponentRequest {

    @Delegate ComponentServletRequest componentServletRequest

    MockComponentRequest(componentServletRequest) {
        this.componentServletRequest = componentServletRequest
    }

    @Override
    Component getComponent() {
        throw new UnsupportedOperationException()
    }

    @Override
    ComponentContext getComponentContext() {
        throw new UnsupportedOperationException()
    }

    @Override
    Design getCurrentDesign() {
        throw new UnsupportedOperationException()
    }

    @Override
    Style getCurrentStyle() {
        throw new UnsupportedOperationException()
    }

    @Override
    Designer getDesigner() {
        throw new UnsupportedOperationException()
    }

    @Override
    EditContext getEditContext() {
        throw new UnsupportedOperationException()
    }
}
