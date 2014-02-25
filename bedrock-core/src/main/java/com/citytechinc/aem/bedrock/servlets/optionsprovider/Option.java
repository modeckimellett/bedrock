/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.aem.bedrock.servlets.optionsprovider;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Text/value pair for displaying in a selection dialog widget, used in conjunction with the
 * <code>AbstractOptionsProviderServlet</code>.
 */
public final class Option {

    public static final Comparator<Option> ALPHA = new Comparator<Option>() {
        @Override
        public int compare(final Option option1, final Option option2) {
            return option1.getText().compareTo(option2.getText());
        }
    };

    public static final Comparator<Option> ALPHA_IGNORE_CASE = new Comparator<Option>() {
        @Override
        public int compare(final Option option1, final Option option2) {
            return option1.getText().compareToIgnoreCase(option2.getText());
        }
    };

    private final String text;

    private final String value;

    public Option(final String value, final String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * @param map map where key=[option value] and value=[option text]
     * @return list of options created from map
     */
    public static List<Option> fromMap(final Map<String, String> map) {
        final List<Option> options = Lists.newArrayList();

        for (final Entry<String, String> entry : map.entrySet()) {
            final String value = entry.getKey();
            final String text = entry.getValue();

            options.add(new Option(value, text));
        }

        return options;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
