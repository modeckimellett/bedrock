/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.page.impl

import com.citytechinc.aem.bedrock.content.node.BasicNode
import com.citytechinc.aem.bedrock.content.node.ComponentNode
import com.citytechinc.aem.bedrock.content.page.PageDecorator
import com.citytechinc.aem.bedrock.content.page.predicates.TemplatePredicate
import com.citytechinc.aem.bedrock.specs.BedrockSpec
import com.day.cq.wcm.api.NameConstants
import com.google.common.base.Predicate
import com.google.common.base.Predicates
import spock.lang.Unroll

@Unroll
class DefaultPageDecoratorSpec extends BedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb", pageTitle: "Page Title",
                    navTitle: "Navigation Title") {
                    component {
                        one("sling:resourceType": "one")
                        two("sling:resourceType": "two")
                    }
                }
                child1 {
                    "jcr:content"(hideInNav: true, "cq:template": "template")
                    grandchild {

                    }
                }
                child2 {
                    "jcr:content"(pageTitle: "Child 2") {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
                }
            }
            other {

            }
        }

        nodeBuilder.content {
            citytechinc {
                empty(NameConstants.NT_PAGE)
            }
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content"("jcr:data": "data")
                }
            }
        }
    }

    def "get image source optional"() {
        setup:
        def page = getPage(path)

        expect:
        page.imageSource.present == isPresent

        where:
        path                          | isPresent
        "/content/citytechinc/child1" | false
        "/content/citytechinc/child2" | true
    }

    def "get image source"() {
        setup:
        def page = getPage("/content/citytechinc/child2")

        expect:
        page.imageSource.get() == "/content/citytechinc/child2.img.png"
    }

    def "get named image source"() {
        setup:
        def page = getPage("/content/citytechinc/child2")

        expect:
        page.getImageSource(name).get() == imageSource

        where:
        name          | imageSource
        "image"       | "/content/citytechinc/child2.img.png"
        "secondimage" | "/content/citytechinc/child2.img.secondimage.png"
    }

    def "get image source with width"() {
        setup:
        def page = getPage("/content/citytechinc/child2")

        expect:
        page.getImageSource(width).get() == imageSource

        where:
        width | imageSource
        -1    | "/content/citytechinc/child2.img.png"
        100   | "/content/citytechinc/child2.img.100.png"
    }

    def "get named image source with width"() {
        setup:
        def page = getPage("/content/citytechinc/child2")

        expect:
        page.getImageSource(name, width).get() == imageSource

        where:
        name          | width | imageSource
        "image"       | -1    | "/content/citytechinc/child2.img.png"
        "image"       | 100   | "/content/citytechinc/child2.img.100.png"
        "secondimage" | -1    | "/content/citytechinc/child2.img.secondimage.png"
        "secondimage" | 100   | "/content/citytechinc/child2.img.secondimage.100.png"
    }

    def "find ancestor optional"() {
        setup:
        def page = getPage(path)
        def predicate = new Predicate<PageDecorator>() {
            @Override
            boolean apply(PageDecorator input) {
                input.title == "CITYTECH, Inc."
            }
        }

        expect:
        page.findAncestor(predicate).present == isPresent

        where:
        path                          | isPresent
        "/content/citytechinc"        | true
        "/content/citytechinc/child1" | true
        "/content/other"              | false
    }

    def "get template path"() {
        setup:
        def page = getPage(path)

        expect:
        page.templatePath == templatePath

        where:
        path                          | templatePath
        "/content/citytechinc"        | ""
        "/content/citytechinc/child1" | "template"
    }

    def "get component node"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.componentNode.get().path == "/content/citytechinc/jcr:content"
    }

    def "get component node returns absent optional for page with no jcr:content node"() {
        setup:
        def page = getPage("/content/citytechinc/empty")

        expect:
        !page.componentNode.present
    }

    def "get component node at relative path"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.getComponentNode("component/one").get().path == "/content/citytechinc/jcr:content/component/one"
    }

    def "adapt to basic node"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.adaptTo(BasicNode).path == "/content/citytechinc/jcr:content"
    }

    def "adapt to component node"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.adaptTo(ComponentNode).path == "/content/citytechinc/jcr:content"
    }

    def "adapt to node for page with no jcr:content node returns null"() {
        setup:
        def page = getPage("/content/citytechinc/empty")

        expect:
        !page.adaptTo(ComponentNode)
    }

    def "get children"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.children.size() == 3
    }

    def "get displayable children"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.getChildren(true).size() == 1
    }

    def "get children filtered for predicate"() {
        setup:
        def page = getPage("/content/citytechinc")
        def predicate = new TemplatePredicate("template")

        expect:
        page.getChildren(predicate).size() == 1
    }

    def "get descendants"() {
        setup:
        def page = getPage("/content/citytechinc")
        def predicate = Predicates.alwaysTrue()

        expect:
        page.getChildren(predicate, true).size() == 4
    }

    def "get properties"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.properties.keySet().containsAll(["jcr:title", "otherPagePath"])
    }

    def "get properties for page with no jcr:content node"() {
        setup:
        def page = getPage("/content/citytechinc/empty")

        expect:
        page.properties.isEmpty()
    }

    def "get properties at relative path"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.getProperties("component/one").containsKey("sling:resourceType")
    }

    def "get properties at non-existent relative path"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.getProperties("component/three").isEmpty()
    }

    def "get page title"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.pageTitleOptional.get() == "Page Title"
    }

    def "get page title returns absent where appropriate"() {
        setup:
        def page = getPage("/content/citytechinc/child1")

        expect:
        !page.pageTitleOptional.present
    }

    def "get navigation title optional"() {
        setup:
        def page = getPage("/content/citytechinc")

        expect:
        page.navigationTitleOptional.get() == "Navigation Title"
    }

    def "get navigation title returns absent where appropriate"() {
        setup:
        def page = getPage("/content/citytechinc/child1")

        expect:
        !page.navigationTitleOptional.present
    }

    def "get image link"() {
        setup:
        def page = getPage("/content/citytechinc")
        def imageLink = page.getImageLink("/image")

        expect:
        imageLink.href == "/content/citytechinc.html"
        imageLink.imageSource == "/image"
    }

    def "get navigation link"() {
        setup:
        def page = getPage("/content/citytechinc")
        def navigationLink = page.navigationLink

        expect:
        navigationLink.href == "/content/citytechinc.html"
        navigationLink.title == "Navigation Title"
    }
}
