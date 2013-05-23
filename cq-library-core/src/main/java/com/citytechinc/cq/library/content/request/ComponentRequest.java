/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.request;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentContext;
import com.day.cq.wcm.api.components.EditContext;
import com.day.cq.wcm.api.designer.Design;
import com.day.cq.wcm.api.designer.Designer;
import com.day.cq.wcm.api.designer.Style;

/**
 * Request facade for use in CQ component beans. Wraps all relevant objects from the current request context and
 * provides additional convenience methods for accessing content.
 */
public interface ComponentRequest extends ComponentServletRequest {

    /**
     * @return current CQ component
     */
    Component getComponent();

    /**
     * @return component context for this request
     */
    ComponentContext getComponentContext();

    /**
     * @return design of the addressed resource
     */
    Design getCurrentDesign();

    /**
     * @return style of the addressed resource
     */
    Style getCurrentStyle();

    /**
     * @return designer
     */
    Designer getDesigner();

    /**
     * @return edit context for this request
     */
    EditContext getEditContext();
}
