package org.graviton.collection;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Botan on 05/01/2017. 18:18
 */
public class NoDuplicatesList<E> extends LinkedList<E> {

    @Override
    public boolean add(E e) {
        if (!this.contains(e))
            return super.add(e);
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }

    @Override
    public void add(int index, E element) {
        if (!this.contains(element))
            super.add(index, element);
    }

}
