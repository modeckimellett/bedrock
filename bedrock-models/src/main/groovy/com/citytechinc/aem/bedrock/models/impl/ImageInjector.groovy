package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.api.node.ComponentNode
import com.citytechinc.aem.bedrock.models.annotations.ImageInject
import com.citytechinc.aem.bedrock.models.constants.ModelsConstants
import com.day.cq.wcm.foundation.Image
import com.google.common.base.Predicate
import groovy.transform.TupleConstructor
import org.apache.felix.scr.annotations.Component
import org.apache.felix.scr.annotations.Property
import org.apache.felix.scr.annotations.Service
import org.apache.sling.models.spi.AcceptsNullName
import org.apache.sling.models.spi.DisposalCallbackRegistry
import org.apache.sling.models.spi.Injector
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory
import org.osgi.framework.Constants

import java.lang.reflect.AnnotatedElement

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
class ImageInjector extends AbstractTypedComponentNodeInjector<Image> implements Injector,
    InjectAnnotationProcessorFactory, AcceptsNullName {

    @Override
    String getName() {
        ModelsConstants.IMAGE_NAME
    }

    @Override
    Object getValue(ComponentNode componentNode, String name, Class<Image> declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        def imageAnnotation = element.getAnnotation(ImageInject)
        def path = imageAnnotation?.path()

        def resource = null

        if (imageAnnotation && imageAnnotation.inherit()) {
            def predicate = new Predicate<ComponentNode>() {
                @Override
                boolean apply(ComponentNode node) {
                    path ? node.isHasImage(path) : node.hasImage
                }
            }

            def componentNodeInherit = componentNode.findAncestor(predicate)

            if (componentNodeInherit.isPresent()) {
                resource = componentNodeInherit.get().resource
            }
        } else {
            resource = componentNode.resource
        }

        def image = null

        if (resource) {
            image = path ? new Image(resource, path) : new Image(resource)
        }

        (image && image.hasContent()) ? image : null
    }

    @Override
    InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        def annotation = element.getAnnotation(ImageInject)

        annotation ? new ImageAnnotationProcessor(annotation) : null
    }

    @TupleConstructor
    private static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor {

        ImageInject annotation

        @Override
        Boolean isOptional() {
            annotation.optional()
        }
    }
}
