/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library

import com.citytechinc.cq.groovy.builders.NodeBuilder
import com.citytechinc.cq.groovy.builders.PageBuilder
import com.citytechinc.cq.groovy.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.AbstractSlingRepositorySpec
import com.citytechinc.cq.library.content.page.PageManagerDecorator
import com.citytechinc.cq.library.content.page.impl.DefaultPageManagerDecorator
import spock.lang.Shared

abstract class AbstractCqLibrarySpec extends AbstractSlingRepositorySpec {

    @Shared nodeBuilder

    @Shared pageBuilder

    @Override
    void addAdapters() {
        addAdapter(PageManagerDecorator, { resourceResolver -> new DefaultPageManagerDecorator(resourceResolver) })
    }

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        nodeBuilder = new NodeBuilder(session)
        pageBuilder = new PageBuilder(session)
    }
}
