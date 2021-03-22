package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

@Entity
public class Setting extends PanacheEntityBase {

    @Id
    private String id;
    @ManyToMany
    @JoinTable(name="setting_components_relation")
    private Set<JukeboxComponent> requires;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<JukeboxComponent> getRequires() {
        return requires;
    }

    public void setRequires(Set<JukeboxComponent> requires) {
        this.requires = requires;
    }
    
    @Override
    public String toString() {
        return "[Setting "+id+" requires "+requires+"]";
    }
    
    @Override
    public boolean equals(Object that) {
        return that instanceof Setting && ((Setting)that).id.equals(this.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
