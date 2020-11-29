package security.devices;

import security.customer.HandBaggage;
import security.customer.Layer;

import java.util.Arrays;

public class ExplosiveDisarmRobot {
    public void destroyBaggage (HandBaggage toDestroy) {
        System.out.println("ExpDisarmBot: Destroying HandBaggage of \"" + toDestroy.getOwner().getName() + "\"");
        System.out.println("ExpDisarmBot: Created Pieces:");
        Arrays.stream(toDestroy.getLayers())
              .map(Layer::getContent)
              .map(chars -> {
                  char[][] toReturn = new char[200][50];
                  for (int i = 0 ; i < 200 ; i++) {
                      toReturn[i] = Arrays.copyOfRange(chars, 50 * i, 50 * (i + 1));
                  }
                  return toReturn;
              })
              .forEach(c -> Arrays
                      .stream(c).map(String::new)
                      .forEach(str -> System.out.printf("JUNK        : %s%n", str))
              );
        toDestroy.setLayers(null);
    }
}
