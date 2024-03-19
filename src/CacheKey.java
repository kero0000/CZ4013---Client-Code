import java.sql.Timestamp;

public class CacheKey {
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private String filename;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    private int offset;

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

    private Timestamp lastValidated;


}
