package com.citytechinc.aem.bedrock.core.specs

import com.citytechinc.aem.bedrock.core.bindings.ComponentBindings
import com.citytechinc.aem.bedrock.core.components.AbstractComponent
import com.citytechinc.aem.prosper.builders.BindingsBuilder

/**
 * Spock specification for testing CQ components.
 */
abstract class ComponentSpec extends BedrockSpec {

    /**
     * Instantiate and initialize the component class for the given type, using the provided closure to build the
     * required bindings.
     *
     * @param type component type
     * @param closure
     * @return initialized component instance
     */
    public <T extends AbstractComponent> T init(Class<T> type,
        @DelegatesTo(value = BindingsBuilder, strategy = Closure.OWNER_FIRST) Closure closure) {
        def bindings = new BindingsBuilder(resourceResolver).build(closure)

        bindings.putAll(new ComponentBindings(bindings))

        def instance = type.newInstance()

        instance.init(bindings)

        instance
    }
}
