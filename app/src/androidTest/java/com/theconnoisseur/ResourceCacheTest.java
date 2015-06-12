package com.theconnoisseur;

import android.test.AndroidTestCase;

import Util.ResourceCache;

/**
 * Tests app cache used for comments,etc
 */
public class ResourceCacheTest extends AndroidTestCase {

    public void testAddsAndRemovesObjects() {
        ResourceCache<String, String> cache = new ResourceCache<String, String>(10000, 4);

        cache.put("tomek", "TOMEK");
        cache.put("alex", "ALEX");
        cache.put("dan", "DAN");
        cache.put("gil", "GIL");

        assertEquals(4, cache.size());

        cache.remove("tomek");
        assertEquals(3, cache.size());

        cache.remove("alex");
        assertEquals(2, cache.size());
    }

    public void testCleanUpsExpiredItems() {
        ResourceCache<String, String> cache = new ResourceCache<String, String>(1000, 3);

        cache.put("tomek", "TOMEK");
        cache.put("alex", "ALEX");
        cache.put("dan", "DAN");

        assertEquals(3, cache.size());

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {}

        cache.cleanUp();

        assertEquals(0, cache.size());
    }

}
