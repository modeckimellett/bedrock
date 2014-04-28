package com.citytechinc.aem.bedrock.core.tags

import com.day.cq.wcm.api.designer.Design

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_DESIGN_NAME

class FavIconTagSpec extends AbstractMetaTagSpec<FavIconTag> {

    static final def FAVICON = { favIcon ->
        """<link rel="icon" type="image/vnd.microsoft.icon" href="$favIcon">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="$favIcon">"""
    }

    def setupSpec() {
        nodeBuilder.etc {
            designs {
                citytechinc {
                    "favicon.ico"()
                }
            }
        }
    }

    @Override
    FavIconTag createTag() {
        new FavIconTag()
    }

    def "no favicon, no output"() {
        setup:
        def design = Mock(Design) {
            getPath() >> ""
        }

        tag.pageContext.setAttribute DEFAULT_CURRENT_DESIGN_NAME, design

        when:
        tag.doEndTag()

        then:
        !result
    }

    def "valid favicon, HTML output"() {
        setup:
        def design = Mock(Design) {
            getPath() >> "/etc/designs/citytechinc"
        }

        tag.pageContext.setAttribute DEFAULT_CURRENT_DESIGN_NAME, design

        when:
        tag.doEndTag()

        then:
        result == FAVICON("/etc/designs/citytechinc/favicon.ico")
    }
}