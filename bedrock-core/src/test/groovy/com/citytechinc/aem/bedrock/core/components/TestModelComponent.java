package com.citytechinc.aem.bedrock.core.components;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class TestModelComponent {

    private boolean adaptedTo = false;

    public TestModelComponent(boolean adaptedTo) {
        this.adaptedTo = adaptedTo;
    }

    public boolean getAdaptedTo() {
        return adaptedTo;
    }

}
