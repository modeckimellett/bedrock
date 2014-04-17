/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.services;

import com.google.common.collect.ImmutableList;
import org.apache.sling.commons.osgi.PropertiesUtil;

import java.util.List;
import java.util.Map;

/**
 * OSGi configuration wrapper.
 */
public final class OsgiConfiguration {

    private final Map<String, Object> properties;

    public OsgiConfiguration(final Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Returns the property as a map with string keys and string values.
     * <p/>
     * The property is considered as a collection whose entries are of the form key=value.
     *
     * @param propertyName configuration property name
     * @return property map
     */
    public Map<String, String> toMap(final String propertyName) {
        return PropertiesUtil.toMap(properties.get(propertyName), new String[0]);
    }

    /**
     * Returns the property as a map with string keys and string values.
     * <p/>
     * The property is considered as a collection whose entries are of the form key=value.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to convert to map return if property value is null
     * @return property map
     */
    public Map<String, String> toMap(final String propertyName, final List<String> defaultValue) {
        return PropertiesUtil.toMap(properties.get(propertyName), defaultValue.toArray(
            new String[defaultValue.size()]));
    }

    /**
     * Returns the property as a double or the <code>defaultValue</code> if the property is <code>null</code> or if the
     * property is not a <code>Double</code> and cannot be converted to a <code>Double</code> from the property's string
     * value.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property value is null
     */
    public double getAsDouble(final String propertyName, final double defaultValue) {
        return PropertiesUtil.toDouble(properties.get(propertyName), defaultValue);
    }

    /**
     * Returns the property as a long or the <code>defaultValue</code> if the property is <code>null</code> or if the
     * property is not a <code>Long</code> and cannot be converted to a <code>Long</code> from the property's string
     * value.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property value is null
     */
    public long getAsLong(final String propertyName, final long defaultValue) {
        return PropertiesUtil.toLong(properties.get(propertyName), defaultValue);
    }

    /**
     * Returns the boolean value of the property or the <code>defaultValue</code> if the property is <code>null</code>.
     * If the property is not a <code>Boolean</code> it is converted by calling <code>Boolean.valueOf</code> on the
     * string value of the object.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property value is null
     */
    public boolean getAsBoolean(final String propertyName, final boolean defaultValue) {
        return PropertiesUtil.toBoolean(properties.get(propertyName), defaultValue);
    }

    /**
     * Returns the property as an integer or the <code>defaultValue</code> if the property is <code>null</code> or if
     * the property is not an <code>Integer</code> and cannot be converted to an <code>Integer</code> from the
     * property's string value.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property value is null
     */
    public int getAsInteger(final String propertyName, final int defaultValue) {
        return PropertiesUtil.toInteger(properties.get(propertyName), defaultValue);
    }

    /**
     * Returns the property as a string or the <code>defaultValue</code> if the property is <code>null</code>.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property value is null
     */
    public String getAsString(final String propertyName, final String defaultValue) {
        return PropertiesUtil.toString(properties.get(propertyName), defaultValue);
    }

    /**
     * Returns the property as an array of Strings. If the property is a scalar value its string value is returned as a
     * single element array. If the property is an array, the elements are converted to String objects and returned as
     * an array. If the property is a collection, the collection elements are converted to String objects and returned
     * as an array. Otherwise (if the property is <code>null</code>) <code>null</code> is returned.
     *
     * @param propertyName configuration property name
     */
    public List<String> getAsList(final String propertyName) {
        return ImmutableList.copyOf(PropertiesUtil.toStringArray(properties.get(propertyName), new String[0]));
    }

    /**
     * Returns the property as an array of Strings. If the property is a scalar value its string value is returned as a
     * single element array. If the property is an array, the elements are converted to String objects and returned as
     * an array. If the property is a collection, the collection elements are converted to String objects and returned
     * as an array. Otherwise (if the property is <code>null</code>) the default value is returned.
     *
     * @param propertyName configuration property name
     * @param defaultValue default value to return if property is null
     */
    public List<String> getAsList(final String propertyName, final List<String> defaultValue) {
        return ImmutableList.copyOf(PropertiesUtil.toStringArray(properties.get(propertyName), defaultValue.toArray(
            new String[defaultValue.size()])));
    }
}
