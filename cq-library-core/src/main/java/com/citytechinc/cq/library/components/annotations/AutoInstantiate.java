/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.components.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker for components that can be automatically instantiated by the {@code <ct:defineObjects/>} in a component JSP.
 * If the component's <code>.content.xml</code> has a "className" attribute value set to a valid component Java class
 * name, the class will be instantiated and set in page context without the need to use the {@code <ct:component/>}
 * tag.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Inherited
@Documented
public @interface AutoInstantiate {

    /**
     * Specify an instance name to use when the component class is set in page context.
     *
     * @return instance name to use instead of the default (uncapitalized simple class name).
     */
    String instanceName() default "";
}
