package android.media.p007tv.tuner.filter;

import android.annotation.SystemApi;
@SystemApi
/* renamed from: android.media.tv.tuner.filter.AudioDescriptor */
/* loaded from: classes2.dex */
public class AudioDescriptor {
    private final byte mAdFade;
    private final byte mAdGainCenter;
    private final byte mAdGainFront;
    private final byte mAdGainSurround;
    private final byte mAdPan;
    private final char mVersionTextTag;

    private AudioDescriptor(byte adFade, byte adPan, char versionTextTag, byte adGainCenter, byte adGainFront, byte adGainSurround) {
        this.mAdFade = adFade;
        this.mAdPan = adPan;
        this.mVersionTextTag = versionTextTag;
        this.mAdGainCenter = adGainCenter;
        this.mAdGainFront = adGainFront;
        this.mAdGainSurround = adGainSurround;
    }

    public byte getAdFade() {
        return this.mAdFade;
    }

    public byte getAdPan() {
        return this.mAdPan;
    }

    public char getAdVersionTextTag() {
        return this.mVersionTextTag;
    }

    public byte getAdGainCenter() {
        return this.mAdGainCenter;
    }

    public byte getAdGainFront() {
        return this.mAdGainFront;
    }

    public byte getAdGainSurround() {
        return this.mAdGainSurround;
    }
}
