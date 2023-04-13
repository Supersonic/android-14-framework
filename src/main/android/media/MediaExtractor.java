package android.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.DrmInitData;
import android.media.MediaCas;
import android.media.MediaCodec;
import android.media.metrics.LogSessionId;
import android.net.Uri;
import android.p008os.IBinder;
import android.p008os.IHwBinder;
import android.p008os.PersistableBundle;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public final class MediaExtractor {
    public static final int SAMPLE_FLAG_ENCRYPTED = 2;
    public static final int SAMPLE_FLAG_PARTIAL_FRAME = 4;
    public static final int SAMPLE_FLAG_SYNC = 1;
    public static final int SEEK_TO_CLOSEST_SYNC = 2;
    public static final int SEEK_TO_NEXT_SYNC = 1;
    public static final int SEEK_TO_PREVIOUS_SYNC = 0;
    private LogSessionId mLogSessionId = LogSessionId.LOG_SESSION_ID_NONE;
    private MediaCas mMediaCas;
    private long mNativeContext;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SampleFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface SeekMode {
    }

    private native Map<String, Object> getFileFormatNative();

    private native Map<String, Object> getTrackFormatNative(int i);

    private final native void nativeSetDataSource(IBinder iBinder, String str, String[] strArr, String[] strArr2) throws IOException;

    private final native void nativeSetMediaCas(IHwBinder iHwBinder);

    private final native void native_finalize();

    private native List<AudioPresentation> native_getAudioPresentations(int i);

    private native PersistableBundle native_getMetrics();

    private static final native void native_init();

    private native void native_setLogSessionId(String str);

    private final native void native_setup();

    public native boolean advance();

    public native long getCachedDuration();

    public native boolean getSampleCryptoInfo(MediaCodec.CryptoInfo cryptoInfo);

    public native int getSampleFlags();

    public native long getSampleSize();

    public native long getSampleTime();

    public native int getSampleTrackIndex();

    public final native int getTrackCount();

    public native boolean hasCacheReachedEndOfStream();

    public native int readSampleData(ByteBuffer byteBuffer, int i);

    public final native void release();

    public native void seekTo(long j, int i);

    public native void selectTrack(int i);

    public final native void setDataSource(MediaDataSource mediaDataSource) throws IOException;

    public final native void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IOException;

    public native void unselectTrack(int i);

    public MediaExtractor() {
        native_setup();
    }

    /* JADX WARN: Code restructure failed: missing block: B:26:0x0054, code lost:
        if (0 == 0) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0056, code lost:
        r1.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x005b, code lost:
        if (0 == 0) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:31:0x005e, code lost:
        setDataSource(r11.toString(), r12);
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0065, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException {
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file")) {
            setDataSource(uri.getPath());
            return;
        }
        AssetFileDescriptor fd = null;
        try {
            ContentResolver resolver = context.getContentResolver();
            AssetFileDescriptor fd2 = resolver.openAssetFileDescriptor(uri, "r");
            if (fd2 == null) {
                if (fd2 != null) {
                    fd2.close();
                    return;
                }
                return;
            }
            if (fd2.getDeclaredLength() < 0) {
                setDataSource(fd2.getFileDescriptor());
            } else {
                setDataSource(fd2.getFileDescriptor(), fd2.getStartOffset(), fd2.getDeclaredLength());
            }
            if (fd2 != null) {
                fd2.close();
            }
        } catch (IOException e) {
        } catch (SecurityException e2) {
        } catch (Throwable th) {
            if (0 != 0) {
                fd.close();
            }
            throw th;
        }
    }

    public final void setDataSource(String path, Map<String, String> headers) throws IOException {
        String[] keys = null;
        String[] values = null;
        if (headers != null) {
            keys = new String[headers.size()];
            values = new String[headers.size()];
            int i = 0;
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                keys[i] = entry.getKey();
                values[i] = entry.getValue();
                i++;
            }
        }
        nativeSetDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(path), path, keys, values);
    }

    public final void setDataSource(String path) throws IOException {
        nativeSetDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(path), path, null, null);
    }

    public final void setDataSource(AssetFileDescriptor afd) throws IOException, IllegalArgumentException, IllegalStateException {
        Preconditions.checkNotNull(afd);
        if (afd.getDeclaredLength() < 0) {
            setDataSource(afd.getFileDescriptor());
        } else {
            setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
        }
    }

    public final void setDataSource(FileDescriptor fd) throws IOException {
        setDataSource(fd, 0L, 576460752303423487L);
    }

    @Deprecated
    public final void setMediaCas(MediaCas mediaCas) {
        this.mMediaCas = mediaCas;
        nativeSetMediaCas(mediaCas.getBinder());
    }

    /* loaded from: classes2.dex */
    public static final class CasInfo {
        private final byte[] mPrivateData;
        private final MediaCas.Session mSession;
        private final int mSystemId;

        CasInfo(int systemId, MediaCas.Session session, byte[] privateData) {
            this.mSystemId = systemId;
            this.mSession = session;
            this.mPrivateData = privateData;
        }

        public int getSystemId() {
            return this.mSystemId;
        }

        public byte[] getPrivateData() {
            return this.mPrivateData;
        }

        public MediaCas.Session getSession() {
            return this.mSession;
        }
    }

    public CasInfo getCasInfo(int index) {
        Map<String, Object> formatMap = getTrackFormatNative(index);
        if (formatMap.containsKey(MediaFormat.KEY_CA_SYSTEM_ID)) {
            int systemId = ((Integer) formatMap.get(MediaFormat.KEY_CA_SYSTEM_ID)).intValue();
            MediaCas.Session session = null;
            byte[] privateData = null;
            if (formatMap.containsKey(MediaFormat.KEY_CA_PRIVATE_DATA)) {
                ByteBuffer buf = (ByteBuffer) formatMap.get(MediaFormat.KEY_CA_PRIVATE_DATA);
                buf.rewind();
                privateData = new byte[buf.remaining()];
                buf.get(privateData);
            }
            if (this.mMediaCas != null && formatMap.containsKey(MediaFormat.KEY_CA_SESSION_ID)) {
                ByteBuffer buf2 = (ByteBuffer) formatMap.get(MediaFormat.KEY_CA_SESSION_ID);
                buf2.rewind();
                byte[] sessionId = new byte[buf2.remaining()];
                buf2.get(sessionId);
                session = this.mMediaCas.createFromSessionId(sessionId);
            }
            return new CasInfo(systemId, session, privateData);
        }
        return null;
    }

    protected void finalize() {
        native_finalize();
    }

    public DrmInitData getDrmInitData() {
        Map<String, Object> formatMap = getFileFormatNative();
        if (formatMap == null) {
            return null;
        }
        if (formatMap.containsKey("pssh")) {
            Map<UUID, byte[]> psshMap = getPsshInfo();
            final DrmInitData.SchemeInitData[] schemeInitDatas = (DrmInitData.SchemeInitData[]) psshMap.entrySet().stream().map(new Function() { // from class: android.media.MediaExtractor$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return MediaExtractor.lambda$getDrmInitData$0((Map.Entry) obj);
                }
            }).toArray(new IntFunction() { // from class: android.media.MediaExtractor$$ExternalSyntheticLambda1
                @Override // java.util.function.IntFunction
                public final Object apply(int i) {
                    return MediaExtractor.lambda$getDrmInitData$1(i);
                }
            });
            final Map<UUID, DrmInitData.SchemeInitData> initDataMap = (Map) Arrays.stream(schemeInitDatas).collect(Collectors.toMap(new Function() { // from class: android.media.MediaExtractor$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    UUID uuid;
                    uuid = ((DrmInitData.SchemeInitData) obj).uuid;
                    return uuid;
                }
            }, new Function() { // from class: android.media.MediaExtractor$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return MediaExtractor.lambda$getDrmInitData$3((DrmInitData.SchemeInitData) obj);
                }
            }));
            return new DrmInitData() { // from class: android.media.MediaExtractor.1
                @Override // android.media.DrmInitData
                public DrmInitData.SchemeInitData get(UUID schemeUuid) {
                    return (DrmInitData.SchemeInitData) initDataMap.get(schemeUuid);
                }

                @Override // android.media.DrmInitData
                public int getSchemeInitDataCount() {
                    return schemeInitDatas.length;
                }

                @Override // android.media.DrmInitData
                public DrmInitData.SchemeInitData getSchemeInitDataAt(int index) {
                    return schemeInitDatas[index];
                }
            };
        }
        int numTracks = getTrackCount();
        for (int i = 0; i < numTracks; i++) {
            Map<String, Object> trackFormatMap = getTrackFormatNative(i);
            if (trackFormatMap.containsKey("crypto-key")) {
                ByteBuffer buf = (ByteBuffer) trackFormatMap.get("crypto-key");
                buf.rewind();
                byte[] data = new byte[buf.remaining()];
                buf.get(data);
                final DrmInitData.SchemeInitData webmSchemeInitData = new DrmInitData.SchemeInitData(DrmInitData.SchemeInitData.UUID_NIL, "webm", data);
                return new DrmInitData() { // from class: android.media.MediaExtractor.2
                    @Override // android.media.DrmInitData
                    public DrmInitData.SchemeInitData get(UUID schemeUuid) {
                        return webmSchemeInitData;
                    }

                    @Override // android.media.DrmInitData
                    public int getSchemeInitDataCount() {
                        return 1;
                    }

                    @Override // android.media.DrmInitData
                    public DrmInitData.SchemeInitData getSchemeInitDataAt(int index) {
                        return webmSchemeInitData;
                    }
                };
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ DrmInitData.SchemeInitData lambda$getDrmInitData$0(Map.Entry entry) {
        return new DrmInitData.SchemeInitData((UUID) entry.getKey(), "cenc", (byte[]) entry.getValue());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ DrmInitData.SchemeInitData[] lambda$getDrmInitData$1(int x$0) {
        return new DrmInitData.SchemeInitData[x$0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ DrmInitData.SchemeInitData lambda$getDrmInitData$3(DrmInitData.SchemeInitData initData) {
        return initData;
    }

    public List<AudioPresentation> getAudioPresentations(int trackIndex) {
        return native_getAudioPresentations(trackIndex);
    }

    public Map<UUID, byte[]> getPsshInfo() {
        Map<UUID, byte[]> psshMap = null;
        Map<String, Object> formatMap = getFileFormatNative();
        if (formatMap != null && formatMap.containsKey("pssh")) {
            ByteBuffer rawpssh = (ByteBuffer) formatMap.get("pssh");
            rawpssh.order(ByteOrder.nativeOrder());
            rawpssh.rewind();
            formatMap.remove("pssh");
            psshMap = new HashMap<>();
            while (rawpssh.remaining() > 0) {
                rawpssh.order(ByteOrder.BIG_ENDIAN);
                long msb = rawpssh.getLong();
                long lsb = rawpssh.getLong();
                UUID uuid = new UUID(msb, lsb);
                rawpssh.order(ByteOrder.nativeOrder());
                int datalen = rawpssh.getInt();
                byte[] psshdata = new byte[datalen];
                rawpssh.get(psshdata);
                psshMap.put(uuid, psshdata);
            }
        }
        return psshMap;
    }

    public MediaFormat getTrackFormat(int index) {
        return new MediaFormat(getTrackFormatNative(index));
    }

    public void setLogSessionId(LogSessionId logSessionId) {
        this.mLogSessionId = (LogSessionId) Objects.requireNonNull(logSessionId);
        native_setLogSessionId(logSessionId.getStringId());
    }

    public LogSessionId getLogSessionId() {
        return this.mLogSessionId;
    }

    public PersistableBundle getMetrics() {
        PersistableBundle bundle = native_getMetrics();
        return bundle;
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    /* loaded from: classes2.dex */
    public static final class MetricsConstants {
        public static final String FORMAT = "android.media.mediaextractor.fmt";
        public static final String MIME_TYPE = "android.media.mediaextractor.mime";
        public static final String TRACKS = "android.media.mediaextractor.ntrk";

        private MetricsConstants() {
        }
    }
}
