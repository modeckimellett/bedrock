/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.content.link;

import com.google.common.base.Optional;

/**
 * Definition for items with associated images, such as pages and components.
 */
public interface ImageSource {

    /**
     * Get the image source for the default image (named "image") for this node. Uses the image servlet rather than a
     * direct reference to the DAM path.
     *
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSource();

    /**
     * Get the image source for the default image (named "image") for this node for the given width.
     *
     * @param width image width
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSource(int width);

    /**
     * Get the image source for this node for the named image.
     *
     * @param name image name (name of image as defined in dialog)
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSource(String name);

    /**
     * Get the image source for this node for the named image and given width.
     *
     * @param name image name (name of image as defined in dialog)
     * @param width image width
     * @return optional image source (absent if the referenced image has no content)
     */
    Optional<String> getImageSource(String name, int width);
}
