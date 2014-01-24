/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.citytechinc.cq.library.content.request.ComponentRequest;

import java.lang.reflect.InvocationTargetException;

import static com.citytechinc.cq.library.tags.DefineObjectsTag.ATTR_COMPONENT_REQUEST;

/**
 * Base class for tags that instantiate component classes.
 */
public abstract class AbstractComponentInstanceTag extends AbstractScopedTag {

    protected final Object getInstance(final Class<?> clazz)
        throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final ComponentRequest componentRequest = (ComponentRequest) pageContext.getAttribute(ATTR_COMPONENT_REQUEST);

        return clazz.getConstructor(ComponentRequest.class).newInstance(componentRequest);
    }

    protected final Object getInstance(final String className)
        throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException,
        IllegalAccessException {
        final Class<?> clazz = Class.forName(className);

        return getInstance(clazz);
    }
}
