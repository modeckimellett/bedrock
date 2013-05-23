/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.impl;

import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.page.predicates.TemplatePredicate;
import com.citytechinc.cq.library.utils.PathUtils;
import com.day.cq.commons.RangeIterator;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.Revision;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.api.msm.Blueprint;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@SuppressWarnings("deprecation")
public final class DefaultPageManagerDecorator implements PageManagerDecorator {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPageManagerDecorator.class);

    private final ResourceResolver resourceResolver;

    private final PageManager pageManager;

    public DefaultPageManagerDecorator(final ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;

        pageManager = resourceResolver.adaptTo(PageManager.class);
    }

    @Override
    public List<PageDecorator> findPages(final String path, final Collection<String> tagIds, final boolean matchOne) {
        checkNotNull(path);
        checkNotNull(tagIds);

        LOG.debug("findPages() path = {}, tag IDs = {}", path, tagIds);

        final Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();

        final RangeIterator<Resource> iterator = resourceResolver.adaptTo(TagManager.class).find(path, tagIds.toArray(
            new String[tagIds.size()]), matchOne);

        final List<PageDecorator> pages = new ArrayList<PageDecorator>();

        if (iterator != null) {
            while (iterator.hasNext()) {
                final Resource resource = iterator.next();

                if (JcrConstants.JCR_CONTENT.equals(resource.getName())) {
                    final PageDecorator page = getPage(resource.getParent().getPath());

                    if (page != null) {
                        pages.add(page);
                    }
                }
            }
        }

        stopwatch.stop();

        LOG.debug("findPages() found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));

        return pages;
    }

    @Override
    public List<PageDecorator> search(final Query query) {
        return search(query, -1);
    }

    @Override
    public List<PageDecorator> search(final Query query, final int limit) {
        checkNotNull(query);

        LOG.debug("search() query statement = {}", query.getStatement());

        final Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();

        final List<PageDecorator> pages = new ArrayList<PageDecorator>();

        int count = 0;

        try {
            final QueryResult result = query.execute();
            final RowIterator rows = result.getRows();

            final Set<String> paths = new HashSet<String>();

            while (rows.hasNext()) {
                if (count > -1 && count == limit) {
                    break;
                }

                final Row row = rows.nextRow();
                final String path = row.getPath();

                LOG.debug("search() result path = {}", path);

                final String pagePath = PathUtils.getPagePath(path);

                // ensure no duplicate pages are added
                if (!paths.contains(pagePath)) {
                    paths.add(pagePath);

                    final PageDecorator page = createPage(path);

                    if (page == null) {
                        LOG.error("search() result is null for path = {}", path);
                    } else {
                        pages.add(page);

                        count++;
                    }
                }
            }

            stopwatch.stop();

            LOG.debug("search() found {} result(s) in {}ms", pages.size(), stopwatch.elapsed(MILLISECONDS));
        } catch (RepositoryException re) {
            LOG.error("error finding pages for query = " + query.getStatement(), re);
        }

        return pages;
    }

    @Override
    public List<PageDecorator> findPages(final String path, final String template) {
        return findPages(path, new TemplatePredicate(template));
    }

    @Override
    public List<PageDecorator> findPages(final String path, final Predicate<PageDecorator> predicate) {
        checkNotNull(path);

        final PageDecorator page = getPage(path);

        final Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();

        final List<PageDecorator> result;

        if (page == null) {
            result = Collections.emptyList();
        } else {
            result = findRecursive(page, predicate);
        }

        stopwatch.stop();

        LOG.debug("findPages() found {} result(s) in {}ms", result.size(), stopwatch.elapsed(MILLISECONDS));

        return result;
    }

    private List<PageDecorator> findRecursive(final PageDecorator page, final Predicate<PageDecorator> predicate) {
        final List<PageDecorator> result = new ArrayList<PageDecorator>();

        for (final PageDecorator child : page.getChildren()) {
            if (predicate.apply(child)) {
                result.add(child);
            }

            result.addAll(findRecursive(child, predicate));
        }

        return result;
    }

    @Override
    public PageDecorator copy(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict) throws WCMException {

        return page == null ? null : new DefaultPageDecorator(pageManager.copy(page, destination, beforeName, shallow,
            resolveConflict));
    }

    @Override
    public PageDecorator copy(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict, final boolean autoSave) throws WCMException {

        return page == null ? null : new DefaultPageDecorator(pageManager.copy(page, destination, beforeName, shallow,
            resolveConflict, autoSave));
    }

    @Override
    public PageDecorator create(final String parentPath, final String pageName, final String template,
        final String title) throws WCMException {
        return new DefaultPageDecorator(pageManager.create(parentPath, pageName, template, title));
    }

    @Override
    public PageDecorator getContainingPage(final Resource resource) {
        final Page page = pageManager.getContainingPage(resource);

        return page == null ? null : new DefaultPageDecorator(page);
    }

    @Override
    public PageDecorator getContainingPage(final String path) {
        final Page page = pageManager.getContainingPage(path);

        return page == null ? null : new DefaultPageDecorator(page);
    }

    @Override
    public PageDecorator getPage(final Page page) {
        return page == null ? null : createPage(page.getPath());
    }

    @Override
    public Optional<PageDecorator> getPageOptional(final String path) {
        checkNotNull(path);

        return Optional.fromNullable(getPage(path));
    }

    @Override
    public PageDecorator getPage(final String path) {
        checkNotNull(path);

        return createPage(path);
    }

    @Override
    public PageDecorator move(final Page page, final String destination, final String beforeName, final boolean shallow,
        final boolean resolveConflict, final String[] adjustRefs) throws WCMException {
        return page == null ? null : new DefaultPageDecorator(pageManager.move(page, destination, beforeName, shallow,
            resolveConflict, adjustRefs));
    }

    private PageDecorator createPage(final String path) {
        final Page page = pageManager.getPage(path);

        return page == null ? null : new DefaultPageDecorator(page);
    }

    @Override
    public PageDecorator restore(final String path, final String revisionId) throws WCMException {
        final Page restoredPage = pageManager.restore(path, revisionId);

        return restoredPage == null ? null : new DefaultPageDecorator(restoredPage);
    }

    @Override
    public PageDecorator restoreTree(final String path, final Calendar date) throws WCMException {
        final Page restoredPage = pageManager.restoreTree(path, date);

        return restoredPage == null ? null : new DefaultPageDecorator(restoredPage);
    }

    @Override
    public Resource copy(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict) throws WCMException {
        return pageManager.copy(resource, destination, beforeName, shallow, resolveConflict);
    }

    @Override
    public Resource copy(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict, final boolean autoSave) throws WCMException {
        return pageManager.copy(resource, destination, beforeName, shallow, resolveConflict, autoSave);
    }

    @Override
    public Revision createRevision(final Page page) throws WCMException {
        return pageManager.createRevision(page);
    }

    @Override
    public Revision createRevision(final Page page, final String label, final String comment) throws WCMException {
        return pageManager.createRevision(page, label, comment);
    }

    @Override
    public void delete(final Page page, final boolean shallow) throws WCMException {
        pageManager.delete(page, shallow);
    }

    @Override
    public void delete(final Resource resource, final boolean shallow) throws WCMException {
        pageManager.delete(resource, shallow);
    }

    @Override
    public Collection<Blueprint> getBlueprints(final String parentPath) {
        return pageManager.getBlueprints(parentPath);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final Calendar arg1) throws WCMException {
        return pageManager.getChildRevisions(parentPath, arg1);
    }

    @Override
    public Collection<Revision> getRevisions(final String parentPath, final Calendar cal) throws WCMException {
        return pageManager.getRevisions(parentPath, cal);
    }

    @Override
    public Template getTemplate(final String templatePath) {
        return pageManager.getTemplate(templatePath);
    }

    @Override
    public Collection<Template> getTemplates(final String parentPath) {
        return pageManager.getTemplates(parentPath);
    }

    @Override
    public Resource move(final Resource resource, final String destination, final String beforeName,
        final boolean shallow, final boolean resolveConflict, final String[] adjustRefs) throws WCMException {
        return pageManager.move(resource, destination, beforeName, shallow, resolveConflict, adjustRefs);
    }

    @Override
    public void order(final Page page, final String beforeName) throws WCMException {
        pageManager.order(page, beforeName);
    }

    @Override
    public void order(final Resource resource, final String beforeName) throws WCMException {
        pageManager.order(resource, beforeName);
    }

    @Override
    public void touch(final Node page, final boolean shallow, final Calendar now, final boolean clearRepl)
        throws WCMException {
        pageManager.touch(page, shallow, now, clearRepl);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final Calendar cal,
        final boolean includeNoLocal) throws WCMException {
        return pageManager.getChildRevisions(parentPath, cal, includeNoLocal);
    }

    @Override
    public Collection<Revision> getChildRevisions(final String parentPath, final String treeRoot, final Calendar cal)
        throws WCMException {
        return pageManager.getChildRevisions(parentPath, treeRoot, cal);
    }

    @Override
    public Collection<Revision> getRevisions(final String path, final Calendar cal, final boolean includeNoLocal)
        throws WCMException {
        return pageManager.getRevisions(path, cal, includeNoLocal);
    }

    @Override
    public PageDecorator restoreTree(final String path, final Calendar date, final boolean preserveNV)
        throws WCMException {
        return new DefaultPageDecorator(pageManager.restoreTree(path, date, preserveNV));
    }
}
