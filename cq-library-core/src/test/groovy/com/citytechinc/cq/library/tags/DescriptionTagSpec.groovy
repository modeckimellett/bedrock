/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import com.citytechinc.cq.library.testing.specs.AbstractPageTagSpec
import spock.lang.Unroll

class DescriptionTagSpec extends AbstractPageTagSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"("jcr:description": "JCR Description", "description": "Description")
            }
            ctmsp {

            }
        }
    }

    @Override
    def createTag() {
        new DescriptionTag()
    }

    @Unroll
    def "description variations"() {
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
        propertyName  | suffix           | html
        ""            | ""               | "<meta name=\"description\" content=\"JCR Description\"/>"
        ""            | " | Chicago, IL" | "<meta name=\"description\" content=\"JCR Description | Chicago, IL\"/>"
        "description" | ""               | "<meta name=\"description\" content=\"Description\"/>"
        "description" | " | Chicago, IL" | "<meta name=\"description\" content=\"Description | Chicago, IL\"/>"
    }

    def "empty description"() {
        setup:
        setupPage("/content/ctmsp")

        when:
        tag.doEndTag()

        then:
        result == "<meta name=\"description\" content=\"\"/>"
    }
}
