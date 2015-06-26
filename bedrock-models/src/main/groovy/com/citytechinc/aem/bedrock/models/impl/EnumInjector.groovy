package com.citytechinc.aem.bedrock.models.impl

import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Type

import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.osgi.framework.Constants

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.google.common.base.Optional

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class EnumInjector extends AbstractComponentNodeInjector{

	@Override
	public String getName() {
		return "enum"
	}

	@Override
	public Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
			DisposalCallbackRegistry callbackRegistry) {
		if(declaredType instanceof Class && declaredType.isEnum()){
			Optional<String> enumString=componentNode.get(name, String.class)
			if(enumString.present){
				return declaredType[enumString.get()]
			}
		}
		return null
	}
}
