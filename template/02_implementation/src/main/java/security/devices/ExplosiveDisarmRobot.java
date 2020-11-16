package security.devices;

import security.customer.HandBaggage;
import security.customer.Layer;

import java.util.Arrays;

public class ExplosiveDisarmRobot {
    public void destroyBaggage (HandBaggage toDestroy) {
        System.out.println("Destroying HandBaggage of \"" + toDestroy.getOwner().getName() + "\"");
        System.out.println("Created Pieces:");
        Arrays.stream(toDestroy.getLayers())
              .map(Layer::getContent)
              .map(chars -> {
                  char[][] toReturn = new char[200][50];
                  for (int i = 0 ; i < 200 ; i++) {
                      toReturn[i] = Arrays.copyOfRange(chars, 50 * i, 50 * (i + 1));
                  }
                  return toReturn;
              }).forEach(c -> Arrays.stream(c).map(String::new).forEach(System.out::println));
        toDestroy.setLayers(null);
    }
}
