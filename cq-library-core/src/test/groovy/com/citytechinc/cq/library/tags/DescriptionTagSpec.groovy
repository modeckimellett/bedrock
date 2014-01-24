/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import spock.lang.Unroll

import javax.servlet.jsp.tagext.TagSupport

class DescriptionTagSpec extends AbstractMetaTagSpec {

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
    TagSupport createTag() {
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
        ""            | ""               | '<meta name="description" content="JCR Description">'
        ""            | " | Chicago, IL" | '<meta name="description" content="JCR Description | Chicago, IL">'
        "description" | ""               | '<meta name="description" content="Description">'
        "description" | " | Chicago, IL" | '<meta name="description" content="Description | Chicago, IL">'
    }

    def "empty description"() {
        setup:
        setupPage("/content/ctmsp")

        when:
        tag.doEndTag()

        then:
        result == '<meta name="description" content="">'
    }
}
