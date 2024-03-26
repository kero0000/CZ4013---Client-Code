public class Request {
    private int operation;
    private String filename;
    private int requestId;
    private int offset;
    private int bytesToReadFrom;
    private String bytesToWrite;
    private int interval;

    public int getBytesToDelete() {
        return bytesToDelete;
    }

    public void setBytesToDelete(int bytesToDelete) {
        this.bytesToDelete = bytesToDelete;
    }

    private int bytesToDelete;

    private boolean removeFlag;
    public String getBytesToWrite() {
        return bytesToWrite;
    }

    public void setBytesToWrite(String bytesToWrite) {
        this.bytesToWrite = bytesToWrite;
    }

    public Request(){}

    // list directory
    public Request(int operation, String filename, int requestId){
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
    }
    // getAttri
    public Request(int operation, int requestId, String filename){
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
    }
    // read
    public Request(int operation, String filename, int requestId, int offset, int bytesToReadFrom) {
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
        this.offset = offset;
        this.bytesToReadFrom = bytesToReadFrom;
    }

    // delete
    public Request(int operation, String filename, int requestId, int offset, int bytesToDelete, boolean removeFlag) {
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
        this.offset = offset;
        this.bytesToDelete = bytesToDelete;
        this.removeFlag = true;
    }

    //write
    public Request(int operation, String filename, int requestId, int offset, String bytesToWrite) {
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
        this.offset = offset;
        this.bytesToWrite = bytesToWrite;
    }


    //monitor
    public Request(int operation, String filename, int requestId, int interval) {
        this.operation = operation;
        this.filename = filename;
        this.requestId = requestId;
        this.interval = interval;
    }
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    public int getOffset() {
        return this.offset;
    }
    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getBytesToReadFrom() {
        return this.bytesToReadFrom;
    }

    public void setBytesToReadFrom(int bytesToReadFrom) {
        this.bytesToReadFrom = bytesToReadFrom;
    }

    public void setrequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getrequestId() {
        return this.requestId;
    }

    public int getOperation() {
        return this.operation;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setOperation(int operation){
        this.operation = operation;
    }

    public void setFilename(String filename){
        this.filename = filename;
    }
}
