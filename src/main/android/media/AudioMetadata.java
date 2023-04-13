package android.media;

import android.util.Log;
import android.util.Pair;
import java.lang.reflect.ParameterizedType;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class AudioMetadata {
    private static final int AUDIO_METADATA_OBJ_TYPE_BASEMAP = 6;
    private static final int AUDIO_METADATA_OBJ_TYPE_DOUBLE = 4;
    private static final int AUDIO_METADATA_OBJ_TYPE_FLOAT = 3;
    private static final int AUDIO_METADATA_OBJ_TYPE_INT = 1;
    private static final int AUDIO_METADATA_OBJ_TYPE_LONG = 2;
    private static final int AUDIO_METADATA_OBJ_TYPE_NONE = 0;
    private static final int AUDIO_METADATA_OBJ_TYPE_STRING = 5;
    private static final String TAG = "AudioMetadata";
    private static final Map<Class, Integer> AUDIO_METADATA_OBJ_TYPES = Map.of(Integer.class, 1, Long.class, 2, Float.class, 3, Double.class, 4, String.class, 5, BaseMap.class, 6);
    private static final Charset AUDIO_METADATA_CHARSET = StandardCharsets.UTF_8;
    private static final Map<Integer, DataPackage<?>> DATA_PACKAGES = Map.of(1, new DataPackage<Integer>() { // from class: android.media.AudioMetadata.2
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public Integer unpack(ByteBuffer buffer) {
            return Integer.valueOf(buffer.getInt());
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, Integer obj) {
            output.putInt(obj.intValue());
            return true;
        }
    }, 2, new DataPackage<Long>() { // from class: android.media.AudioMetadata.3
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public Long unpack(ByteBuffer buffer) {
            return Long.valueOf(buffer.getLong());
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, Long obj) {
            output.putLong(obj.longValue());
            return true;
        }
    }, 3, new DataPackage<Float>() { // from class: android.media.AudioMetadata.4
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public Float unpack(ByteBuffer buffer) {
            return Float.valueOf(buffer.getFloat());
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, Float obj) {
            output.putFloat(obj.floatValue());
            return true;
        }
    }, 4, new DataPackage<Double>() { // from class: android.media.AudioMetadata.5
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public Double unpack(ByteBuffer buffer) {
            return Double.valueOf(buffer.getDouble());
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, Double obj) {
            output.putDouble(obj.doubleValue());
            return true;
        }
    }, 5, new DataPackage<String>() { // from class: android.media.AudioMetadata.6
        @Override // android.media.AudioMetadata.DataPackage
        public String unpack(ByteBuffer buffer) {
            int dataSize = buffer.getInt();
            if (buffer.position() + dataSize > buffer.limit()) {
                return null;
            }
            byte[] valueArr = new byte[dataSize];
            buffer.get(valueArr);
            String value = new String(valueArr, AudioMetadata.AUDIO_METADATA_CHARSET);
            return value;
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, String obj) {
            byte[] valueArr = obj.getBytes(AudioMetadata.AUDIO_METADATA_CHARSET);
            output.putInt(valueArr.length);
            output.put(valueArr);
            return true;
        }
    }, 6, new BaseMapPackage());
    private static final ObjectPackage OBJECT_PACKAGE = new ObjectPackage();

    /* loaded from: classes2.dex */
    public interface Key<T> {
        String getName();

        Class<T> getValueClass();
    }

    public static AudioMetadataMap createMap() {
        return new BaseMap();
    }

    /* loaded from: classes2.dex */
    public static class Format {
        public static final Key<Integer> KEY_BIT_RATE = AudioMetadata.createKey(MediaFormat.KEY_BIT_RATE, Integer.class);
        public static final Key<Integer> KEY_CHANNEL_MASK = AudioMetadata.createKey(MediaFormat.KEY_CHANNEL_MASK, Integer.class);
        public static final Key<String> KEY_MIME = AudioMetadata.createKey(MediaFormat.KEY_MIME, String.class);
        public static final Key<Integer> KEY_SAMPLE_RATE = AudioMetadata.createKey(MediaFormat.KEY_SAMPLE_RATE, Integer.class);
        public static final Key<Integer> KEY_BIT_WIDTH = AudioMetadata.createKey("bit-width", Integer.class);
        public static final Key<Boolean> KEY_ATMOS_PRESENT = AudioMetadata.createKey("atmos-present", Boolean.class);
        public static final Key<Integer> KEY_HAS_ATMOS = AudioMetadata.createKey("has-atmos", Integer.class);
        public static final Key<Integer> KEY_AUDIO_ENCODING = AudioMetadata.createKey("audio-encoding", Integer.class);
        public static final Key<Integer> KEY_PRESENTATION_ID = AudioMetadata.createKey("presentation-id", Integer.class);
        public static final Key<Integer> KEY_PROGRAM_ID = AudioMetadata.createKey("program-id", Integer.class);
        public static final Key<Integer> KEY_PRESENTATION_CONTENT_CLASSIFIER = AudioMetadata.createKey("presentation-content-classifier", Integer.class);
        public static final Key<String> KEY_PRESENTATION_LANGUAGE = AudioMetadata.createKey("presentation-language", String.class);

        private Format() {
        }
    }

    public static <T> Key<T> createKey(String name, Class<T> type) {
        return new Key<T>(name, type) { // from class: android.media.AudioMetadata.1
            private final String mName;
            private final Class<T> mType;
            final /* synthetic */ String val$name;
            final /* synthetic */ Class val$type;

            {
                this.val$name = name;
                this.val$type = type;
                this.mName = name;
                this.mType = type;
            }

            @Override // android.media.AudioMetadata.Key
            public String getName() {
                return this.mName;
            }

            @Override // android.media.AudioMetadata.Key
            public Class<T> getValueClass() {
                return this.mType;
            }

            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj instanceof Key) {
                    Key<?> other = (Key) obj;
                    return this.mName.equals(other.getName()) && this.mType.equals(other.getValueClass());
                }
                return false;
            }

            public int hashCode() {
                return Objects.hash(this.mName, this.mType);
            }
        };
    }

    /* loaded from: classes2.dex */
    public static class BaseMap implements AudioMetadataMap {
        private final HashMap<Pair<String, Class<?>>, Pair<Key<?>, Object>> mHashMap = new HashMap<>();

        @Override // android.media.AudioMetadataReadMap
        public <T> boolean containsKey(Key<T> key) {
            Pair<Key<?>, Object> valuePair = this.mHashMap.get(pairFromKey(key));
            return valuePair != null;
        }

        @Override // android.media.AudioMetadataReadMap
        public AudioMetadataMap dup() {
            BaseMap map = new BaseMap();
            map.mHashMap.putAll(this.mHashMap);
            return map;
        }

        @Override // android.media.AudioMetadataReadMap
        public <T> T get(Key<T> key) {
            Pair<Key<?>, Object> valuePair = this.mHashMap.get(pairFromKey(key));
            return (T) getValueFromValuePair(valuePair);
        }

        @Override // android.media.AudioMetadataReadMap
        public Set<Key<?>> keySet() {
            HashSet<Key<?>> set = new HashSet<>();
            for (Pair<Key<?>, Object> pair : this.mHashMap.values()) {
                set.add(pair.first);
            }
            return set;
        }

        @Override // android.media.AudioMetadataMap
        public <T> T remove(Key<T> key) {
            Pair<Key<?>, Object> valuePair = this.mHashMap.remove(pairFromKey(key));
            return (T) getValueFromValuePair(valuePair);
        }

        @Override // android.media.AudioMetadataMap
        public <T> T set(Key<T> key, T value) {
            Objects.requireNonNull(value);
            Pair<Key<?>, Object> valuePair = this.mHashMap.put(pairFromKey(key), new Pair<>(key, value));
            return (T) getValueFromValuePair(valuePair);
        }

        @Override // android.media.AudioMetadataReadMap
        public int size() {
            return this.mHashMap.size();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof BaseMap)) {
                return false;
            }
            BaseMap other = (BaseMap) obj;
            return this.mHashMap.equals(other.mHashMap);
        }

        public int hashCode() {
            return Objects.hash(this.mHashMap);
        }

        private static <T> Pair<String, Class<?>> pairFromKey(Key<T> key) {
            Objects.requireNonNull(key);
            return new Pair<>(key.getName(), key.getValueClass());
        }

        private static Object getValueFromValuePair(Pair<Key<?>, Object> valuePair) {
            if (valuePair == null) {
                return null;
            }
            return valuePair.second;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class AutoGrowByteBuffer {
        private static final int DOUBLE_BYTE_COUNT = 8;
        private static final int FLOAT_BYTE_COUNT = 4;
        private static final int INTEGER_BYTE_COUNT = 4;
        private static final int LONG_BYTE_COUNT = 8;
        private ByteBuffer mBuffer;

        AutoGrowByteBuffer() {
            this(1024);
        }

        AutoGrowByteBuffer(int initialCapacity) {
            this.mBuffer = ByteBuffer.allocateDirect(initialCapacity);
        }

        public ByteBuffer getRawByteBuffer() {
            int limit = this.mBuffer.limit();
            int position = this.mBuffer.position();
            this.mBuffer.limit(position);
            this.mBuffer.position(0);
            ByteBuffer buffer = this.mBuffer.slice();
            this.mBuffer.limit(limit);
            this.mBuffer.position(position);
            return buffer;
        }

        public ByteOrder order() {
            return this.mBuffer.order();
        }

        public int position() {
            return this.mBuffer.position();
        }

        public AutoGrowByteBuffer position(int newPosition) {
            this.mBuffer.position(newPosition);
            return this;
        }

        public AutoGrowByteBuffer order(ByteOrder order) {
            this.mBuffer.order(order);
            return this;
        }

        public AutoGrowByteBuffer putInt(int value) {
            ensureCapacity(4);
            this.mBuffer.putInt(value);
            return this;
        }

        public AutoGrowByteBuffer putLong(long value) {
            ensureCapacity(8);
            this.mBuffer.putLong(value);
            return this;
        }

        public AutoGrowByteBuffer putFloat(float value) {
            ensureCapacity(4);
            this.mBuffer.putFloat(value);
            return this;
        }

        public AutoGrowByteBuffer putDouble(double value) {
            ensureCapacity(8);
            this.mBuffer.putDouble(value);
            return this;
        }

        public AutoGrowByteBuffer put(byte[] src) {
            ensureCapacity(src.length);
            this.mBuffer.put(src);
            return this;
        }

        private void ensureCapacity(int count) {
            if (this.mBuffer.remaining() < count) {
                int newCapacity = this.mBuffer.position() + count;
                if (newCapacity > 1073741823) {
                    throw new IllegalStateException("Item memory requirements too large: " + newCapacity);
                }
                ByteBuffer buffer = ByteBuffer.allocateDirect(newCapacity << 1);
                buffer.order(this.mBuffer.order());
                this.mBuffer.flip();
                buffer.put(this.mBuffer);
                this.mBuffer = buffer;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public interface DataPackage<T> {
        boolean pack(AutoGrowByteBuffer autoGrowByteBuffer, T t);

        T unpack(ByteBuffer byteBuffer);

        default Class getMyType() {
            return (Class) ((ParameterizedType) getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static class ObjectPackage implements DataPackage<Pair<Class, Object>> {
        private ObjectPackage() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public Pair<Class, Object> unpack(ByteBuffer buffer) {
            int dataType = buffer.getInt();
            DataPackage dataPackage = (DataPackage) AudioMetadata.DATA_PACKAGES.get(Integer.valueOf(dataType));
            if (dataPackage == null) {
                Log.m110e(AudioMetadata.TAG, "Cannot find DataPackage for type:" + dataType);
                return null;
            }
            int dataSize = buffer.getInt();
            int position = buffer.position();
            Object obj = dataPackage.unpack(buffer);
            if (buffer.position() - position != dataSize) {
                Log.m110e(AudioMetadata.TAG, "Broken data package");
                return null;
            }
            return new Pair<>(dataPackage.getMyType(), obj);
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, Pair<Class, Object> obj) {
            Integer dataType = (Integer) AudioMetadata.AUDIO_METADATA_OBJ_TYPES.get(obj.first);
            if (dataType == null) {
                Log.m110e(AudioMetadata.TAG, "Cannot find data type for " + obj.first);
                return false;
            }
            DataPackage dataPackage = (DataPackage) AudioMetadata.DATA_PACKAGES.get(dataType);
            if (dataPackage == null) {
                Log.m110e(AudioMetadata.TAG, "Cannot find DataPackage for type:" + dataType);
                return false;
            }
            output.putInt(dataType.intValue());
            int position = output.position();
            output.putInt(0);
            int payloadIdx = output.position();
            if (!dataPackage.pack(output, obj.second)) {
                Log.m108i(AudioMetadata.TAG, "Failed to pack object: " + obj.second);
                return false;
            }
            int currentPosition = output.position();
            output.position(position);
            output.putInt(currentPosition - payloadIdx);
            output.position(currentPosition);
            return true;
        }
    }

    /* loaded from: classes2.dex */
    private static class BaseMapPackage implements DataPackage<BaseMap> {
        private BaseMapPackage() {
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.media.AudioMetadata.DataPackage
        public BaseMap unpack(ByteBuffer buffer) {
            BaseMap ret = new BaseMap();
            int mapSize = buffer.getInt();
            DataPackage<String> strDataPackage = (DataPackage) AudioMetadata.DATA_PACKAGES.get(5);
            if (strDataPackage == null) {
                Log.m110e(AudioMetadata.TAG, "Cannot find DataPackage for String");
                return null;
            }
            for (int i = 0; i < mapSize; i++) {
                String key = strDataPackage.unpack(buffer);
                if (key == null) {
                    Log.m110e(AudioMetadata.TAG, "Failed to unpack key for map");
                    return null;
                }
                Pair<Class, Object> value = AudioMetadata.OBJECT_PACKAGE.unpack(buffer);
                if (value == null) {
                    Log.m110e(AudioMetadata.TAG, "Failed to unpack value for map");
                    return null;
                }
                if (key.equals(Format.KEY_HAS_ATMOS.getName()) && value.first == Format.KEY_HAS_ATMOS.getValueClass()) {
                    ret.set(Format.KEY_ATMOS_PRESENT, Boolean.valueOf(((Integer) value.second).intValue() != 0));
                } else {
                    ret.set(AudioMetadata.createKey(key, value.first), value.first.cast(value.second));
                }
            }
            return ret;
        }

        @Override // android.media.AudioMetadata.DataPackage
        public boolean pack(AutoGrowByteBuffer output, BaseMap obj) {
            output.putInt(obj.size());
            DataPackage<String> strDataPackage = (DataPackage) AudioMetadata.DATA_PACKAGES.get(5);
            if (strDataPackage == null) {
                Log.m110e(AudioMetadata.TAG, "Cannot find DataPackage for String");
                return false;
            }
            Iterator<Key<?>> it = obj.keySet().iterator();
            while (it.hasNext()) {
                Key<?> key = it.next();
                Object value = obj.get(key);
                if (key == Format.KEY_ATMOS_PRESENT) {
                    key = Format.KEY_HAS_ATMOS;
                    value = Integer.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
                }
                if (!strDataPackage.pack(output, key.getName())) {
                    Log.m108i(AudioMetadata.TAG, "Failed to pack key: " + key.getName());
                    return false;
                } else if (!AudioMetadata.OBJECT_PACKAGE.pack(output, new Pair<>(key.getValueClass(), value))) {
                    Log.m108i(AudioMetadata.TAG, "Failed to pack value: " + obj.get(key));
                    return false;
                }
            }
            return true;
        }
    }

    public static BaseMap fromByteBuffer(ByteBuffer buffer) {
        DataPackage dataPackage = DATA_PACKAGES.get(6);
        if (dataPackage == null) {
            Log.m110e(TAG, "Cannot find DataPackage for BaseMap");
            return null;
        }
        try {
            return (BaseMap) dataPackage.unpack(buffer);
        } catch (BufferUnderflowException e) {
            Log.m110e(TAG, "No enough data to unpack");
            return null;
        }
    }

    public static ByteBuffer toByteBuffer(BaseMap data, ByteOrder order) {
        DataPackage dataPackage = DATA_PACKAGES.get(6);
        if (dataPackage == null) {
            Log.m110e(TAG, "Cannot find DataPackage for BaseMap");
            return null;
        }
        AutoGrowByteBuffer output = new AutoGrowByteBuffer();
        output.order(order);
        if (!dataPackage.pack(output, data)) {
            return null;
        }
        return output.getRawByteBuffer();
    }

    private AudioMetadata() {
    }
}
