package com.citytechinc.aem.bedrock.core.components

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class TestModelResourceComponent {

    private boolean adaptedTo = false

    public TestModelResourceComponent(boolean adaptedTo) {
        this.adaptedTo = adaptedTo
    }

    public boolean getAdaptedTo() {
        return adaptedTo
    }

}

