package helper;

import java.util.Map;

public class PatternEntry<K,V> implements Map.Entry<K,V>{
    private final K key;
    private V value;

    public PatternEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }
}
