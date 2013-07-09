/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.components.multicompositefield;

import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.dialog.DialogElement;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.factory.WidgetFactory;
import com.citytechinc.cq.component.dialog.impl.WidgetCollection;
import com.citytechinc.cq.component.dialog.maker.AbstractWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.WidgetMaker;
import com.citytechinc.cq.component.maven.util.ComponentMojoUtil;
import com.citytechinc.cq.component.maven.util.WidgetConfigHolder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMember;
import javassist.CtMethod;
import javassist.NotFoundException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class MultiCompositeFieldWidgetMaker extends AbstractWidgetMaker {

    private static final String FIELD_CONFIGS = "fieldConfigs";

    @Override
    public DialogElement make(final String xtype, final AccessibleObject widgetField, final CtMember ctWidgetField,
        final Class<?> containingClass, final CtClass ctContainingClass, final Map<Class<?>, WidgetConfigHolder> xtypeMap,
        final Map<String, WidgetMaker> xTypeToWidgetMakerMap, final ClassLoader classLoader, final ClassPool classPool,
        final boolean useDotSlashInName)
        throws ClassNotFoundException, InvalidComponentFieldException, CannotCompileException, NotFoundException,
        SecurityException, NoSuchFieldException {
        final MultiCompositeField multiCompositeFieldAnnotation = (MultiCompositeField) ctWidgetField.getAnnotation(
            MultiCompositeField.class);
        final DialogField dialogFieldAnnotation = (DialogField) ctWidgetField.getAnnotation(DialogField.class);

        final boolean matchBaseName = multiCompositeFieldAnnotation.matchBaseName();
        final String prefix = multiCompositeFieldAnnotation.prefix();

        final String name = getNameForField(dialogFieldAnnotation, widgetField, useDotSlashInName);
        final String fieldName = getFieldNameForField(dialogFieldAnnotation, widgetField);
        final String fieldLabel = getFieldLabelForField(dialogFieldAnnotation, widgetField);
        final String fieldDescription = getFieldDescriptionForField(dialogFieldAnnotation);
        final Boolean isRequired = getIsRequiredForField(dialogFieldAnnotation);
        final Map<String, String> additionalProperties = getAdditionalPropertiesForField(dialogFieldAnnotation);
        final String defaultValue = getDefaultValueForField(dialogFieldAnnotation);
        final boolean hideLabel = dialogFieldAnnotation.hideLabel();

        final List<DialogElement> widgetCollection = buildWidgetCollection(ctContainingClass, ctWidgetField, widgetField,
            xtypeMap, xTypeToWidgetMakerMap, classLoader, classPool);

        return new MultiCompositeFieldWidget(matchBaseName, prefix, fieldLabel, fieldDescription, !isRequired,
            hideLabel, defaultValue, name, fieldName, additionalProperties, widgetCollection);
    }

    private List<DialogElement> buildWidgetCollection(final CtClass componentClass, final CtMember curField,
        final AccessibleObject trueField, final Map<Class<?>, WidgetConfigHolder> classToXTypeMap,
        final Map<String, WidgetMaker> xTypeToWidgetMakerMap, final ClassLoader classLoader, final ClassPool classPool)
        throws InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException,
        SecurityException, NoSuchFieldException {
        CtClass clazz;

        final Class<?> fieldType;

        if (curField instanceof CtField) {
            final CtField field = (CtField) curField;
            clazz = field.getType();
            fieldType = ((Field) trueField).getType();
        } else {
            final CtMethod method = (CtMethod) curField;
            clazz = method.getReturnType();
            fieldType = ((Method) trueField).getReturnType();
        }

        if (List.class.isAssignableFrom(fieldType)) {
            ParameterizedType parameterizedType;

            if (trueField instanceof Field) {
                final Field field = (Field) trueField;
                parameterizedType = (ParameterizedType) field.getGenericType();
            } else {
                final Method method = (Method) trueField;
                parameterizedType = (ParameterizedType) method.getGenericReturnType();
            }

            clazz = classPool.get(((Class<?>) parameterizedType.getActualTypeArguments()[0]).getName());
        }

        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }

        final List<CtMember> fieldsAndMethods = new ArrayList<CtMember>();

        fieldsAndMethods.addAll(ComponentMojoUtil.collectFields(clazz));
        fieldsAndMethods.addAll(ComponentMojoUtil.collectMethods(clazz));

        final List<DialogElement> elements = new ArrayList<DialogElement>();

        for (final CtMember field : fieldsAndMethods) {
            if (field.hasAnnotation(DialogField.class)) {
                AccessibleObject mcTrueField;

                final Class<?> fieldClass = classLoader.loadClass(field.getDeclaringClass().getName());

                if (field instanceof CtField) {
                    mcTrueField = ComponentMojoUtil.getField(fieldClass, field.getName());
                } else {
                    mcTrueField = ComponentMojoUtil.getMethod(fieldClass, field.getName());
                }

                final DialogElement builtFieldWidget = WidgetFactory.make(componentClass, field, mcTrueField, classToXTypeMap,
                    xTypeToWidgetMakerMap, classLoader, classPool, false, -1);

                elements.add(builtFieldWidget);
            }
        }

        return Arrays.asList(new DialogElement[]{ new WidgetCollection(elements, FIELD_CONFIGS) });
    }
}
