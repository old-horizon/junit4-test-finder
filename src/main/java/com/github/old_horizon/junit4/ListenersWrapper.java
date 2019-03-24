package com.github.old_horizon.junit4;

import java.util.ServiceLoader;
import java.util.function.Consumer;

class ListenersWrapper implements JUnit4TestFoundListener {

    private final ServiceLoader<JUnit4TestFoundListener> listeners =
            ServiceLoader.load(JUnit4TestFoundListener.class, getClass().getClassLoader());

    @Override
    public void onStart() {
        invoke(JUnit4TestFoundListener::onStart);
    }

    @Override
    public void testFound(final String className, final String methodName) {
        invoke(listener -> listener.testFound(className, methodName));
    }

    @Override
    public void onEnd() {
        invoke(JUnit4TestFoundListener::onEnd);
    }

    private void invoke(final Consumer<JUnit4TestFoundListener> method) {
        listeners.forEach(method);
    }

}
