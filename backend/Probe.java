import com.rentease.config.WebConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import static org.mockito.Mockito.mock;

public class Probe {
    static class ExposedResourceHandlerRegistry extends ResourceHandlerRegistry {
        ExposedResourceHandlerRegistry() { super(mock(ApplicationContext.class), null); }
        AbstractHandlerMapping exposedHandlerMapping() { return getHandlerMapping(); }
    }
    public static void main(String[] args) {
        ExposedResourceHandlerRegistry registry = new ExposedResourceHandlerRegistry();
        new WebConfig().addResourceHandlers(registry);
        AbstractHandlerMapping handlerMapping = registry.exposedHandlerMapping();
        SimpleUrlHandlerMapping mapping = (SimpleUrlHandlerMapping) handlerMapping;
        System.out.println(mapping.getHandlerMap());
        System.out.println(mapping.getHandlerMap().containsKey("/uploads/**"));
        Object handler = mapping.getHandlerMap().get("/uploads/**");
        System.out.println(handler == null ? "NULL" : handler.getClass().getName());
        System.out.println(handler instanceof ResourceHttpRequestHandler);
    }
}
