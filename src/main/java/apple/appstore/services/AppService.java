package apple.appstore.services;

import apple.appstore.events.AppCreatedEvent;
import apple.appstore.exceptions.AppSaveException;
import apple.appstore.exceptions.SearchFailedException;
import apple.appstore.models.App;
import apple.appstore.repositories.JpaAppRepository;
import apple.appstore.repositories.ElasticSearchAppRepository;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppService {


    private final ElasticSearchAppRepository searchAppRepository;
    private final JpaAppRepository jpaAppRepository;
    private ApplicationEventPublisher eventPublisher;

    private static final Logger log = LoggerFactory.getLogger(AppService.class);



    public AppService(ElasticSearchAppRepository searchAppRepository, JpaAppRepository jpaAppRepository, ApplicationEventPublisher eventPublisher) {
        this.searchAppRepository = searchAppRepository;
        this.jpaAppRepository = jpaAppRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<App> getAllApps() {
        return searchAppRepository.findAll();
    }

    public Optional<App> getAppById(int id) {
        return searchAppRepository.findById(id);
    }

    public List<App> searchApps(String keyword) {
        try {
            return searchAppRepository.search(keyword);
        } catch (ElasticsearchException e) {
            log.error("Elasticsearch query failed", e);
            throw new SearchFailedException("Search currently unavailable");
        }
    }

    @Transactional
    public App saveApp(App incoming) {
        try {
            App saved = searchAppRepository.findByName(incoming.getName())
                    .map(existing -> {
                        existing.setTagline(incoming.getTagline());
                        existing.setImageUrl(incoming.getImageUrl());
                        return jpaAppRepository.save(existing);
                    })
                    .orElseGet(() -> jpaAppRepository.save(incoming));

            eventPublisher.publishEvent(new AppCreatedEvent(saved));

            return saved;
        } catch (DataAccessException e) {
            log.error("Failed to save app to JPA", e);
            throw new AppSaveException("Unable to save app");
        }
    }



}
