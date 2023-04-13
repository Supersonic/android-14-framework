package com.android.internal.telephony.protobuf.nano;

import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public final class MapFactories {
    private static volatile MapFactory mapFactory = new DefaultMapFactory();

    /* loaded from: classes.dex */
    public interface MapFactory {
        <K, V> Map<K, V> forMap(Map<K, V> map);
    }

    public static MapFactory getMapFactory() {
        return mapFactory;
    }

    /* loaded from: classes.dex */
    private static class DefaultMapFactory implements MapFactory {
        private DefaultMapFactory() {
        }

        @Override // com.android.internal.telephony.protobuf.nano.MapFactories.MapFactory
        public <K, V> Map<K, V> forMap(Map<K, V> map) {
            return map == null ? new HashMap() : map;
        }
    }

    private MapFactories() {
    }
}
