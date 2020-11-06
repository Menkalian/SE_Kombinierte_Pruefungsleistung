package security.components;

import security.customer.Passenger;
import security.devices.ExplosivesTraceDetector;
import security.staff.Employee;
import security.staff.FederalPoliceOfficer;

public class ManualPostControl {
    private Track belongingTrack;
    private BaggageScanner connectedScanner;
    private Employee workingInspector;
    private FederalPoliceOfficer[] presentOfficers;
    private Tray currentTrayToInvestigate;
    private Passenger presentPassenger;
    private ExplosivesTraceDetector explosivesTraceDetector;
}
