package xyz.holocons.mc.holoitemsrevamp.util;

import java.util.function.Predicate;

import org.bukkit.Bukkit;

import java.util.function.Function;

import it.unimi.dsi.fastutil.objects.Object2LongArrayMap;

public abstract class ExpiringSet<K, E> implements Predicate<K> {

    @FunctionalInterface
    public interface ExpirationPolicy<K> {

        long expirationTime(final K k);

        default long now() {
            return Integer.toUnsignedLong(Bukkit.getCurrentTick());
        }
    }

    public static class ConstantTicksToLiveExpirationPolicy<K> implements ExpirationPolicy<K> {

        private final long ticksToLive;

        public ConstantTicksToLiveExpirationPolicy(final long ticksToLive) {
            this.ticksToLive = ticksToLive;
        }

        @Override
        public long expirationTime(final K k) {
            return now() + ticksToLive;
        }
    }

    private final Object2LongArrayMap<E> delegate = new Object2LongArrayMap<>();
    private final Function<K, E> keyToEntryMapper;
    private final ExpirationPolicy<K> expirationPolicy;

    public ExpiringSet(final Function<K, E> keyToEntryMapper, final ExpirationPolicy<K> expirationPolicy) {
        delegate.defaultReturnValue(-1);
        this.keyToEntryMapper = keyToEntryMapper;
        this.expirationPolicy = expirationPolicy;
    }

    @Override
    public boolean test(final K k) {
        final var e = keyToEntryMapper.apply(k);
        removeIfExpired(e);
        return delegate.containsKey(e);
    }

    public void add(final K k) {
        final var e = keyToEntryMapper.apply(k);
        delegate.put(e, expirationPolicy.expirationTime(k));
    }

    public void remove(final K k) {
        final var e = keyToEntryMapper.apply(k);
        delegate.removeLong(e);
    }

    public void clear() {
        delegate.clear();
    }

    public boolean isEmpty() {
        removeAllExpired();
        return delegate.isEmpty();
    }

    public int size() {
        removeAllExpired();
        return delegate.size();
    }

    public long now() {
        return expirationPolicy.now();
    }

    private boolean isExpired(final E e) {
        return now() >= delegate.getLong(e);
    }

    private void removeIfExpired(final E e) {
        if (isExpired(e)) {
            delegate.removeLong(e);
        }
    }

    private void removeAllExpired() {
        for (final var e : delegate.keySet()) {
            removeIfExpired(e);
        }
    }
}
