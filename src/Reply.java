import java.sql.Timestamp;

public class Reply {

    public Reply (int requestId, int status, int modifiedTime, String content) {
        this.requestId = requestId;
        this.status = status;
        this.modifiedTime = modifiedTime;
        this.content = content;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    private int requestId;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int status;

    public int getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(int modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    private int modifiedTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

}
