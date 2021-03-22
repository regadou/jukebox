package org.regadou.jukebox;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DataInitializer {

    private ObjectMapper jsonHandler;
    private SettingRepository settingRepo;
    private JukeboxRepository jukeboxRepo;
    private ComponentRepository componentRepo;
    private boolean initialized;
    private boolean initializing;

    @ConfigProperty(name = "data.settings.url")
    String settingsUrl;
    @ConfigProperty(name = "data.jukeboxes.url")
    String jukeboxesUrl;

    @Inject
    public DataInitializer(SettingRepository settingRepo, JukeboxRepository jukeboxRepo, ComponentRepository componentRepo, ObjectMapper jsonHandler) {
        this.settingRepo = settingRepo;
        this.jukeboxRepo = jukeboxRepo;
        this.componentRepo = componentRepo;
        this.jsonHandler = jsonHandler;
    }
    
    public boolean isInitialized() {
        return initialized;
    }

    @Transactional
    public boolean initialize() throws IOException {
        if (initialized || initializing)
            return false;
        try {
            Set<JukeboxComponent> components = new LinkedHashSet<>();
            Jukeboxes jukeboxes = downloadData(jukeboxesUrl, Jukeboxes.class);
            for (Jukebox jukebox : jukeboxes)
                components.addAll(jukebox.getComponents());
            List<Setting> settings = downloadData(settingsUrl, Settings.class).getSettings();
            for (Setting setting : settings)
                components.addAll(setting.getRequires());
            for (JukeboxComponent component : components)
                componentRepo.persist(component);
            for (Jukebox jukebox : jukeboxes)
                jukeboxRepo.persist(jukebox);
            for (Setting setting : settings)
                settingRepo.persist(setting);
            initialized = true;
        }
        finally {
            initializing = false;
        }
        return initialized;
    }
    
    public <T> T downloadData(String url, Class<T> type) throws IOException {
        try (InputStream input = new URL(url).openStream()) {
            return jsonHandler.readValue(input, type);
        }
    }
}
