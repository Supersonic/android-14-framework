package android.view;

import android.hardware.input.InputManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AndroidRuntimeException;
import android.util.SparseIntArray;
import com.android.internal.logging.nano.MetricsProto;
import java.text.Normalizer;
/* loaded from: classes4.dex */
public class KeyCharacterMap implements Parcelable {
    private static final int ACCENT_ACUTE = 180;
    private static final int ACCENT_APOSTROPHE = 39;
    private static final int ACCENT_BREVE = 728;
    private static final int ACCENT_CARON = 711;
    private static final int ACCENT_CEDILLA = 184;
    private static final int ACCENT_CIRCUMFLEX = 710;
    private static final int ACCENT_CIRCUMFLEX_LEGACY = 94;
    private static final int ACCENT_COMMA_ABOVE = 8125;
    private static final int ACCENT_COMMA_ABOVE_RIGHT = 700;
    private static final int ACCENT_DOT_ABOVE = 729;
    private static final int ACCENT_DOT_BELOW = 46;
    private static final int ACCENT_DOUBLE_ACUTE = 733;
    private static final int ACCENT_GRAVE = 715;
    private static final int ACCENT_GRAVE_LEGACY = 96;
    private static final int ACCENT_HOOK_ABOVE = 704;
    private static final int ACCENT_HORN = 39;
    private static final int ACCENT_MACRON = 175;
    private static final int ACCENT_MACRON_BELOW = 717;
    private static final int ACCENT_OGONEK = 731;
    private static final int ACCENT_QUOTATION_MARK = 34;
    private static final int ACCENT_REVERSED_COMMA_ABOVE = 701;
    private static final int ACCENT_RING_ABOVE = 730;
    private static final int ACCENT_STROKE = 45;
    private static final int ACCENT_TILDE = 732;
    private static final int ACCENT_TILDE_LEGACY = 126;
    private static final int ACCENT_TURNED_COMMA_ABOVE = 699;
    private static final int ACCENT_UMLAUT = 168;
    private static final int ACCENT_VERTICAL_LINE_ABOVE = 712;
    private static final int ACCENT_VERTICAL_LINE_BELOW = 716;
    public static final int ALPHA = 3;
    @Deprecated
    public static final int BUILT_IN_KEYBOARD = 0;
    private static final int CHAR_SPACE = 32;
    public static final int COMBINING_ACCENT = Integer.MIN_VALUE;
    public static final int COMBINING_ACCENT_MASK = Integer.MAX_VALUE;
    public static final Parcelable.Creator<KeyCharacterMap> CREATOR;
    public static final int FULL = 4;
    public static final char HEX_INPUT = 61184;
    public static final int MODIFIER_BEHAVIOR_CHORDED = 0;
    public static final int MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED = 1;
    public static final int NUMERIC = 1;
    public static final char PICKER_DIALOG_INPUT = 61185;
    public static final int PREDICTIVE = 2;
    public static final int SPECIAL_FUNCTION = 5;
    public static final int VIRTUAL_KEYBOARD = -1;
    private static final SparseIntArray sAccentToCombining;
    private static final SparseIntArray sCombiningToAccent;
    private static final StringBuilder sDeadKeyBuilder;
    private static final SparseIntArray sDeadKeyCache;
    private long mPtr;

    @Deprecated
    /* loaded from: classes4.dex */
    public static class KeyData {
        public static final int META_LENGTH = 4;
        public char displayLabel;
        public char[] meta = new char[4];
        public char number;
    }

    private static native void nativeDispose(long j);

    private static native boolean nativeEquals(long j, long j2);

    private static native char nativeGetCharacter(long j, int i, int i2);

    private static native char nativeGetDisplayLabel(long j, int i);

    private static native KeyEvent[] nativeGetEvents(long j, char[] cArr);

    private static native boolean nativeGetFallbackAction(long j, int i, int i2, FallbackAction fallbackAction);

    private static native int nativeGetKeyboardType(long j);

    private static native char nativeGetMatch(long j, int i, char[] cArr, int i2);

    private static native char nativeGetNumber(long j, int i);

    private static native KeyCharacterMap nativeObtainEmptyKeyCharacterMap(int i);

    private static native long nativeReadFromParcel(Parcel parcel);

