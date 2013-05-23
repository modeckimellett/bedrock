/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.predicates;

import com.citytechinc.cq.library.content.page.PageDecorator;
import com.google.common.base.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Predicate that filters on the value of the page's cq:template property.
 */
public final class TemplatePredicate implements Predicate<PageDecorator> {

    private final String templatePath;

    public TemplatePredicate(final PageDecorator page) {
        checkNotNull(page);

        templatePath = page.getTemplatePath();
    }

    public TemplatePredicate(final String templatePath) {
        checkNotNull(templatePath);

        this.templatePath = templatePath;
    }

    @Override
    public boolean apply(final PageDecorator page) {
        return templatePath.equals(page.getTemplatePath());
    }
}
