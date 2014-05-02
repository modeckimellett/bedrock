package com.citytechinc.aem.bedrock.core.link.impl

import com.citytechinc.aem.bedrock.api.link.Link
import com.google.common.base.Function

final class LinkFunctions {

    static final Function<Link, String> LINK_TO_HREF = new Function<Link, String>() {
        @Override
        String apply(Link link) {
            link.href
        }
    }

    private LinkFunctions() {

    }
}
