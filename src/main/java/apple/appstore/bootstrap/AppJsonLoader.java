package apple.appstore.bootstrap;

import apple.appstore.models.App;
import apple.appstore.repositories.JpaAppRepository;
import apple.appstore.services.AppService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class AppJsonLoader {

    private static final Logger log = LoggerFactory.getLogger(AppJsonLoader.class);
    private final AppService appService;
    private final ObjectMapper mapper;

    public AppJsonLoader(AppService appService) {
        this.appService = appService;
        this.mapper = new ObjectMapper(); // Or inject if shared
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadApps() {
        log.info("✅ Attempting to load apps from apps.json...");

        try (InputStream is = new ClassPathResource("apps.json").getInputStream()) {
            List<App> apps = mapper.readValue(is, new TypeReference<>() {});
            for (App app : apps) {
                try {
                    appService.saveApp(app);
                } catch (Exception e) {
                    log.warn("⚠️ Failed to save app id={}", app.getId(), e);
                }
            }

            log.info("✅ Loaded {} apps into CockroachDB", apps.size());
        } catch (Exception e) {
            log.error("❌ App data load failed", e);
        }
    }
}
