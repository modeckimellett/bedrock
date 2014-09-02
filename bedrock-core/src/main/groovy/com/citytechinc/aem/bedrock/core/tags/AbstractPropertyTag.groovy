package com.citytechinc.aem.bedrock.core.tags

/**
 * Base class for component tag handlers that access a JCR property.
 */
abstract class AbstractPropertyTag extends AbstractComponentTag {

    String propertyName

    protected final boolean hasPropertyName() {
        propertyName as Boolean
    }
}
