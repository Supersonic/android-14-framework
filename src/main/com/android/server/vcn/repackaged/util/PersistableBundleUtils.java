package com.android.server.vcn.repackaged.util;

import android.p008os.ParcelUuid;
import android.p008os.PersistableBundle;
import com.android.internal.util.HexDump;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
/* loaded from: classes5.dex */
public class PersistableBundleUtils {
    private static final String BYTE_ARRAY_KEY = "BYTE_ARRAY_KEY";
    private static final String COLLECTION_SIZE_KEY = "COLLECTION_LENGTH";
    private static final String INTEGER_KEY = "INTEGER_KEY";
    private static final String LIST_KEY_FORMAT = "LIST_ITEM_%d";
    private static final String MAP_KEY_FORMAT = "MAP_KEY_%d";
    private static final String MAP_VALUE_FORMAT = "MAP_VALUE_%d";
    private static final String PARCEL_UUID_KEY = "PARCEL_UUID";
    private static final String STRING_KEY = "STRING_KEY";
    public static final Serializer<Integer> INTEGER_SERIALIZER = new Serializer() { // from class: com.android.server.vcn.repackaged.util.PersistableBundleUtils$$ExternalSyntheticLambda0
        @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
        public final PersistableBundle toPersistableBundle(Object obj) {
            return PersistableBundleUtils.lambda$static$0((Integer) obj);
        }
    };
    public static final Deserializer<Integer> INTEGER_DESERIALIZER = new Deserializer() { // from class: com.android.server.vcn.repackaged.util.PersistableBundleUtils$$ExternalSyntheticLambda1
        @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
        public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
            return Objects.requireNonNull(persistableBundle, "PersistableBundle is null");
        }
    };
    public static final Serializer<String> STRING_SERIALIZER = new Serializer() { // from class: com.android.server.vcn.repackaged.util.PersistableBundleUtils$$ExternalSyntheticLambda2
        @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Serializer
        public final PersistableBundle toPersistableBundle(Object obj) {
            return PersistableBundleUtils.lambda$static$2((String) obj);
        }
    };
    public static final Deserializer<String> STRING_DESERIALIZER = new Deserializer() { // from class: com.android.server.vcn.repackaged.util.PersistableBundleUtils$$ExternalSyntheticLambda3
        @Override // com.android.server.vcn.repackaged.util.PersistableBundleUtils.Deserializer
        public final Object fromPersistableBundle(PersistableBundle persistableBundle) {
            return Objects.requireNonNull(persistableBundle, "PersistableBundle is null");
        }
    };

    /* loaded from: classes5.dex */
    public interface Deserializer<T> {
        T fromPersistableBundle(PersistableBundle persistableBundle);
    }

    /* loaded from: classes5.dex */
    public interface Serializer<T> {
        PersistableBundle toPersistableBundle(T t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ PersistableBundle lambda$static$0(Integer i) {
        PersistableBundle result = new PersistableBundle();
        result.putInt(INTEGER_KEY, i.intValue());
        return result;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ PersistableBundle lambda$static$2(String i) {
        PersistableBundle result = new PersistableBundle();
        result.putString(STRING_KEY, i);
        return result;
    }

    public static PersistableBundle fromParcelUuid(ParcelUuid uuid) {
        PersistableBundle result = new PersistableBundle();
        result.putString(PARCEL_UUID_KEY, uuid.toString());
        return result;
    }

    public static ParcelUuid toParcelUuid(PersistableBundle bundle) {
        return ParcelUuid.fromString(bundle.getString(PARCEL_UUID_KEY));
    }

    public static <T> PersistableBundle fromList(List<T> in, Serializer<T> serializer) {
        PersistableBundle result = new PersistableBundle();
        result.putInt(COLLECTION_SIZE_KEY, in.size());
        for (int i = 0; i < in.size(); i++) {
            String key = String.format(LIST_KEY_FORMAT, Integer.valueOf(i));
            result.putPersistableBundle(key, serializer.toPersistableBundle(in.get(i)));
        }
        return result;
    }

    public static <T> List<T> toList(PersistableBundle in, Deserializer<T> deserializer) {
        int listLength = in.getInt(COLLECTION_SIZE_KEY);
        ArrayList<T> result = new ArrayList<>(listLength);
        for (int i = 0; i < listLength; i++) {
            String key = String.format(LIST_KEY_FORMAT, Integer.valueOf(i));
            PersistableBundle item = in.getPersistableBundle(key);
            result.add(deserializer.fromPersistableBundle(item));
        }
        return result;
    }

    public static PersistableBundle fromByteArray(byte[] array) {
        PersistableBundle result = new PersistableBundle();
        result.putString(BYTE_ARRAY_KEY, HexDump.toHexString(array));
        return result;
    }

    public static byte[] toByteArray(PersistableBundle bundle) {
        Objects.requireNonNull(bundle, "PersistableBundle is null");
        String hex = bundle.getString(BYTE_ARRAY_KEY);
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("PersistableBundle contains invalid byte array");
        }
        return HexDump.hexStringToByteArray(hex);
    }

    public static <K, V> PersistableBundle fromMap(Map<K, V> in, Serializer<K> keySerializer, Serializer<V> valueSerializer) {
        PersistableBundle result = new PersistableBundle();
        result.putInt(COLLECTION_SIZE_KEY, in.size());
        int i = 0;
        for (Map.Entry<K, V> entry : in.entrySet()) {
            String keyKey = String.format(MAP_KEY_FORMAT, Integer.valueOf(i));
            String valueKey = String.format(MAP_VALUE_FORMAT, Integer.valueOf(i));
            result.putPersistableBundle(keyKey, keySerializer.toPersistableBundle(entry.getKey()));
            result.putPersistableBundle(valueKey, valueSerializer.toPersistableBundle(entry.getValue()));
            i++;
        }
        return result;
    }

    public static <K, V> LinkedHashMap<K, V> toMap(PersistableBundle in, Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
        int mapSize = in.getInt(COLLECTION_SIZE_KEY);
        LinkedHashMap<K, V> result = new LinkedHashMap<>(mapSize);
        for (int i = 0; i < mapSize; i++) {
            String keyKey = String.format(MAP_KEY_FORMAT, Integer.valueOf(i));
            String valueKey = String.format(MAP_VALUE_FORMAT, Integer.valueOf(i));
            PersistableBundle keyBundle = in.getPersistableBundle(keyKey);
            PersistableBundle valueBundle = in.getPersistableBundle(valueKey);
            K key = keyDeserializer.fromPersistableBundle(keyBundle);
            V value = valueDeserializer.fromPersistableBundle(valueBundle);
            result.put(key, value);
        }
        return result;
    }

    public static byte[] toDiskStableBytes(PersistableBundle bundle) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bundle.writeToStream(outputStream);
        return outputStream.toByteArray();
    }

    public static PersistableBundle fromDiskStableBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        return PersistableBundle.readFromStream(inputStream);
    }

    /* loaded from: classes5.dex */
    public static class LockingReadWriteHelper {
        private final ReadWriteLock mDiskLock = new ReentrantReadWriteLock();
        private final String mPath;

        public LockingReadWriteHelper(String path) {
            this.mPath = (String) Objects.requireNonNull(path, "fileName was null");
        }

        public PersistableBundle readFromDisk() throws IOException {
            try {
                this.mDiskLock.readLock().lock();
                File file = new File(this.mPath);
                if (file.exists()) {
                    FileInputStream fis = new FileInputStream(file);
                    PersistableBundle readFromStream = PersistableBundle.readFromStream(fis);
                    fis.close();
                    return readFromStream;
                }
                this.mDiskLock.readLock().unlock();
                return null;
            } finally {
                this.mDiskLock.readLock().unlock();
            }
        }

        public void writeToDisk(PersistableBundle bundle) throws IOException {
            Objects.requireNonNull(bundle, "bundle was null");
            try {
                this.mDiskLock.writeLock().lock();
                File file = new File(this.mPath);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(file);
                bundle.writeToStream(fos);
                fos.close();
            } finally {
                this.mDiskLock.writeLock().unlock();
            }
        }
    }

    public static PersistableBundle minimizeBundle(PersistableBundle bundle, String... keys) {
        Object value;
        PersistableBundle minimized = new PersistableBundle();
        if (bundle == null) {
            return minimized;
        }
        for (String key : keys) {
            if (bundle.containsKey(key) && (value = bundle.get(key)) != null) {
                if (value instanceof Boolean) {
                    minimized.putBoolean(key, ((Boolean) value).booleanValue());
                } else if (value instanceof boolean[]) {
                    minimized.putBooleanArray(key, (boolean[]) value);
                } else if (value instanceof Double) {
                    minimized.putDouble(key, ((Double) value).doubleValue());
                } else if (value instanceof double[]) {
                    minimized.putDoubleArray(key, (double[]) value);
                } else if (value instanceof Integer) {
                    minimized.putInt(key, ((Integer) value).intValue());
                } else if (value instanceof int[]) {
                    minimized.putIntArray(key, (int[]) value);
                } else if (value instanceof Long) {
                    minimized.putLong(key, ((Long) value).longValue());
                } else if (value instanceof long[]) {
                    minimized.putLongArray(key, (long[]) value);
                } else if (value instanceof String) {
                    minimized.putString(key, (String) value);
                } else if (value instanceof String[]) {
                    minimized.putStringArray(key, (String[]) value);
                } else if (value instanceof PersistableBundle) {
                    minimized.putPersistableBundle(key, (PersistableBundle) value);
                }
            }
        }
        return minimized;
    }

    public static int getHashCode(PersistableBundle bundle) {
        if (bundle == null) {
            return -1;
        }
        int iterativeHashcode = 0;
        TreeSet<String> treeSet = new TreeSet<>(bundle.keySet());
        Iterator<String> it = treeSet.iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object val = bundle.get(key);
            if (val instanceof PersistableBundle) {
                iterativeHashcode = Objects.hash(Integer.valueOf(iterativeHashcode), key, Integer.valueOf(getHashCode((PersistableBundle) val)));
            } else {
                iterativeHashcode = Objects.hash(Integer.valueOf(iterativeHashcode), key, val);
            }
        }
        return iterativeHashcode;
    }

    public static boolean isEqual(PersistableBundle left, PersistableBundle right) {
        if (Objects.equals(left, right)) {
            return true;
        }
        if (Objects.isNull(left) == Objects.isNull(right) && left.keySet().equals(right.keySet())) {
            for (String key : left.keySet()) {
                Object leftVal = left.get(key);
                Object rightVal = right.get(key);
                if (!Objects.equals(leftVal, rightVal)) {
                    if (Objects.isNull(leftVal) != Objects.isNull(rightVal) || !Objects.equals(leftVal.getClass(), rightVal.getClass())) {
                        return false;
                    }
                    if (leftVal instanceof PersistableBundle) {
                        if (!isEqual((PersistableBundle) leftVal, (PersistableBundle) rightVal)) {
                            return false;
                        }
                    } else if (leftVal.getClass().isArray()) {
                        if (leftVal instanceof boolean[]) {
                            if (!Arrays.equals((boolean[]) leftVal, (boolean[]) rightVal)) {
                                return false;
                            }
                        } else if (leftVal instanceof double[]) {
                            if (!Arrays.equals((double[]) leftVal, (double[]) rightVal)) {
                                return false;
                            }
                        } else if (leftVal instanceof int[]) {
                            if (!Arrays.equals((int[]) leftVal, (int[]) rightVal)) {
                                return false;
                            }
                        } else if (leftVal instanceof long[]) {
                            if (!Arrays.equals((long[]) leftVal, (long[]) rightVal)) {
                                return false;
                            }
                        } else if (!Arrays.equals((Object[]) leftVal, (Object[]) rightVal)) {
                            return false;
                        }
                    } else if (!Objects.equals(leftVal, rightVal)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /* loaded from: classes5.dex */
    public static class PersistableBundleWrapper {
        private final PersistableBundle mBundle;

        public PersistableBundleWrapper(PersistableBundle bundle) {
            this.mBundle = (PersistableBundle) Objects.requireNonNull(bundle, "Bundle was null");
        }

        public int getInt(String key, int defaultValue) {
            return this.mBundle.getInt(key, defaultValue);
        }

        public int[] getIntArray(String key, int[] defaultValue) {
            int[] value = this.mBundle.getIntArray(key);
            return value == null ? defaultValue : value;
        }

        public int hashCode() {
            return PersistableBundleUtils.getHashCode(this.mBundle);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof PersistableBundleWrapper)) {
                return false;
            }
            PersistableBundleWrapper other = (PersistableBundleWrapper) obj;
            return PersistableBundleUtils.isEqual(this.mBundle, other.mBundle);
        }

        public String toString() {
            return this.mBundle.toString();
        }
    }
}
