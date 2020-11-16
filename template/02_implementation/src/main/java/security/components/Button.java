package security.components;

import security.data.enums.ButtonIcon;

public class Button {
    private final ButtonIcon icon;
    private final OperatingStation connectedOperatingStation;

    public Button (ButtonIcon icon, OperatingStation connectedOperatingStation) {
        this.icon = icon;
        this.connectedOperatingStation = connectedOperatingStation;
    }

    public OperatingStation getConnectedOperatingStation () {
        return connectedOperatingStation;
    }

    public ButtonIcon getIcon () {
        return icon;
    }

    public void push () {
        System.out.println("Button with Icon '" + icon + "' pushed.");
        switch (icon) {
            case LEFT_ARROW -> connectedOperatingStation.getConnectedScanner().moveBeltBackwards();
            case RECTANGLE -> connectedOperatingStation.getConnectedScanner().scan();
            case RIGHT_ARROW -> connectedOperatingStation.getConnectedScanner().moveBeltForward();
        }
    }

    @Override
    public String toString () {
        return icon.name();
    }
}
