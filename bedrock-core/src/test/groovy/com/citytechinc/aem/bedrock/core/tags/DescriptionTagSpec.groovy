package com.citytechinc.aem.bedrock.core.tags

import spock.lang.Unroll

@Unroll
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

    def "description variations"() {
        setup:
        def tag = new DescriptionTag()

        tag.propertyName = propertyName
        tag.suffix = suffix

        def jspTag = init(tag, "/content/citytechinc")

        when:
        tag.doEndTag()

        then:
        jspTag.output == html

        where:
        propertyName  | suffix           | html
        ""            | ""               | '<meta name="description" content="JCR Description">'
        ""            | " | Chicago, IL" | '<meta name="description" content="JCR Description | Chicago, IL">'
        "description" | ""               | '<meta name="description" content="Description">'
        "description" | " | Chicago, IL" | '<meta name="description" content="Description | Chicago, IL">'
    }

    def "empty description"() {
        setup:
        def tag = new DescriptionTag()
        def jspTag = init(tag, "/content/ctmsp")

        when:
        tag.doEndTag()

        then:
        jspTag.output == '<meta name="description" content="">'
    }
}
