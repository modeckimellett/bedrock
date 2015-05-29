package com.citytechinc.aem.bedrock.core.services.impl

import com.citytechinc.aem.bedrock.api.services.ServiceProvider
import org.apache.sling.api.scripting.SlingScriptHelper
import org.apache.sling.scripting.core.ScriptHelper
import org.osgi.framework.BundleContext

final class DefaultServiceProvider implements ServiceProvider {

    private final SlingScriptHelper slingScriptHelper

    DefaultServiceProvider(SlingScriptHelper slingScriptHelper) {
        this.slingScriptHelper = slingScriptHelper
    }

    DefaultServiceProvider(BundleContext bundleContext) {
        slingScriptHelper = new ScriptHelper(bundleContext, null)
    }

    @Override
    public <T> T getService(Class<T> serviceType) {
        slingScriptHelper.getService(serviceType)
    }

    @Override
    public <T> List<T> getServices(Class<T> serviceType, String filter) {
        slingScriptHelper.getServices(serviceType, filter) as List
    }
}
