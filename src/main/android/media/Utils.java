package android.media;

import android.content.Context;
import android.database.Cursor;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.Utils;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Environment;
import android.p008os.FileUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.Executor;
/* loaded from: classes2.dex */
public class Utils {
    private static final String TAG = "Utils";

    public static <T extends Comparable<? super T>> void sortDistinctRanges(Range<T>[] ranges) {
        Arrays.sort(ranges, new Comparator<Range<T>>() { // from class: android.media.Utils.1
            @Override // java.util.Comparator
            public int compare(Range<T> lhs, Range<T> rhs) {
                if (lhs.getUpper().compareTo(rhs.getLower()) < 0) {
                    return -1;
                }
                if (lhs.getLower().compareTo(rhs.getUpper()) > 0) {
                    return 1;
                }
                throw new IllegalArgumentException("sample rate ranges must be distinct (" + lhs + " and " + rhs + NavigationBarInflaterView.KEY_CODE_END);
            }
        });
    }

    public static <T extends Comparable<? super T>> Range<T>[] intersectSortedDistinctRanges(Range<T>[] one, Range<T>[] another) {
        int ix = 0;
        Vector<Range<T>> result = new Vector<>();
        for (Range<T> range : another) {
            while (ix < one.length && one[ix].getUpper().compareTo(range.getLower()) < 0) {
                ix++;
            }
            while (ix < one.length && one[ix].getUpper().compareTo(range.getUpper()) < 0) {
                result.add(range.intersect(one[ix]));
                ix++;
            }
            if (ix == one.length) {
                break;
            }
            if (one[ix].getLower().compareTo(range.getUpper()) <= 0) {
                result.add(range.intersect(one[ix]));
            }
        }
        return (Range[]) result.toArray(new Range[result.size()]);
    }

    public static <T extends Comparable<? super T>> int binarySearchDistinctRanges(Range<T>[] ranges, T value) {
        return Arrays.binarySearch(ranges, Range.create(value, value), new Comparator<Range<T>>() { // from class: android.media.Utils.2
            @Override // java.util.Comparator
            public int compare(Range<T> lhs, Range<T> rhs) {
                if (lhs.getUpper().compareTo(rhs.getLower()) < 0) {
                    return -1;
                }
                if (lhs.getLower().compareTo(rhs.getUpper()) > 0) {
                    return 1;
                }
                return 0;
            }
        });
    }

