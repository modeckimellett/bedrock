/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import spock.lang.Unroll

class TitleTagSpec extends AbstractMetaTagSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc("CITYTECH, Inc.") {
                "jcr:content"(pageTitle: "CITYTECH")
            }
            ctmsp {
                "jcr:content"(pageTitle: "CTMSP")
            }
        }
    }

    @Override
    def createTag() {
        new TitleTag()
    }

    @Unroll
    def "title variations"() {
        setup:
        setupPage("/content/citytechinc")

        when:
        tag.propertyName = propertyName
        tag.suffix = suffix

        and:
        tag.doEndTag()

        then:
        result == html

        where:
        propertyName | suffix           | html
        ""           | ""               | "<title>CITYTECH, Inc.</title>"
        ""           | " | Chicago, IL" | "<title>CITYTECH, Inc. | Chicago, IL</title>"
        "pageTitle"  | ""               | "<title>CITYTECH</title>"
        "pageTitle"  | " | Chicago, IL" | "<title>CITYTECH | Chicago, IL</title>"
        "navTitle"   | ""               | "<title>CITYTECH, Inc.</title>"
        "navTitle"   | " | Chicago, IL" | "<title>CITYTECH, Inc. | Chicago, IL</title>"
    }

    def "empty title defaults to page name"() {
        setup:
        setupPage("/content/ctmsp")

        when:
        tag.doEndTag()

        then:
        result == "<title>ctmsp</title>"
    }
}
