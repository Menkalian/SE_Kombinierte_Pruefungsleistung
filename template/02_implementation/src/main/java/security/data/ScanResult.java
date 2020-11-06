package security.data;

import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;

public class ScanResult {
    private final ScanResultType type;
    private final ProhibitedItem itemType;
    private final int[] position;

    public ScanResult (ScanResultType type, ProhibitedItem itemType, int[] position) {
        this.type = type;
        this.itemType = itemType;
        this.position = position;
    }

    public ScanResultType getType () {
        return type;
    }

    public ProhibitedItem getItemType () {
        return itemType;
    }

    public int[] getPosition () {
        return position;
    }
}
