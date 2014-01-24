/**
 * Copyright 2014, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.components.multicompositefield;

import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.dialog.DialogElement;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.factory.WidgetFactory;
import com.citytechinc.cq.component.dialog.maker.AbstractWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.WidgetMakerParameters;
import com.citytechinc.cq.component.dialog.widgetcollection.WidgetCollection;
import com.citytechinc.cq.component.dialog.widgetcollection.WidgetCollectionParameters;
import com.citytechinc.cq.component.maven.util.ComponentMojoUtil;
import javassist.CannotCompileException;
import javassist.CtMember;
import javassist.NotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MultiCompositeFieldWidgetMaker extends AbstractWidgetMaker {

    private static final String FIELD_CONFIGS = "fieldConfigs";

    public MultiCompositeFieldWidgetMaker(final WidgetMakerParameters parameters) {
        super(parameters);
    }

    @Override
    public DialogElement make()
        throws ClassNotFoundException, InvalidComponentFieldException, NotFoundException, CannotCompileException,
        NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        final MultiCompositeField multiCompositeFieldAnnotation = getAnnotation(MultiCompositeField.class);

        final MultiCompositeFieldWidgetParameters widgetParameters = new MultiCompositeFieldWidgetParameters();

        widgetParameters.setMatchBaseName(multiCompositeFieldAnnotation.matchBaseName());
        widgetParameters.setPrefix(multiCompositeFieldAnnotation.prefix());
        widgetParameters.setFieldName(getFieldNameForField());
        widgetParameters.setFieldLabel(getFieldLabelForField());
        widgetParameters.setFieldDescription(getFieldDescriptionForField());
        widgetParameters.setAdditionalProperties(getAdditionalPropertiesForField());
        widgetParameters.setHideLabel(getHideLabelForField());
        widgetParameters.setName(getNameForField());
        widgetParameters.setAllowBlank(!getIsRequiredForField());
        widgetParameters.setDefaultValue(getDefaultValueForField());
        widgetParameters.setListeners(getListeners());
        widgetParameters.setContainedElements(buildWidgetCollection());

        return new MultiCompositeFieldWidget(widgetParameters);
    }

    private List<DialogElement> buildWidgetCollection()
        throws InvalidComponentFieldException, NotFoundException, ClassNotFoundException, CannotCompileException,
        NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException,
        NoSuchMethodException {
        final List<CtMember> fieldsAndMethods = new ArrayList<CtMember>();

        fieldsAndMethods.addAll(ComponentMojoUtil.collectFields(getCtType()));
        fieldsAndMethods.addAll(ComponentMojoUtil.collectMethods(getCtType()));

        final List<DialogElement> elements = new ArrayList<DialogElement>();

        for (final CtMember curField : fieldsAndMethods) {
            if (curField.hasAnnotation(DialogField.class)) {
                final Class<?> fieldClass = parameters.getClassLoader().loadClass(
                    curField.getDeclaringClass().getName());

                final WidgetMakerParameters curFieldMember = new WidgetMakerParameters(
                    (DialogField) curField.getAnnotation(DialogField.class), curField, fieldClass,
                    parameters.getClassLoader(), parameters.getClassPool(), parameters.getWidgetRegistry(), null,
                    false);

                final DialogElement builtFieldWidget = WidgetFactory.make(curFieldMember, -1);

                elements.add(builtFieldWidget);
            }
        }

        final WidgetCollectionParameters wcp = new WidgetCollectionParameters();

        wcp.setContainedElements(elements);
        wcp.setFieldName(FIELD_CONFIGS);

        return Arrays.asList(new DialogElement[]{ new WidgetCollection(wcp) });
    }
}
