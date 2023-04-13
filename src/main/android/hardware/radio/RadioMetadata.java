package android.hardware.radio;

import android.annotation.SystemApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
@SystemApi
/* loaded from: classes2.dex */
public final class RadioMetadata implements Parcelable {
    public static final Parcelable.Creator<RadioMetadata> CREATOR;
    private static final ArrayMap<String, Integer> METADATA_KEYS_TYPE;
    public static final String METADATA_KEY_ALBUM = "android.hardware.radio.metadata.ALBUM";
    public static final String METADATA_KEY_ART = "android.hardware.radio.metadata.ART";
    public static final String METADATA_KEY_ARTIST = "android.hardware.radio.metadata.ARTIST";
    public static final String METADATA_KEY_CLOCK = "android.hardware.radio.metadata.CLOCK";
    public static final String METADATA_KEY_DAB_COMPONENT_NAME = "android.hardware.radio.metadata.DAB_COMPONENT_NAME";
    public static final String METADATA_KEY_DAB_COMPONENT_NAME_SHORT = "android.hardware.radio.metadata.DAB_COMPONENT_NAME_SHORT";
    public static final String METADATA_KEY_DAB_ENSEMBLE_NAME = "android.hardware.radio.metadata.DAB_ENSEMBLE_NAME";
    public static final String METADATA_KEY_DAB_ENSEMBLE_NAME_SHORT = "android.hardware.radio.metadata.DAB_ENSEMBLE_NAME_SHORT";
    public static final String METADATA_KEY_DAB_SERVICE_NAME = "android.hardware.radio.metadata.DAB_SERVICE_NAME";
    public static final String METADATA_KEY_DAB_SERVICE_NAME_SHORT = "android.hardware.radio.metadata.DAB_SERVICE_NAME_SHORT";
    public static final String METADATA_KEY_GENRE = "android.hardware.radio.metadata.GENRE";
    public static final String METADATA_KEY_ICON = "android.hardware.radio.metadata.ICON";
    public static final String METADATA_KEY_PROGRAM_NAME = "android.hardware.radio.metadata.PROGRAM_NAME";
    public static final String METADATA_KEY_RBDS_PTY = "android.hardware.radio.metadata.RBDS_PTY";
    public static final String METADATA_KEY_RDS_PI = "android.hardware.radio.metadata.RDS_PI";
    public static final String METADATA_KEY_RDS_PS = "android.hardware.radio.metadata.RDS_PS";
    public static final String METADATA_KEY_RDS_PTY = "android.hardware.radio.metadata.RDS_PTY";
    public static final String METADATA_KEY_RDS_RT = "android.hardware.radio.metadata.RDS_RT";
    public static final String METADATA_KEY_TITLE = "android.hardware.radio.metadata.TITLE";
    private static final int METADATA_TYPE_BITMAP = 2;
    private static final int METADATA_TYPE_CLOCK = 3;
    private static final int METADATA_TYPE_INT = 0;
    private static final int METADATA_TYPE_INVALID = -1;
    private static final int METADATA_TYPE_TEXT = 1;
    private static final int NATIVE_KEY_ALBUM = 7;
    private static final int NATIVE_KEY_ART = 10;
    private static final int NATIVE_KEY_ARTIST = 6;
    private static final int NATIVE_KEY_CLOCK = 11;
    private static final int NATIVE_KEY_GENRE = 8;
    private static final int NATIVE_KEY_ICON = 9;
    private static final int NATIVE_KEY_INVALID = -1;
    private static final SparseArray<String> NATIVE_KEY_MAPPING;
    private static final int NATIVE_KEY_RBDS_PTY = 3;
    private static final int NATIVE_KEY_RDS_PI = 0;
    private static final int NATIVE_KEY_RDS_PS = 1;
    private static final int NATIVE_KEY_RDS_PTY = 2;
    private static final int NATIVE_KEY_RDS_RT = 4;
    private static final int NATIVE_KEY_TITLE = 5;
    private static final String TAG = "BroadcastRadio.metadata";
    private final Bundle mBundle;
    private Integer mHashCode;

