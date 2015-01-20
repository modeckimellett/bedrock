package com.citytechinc.aem.bedrock.core.tags

import spock.lang.Unroll

@Unroll
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

    def "title variations"() {
        setup:
        def proxy = init(TitleTag, "/content/citytechinc")

        proxy.tag.with {
            propertyName = testPropertyName
            suffix = testSuffix
        }

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == html

        where:
        testPropertyName | testSuffix       | html
        ""               | ""               | "<title>CITYTECH, Inc.</title>"
        ""               | " | Chicago, IL" | "<title>CITYTECH, Inc. | Chicago, IL</title>"
        "pageTitle"      | ""               | "<title>CITYTECH</title>"
        "pageTitle"      | " | Chicago, IL" | "<title>CITYTECH | Chicago, IL</title>"
        "navTitle"       | ""               | "<title>CITYTECH, Inc.</title>"
        "navTitle"       | " | Chicago, IL" | "<title>CITYTECH, Inc. | Chicago, IL</title>"
    }

    def "empty title defaults to page name"() {
        setup:
        def proxy = init(TitleTag, "/content/ctmsp")

        when:
        proxy.tag.doEndTag()

        then:
        proxy.output == "<title>ctmsp</title>"
    }
}