    static int gcd(int a, int b) {
        if (a == 0 && b == 0) {
            return 1;
        }
        if (b < 0) {
            b = -b;
        }
        if (a < 0) {
            a = -a;
        }
        while (a != 0) {
            int c = b % a;
            b = a;
            a = c;
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Integer> factorRange(Range<Integer> range, int factor) {
        if (factor == 1) {
            return range;
        }
        return Range.create(Integer.valueOf(divUp(range.getLower().intValue(), factor)), Integer.valueOf(range.getUpper().intValue() / factor));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Long> factorRange(Range<Long> range, long factor) {
        if (factor == 1) {
            return range;
        }
        return Range.create(Long.valueOf(divUp(range.getLower().longValue(), factor)), Long.valueOf(range.getUpper().longValue() / factor));
    }

    private static Rational scaleRatio(Rational ratio, int num, int den) {
        int common = gcd(num, den);
        return new Rational((int) (ratio.getNumerator() * (num / common)), (int) (ratio.getDenominator() * (den / common)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Rational> scaleRange(Range<Rational> range, int num, int den) {
        if (num == den) {
            return range;
        }
        return Range.create(scaleRatio(range.getLower(), num, den), scaleRatio(range.getUpper(), num, den));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Integer> alignRange(Range<Integer> range, int align) {
        return range.intersect(Integer.valueOf(divUp(range.getLower().intValue(), align) * align), Integer.valueOf((range.getUpper().intValue() / align) * align));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int divUp(int num, int den) {
        return ((num + den) - 1) / den;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static long divUp(long num, long den) {
        return ((num + den) - 1) / den;
    }

    private static long lcm(int a, int b) {
        if (a == 0 || b == 0) {
            throw new IllegalArgumentException("lce is not defined for zero arguments");
        }
        return (a * b) / gcd(a, b);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Integer> intRangeFor(double v) {
        return Range.create(Integer.valueOf((int) v), Integer.valueOf((int) Math.ceil(v)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Long> longRangeFor(double v) {
        return Range.create(Long.valueOf((long) v), Long.valueOf((long) Math.ceil(v)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Size parseSize(Object o, Size fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            return Size.parseSize((String) o);
        } catch (ClassCastException | NumberFormatException e) {
            Log.m104w(TAG, "could not parse size '" + o + "'");
            return fallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int parseIntSafely(Object o, int fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            String s = (String) o;
            return Integer.parseInt(s);
        } catch (ClassCastException | NumberFormatException e) {
            Log.m104w(TAG, "could not parse integer '" + o + "'");
            return fallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Integer> parseIntRange(Object o, Range<Integer> fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            String s = (String) o;
            int ix = s.indexOf(45);
            if (ix >= 0) {
                return Range.create(Integer.valueOf(Integer.parseInt(s.substring(0, ix), 10)), Integer.valueOf(Integer.parseInt(s.substring(ix + 1), 10)));
            }
            int value = Integer.parseInt(s);
            return Range.create(Integer.valueOf(value), Integer.valueOf(value));
        } catch (ClassCastException | NumberFormatException | IllegalArgumentException e) {
            Log.m104w(TAG, "could not parse integer range '" + o + "'");
            return fallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Long> parseLongRange(Object o, Range<Long> fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            String s = (String) o;
            int ix = s.indexOf(45);
            if (ix >= 0) {
                return Range.create(Long.valueOf(Long.parseLong(s.substring(0, ix), 10)), Long.valueOf(Long.parseLong(s.substring(ix + 1), 10)));
            }
            long value = Long.parseLong(s);
            return Range.create(Long.valueOf(value), Long.valueOf(value));
        } catch (ClassCastException | NumberFormatException | IllegalArgumentException e) {
            Log.m104w(TAG, "could not parse long range '" + o + "'");
            return fallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Range<Rational> parseRationalRange(Object o, Range<Rational> fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            String s = (String) o;
            int ix = s.indexOf(45);
            if (ix >= 0) {
                return Range.create(Rational.parseRational(s.substring(0, ix)), Rational.parseRational(s.substring(ix + 1)));
            }
            Rational value = Rational.parseRational(s);
            return Range.create(value, value);
        } catch (ClassCastException | NumberFormatException | IllegalArgumentException e) {
            Log.m104w(TAG, "could not parse rational range '" + o + "'");
            return fallback;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Pair<Size, Size> parseSizeRange(Object o) {
        if (o == null) {
            return null;
        }
        try {
            String s = (String) o;
            int ix = s.indexOf(45);
            if (ix >= 0) {
                return Pair.create(Size.parseSize(s.substring(0, ix)), Size.parseSize(s.substring(ix + 1)));
            }
            Size value = Size.parseSize(s);
            return Pair.create(value, value);
        } catch (ClassCastException | NumberFormatException | IllegalArgumentException e) {
            Log.m104w(TAG, "could not parse size range '" + o + "'");
            return null;
        }
    }

    public static File getUniqueExternalFile(Context context, String subdirectory, String fileName, String mimeType) {
        File externalStorage = Environment.getExternalStoragePublicDirectory(subdirectory);
        externalStorage.mkdirs();
        try {
            File outFile = FileUtils.buildUniqueFile(externalStorage, mimeType, fileName);
            return outFile;
        } catch (FileNotFoundException e) {
            Log.m110e(TAG, "Unable to get a unique file name: " + e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String getFileDisplayNameFromUri(Context context, Uri uri) {
        String scheme = uri.getScheme();
        if ("file".equals(scheme)) {
            return uri.getLastPathSegment();
        }
        if ("content".equals(scheme)) {
            String[] proj = {"_display_name"};
            Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        String string = cursor.getString(cursor.getColumnIndex("_display_name"));
                        if (cursor != null) {
                            cursor.close();
                        }
                        return string;
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        try {
                            cursor.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return uri.toString();
    }

    /* loaded from: classes2.dex */
    public static class ListenerList<V> {
        private final boolean mClearCallingIdentity;
        private final boolean mForceRemoveConsistency;
        private HashMap<Object, ListenerWithCancellation<V>> mListeners;
        private final boolean mRestrictSingleCallerOnEvent;

        /* loaded from: classes2.dex */
        public interface Listener<V> {
            void onEvent(int i, V v);
        }

        /* loaded from: classes2.dex */
        private interface ListenerWithCancellation<V> extends Listener<V> {
            void cancel();
        }

        public ListenerList() {
            this(true, true, false);
        }

        public ListenerList(boolean restrictSingleCallerOnEvent, boolean clearCallingIdentity, boolean forceRemoveConsistency) {
            this.mListeners = new HashMap<>();
            this.mRestrictSingleCallerOnEvent = restrictSingleCallerOnEvent;
            this.mClearCallingIdentity = clearCallingIdentity;
            this.mForceRemoveConsistency = forceRemoveConsistency;
        }

        public void add(Object key, Executor executor, Listener<V> listener) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(executor);
            Objects.requireNonNull(listener);
            ListenerWithCancellation<V> listenerWithCancellation = new C17551(executor, listener);
            synchronized (this.mListeners) {
                this.mListeners.put(key, listenerWithCancellation);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: android.media.Utils$ListenerList$1 */
        /* loaded from: classes2.dex */
        public class C17551 implements ListenerWithCancellation<V> {
            final /* synthetic */ Executor val$executor;
            final /* synthetic */ Listener val$listener;
            private final Object mLock = new Object();
            private volatile boolean mCancelled = false;

            C17551(Executor executor, Listener listener) {
                this.val$executor = executor;
                this.val$listener = listener;
            }

            @Override // android.media.Utils.ListenerList.Listener
            public void onEvent(final int eventCode, final V info) {
                Executor executor = this.val$executor;
                final Listener listener = this.val$listener;
                executor.execute(new Runnable() { // from class: android.media.Utils$ListenerList$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Utils.ListenerList.C17551.this.lambda$onEvent$0(listener, eventCode, info);
                    }
                });
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onEvent$0(Listener listener, int eventCode, Object info) {
                if (ListenerList.this.mRestrictSingleCallerOnEvent || ListenerList.this.mForceRemoveConsistency) {
                    synchronized (this.mLock) {
                        if (this.mCancelled) {
                            return;
                        }
                        listener.onEvent(eventCode, info);
                    }
                } else if (this.mCancelled) {
                } else {
                    listener.onEvent(eventCode, info);
                }
            }

            @Override // android.media.Utils.ListenerList.ListenerWithCancellation
            public void cancel() {
                if (ListenerList.this.mForceRemoveConsistency) {
                    synchronized (this.mLock) {
                        this.mCancelled = true;
                    }
                    return;
                }
                this.mCancelled = true;
            }
        }

        public void remove(Object key) {
            Objects.requireNonNull(key);
            synchronized (this.mListeners) {
                ListenerWithCancellation<V> listener = this.mListeners.get(key);
                if (listener == null) {
                    return;
                }
                this.mListeners.remove(key);
                listener.cancel();
            }
        }

        public void notify(int eventCode, V info) {
            synchronized (this.mListeners) {
                if (this.mListeners.size() == 0) {
                    return;
                }
                Object[] listeners = this.mListeners.values().toArray();
                Long identity = this.mClearCallingIdentity ? Long.valueOf(Binder.clearCallingIdentity()) : null;
                try {
                    for (Object object : listeners) {
                        ListenerWithCancellation<V> listener = (ListenerWithCancellation) object;
                        listener.onEvent(eventCode, info);
                    }
                } finally {
                    if (identity != null) {
                        Binder.restoreCallingIdentity(identity.longValue());
                    }
                }
            }
        }
    }
}
