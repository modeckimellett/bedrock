/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.page.impl;

import com.citytechinc.cq.library.content.link.ImageLink;
import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.NavigationLink;
import com.citytechinc.cq.library.content.link.builders.LinkBuilder;
import com.citytechinc.cq.library.content.node.BasicNode;
import com.citytechinc.cq.library.content.node.ComponentNode;
import com.citytechinc.cq.library.content.node.impl.DefaultComponentNode;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.citytechinc.cq.library.content.page.PageManagerDecorator;
import com.citytechinc.cq.library.content.page.enums.TitleType;
import com.day.cq.commons.Filter;
import com.day.cq.tagging.Tag;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.Template;
import com.day.cq.wcm.api.WCMException;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import static com.citytechinc.cq.library.content.node.impl.NodeFunctions.RESOURCE_TO_COMPONENT_NODE;
import static com.google.common.base.Preconditions.checkNotNull;

public final class DefaultPageDecorator implements PageDecorator {

    private static final Predicate<PageDecorator> ALL = Predicates.alwaysTrue();

    private static final Filter<Page> ALL_PAGES = new Filter<Page>() {
        @Override
        public boolean includes(final Page page) {
            return true;
        }
    };

    private static final Predicate<PageDecorator> DISPLAYABLE_ONLY = new Predicate<PageDecorator>() {
        @Override
        public boolean apply(final PageDecorator page) {
            return page.getContentResource() != null && !page.isHideInNav();
        }
    };

    private final Optional<ComponentNode> componentNodeOptional;

    private final Page page;

    public DefaultPageDecorator(final Page page) {
        this.page = page;

        componentNodeOptional = Optional.fromNullable(page.getContentResource()).transform(RESOURCE_TO_COMPONENT_NODE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <AdapterType> AdapterType adaptTo(final Class<AdapterType> type) {
        final AdapterType result;

        if (type == BasicNode.class || type == ComponentNode.class) {
            final Resource resource = page.getContentResource();

            result = resource == null ? null : (AdapterType) new DefaultComponentNode(resource);
        } else {
            result = page.adaptTo(type);
        }

        return result;
    }

    @Override
    public boolean canUnlock() {
        return page.canUnlock();
    }

    @Override
    public Optional<PageDecorator> findAncestor(final Predicate<PageDecorator> predicate) {
        PageDecorator page = this;
        PageDecorator ancestorPage = null;

        while (page != null) {
            if (predicate.apply(page)) {
                ancestorPage = page;
                break;
            } else {
                page = page.getParent();
            }
        }

        return Optional.fromNullable(ancestorPage);
    }

    @Override
    public PageDecorator getAbsoluteParent(final int level) {
        final Page absoluteParent = page.getAbsoluteParent(level);

        return absoluteParent == null ? null : new DefaultPageDecorator(absoluteParent);
    }

    @Override
    public List<PageDecorator> getChildren() {
        return filterChildren(ALL, false);
    }

    @Override
    public List<PageDecorator> getChildren(final boolean displayableOnly) {
        return displayableOnly ? filterChildren(DISPLAYABLE_ONLY, false) : filterChildren(ALL, false);
    }

    @Override
    public List<PageDecorator> getChildren(final Predicate<PageDecorator> predicate) {
        return filterChildren(checkNotNull(predicate), false);
    }

    @Override
    public List<PageDecorator> getChildren(final Predicate<PageDecorator> predicate, final boolean deep) {
        return filterChildren(checkNotNull(predicate), deep);
    }

    @Override
    public Optional<ComponentNode> getComponentNode() {
        return componentNodeOptional;
    }

    @Override
    public Optional<ComponentNode> getComponentNode(final String relativePath) {
        final Optional<ComponentNode> optional;

        if (componentNodeOptional.isPresent()) {
            optional = componentNodeOptional.get().getComponentNode(relativePath);
        } else {
            optional = Optional.absent();
        }

        return optional;
    }

    @Override
    public Resource getContentResource() {
        return page.getContentResource();
    }

    @Override
    public Resource getContentResource(final String relPath) {
        return page.getContentResource(relPath);
    }

    @Override
    public int getDepth() {
        return page.getDepth();
    }

    @Override
    public String getDescription() {
        return page.getDescription();
    }

    @Override
    public String getHref() {
        return getLink().getHref();
    }

    @Override
    public ImageLink getImageLink(final String imageSource) {
        return LinkBuilder.forPage(this).setImageSource(checkNotNull(imageSource)).buildImageLink();
    }

    @Override
    public Optional<String> getImageSource() {
        return getImageSource(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource();
            }
        });
    }

