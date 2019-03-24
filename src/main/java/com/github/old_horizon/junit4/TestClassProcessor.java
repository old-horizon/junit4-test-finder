package com.github.old_horizon.junit4;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.experimental.theories.Theory;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.lang.model.element.Modifier.PUBLIC;

class TestClassProcessor {

    private final Types typeUtils;
    private final TypeMirror jUnit3TestClass;

    TestClassProcessor(final Types typeUtils, final Elements elementUtils) {
        this.typeUtils = typeUtils;
        jUnit3TestClass = elementUtils.getTypeElement(TestCase.class.getName()).asType();
    }

    void process(final TypeElement clazz, final JUnit4TestFoundListener listeners) {
        final Predicate<ExecutableElement> testMethodPredicate = isJUnit3TestClass(clazz) ?
                TestClassProcessor::isJUnit3TestMethod : TestClassProcessor::isJUnit4TestMethod;
        getAllMethods(clazz).stream()
                .filter(testMethodPredicate)
                .forEach(method -> invokeListeners(clazz, method, listeners));
    }

    private boolean isJUnit3TestClass(final TypeElement clazz) {
        return typeUtils.isSubtype(clazz.asType(), jUnit3TestClass);
    }

    private List<ExecutableElement> getAllMethods(final TypeElement clazz) {
        return Stream.concat(Stream.of(clazz), SuperClassesStream.of(typeUtils, clazz))
                .flatMap(TestClassProcessor::getDirectlyDeclaredMethods)
                .collect(Collectors.toList());
    }

    private static boolean isJUnit4TestMethod(final ExecutableElement method) {
        return method.getModifiers().contains(PUBLIC)
                && (method.getAnnotation(Test.class) != null || method.getAnnotation(Theory.class) != null);
    }

    private static boolean isJUnit3TestMethod(final ExecutableElement method) {
        return method.getModifiers().contains(PUBLIC) && method.getSimpleName().toString().startsWith("test")
                || isJUnit4TestMethod(method);
    }

    private static Stream<ExecutableElement> getDirectlyDeclaredMethods(final TypeElement clazz) {
        return ElementFilter.methodsIn(clazz.getEnclosedElements()).stream();
    }

    private static void invokeListeners(final TypeElement clazz, final ExecutableElement method,
                                        final JUnit4TestFoundListener listeners) {
        final String className = clazz.getQualifiedName().toString();
        final String methodName = method.getSimpleName().toString();
        listeners.testFound(className, methodName);
    }

}
