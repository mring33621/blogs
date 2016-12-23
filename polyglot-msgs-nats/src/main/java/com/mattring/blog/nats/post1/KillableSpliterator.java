package com.mattring.blog.nats.post1;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by mring on 12/11/2016.
 */
public class KillableSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

    private final Supplier<T> itemSupplier;
    private final Predicate<T> killSignalDetector;

    public KillableSpliterator(Supplier<T> itemSupplier, Predicate<T> killSignalDetector) {
        super(Long.MAX_VALUE, Spliterator.CONCURRENT);
        this.itemSupplier = itemSupplier;
        this.killSignalDetector = killSignalDetector;
    }

    public boolean tryAdvance(Consumer<? super T> action) {
        final T item = itemSupplier.get();
        if (killSignalDetector.test(item)) {
            return false;
        }
        action.accept(item);
        return true;
    }
}