    @Override
    public Optional<String> getImageSource(final int width) {
        return getImageSource(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(width);
            }
        });
    }

    @Override
    public Optional<String> getImageSource(final String name) {
        return getImageSource(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name);
            }
        });
    }

    @Override
    public Optional<String> getImageSource(final String name, final int width) {
        return getImageSource(new Function<ComponentNode, Optional<String>>() {
            @Override
            public Optional<String> apply(final ComponentNode componentNode) {
                return componentNode.getImageSource(name, width);
            }
        });
    }

    @Override
    public Locale getLanguage(final boolean ignoreContent) {
        return page.getLanguage(ignoreContent);
    }

    @Override
    public Calendar getLastModified() {
        return page.getLastModified();
    }

    @Override
    public String getLastModifiedBy() {
        return page.getLastModifiedBy();
    }

    @Override
    public Link getLink() {
        return getLinkBuilder().build();
    }

    @Override
    public LinkBuilder getLinkBuilder() {
        return LinkBuilder.forPage(this);
    }

    @Override
    public String getLockOwner() {
        return page.getLockOwner();
    }

    @Override
    public String getMappedHref() {
        return getMappedLink().getHref();
    }

    @Override
    public Link getMappedLink() {
        return getMappedLinkBuilder().build();
    }

    @Override
    public LinkBuilder getMappedLinkBuilder() {
        return LinkBuilder.forPage(this, true);
    }

    @Override
    public String getName() {
        return page.getName();
    }

    @Override
    public NavigationLink getNavigationLink() {
        return LinkBuilder.forPage(this, false, TitleType.NAVIGATION_TITLE).buildNavigationLink();
    }

    @Override
    public String getNavigationTitle() {
        return page.getNavigationTitle();
    }

    @Override
    public Optional<String> getNavigationTitleOptional() {
        return Optional.fromNullable(page.getNavigationTitle());
    }

    @Override
    public Calendar getOffTime() {
        return page.getOffTime();
    }

    @Override
    public Calendar getOnTime() {
        return page.getOnTime();
    }

    @Override
    public PageManagerDecorator getPageManager() {
        return getPageManagerDecorator();
    }

    @Override
    public String getPageTitle() {
        return page.getPageTitle();
    }

    @Override
    public Optional<String> getPageTitleOptional() {
        return Optional.fromNullable(page.getPageTitle());
    }

    @Override
    public PageDecorator getParent() {
        final Page parent = page.getParent();

        return parent == null ? null : new DefaultPageDecorator(parent);
    }

    @Override
    public PageDecorator getParent(final int level) {
        final Page parent = page.getParent(level);

        return parent == null ? null : new DefaultPageDecorator(parent);
    }

    @Override
    public String getPath() {
        return page.getPath();
    }

    @Override
    public ValueMap getProperties() {
        return page.getProperties();
    }

    @Override
    public ValueMap getProperties(final String relativePath) {
        return page.getProperties(relativePath);
    }

    @Override
    public Tag[] getTags() {
        return page.getTags();
    }

    @Override
    public Template getTemplate() {
        return page.getTemplate();
    }

    @Override
    public String getTemplatePath() {
        return getProperties().get(NameConstants.NN_TEMPLATE, "");
    }

    @Override
    public String getTitle() {
        return getProperties().get(NameConstants.PN_TITLE, "");
    }

    @Override
    public String getVanityUrl() {
        return page.getVanityUrl();
    }

    @Override
    public boolean hasChild(final String name) {
        return page.hasChild(name);
    }

    @Override
    public boolean hasContent() {
        return page.hasContent();
    }

    @Override
    public boolean isHideInNav() {
        return page.isHideInNav();
    }

    @Override
    public boolean isLocked() {
        return page.isLocked();
    }

    @Override
    public boolean isValid() {
        return page.isValid();
    }

    @Override
    public Iterator<Page> listChildren() {
        return page.listChildren();
    }

    @Override
    public Iterator<Page> listChildren(final Filter<Page> filter) {
        return page.listChildren(filter);
    }

    @Override
    public Iterator<Page> listChildren(final Filter<Page> filter, final boolean deep) {
        return page.listChildren(filter, deep);
    }

    @Override
    public void lock() throws WCMException {
        page.lock();
    }

    @Override
    public long timeUntilValid() {
        return page.timeUntilValid();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("path", getPath()).append("title", getTitle()).toString();
    }

    @Override
    public void unlock() throws WCMException {
        page.unlock();
    }

    private List<PageDecorator> filterChildren(final Predicate<PageDecorator> predicate, final boolean deep) {
        final List<PageDecorator> pages = new ArrayList<PageDecorator>();

        final Iterator<Page> children = page.listChildren(ALL_PAGES, deep);

        final PageManagerDecorator pageManager = getPageManagerDecorator();

        while (children.hasNext()) {
            final PageDecorator child = pageManager.getPage(children.next());

            if (child != null && predicate.apply(child)) {
                pages.add(child);
            }
        }

        return pages;
    }

    private Optional<String> getImageSource(final Function<ComponentNode, Optional<String>> function) {
        return componentNodeOptional.transform(function).or(Optional.<String>absent());
    }

    private PageManagerDecorator getPageManagerDecorator() {
        return page.adaptTo(Resource.class).getResourceResolver().adaptTo(PageManagerDecorator.class);
    }
}
