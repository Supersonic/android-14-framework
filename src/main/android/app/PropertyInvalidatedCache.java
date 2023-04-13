package android.app;

import android.media.MediaMetrics;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class PropertyInvalidatedCache<Query, Result> {
    private static final boolean DEBUG = false;
    public static final String MODULE_BLUETOOTH = "bluetooth";
    public static final String MODULE_SYSTEM = "system_server";
    public static final String MODULE_TELEPHONY = "telephony";
    public static final String MODULE_TEST = "test";
    static final String NAME_CONTAINS = "-name-has=";
    static final String NAME_LIKE = "-name-like=";
    private static final int NONCE_BYPASS = 3;
    private static final int NONCE_CORKED = 2;
    private static final int NONCE_DISABLED = 1;
    private static final int NONCE_UNSET = 0;
    static final String PROPERTY_CONTAINS = "-property-has=";
    private static final int PROPERTY_FAILURE_RETRY_DELAY_MILLIS = 200;
    private static final int PROPERTY_FAILURE_RETRY_LIMIT = 5;
    static final String PROPERTY_LIKE = "-property-like=";
    private static final String TAG = "PropertyInvalidatedCache";
    private static final boolean VERIFY = false;
    private final LinkedHashMap<Query, Result> mCache;
    private final String mCacheName;
    private long mClears;
    private QueryHandler<Query, Result> mComputer;
    private boolean mDisabled;
    private long mHighWaterMark;
    private long mHits;
    private long mLastSeenNonce;
    private final Object mLock;
    private final int mMaxEntries;
    private long mMissOverflow;
    private long mMisses;
    private volatile SystemProperties.Handle mPropertyHandle;
    private final String mPropertyName;
    private long[] mSkips;
    private static final String[] sNonceName = {"unset", "disabled", "corked", "bypass"};
    private static final Object sCorkLock = new Object();
    private static final HashMap<String, Long> sCorkedInvalidates = new HashMap<>();
    private static final HashMap<String, Integer> sCorks = new HashMap<>();
    private static final Object sGlobalLock = new Object();
    private static final HashSet<String> sDisabledKeys = new HashSet<>();
    private static final WeakHashMap<PropertyInvalidatedCache, Void> sCaches = new WeakHashMap<>();
    private static final HashMap<String, Long> sInvalidates = new HashMap<>();
    private static boolean sEnabled = true;
    private static volatile boolean sTesting = false;
    private static final HashMap<String, Long> sTestingPropertyMap = new HashMap<>();

    /* loaded from: classes.dex */
    public static abstract class QueryHandler<Q, R> {
        public abstract R apply(Q q);

        public boolean shouldBypassCache(Q query) {
            return false;
        }
    }

    public static String createPropertyName(String module, String apiName) {
        int j;
        char[] api = apiName.toCharArray();
        int upper = 0;
        for (int i = 1; i < api.length; i++) {
            if (Character.isUpperCase(api[i])) {
                upper++;
            }
        }
        int i2 = api.length;
        char[] suffix = new char[i2 + upper];
        int j2 = 0;
        for (int i3 = 0; i3 < api.length; i3++) {
            if (Character.isJavaIdentifierPart(api[i3])) {
                if (Character.isUpperCase(api[i3])) {
                    if (i3 > 0) {
                        suffix[j2] = '_';
                        j2++;
                    }
                    j = j2 + 1;
                    suffix[j2] = Character.toLowerCase(api[i3]);
                } else {
                    j = j2 + 1;
                    suffix[j2] = api[i3];
                }
                j2 = j;
            } else {
                throw new IllegalArgumentException("invalid api name");
            }
        }
        return "cache_key." + module + MediaMetrics.SEPARATOR + new String(suffix);
    }

    private static boolean isReservedNonce(long n) {
        return n >= 0 && n <= 3;
    }

    /* loaded from: classes.dex */
    private static class DefaultComputer<Query, Result> extends QueryHandler<Query, Result> {
        final PropertyInvalidatedCache<Query, Result> mCache;

        DefaultComputer(PropertyInvalidatedCache<Query, Result> cache) {
            this.mCache = cache;
        }

        @Override // android.app.PropertyInvalidatedCache.QueryHandler
        public Result apply(Query query) {
            return this.mCache.recompute(query);
        }
    }

    public PropertyInvalidatedCache(int maxEntries, String propertyName) {
        this(maxEntries, propertyName, propertyName);
    }

    public PropertyInvalidatedCache(int maxEntries, String propertyName, String cacheName) {
        this.mLock = new Object();
        this.mHits = 0L;
        this.mMisses = 0L;
        this.mSkips = new long[]{0, 0, 0, 0};
        this.mMissOverflow = 0L;
        this.mHighWaterMark = 0L;
        this.mClears = 0L;
        this.mLastSeenNonce = 0L;
        this.mDisabled = false;
        this.mPropertyName = propertyName;
        this.mCacheName = cacheName;
        this.mMaxEntries = maxEntries;
        this.mComputer = new DefaultComputer(this);
        this.mCache = createMap();
        registerCache();
    }

    public PropertyInvalidatedCache(int maxEntries, String module, String api, String cacheName, QueryHandler<Query, Result> computer) {
        this.mLock = new Object();
        this.mHits = 0L;
        this.mMisses = 0L;
        this.mSkips = new long[]{0, 0, 0, 0};
        this.mMissOverflow = 0L;
        this.mHighWaterMark = 0L;
        this.mClears = 0L;
        this.mLastSeenNonce = 0L;
        this.mDisabled = false;
        this.mPropertyName = createPropertyName(module, api);
        this.mCacheName = cacheName;
        this.mMaxEntries = maxEntries;
        this.mComputer = computer;
        this.mCache = createMap();
        registerCache();
    }

    private LinkedHashMap<Query, Result> createMap() {
        return new LinkedHashMap<Query, Result>(2, 0.75f, true) { // from class: android.app.PropertyInvalidatedCache.1
            @Override // java.util.LinkedHashMap
            protected boolean removeEldestEntry(Map.Entry eldest) {
                int size = size();
                if (size > PropertyInvalidatedCache.this.mHighWaterMark) {
                    PropertyInvalidatedCache.this.mHighWaterMark = size;
                }
                if (size > PropertyInvalidatedCache.this.mMaxEntries) {
                    PropertyInvalidatedCache.this.mMissOverflow++;
                    return true;
                }
                return false;
            }
        };
    }

    private void registerCache() {
        synchronized (sGlobalLock) {
            if (sDisabledKeys.contains(this.mCacheName)) {
                disableInstance();
            }
            sCaches.put(this, null);
        }
    }

    public static void setTestMode(boolean mode) {
        sTesting = mode;
        HashMap<String, Long> hashMap = sTestingPropertyMap;
        synchronized (hashMap) {
            hashMap.clear();
        }
    }

    private static void testPropertyName(String name) {
        HashMap<String, Long> hashMap = sTestingPropertyMap;
        synchronized (hashMap) {
            hashMap.put(name, 0L);
        }
    }

    public void testPropertyName() {
        testPropertyName(this.mPropertyName);
    }

    private long getCurrentNonce() {
        if (sTesting) {
            HashMap<String, Long> hashMap = sTestingPropertyMap;
            synchronized (hashMap) {
                Long n = hashMap.get(this.mPropertyName);
                if (n != null) {
                    return n.longValue();
                }
            }
        }
        SystemProperties.Handle handle = this.mPropertyHandle;
        if (handle == null) {
            handle = SystemProperties.find(this.mPropertyName);
            if (handle == null) {
                return 0L;
            }
            this.mPropertyHandle = handle;
        }
        return handle.getLong(0L);
    }

    private static void setNonce(String name, long val) {
        if (sTesting) {
            HashMap<String, Long> hashMap = sTestingPropertyMap;
            synchronized (hashMap) {
                Long n = hashMap.get(name);
                if (n != null) {
                    hashMap.put(name, Long.valueOf(val));
                    return;
                }
            }
        }
        RuntimeException failure = null;
        for (int attempt = 0; attempt < 5; attempt++) {
            try {
                SystemProperties.set(name, Long.toString(val));
                if (attempt > 0) {
                    Log.m104w(TAG, "Nonce set after " + attempt + " tries");
                    return;
                }
                return;
            } catch (RuntimeException e) {
                if (failure == null) {
                    failure = e;
                }
                try {
                    Thread.sleep(200L);
                } catch (InterruptedException e2) {
                }
            }
        }
        throw failure;
    }

    private static long getNonce(String name) {
        if (sTesting) {
            HashMap<String, Long> hashMap = sTestingPropertyMap;
            synchronized (hashMap) {
                Long n = hashMap.get(name);
                if (n != null) {
                    return n.longValue();
                }
            }
        }
        return SystemProperties.getLong(name, 0L);
    }

    public final void clear() {
        synchronized (this.mLock) {
            this.mCache.clear();
            this.mClears++;
        }
    }

    public Result recompute(Query query) {
        return this.mComputer.apply(query);
    }

    public boolean bypass(Query query) {
        return this.mComputer.shouldBypassCache(query);
    }

    public boolean resultEquals(Result cachedResult, Result fetchedResult) {
        if (fetchedResult != null) {
            return Objects.equals(cachedResult, fetchedResult);
        }
        return true;
    }

    protected Result refresh(Result oldResult, Query query) {
        return oldResult;
    }

    public final void disableInstance() {
        synchronized (this.mLock) {
            this.mDisabled = true;
            clear();
        }
    }

    private static final void disableLocal(String name) {
        synchronized (sGlobalLock) {
            if (sDisabledKeys.contains(name)) {
                return;
            }
            for (PropertyInvalidatedCache cache : sCaches.keySet()) {
                if (name.equals(cache.mCacheName)) {
                    cache.disableInstance();
                }
            }
            sDisabledKeys.add(name);
        }
    }

    public final void forgetDisableLocal() {
        synchronized (sGlobalLock) {
            sDisabledKeys.remove(this.mCacheName);
        }
    }

    public void disableLocal() {
        disableForCurrentProcess();
    }

    public void disableForCurrentProcess() {
        disableLocal(this.mCacheName);
    }

    public static void disableForCurrentProcess(String cacheName) {
        disableLocal(cacheName);
    }

    public final boolean isDisabled() {
        return this.mDisabled || !sEnabled;
    }

    public Result query(Query query) {
        Result cachedResult;
        long currentNonce = !isDisabled() ? getCurrentNonce() : 1L;
        if (bypass(query)) {
            currentNonce = 3;
        }
        while (!isReservedNonce(currentNonce)) {
            synchronized (this.mLock) {
                if (currentNonce == this.mLastSeenNonce) {
                    cachedResult = this.mCache.get(query);
                    if (cachedResult != null) {
                        this.mHits++;
                    }
                } else {
                    clear();
                    this.mLastSeenNonce = currentNonce;
                    cachedResult = null;
                }
            }
            if (cachedResult != null) {
                Result refreshedResult = refresh(cachedResult, query);
                if (refreshedResult != cachedResult) {
                    long afterRefreshNonce = getCurrentNonce();
                    if (currentNonce != afterRefreshNonce) {
                        currentNonce = afterRefreshNonce;
                    } else {
                        synchronized (this.mLock) {
                            if (currentNonce == this.mLastSeenNonce) {
                                if (refreshedResult == null) {
                                    this.mCache.remove(query);
                                } else {
                                    this.mCache.put(query, refreshedResult);
                                }
                            }
                        }
                        return maybeCheckConsistency(query, refreshedResult);
                    }
                } else {
                    return maybeCheckConsistency(query, cachedResult);
                }
            } else {
                Result result = recompute(query);
                synchronized (this.mLock) {
                    if (this.mLastSeenNonce == currentNonce && result != null) {
                        this.mCache.put(query, result);
                    }
                    this.mMisses++;
                }
                return maybeCheckConsistency(query, result);
            }
        }
        if (!this.mDisabled) {
            synchronized (this.mLock) {
                long[] jArr = this.mSkips;
                int i = (int) currentNonce;
                jArr[i] = jArr[i] + 1;
            }
        }
        return recompute(query);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class NoPreloadHolder {
        private static final AtomicLong sNextNonce = new AtomicLong(new Random().nextLong());

        private NoPreloadHolder() {
        }

        public static long next() {
            return sNextNonce.getAndIncrement();
        }
    }

    public final void disableSystemWide() {
        disableSystemWide(this.mPropertyName);
    }

    private static void disableSystemWide(String name) {
        if (!sEnabled) {
            return;
        }
        setNonce(name, 1L);
    }

    public void invalidateCache() {
        invalidateCache(this.mPropertyName);
    }

    public static void invalidateCache(String module, String api) {
        invalidateCache(createPropertyName(module, api));
    }

    public static void invalidateCache(String name) {
        if (!sEnabled) {
            return;
        }
        synchronized (sCorkLock) {
            Integer numberCorks = sCorks.get(name);
            if (numberCorks != null && numberCorks.intValue() > 0) {
                HashMap<String, Long> hashMap = sCorkedInvalidates;
                long count = hashMap.getOrDefault(name, 0L).longValue();
                hashMap.put(name, Long.valueOf(1 + count));
                return;
            }
            invalidateCacheLocked(name);
        }
    }

    private static void invalidateCacheLocked(String name) {
        long newValue;
        long nonce = getNonce(name);
        if (nonce == 1) {
            return;
        }
        do {
            newValue = NoPreloadHolder.next();
        } while (isReservedNonce(newValue));
        setNonce(name, newValue);
        HashMap<String, Long> hashMap = sInvalidates;
        long invalidateCount = hashMap.getOrDefault(name, 0L).longValue();
        hashMap.put(name, Long.valueOf(1 + invalidateCount));
    }

    public static void corkInvalidations(String name) {
        if (!sEnabled) {
            return;
        }
        synchronized (sCorkLock) {
            HashMap<String, Integer> hashMap = sCorks;
            int numberCorks = hashMap.getOrDefault(name, 0).intValue();
            if (numberCorks == 0) {
                long nonce = getNonce(name);
                if (nonce != 0 && nonce != 1) {
                    setNonce(name, 2L);
                }
            } else {
                HashMap<String, Long> hashMap2 = sCorkedInvalidates;
                long count = hashMap2.getOrDefault(name, 0L).longValue();
                hashMap2.put(name, Long.valueOf(1 + count));
            }
            hashMap.put(name, Integer.valueOf(numberCorks + 1));
        }
    }

    public static void uncorkInvalidations(String name) {
        if (!sEnabled) {
            return;
        }
        synchronized (sCorkLock) {
            HashMap<String, Integer> hashMap = sCorks;
            int numberCorks = hashMap.getOrDefault(name, 0).intValue();
            if (numberCorks < 1) {
                throw new AssertionError("cork underflow: " + name);
            }
            if (numberCorks == 1) {
                hashMap.remove(name);
                invalidateCacheLocked(name);
            } else {
                hashMap.put(name, Integer.valueOf(numberCorks - 1));
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class AutoCorker {
        public static final int DEFAULT_AUTO_CORK_DELAY_MS = 50;
        private final int mAutoCorkDelayMs;
        private Handler mHandler;
        private final Object mLock;
        private final String mPropertyName;
        private long mUncorkDeadlineMs;

        public AutoCorker(String propertyName) {
            this(propertyName, 50);
        }

        public AutoCorker(String propertyName, int autoCorkDelayMs) {
            this.mLock = new Object();
            this.mUncorkDeadlineMs = -1L;
            this.mPropertyName = propertyName;
            this.mAutoCorkDelayMs = autoCorkDelayMs;
        }

        public void autoCork() {
            if (Looper.getMainLooper() == null) {
                PropertyInvalidatedCache.invalidateCache(this.mPropertyName);
                return;
            }
            synchronized (this.mLock) {
                boolean alreadyQueued = this.mUncorkDeadlineMs >= 0;
                this.mUncorkDeadlineMs = SystemClock.uptimeMillis() + this.mAutoCorkDelayMs;
                if (!alreadyQueued) {
                    getHandlerLocked().sendEmptyMessageAtTime(0, this.mUncorkDeadlineMs);
                    PropertyInvalidatedCache.corkInvalidations(this.mPropertyName);
                } else {
                    synchronized (PropertyInvalidatedCache.sCorkLock) {
                        long count = ((Long) PropertyInvalidatedCache.sCorkedInvalidates.getOrDefault(this.mPropertyName, 0L)).longValue();
                        PropertyInvalidatedCache.sCorkedInvalidates.put(this.mPropertyName, Long.valueOf(1 + count));
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleMessage(Message msg) {
            synchronized (this.mLock) {
                if (this.mUncorkDeadlineMs < 0) {
                    return;
                }
                long nowMs = SystemClock.uptimeMillis();
                if (this.mUncorkDeadlineMs > nowMs) {
                    this.mUncorkDeadlineMs = this.mAutoCorkDelayMs + nowMs;
                    getHandlerLocked().sendEmptyMessageAtTime(0, this.mUncorkDeadlineMs);
                    return;
                }
                this.mUncorkDeadlineMs = -1L;
                PropertyInvalidatedCache.uncorkInvalidations(this.mPropertyName);
            }
        }

        private Handler getHandlerLocked() {
            if (this.mHandler == null) {
                this.mHandler = new Handler(Looper.getMainLooper()) { // from class: android.app.PropertyInvalidatedCache.AutoCorker.1
                    @Override // android.p008os.Handler
                    public void handleMessage(Message msg) {
                        AutoCorker.this.handleMessage(msg);
                    }
                };
            }
            return this.mHandler;
        }
    }

    private Result maybeCheckConsistency(Query query, Result proposedResult) {
        return proposedResult;
    }

    public final String cacheName() {
        return this.mCacheName;
    }

    public final String propertyName() {
        return this.mPropertyName;
    }

    protected String queryToString(Query query) {
        return Objects.toString(query);
    }

    public static void disableForTestMode() {
        Log.m112d(TAG, "disabling all caches in the process");
        sEnabled = false;
    }

    public boolean getDisabledState() {
        return isDisabled();
    }

    public int size() {
        int size;
        synchronized (this.mLock) {
            size = this.mCache.size();
        }
        return size;
    }

    private static ArrayList<PropertyInvalidatedCache> getActiveCaches() {
        return new ArrayList<>(sCaches.keySet());
    }

    private static ArrayList<Map.Entry<String, Integer>> getActiveCorks() {
        ArrayList<Map.Entry<String, Integer>> arrayList;
        synchronized (sCorkLock) {
            arrayList = new ArrayList<>(sCorks.entrySet());
        }
        return arrayList;
    }

    private static boolean anyDetailed(String[] args) {
        for (String a : args) {
            if (a.startsWith(NAME_CONTAINS) || a.startsWith(NAME_LIKE) || a.startsWith(PROPERTY_CONTAINS) || a.startsWith(PROPERTY_LIKE)) {
                return true;
            }
        }
        return false;
    }

    private static boolean chooses(String arg, String key, String reference, boolean contains) {
        if (arg.startsWith(key)) {
            String value = arg.substring(key.length());
            if (contains) {
                return reference.contains(value);
            }
            return reference.matches(value);
        }
        return false;
    }

    private boolean showDetailed(String[] args) {
        for (String a : args) {
            if (chooses(a, NAME_CONTAINS, cacheName(), true) || chooses(a, NAME_LIKE, cacheName(), false) || chooses(a, PROPERTY_CONTAINS, this.mPropertyName, true) || chooses(a, PROPERTY_LIKE, this.mPropertyName, false)) {
                return true;
            }
        }
        return false;
    }

    private void dumpContents(PrintWriter pw, boolean detailed, String[] args) {
        long invalidateCount;
        long corkedInvalidates;
        Object obj;
        long skips;
        Object[] objArr;
        if (detailed && !showDetailed(args)) {
            return;
        }
        synchronized (sCorkLock) {
            invalidateCount = sInvalidates.getOrDefault(this.mPropertyName, 0L).longValue();
            corkedInvalidates = sCorkedInvalidates.getOrDefault(this.mPropertyName, 0L).longValue();
        }
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                try {
                    pw.println(TextUtils.formatSimple("  Cache Name: %s", cacheName()));
                    pw.println(TextUtils.formatSimple("    Property: %s", this.mPropertyName));
                    long[] jArr = this.mSkips;
                    skips = jArr[2] + jArr[0] + jArr[1] + jArr[3];
                    objArr = new Object[4];
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    objArr[0] = Long.valueOf(this.mHits);
                    objArr[1] = Long.valueOf(this.mMisses);
                    objArr[2] = Long.valueOf(skips);
                    objArr[3] = Long.valueOf(this.mClears);
                    pw.println(TextUtils.formatSimple("    Hits: %d, Misses: %d, Skips: %d, Clears: %d", objArr));
                    pw.println(TextUtils.formatSimple("    Skip-corked: %d, Skip-unset: %d, Skip-bypass: %d, Skip-other: %d", Long.valueOf(this.mSkips[2]), Long.valueOf(this.mSkips[0]), Long.valueOf(this.mSkips[3]), Long.valueOf(this.mSkips[1])));
                    Object[] objArr2 = new Object[3];
                    objArr2[0] = Long.valueOf(this.mLastSeenNonce);
                    objArr2[1] = Long.valueOf(invalidateCount);
                    objArr2[2] = Long.valueOf(corkedInvalidates);
                    pw.println(TextUtils.formatSimple("    Nonce: 0x%016x, Invalidates: %d, CorkedInvalidates: %d", objArr2));
                    pw.println(TextUtils.formatSimple("    Current Size: %d, Max Size: %d, HW Mark: %d, Overflows: %d", Integer.valueOf(this.mCache.size()), Integer.valueOf(this.mMaxEntries), Long.valueOf(this.mHighWaterMark), Long.valueOf(this.mMissOverflow)));
                    Object[] objArr3 = new Object[1];
                    objArr3[0] = this.mDisabled ? "false" : "true";
                    pw.println(TextUtils.formatSimple("    Enabled: %s", objArr3));
                    pw.println("");
                    if (detailed) {
                        Set<Map.Entry<Query, Result>> cacheEntries = this.mCache.entrySet();
                        if (cacheEntries.size() == 0) {
                            return;
                        }
                        pw.println("    Contents:");
                        for (Map.Entry<Query, Result> entry : cacheEntries) {
                            String key = Objects.toString(entry.getKey());
                            String value = Objects.toString(entry.getValue());
                            pw.println(TextUtils.formatSimple("      Key: %s\n      Value: %s\n", key, value));
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                    obj = obj2;
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                obj = obj2;
            }
        }
    }

    private static void dumpCorkInfo(PrintWriter pw) {
        ArrayList<Map.Entry<String, Integer>> activeCorks = getActiveCorks();
        if (activeCorks.size() > 0) {
            pw.println("  Corking Status:");
            for (int i = 0; i < activeCorks.size(); i++) {
                Map.Entry<String, Integer> entry = activeCorks.get(i);
                pw.println(TextUtils.formatSimple("    Property Name: %s Count: %d", entry.getKey(), entry.getValue()));
            }
        }
    }

    private static void dumpCacheInfo(PrintWriter pw, String[] args) {
        ArrayList<PropertyInvalidatedCache> activeCaches;
        if (!sEnabled) {
            pw.println("  Caching is disabled in this process.");
            return;
        }
        boolean detail = anyDetailed(args);
        synchronized (sGlobalLock) {
            activeCaches = getActiveCaches();
            if (!detail) {
                dumpCorkInfo(pw);
            }
        }
        for (int i = 0; i < activeCaches.size(); i++) {
            PropertyInvalidatedCache currentCache = activeCaches.get(i);
            currentCache.dumpContents(pw, detail, args);
        }
    }

    public static void dumpCacheInfo(ParcelFileDescriptor pfd, String[] args) {
        ByteArrayOutputStream barray = new ByteArrayOutputStream();
        PrintWriter bout = new PrintWriter(barray);
        dumpCacheInfo(bout, args);
        bout.close();
        try {
            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            barray.writeTo(out);
            out.close();
            barray.close();
        } catch (IOException e) {
            Log.m110e(TAG, "Failed to dump PropertyInvalidatedCache instances");
        }
    }

    public static void onTrimMemory() {
        ArrayList<PropertyInvalidatedCache> activeCaches;
        synchronized (sGlobalLock) {
            activeCaches = getActiveCaches();
        }
        for (int i = 0; i < activeCaches.size(); i++) {
            activeCaches.get(i).clear();
        }
    }
}
