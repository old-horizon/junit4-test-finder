package com.github.old_horizon.junit4;

public interface JUnit4TestFoundListener {

    void onStart();

    void testFound(String className, String methodName);

    void onEnd();

}
