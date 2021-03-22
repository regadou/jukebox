package org.regadou.jukebox;

import java.util.List;

public class Settings {
    
    private List<Setting> settings;

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
    
    public String toString() {
        return settings.toString();
    }
}
