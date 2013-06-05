/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import com.citytechinc.cq.groovy.builders.PageBuilder
import com.citytechinc.cq.groovy.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.tag.AbstractTagSpec
import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode
import com.citytechinc.cq.library.content.page.PageManagerDecorator
import com.citytechinc.cq.library.content.page.impl.DefaultPageManagerDecorator
import spock.lang.Shared

abstract class AbstractPropertyTagSpec extends AbstractTagSpec {

    @Shared pageBuilder

    @Override
    void addAdapters() {
        addAdapter(PageManagerDecorator, { resourceResolver -> new DefaultPageManagerDecorator(resourceResolver) })
    }

    def setupSpec() {
        GroovyExtensionMetaClassRegistry.registerMetaClasses()

        pageBuilder = new PageBuilder(session)
    }

    /**
     * Set a <code>ComponentNode</code> for the given path in the <code>PageContext</code> for the tag under test.
     *
     * @param path node path
     */
    void setComponentNode(path) {
        def resource = resourceResolver.getResource(path)
        def componentNode = new DefaultComponentNode(resource)

        def pageContext = mockPageContext()

        pageContext.getAttribute(DefineObjectsTag.ATTR_COMPONENT_NODE) >> componentNode
    }
}
