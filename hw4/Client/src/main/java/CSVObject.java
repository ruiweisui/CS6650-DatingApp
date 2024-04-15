public class CSVObject {
    long startTime;
    long endTime;
    int statusCode;
    String requestType;

    public CSVObject(long startTime, long endTime, int statusCode, String requestType) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.statusCode = statusCode;
        this.requestType = requestType;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getRequestType() {
        return requestType;
    }
}
