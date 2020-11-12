package security.components;

import security.algorithm.IStringMatching;
import security.data.Record;
import security.data.ScanResult;
import security.data.enums.ScanResultType;

import java.time.Instant;

public class Scanner {
    private IStringMatching stringMatcher;
    private Tray currentTray = null;

    public Record scan () {
        return new Record(1, Instant.now().toString(), new ScanResult(ScanResultType.CLEAN, null, null));
    }

    public Tray getCurrentTray () {
        return currentTray;
    }

    public Tray move(Tray enteringTray){
        return null;
    }
}
