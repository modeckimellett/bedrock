package com.citytechinc.aem.bedrock.models.annotations;

import com.citytechinc.aem.bedrock.models.constants.ModelsConstants;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(value = ModelsConstants.INHERIT_NAME)
public @interface InheritInject {

	/**
	 * If set to true, the model can be instantiated even if there is no
	 * property available. Default = true.
	 */
	boolean optional() default true;
}
