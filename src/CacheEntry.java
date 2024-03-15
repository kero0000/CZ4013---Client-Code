import java.sql.Time;
import java.sql.Timestamp;
public class CacheEntry {

    public CacheEntry(){}
    public CacheEntry(String filename, String content, Timestamp lastModified, Timestamp lastValidated){
        this.filename = filename;
        this.content = content;
        this.lastModified = lastModified;
        this.lastValidated = lastValidated;
    }
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public Timestamp getLastModified() {
        return lastModified;
    }

    public void setLastModified(Timestamp lastModified) {
        this.lastModified = lastModified;
    }

    private Timestamp lastModified;

    public Timestamp getLastValidated() {
        return lastValidated;
    }

    public void setLastValidated(Timestamp lastValidated) {
        this.lastValidated = lastValidated;
    }

    // validation time updated when client validity check of file in server
    private Timestamp lastValidated;
}
