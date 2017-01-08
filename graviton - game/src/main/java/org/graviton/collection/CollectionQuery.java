package org.graviton.collection;

/**
 * Created by Botan on 21/12/2016. 21:50
 */

import com.google.common.collect.Ordering;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CollectionQuery<T> implements Iterable<T> {

    private Iterable<T> from;

    public CollectionQuery(Iterable<T> from) {
        this.from = from;
    }

    public static <T> CollectionQuery<T> from(Iterable<T> from) {
        return new CollectionQuery<>(from);
    }

    public CollectionQuery<T> filter(Predicate<T> predicate) {
        return from(StreamSupport.stream(from.spliterator(), false).filter(predicate).collect(Collectors.toList()));
    }

    public <E> CollectionQuery<E> transform(Function<T, E> function) {
        return from(StreamSupport.stream(from.spliterator(), false).map(function::apply).collect(Collectors.toList()));
    }

    public CollectionQuery<T> orderBy(Comparator<T> comparator) {
        return from(Ordering.from(comparator).sortedCopy(from));
    }

    private <C extends Collection<T>> C addTo(C collection) {
        from.forEach(collection::add);
        return collection;
    }

    public <A extends List<T>> A computeList(A collection) {
        return addTo(collection);
    }

    @Override
    public Iterator<T> iterator() {
        return from.iterator();
    }
}
