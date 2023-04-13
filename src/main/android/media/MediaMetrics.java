package android.media;

import android.graphics.FontListParser;
import android.hardware.usb.UsbManager;
import android.p008os.Bundle;
import android.speech.tts.TextToSpeech;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
/* loaded from: classes2.dex */
public class MediaMetrics {
    private static final Charset MEDIAMETRICS_CHARSET = StandardCharsets.UTF_8;
    public static final String SEPARATOR = ".";
    public static final String TAG = "MediaMetrics";
    private static final int TYPE_CSTRING = 4;
    private static final int TYPE_DOUBLE = 3;
    private static final int TYPE_INT32 = 1;
    private static final int TYPE_INT64 = 2;
    private static final int TYPE_NONE = 0;
    private static final int TYPE_RATE = 5;

    /* loaded from: classes2.dex */
    public interface Key<T> {
        String getName();

        Class<T> getValueClass();
    }

    /* loaded from: classes2.dex */
    public static class Name {
        public static final String AUDIO = "audio";
        public static final String AUDIO_BLUETOOTH = "audio.bluetooth";
        public static final String AUDIO_DEVICE = "audio.device";
        public static final String AUDIO_FOCUS = "audio.focus";
        public static final String AUDIO_FORCE_USE = "audio.forceUse";
        public static final String AUDIO_MIC = "audio.mic";
        public static final String AUDIO_MIDI = "audio.midi";
        public static final String AUDIO_MODE = "audio.mode";
        public static final String AUDIO_SERVICE = "audio.service";
        public static final String AUDIO_VOLUME = "audio.volume";
        public static final String AUDIO_VOLUME_EVENT = "audio.volume.event";
        public static final String METRICS_MANAGER = "metrics.manager";
    }

