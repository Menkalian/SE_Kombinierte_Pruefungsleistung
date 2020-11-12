package security.data;

import java.text.DecimalFormat;

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


    @Override
    public String toString () {
        DecimalFormat idFormat = new DecimalFormat("000");

        return "# Scan " + idFormat.format(id) + "   Timestamp: #\n" +
               "#" + timestamp + "#\n" +
               "# Result:              #\n" +
               result.toString() + "\n";
    }
}
