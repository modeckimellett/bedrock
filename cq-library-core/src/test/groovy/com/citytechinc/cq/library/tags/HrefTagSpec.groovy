/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

class HrefTagSpec extends AbstractPropertyTagSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"(path: "/content/global")
                ctmsp {

                }
            }
        }
    }

    @Override
    def createTag() {
        new HrefTag()
    }

    def "href for existing property"() {
        setup:
        setComponentNode("/content/citytechinc/jcr:content")

        when:
        tag.propertyName = "path"

        and:
        tag.doEndTag()

        then:
        result == "/content/global.html"
    }

    def "href for non-existent property"() {
        setup:
        setComponentNode("/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.propertyName = "path"

        and:
        tag.doEndTag()

        then:
        !result
    }

    def "href for inherited property"() {
        setup:
        setComponentNode("/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.propertyName = "path"
        tag.inherit = String.valueOf(true)

        and:
        tag.doEndTag()

        then:
        result == "/content/global.html"
    }

    def "href for non-existent inherited property"() {
        setup:
        setComponentNode("/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.propertyName = "nonExistentPath"
        tag.inherit = String.valueOf(true)

        and:
        tag.doEndTag()

        then:
        !result
    }
}