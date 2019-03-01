package nl.trifork.coins.version.query;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VersionEntity {

    @Id
    private String id;

    public VersionEntity() {
    }

    public VersionEntity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
