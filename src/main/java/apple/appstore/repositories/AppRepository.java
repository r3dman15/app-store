package apple.appstore.repositories;

import apple.appstore.models.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppRepository {

    List<App> findAll();

    Optional<App> findById(int id);

    Optional<App> findByName(String id);

    List<App> search(String keyword);

    void save(App app);
}
