/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.activator

import groovy.util.logging.Slf4j
import org.osgi.framework.BundleActivator
import org.osgi.framework.BundleContext

@Slf4j("LOG")
class Activator implements BundleActivator {

    static final String JSP_BUNDLE_SYMBOLIC_NAME = "org.apache.sling.scripting.jsp"

    @Override
    void start(BundleContext context) throws Exception {
        def jspBundle = context.bundles.find { bundle -> bundle.symbolicName == JSP_BUNDLE_SYMBOLIC_NAME }

        if (jspBundle) {
            LOG.info "start() restarting JSP bundle"

            // jspBundle.stop()
            // jspBundle.start()
        } else {
            LOG.error "start() JSP bundle not found"
        }
    }

    @Override
    void stop(BundleContext context) throws Exception {

    }
}
