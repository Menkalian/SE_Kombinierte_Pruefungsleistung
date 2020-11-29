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


    @Override
    public String toString () {
        if (type == ScanResultType.CLEAN) {
            return "REPORT      : #         CLEAN         #\n" +
                   "REPORT      : #                       #";
        } else {
            DecimalFormat positionFormat = new DecimalFormat("00000");
            return ("""
                    REPORT      : #  PROHIBITED ITEM AT:  #
                    REPORT      : #    Layer %d            #
                    REPORT      : #    Position %s     #
                    REPORT      : # Type: %s #
                    REPORT      : #                       #""")
                    .formatted(
                            position[0],
                            positionFormat.format(position[1]),
                            itemType.toString()
                    );
        }
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
