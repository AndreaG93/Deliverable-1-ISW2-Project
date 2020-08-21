package entities.release;

import entities.Commit;
import entities.MetadataProvider;
import entities.enums.ReleaseOutputField;

import java.time.LocalDateTime;

public class Release extends MetadataProvider<ReleaseOutputField> {


    private Commit commit;

    public Release() {
        super();
    }





    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
        this.setMetadata(ReleaseOutputField.RELEASE_DATE, this.commit.date);
    }

    public LocalDateTime getReleaseDate() {
        return (LocalDateTime) this.getMetadata(ReleaseOutputField.RELEASE_DATE);
    }

    public int getReleaseVersionID() {
        return (int) this.getMetadata(ReleaseOutputField.VERSION_ID);
    }

    public int getVersionIndex() {
        return (int) this.getMetadata(ReleaseOutputField.VERSION_INDEX);
    }

    public void setVersionIndex(int index) {
        this.setMetadata(ReleaseOutputField.VERSION_INDEX, index);
    }
}