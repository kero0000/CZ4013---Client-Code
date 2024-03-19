import java.sql.Time;
import java.sql.Timestamp;
public class CacheEntry {

    public CacheEntry(){}
    public CacheEntry(String content){
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

}
