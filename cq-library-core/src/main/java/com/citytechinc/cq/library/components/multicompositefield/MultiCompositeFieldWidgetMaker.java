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

public class MultiCompositeFieldWidgetMaker extends AbstractWidgetMaker {

    private static final String FIELD_CONFIGS = "fieldConfigs";

    @Override
    public DialogElement make(String xtype, AccessibleObject widgetField, CtMember ctWidgetField,
        Class<?> containingClass, CtClass ctContainingClass, Map<Class<?>, WidgetConfigHolder> xtypeMap,
        Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool,
        boolean useDotSlashInName)
        throws ClassNotFoundException, InvalidComponentFieldException, CannotCompileException, NotFoundException,
        SecurityException, NoSuchFieldException {
        MultiCompositeField multiCompositeFieldAnnotation = (MultiCompositeField) ctWidgetField.getAnnotation(
            MultiCompositeField.class);
        DialogField dialogFieldAnnotation = (DialogField) ctWidgetField.getAnnotation(DialogField.class);

        boolean matchBaseName = multiCompositeFieldAnnotation.matchBaseName();
        String prefix = multiCompositeFieldAnnotation.prefix();

        String name = getNameForField(dialogFieldAnnotation, widgetField, useDotSlashInName);
        String fieldName = getFieldNameForField(dialogFieldAnnotation, widgetField);
        String fieldLabel = getFieldLabelForField(dialogFieldAnnotation, widgetField);
        String fieldDescription = getFieldDescriptionForField(dialogFieldAnnotation);
        Boolean isRequired = getIsRequiredForField(dialogFieldAnnotation);
        Map<String, String> additionalProperties = getAdditionalPropertiesForField(dialogFieldAnnotation);
        String defaultValue = getDefaultValueForField(dialogFieldAnnotation);
        boolean hideLabel = dialogFieldAnnotation.hideLabel();

        List<DialogElement> widgetCollection = buildWidgetCollection(ctContainingClass, ctWidgetField, widgetField,
            xtypeMap, xTypeToWidgetMakerMap, classLoader, classPool);

        return new MultiCompositeFieldWidget(matchBaseName, prefix, fieldLabel, fieldDescription, !isRequired,
            hideLabel, defaultValue, name, fieldName, additionalProperties, widgetCollection);
    }

    private List<DialogElement> buildWidgetCollection(CtClass componentClass, CtMember curField,
        AccessibleObject trueField, Map<Class<?>, WidgetConfigHolder> classToXTypeMap,
        Map<String, WidgetMaker> xTypeToWidgetMakerMap, ClassLoader classLoader, ClassPool classPool)
        throws InvalidComponentFieldException, ClassNotFoundException, CannotCompileException, NotFoundException,
        SecurityException, NoSuchFieldException {
        CtClass clazz = null;
        Class<?> fieldType;
        if (curField instanceof CtField) {
            CtField field = (CtField) curField;
            clazz = field.getType();
            fieldType = ((Field) trueField).getType();
        } else {
            CtMethod method = (CtMethod) curField;
            clazz = method.getReturnType();
            fieldType = ((Method) trueField).getReturnType();
        }
        if (List.class.isAssignableFrom(fieldType)) {
            ParameterizedType parameterizedType = null;
            if (trueField instanceof Field) {
                Field field = (Field) trueField;
                parameterizedType = (ParameterizedType) field.getGenericType();
            } else {
                Method method = (Method) trueField;
                parameterizedType = (ParameterizedType) method.getGenericReturnType();
            }
            clazz = classPool.get(((Class<?>) parameterizedType.getActualTypeArguments()[0]).getName());
        }
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        List<CtMember> fieldsAndMethods = new ArrayList<CtMember>();
        fieldsAndMethods.addAll(ComponentMojoUtil.collectFields(clazz));
        fieldsAndMethods.addAll(ComponentMojoUtil.collectMethods(clazz));
        List<DialogElement> elements = new ArrayList<DialogElement>();
        for (CtMember field : fieldsAndMethods) {
            if (field.hasAnnotation(DialogField.class)) {
                AccessibleObject mcTrueField = null;
                Class<?> fieldClass = classLoader.loadClass(field.getDeclaringClass().getName());
                if (field instanceof CtField) {
                    mcTrueField = ComponentMojoUtil.getField(fieldClass, field.getName());
                } else {
                    mcTrueField = ComponentMojoUtil.getMethod(fieldClass, field.getName());
                }
                DialogElement builtFieldWidget = WidgetFactory.make(componentClass, field, mcTrueField, classToXTypeMap,
                    xTypeToWidgetMakerMap, classLoader, classPool, false, -1);
                elements.add(builtFieldWidget);
            }
        }
        return Arrays.asList(new DialogElement[]{ new WidgetCollection(elements, FIELD_CONFIGS) });
    }
}
