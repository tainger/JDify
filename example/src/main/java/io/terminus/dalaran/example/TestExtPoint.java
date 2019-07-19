package io.terminus.dalaran.example;

import io.terminus.dalaran.DalaranIntegration;

import java.util.Map;

@DalaranIntegration(key = "testAbccc")
public interface TestExtPoint {

    Map getName(String a, String b);
}
