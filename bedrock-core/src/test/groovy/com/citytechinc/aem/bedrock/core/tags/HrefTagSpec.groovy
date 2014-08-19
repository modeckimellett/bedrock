package com.citytechinc.aem.bedrock.core.tags

import com.citytechinc.aem.bedrock.core.specs.BedrockJspTagSpec
import spock.lang.Unroll

@Unroll
class HrefTagSpec extends BedrockJspTagSpec {

    def setupSpec() {
        pageBuilder.content {
            citytechinc {
                "jcr:content"(path: "/content/global")
                ctmsp()
            }
        }
    }

    def "href for property"() {
        setup:
        def tag = new HrefTag()

        tag.propertyName = "path"

        def jspTag = init(tag, "/content/citytechinc/jcr:content")

        when:
        tag.doEndTag()

        then:
        jspTag.output == "/content/global.html"
    }

    def "href for inherited property"() {
        setup:
        def tag = new HrefTag()

        tag.propertyName = propertyName
        tag.inherit = String.valueOf(inherit)

        def jspTag = init(tag, "/content/citytechinc/ctmsp/jcr:content")

        when:
        tag.doEndTag()

        then:
        jspTag.output == output

        where:
        propertyName      | inherit | output
        "path"            | false   | ""
        "path"            | true    | "/content/global.html"
        "nonExistentPath" | false   | ""
        "nonExistentPath" | true    | ""
    }
}