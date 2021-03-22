package org.regadou.jukebox;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class JukeboxComponent extends PanacheEntityBase {

    @Id
    private String name;

    public JukeboxComponent() {}
    
    public JukeboxComponent(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "[JukeboxComponent "+name+"]";
    }
    
    @Override
    public boolean equals(Object that) {
        return that instanceof JukeboxComponent && ((JukeboxComponent)that).name.equals(this.name);
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