    private static native void nativeWriteToParcel(long j, Parcel parcel);

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sCombiningToAccent = sparseIntArray;
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        sAccentToCombining = sparseIntArray2;
        addCombining(768, 715);
        addCombining(769, 180);
        addCombining(770, 710);
        addCombining(771, 732);
        addCombining(772, 175);
        addCombining(774, 728);
        addCombining(775, 729);
        addCombining(776, 168);
        addCombining(777, 704);
        addCombining(778, 730);
        addCombining(779, 733);
        addCombining(780, 711);
        addCombining(786, 699);
        addCombining(787, ACCENT_COMMA_ABOVE);
        addCombining(788, 701);
        addCombining(789, 700);
        addCombining(MetricsProto.MetricsEvent.NOTIFICATION_SINCE_UPDATE_MILLIS, 39);
        addCombining(803, 46);
        addCombining(807, 184);
        addCombining(808, 731);
        addCombining(MetricsProto.MetricsEvent.PROVISIONING_TERMS_ACTIVITY_TIME_MS, 716);
        addCombining(817, 717);
        addCombining(821, 45);
        sparseIntArray.append(MetricsProto.MetricsEvent.NOTIFICATION_SNOOZED_CRITERIA, 715);
        sparseIntArray.append(MetricsProto.MetricsEvent.FIELD_CONTEXT, 180);
        sparseIntArray.append(MetricsProto.MetricsEvent.WIFI_NETWORK_RECOMMENDATION_SAVED_NETWORK_EVALUATOR, ACCENT_COMMA_ABOVE);
        sparseIntArray.append(781, 39);
        sparseIntArray.append(782, 34);
        sparseIntArray2.append(96, 768);
        sparseIntArray2.append(94, 770);
        sparseIntArray2.append(126, 771);
        sparseIntArray2.append(39, 769);
        sparseIntArray2.append(34, 776);
        sDeadKeyCache = new SparseIntArray();
        sDeadKeyBuilder = new StringBuilder();
        addDeadKey(45, 68, 272);
        addDeadKey(45, 71, MetricsProto.MetricsEvent.ACTION_SUPPORT_DISCLAIMER_OK);
        addDeadKey(45, 72, 294);
        addDeadKey(45, 73, 407);
        addDeadKey(45, 76, 321);
        addDeadKey(45, 79, 216);
        addDeadKey(45, 84, 358);
        addDeadKey(45, 100, 273);
        addDeadKey(45, 103, MetricsProto.MetricsEvent.ACTION_SUPPORT_DAIL_TOLLFREE);
        addDeadKey(45, 104, 295);
        addDeadKey(45, 105, MetricsProto.MetricsEvent.PROVISIONING_ENTRY_POINT_QR_CODE);
        addDeadKey(45, 108, 322);
        addDeadKey(45, 111, 248);
        addDeadKey(45, 116, 359);
        CREATOR = new Parcelable.Creator<KeyCharacterMap>() { // from class: android.view.KeyCharacterMap.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyCharacterMap createFromParcel(Parcel in) {
                return new KeyCharacterMap(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeyCharacterMap[] newArray(int size) {
                return new KeyCharacterMap[size];
            }
        };
    }

    private static void addCombining(int combining, int accent) {
        sCombiningToAccent.append(combining, accent);
        sAccentToCombining.append(accent, combining);
    }

    private static void addDeadKey(int accent, int c, int result) {
        int combining = sAccentToCombining.get(accent);
        if (combining == 0) {
            throw new IllegalStateException("Invalid dead key declaration.");
        }
        int combination = (combining << 16) | c;
        sDeadKeyCache.put(combination, result);
    }

    private KeyCharacterMap(Parcel in) {
        if (in == null) {
            throw new IllegalArgumentException("parcel must not be null");
        }
        long nativeReadFromParcel = nativeReadFromParcel(in);
        this.mPtr = nativeReadFromParcel;
        if (nativeReadFromParcel == 0) {
            throw new RuntimeException("Could not read KeyCharacterMap from parcel.");
        }
    }

    private KeyCharacterMap(long ptr) {
        this.mPtr = ptr;
    }

    protected void finalize() throws Throwable {
        long j = this.mPtr;
        if (j != 0) {
            nativeDispose(j);
            this.mPtr = 0L;
        }
    }

    public static KeyCharacterMap obtainEmptyMap(int deviceId) {
        return nativeObtainEmptyKeyCharacterMap(deviceId);
    }

    public static KeyCharacterMap load(int deviceId) {
        InputManager im = InputManager.getInstance();
        InputDevice inputDevice = im.getInputDevice(deviceId);
        if (inputDevice == null && (inputDevice = im.getInputDevice(-1)) == null) {
            throw new UnavailableException("Could not load key character map for device " + deviceId);
        }
        return inputDevice.getKeyCharacterMap();
    }

    public int get(int keyCode, int metaState) {
        char ch = nativeGetCharacter(this.mPtr, keyCode, KeyEvent.normalizeMetaState(metaState));
        int map = sCombiningToAccent.get(ch);
        if (map != 0) {
            return Integer.MIN_VALUE | map;
        }
        return ch;
    }

    public FallbackAction getFallbackAction(int keyCode, int metaState) {
        FallbackAction action = FallbackAction.obtain();
        if (nativeGetFallbackAction(this.mPtr, keyCode, KeyEvent.normalizeMetaState(metaState), action)) {
            action.metaState = KeyEvent.normalizeMetaState(action.metaState);
            return action;
        }
        action.recycle();
        return null;
    }

