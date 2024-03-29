import java.sql.Time;
import java.sql.Timestamp;
public class CacheEntry {
    private static int FRESHNESS = 30000;
    public CacheEntry(){}
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
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public boolean validityCheck(){
        if (System.currentTimeMillis()-this.lastValidated < FRESHNESS){
            return true;
        }
        return false;
    }

}
