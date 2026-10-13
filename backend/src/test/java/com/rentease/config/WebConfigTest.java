package com.rentease.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import static org.junit.jupiter.api.Assertions.*;

class WebConfigTest {

    @Test
    void registersUploadResourceHandler() {
        ExposedResourceHandlerRegistry registry = new ExposedResourceHandlerRegistry();

        new WebConfig().addResourceHandlers(registry);

        assertDoesNotThrow(() -> new WebConfig().addResourceHandlers(registry));

        AbstractHandlerMapping handlerMapping = registry.exposedHandlerMapping();
        assertNotNull(handlerMapping);
        assertInstanceOf(SimpleUrlHandlerMapping.class, handlerMapping);
    }

    private static class ExposedResourceHandlerRegistry extends ResourceHandlerRegistry {
        ExposedResourceHandlerRegistry() {
            super(new StaticWebApplicationContext(), null);
        }

        AbstractHandlerMapping exposedHandlerMapping() {
            return getHandlerMapping();
        }
    }
}
