/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.components;

/**
 * Definition for components that are conditionally editable (e.g. "editable only if on the homepage").  Implementing
 * this interface allows a component class to define a conditional that can be checked while rendering to determine if
 * editing should be disabled for the current component instance.
 * <p/>
 * Note that the actual hiding/disabling of editables is implementation-specific; this interface is not coupled to any
 * client-side functionality.
 */
public interface Editable {

    /**
     * @return true if this component is currently editable
     */
    boolean isEditable();
}
