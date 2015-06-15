package com.citytechinc.aem.bedrock.models.impl;

import com.citytechinc.aem.bedrock.api.node.ComponentNode;
import com.citytechinc.aem.bedrock.models.annotations.ImageInject;
import com.day.cq.wcm.foundation.Image;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.spi.AcceptsNullName;
import org.apache.sling.models.spi.DisposalCallbackRegistry;
import org.apache.sling.models.spi.Injector;
import org.apache.sling.models.spi.injectorspecific.AbstractInjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessor;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotationProcessorFactory;
import org.osgi.framework.Constants;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

@Component
@Service
@Property(name = Constants.SERVICE_RANKING, intValue = 4000)
public class ImageInjector extends AbstractTypedComponentNodeInjector<Image> implements Injector, InjectAnnotationProcessorFactory, AcceptsNullName {

    public static final String NAME = "images";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Object getValue(ComponentNode componentNode, String name, Type declaredType, AnnotatedElement element,
        DisposalCallbackRegistry callbackRegistry) {
        ImageInject imageAnnotation = element.getAnnotation(ImageInject.class);
        final String path = imageAnnotation == null ? null : imageAnnotation.path();

        Image image;
        Resource resource = null;
        if (imageAnnotation != null && imageAnnotation.inherit()) {
            Predicate<ComponentNode> predicate = new Predicate<ComponentNode>() {
                @Override
                public boolean apply(ComponentNode componentNode) {
                    if (StringUtils.isNotEmpty(path)) {
                        return componentNode.isHasImage(path);
                    } else {
                        return componentNode.isHasImage();
                    }
                }
            };

            Optional<ComponentNode> componentNodeInherit = componentNode.findAncestor(predicate);
            if (componentNodeInherit.isPresent()) {
                resource = componentNodeInherit.get().getResource();
            }

        } else {
            resource = componentNode.getResource();
        }
        if (resource != null) {
            if (StringUtils.isNotEmpty(path)) {
                image = new Image(resource, path);
            } else {
                image = new Image(resource);
            }
            if (image.hasContent()) {
                return image;
            }
        }

        return null;
    }

    @Override
    public InjectAnnotationProcessor createAnnotationProcessor(Object adaptable, AnnotatedElement element) {
        // check if the element has the expected annotation
        ImageInject annotation = element.getAnnotation(ImageInject.class);
        if (annotation != null) {
            return new ImageAnnotationProcessor(annotation, adaptable);
        }
        return null;
    }

    private static class ImageAnnotationProcessor extends AbstractInjectAnnotationProcessor {

        private final ImageInject annotation;

        public ImageAnnotationProcessor(ImageInject annotation, Object adaptable) {
            this.annotation = annotation;
        }

        @Override
        public Boolean isOptional() {
            return annotation.optional();
        }

    }
}
