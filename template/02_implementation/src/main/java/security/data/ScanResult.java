package security.data;

import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;

import java.text.DecimalFormat;

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


    @Override
    public String toString () {
        if (type == ScanResultType.CLEAN) {
            return "#         CLEAN         #\n";
        } else {
            DecimalFormat positionFormat = new DecimalFormat("00000");
            return "#  PROHIBITED ITEM AT:  #\n" +
                   "# Layer " + position[0] + "; Position " + positionFormat.format(position[1]) + " #\n" +
                   "# Type: " + itemType.toString() + " #\n";
        }
    }
}
