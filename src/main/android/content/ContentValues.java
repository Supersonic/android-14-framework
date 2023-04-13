package android.content;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class ContentValues implements Parcelable {
    public static final Parcelable.Creator<ContentValues> CREATOR = new Parcelable.Creator<ContentValues>() { // from class: android.content.ContentValues.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentValues createFromParcel(Parcel in) {
            return new ContentValues(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ContentValues[] newArray(int size) {
            return new ContentValues[size];
        }
    };
    public static final String TAG = "ContentValues";
    private final ArrayMap<String, Object> mMap;
    @Deprecated
    private HashMap<String, Object> mValues;

    public ContentValues() {
        this.mMap = new ArrayMap<>();
    }

    public ContentValues(int size) {
        Preconditions.checkArgumentNonnegative(size);
        this.mMap = new ArrayMap<>(size);
    }

    public ContentValues(ContentValues from) {
        Objects.requireNonNull(from);
        this.mMap = new ArrayMap<>(from.mMap);
    }

    @Deprecated
    private ContentValues(HashMap<String, Object> from) {
        ArrayMap<String, Object> arrayMap = new ArrayMap<>();
        this.mMap = arrayMap;
        arrayMap.putAll(from);
    }

    private ContentValues(Parcel in) {
        ArrayMap<String, Object> arrayMap = new ArrayMap<>(in.readInt());
        this.mMap = arrayMap;
        in.readArrayMap(arrayMap, null);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ContentValues)) {
            return false;
        }
        return this.mMap.equals(((ContentValues) object).mMap);
    }

    public ArrayMap<String, Object> getValues() {
        return this.mMap;
    }

    public int hashCode() {
        return this.mMap.hashCode();
    }

    public void put(String key, String value) {
        this.mMap.put(key, value);
    }

    public void putAll(ContentValues other) {
        this.mMap.putAll((ArrayMap<? extends String, ? extends Object>) other.mMap);
    }

    public void put(String key, Byte value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Short value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Integer value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Long value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Float value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Double value) {
        this.mMap.put(key, value);
    }

    public void put(String key, Boolean value) {
        this.mMap.put(key, value);
    }

    public void put(String key, byte[] value) {
        this.mMap.put(key, value);
    }

    public void putNull(String key) {
        this.mMap.put(key, null);
    }

    public void putObject(String key, Object value) {
        if (value == null) {
            putNull(key);
        } else if (value instanceof String) {
            put(key, (String) value);
        } else if (value instanceof Byte) {
            put(key, (Byte) value);
        } else if (value instanceof Short) {
            put(key, (Short) value);
        } else if (value instanceof Integer) {
            put(key, (Integer) value);
        } else if (value instanceof Long) {
            put(key, (Long) value);
        } else if (value instanceof Float) {
            put(key, (Float) value);
        } else if (value instanceof Double) {
            put(key, (Double) value);
        } else if (value instanceof Boolean) {
            put(key, (Boolean) value);
        } else if (value instanceof byte[]) {
            put(key, (byte[]) value);
        } else {
            throw new IllegalArgumentException("Unsupported type " + value.getClass());
        }
    }

    public int size() {
        return this.mMap.size();
    }

    public boolean isEmpty() {
        return this.mMap.isEmpty();
    }

    public void remove(String key) {
        this.mMap.remove(key);
    }

    public void clear() {
        this.mMap.clear();
    }

    public boolean containsKey(String key) {
        return this.mMap.containsKey(key);
    }

    public Object get(String key) {
        return this.mMap.get(key);
    }

    public String getAsString(String key) {
        Object value = this.mMap.get(key);
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public Long getAsLong(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.valueOf(((Number) value).longValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Long.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Long value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Long: " + value, e);
            return null;
        }
    }

    public Integer getAsInteger(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(((Number) value).intValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Integer.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Integer value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Integer: " + value, e);
            return null;
        }
    }

    public Short getAsShort(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Short.valueOf(((Number) value).shortValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Short.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Short value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Short: " + value, e);
            return null;
        }
    }

    public Byte getAsByte(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Byte.valueOf(((Number) value).byteValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Byte.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Byte value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Byte: " + value, e);
            return null;
        }
    }

    public Double getAsDouble(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Double.valueOf(((Number) value).doubleValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Double.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Double value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Double: " + value, e);
            return null;
        }
    }

    public Float getAsFloat(String key) {
        Object value = this.mMap.get(key);
        if (value == null) {
            return null;
        }
        try {
            return Float.valueOf(((Number) value).floatValue());
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                try {
                    return Float.valueOf(value.toString());
                } catch (NumberFormatException e2) {
                    Log.m110e(TAG, "Cannot parse Float value for " + value + " at key " + key);
                    return null;
                }
            }
            Log.m109e(TAG, "Cannot cast value for " + key + " to a Float: " + value, e);
            return null;
        }
    }

    public Boolean getAsBoolean(String key) {
        Object value = this.mMap.get(key);
        try {
            return (Boolean) value;
        } catch (ClassCastException e) {
            if (value instanceof CharSequence) {
                if (Boolean.valueOf(value.toString()).booleanValue() || "1".equals(value)) {
                    r3 = true;
                }
                return Boolean.valueOf(r3);
            } else if (value instanceof Number) {
                return Boolean.valueOf(((Number) value).intValue() != 0);
            } else {
                Log.m109e(TAG, "Cannot cast value for " + key + " to a Boolean: " + value, e);
                return null;
            }
        }
    }

    public byte[] getAsByteArray(String key) {
        Object value = this.mMap.get(key);
        if (value instanceof byte[]) {
            return (byte[]) value;
        }
        return null;
    }

    public Set<Map.Entry<String, Object>> valueSet() {
        return this.mMap.entrySet();
    }

    public Set<String> keySet() {
        return this.mMap.keySet();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mMap.size());
        parcel.writeArrayMap(this.mMap);
    }

    @Deprecated
    public void putStringArrayList(String key, ArrayList<String> value) {
        this.mMap.put(key, value);
    }

    @Deprecated
    public ArrayList<String> getStringArrayList(String key) {
        return (ArrayList) this.mMap.get(key);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String name : this.mMap.keySet()) {
            String value = getAsString(name);
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(name + "=" + value);
        }
        return sb.toString();
    }

    public static boolean isSupportedValue(Object value) {
        if (value == null || (value instanceof String) || (value instanceof Byte) || (value instanceof Short) || (value instanceof Integer) || (value instanceof Long) || (value instanceof Float) || (value instanceof Double) || (value instanceof Boolean) || (value instanceof byte[])) {
            return true;
        }
        return false;
    }
}
