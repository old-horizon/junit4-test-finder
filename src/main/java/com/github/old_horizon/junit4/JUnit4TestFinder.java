package com.github.old_horizon.junit4;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.lang.model.util.Types;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.Modifier.ABSTRACT;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes("*")
public class JUnit4TestFinder extends AbstractProcessor {

    private ElementVisitor visitor;
    private ListenersWrapper listeners;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        final Types typeUtils = processingEnv.getTypeUtils();
        final Elements elementUtils = processingEnv.getElementUtils();
        listeners = new ListenersWrapper();
        visitor = new ElementVisitor(typeUtils, elementUtils);
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        listeners.onStart();
        roundEnv.getRootElements().forEach(element -> visitor.visit(element));
        listeners.onEnd();
        return false;
    }

    private class ElementVisitor extends SimpleElementVisitor8<Void, Void> {

        private final TestClassProcessor processor;

        ElementVisitor(final Types typeUtils, final Elements elementUtils) {
            processor = new TestClassProcessor(typeUtils, elementUtils);
        }

        @Override
        public Void visitType(final TypeElement element, final Void param) {
            if (element.getKind() == CLASS && !element.getModifiers().contains(ABSTRACT)) {
                processor.process(element, listeners);
                processNestedClasses(element);
            }
            return null;
        }

        private void processNestedClasses(final TypeElement element) {
            element.getEnclosedElements().forEach(this::visit);
        }

    }

}