    /* loaded from: classes2.dex */
    public static class Property {
        public static final Key<String> ADDRESS = MediaMetrics.createKey("address", String.class);
        public static final Key<String> ATTRIBUTES = MediaMetrics.createKey("attributes", String.class);
        public static final Key<String> CALLING_PACKAGE = MediaMetrics.createKey("callingPackage", String.class);
        public static final Key<String> CLIENT_NAME = MediaMetrics.createKey("clientName", String.class);
        public static final Key<Integer> CLOSED_COUNT = MediaMetrics.createKey("closedCount", Integer.class);
        public static final Key<Integer> DELAY_MS = MediaMetrics.createKey("delayMs", Integer.class);
        public static final Key<String> DEVICE = MediaMetrics.createKey(UsbManager.EXTRA_DEVICE, String.class);
        public static final Key<String> DEVICE_DISCONNECTED = MediaMetrics.createKey("deviceDisconnected", String.class);
        public static final Key<Integer> DEVICE_ID = MediaMetrics.createKey("deviceId", Integer.class);
        public static final Key<String> DIRECTION = MediaMetrics.createKey("direction", String.class);
        public static final Key<Long> DURATION_NS = MediaMetrics.createKey("durationNs", Long.class);
        public static final Key<String> EARLY_RETURN = MediaMetrics.createKey("earlyReturn", String.class);
        public static final Key<String> ENCODING = MediaMetrics.createKey("encoding", String.class);
        public static final Key<String> EVENT = MediaMetrics.createKey("event#", String.class);
        public static final Key<String> ENABLED = MediaMetrics.createKey("enabled", String.class);
        public static final Key<String> EXTERNAL = MediaMetrics.createKey("external", String.class);
        public static final Key<Integer> FLAGS = MediaMetrics.createKey("flags", Integer.class);
        public static final Key<String> FOCUS_CHANGE_HINT = MediaMetrics.createKey("focusChangeHint", String.class);
        public static final Key<String> FORCE_USE_DUE_TO = MediaMetrics.createKey("forceUseDueTo", String.class);
        public static final Key<String> FORCE_USE_MODE = MediaMetrics.createKey("forceUseMode", String.class);
        public static final Key<Double> GAIN_DB = MediaMetrics.createKey("gainDb", Double.class);
        public static final Key<String> GROUP = MediaMetrics.createKey("group", String.class);
        public static final Key<String> HAS_HEAD_TRACKER = MediaMetrics.createKey("hasHeadTracker", String.class);
        public static final Key<Integer> HARDWARE_TYPE = MediaMetrics.createKey("hardwareType", Integer.class);
        public static final Key<String> HEAD_TRACKER_ENABLED = MediaMetrics.createKey("headTrackerEnabled", String.class);
        public static final Key<Integer> INDEX = MediaMetrics.createKey(FontListParser.ATTR_INDEX, Integer.class);
        public static final Key<Integer> INPUT_PORT_COUNT = MediaMetrics.createKey("inputPortCount", Integer.class);
        public static final Key<String> IS_SHARED = MediaMetrics.createKey("isShared", String.class);
        public static final Key<String> LOG_SESSION_ID = MediaMetrics.createKey("logSessionId", String.class);
        public static final Key<Integer> MAX_INDEX = MediaMetrics.createKey("maxIndex", Integer.class);
        public static final Key<Integer> MIN_INDEX = MediaMetrics.createKey("minIndex", Integer.class);
        public static final Key<String> MODE = MediaMetrics.createKey("mode", String.class);
        public static final Key<String> MUTE = MediaMetrics.createKey(Value.MUTE, String.class);
        public static final Key<String> NAME = MediaMetrics.createKey("name", String.class);
        public static final Key<Integer> OBSERVERS = MediaMetrics.createKey("observers", Integer.class);
        public static final Key<Integer> OPENED_COUNT = MediaMetrics.createKey("openedCount", Integer.class);
        public static final Key<Integer> OUTPUT_PORT_COUNT = MediaMetrics.createKey("outputPortCount", Integer.class);
        public static final Key<String> REQUEST = MediaMetrics.createKey("request", String.class);
        public static final Key<String> REQUESTED_MODE = MediaMetrics.createKey("requestedMode", String.class);
        public static final Key<String> SCO_AUDIO_MODE = MediaMetrics.createKey("scoAudioMode", String.class);
        public static final Key<Integer> SDK = MediaMetrics.createKey("sdk", Integer.class);
        public static final Key<String> STATE = MediaMetrics.createKey("state", String.class);
        public static final Key<Integer> STATUS = MediaMetrics.createKey("status", Integer.class);
        public static final Key<String> STREAM_TYPE = MediaMetrics.createKey(TextToSpeech.Engine.KEY_PARAM_STREAM, String.class);
        public static final Key<String> SUPPORTS_MIDI_UMP = MediaMetrics.createKey("supportsMidiUmp", String.class);
        public static final Key<Integer> TOTAL_INPUT_BYTES = MediaMetrics.createKey("totalInputBytes", Integer.class);
        public static final Key<Integer> TOTAL_OUTPUT_BYTES = MediaMetrics.createKey("totalOutputBytes", Integer.class);
        public static final Key<String> USING_ALSA = MediaMetrics.createKey("usingAlsa", String.class);
    }

    /* loaded from: classes2.dex */
    public static class Value {
        public static final String CONNECT = "connect";
        public static final String CONNECTED = "connected";
        public static final String DISCONNECT = "disconnect";
        public static final String DISCONNECTED = "disconnected";
        public static final String DOWN = "down";
        public static final String MUTE = "mute";

        /* renamed from: NO */
        public static final String f269NO = "no";
        public static final String OFF = "off";

        /* renamed from: ON */
        public static final String f270ON = "on";
        public static final String UNMUTE = "unmute";

        /* renamed from: UP */
        public static final String f271UP = "up";
        public static final String YES = "yes";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native int native_submit_bytebuffer(ByteBuffer byteBuffer, int i);

