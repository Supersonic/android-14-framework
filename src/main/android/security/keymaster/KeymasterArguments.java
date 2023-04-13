package android.security.keymaster;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
/* loaded from: classes3.dex */
public class KeymasterArguments implements Parcelable {
    public static final Parcelable.Creator<KeymasterArguments> CREATOR;
    public static final long UINT32_MAX_VALUE = 4294967295L;
    private static final long UINT32_RANGE = 4294967296L;
    public static final BigInteger UINT64_MAX_VALUE;
    private static final BigInteger UINT64_RANGE;
    private List<KeymasterArgument> mArguments;

    static {
        BigInteger shiftLeft = BigInteger.ONE.shiftLeft(64);
        UINT64_RANGE = shiftLeft;
        UINT64_MAX_VALUE = shiftLeft.subtract(BigInteger.ONE);
        CREATOR = new Parcelable.Creator<KeymasterArguments>() { // from class: android.security.keymaster.KeymasterArguments.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeymasterArguments createFromParcel(Parcel in) {
                return new KeymasterArguments(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public KeymasterArguments[] newArray(int size) {
                return new KeymasterArguments[size];
            }
        };
    }

    public KeymasterArguments() {
        this.mArguments = new ArrayList();
    }

    private KeymasterArguments(Parcel in) {
        this.mArguments = in.createTypedArrayList(KeymasterArgument.CREATOR);
    }

    public void addEnum(int tag, int value) {
        int tagType = KeymasterDefs.getTagType(tag);
        if (tagType != 268435456 && tagType != 536870912) {
            throw new IllegalArgumentException("Not an enum or repeating enum tag: " + tag);
        }
        addEnumTag(tag, value);
    }

    public void addEnums(int tag, int... values) {
        if (KeymasterDefs.getTagType(tag) != 536870912) {
            throw new IllegalArgumentException("Not a repeating enum tag: " + tag);
        }
        for (int value : values) {
            addEnumTag(tag, value);
        }
    }

    public int getEnum(int tag, int defaultValue) {
        if (KeymasterDefs.getTagType(tag) != 268435456) {
            throw new IllegalArgumentException("Not an enum tag: " + tag);
        }
        KeymasterArgument arg = getArgumentByTag(tag);
        if (arg == null) {
            return defaultValue;
        }
        return getEnumTagValue(arg);
    }

    public List<Integer> getEnums(int tag) {
        if (KeymasterDefs.getTagType(tag) != 536870912) {
            throw new IllegalArgumentException("Not a repeating enum tag: " + tag);
        }
        List<Integer> values = new ArrayList<>();
        for (KeymasterArgument arg : this.mArguments) {
            if (arg.tag == tag) {
                values.add(Integer.valueOf(getEnumTagValue(arg)));
            }
        }
        return values;
    }

    private void addEnumTag(int tag, int value) {
        this.mArguments.add(new KeymasterIntArgument(tag, value));
    }

    private int getEnumTagValue(KeymasterArgument arg) {
        return ((KeymasterIntArgument) arg).value;
    }

    public void addUnsignedInt(int tag, long value) {
        int tagType = KeymasterDefs.getTagType(tag);
        if (tagType != 805306368 && tagType != 1073741824) {
            throw new IllegalArgumentException("Not an int or repeating int tag: " + tag);
        }
        if (value < 0 || value > 4294967295L) {
            throw new IllegalArgumentException("Int tag value out of range: " + value);
        }
        this.mArguments.add(new KeymasterIntArgument(tag, (int) value));
    }

    public long getUnsignedInt(int tag, long defaultValue) {
        if (KeymasterDefs.getTagType(tag) != 805306368) {
            throw new IllegalArgumentException("Not an int tag: " + tag);
        }
        KeymasterArgument arg = getArgumentByTag(tag);
        if (arg == null) {
            return defaultValue;
        }
        return ((KeymasterIntArgument) arg).value & 4294967295L;
    }

    public void addUnsignedLong(int tag, BigInteger value) {
        int tagType = KeymasterDefs.getTagType(tag);
        if (tagType != 1342177280 && tagType != -1610612736) {
            throw new IllegalArgumentException("Not a long or repeating long tag: " + tag);
        }
        addLongTag(tag, value);
    }

    public List<BigInteger> getUnsignedLongs(int tag) {
        if (KeymasterDefs.getTagType(tag) != -1610612736) {
            throw new IllegalArgumentException("Tag is not a repeating long: " + tag);
        }
        List<BigInteger> values = new ArrayList<>();
        for (KeymasterArgument arg : this.mArguments) {
            if (arg.tag == tag) {
                values.add(getLongTagValue(arg));
            }
        }
        return values;
    }

    private void addLongTag(int tag, BigInteger value) {
        if (value.signum() == -1 || value.compareTo(UINT64_MAX_VALUE) > 0) {
            throw new IllegalArgumentException("Long tag value out of range: " + value);
        }
        this.mArguments.add(new KeymasterLongArgument(tag, value.longValue()));
    }

    private BigInteger getLongTagValue(KeymasterArgument arg) {
        return toUint64(((KeymasterLongArgument) arg).value);
    }

    public void addBoolean(int tag) {
        if (KeymasterDefs.getTagType(tag) != 1879048192) {
            throw new IllegalArgumentException("Not a boolean tag: " + tag);
        }
        this.mArguments.add(new KeymasterBooleanArgument(tag));
    }

    public boolean getBoolean(int tag) {
        if (KeymasterDefs.getTagType(tag) != 1879048192) {
            throw new IllegalArgumentException("Not a boolean tag: " + tag);
        }
        KeymasterArgument arg = getArgumentByTag(tag);
        if (arg == null) {
            return false;
        }
        return true;
    }

    public void addBytes(int tag, byte[] value) {
        if (KeymasterDefs.getTagType(tag) != -1879048192) {
            throw new IllegalArgumentException("Not a bytes tag: " + tag);
        }
        if (value == null) {
            throw new NullPointerException("value == nulll");
        }
        this.mArguments.add(new KeymasterBlobArgument(tag, value));
    }

    public byte[] getBytes(int tag, byte[] defaultValue) {
        if (KeymasterDefs.getTagType(tag) != -1879048192) {
            throw new IllegalArgumentException("Not a bytes tag: " + tag);
        }
        KeymasterArgument arg = getArgumentByTag(tag);
        if (arg == null) {
            return defaultValue;
        }
        return ((KeymasterBlobArgument) arg).blob;
    }

    public void addDate(int tag, Date value) {
        if (KeymasterDefs.getTagType(tag) != 1610612736) {
            throw new IllegalArgumentException("Not a date tag: " + tag);
        }
        if (value == null) {
            throw new NullPointerException("value == nulll");
        }
        if (value.getTime() < 0) {
            throw new IllegalArgumentException("Date tag value out of range: " + value);
        }
        this.mArguments.add(new KeymasterDateArgument(tag, value));
    }

    public void addDateIfNotNull(int tag, Date value) {
        if (KeymasterDefs.getTagType(tag) != 1610612736) {
            throw new IllegalArgumentException("Not a date tag: " + tag);
        }
        if (value != null) {
            addDate(tag, value);
        }
    }

    public Date getDate(int tag, Date defaultValue) {
        if (KeymasterDefs.getTagType(tag) != 1610612736) {
            throw new IllegalArgumentException("Tag is not a date type: " + tag);
        }
        KeymasterArgument arg = getArgumentByTag(tag);
        if (arg == null) {
            return defaultValue;
        }
        Date result = ((KeymasterDateArgument) arg).date;
        if (result.getTime() < 0) {
            throw new IllegalArgumentException("Tag value too large. Tag: " + tag);
        }
        return result;
    }

    private KeymasterArgument getArgumentByTag(int tag) {
        for (KeymasterArgument arg : this.mArguments) {
            if (arg.tag == tag) {
                return arg;
            }
        }
        return null;
    }

    public boolean containsTag(int tag) {
        return getArgumentByTag(tag) != null;
    }

    public int size() {
        return this.mArguments.size();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeTypedList(this.mArguments);
    }

    public void readFromParcel(Parcel in) {
        in.readTypedList(this.mArguments, KeymasterArgument.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static BigInteger toUint64(long value) {
        if (value >= 0) {
            return BigInteger.valueOf(value);
        }
        return BigInteger.valueOf(value).add(UINT64_RANGE);
    }
}
