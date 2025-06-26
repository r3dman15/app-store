package apple.appstore.repositories;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;

import apple.appstore.models.App;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ElasticSearchAppRepository implements AppRepository {

    private final ElasticsearchClient client;
    private static final String INDEX = "apps";

    public ElasticSearchAppRepository(ElasticsearchClient client) {
        this.client = client;
    }

    @Override
    public List<App> findAll() {
        try {
            SearchResponse<App> resp = client.search(s -> s
                            .index(INDEX)
                            .size(10_000_000)
                            .query(q -> q
                                    .matchAll(m -> m)
                            ),
                    App.class
            );
            return resp.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch all apps from ES", e);
        }
    }

    @Override
    public Optional<App> findById(int id) {
        try {
            var resp = client.get(g -> g
                            .index(INDEX)
                            .id(String.valueOf(id)),
                    App.class
            );
            return resp.found() ? Optional.of(resp.source()) : Optional.empty();
        } catch (IOException e) {
            throw new RuntimeException("Failed to fetch app by id from ES", e);
        }
    }

    @Override
    public Optional<App> findByName(String name) {
        try {
            SearchResponse<App> resp = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .term(t -> t
                                            .field("name.keyword")
                                            .value(name)
                                    )
                            ),
                    App.class
            );
            return resp.hits().hits().stream()
                    .map(Hit::source)
                    .findFirst();

        } catch (IOException e) {
            throw new RuntimeException("Failed to search apps in ES", e);
        }
    }

    @Override
    public List<App> search(String keyword) {
        try {
            SearchResponse<App> resp = client.search(s -> s
                            .index(INDEX)
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .fields("name", "tagline")
                                            .query(keyword)
                                    )
                            ),
                    App.class
            );
            return resp.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to search apps in ES", e);
        }
    }



    @Override
    public void save(App app) {
        try {
            client.index(i -> i
                    .index(INDEX)
                    .id(String.valueOf(app.getId()))
                    .document(app));
        }catch (IOException e) {
            throw new RuntimeException("Failed to save app to ES", e);
        }
    }
}
