package com.citytechinc.aem.bedrock.core.services.impl

import com.citytechinc.aem.bedrock.api.services.ServiceProvider
import com.google.common.collect.ImmutableList
import org.apache.sling.api.scripting.SlingScriptHelper
import org.apache.sling.scripting.core.ScriptHelper
import org.osgi.framework.BundleContext

final class DefaultServiceProvider implements ServiceProvider {

    private final SlingScriptHelper sling

    DefaultServiceProvider(SlingScriptHelper sling) {
        this.sling = sling
    }

    DefaultServiceProvider(BundleContext bundleContext) {
        sling = new ScriptHelper(bundleContext, null)
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        sling.getService(serviceType)
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, String filter) {
        ImmutableList.of(sling.getServices(serviceType, filter))
    }
}
