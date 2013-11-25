/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.testing.specs

import com.citytechinc.cq.groovy.extension.builders.PageBuilder
import com.citytechinc.cq.groovy.extension.metaclass.GroovyExtensionMetaClassRegistry
import com.citytechinc.cq.groovy.testing.specs.tag.AbstractTagSpec
import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode
import com.citytechinc.cq.library.content.page.PageManagerDecorator
import com.citytechinc.cq.library.content.page.impl.DefaultPageManagerDecorator
import com.citytechinc.cq.library.tags.DefineObjectsTag
import spock.lang.Shared

/**
 * Spock specification for testing CQ component-based tag support classes.
 */
abstract class AbstractPropertyTagSpec extends AbstractTagSpec {

    @Shared pageBuilder

    @Override
    void addResourceResolverAdapters() {
        addResourceResolverAdapter(PageManagerDecorator, {
            resourceResolver -> new DefaultPageManagerDecorator(resourceResolver)
        })
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
    void setupComponentNode(path) {
        def resource = resourceResolver.getResource(path)
        def componentNode = new DefaultComponentNode(resource)

        tag.pageContext.getAttribute(DefineObjectsTag.ATTR_COMPONENT_NODE) >> componentNode
    }
}
