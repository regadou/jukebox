package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SettingRepository implements PanacheRepository<Setting> {

    public Setting findById(String id) {
        return this.find("select s from Setting s where id = ?1", id).firstResult();
    }
}
