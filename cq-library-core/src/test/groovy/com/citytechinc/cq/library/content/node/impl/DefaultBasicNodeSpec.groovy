/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl

import com.citytechinc.cq.library.content.node.predicates.PropertyNamePredicate
import com.citytechinc.cq.library.testing.specs.AbstractCqSpec
import spock.lang.Unroll

class DefaultBasicNodeSpec extends AbstractCqSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb", externalPath: "http://www.reddit.com") {
                    image(fileReference: "/content/dam/image")
                    secondimage(fileReference: "/content/dam/image")
                    nsfwImage(fileReference: "omg.png")
                    beer(label: "orval", abv: "9.0", oz: "12") {
                        image(fileReference: "/content/dam/image")
                        secondimage(fileReference: "/content/dam/image")
                    }
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

        nodeBuilder.content {
            dam("sling:Folder") {
                image("dam:Asset") {
                    "jcr:content"("jcr:data": "data")
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

    def "get href"() {
        setup:
        def node = createBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.href == "/content/citytechinc/jcr:content.html"
    }

    @Unroll
    def "get image reference"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.imageReference.present == isPresent

        where:
        path                               | isPresent
        "/content/citytechinc/jcr:content" | true
        "/content/ales/esb/jcr:content"    | false
    }

    @Unroll
    def "get image source optional"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.imageSrc.present == isPresent

        where:
        path                                       | isPresent
        "/content/citytechinc/jcr:content"         | true
        "/content/ales/esb/jcr:content"            | false
        "/content/citytechinc/jcr:content/beer"    | true
        "/content/citytechinc/jcr:content/whiskey" | false
    }

    @Unroll
    def "get image source"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.imageSrc.get() == imageSrc

        where:
        path                                    | imageSrc
        "/content/citytechinc/jcr:content"      | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content/beer" | "/content/citytechinc/jcr:content/beer.img.png"
    }

    @Unroll
    def "get named image source"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.getImageSrc(name).get() == imageSrc

        where:
        path                                    | name          | imageSrc
        "/content/citytechinc/jcr:content"      | "image"       | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content"      | "secondimage" | "/content/citytechinc.img.secondimage.png"
        "/content/citytechinc/jcr:content/beer" | "image"       | "/content/citytechinc/jcr:content/beer.img.png"
        "/content/citytechinc/jcr:content/beer" | "secondimage" | "/content/citytechinc/jcr:content/beer.img.secondimage.png"
    }

    @Unroll
    def "get image source with width"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.getImageSrc(width).get() == imageSrc

        where:
        path                                    | width | imageSrc
        "/content/citytechinc/jcr:content"      | -1    | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content"      | 100   | "/content/citytechinc.img.100.png"
        "/content/citytechinc/jcr:content/beer" | -1    | "/content/citytechinc/jcr:content/beer.img.png"
        "/content/citytechinc/jcr:content/beer" | 100   | "/content/citytechinc/jcr:content/beer.img.100.png"
    }

    @Unroll
    def "get named image source with width"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.getImageSrc(name, width).get() == imageSrc

        where:
        path                                    | name          | width | imageSrc
        "/content/citytechinc/jcr:content"      | "image"       | -1    | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content"      | "image"       | 100   | "/content/citytechinc.img.100.png"
        "/content/citytechinc/jcr:content"      | "secondimage" | -1    | "/content/citytechinc.img.secondimage.png"
        "/content/citytechinc/jcr:content"      | "secondimage" | 100   | "/content/citytechinc.img.secondimage.100.png"
        "/content/citytechinc/jcr:content/beer" | "image"       | -1    | "/content/citytechinc/jcr:content/beer.img.png"
        "/content/citytechinc/jcr:content/beer" | "image"       | 100   | "/content/citytechinc/jcr:content/beer.img.100.png"
        "/content/citytechinc/jcr:content/beer" | "secondimage" | -1    | "/content/citytechinc/jcr:content/beer.img.secondimage.png"
        "/content/citytechinc/jcr:content/beer" | "secondimage" | 100   | "/content/citytechinc/jcr:content/beer.img.secondimage.100.png"
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

    @Unroll
    def "has image"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.hasImage == hasImage

        where:
        path                               | hasImage
        "/content/citytechinc/jcr:content" | true
        "/content/ales/esb/jcr:content"    | false
    }

    @Unroll
    def "has named image"() {
        setup:
        def node = createBasicNode(path)

        expect:
        node.isHasImage(name) == hasImage

        where:
        path                               | name          | hasImage
        "/content/citytechinc/jcr:content" | "image"       | true
        "/content/citytechinc/jcr:content" | "secondimage" | true
        "/content/citytechinc/jcr:content" | "thirdimage"  | false
        "/content/ales/esb/jcr:content"    | "image"       | false
    }

    def createBasicNode(path) {
        def resource = resourceResolver.getResource(path)

        new DefaultBasicNode(resource)
    }
}
