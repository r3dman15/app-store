package apple.appstore.repositories;

import apple.appstore.models.App;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaAppRepository extends JpaRepository<App, Long> {
    Optional<App> findByName(String name);
}
