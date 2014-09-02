package com.citytechinc.aem.bedrock.core.bindings

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Service
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.scripting.api.BindingsValuesProvider

import javax.script.Bindings

import static org.apache.sling.api.scripting.SlingBindings.REQUEST

@Component(immediate = true)
@Service(BindingsValuesProvider)
final class BedrockBindingValuesProvider implements BindingsValuesProvider {

    @Override
    void addBindings(Bindings bindings) {
        def slingRequest = bindings.get(REQUEST) as SlingHttpServletRequest

        bindings.putAll(new BedrockBindings(slingRequest))
    }
}
