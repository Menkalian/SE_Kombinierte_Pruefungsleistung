package security.data;

public class Record {
    private final int id;
    private final String timestamp;
    private final ScanResult result;

    public Record (int id, String timestamp, ScanResult result) {
        this.id = id;
        this.timestamp = timestamp;
        this.result = result;
    }

    public int getId () {
        return id;
    }

    public String getTimestamp () {
        return timestamp;
    }

    public ScanResult getResult () {
        return result;
    }
}
