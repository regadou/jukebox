package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ComponentRepository implements PanacheRepository<JukeboxComponent> {

    public JukeboxComponent findByName(String name) {
        return this.find("select c from JukeboxComponent c where name = ?1", name).firstResult();
    }
}
