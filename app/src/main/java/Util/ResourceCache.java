package Util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ResourceCache<K, T> {
    private final int TIME_TO_LIVE = (2 * 60 * 1000); //2 minutes

    private ResourceCache mInstance;

    private Map mCache;
    private long mTimeToLive;

    protected class CacheItem {
        public long lastAccessed = System.currentTimeMillis();
        public T value;

        protected CacheItem(T value) {
            this.value = value;
        }
    }

    public ResourceCache(long timeToLive, int maxItems) {
        this.mTimeToLive = timeToLive;

        mCache = new HashMap(maxItems);
    }

    public void put(K key, T value) {
        synchronized (mCache) {
            mCache.put(key, new CacheItem(value));
        }
    }

    public T get(K key) {
        synchronized (mCache) {
            CacheItem c = (CacheItem) mCache.get(key);
            if (c == null) {
                return null;
            } else {
                c.lastAccessed = System.currentTimeMillis();
                return c.value;
            }
        }
    }

    public void remove(K key) {
        synchronized (mCache) {
            mCache.remove(key);
        }
    }

    public int size() {
        synchronized (mCache) {
            return mCache.size();
        }
    }

    public void cleanUp() {
        long now = System.currentTimeMillis();
        ArrayList<K> keys = null;

        synchronized (mCache) {
            Iterator itr =  mCache.keySet().iterator();

            keys = new ArrayList<K>(mCache.size());
            K key = null;
            CacheItem c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (CacheItem) mCache.get(key);

                if (c != null && (now > (mTimeToLive + c.lastAccessed))) {
                    keys.add(key);
                }
            }
        }

        for (K key : keys) {
            synchronized (mCache) {
                mCache.remove(key);
            }

            Thread.yield();
        }
    }

}