    public char getNumber(int keyCode) {
        return nativeGetNumber(this.mPtr, keyCode);
    }

    public char getMatch(int keyCode, char[] chars) {
        return getMatch(keyCode, chars, 0);
    }

    public char getMatch(int keyCode, char[] chars, int metaState) {
        if (chars == null) {
            throw new IllegalArgumentException("chars must not be null.");
        }
        return nativeGetMatch(this.mPtr, keyCode, chars, KeyEvent.normalizeMetaState(metaState));
    }

    public char getDisplayLabel(int keyCode) {
        return nativeGetDisplayLabel(this.mPtr, keyCode);
    }

    public static int getDeadChar(int accent, int c) {
        int combined;
        if (c == accent || 32 == c) {
            return accent;
        }
        int combining = sAccentToCombining.get(accent);
        int i = 0;
        if (combining == 0) {
            return 0;
        }
        int combination = (combining << 16) | c;
        SparseIntArray sparseIntArray = sDeadKeyCache;
        synchronized (sparseIntArray) {
            combined = sparseIntArray.get(combination, -1);
            if (combined == -1) {
                StringBuilder sb = sDeadKeyBuilder;
                sb.setLength(0);
                sb.append((char) c);
                sb.append((char) combining);
                String result = Normalizer.normalize(sb, Normalizer.Form.NFC);
                if (result.codePointCount(0, result.length()) == 1) {
                    i = result.codePointAt(0);
                }
                combined = i;
                sparseIntArray.put(combination, combined);
            }
        }
        return combined;
    }

    @Deprecated
    public boolean getKeyData(int keyCode, KeyData results) {
        if (results.meta.length < 4) {
            throw new IndexOutOfBoundsException("results.meta.length must be >= 4");
        }
        char displayLabel = nativeGetDisplayLabel(this.mPtr, keyCode);
        if (displayLabel == 0) {
            return false;
        }
        results.displayLabel = displayLabel;
        results.number = nativeGetNumber(this.mPtr, keyCode);
        results.meta[0] = nativeGetCharacter(this.mPtr, keyCode, 0);
        results.meta[1] = nativeGetCharacter(this.mPtr, keyCode, 1);
        results.meta[2] = nativeGetCharacter(this.mPtr, keyCode, 2);
        results.meta[3] = nativeGetCharacter(this.mPtr, keyCode, 3);
        return true;
    }

    public KeyEvent[] getEvents(char[] chars) {
        if (chars == null) {
            throw new IllegalArgumentException("chars must not be null.");
        }
        return nativeGetEvents(this.mPtr, chars);
    }

    public boolean isPrintingKey(int keyCode) {
        int type = Character.getType(nativeGetDisplayLabel(this.mPtr, keyCode));
        switch (type) {
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return false;
            default:
                return true;
        }
    }

    public int getKeyboardType() {
        return nativeGetKeyboardType(this.mPtr);
    }

    public int getModifierBehavior() {
        switch (getKeyboardType()) {
            case 4:
            case 5:
                return 0;
            default:
                return 1;
        }
    }

    public static boolean deviceHasKey(int keyCode) {
        return InputManager.getInstance().deviceHasKeys(new int[]{keyCode})[0];
    }

    public static boolean[] deviceHasKeys(int[] keyCodes) {
        return InputManager.getInstance().deviceHasKeys(keyCodes);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        if (out == null) {
            throw new IllegalArgumentException("parcel must not be null");
        }
        nativeWriteToParcel(this.mPtr, out);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof KeyCharacterMap)) {
            return false;
        }
        KeyCharacterMap peer = (KeyCharacterMap) obj;
        long j = this.mPtr;
        if (j != 0) {
            long j2 = peer.mPtr;
            if (j2 != 0) {
                return nativeEquals(j, j2);
            }
        }
        return j == peer.mPtr;
    }

    /* loaded from: classes4.dex */
    public static class UnavailableException extends AndroidRuntimeException {
        public UnavailableException(String msg) {
            super(msg);
        }
    }

    /* loaded from: classes4.dex */
    public static final class FallbackAction {
        private static final int MAX_RECYCLED = 10;
        private static FallbackAction sRecycleBin;
        private static final Object sRecycleLock = new Object();
        private static int sRecycledCount;
        public int keyCode;
        public int metaState;
        private FallbackAction next;

        private FallbackAction() {
        }

        public static FallbackAction obtain() {
            FallbackAction target;
            synchronized (sRecycleLock) {
                target = sRecycleBin;
                if (target == null) {
                    target = new FallbackAction();
                } else {
                    sRecycleBin = target.next;
                    sRecycledCount--;
                    target.next = null;
                }
            }
            return target;
        }

        public void recycle() {
            synchronized (sRecycleLock) {
                int i = sRecycledCount;
                if (i < 10) {
                    this.next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount = i + 1;
                } else {
                    this.next = null;
                }
            }
        }
    }
}