    static {
        ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
        METADATA_KEYS_TYPE = arrayMap;
        arrayMap.put(METADATA_KEY_RDS_PI, 0);
        arrayMap.put(METADATA_KEY_RDS_PS, 1);
        arrayMap.put(METADATA_KEY_RDS_PTY, 0);
        arrayMap.put(METADATA_KEY_RBDS_PTY, 0);
        arrayMap.put(METADATA_KEY_RDS_RT, 1);
        arrayMap.put(METADATA_KEY_TITLE, 1);
        arrayMap.put(METADATA_KEY_ARTIST, 1);
        arrayMap.put(METADATA_KEY_ALBUM, 1);
        arrayMap.put(METADATA_KEY_GENRE, 1);
        arrayMap.put(METADATA_KEY_ICON, 2);
        arrayMap.put(METADATA_KEY_ART, 2);
        arrayMap.put(METADATA_KEY_CLOCK, 3);
        arrayMap.put(METADATA_KEY_PROGRAM_NAME, 1);
        arrayMap.put(METADATA_KEY_DAB_ENSEMBLE_NAME, 1);
        arrayMap.put(METADATA_KEY_DAB_ENSEMBLE_NAME_SHORT, 1);
        arrayMap.put(METADATA_KEY_DAB_SERVICE_NAME, 1);
        arrayMap.put(METADATA_KEY_DAB_SERVICE_NAME_SHORT, 1);
        arrayMap.put(METADATA_KEY_DAB_COMPONENT_NAME, 1);
        arrayMap.put(METADATA_KEY_DAB_COMPONENT_NAME_SHORT, 1);
        SparseArray<String> sparseArray = new SparseArray<>();
        NATIVE_KEY_MAPPING = sparseArray;
        sparseArray.put(0, METADATA_KEY_RDS_PI);
        sparseArray.put(1, METADATA_KEY_RDS_PS);
        sparseArray.put(2, METADATA_KEY_RDS_PTY);
        sparseArray.put(3, METADATA_KEY_RBDS_PTY);
        sparseArray.put(4, METADATA_KEY_RDS_RT);
        sparseArray.put(5, METADATA_KEY_TITLE);
        sparseArray.put(6, METADATA_KEY_ARTIST);
        sparseArray.put(7, METADATA_KEY_ALBUM);
        sparseArray.put(8, METADATA_KEY_GENRE);
        sparseArray.put(9, METADATA_KEY_ICON);
        sparseArray.put(10, METADATA_KEY_ART);
        sparseArray.put(11, METADATA_KEY_CLOCK);
        CREATOR = new Parcelable.Creator<RadioMetadata>() { // from class: android.hardware.radio.RadioMetadata.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RadioMetadata createFromParcel(Parcel in) {
                return new RadioMetadata(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public RadioMetadata[] newArray(int size) {
                return new RadioMetadata[size];
            }
        };
    }

    @SystemApi
    /* loaded from: classes2.dex */
    public static final class Clock implements Parcelable {
        public static final Parcelable.Creator<Clock> CREATOR = new Parcelable.Creator<Clock>() { // from class: android.hardware.radio.RadioMetadata.Clock.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Clock createFromParcel(Parcel in) {
                return new Clock(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Clock[] newArray(int size) {
                return new Clock[size];
            }
        };
        private final int mTimezoneOffsetMinutes;
        private final long mUtcEpochSeconds;

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeLong(this.mUtcEpochSeconds);
            out.writeInt(this.mTimezoneOffsetMinutes);
        }

        public Clock(long utcEpochSeconds, int timezoneOffsetMinutes) {
            this.mUtcEpochSeconds = utcEpochSeconds;
            this.mTimezoneOffsetMinutes = timezoneOffsetMinutes;
        }

        private Clock(Parcel in) {
            this.mUtcEpochSeconds = in.readLong();
            this.mTimezoneOffsetMinutes = in.readInt();
        }

        public long getUtcEpochSeconds() {
            return this.mUtcEpochSeconds;
        }

        public int getTimezoneOffsetMinutes() {
            return this.mTimezoneOffsetMinutes;
        }
    }

    public int hashCode() {
        if (this.mHashCode == null) {
            List<String> keys = new ArrayList<>(this.mBundle.keySet());
            keys.sort(null);
            Object[] objs = new Object[keys.size() * 2];
            for (int i = 0; i < keys.size(); i++) {
                objs[i * 2] = keys.get(i);
                objs[(i * 2) + 1] = this.mBundle.get(keys.get(i));
            }
            int i2 = Arrays.hashCode(objs);
            this.mHashCode = Integer.valueOf(i2);
        }
        return this.mHashCode.intValue();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof RadioMetadata) {
            Bundle otherBundle = ((RadioMetadata) obj).mBundle;
            if (this.mBundle.keySet().equals(otherBundle.keySet())) {
                for (String key : this.mBundle.keySet()) {
                    if (!this.mBundle.get(key).equals(otherBundle.get(key))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    RadioMetadata() {
        this.mBundle = new Bundle();
    }

    private RadioMetadata(Bundle bundle) {
        this.mBundle = new Bundle(bundle);
    }

    private RadioMetadata(Parcel in) {
        this.mBundle = in.readBundle();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("RadioMetadata[");
        boolean first = true;
        for (String key : this.mBundle.keySet()) {
            if (first) {
                first = false;
            } else {
                sb.append(", ");
            }
            String keyDisp = key;
            if (key.startsWith("android.hardware.radio.metadata")) {
                keyDisp = key.substring("android.hardware.radio.metadata".length());
            }
            sb.append(keyDisp);
            sb.append('=');
            sb.append(this.mBundle.get(key));
        }
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }

    public boolean containsKey(String key) {
        return this.mBundle.containsKey(key);
    }

    public String getString(String key) {
        return this.mBundle.getString(key);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void putInt(Bundle bundle, String key, int value) {
        int type = METADATA_KEYS_TYPE.getOrDefault(key, -1).intValue();
        if (type != 0 && type != 2) {
            throw new IllegalArgumentException("The " + key + " key cannot be used to put an int");
        }
        bundle.putInt(key, value);
    }

    public int getInt(String key) {
        return this.mBundle.getInt(key, 0);
    }

    @Deprecated
    public Bitmap getBitmap(String key) {
        try {
            Bitmap bmp = (Bitmap) this.mBundle.getParcelable(key, Bitmap.class);
            return bmp;
        } catch (Exception e) {
            Log.m103w(TAG, "Failed to retrieve a key as Bitmap.", e);
            return null;
        }
    }

    public int getBitmapId(String key) {
        if (METADATA_KEY_ICON.equals(key) || METADATA_KEY_ART.equals(key)) {
            return getInt(key);
        }
        return 0;
    }

    public Clock getClock(String key) {
        try {
            Clock clock = (Clock) this.mBundle.getParcelable(key, Clock.class);
            return clock;
        } catch (Exception e) {
            Log.m103w(TAG, "Failed to retrieve a key as Clock.", e);
            return null;
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBundle(this.mBundle);
    }

    public int size() {
        return this.mBundle.size();
    }

    public Set<String> keySet() {
        return this.mBundle.keySet();
    }

    public static String getKeyFromNativeKey(int nativeKey) {
        return NATIVE_KEY_MAPPING.get(nativeKey, null);
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private final Bundle mBundle;

        public Builder() {
            this.mBundle = new Bundle();
        }

        public Builder(RadioMetadata source) {
            this.mBundle = new Bundle(source.mBundle);
        }

        public Builder(RadioMetadata source, int maxBitmapSize) {
            this(source);
            for (String key : this.mBundle.keySet()) {
                Object value = this.mBundle.get(key);
                if (value != null && (value instanceof Bitmap)) {
                    Bitmap bmp = (Bitmap) value;
                    if (bmp.getHeight() > maxBitmapSize || bmp.getWidth() > maxBitmapSize) {
                        putBitmap(key, scaleBitmap(bmp, maxBitmapSize));
                    }
                }
            }
        }

        public Builder putString(String key, String value) {
            if (!RadioMetadata.METADATA_KEYS_TYPE.containsKey(key) || ((Integer) RadioMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 1) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a String");
            }
            this.mBundle.putString(key, value);
            return this;
        }

        public Builder putInt(String key, int value) {
            RadioMetadata.putInt(this.mBundle, key, value);
            return this;
        }

        public Builder putBitmap(String key, Bitmap value) {
            if (!RadioMetadata.METADATA_KEYS_TYPE.containsKey(key) || ((Integer) RadioMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 2) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a Bitmap");
            }
            this.mBundle.putParcelable(key, value);
            return this;
        }

        public Builder putClock(String key, long utcSecondsSinceEpoch, int timezoneOffsetMinutes) {
            if (!RadioMetadata.METADATA_KEYS_TYPE.containsKey(key) || ((Integer) RadioMetadata.METADATA_KEYS_TYPE.get(key)).intValue() != 3) {
                throw new IllegalArgumentException("The " + key + " key cannot be used to put a RadioMetadata.Clock.");
            }
            this.mBundle.putParcelable(key, new Clock(utcSecondsSinceEpoch, timezoneOffsetMinutes));
            return this;
        }

        public RadioMetadata build() {
            return new RadioMetadata(this.mBundle);
        }

        private Bitmap scaleBitmap(Bitmap bmp, int maxSize) {
            float maxSizeF = maxSize;
            float widthScale = maxSizeF / bmp.getWidth();
            float heightScale = maxSizeF / bmp.getHeight();
            float scale = Math.min(widthScale, heightScale);
            int height = (int) (bmp.getHeight() * scale);
            int width = (int) (bmp.getWidth() * scale);
            return Bitmap.createScaledBitmap(bmp, width, height, true);
        }
    }

    int putIntFromNative(int nativeKey, int value) {
        String key = getKeyFromNativeKey(nativeKey);
        try {
            putInt(this.mBundle, key, value);
            this.mHashCode = null;
            return 0;
        } catch (IllegalArgumentException e) {
            return -1;
        }
    }

    int putStringFromNative(int nativeKey, String value) {
        String key = getKeyFromNativeKey(nativeKey);
        ArrayMap<String, Integer> arrayMap = METADATA_KEYS_TYPE;
        if (!arrayMap.containsKey(key) || arrayMap.get(key).intValue() != 1) {
            return -1;
        }
        this.mBundle.putString(key, value);
        this.mHashCode = null;
        return 0;
    }

    int putBitmapFromNative(int nativeKey, byte[] value) {
        String key = getKeyFromNativeKey(nativeKey);
        ArrayMap<String, Integer> arrayMap = METADATA_KEYS_TYPE;
        if (arrayMap.containsKey(key) && arrayMap.get(key).intValue() == 2) {
            try {
                Bitmap bmp = BitmapFactory.decodeByteArray(value, 0, value.length);
                if (bmp != null) {
                    this.mBundle.putParcelable(key, bmp);
                    this.mHashCode = null;
                    return 0;
                }
            } catch (Exception e) {
            }
            return -1;
        }
        return -1;
    }

    int putClockFromNative(int nativeKey, long utcEpochSeconds, int timezoneOffsetInMinutes) {
        String key = getKeyFromNativeKey(nativeKey);
        ArrayMap<String, Integer> arrayMap = METADATA_KEYS_TYPE;
        if (!arrayMap.containsKey(key) || arrayMap.get(key).intValue() != 3) {
            return -1;
        }
        this.mBundle.putParcelable(key, new Clock(utcEpochSeconds, timezoneOffsetInMinutes));
        this.mHashCode = null;
        return 0;
    }
}
