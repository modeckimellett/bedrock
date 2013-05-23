/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;

import javax.jcr.query.Query;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

public interface PageManagerDecorator extends PageManager {

    /**
     * Search for pages containing the specified tags.
     *
     * @param path start path
     * @param tagIds set of tag IDs
     * @param matchOne if true, 'OR' the specified tag IDs, 'AND' otherwise
     * @return pages containing specified tags
     */
    List<PageDecorator> findPages(String path, Collection<String> tagIds, boolean matchOne);

    /**
     * @param path start path
     * @param template template path
     * @return pages matching specified template
     */
    List<PageDecorator> findPages(String path, String template);

    /**
     * Recursively search for pages, starting at the specified path.
     *
     * @param path start path
     * @param predicate predicate to determine if a page should be included in the result list
     * @return pages matching filter criteria
     */
    List<PageDecorator> findPages(String path, Predicate<PageDecorator> predicate);

    /**
     * Decorate the given page.
     *
     * @param page non-null CQ page
     * @return decorated page
     */
    PageDecorator getPage(Page page);

    /**
     * Get the page for the given path, returning an absent <code>Optional</code> if the page does not exist.
     *
     * @param path page path
     * @return Optional page
     */
    Optional<PageDecorator> getPageOptional(String path);

    @Override
    PageDecorator copy(Page page, String destination, String beforeName, boolean shallow, boolean resolveConflict)
        throws WCMException;

    @Override
    PageDecorator copy(Page page, String destination, String beforeName, boolean shallow, boolean resolveConflict,
        boolean autoSave) throws WCMException;

    @Override
    PageDecorator create(String parentPath, String pageName, String template, String title) throws WCMException;

    @Override
    PageDecorator getContainingPage(Resource resource);

    @Override
    PageDecorator getContainingPage(String path);

    @Override
    PageDecorator getPage(String path);

    @Override
    PageDecorator move(Page page, String destination, String beforeName, boolean shallow, boolean resolveConflict,
        String[] adjustRefs) throws WCMException;

    @Override
    PageDecorator restore(String path, String revisionId) throws WCMException;

    @Override
    PageDecorator restoreTree(String path, Calendar date) throws WCMException;

    List<PageDecorator> search(Query query);

    List<PageDecorator> search(Query query, int limit);
}
