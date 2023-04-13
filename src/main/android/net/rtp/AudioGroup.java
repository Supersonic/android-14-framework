package android.net.rtp;

import android.content.AttributionSource;
import android.content.Context;
import android.os.Parcel;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes.dex */
public class AudioGroup {
    public static final int MODE_ECHO_SUPPRESSION = 3;
    private static final int MODE_LAST = 3;
    public static final int MODE_MUTED = 1;
    public static final int MODE_NORMAL = 2;
    public static final int MODE_ON_HOLD = 0;
    private Context mContext;
    private int mMode;
    private long mNative;
    private final Map<AudioStream, Long> mStreams;

    private native long nativeAdd(int i, int i2, String str, int i3, String str2, int i4, Parcel parcel);

    private native void nativeRemove(long j);

    private native void nativeSendDtmf(int i);

    private native void nativeSetMode(int i);

    static {
        System.loadLibrary("rtp_jni");
    }

    @Deprecated
    public AudioGroup() {
        this(null);
    }

    public AudioGroup(Context context) {
        this.mMode = 0;
        this.mContext = context;
        this.mStreams = new HashMap();
    }

    public AudioStream[] getStreams() {
        AudioStream[] audioStreamArr;
        synchronized (this) {
            audioStreamArr = (AudioStream[]) this.mStreams.keySet().toArray(new AudioStream[this.mStreams.size()]);
        }
        return audioStreamArr;
    }

    public int getMode() {
        return this.mMode;
    }

    public void setMode(int mode) {
        if (mode < 0 || mode > 3) {
            throw new IllegalArgumentException("Invalid mode");
        }
        synchronized (this) {
            nativeSetMode(mode);
            this.mMode = mode;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void add(AudioStream stream) {
        if (!this.mStreams.containsKey(stream)) {
            try {
                AudioCodec codec = stream.getCodec();
                String codecSpec = String.format(Locale.US, "%d %s %s", Integer.valueOf(codec.type), codec.rtpmap, codec.fmtp);
                AttributionSource.ScopedParcelState attributionSourceState = this.mContext.getAttributionSource().asScopedParcelState();
                try {
                    long id = nativeAdd(stream.getMode(), stream.getSocket(), stream.getRemoteAddress().getHostAddress(), stream.getRemotePort(), codecSpec, stream.getDtmfType(), attributionSourceState.getParcel());
                    if (attributionSourceState != null) {
                        attributionSourceState.close();
                    }
                    this.mStreams.put(stream, Long.valueOf(id));
                } catch (Throwable th) {
                    if (attributionSourceState != null) {
                        try {
                            attributionSourceState.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (NullPointerException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized void remove(AudioStream stream) {
        Long id = this.mStreams.remove(stream);
        if (id != null) {
            nativeRemove(id.longValue());
        }
    }

    public void sendDtmf(int event) {
        if (event < 0 || event > 15) {
            throw new IllegalArgumentException("Invalid event");
        }
        synchronized (this) {
            nativeSendDtmf(event);
        }
    }

    public void clear() {
        AudioStream[] streams;
        for (AudioStream stream : getStreams()) {
            stream.join(null);
        }
    }

    protected void finalize() throws Throwable {
        nativeRemove(0L);
        super.finalize();
    }
}
