package android.media.p007tv.tuner.frontend;

import android.annotation.SystemApi;
import android.media.p007tv.tuner.TunerVersionChecker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* renamed from: android.media.tv.tuner.frontend.FrontendSettings */
/* loaded from: classes2.dex */
public abstract class FrontendSettings {
    public static final long FEC_11_15 = 4194304;
    public static final long FEC_11_20 = 8388608;
    public static final long FEC_11_45 = 16777216;
    public static final long FEC_13_18 = 33554432;
    public static final long FEC_13_45 = 67108864;
    public static final long FEC_14_45 = 134217728;
    public static final long FEC_1_2 = 2;
    public static final long FEC_1_3 = 4;
    public static final long FEC_1_4 = 8;
    public static final long FEC_1_5 = 16;
    public static final long FEC_23_36 = 268435456;
    public static final long FEC_25_36 = 536870912;
    public static final long FEC_26_45 = 1073741824;
    public static final long FEC_28_45 = 2147483648L;
    public static final long FEC_29_45 = 4294967296L;
    public static final long FEC_2_3 = 32;
    public static final long FEC_2_5 = 64;
    public static final long FEC_2_9 = 128;
    public static final long FEC_31_45 = 8589934592L;
    public static final long FEC_32_45 = 17179869184L;
    public static final long FEC_3_4 = 256;
    public static final long FEC_3_5 = 512;
    public static final long FEC_4_15 = 2048;
    public static final long FEC_4_5 = 1024;
    public static final long FEC_5_6 = 4096;
    public static final long FEC_5_9 = 8192;
    public static final long FEC_6_7 = 16384;
    public static final long FEC_77_90 = 34359738368L;
    public static final long FEC_7_15 = 131072;
    public static final long FEC_7_8 = 32768;
    public static final long FEC_7_9 = 65536;
    public static final long FEC_8_15 = 524288;
    public static final long FEC_8_9 = 262144;
    public static final long FEC_9_10 = 1048576;
    public static final long FEC_9_20 = 2097152;
    public static final long FEC_AUTO = 1;
    public static final long FEC_UNDEFINED = 0;
    public static final int FRONTEND_SPECTRAL_INVERSION_INVERTED = 2;
    public static final int FRONTEND_SPECTRAL_INVERSION_NORMAL = 1;
    public static final int FRONTEND_SPECTRAL_INVERSION_UNDEFINED = 0;
    public static final int TYPE_ANALOG = 1;
    public static final int TYPE_ATSC = 2;
    public static final int TYPE_ATSC3 = 3;
    public static final int TYPE_DTMB = 10;
    public static final int TYPE_DVBC = 4;
    public static final int TYPE_DVBS = 5;
    public static final int TYPE_DVBT = 6;
    public static final int TYPE_IPTV = 11;
    public static final int TYPE_ISDBS = 7;
    public static final int TYPE_ISDBS3 = 8;
    public static final int TYPE_ISDBT = 9;
    public static final int TYPE_UNDEFINED = 0;
    private final long mFrequency;
    private long mEndFrequency = -1;
    private int mSpectralInversion = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendSettings$FrontendSpectralInversion */
    /* loaded from: classes2.dex */
    public @interface FrontendSpectralInversion {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendSettings$InnerFec */
    /* loaded from: classes2.dex */
    public @interface InnerFec {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.tuner.frontend.FrontendSettings$Type */
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    public abstract int getType();

    /* JADX INFO: Access modifiers changed from: package-private */
    public FrontendSettings(long frequency) {
        this.mFrequency = frequency;
    }

    @Deprecated
    public int getFrequency() {
        return (int) getFrequencyLong();
    }

    public long getFrequencyLong() {
        return this.mFrequency;
    }

    @Deprecated
    public int getEndFrequency() {
        return (int) getEndFrequencyLong();
    }

    public long getEndFrequencyLong() {
        return this.mEndFrequency;
    }

    public int getFrontendSpectralInversion() {
        return this.mSpectralInversion;
    }

    public void setSpectralInversion(int inversion) {
        if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setSpectralInversion")) {
            this.mSpectralInversion = inversion;
        }
    }

    @Deprecated
    public void setEndFrequency(int frequency) {
        setEndFrequencyLong(frequency);
    }

    public void setEndFrequencyLong(long endFrequency) {
        if (TunerVersionChecker.checkHigherOrEqualVersionTo(65537, "setEndFrequency")) {
            if (endFrequency < 1) {
                throw new IllegalArgumentException("endFrequency must be greater than 0");
            }
            this.mEndFrequency = endFrequency;
        }
    }
}
