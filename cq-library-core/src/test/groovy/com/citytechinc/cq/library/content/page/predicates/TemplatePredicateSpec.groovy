/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.predicates

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.page.PageManagerDecorator

class TemplatePredicateSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"("cq:template": "homepage")
                child1 {
                    "jcr:content"("cq:template": "template")
                }
                child2()
            }
        }
    }

    def "page has no template property"() {
        setup:
        def page = createPage("/content/citytechinc/child2")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.apply(page)
    }

    def "template matches page template"() {
        setup:
        def page = createPage("/content/citytechinc/child1")
        def predicate = new TemplatePredicate("template")

        expect:
        predicate.apply(page)
    }

    def "template does not match page template"() {
        setup:
        def page = createPage("/content/citytechinc")
        def predicate = new TemplatePredicate("template")

        expect:
        !predicate.apply(page)
    }

    def createPage(path) {
        resourceResolver.adaptTo(PageManagerDecorator).getPage(path)
    }
}
