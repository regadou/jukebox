package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JukeboxRepository implements PanacheRepository<Jukebox> {

    public Jukebox findById(String id) {
        return this.find("select j from Jukebox j where id = ?1", id).firstResult();
    }
    
    public List<Jukebox> findModelAndSetting(String model, Setting setting, Integer limit, Integer offset) {
        String jpql = "select j from Jukebox j";
        List<Object> params = new ArrayList<>();
        if (model != null) {
            params.add(model);
            jpql += " where j.model = ?1";
        }
        jpql += " order by j.id";
        List<Jukebox> jukes = this.list(jpql, params.toArray());
        if (offset == null)
            offset = 0;
        if (limit == null)
            limit = jukes.size();
        List<Jukebox> filtered = new ArrayList<>();
        Set<JukeboxComponent> required = setting.getRequires();
        for (Jukebox jukebox : jukes) {
            if (jukebox.getComponents().containsAll(required)) {
                if (offset > 0) {
                    offset--;
                    continue;
                }
                filtered.add(jukebox);
                if (filtered.size() >= limit)
                    break;
            }
        }
        return filtered;
    }    
/**    
    public List<Jukebox> findModelAndSetting(String model, Setting setting, Integer limit, Integer offset) {
        String jpql = "select j from Jukebox j";
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        if (model != null) {
            params.add(model);
            conditions.add("j.model = ?"+params.size());
        }
        if (setting != null) {
            jpql += " join j.components c";
            for (JukeboxComponent required : setting.getRequires()) {
                params.add(required.getName());
                conditions.add("?"+params.size()+" = c.name");
            }
        }
        if (!conditions.isEmpty())
            jpql += " where "+String.join(" and ", conditions);
        if (limit != null) {
            if (offset == null)
                offset = 0;
            jpql += " limit "+limit+" offset "+offset;
        }
        else if (offset != null)
            jpql += " offset "+offset;
        return this.list(jpql, params.toArray());
    }
**/    
}
