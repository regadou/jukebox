package org.regadou.jukebox;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DataInitializer {

    private static final Logger LOGGER = Logger.getLogger("DataInitializer");

    @Inject
    SettingRepository settingRepo;
    @Inject
    JukeboxRepository jukeboxRepo;
    @Inject
    ComponentRepository componentRepo;
    @Inject
    ObjectMapper jsonHandler;
    @ConfigProperty(name = "data.settings.url")
    String settingsUrl;
    @ConfigProperty(name = "data.jukeboxes.url")
    String jukeboxesUrl;

    @Transactional
    public void initialize() throws IOException {
        Set<JukeboxComponent> components = new LinkedHashSet<>();
        LOGGER.info("Downloading data from "+jukeboxesUrl+" ...");
        Jukeboxes jukeboxes = downloadData(jukeboxesUrl, Jukeboxes.class);
        for (Jukebox jukebox : jukeboxes)
            components.addAll(jukebox.getComponents());

        LOGGER.info("Downloading data from "+settingsUrl+" ...");
        List<Setting> settings = downloadData(settingsUrl, Settings.class).getSettings();
        for (Setting setting : settings)
            components.addAll(setting.getRequires());

        LOGGER.info("Found "+jukeboxes.size()+" jukeboxes, "+components.size()+" components and "+settings.size()+" settings");
        for (JukeboxComponent component : components)
            componentRepo.persist(component);
        for (Jukebox jukebox : jukeboxes)
            jukeboxRepo.persist(jukebox);
        for (Setting setting : settings)
            settingRepo.persist(setting);
    }
    
    private <T> T downloadData(String url, Class<T> type) throws IOException {
        try (InputStream input = new URL(url).openStream()) {
            return jsonHandler.readValue(input, type);
        }
    }
}
