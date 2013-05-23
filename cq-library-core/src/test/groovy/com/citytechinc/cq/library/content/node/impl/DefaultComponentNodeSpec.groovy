/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node.impl

import com.citytechinc.cq.library.AbstractCqLibrarySpec
import com.citytechinc.cq.library.content.node.ComponentNode
import com.google.common.base.Predicate
import spock.lang.Unroll

class DefaultComponentNodeSpec extends AbstractCqLibrarySpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(otherPagePath: "/content/ales/esb") {
                    nsfwImage(fileReference: "omg.png")
                    beer()
                    whiskey("sling:resourceType": "rye")
                    malort {
                        one("sling:resourceType": "won")
                        two("sling:resourceType": "tew")
                    }
                }
            }
            ales {
                esb("ESB") {
                    "jcr:content"(otherPagePath: "/content/citytechinc", externalPath: "http://www.reddit.com") {
                        fullers("sling:resourceType": "bitter")
                        morland("sling:resourceType": "bitter")
                        greeneking("sling:resourceType": "bitter")
                    }
                    suds {
                        "jcr:content"(otherPagePath: "") {
                            container {
                                child1("jcr:title": "Zeus")
                                child2()
                            }
                        }
                        pint {
                            keg {
                                "jcr:content" {
                                    container()
                                }
                            }
                            barrel {
                                "jcr:content" {
                                    container {
                                        child1()
                                    }
                                }
                            }
                        }
                    }
                    bar {
                        "jcr:content" {
                            wood {
                                container {
                                    pine()
                                    spruce()
                                    maple()
                                }
                            }
                        }
                        tree {
                            "jcr:content" {
                                wood()
                            }
                        }
                    }
                    lace {
                        "jcr:content"() {
                            parent {
                                child1("sling:resourceType": "unknown")
                                child2("sling:resourceType": "unknown")
                                child3("sling:resourceType": "known")
                            }
                        }
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
            inheritance {
                "jcr:content"("jcr:title": "Inheritance") {
                    component("jcr:title": "Component")
                }
                child {
                    "jcr:content" {
                        component()
                    }
                }
            }
        }
    }

    @Unroll
    def "find ancestor with property"() {
        setup:
        def node = createComponentNode(path)
        def ancestorNodeOptional = node.findAncestorWithProperty("jcr:title")

        expect:
        ancestorNodeOptional.get().path == ancestorPath

        where:
        path                                               | ancestorPath
        "/content/inheritance/child/jcr:content"           | "/content/inheritance/jcr:content"
        "/content/inheritance/child/jcr:content/component" | "/content/inheritance/jcr:content/component"
    }

    @Unroll
    def "find ancestor returns absent"() {
        setup:
        def node = createComponentNode(path)
        def ancestorNodeOptional = node.findAncestorWithProperty("jcr:description")

        expect:
        !ancestorNodeOptional.present

        where:
        path << ["/content/inheritance/child/jcr:content", "/content/inheritance/child/jcr:content/component"]
    }

    @Unroll
    def "get as href inherited"() {
        setup:
        def node = createComponentNode(path)

        expect:
        node.getAsHrefInherited(propertyName).get() == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/citytechinc.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/citytechinc.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    @Unroll
    def "get as href inherited returns absent where appropriate"() {
        setup:
        def node = createComponentNode(path)

        expect:
        !node.getAsHrefInherited(propertyName).present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    @Unroll
    def "get as link inherited"() {
        setup:
        def node = createComponentNode(path)

        expect:
        node.getAsLinkInherited(propertyName).get().href == href

        where:
        path                                 | propertyName    | href
        "/content/ales/esb/jcr:content"      | "otherPagePath" | "/content/citytechinc.html"
        "/content/ales/esb/suds/jcr:content" | "otherPagePath" | ""
        "/content/ales/esb/lace/jcr:content" | "otherPagePath" | "/content/citytechinc.html"
        "/content/ales/esb/jcr:content"      | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/suds/jcr:content" | "externalPath"  | "http://www.reddit.com"
        "/content/ales/esb/lace/jcr:content" | "externalPath"  | "http://www.reddit.com"
    }

    @Unroll
    def "get as link inherited returns absent where appropriate"() {
        setup:
        def node = createComponentNode(path)

        expect:
        !node.getAsLinkInherited("nonExistentPath").present

        where:
        path                                 | propertyName
        "/content/ales/esb/jcr:content"      | "nonExistentPath"
        "/content/ales/esb/suds/jcr:content" | "nonExistentPath"
    }

    def "get as page inherited"() {
        setup:
        def node = createComponentNode("/content/ales/esb/lace/jcr:content")

        expect:
        node.getAsPageInherited("otherPagePath").get().path == "/content/citytechinc"
        !node.getAsPageInherited("nonExistentPagePath").present
    }

    def "get nodes inherited"() {
        setup:
        def node = createComponentNode(path)

        expect:
        node.getNodesInherited("container").size() == size

        where:
        path                                             | size
        "/content/ales/esb/suds/pint/jcr:content"        | 2
        "/content/ales/esb/suds/pint/keg/jcr:content"    | 0
        "/content/ales/esb/suds/pint/barrel/jcr:content" | 1
        "/content/ales/esb/bar/tree/jcr:content/wood"    | 3
    }

    def "get image reference inherited"() {

    }

    def "get image reference inherited for name"() {

    }

    @Unroll
    def "get inherited"() {
        setup:
        def node = createComponentNode(path)

        expect:
        node.getInherited(propertyName, "") == propertyValue

        where:
        path                                                              | propertyName  | propertyValue
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "jcr:title"   | "Zeus"
        "/content/ales/esb/suds/pint/barrel/jcr:content/container/child1" | "nonExistent" | ""
        "/content/ales/esb/jcr:content/fullers"                           | "any"         | ""

    }

    def "get inherited optional"() {
        setup:
        def node = createComponentNode("/content/ales/esb/lace/jcr:content")

        expect:
        node.getInherited("otherPagePath").present
        !node.getInherited("nonExistentProperty").present
    }

    def "get component node at relative path"() {
        setup:
        def node = createComponentNode("/content/citytechinc/jcr:content")

        expect:
        node.getComponentNode("whiskey").present
        !node.getComponentNode("vodka").present
    }

    @Unroll
    def "get component nodes"() {
        setup:
        def node = createComponentNode(path)

        expect:
        node.getComponentNodes().size() == size

        where:
        path                                    | size
        "/content/citytechinc/jcr:content"      | 4
        "/content/citytechinc/jcr:content/beer" | 0
    }

    def "get component nodes for predicate"() {
        setup:
        def node = createComponentNode("/content/citytechinc/jcr:content/malort")
        def predicate = new Predicate<ComponentNode>() {
            @Override
            boolean apply(ComponentNode input) {
                return input.resource.resourceType == "tew"
            }
        }

        expect:
        node.getComponentNodes(predicate).size() == 1
    }

    def "get component nodes at relative path"() {
        setup:
        def node = createComponentNode("/content/citytechinc/jcr:content")

        expect:
        node.getComponentNodes(relativePath).size() == size

        where:
        relativePath | size
        "whiskey"    | 0
        "malort"     | 2
    }

    def "get component nodes at relative path for resource type"() {
        setup:
        def node = createComponentNode("/content/citytechinc/jcr:content")

        expect:
        node.getComponentNodes("malort", resourceType).size() == size

        where:
        resourceType   | size
        "non-existent" | 0
        "tew"          | 1
    }

    def "get component nodes at relative path for predicate"() {
        setup:
        def node = createComponentNode("/content/ales/esb/lace/jcr:content")
        def predicate = new Predicate<ComponentNode>() {
            @Override
            boolean apply(ComponentNode input) {
                return input.resource.resourceType == "unknown"
            }
        }

        expect:
        node.getComponentNodes("parent", predicate).size() == 2
    }

    def createComponentNode(path) {
        def resource = resourceResolver.getResource(path)

        new DefaultComponentNode(resource)
    }
}