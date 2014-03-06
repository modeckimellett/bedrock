/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.node.impl

import com.citytechinc.aem.bedrock.content.node.predicates.PropertyNamePredicate
import com.citytechinc.aem.bedrock.testing.specs.AbstractBedrockSpec
import spock.lang.Unroll

@Unroll
class DefaultBasicNodeSpec extends AbstractBedrockSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb", nonExistentPagePath: "/content/home", externalPath: "http://www.reddit.com", multiValue: ["one", "two"]) {
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
        def map = getBasicNode("/content/citytechinc/jcr:content").asMap()

        expect:
        map["jcr:title"] == "CITYTECH, Inc."
        map["otherPagePath"] == "/content/ales/esb"
    }

    def "get as list"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsList("multiValue", String) == ["one", "two"]
    }

    def "get as href"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsHref(propertyName).get() == href

        where:
        propertyName    | href
        "otherPagePath" | "/content/ales/esb.html"
        "externalPath"  | "http://www.reddit.com"
    }

    def "get as href strict"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsHref(propertyName, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/content/ales/esb.html"
        "nonExistentPagePath" | "/content/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href returns absent where appropriate"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        !node.getAsHref(propertyName).present

        where:
        propertyName << ["beer", ""]
    }

    def "get as mapped href"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsHref("otherPagePath", false, true).get() == "/content/ales/esb.html"
    }

    def "get as mapped href strict"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsHref(propertyName, true, true).get() == href

        where:
        propertyName          | href
        "otherPagePath"       | "/content/ales/esb.html"
        "nonExistentPagePath" | "/content/home"
        "externalPath"        | "http://www.reddit.com"
    }

    def "get as href for null"() {
        when:
        getBasicNode("/content/citytechinc/jcr:content").getAsHref(null)

        then:
        thrown NullPointerException
    }

    def "get as link"() {
        setup:
        def link = getBasicNode("/content/citytechinc/jcr:content").getAsLink("otherPagePath").get()

        expect:
        link.path == "/content/ales/esb"
    }

    def "get as link strict"() {
        setup:
        def link = getBasicNode("/content/citytechinc/jcr:content").getAsLink("nonExistentPagePath", true).get()

        expect:
        link.path == "/content/home"
        link.external
        link.extension == ""
    }

    def "get as mapped link"() {
        setup:
        def link = getBasicNode("/content/citytechinc/jcr:content").getAsLink("otherPagePath", false, true).get()

        expect:
        link.path == "/content/ales/esb"
    }

    def "get as mapped link strict"() {
        setup:
        def link = getBasicNode("/content/citytechinc/jcr:content").getAsLink("nonExistentPagePath", true, true).get()

        expect:
        link.path == "/content/home"
        link.external
        link.extension == ""
    }

    def "get as link for null"() {
        when:
        getBasicNode("/content/citytechinc/jcr:content").getAsLink(null)

        then:
        thrown NullPointerException
    }

    def "get as link for non-existent property"() {
        setup:
        def linkOptional = getBasicNode("/content/citytechinc/jcr:content").getAsLink("beer")

        expect:
        !linkOptional.present
    }

    def "get as page"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getAsPage("otherPagePath").get().path == "/content/ales/esb"
        !node.getAsPage("nonExistentProperty").present
    }

    def "get href"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.href == "/content/citytechinc/jcr:content.html"
    }

    def "get image reference"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.imageReference.present == isPresent

        where:
        path                               | isPresent
        "/content/citytechinc/jcr:content" | true
        "/content/ales/esb/jcr:content"    | false
    }

    def "get image source optional"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.imageSource.present == isPresent

        where:
        path                                       | isPresent
        "/content/citytechinc/jcr:content"         | true
        "/content/ales/esb/jcr:content"            | false
        "/content/citytechinc/jcr:content/beer"    | true
        "/content/citytechinc/jcr:content/whiskey" | false
    }

    def "get image source"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.imageSource.get() == imageSrc

        where:
        path                                    | imageSrc
        "/content/citytechinc/jcr:content"      | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content/beer" | "/content/citytechinc/jcr:content/beer.img.png"
    }

    def "get named image source"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.getImageSource(name).get() == imageSrc

        where:
        path                                    | name          | imageSrc
        "/content/citytechinc/jcr:content"      | "image"       | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content"      | "secondimage" | "/content/citytechinc.img.secondimage.png"
        "/content/citytechinc/jcr:content/beer" | "image"       | "/content/citytechinc/jcr:content/beer.img.png"
        "/content/citytechinc/jcr:content/beer" | "secondimage" | "/content/citytechinc/jcr:content/beer.img.secondimage.png"
    }

    def "get image source with width"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.getImageSource(width).get() == imageSrc

        where:
        path                                    | width | imageSrc
        "/content/citytechinc/jcr:content"      | -1    | "/content/citytechinc.img.png"
        "/content/citytechinc/jcr:content"      | 100   | "/content/citytechinc.img.100.png"
        "/content/citytechinc/jcr:content/beer" | -1    | "/content/citytechinc/jcr:content/beer.img.png"
        "/content/citytechinc/jcr:content/beer" | 100   | "/content/citytechinc/jcr:content/beer.img.100.png"
    }

    def "get named image source with width"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.getImageSource(name, width).get() == imageSrc

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
        def node = getBasicNode("/content/ales/esb/jcr:content/morland")

        expect:
        node.index == 1
    }

    def "get index for resource type"() {
        setup:
        def node = getBasicNode("/content/lagers/jcr:content/stiegl")

        expect:
        node.getIndex("de") == 0
    }

    def "get named image reference"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.getImageReference("nsfwImage").get() == "omg.png"
        !node.getImageReference("sfwImage").present
    }

    def "get link"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.link.path == "/content/citytechinc/jcr:content"
    }

    def "get link builder"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.linkBuilder.build().path == "/content/citytechinc/jcr:content"
    }

    def "get node"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.node.get().path == "/content/citytechinc/jcr:content"
    }

    def "get path"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.path == "/content/citytechinc/jcr:content"
    }

    def "get properties"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content/beer")
        def predicate = new PropertyNamePredicate("label")

        expect:
        node.getProperties(predicate)*.name == ["label"]
    }

    def "get resource"() {
        setup:
        def node = getBasicNode("/content/citytechinc/jcr:content")

        expect:
        node.resource.path == "/content/citytechinc/jcr:content"
    }

    def "has image"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.hasImage == hasImage

        where:
        path                               | hasImage
        "/content/citytechinc/jcr:content" | true
        "/content/ales/esb/jcr:content"    | false
    }

    def "has named image"() {
        setup:
        def node = getBasicNode(path)

        expect:
        node.isHasImage(name) == hasImage

        where:
        path                               | name          | hasImage
        "/content/citytechinc/jcr:content" | "image"       | true
        "/content/citytechinc/jcr:content" | "secondimage" | true
        "/content/citytechinc/jcr:content" | "thirdimage"  | false
        "/content/ales/esb/jcr:content"    | "image"       | false
    }

    def getBasicNode(path) {
        def resource = resourceResolver.getResource(path)

        new DefaultBasicNode(resource)
    }
}
