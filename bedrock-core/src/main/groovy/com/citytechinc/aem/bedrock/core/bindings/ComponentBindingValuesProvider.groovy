package com.citytechinc.aem.bedrock.core.bindings

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Reference
import org.apache.felix.scr.annotations.Service
import org.apache.sling.scripting.api.BindingsValuesProvider
import org.osgi.framework.Constants

import javax.script.Bindings

@Component(immediate = true)
@Service(BindingsValuesProvider)
final class ComponentBindingValuesProvider implements BindingsValuesProvider {

    @Reference(target = "(${Constants.SERVICE_PID}=com.day.cq.wcm.core.impl.WCMBindingsValuesProvider)")
    protected BindingsValuesProvider wcmBindingValuesProvider

    @Override
    void addBindings(final Bindings bindings) {
        // ensure WCM bindings are added
        wcmBindingValuesProvider.addBindings(bindings)

        bindings.putAll(new ComponentBindings(bindings))
    }
}
