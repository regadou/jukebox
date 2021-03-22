package org.regadou.jukebox;

import java.io.IOException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.spi.HttpRequest;

@Path("/settings")
public class SettingsController {

    @Context
    HttpRequest request;
    private SettingRepository settingRepo;
    private JukeboxRepository jukeboxRepo;

    @Inject
    public SettingsController(SettingRepository settingRepo, JukeboxRepository jukeboxRepo, DataInitializer initializer) throws IOException {
        this.settingRepo = settingRepo;
        this.jukeboxRepo = jukeboxRepo;
        if (!initializer.isInitialized())
            initializer.initialize();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Jukebox> getCompatibleJukeboxes() {
        MultivaluedMap<String,String> query = request.getUri().getQueryParameters();
        String settingId = getValue(query, "settingId");
        Setting setting;
        if (settingId == null)
            throw new WebApplicationException("Mandatory settingId parameter is missing", Response.Status.BAD_REQUEST);
        setting = settingRepo.findById(settingId);
        if (setting == null)
            throw new WebApplicationException("No setting with id "+settingId, Response.Status.NOT_FOUND);
        String model = getValue(query, "model");
        Integer offset = getInteger(query, "offset");
        Integer limit = getInteger(query, "limit");
        List<Jukebox> jukeboxes = jukeboxRepo.findModelAndSetting(model, setting, limit, offset);
        if (jukeboxes.isEmpty())
            throw new WebApplicationException("No jukebox found with specified model and setting", Response.Status.NOT_FOUND);
        return jukeboxes;
    }
    
    private String getValue(MultivaluedMap<String,String> map, String key) {
        List<String> values = map.get(key);
        return (values == null || values.isEmpty()) ? null : values.get(values.size()-1);
    }
    
    private Integer getInteger(MultivaluedMap<String,String> map, String key) {
        String value = getValue(map, key);
        if (value == null || value.isEmpty())
            return null;
        try {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e) {
            throw new WebApplicationException("Invalid value for "+key+" parameter: "+value, e, Response.Status.BAD_REQUEST);
        }
    }
}
