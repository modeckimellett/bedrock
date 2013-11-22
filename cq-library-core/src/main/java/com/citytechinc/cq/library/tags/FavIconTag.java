/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.tags;

import com.day.cq.wcm.api.designer.Design;
import com.google.common.collect.ImmutableList;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.day.cq.wcm.tags.DefineObjectsTag.DEFAULT_CURRENT_DESIGN_NAME;
import static org.apache.sling.scripting.jsp.taglib.DefineObjectsTag.DEFAULT_RESOURCE_RESOLVER_NAME;

public final class FavIconTag extends AbstractMetaTag {

    private static final Logger LOG = LoggerFactory.getLogger(FavIconTag.class);

    private static final long serialVersionUID = 1L;

    private static final String TAG_START = "<link rel=";

    private static final List<String> RELS = ImmutableList.of("icon", "shortcut icon");

    @Override
    public int doEndTag() throws JspTagException {
        final Design currentDesign = (Design) pageContext.getAttribute(DEFAULT_CURRENT_DESIGN_NAME);
        final ResourceResolver resourceResolver = (ResourceResolver) pageContext.getAttribute(
            DEFAULT_RESOURCE_RESOLVER_NAME);

        final String favIconPath = currentDesign.getPath() + "/favicon.ico";
        final String favIcon = resourceResolver.getResource(favIconPath) == null ? null : favIconPath;

        if (favIcon == null) {
            LOG.debug("doEndTag() favicon is null, skipping output");
        } else {
            final StringBuilder html = new StringBuilder();

            final Iterator<String> iterator = RELS.iterator();

            while (iterator.hasNext()) {
                html.append(TAG_START);
                html.append('"');
                html.append(iterator.next());
                html.append('"');
                html.append(" type=\"image/vnd.microsoft.icon\" href=\"");
                html.append(getXssApi().getValidHref(favIcon));
                html.append(getTagEnd());

                if (iterator.hasNext()) {
                    html.append('\n');
                }
            }

            try {
                pageContext.getOut().write(html.toString());
            } catch (IOException ioe) {
                LOG.error("error writing favicon", ioe);

                throw new JspTagException(ioe);
            }
        }

        return EVAL_PAGE;
    }
}
