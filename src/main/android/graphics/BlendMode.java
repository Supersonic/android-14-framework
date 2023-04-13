package android.graphics;

import android.graphics.PorterDuff;
/* loaded from: classes.dex */
public enum BlendMode {
    CLEAR(0),
    SRC(1),
    DST(2),
    SRC_OVER(3),
    DST_OVER(4),
    SRC_IN(5),
    DST_IN(6),
    SRC_OUT(7),
    DST_OUT(8),
    SRC_ATOP(9),
    DST_ATOP(10),
    XOR(11),
    PLUS(12),
    MODULATE(13),
    SCREEN(14),
    OVERLAY(15),
    DARKEN(16),
    LIGHTEN(17),
    COLOR_DODGE(18),
    COLOR_BURN(19),
    HARD_LIGHT(20),
    SOFT_LIGHT(21),
    DIFFERENCE(22),
    EXCLUSION(23),
    MULTIPLY(24),
    HUE(25),
    SATURATION(26),
    COLOR(27),
    LUMINOSITY(28);
    
    private static final BlendMode[] BLEND_MODES = values();
    private final Xfermode mXfermode;

    public static BlendMode fromValue(int value) {
        BlendMode[] blendModeArr;
        for (BlendMode mode : BLEND_MODES) {
            if (mode.mXfermode.porterDuffMode == value) {
                return mode;
            }
        }
        return null;
    }

    public static int toValue(BlendMode mode) {
        return mode.getXfermode().porterDuffMode;
    }

    /* renamed from: android.graphics.BlendMode$1 */
    /* loaded from: classes.dex */
    static /* synthetic */ class C07971 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$BlendMode;

        static {
            int[] iArr = new int[BlendMode.values().length];
            $SwitchMap$android$graphics$BlendMode = iArr;
            try {
                iArr[BlendMode.CLEAR.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SRC.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DST.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SRC_OVER.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DST_OVER.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SRC_IN.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DST_IN.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SRC_OUT.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DST_OUT.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SRC_ATOP.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DST_ATOP.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.XOR.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.DARKEN.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.LIGHTEN.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.MODULATE.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.SCREEN.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.PLUS.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            try {
                $SwitchMap$android$graphics$BlendMode[BlendMode.OVERLAY.ordinal()] = 18;
            } catch (NoSuchFieldError e18) {
            }
        }
    }

    public static PorterDuff.Mode blendModeToPorterDuffMode(BlendMode mode) {
        if (mode == null) {
            return null;
        }
        switch (C07971.$SwitchMap$android$graphics$BlendMode[mode.ordinal()]) {
            case 1:
                return PorterDuff.Mode.CLEAR;
            case 2:
                return PorterDuff.Mode.SRC;
            case 3:
                return PorterDuff.Mode.DST;
            case 4:
                return PorterDuff.Mode.SRC_OVER;
            case 5:
                return PorterDuff.Mode.DST_OVER;
            case 6:
                return PorterDuff.Mode.SRC_IN;
            case 7:
                return PorterDuff.Mode.DST_IN;
            case 8:
                return PorterDuff.Mode.SRC_OUT;
            case 9:
                return PorterDuff.Mode.DST_OUT;
            case 10:
                return PorterDuff.Mode.SRC_ATOP;
            case 11:
                return PorterDuff.Mode.DST_ATOP;
            case 12:
                return PorterDuff.Mode.XOR;
            case 13:
                return PorterDuff.Mode.DARKEN;
            case 14:
                return PorterDuff.Mode.LIGHTEN;
            case 15:
                return PorterDuff.Mode.MULTIPLY;
            case 16:
                return PorterDuff.Mode.SCREEN;
            case 17:
                return PorterDuff.Mode.ADD;
            case 18:
                return PorterDuff.Mode.OVERLAY;
            default:
                return null;
        }
    }

    BlendMode(int mode) {
        Xfermode xfermode = new Xfermode();
        this.mXfermode = xfermode;
        xfermode.porterDuffMode = mode;
    }

    public Xfermode getXfermode() {
        return this.mXfermode;
    }
}
