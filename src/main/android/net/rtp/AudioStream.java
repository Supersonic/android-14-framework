package android.net.rtp;

import java.net.InetAddress;
import java.net.SocketException;
/* loaded from: classes.dex */
public class AudioStream extends RtpStream {
    private AudioCodec mCodec;
    private int mDtmfType;
    private AudioGroup mGroup;

    public AudioStream(InetAddress address) throws SocketException {
        super(address);
        this.mDtmfType = -1;
    }

    @Override // android.net.rtp.RtpStream
    public final boolean isBusy() {
        return this.mGroup != null;
    }

    public AudioGroup getGroup() {
        return this.mGroup;
    }

    public void join(AudioGroup group) {
        synchronized (this) {
            AudioGroup audioGroup = this.mGroup;
            if (audioGroup == group) {
                return;
            }
            if (audioGroup != null) {
                audioGroup.remove(this);
                this.mGroup = null;
            }
            if (group != null) {
                group.add(this);
                this.mGroup = group;
            }
        }
    }

    public AudioCodec getCodec() {
        return this.mCodec;
    }

    public void setCodec(AudioCodec codec) {
        if (isBusy()) {
            throw new IllegalStateException("Busy");
        }
        if (codec.type == this.mDtmfType) {
            throw new IllegalArgumentException("The type is used by DTMF");
        }
        this.mCodec = codec;
    }

    public int getDtmfType() {
        return this.mDtmfType;
    }

    public void setDtmfType(int type) {
        if (isBusy()) {
            throw new IllegalStateException("Busy");
        }
        if (type != -1) {
            if (type < 96 || type > 127) {
                throw new IllegalArgumentException("Invalid type");
            }
            AudioCodec audioCodec = this.mCodec;
            if (audioCodec != null && type == audioCodec.type) {
                throw new IllegalArgumentException("The type is used by codec");
            }
        }
        this.mDtmfType = type;
    }
}
