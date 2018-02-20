package com.github.old_horizon.junit4;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static javax.lang.model.type.TypeKind.NONE;

class SuperClassesStream {

    private SuperClassesStream() {
    }

    static Stream<TypeElement> of(final Types typeUtils, final TypeElement clazz) {
        final Spliterator spliterator = new Spliterator(typeUtils, clazz);
        return StreamSupport.stream(spliterator, false);
    }

    private static class Spliterator extends AbstractSpliterator<TypeElement> {

        private final Types typeUtils;

        private TypeElement clazz;

        Spliterator(final Types typeUtils, final TypeElement clazz) {
            super(Long.MAX_VALUE, 0);
            this.typeUtils = typeUtils;
            this.clazz = clazz;
        }

        @Override
        public boolean tryAdvance(final Consumer<? super TypeElement> action) {
            final TypeMirror superClazz = clazz.getSuperclass();

            if (superClazz.getKind() == NONE) {
                return false;
            }

            final Element element = typeUtils.asElement(superClazz);

            if (element == null) {
                return false;
            }

            clazz = (TypeElement) element;
            action.accept(clazz);
            return true;
        }

    }

}
