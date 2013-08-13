/**
 * Copyright 2013, CITYTECH, Inc.
 * All rights reserved - Do Not Redistribute
 * Confidential and Proprietary
 */
package com.citytechinc.cq.library.components.multicompositefield;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtMember;
import javassist.NotFoundException;

import com.citytechinc.cq.component.annotations.DialogField;
import com.citytechinc.cq.component.dialog.DialogElement;
import com.citytechinc.cq.component.dialog.exception.InvalidComponentFieldException;
import com.citytechinc.cq.component.dialog.factory.WidgetFactory;
import com.citytechinc.cq.component.dialog.maker.AbstractWidgetMaker;
import com.citytechinc.cq.component.dialog.maker.WidgetMakerParameters;
import com.citytechinc.cq.component.dialog.widgetcollection.WidgetCollection;
import com.citytechinc.cq.component.dialog.widgetcollection.WidgetCollectionParameters;
import com.citytechinc.cq.component.maven.util.ComponentMojoUtil;

public final class MultiCompositeFieldWidgetMaker extends AbstractWidgetMaker {

	private static final String FIELD_CONFIGS = "fieldConfigs";

	public MultiCompositeFieldWidgetMaker(WidgetMakerParameters parameters) {
		super(parameters);
	}

	@Override
	public DialogElement make() throws ClassNotFoundException,
			SecurityException, InvalidComponentFieldException,
			NotFoundException, CannotCompileException, NoSuchFieldException,
			InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException {
		MultiCompositeField multiCompositeFieldAnnotation = getAnnotation(MultiCompositeField.class);
		
		MultiCompositeFieldWidgetParameters widgetParameters=new MultiCompositeFieldWidgetParameters();
		
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
		widgetParameters.setContainedElements(buildWidgetCollection(multiCompositeFieldAnnotation));

		return new MultiCompositeFieldWidget(widgetParameters);
	}

	private List<DialogElement> buildWidgetCollection(
			MultiCompositeField multiCompositeFieldAnnotation)
			throws InvalidComponentFieldException, NotFoundException,
			ClassNotFoundException, SecurityException, CannotCompileException,
			NoSuchFieldException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {

		List<CtMember> fieldsAndMethods = new ArrayList<CtMember>();

		fieldsAndMethods.addAll(ComponentMojoUtil.collectFields(getCtType()));
		fieldsAndMethods.addAll(ComponentMojoUtil.collectMethods(getCtType()));

		List<DialogElement> elements = new ArrayList<DialogElement>();

		for (CtMember curField : fieldsAndMethods) {
			if (curField.hasAnnotation(DialogField.class)) {

				Class<?> fieldClass = parameters.getClassLoader().loadClass(
						curField.getDeclaringClass().getName());

				WidgetMakerParameters curFieldMember = new WidgetMakerParameters(
						(DialogField) curField.getAnnotation(DialogField.class),
						curField, fieldClass, parameters.getClassLoader(),
						parameters.getClassPool(), parameters
								.getWidgetRegistry(), null, false);

				DialogElement builtFieldWidget = WidgetFactory.make(
						curFieldMember, -1);
				elements.add(builtFieldWidget);
			}
		}
		WidgetCollectionParameters wcp = new WidgetCollectionParameters();
		wcp.setContainedElements(elements);
		wcp.setFieldName(FIELD_CONFIGS);
		return Arrays.asList(new DialogElement[] { new WidgetCollection(wcp) });

	}
}
