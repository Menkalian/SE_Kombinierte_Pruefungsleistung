package security.devices;

import security.algorithm.BruteForce;
import security.algorithm.IStringMatching;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ExplosivesTraceDetector {
    private final IStringMatching stripeTestAlgorithm = new BruteForce();


    public boolean testStripe (ExplosivesTestStrip toTest) {
        System.out.println("ExpTraceDet.: Testing for Explosives");
        String testString = Arrays.stream(toTest.getStripe()).map(String::new).collect(Collectors.joining());
        return stripeTestAlgorithm.search(testString, "exp") != -1;
    }
}
