package apple.appstore.listeners;

import apple.appstore.events.AppCreatedEvent;
import apple.appstore.repositories.ElasticSearchAppRepository;
import apple.appstore.services.AppService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class AppEventListener {

    private final ElasticSearchAppRepository elasticSearchAppRepository;
    private static final Logger log = LoggerFactory.getLogger(AppService.class);
    public AppEventListener(ElasticSearchAppRepository elasticSearchAppRepository) {
        this.elasticSearchAppRepository = elasticSearchAppRepository;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAppCreated(AppCreatedEvent event) {
        try {
            elasticSearchAppRepository.save(event.app());
            log.info("Indexed app into Elasticsearch: {}", event.app().getId());
        } catch (Exception e) {
            log.error("Failed to index app {} into Elasticsearch", event.app().getId(), e);
            // Optional: Send to retry queue / DLQ
        }
    }
}
