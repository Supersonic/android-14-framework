package android.p008os;

import android.p008os.Parcelable;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.BinaryOperator;
/* renamed from: android.os.BundleMerger */
/* loaded from: classes3.dex */
public class BundleMerger implements Parcelable {
    public static final Parcelable.Creator<BundleMerger> CREATOR = new Parcelable.Creator<BundleMerger>() { // from class: android.os.BundleMerger.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BundleMerger createFromParcel(Parcel in) {
            return new BundleMerger(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BundleMerger[] newArray(int size) {
            return new BundleMerger[size];
        }
    };
    public static final int STRATEGY_ARRAY_APPEND = 50;
    public static final int STRATEGY_ARRAY_LIST_APPEND = 60;
    public static final int STRATEGY_BOOLEAN_AND = 30;
    public static final int STRATEGY_BOOLEAN_OR = 40;
    public static final int STRATEGY_COMPARABLE_MAX = 4;
    public static final int STRATEGY_COMPARABLE_MIN = 3;
    public static final int STRATEGY_FIRST = 1;
    public static final int STRATEGY_LAST = 2;
    public static final int STRATEGY_NUMBER_ADD = 10;
    public static final int STRATEGY_NUMBER_INCREMENT_FIRST = 20;
    public static final int STRATEGY_NUMBER_INCREMENT_FIRST_AND_ADD = 25;
    public static final int STRATEGY_REJECT = 0;
    private static final String TAG = "BundleMerger";
    private int mDefaultStrategy;
    private final ArrayMap<String, Integer> mStrategies;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.os.BundleMerger$Strategy */
    /* loaded from: classes3.dex */
    public @interface Strategy {
    }

    public BundleMerger() {
        this.mDefaultStrategy = 0;
        this.mStrategies = new ArrayMap<>();
    }

    private BundleMerger(Parcel in) {
        this.mDefaultStrategy = 0;
        this.mStrategies = new ArrayMap<>();
        this.mDefaultStrategy = in.readInt();
        int N = in.readInt();
        for (int i = 0; i < N; i++) {
            this.mStrategies.put(in.readString(), Integer.valueOf(in.readInt()));
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mDefaultStrategy);
        int N = this.mStrategies.size();
        out.writeInt(N);
        for (int i = 0; i < N; i++) {
            out.writeString(this.mStrategies.keyAt(i));
            out.writeInt(this.mStrategies.valueAt(i).intValue());
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void setDefaultMergeStrategy(int strategy) {
        this.mDefaultStrategy = strategy;
    }

    public void setMergeStrategy(String key, int strategy) {
        this.mStrategies.put(key, Integer.valueOf(strategy));
    }

    public int getMergeStrategy(String key) {
        return this.mStrategies.getOrDefault(key, Integer.valueOf(this.mDefaultStrategy)).intValue();
    }

    public BinaryOperator<Bundle> asBinaryOperator() {
        return new BinaryOperator() { // from class: android.os.BundleMerger$$ExternalSyntheticLambda0
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                return BundleMerger.this.merge((Bundle) obj, (Bundle) obj2);
            }
        };
    }

    public Bundle merge(Bundle first, Bundle last) {
        if (first == null && last == null) {
            return null;
        }
        if (first == null) {
            first = Bundle.EMPTY;
        }
        if (last == null) {
            last = Bundle.EMPTY;
        }
        Bundle res = new Bundle();
        res.putAll(first);
        res.putAll(last);
        ArraySet<String> conflictingKeys = new ArraySet<>();
        conflictingKeys.addAll(first.keySet());
        conflictingKeys.retainAll(last.keySet());
        for (int i = 0; i < conflictingKeys.size(); i++) {
            String key = conflictingKeys.valueAt(i);
            int strategy = getMergeStrategy(key);
            Object firstValue = first.get(key);
            Object lastValue = last.get(key);
            try {
                res.putObject(key, merge(strategy, firstValue, lastValue));
            } catch (Exception e) {
                Log.m103w(TAG, "Failed to merge key " + key + " with " + firstValue + " and " + lastValue + " using strategy " + strategy, e);
            }
        }
        return res;
    }

    public static Object merge(int strategy, Object first, Object last) {
        if (first == null) {
            return last;
        }
        if (last == null) {
            return first;
        }
        if (first.getClass() != last.getClass()) {
            throw new IllegalArgumentException("Merging requires consistent classes; first " + first.getClass() + " last " + last.getClass());
        }
        switch (strategy) {
            case 0:
                if (Objects.deepEquals(first, last)) {
                    return first;
                }
                return null;
            case 1:
                return first;
            case 2:
                return last;
            case 3:
                return comparableMin(first, last);
            case 4:
                return comparableMax(first, last);
            case 10:
                return numberAdd(first, last);
            case 20:
                return numberIncrementFirst(first, last);
            case 25:
                return numberAdd(numberIncrementFirst(first, last), last);
            case 30:
                return booleanAnd(first, last);
            case 40:
                return booleanOr(first, last);
            case 50:
                return arrayAppend(first, last);
            case 60:
                return arrayListAppend(first, last);
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static Object comparableMin(Object first, Object last) {
        return ((Comparable) first).compareTo(last) < 0 ? first : last;
    }

    private static Object comparableMax(Object first, Object last) {
        return ((Comparable) first).compareTo(last) >= 0 ? first : last;
    }

    private static Object numberAdd(Object first, Object last) {
        if (first instanceof Integer) {
            return Integer.valueOf(((Integer) first).intValue() + ((Integer) last).intValue());
        }
        if (first instanceof Long) {
            return Long.valueOf(((Long) first).longValue() + ((Long) last).longValue());
        }
        if (first instanceof Float) {
            return Float.valueOf(((Float) first).floatValue() + ((Float) last).floatValue());
        }
        if (first instanceof Double) {
            return Double.valueOf(((Double) first).doubleValue() + ((Double) last).doubleValue());
        }
        throw new IllegalArgumentException("Unable to add " + first.getClass());
    }

    private static Number numberIncrementFirst(Object first, Object last) {
        if (first instanceof Integer) {
            return Integer.valueOf(((Integer) first).intValue() + 1);
        }
        if (first instanceof Long) {
            return Long.valueOf(((Long) first).longValue() + 1);
        }
        throw new IllegalArgumentException("Unable to add " + first.getClass());
    }

    private static Object booleanAnd(Object first, Object last) {
        return Boolean.valueOf(((Boolean) first).booleanValue() && ((Boolean) last).booleanValue());
    }

    private static Object booleanOr(Object first, Object last) {
        return Boolean.valueOf(((Boolean) first).booleanValue() || ((Boolean) last).booleanValue());
    }

    private static Object arrayAppend(Object first, Object last) {
        if (!first.getClass().isArray()) {
            throw new IllegalArgumentException("Unable to append " + first.getClass());
        }
        Class<?> clazz = first.getClass().getComponentType();
        int firstLength = Array.getLength(first);
        int lastLength = Array.getLength(last);
        Object res = Array.newInstance(clazz, firstLength + lastLength);
        System.arraycopy(first, 0, res, 0, firstLength);
        System.arraycopy(last, 0, res, firstLength, lastLength);
        return res;
    }

    private static Object arrayListAppend(Object first, Object last) {
        if (!(first instanceof ArrayList)) {
            throw new IllegalArgumentException("Unable to append " + first.getClass());
        }
        ArrayList<Object> firstList = (ArrayList) first;
        ArrayList<Object> lastList = (ArrayList) last;
        ArrayList<Object> res = new ArrayList<>(firstList.size() + lastList.size());
        res.addAll(firstList);
        res.addAll(lastList);
        return res;
    }
}
