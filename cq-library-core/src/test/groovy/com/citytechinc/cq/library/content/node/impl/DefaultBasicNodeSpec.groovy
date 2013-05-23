/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.node.predicates.BasicNodeResourceTypePredicate
import com.citytechinc.cq.library.content.node.predicates.PropertyNamePredicate
import spock.lang.Unroll

class DefaultBasicNodeSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb", externalPath: "http://www.reddit.com") {
                    nsfwImage(fileReference: "omg.png")
                    beer(label: "orval", abv: "9.0", oz: "12")
                    whiskey("sling:resourceType": "rye")
                    malort {
                        one("sling:resourceType": "won")
                        two("sling:resourceType": "tew")
                    }
                }
            }
            ales {
                esb("ESB") {
                    "jcr:content"(otherPagePath: "/content/citytechinc") {
                        fullers("sling:resourceType": "bitter")
                        morland("sling:resourceType": "bitter")
                        greeneking("sling:resourceType": "bitter")
                    }
                }
            }
            lagers {
                "jcr:content"(otherPagePath: "/content/citytechinc") {
                    dynamo("sling:resourceType": "us")
                    stiegl("sling:resourceType": "de")
                    spaten("sling:resourceType": "de")
                }
            }
        }
    }

    def "as map"() {
        setup:
        def map = createBasicNode("/content/citytechinc/jcr:content").asMap()

        expect:
        map["jcr:title"] == "CITYTECH, Inc."
        map["otherPagePath"] == "/content/ales/esb"
    }

    def "get optional"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.get("otherPagePath").get() == "/content/ales/esb"
        !node.get("beer").present
    }

    @Unroll
    def "get as href"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsHref(propertyName).get() == href

        where:
        propertyName    | href
        "otherPagePath" | "/content/ales/esb.html"
        "externalPath"  | "http://www.reddit.com"
    }

    @Unroll
    def "get as href returns absent where appropriate"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        !node.getAsHref(propertyName).present

        where:
        propertyName << ["beer", ""]
    }

    def "get as mapped href"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsMappedHref("otherPagePath").get() == "/content/ales/esb.html"
    }

    def "get as href for null"() {
        when:
        createBasicNode("/content/citytechinc/jcr:content").getAsHref(null)

        then:
        thrown NullPointerException
    }

    def "get as link"() {
        setup:
        def link = createBasicNode("/content/citytechinc/jcr:content").getAsLink("otherPagePath")

        expect:
        link.get().path == "/content/ales/esb"
    }

    def "get as mapped link"() {
        setup:
        def link = createBasicNode("/content/citytechinc/jcr:content").getAsMappedLink("otherPagePath")

        expect:
        link.get().path == "/content/ales/esb"
    }

    def "get as link for null"() {
        when:
        createBasicNode("/content/citytechinc/jcr:content").getAsLink(null)

        then:
        thrown NullPointerException
    }

    def "get as link for non-existent property"() {
        setup:
        def linkOptional = createBasicNode("/content/citytechinc/jcr:content").getAsLink("beer")

        expect:
        !linkOptional.present
    }

    def "get as page"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsPage("otherPagePath").get().path == "/content/ales/esb"
        !node.getAsPage("nonExistentProperty").present
    }

    def "get nodes"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.nodes.size() == 4
    }

    def "get nodes with null predicate"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        when:
        node.getNodes(null)

        then:
        thrown(NullPointerException)
    }

    def "get nodes with predicate"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")
        def predicate = new BasicNodeResourceTypePredicate("rye")

        expect:
        node.getNodes(predicate).size() == 1
    }

    def "get href"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.href == "/content/citytechinc/jcr:content.html"
    }

    def "get image reference"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        !node.imageReference.present
    }

    def "get index"() {
        setup:
        def node = createBasicNode("/content/ales/esb/jcr:content/morland")

        expect:
        node.index == 1
    }

    def "get index for resource type"() {
        setup:
        def node = createBasicNode("/content/lagers/jcr:content/stiegl")

        expect:
        node.getIndex("de") == 0
    }

    def "get named image reference"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getImageReference("nsfwImage").get() == "omg.png"
        !node.getImageReference("sfwImage").present
    }

    def "get link"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.link.path == "/content/citytechinc/jcr:content"
    }

    def "get link builder"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.linkBuilder.build().path == "/content/citytechinc/jcr:content"
    }

    def "get node"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.node.get().path == "/content/citytechinc/jcr:content"
    }

    def "get node at relative path"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getNode("beer").get().path == "/content/citytechinc/jcr:content/beer"
        !node.getNode("wine").present
    }

    def "get nodes at relative path"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getNodes("malort").size() == 2
    }

    def "get nodes at relative path with resource type"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getNodes("malort", "tew").size() == 1
    }

    def "get nodes at relative path with predicate"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")
        def predicate = new BasicNodeResourceTypePredicate("won")

        expect:
        node.getNodes("malort", predicate).size() == 1
    }

    def "get path"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.path == "/content/citytechinc/jcr:content"
    }

    def "get properties"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content/beer")
        def predicate = new PropertyNamePredicate("label")

        expect:
        node.getProperties(predicate)*.name == ["label"]
    }

    def "get resource"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.resource.path == "/content/citytechinc/jcr:content"
    }

    def "has image"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        !node.hasImage
    }

    def "has named image"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        !node.isHasImage("foo")
    }

    def createBasicNode(path) {
        def resource = resourceResolver.getResource(path)

        new DefaultBasicNode(resource)
    }
}
