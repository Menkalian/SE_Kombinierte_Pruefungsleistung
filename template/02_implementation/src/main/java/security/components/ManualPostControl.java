package security.components;

import security.customer.Passenger;
import security.devices.ExplosivesTraceDetector;
import security.staff.Employee;
import security.staff.FederalPoliceOfficer;

public class ManualPostControl {

    //region connected components/devices
    private final BaggageScanner connectedScanner;
    private final Track belongingTrack;
    private final ExplosivesTraceDetector explosivesTraceDetector;
    //endregion connected components/devices
    //region people
    private Employee workingInspector;
    private FederalPoliceOfficer[] presentOfficers;
    private Passenger presentPassenger;
    //endregion people
    private Tray currentTrayToInvestigate;


    public ManualPostControl (Track belongingTrack, BaggageScanner connectedScanner, ExplosivesTraceDetector explosivesTraceDetector) {
        this.belongingTrack = belongingTrack;
        this.connectedScanner = connectedScanner;
        this.explosivesTraceDetector = explosivesTraceDetector;
    }


    public Track getBelongingTrack () {
        return belongingTrack;
    }


    public BaggageScanner getConnectedScanner () {
        return connectedScanner;
    }


    public Employee getWorkingInspector () {
        return workingInspector;
    }

    public void setWorkingInspector (Employee workingInspector) {
        this.workingInspector = workingInspector;
    }


    public FederalPoliceOfficer[] getPresentOfficers () {
        return presentOfficers;
    }

    public void setPresentOfficers (FederalPoliceOfficer[] presentOfficers) {
        this.presentOfficers = presentOfficers;
    }


    public Tray getCurrentTrayToInvestigate () {
        return currentTrayToInvestigate;
    }

    public void setCurrentTrayToInvestigate (Tray currentTrayToInvestigate) {
        this.currentTrayToInvestigate = currentTrayToInvestigate;
    }


    public Passenger getPresentPassenger () {
        return presentPassenger;
    }

    public void setPresentPassenger (Passenger presentPassenger) {
        this.presentPassenger = presentPassenger;
    }


    public ExplosivesTraceDetector getExplosivesTraceDetector () {
        return explosivesTraceDetector;
    }
}
