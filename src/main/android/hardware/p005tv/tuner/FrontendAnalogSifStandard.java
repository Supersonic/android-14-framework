package android.hardware.p005tv.tuner;
/* renamed from: android.hardware.tv.tuner.FrontendAnalogSifStandard */
/* loaded from: classes2.dex */
public @interface FrontendAnalogSifStandard {
    public static final int AUTO = 1;

    /* renamed from: BG */
    public static final int f234BG = 2;
    public static final int BG_A2 = 4;
    public static final int BG_NICAM = 8;

    /* renamed from: DK */
    public static final int f235DK = 32;
    public static final int DK1_A2 = 64;
    public static final int DK2_A2 = 128;
    public static final int DK3_A2 = 256;
    public static final int DK_NICAM = 512;

    /* renamed from: I */
    public static final int f236I = 16;
    public static final int I_NICAM = 32768;

    /* renamed from: L */
    public static final int f237L = 1024;
    public static final int L_NICAM = 65536;
    public static final int L_PRIME = 131072;

    /* renamed from: M */
    public static final int f238M = 2048;
    public static final int M_A2 = 8192;
    public static final int M_BTSC = 4096;
    public static final int M_EIAJ = 16384;
    public static final int UNDEFINED = 0;
}
