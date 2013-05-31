/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page;

import com.citytechinc.cq.library.content.link.ImageLink;
import com.citytechinc.cq.library.content.link.Linkable;
import com.citytechinc.cq.library.content.link.NavigationLink;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.List;

public interface PageDecorator extends Page, Linkable {

    @Override
    PageDecorator getAbsoluteParent(int level);

    /**
     * @return all child pages of current page
     */
    List<PageDecorator> getChildren();

    /**
     * @param displayableOnly if true, only pages that are not hidden in navigation will be returned
     * @return child pages of current page
     */
    List<PageDecorator> getChildren(boolean displayableOnly);

    /**
     * @param predicate predicate to filter pages on
     * @return filtered list of child pages
     */
    List<PageDecorator> getChildren(Predicate<PageDecorator> predicate);

    /**
     * Get the component node for the "jcr:content" node for this page.  If the page does not have a content node, an
     * "absent" Optional is returned.
     *
     * @return optional component node for page content
     */
    Optional<ComponentNode> getComponentNode();

    /**
     * Get the component node for the node at the given path relative to the "jcr:content" node for this page.  If the
     * node does not exist, an "absent" Optional is returned.
     *
     * @return optional component node for resource relative to page content
     */
    Optional<ComponentNode> getComponentNode(String relativePath);

    /**
     * Get a link for this page with an attached image source.
     *
     * @param imageSrc image source to set on the returned image link
     * @return image link with the provided image source
     */
    ImageLink getImageLink(String imageSrc);

    /**
     * Get a navigation link for this page.  The returned link will use the navigation title as the link title,
     * defaulting to the JCR title if it does not exist.
     *
     * @return navigation link
     */
    NavigationLink getNavigationLink();

    /**
     * Get the navigation title for this page.
     *
     * @return optional navigation title
     */
    Optional<String> getNavigationTitleOptional();

    @Override
    PageManagerDecorator getPageManager();

    /**
     * Get the page title for this page.  This secondary page title is absent unless it is set in page properties by an
     * author, as opposed to the title returned by <code>getTitle()</code>, which is the required title when the page is
     * created.
     *
     * @return optional page title
     */
    Optional<String> getPageTitleOptional();

    @Override
    PageDecorator getParent();

    @Override
    PageDecorator getParent(int level);

    /**
     * Get the template path for this page.  This method is preferred over getTemplate().getPath(), which is dependent
     * on access to /apps and will therefore fail in publish mode.
     *
     * @return value of cq:template property or empty string if none exists
     */
    String getTemplatePath();
}
