package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.PropertyTagSpec

class HrefTagSpec extends PropertyTagSpec<HrefTag> {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"(path: "/content/global")
                ctmsp()
            }
        }
    }

    @Override
    HrefTag createTag() {
        new HrefTag()
    }

    def "href for existing property"() {
        setup:
        setupComponentNode("/content/citytechinc/jcr:content")

        when:
        tag.propertyName = "path"

        and:
        tag.doEndTag()

        then:
        result == "/content/global.html"
    }

    def "href for non-existent property"() {
        setup:
        setupComponentNode("/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.propertyName = "path"

        and:
        tag.doEndTag()

        then:
        !result
    }

    def "href for inherited property"() {
        setup:
        setupComponentNode("/content/citytechinc/ctmsp/jcr:content")

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
        setupComponentNode("/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.propertyName = "nonExistentPath"
        tag.inherit = String.valueOf(true)

        and:
        tag.doEndTag()

        then:
        !result
    }
}