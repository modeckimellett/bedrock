/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.bindings;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.scripting.api.BindingsValuesProvider;

import javax.script.Bindings;

@Component(immediate = true)
@Service
public final class ComponentBindingValuesProvider implements BindingsValuesProvider {

    @Override
    public void addBindings(final Bindings bindings) {
        bindings.putAll(ComponentBindings.fromBindings(bindings));
    }
}
