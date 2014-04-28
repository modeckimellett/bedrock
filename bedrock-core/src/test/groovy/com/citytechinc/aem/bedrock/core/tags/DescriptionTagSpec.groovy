package com.citytechinc.aem.bedrock.core.tags
import spock.lang.Unroll

@Unroll
class DescriptionTagSpec extends AbstractMetaTagSpec<DescriptionTag> {

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
    DescriptionTag createTag() {
        new DescriptionTag()
    }

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
