package android.util;
/* loaded from: classes3.dex */
public class TimeSparseArray<E> extends LongSparseArray<E> {
    private static final String TAG = TimeSparseArray.class.getSimpleName();
    private boolean mWtfReported;

    public int closestIndexOnOrAfter(long time) {
        int size = size();
        int result = size;
        int lo = 0;
        int hi = size - 1;
        while (lo <= hi) {
            int mid = ((hi - lo) / 2) + lo;
            long key = keyAt(mid);
            if (time > key) {
                lo = mid + 1;
            } else if (time < key) {
                hi = mid - 1;
                result = mid;
            } else {
                return mid;
            }
        }
        return result;
    }

    @Override // android.util.LongSparseArray
    public void put(long key, E value) {
        if (indexOfKey(key) >= 0 && !this.mWtfReported) {
            Slog.wtf(TAG, "Overwriting value " + get(key) + " by " + value);
            this.mWtfReported = true;
        }
        super.put(key, value);
    }

    public int closestIndexOnOrBefore(long time) {
        int index = closestIndexOnOrAfter(time);
        if (index < size() && keyAt(index) == time) {
            return index;
        }
        return index - 1;
    }
}
