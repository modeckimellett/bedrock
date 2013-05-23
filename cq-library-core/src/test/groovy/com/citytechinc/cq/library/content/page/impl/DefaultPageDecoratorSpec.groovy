/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.impl

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.node.BasicNode
import com.citytechinc.cq.library.content.node.ComponentNode
import com.citytechinc.cq.library.content.page.predicates.TemplatePredicate
import com.day.cq.wcm.api.NameConstants
import com.day.cq.wcm.api.PageManager
import spock.lang.Unroll

class DefaultPageDecoratorSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb", pageTitle: "Page Title", navTitle: "Navigation Title") {
                    component {
                        one("sling:resourceType": "one")
                        two("sling:resourceType": "two")
                    }
                }
                child1 {
                    "jcr:content"(hideInNav: true, "cq:template": "template")
                }
                child2 {

                }
            }
        }

        // also create a page with no jcr:content node
        session.getNode("/content/citytechinc").addNode("empty", NameConstants.NT_PAGE)
        session.save()
    }

    @Unroll
    def "get template path"() {
        setup:
        def page = createPage(path)

        expect:
        page.templatePath == templatePath

        where:
        path                          | templatePath
        "/content/citytechinc"        | ""
        "/content/citytechinc/child1" | "template"
    }

    def "get component node"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.componentNode.get().path == "/content/citytechinc/jcr:content"
    }

    def "get component node returns absent optional for page with no jcr:content node"() {
        setup:
        def page = createPage("/content/citytechinc/empty")

        expect:
        !page.componentNode.present
    }

    def "get component node at relative path"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.getComponentNode("component/one").get().path == "/content/citytechinc/jcr:content/component/one"
    }

    def "adapt to basic node"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.adaptTo(BasicNode).path == "/content/citytechinc/jcr:content"
    }

    def "adapt to component node"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.adaptTo(ComponentNode).path == "/content/citytechinc/jcr:content"
    }

    def "adapt to node for page with no jcr:content node returns null"() {
        setup:
        def page = createPage("/content/citytechinc/empty")

        expect:
        !page.adaptTo(ComponentNode)
    }

    def "get children"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.children.size() == 3
    }

    def "get displayable children"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.getChildren(true).size() == 1
    }

    def "get children filtered for predicate"() {
        setup:
        def page = createPage("/content/citytechinc")
        def predicate = new TemplatePredicate("template")

        expect:
        page.getChildren(predicate).size() == 1
    }

    def "get properties"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.properties.keySet().containsAll(["jcr:title", "otherPagePath"])
    }

    def "get properties for page with no jcr:content node"() {
        setup:
        def page = createPage("/content/citytechinc/empty")

        expect:
        page.properties.isEmpty()
    }

    def "get properties at relative path"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.getProperties("component/one").containsKey("sling:resourceType")
    }

    def "get properties at non-existent relative path"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.getProperties("component/three").isEmpty()
    }

    def "get page title"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.pageTitleOptional.get() == "Page Title"
    }

    def "get page title returns absent where appropriate"() {
        setup:
        def page = createPage("/content/citytechinc/child1")

        expect:
        !page.pageTitleOptional.present
    }

    def "get navigation title optional"() {
        setup:
        def page = createPage("/content/citytechinc")

        expect:
        page.navigationTitleOptional.get() == "Navigation Title"
    }

    def "get navigation title returns absent where appropriate"() {
        setup:
        def page = createPage("/content/citytechinc/child1")

        expect:
        !page.navigationTitleOptional.present
    }

    def "get image link"() {
        setup:
        def page = createPage("/content/citytechinc")
        def imageLink = page.getImageLink("/image")

        expect:
        imageLink.href == "/content/citytechinc.html"
        imageLink.imageSrc == "/image"
    }

    def "get navigation link"() {
        setup:
        def page = createPage("/content/citytechinc")
        def navigationLink = page.navigationLink

        expect:
        navigationLink.href == "/content/citytechinc.html"
        navigationLink.title == "Navigation Title"
    }

    def createPage(path) {
        def pageManager = resourceResolver.adaptTo(PageManager)
        def page = pageManager.getPage(path)

        new DefaultPageDecorator(page)
    }
}
