/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.node;

import com.citytechinc.cq.library.content.link.Link;
import com.citytechinc.cq.library.content.link.Linkable;
import com.citytechinc.cq.library.content.page.PageDecorator;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import javax.jcr.Node;
import javax.jcr.Property;
import java.util.List;

/**
 * Represents a "basic" node in the JCR, typically an unstructured node that may or may not exist in a CQ page
 * hierarchy.  Examples of non-page descendant nodes that could be considered basic nodes include design nodes and
 * arbitrary unstructured nodes that do not require inheritance capabilities.
 * <p/>
 * Many methods return an <a href="https://code.google.com/p/guava-libraries/wiki/UsingAndAvoidingNullExplained#Optional">Optional</a>
 * type where a null instance would otherwise be returned (e.g. when a descendant node is requested for a path that does
 * not exist in the JCR).
 */
public interface BasicNode extends Linkable {

    /**
     * @return map of property names to values, or empty map if underlying resource is null or nonexistent
     */
    ValueMap asMap();

    /**
     * Get a property value from the current node, returning an "absent" <code>Optional</code> if the property does not
     * exist.
     *
     * @param propertyName property name
     * @param <T> property type
     * @return property value wrapped in an <code>Optional</code>
     */
    <T> Optional<T> get(String propertyName);

    /**
     * Get a property value from the current node, returning the default value if the property does not exist.
     *
     * @param <T>
     * @param propertyName property name
     * @param defaultValue
     * @return property value or default value if it does not exist
     */
    <T> T get(String propertyName, T defaultValue);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the href to the resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsHref(String propertyName);

    /**
     * Given a property on this component containing the path of another resource, get the href to the resource or a
     * default value if the property does not exist.
     *
     * @param propertyName name of property containing a valid content path
     * @param defaultValue value to return if property does not exist
     * @return href or default value
     */
    String getAsHref(String propertyName, String defaultValue);

    /**
     * Given a property on this resource containing the path of another resource, get a link to the resource.
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link object, absent if property does not contain a valid content path
     */
    Optional<Link> getAsLink(String propertyName);

    /**
     * Given a property on this resource containing the path of another resource, get an <code>Optional</code>
     * containing the mapped href to the resource.
     * <p/>
     * A "mapped" link has been routed through the Resource Resolver to determine the mapped path for the current
     * resource.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource Resolver Factory
     * OSGi configuration, getting the mapped link for the path "/content/citytechinc" will return "/citytechinc".
     *
     * @param propertyName name of property containing a valid content path
     * @return mapped href value wrapped in an <code>Optional</code>
     */
    Optional<String> getAsMappedHref(String propertyName);

    /**
     * Given a property on this component containing the path of another resource, get the mapped href to the resource
     * or a default value if the property does not exist.
     *
     * @param propertyName name of property containing a valid content path
     * @param defaultValue value to return if property does not exist
     * @return mapped href or default value
     */
    String getAsMappedHref(String propertyName, String defaultValue);

    /**
     * Given a property on this resource containing the path of another resource, get a mapped link to the resource.
     * <p/>
     * A "mapped" link has been routed through the Resource Resolver to determine the mapped path for the current
     * resource.  For example, if a mapping from "/content/" to "/" exists in the Apache Sling Resource Resolver Factory
     * OSGi configuration, getting the mapped link for the path "/content/citytechinc" will return "/citytechinc".
     *
     * @param propertyName name of property containing a valid content path
     * @return <code>Optional</code> link object, absent if the property does not contain a valid content path
     */
    Optional<Link> getAsMappedLink(String propertyName);

    /**
     * Get a page instance from the value of the given property.  Will return an absent <code>Optional</code> if the
     * path value for the given property name does not resolve to a valid CQ page.
     *
     * @param propertyName property name
     * @return <code>Optional</code> page for property value
     */
    Optional<PageDecorator> getAsPage(String propertyName);

    /**
     * Get the referenced DAM asset path for the default image (named "image") for this component.
     *
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference();

    /**
     * @param name image name
     * @return <code>Optional</code> image reference path
     */
    Optional<String> getImageReference(String name);

    /**
     * Get the DAM asset rendition path for the default image (named "image") for this component.
     *
     * @param renditionName rendition name for this asset (e.g. "cq5dam.thumbnail.140.100.png")
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String renditionName);

    /**
     * @param name image name
     * @param renditionName rendition name for this asset
     * @return <code>Optional</code> image rendition path
     */
    Optional<String> getImageRendition(String name, String renditionName);

    /**
     * Get the image source for the default image (named "image") for this node. Uses the image servlet rather than a
     * direct reference to the DAM path.
     *
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSrc();

    /**
     * Get the image source for the default image (named "image") for this node for the given width.
     *
     * @param width image width
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSrc(int width);

    /**
     * Get the image source for this node for the named image.
     *
     * @param name image name (name of image as defined in dialog)
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSrc(String name);

    /**
     * Get the image source for this node for the named image and given width.
     *
     * @param name image name (name of image as defined in dialog)
     * @param width image width
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSrc(String name, int width);

    /**
     * @return index in sibling nodes or -1 if resource is null or has null parent node
     */
    int getIndex();

    /**
     * Get the index of this node in sibling nodes, ignoring resource types that do not match the specified value.
     *
     * @param resourceType sling:resourceType to filter on
     * @return index in sibling nodes or -1 if resource is null or has null parent node
     */
    int getIndex(String resourceType);

    /**
     * Get the JCR node for this instance.  This will return an absent <code>Optional</code> if the underlying resource
     * for this instance is synthetic or non-existent.
     *
     * @return <code>Optional</code> node for this resource
     */
    Optional<Node> getNode();

    /**
     * Shortcut for getting current resource path.
     *
     * @return resource path
     */
    String getPath();

    /**
     * Get a list of properties that apply for the given predicate.
     *
     * @param predicate predicate to apply
     * @return filtered list of properties or empty list if no properties of this node apply for the given predicate
     */
    List<Property> getProperties(Predicate<Property> predicate);

    /**
     * Get the underlying resource for this instance.
     *
     * @return current resource
     */
    Resource getResource();

    /**
     * @return true if image has content
     */
    boolean isHasImage();

    /**
     * @param name image name (name of image as defined in dialog)
     * @return true if image has content
     */
    boolean isHasImage(String name);
}
