package com.citytechinc.aem.bedrock.core.tags;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import javax.servlet.jsp.tagext.TagSupport;
import java.util.List;

import static javax.servlet.jsp.PageContext.REQUEST_SCOPE;

public abstract class AbstractDeferredClientLibTag extends TagSupport {

    protected static final String ATTR_CATEGORIES = "deferredClientLibCategories";

    private static final Splitter SPLITTER = Splitter.on(',').trimResults();

    private String js;

    protected List<String> getCategories() {
        return SPLITTER.splitToList(js);
    }

    protected List<String> getRequestCategories() {
        final String categories = (String) pageContext.getAttribute(ATTR_CATEGORIES, REQUEST_SCOPE);

        final List<String> result;

        if (categories == null) {
            result = Lists.newArrayList();
        } else {
            result = Lists.newArrayList(SPLITTER.splitToList(categories));
        }

        return result;
    }

    public String getJs() {
        return js;
    }

    public void setJs(final String js) {
        this.js = js;
    }
}
