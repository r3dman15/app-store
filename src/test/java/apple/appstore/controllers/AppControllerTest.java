package apple.appstore.controllers;

import apple.appstore.models.App;
import apple.appstore.services.AppService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppService appService;

    @Test
    void testGetAllApps() throws Exception {
        List<App> apps = List.of(new App(1L, "App One", "Description", "app-one.png"));
        Mockito.when(appService.getAllApps()).thenReturn(apps);

        mockMvc.perform(get("/apps"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].name", is("App One")));
    }

    @Test
    void testGetAppById_Found() throws Exception {
        App app = new App(1L, "App One", "Description", "app-one.png");
        Mockito.when(appService.getAppById(1)).thenReturn(Optional.of(app));

        mockMvc.perform(get("/apps/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("App One")));
    }

    @Test
    void testGetAppById_NotFound() throws Exception {
        Mockito.when(appService.getAppById(99)).thenReturn(Optional.empty());

        mockMvc.perform(get("/apps/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSearchApps_WithKeyword() throws Exception {
        List<App> results = List.of(new App(2L, "Cool App", "Cool description", "cool-app.png"));
        Mockito.when(appService.searchApps("cool")).thenReturn(results);

        mockMvc.perform(get("/apps/search").param("keyword", "cool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(1)))
                .andExpect(jsonPath("$[0].name", is("Cool App")));
    }

    @Test
    void testSearchApps_EmptyKeyword() throws Exception {
        mockMvc.perform(get("/apps/search").param("keyword", " "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateApp() throws Exception {
        App newApp = new App(3L, "New App", "New app description", "new-app.png");
        Mockito.when(appService.saveApp(Mockito.any(App.class))).thenReturn(newApp);

        String jsonPayload = """
                {
                    "id": 3,
                    "name": "New App",
                    "description": "New app description"
                }
                """;

        mockMvc.perform(post("/apps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New App")));
    }
}
