package apple.appstore.controllers;

import apple.appstore.exceptions.AppNotFoundException;
import apple.appstore.models.App;
import apple.appstore.services.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/apps")
public class AppController {

    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    /**
     * GET /apps — return all apps
     */
    @GetMapping
    public ResponseEntity<List<App>> getAllApps() {
        return ResponseEntity.ok(appService.getAllApps());
    }

    /**
     * GET /apps/{id} — return one app by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<App> getAppById(@PathVariable int id) {
        return appService.getAppById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new AppNotFoundException(id));

    }


    /**
     * GET /apps/search?keyword=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<App>> searchApps(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return ResponseEntity.badRequest().body(List.of()); // Or return all apps
        }
        return ResponseEntity.ok(appService.searchApps(keyword));
    }

    @PostMapping()
    public ResponseEntity<App> createApp(@RequestBody App app) {
        App createdApp = appService.saveApp(app);
        return ResponseEntity.status(201).body(createdApp);
    }
}
