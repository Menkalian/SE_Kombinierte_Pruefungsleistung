package security.components;

import security.algorithm.IStringMatching;
import security.customer.HandBaggage;
import security.data.Record;
import security.data.ScanResult;
import security.data.enums.ProhibitedItem;
import security.data.enums.ScanResultType;

import java.time.Instant;

public class Scanner {
    private final IStringMatching stringMatcher;
    private Tray currentTray = null;
    private int scanCount = 0;

    public Scanner (IStringMatching stringMatcher) {
        this.stringMatcher = stringMatcher;
    }

    public Record scan () {
        if (currentTray == null || currentTray.getContainedBaggage() == null) {
            System.out.println("No Baggage in Scanner. Aborting scan!");
            throw new NullPointerException();
        }

        for (ProhibitedItem item : ProhibitedItem.values()) {
            final HandBaggage currentBaggage = currentTray.getContainedBaggage();
            for (int layer = 0 ; layer < currentBaggage.getLayers().length ; layer++) {
                String toScan = new String(currentBaggage.getLayers()[layer].getContent());

                final int searchResult = stringMatcher.search(toScan, item.getSignature());
                if (searchResult != -1) {
                    return new Record(scanCount++, Instant.now().toString(), new ScanResult(ScanResultType.PROHIBITED_ITEM, item, new int[] {layer, searchResult}));
                }
            }
        }
        return new Record(scanCount++, Instant.now().toString(), new ScanResult(ScanResultType.CLEAN, null, null));
    }

    public Tray getCurrentTray () {
        return currentTray;
    }

    public Tray move (Tray enteringTray) {
        Tray temp = currentTray;
        currentTray = enteringTray;
        return temp;
    }
}
