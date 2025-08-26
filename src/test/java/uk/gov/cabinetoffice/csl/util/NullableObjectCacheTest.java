package uk.gov.cabinetoffice.csl.util;

import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NullableObjectCacheTest {

    private record TestObject(String id) implements Cacheable {

        @Override
        public String getCacheableId() {
            return id;
        }
    }

    private static class NullableTestObject extends NullableCacheObject<TestObject> {

        public NullableTestObject(String cacheableId, TestObject object) {
            super(cacheableId, object);
        }
    }

    private final Class<NullableTestObject> clazz = NullableTestObject.class;

    @Test
    void testGetMultipleValues() {
        ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>();
        Stream.of(
                new NullableTestObject("id", new TestObject("id")),
                new NullableTestObject("id2", new TestObject("id2")),
                new NullableTestObject("id3", null),
                new NullableTestObject("id4", null)
        ).forEach(o -> map.put(o.getCacheableId(), o));

        Cache mockCache = new ConcurrentMapCache("test", map, true);

        NullableObjectCache<TestObject, NullableTestObject> cache = new NullableObjectCache<>(mockCache, clazz) {
            @Override
            NullableTestObject createCacheObject(String id, TestObject value) {
                return new NullableTestObject(id, value);
            }
        };

        CacheGetMultipleOp<TestObject> result = cache.getMultipleValues(List.of("id", "id2", "id3", "id4", "id5", "id6"));

        assertEquals(2, result.getCacheHits().size());
        assertEquals(2, result.getCacheMisses().size());

        NullableTestObject res5 = (NullableTestObject) map.get("id5");
        assertEquals("id5", res5.getCacheableId());
        assertTrue(res5.getCacheable().isEmpty());

        NullableTestObject res6 = (NullableTestObject) map.get("id6");
        assertEquals("id6", res6.getCacheableId());
        assertTrue(res6.getCacheable().isEmpty());
    }
}
