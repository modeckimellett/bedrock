package com.citytechinc.aem.bedrock.core.bindings

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service
import org.apache.sling.scripting.api.BindingsValuesProvider

import javax.script.Bindings

@Component(immediate = true)
@Service(BindingsValuesProvider)
final class ComponentBindingValuesProvider implements BindingsValuesProvider {

    @Override
    void addBindings(final Bindings bindings) {
        bindings.putAll(new ComponentBindings(bindings))
    }
}
