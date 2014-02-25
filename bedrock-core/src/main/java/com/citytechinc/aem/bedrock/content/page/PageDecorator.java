/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content.page;

import com.citytechinc.aem.bedrock.content.link.ImageLink;
import com.citytechinc.aem.bedrock.content.link.ImageSource;
import com.citytechinc.aem.bedrock.content.link.Linkable;
import com.citytechinc.aem.bedrock.content.link.NavigationLink;
import com.citytechinc.aem.bedrock.content.node.ComponentNode;
import com.day.cq.wcm.api.Page;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.List;

/**
 * Decorates the CQ <code>Page</code> interface with additional convenience methods for traversing the content hierarchy
 * and getters for Bedrock classes.
 */
public interface PageDecorator extends Page, Linkable, ImageSource {

    /**
     * Find the first ancestor page that matches the given predicate condition.
     *
     * @param predicate predicate to match ancestor pages against
     * @return <code>Optional</code> page that matches the predicate condition
     */
    Optional<PageDecorator> findAncestor(Predicate<PageDecorator> predicate);

    /**
     * Returns the absolute parent page. If no page exists at that level, <code>null</code> is returned.
     * <p/>
     * Example (this path == /content/geometrixx/en/products)
     * <pre>
     * | level | returned                        |
     * |     0 | /content                        |
     * |     1 | /content/geometrixx             |
     * |     2 | /content/geometrixx/en          |
     * |     3 | /content/geometrixx/en/products |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
    @Override
    PageDecorator getAbsoluteParent(int level);

    /**
     * Get the child pages of the current page.
     *
     * @return all child pages of current page
     */
    List<PageDecorator> getChildren();

    /**
     * Get the child pages of the current page, excluding children that are not "displayable" (i.e. hidden in nav).
     *
     * @param displayableOnly if true, only pages that are not hidden in navigation will be returned
     * @return child pages of current page
     */
    List<PageDecorator> getChildren(boolean displayableOnly);

    /**
     * Get the child pages of the current page filtered using the given predicate.
     *
     * @param predicate predicate to filter pages on
     * @return filtered list of child pages
     */
    List<PageDecorator> getChildren(Predicate<PageDecorator> predicate);

    /**
     * Get the descendant pages of the current page filtered using the given predicate.
     *
     * @param predicate predicate to filter pages on
     * @param deep false traverses only children; true traverses all descendants
     * @return filtered list of descendant pages
     */
    List<PageDecorator> getChildren(Predicate<PageDecorator> predicate, boolean deep);

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
     * @param imageSource image source to set on the returned image link
     * @return image link with the provided image source
     */
    ImageLink getImageLink(String imageSource);

    /**
     * Get a navigation link for this page.  The returned link will use the navigation title as the link title,
     * defaulting to the JCR title if it does not exist.
     *
     * @return navigation link
     */
    NavigationLink getNavigationLink();

    /**
     * Get a navigation link for this page containing an active state.  The returned link will use the navigation title
     * as the link title, defaulting to the JCR title if it does not exist.
     *
     * @param isActive active state to be set on returned link
     * @return navigation link
     */
    NavigationLink getNavigationLink(boolean isActive);

    /**
     * Get the navigation title for this page.
     *
     * @return optional navigation title
     */
    Optional<String> getNavigationTitleOptional();

    /**
     * Convenience method that returns the manager of this page.
     *
     * @return the page manager
     */
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

    /**
     * Returns the parent page if it's resource adapts to page.
     *
     * @return the parent page or <code>null</code>
     */
    @Override
    PageDecorator getParent();

    /**
     * Returns the relative parent page. If no page exists at that level, <code>null</code> is returned.
     * <p/>
     * Example (this path == /content/geometrixx/en/products)
     * <pre>
     * | level | returned                        |
     * |     0 | /content/geometrixx/en/products |
     * |     1 | /content/geometrixx/en          |
     * |     2 | /content/geometrixx             |
     * |     3 | /content                        |
     * |     4 | null                            |
     * </pre>
     *
     * @param level hierarchy level of the parent page to retrieve
     * @return the respective parent page or <code>null</code>
     */
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
