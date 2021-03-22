package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;


@Entity
public class Jukebox extends PanacheEntityBase {

    @Id
    private String id;
    @Column
    private String model;
    @ManyToMany
    @JoinTable(name="jukebox_components_relation")
    private Set<JukeboxComponent> components;

    public Jukebox() {}
    
    public Jukebox(String id, String model, JukeboxComponent...components) {
        this.id = id;
        this.model = model;
        this.components = new LinkedHashSet<>(Arrays.asList(components));
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Set<JukeboxComponent> getComponents() {
        return components;
    }

    public void setComponents(Set<JukeboxComponent> components) {
        this.components = components;
    }
    
    @Override
    public String toString() {
        return "[Jukebox "+model+" "+id+"]";
    }
    
    @Override
    public boolean equals(Object that) {
        return that instanceof Jukebox && ((Jukebox)that).id.equals(this.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