    public static <T> Key<T> createKey(String name, Class<T> type) {
        return new Key<T>(name, type) { // from class: android.media.MediaMetrics.1
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

            @Override // android.media.MediaMetrics.Key
            public String getName() {
                return this.mName;
            }

            @Override // android.media.MediaMetrics.Key
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
    public static class Item {
        public static final String BUNDLE_HEADER_SIZE = "_headerSize";
        public static final String BUNDLE_KEY = "_key";
        public static final String BUNDLE_KEY_SIZE = "_keySize";
        public static final String BUNDLE_PID = "_pid";
        public static final String BUNDLE_PROPERTY_COUNT = "_propertyCount";
        public static final String BUNDLE_TIMESTAMP = "_timestamp";
        public static final String BUNDLE_TOTAL_SIZE = "_totalSize";
        public static final String BUNDLE_UID = "_uid";
        public static final String BUNDLE_VERSION = "_version";
        private static final int FORMAT_VERSION = 0;
        private static final int HEADER_SIZE_OFFSET = 4;
        private static final int MINIMUM_PAYLOAD_SIZE = 4;
        private static final int TOTAL_SIZE_OFFSET = 0;
        private ByteBuffer mBuffer;
        private final int mHeaderSize;
        private final String mKey;
        private final int mPidOffset;
        private int mPropertyCount;
        private final int mPropertyCountOffset;
        private final int mPropertyStartOffset;
        private final int mTimeNsOffset;
        private final int mUidOffset;

        public Item(String key) {
            this(key, -1, -1, 0L, 2048);
        }

        public Item(String key, int pid, int uid, long timeNs, int capacity) {
            this.mPropertyCount = 0;
            byte[] keyBytes = key.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            int keyLength = keyBytes.length;
            if (keyLength > 65534) {
                throw new IllegalArgumentException("Key length too large");
            }
            int i = keyLength + 12 + 1 + 4 + 4 + 8;
            this.mHeaderSize = i;
            this.mPidOffset = i - 16;
            this.mUidOffset = i - 12;
            this.mTimeNsOffset = i - 8;
            this.mPropertyCountOffset = i;
            this.mPropertyStartOffset = i + 4;
            this.mKey = key;
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(Math.max(capacity, i + 4));
            this.mBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder()).putInt(0).putInt(i).putChar((char) 0).putChar((char) (keyLength + 1)).put(keyBytes).put((byte) 0).putInt(pid).putInt(uid).putLong(timeNs);
            if (i != this.mBuffer.position()) {
                throw new IllegalStateException("Mismatched sizing");
            }
            this.mBuffer.putInt(0);
        }

        public <T> Item set(Key<T> key, T value) {
            if (value instanceof Integer) {
                putInt(key.getName(), ((Integer) value).intValue());
            } else if (value instanceof Long) {
                putLong(key.getName(), ((Long) value).longValue());
            } else if (value instanceof Double) {
                putDouble(key.getName(), ((Double) value).doubleValue());
            } else if (value instanceof String) {
                putString(key.getName(), (String) value);
            }
            return this;
        }

        public Item putInt(String key, int value) {
            byte[] keyBytes = key.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            char propSize = (char) reserveProperty(keyBytes, 4);
            int estimatedFinalPosition = this.mBuffer.position() + propSize;
            this.mBuffer.putChar(propSize).put((byte) 1).put(keyBytes).put((byte) 0).putInt(value);
            this.mPropertyCount++;
            if (this.mBuffer.position() != estimatedFinalPosition) {
                throw new IllegalStateException("Final position " + this.mBuffer.position() + " != estimatedFinalPosition " + estimatedFinalPosition);
            }
            return this;
        }

        public Item putLong(String key, long value) {
            byte[] keyBytes = key.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            char propSize = (char) reserveProperty(keyBytes, 8);
            int estimatedFinalPosition = this.mBuffer.position() + propSize;
            this.mBuffer.putChar(propSize).put((byte) 2).put(keyBytes).put((byte) 0).putLong(value);
            this.mPropertyCount++;
            if (this.mBuffer.position() != estimatedFinalPosition) {
                throw new IllegalStateException("Final position " + this.mBuffer.position() + " != estimatedFinalPosition " + estimatedFinalPosition);
            }
            return this;
        }

        public Item putDouble(String key, double value) {
            byte[] keyBytes = key.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            char propSize = (char) reserveProperty(keyBytes, 8);
            int estimatedFinalPosition = this.mBuffer.position() + propSize;
            this.mBuffer.putChar(propSize).put((byte) 3).put(keyBytes).put((byte) 0).putDouble(value);
            this.mPropertyCount++;
            if (this.mBuffer.position() != estimatedFinalPosition) {
                throw new IllegalStateException("Final position " + this.mBuffer.position() + " != estimatedFinalPosition " + estimatedFinalPosition);
            }
            return this;
        }

        public Item putString(String key, String value) {
            byte[] keyBytes = key.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            byte[] valueBytes = value.getBytes(MediaMetrics.MEDIAMETRICS_CHARSET);
            char propSize = (char) reserveProperty(keyBytes, valueBytes.length + 1);
            int estimatedFinalPosition = this.mBuffer.position() + propSize;
            this.mBuffer.putChar(propSize).put((byte) 4).put(keyBytes).put((byte) 0).put(valueBytes).put((byte) 0);
            this.mPropertyCount++;
            if (this.mBuffer.position() != estimatedFinalPosition) {
                throw new IllegalStateException("Final position " + this.mBuffer.position() + " != estimatedFinalPosition " + estimatedFinalPosition);
            }
            return this;
        }

        public Item setPid(int pid) {
            this.mBuffer.putInt(this.mPidOffset, pid);
            return this;
        }

        public Item setUid(int uid) {
            this.mBuffer.putInt(this.mUidOffset, uid);
            return this;
        }

        public Item setTimestamp(long timeNs) {
            this.mBuffer.putLong(this.mTimeNsOffset, timeNs);
            return this;
        }

        public Item clear() {
            this.mBuffer.position(this.mPropertyStartOffset);
            ByteBuffer byteBuffer = this.mBuffer;
            byteBuffer.limit(byteBuffer.capacity());
            this.mBuffer.putLong(this.mTimeNsOffset, 0L);
            this.mPropertyCount = 0;
            return this;
        }

        public boolean record() {
            updateHeader();
            ByteBuffer byteBuffer = this.mBuffer;
            return MediaMetrics.native_submit_bytebuffer(byteBuffer, byteBuffer.limit()) >= 0;
        }

        public Bundle toBundle() {
            updateHeader();
            ByteBuffer buffer = this.mBuffer.duplicate();
            buffer.order(ByteOrder.nativeOrder()).flip();
            return toBundle(buffer);
        }

        public static Bundle toBundle(ByteBuffer buffer) {
            int uid;
            long timestamp;
            Bundle bundle = new Bundle();
            int totalSize = buffer.getInt();
            int headerSize = buffer.getInt();
            char version = buffer.getChar();
            char keySize = buffer.getChar();
            if (totalSize < 0 || headerSize < 0) {
                throw new IllegalArgumentException("Item size cannot be > 2147483647");
            }
            if (keySize > 0) {
                String key = getStringFromBuffer(buffer, keySize);
                int pid = buffer.getInt();
                int uid2 = buffer.getInt();
                long timestamp2 = buffer.getLong();
                int headerRead = buffer.position();
                if (version == 0) {
                    if (headerRead != headerSize) {
                        throw new IllegalArgumentException("Item key:" + key + " headerRead:" + headerRead + " != headerSize:" + headerSize);
                    }
                } else if (headerRead > headerSize) {
                    throw new IllegalArgumentException("Item key:" + key + " headerRead:" + headerRead + " > headerSize:" + headerSize);
                } else {
                    if (headerRead < headerSize) {
                        buffer.position(headerSize);
                    }
                }
                int propertyCount = buffer.getInt();
                if (propertyCount >= 0) {
                    bundle.putInt(BUNDLE_TOTAL_SIZE, totalSize);
                    bundle.putInt(BUNDLE_HEADER_SIZE, headerSize);
                    bundle.putChar(BUNDLE_VERSION, version);
                    bundle.putChar(BUNDLE_KEY_SIZE, keySize);
                    bundle.putString(BUNDLE_KEY, key);
                    bundle.putInt(BUNDLE_PID, pid);
                    bundle.putInt(BUNDLE_UID, uid2);
                    bundle.putLong(BUNDLE_TIMESTAMP, timestamp2);
                    bundle.putInt(BUNDLE_PROPERTY_COUNT, propertyCount);
                    int i = 0;
                    while (i < propertyCount) {
                        int initialBufferPosition = buffer.position();
                        char propSize = buffer.getChar();
                        char keySize2 = keySize;
                        byte type = buffer.get();
                        int pid2 = pid;
                        String propKey = getStringFromBuffer(buffer);
                        switch (type) {
                            case 0:
                                uid = uid2;
                                timestamp = timestamp2;
                                break;
                            case 1:
                                uid = uid2;
                                timestamp = timestamp2;
                                bundle.putInt(propKey, buffer.getInt());
                                break;
                            case 2:
                                uid = uid2;
                                timestamp = timestamp2;
                                bundle.putLong(propKey, buffer.getLong());
                                break;
                            case 3:
                                uid = uid2;
                                timestamp = timestamp2;
                                bundle.putDouble(propKey, buffer.getDouble());
                                break;
                            case 4:
                                uid = uid2;
                                bundle.putString(propKey, getStringFromBuffer(buffer));
                                timestamp = timestamp2;
                                break;
                            case 5:
                                buffer.getLong();
                                buffer.getLong();
                                uid = uid2;
                                timestamp = timestamp2;
                                break;
                            default:
                                uid = uid2;
                                timestamp = timestamp2;
                                if (version != 0) {
                                    buffer.position(initialBufferPosition + propSize);
                                    break;
                                } else {
                                    throw new IllegalArgumentException("Property " + propKey + " has unsupported type " + ((int) type));
                                }
                        }
                        int deltaPosition = buffer.position() - initialBufferPosition;
                        if (deltaPosition == propSize) {
                            i++;
                            keySize = keySize2;
                            pid = pid2;
                            uid2 = uid;
                            timestamp2 = timestamp;
                        } else {
                            throw new IllegalArgumentException("propSize:" + propSize + " != deltaPosition:" + deltaPosition);
                        }
                    }
                    int finalPosition = buffer.position();
                    if (finalPosition != totalSize) {
                        throw new IllegalArgumentException("totalSize:" + totalSize + " != finalPosition:" + finalPosition);
                    }
                    return bundle;
                }
                throw new IllegalArgumentException("Cannot have more than 2147483647 properties");
            }
            throw new IllegalArgumentException("Illegal null key");
        }

        private int reserveProperty(byte[] keyBytes, int payloadSize) {
            int keyLength = keyBytes.length;
            if (keyLength > 65535) {
                throw new IllegalStateException("property key too long " + new String(keyBytes, MediaMetrics.MEDIAMETRICS_CHARSET));
            }
            if (payloadSize > 65535) {
                throw new IllegalStateException("payload too large " + payloadSize);
            }
            int size = keyLength + 3 + 1 + payloadSize;
            if (size > 65535) {
                throw new IllegalStateException("Item property " + new String(keyBytes, MediaMetrics.MEDIAMETRICS_CHARSET) + " is too large to send");
            }
            if (this.mBuffer.remaining() < size) {
                int newCapacity = this.mBuffer.position() + size;
                if (newCapacity > 1073741823) {
                    throw new IllegalStateException("Item memory requirements too large: " + newCapacity);
                }
                ByteBuffer buffer = ByteBuffer.allocateDirect(newCapacity << 1);
                buffer.order(ByteOrder.nativeOrder());
                this.mBuffer.flip();
                buffer.put(this.mBuffer);
                this.mBuffer = buffer;
            }
            return size;
        }

        private static String getStringFromBuffer(ByteBuffer buffer) {
            return getStringFromBuffer(buffer, Integer.MAX_VALUE);
        }

        private static String getStringFromBuffer(ByteBuffer buffer, int size) {
            int i = buffer.position();
            int limit = buffer.limit();
            if (size < Integer.MAX_VALUE - i && i + size < limit) {
                limit = i + size;
            }
            while (i < limit) {
                if (buffer.get(i) != 0) {
                    i++;
                } else {
                    int newPosition = i + 1;
                    if (size != Integer.MAX_VALUE && newPosition - buffer.position() != size) {
                        throw new IllegalArgumentException("chars consumed at " + i + ": " + (newPosition - buffer.position()) + " != size: " + size);
                    }
                    if (buffer.hasArray()) {
                        String found = new String(buffer.array(), buffer.position() + buffer.arrayOffset(), i - buffer.position(), MediaMetrics.MEDIAMETRICS_CHARSET);
                        buffer.position(newPosition);
                        return found;
                    }
                    byte[] array = new byte[i - buffer.position()];
                    buffer.get(array);
                    String found2 = new String(array, MediaMetrics.MEDIAMETRICS_CHARSET);
                    buffer.get();
                    return found2;
                }
            }
            throw new IllegalArgumentException("No zero termination found in string position: " + buffer.position() + " end: " + i);
        }

        private void updateHeader() {
            ByteBuffer byteBuffer = this.mBuffer;
            byteBuffer.putInt(0, byteBuffer.position()).putInt(this.mPropertyCountOffset, (char) this.mPropertyCount);
        }
    }
}
