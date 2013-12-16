/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.constants;

/**
 * Component and resource type constants.
 */
public final class ComponentConstants {

    /**
     * Relative path to content components.
     */
    public static final String COMPONENT_PATH_CONTENT = "/content";

    /**
     * Default page/component image name.
     */
    public static final String DEFAULT_IMAGE_NAME = "image";

    /**
     * Component group that is hidden from the Sidekick.
     */
    public static final String GROUP_HIDDEN = ".hidden";

    /**
     * Conventional node/resource name for a main paragraph system.
     */
    public static final String NODE_NAME_PAR = "par";

    /**
     * Base resource type for all components.
     */
    public static final String RESOURCE_TYPE_PARBASE = "foundation/components/parbase";

    /**
     * Sling resource type for paragraph systems.
     */
    public static final String RESOURCE_TYPE_PARSYS = "foundation/components/parsys";

    /**
     * Property name in component descriptor containing annotated class name.
     */
    public static final String PROPERTY_CLASS_NAME = "className";

    private ComponentConstants() {

    }
}
