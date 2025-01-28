package com.ssafy.raidtest.raid.domain.room;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 동시성을 처리하는 HashSet 구현 (ConcurrentHashMap 사용)
public class ConcurrentHashSet<E> implements Set<E>{
    private final Map<E, Boolean> map = new ConcurrentHashMap<>();

    @Override
    public int size() { return map.size(); }

    @Override
    public boolean isEmpty() { return map.isEmpty(); }

    @Override
    public boolean contains(Object o) { return map.containsKey(o); }

    @Override
    public Iterator<E> iterator() { return map.keySet().iterator(); }

    @Override
    public Object[] toArray() { return map.keySet().toArray(); }

    @Override
    public <T> T[] toArray(T[] a) { return map.keySet().toArray(a); }

    @Override
    public boolean add(E e) { return map.putIfAbsent(e, Boolean.TRUE) == null; }

    @Override
    public boolean remove(Object o) { return map.remove(o) != null; }

    @Override
    public boolean containsAll(Collection<?> c) { return map.keySet().containsAll(c); }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return map.keySet().retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return map.keySet().removeAll(c);
    }

    @Override
    public void clear() { map.clear(); }
}
