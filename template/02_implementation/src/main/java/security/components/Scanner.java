package security.components;

import security.algorithm.IStringMatching;
import security.customer.HandBaggage;
import security.data.Record;
import security.data.ScanResult;
import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Scanner {
    private final IStringMatching stringMatcher;
    private Tray currentTray = null;
    private int scanCount = 0;


    public Scanner (IStringMatching stringMatcher) {
        this.stringMatcher = stringMatcher;
    }


    public Record scan () {
        if (currentTray == null || currentTray.getContainedBaggage() == null) {
            System.out.println("Scanner     : No Baggage in Scanner Chamber. Aborting scan!");
            throw new NullPointerException();
        }

        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm:ss,SSS");
        final HandBaggage currentBaggage = currentTray.getContainedBaggage();

        // Scan every layer successive for all types of prohibited Items
        for (int layer = 0 ; layer < currentBaggage.getLayers().length ; layer++) {
            for (ProhibitedItem item : ProhibitedItem.values()) {
                String toScan = new String(currentBaggage.getLayers()[layer].getContent());
                final int searchResult = stringMatcher.search(toScan, item.getSignature());

                if (searchResult != -1) {
                    return new Record(
                            scanCount++,
                            dateTimeFormatter.format(LocalDateTime.now()),
                            new ScanResult(
                                    ScanResultType.PROHIBITED_ITEM,
                                    item,
                                    new int[] {layer, searchResult}
                            )
                    );
                }
            }
        }

        // Nothing found. Clean baggage
        return new Record(
                scanCount++,
                dateTimeFormatter.format(LocalDateTime.now()),
                new ScanResult(ScanResultType.CLEAN, null, null)
        );
    }

    public Tray move (Tray enteringTray) {
        Tray temp = currentTray;
        currentTray = enteringTray;
        return temp;
    }


    public Tray getCurrentTray () {
        return currentTray;
    }
}
