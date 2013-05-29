/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library

import com.citytechinc.cq.groovy.builders.NodeBuilder
import com.citytechinc.cq.groovy.builders.PageBuilder
import com.citytechinc.cq.groovy.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.AbstractRepositorySpec
import com.citytechinc.cq.groovy.testing.resource.TestingResourceResolver
import com.citytechinc.cq.library.content.page.PageManagerDecorator
import com.citytechinc.cq.library.content.page.impl.DefaultPageManagerDecorator
import spock.lang.Shared

abstract class AbstractCqLibrarySpec extends AbstractRepositorySpec {

    @Shared nodeBuilder

    @Shared pageBuilder

    @Shared resourceResolver

    def setupSpec() {
        resourceResolver = new TestingResourceResolver(session) {
            @Override
            <AdapterType> AdapterType adaptTo(Class<AdapterType> type) {
                if (type == PageManagerDecorator) {
                    new DefaultPageManagerDecorator(this)
                } else {
                    super.adaptTo(type)
                }
            }
        }

        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }
}
