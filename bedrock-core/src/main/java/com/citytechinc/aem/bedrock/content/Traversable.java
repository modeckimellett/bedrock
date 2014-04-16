/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.content;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.List;

/**
 * Definition for hierarchical JCR resources that can be traversed.
 *
 * @param <T> type of traversable resource
 */
public interface Traversable<T> {

    /**
     * Find the first ancestor resource that matches the given predicate condition.
     *
     * @param predicate predicate to match ancestor resources against
     * @return <code>Optional</code> resource that matches the predicate condition
     */
    Optional<T> findAncestor(Predicate<T> predicate);

    /**
     * Get a list of descendant resources that match the given predicate condition.
     *
     * @param predicate predicate to match descendant resources against
     * @return list of resources that match the predicate condition or empty list if none exist
     */
    List<T> findDescendants(Predicate<T> predicate);

    /**
     * Get the parent of this resource.
     *
     * @return parent resource or absent <code>Optional</code> if none exists
     */
    // Optional<T> getParent();
}
