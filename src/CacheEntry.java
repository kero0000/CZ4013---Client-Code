import java.sql.Time;
import java.sql.Timestamp;
public class CacheEntry {
    private static int FRESHNESS = 30000;
    public CacheEntry(){}

    public CacheEntry(String content, int lastModified, int lastValidated){
        this.lastModified = lastModified;
        this.lastValidated = lastValidated;
        this.content = content;
    }
    public CacheEntry(String content){
        this.content = content;
    }

    public int getLastValidated() {
        return lastValidated;
    }

    public void setLastValidated(int lastValidated) {
        this.lastValidated = lastValidated;
    }

    private int lastValidated;

    public int getLastModified() {
        return lastModified;
    }

    public void setLastModified(int lastModified) {
        this.lastModified = lastModified;
    }

    private int lastModified;
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public boolean validityCheck(){
        int current = (int)System.currentTimeMillis();
        if (current-this.lastValidated < FRESHNESS){
            return true;
        }
        return false;
    }

    public boolean validityModifiedCheck(int lastModified){
        if (this.lastModified==lastModified){
            this.lastValidated = (int) System.currentTimeMillis();
            return true;
        }
        return false;
    }

}
