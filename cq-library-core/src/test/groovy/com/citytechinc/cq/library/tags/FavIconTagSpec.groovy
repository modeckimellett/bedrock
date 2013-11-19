/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags

import com.day.cq.wcm.api.designer.Design
import com.day.cq.wcm.tags.DefineObjectsTEI

class FavIconTagSpec extends AbstractMetaTagSpec {

    static final def FAVICON = { favIcon ->
        """<link rel="icon" type="image/vnd.microsoft.icon" href="$favIcon">
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="$favIcon">"""
    }

    @Override
    def createTag() {
        new FavIconTag()
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

    def "no favicon, no output"() {
        setup:
        setupPageContext()

        tag.pageContext.getAttribute(DefineObjectsTEI.ATTR_CURRENT_DESIGN_NAME) >> Mock(Design) {
            getPath() >> ""
        }

        when:
        tag.doEndTag()

        then:
        !result
    }

    def "valid favicon, HTML output"() {
        setup:
        setupPageContext()

        tag.pageContext.getAttribute(DefineObjectsTEI.ATTR_CURRENT_DESIGN_NAME) >> Mock(Design) {
            getPath() >> "/etc/designs/citytechinc"
        }

        when:
        tag.doEndTag()

        then:
        result == FAVICON("/etc/designs/citytechinc/favicon.ico")
    }
}