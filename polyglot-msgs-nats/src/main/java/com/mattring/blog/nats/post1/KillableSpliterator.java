package com.mattring.blog.nats.post1;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by mring on 12/11/2016.
 */
public class KillableSpliterator<T> extends Spliterators.AbstractSpliterator<T> {

    private final Supplier<T> itemSupplier;
    private final T poison;

    public KillableSpliterator(Supplier<T> itemSupplier, T poison) {
        super(Long.MAX_VALUE, Spliterator.CONCURRENT);
        this.itemSupplier = itemSupplier;
        this.poison = poison;
    }

    public boolean tryAdvance(Consumer<? super T> action) {
        final T item = itemSupplier.get();
        if (poison == item) {
            return false;
        }
        action.accept(item);
        return true;
    }
}
