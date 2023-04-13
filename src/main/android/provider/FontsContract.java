package android.provider;

import android.app.job.JobInfo;
import android.content.ContentUris;
import android.content.Context;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ProviderInfo;
import android.content.p001pm.Signature;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.graphics.fonts.FontStyle;
import android.graphics.fonts.FontVariationAxis;
import android.net.Uri;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.provider.FontsContract;
import android.util.Log;
import android.util.LruCache;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
@Deprecated
/* loaded from: classes3.dex */
public class FontsContract {
    private static final long SYNC_FONT_FETCH_TIMEOUT_MS = 500;
    private static final String TAG = "FontsContract";
    private static final int THREAD_RENEWAL_THRESHOLD_MS = 10000;
    private static volatile Context sContext;
    private static Handler sHandler;
    private static Set<String> sInQueueSet;
    private static HandlerThread sThread;
    private static final Object sLock = new Object();
    private static final LruCache<String, Typeface> sTypefaceCache = new LruCache<>(16);
    private static final Runnable sReplaceDispatcherThreadRunnable = new Runnable() { // from class: android.provider.FontsContract.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (FontsContract.sLock) {
                if (FontsContract.sThread != null) {
                    FontsContract.sThread.quitSafely();
                    FontsContract.sThread = null;
                    FontsContract.sHandler = null;
                }
            }
        }
    };
    private static final Comparator<byte[]> sByteArrayComparator = new Comparator() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return FontsContract.lambda$static$13((byte[]) obj, (byte[]) obj2);
        }
    };

    @Deprecated
    /* loaded from: classes3.dex */
    public static final class Columns implements BaseColumns {
        public static final String FILE_ID = "file_id";
        public static final String ITALIC = "font_italic";
        public static final String RESULT_CODE = "result_code";
        public static final int RESULT_CODE_FONT_NOT_FOUND = 1;
        public static final int RESULT_CODE_FONT_UNAVAILABLE = 2;
        public static final int RESULT_CODE_MALFORMED_QUERY = 3;
        public static final int RESULT_CODE_OK = 0;
        public static final String TTC_INDEX = "font_ttc_index";
        public static final String VARIATION_SETTINGS = "font_variation_settings";
        public static final String WEIGHT = "font_weight";

        private Columns() {
        }
    }

    private FontsContract() {
    }

    public static void setApplicationContextForResources(Context context) {
        sContext = context.getApplicationContext();
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class FontInfo {
        private final FontVariationAxis[] mAxes;
        private final boolean mItalic;
        private final int mResultCode;
        private final int mTtcIndex;
        private final Uri mUri;
        private final int mWeight;

        public FontInfo(Uri uri, int ttcIndex, FontVariationAxis[] axes, int weight, boolean italic, int resultCode) {
            this.mUri = (Uri) Preconditions.checkNotNull(uri);
            this.mTtcIndex = ttcIndex;
            this.mAxes = axes;
            this.mWeight = weight;
            this.mItalic = italic;
            this.mResultCode = resultCode;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public int getTtcIndex() {
            return this.mTtcIndex;
        }

        public FontVariationAxis[] getAxes() {
            return this.mAxes;
        }

        public int getWeight() {
            return this.mWeight;
        }

        public boolean isItalic() {
            return this.mItalic;
        }

        public int getResultCode() {
            return this.mResultCode;
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class FontFamilyResult {
        public static final int STATUS_OK = 0;
        public static final int STATUS_REJECTED = 3;
        public static final int STATUS_UNEXPECTED_DATA_PROVIDED = 2;
        public static final int STATUS_WRONG_CERTIFICATES = 1;
        private final FontInfo[] mFonts;
        private final int mStatusCode;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        @interface FontResultStatus {
        }

        public FontFamilyResult(int statusCode, FontInfo[] fonts) {
            this.mStatusCode = statusCode;
            this.mFonts = fonts;
        }

        public int getStatusCode() {
            return this.mStatusCode;
        }

        public FontInfo[] getFonts() {
            return this.mFonts;
        }
    }

    public static Typeface getFontSync(final FontRequest request) {
        Typeface cachedTypeface;
        final String id = request.getIdentifier();
        LruCache<String, Typeface> lruCache = sTypefaceCache;
        Typeface cachedTypeface2 = lruCache.get(id);
        if (cachedTypeface2 != null) {
            return cachedTypeface2;
        }
        Log.m104w(TAG, "Platform version of downloadable fonts is deprecated. Please use androidx version instead.");
        synchronized (sLock) {
            try {
                cachedTypeface = lruCache.get(id);
            } catch (Throwable th) {
                th = th;
            }
            try {
                if (cachedTypeface != null) {
                    return cachedTypeface;
                }
                if (sHandler == null) {
                    HandlerThread handlerThread = new HandlerThread("fonts", -2);
                    sThread = handlerThread;
                    handlerThread.start();
                    sHandler = new Handler(sThread.getLooper());
                }
                final Lock lock = new ReentrantLock();
                final Condition cond = lock.newCondition();
                final AtomicReference<Typeface> holder = new AtomicReference<>();
                final AtomicBoolean waiting = new AtomicBoolean(true);
                final AtomicBoolean timeout = new AtomicBoolean(false);
                sHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        FontsContract.lambda$getFontSync$0(FontRequest.this, id, holder, lock, timeout, waiting, cond);
                    }
                });
                Handler handler = sHandler;
                Runnable runnable = sReplaceDispatcherThreadRunnable;
                handler.removeCallbacks(runnable);
                sHandler.postDelayed(runnable, JobInfo.MIN_BACKOFF_MILLIS);
                long remaining = TimeUnit.MILLISECONDS.toNanos(SYNC_FONT_FETCH_TIMEOUT_MS);
                lock.lock();
                if (!waiting.get()) {
                    Typeface typeface = holder.get();
                    lock.unlock();
                    return typeface;
                }
                do {
                    try {
                        remaining = cond.awaitNanos(remaining);
                    } catch (InterruptedException e) {
                    }
                    if (!waiting.get()) {
                        Typeface typeface2 = holder.get();
                        lock.unlock();
                        return typeface2;
                    }
                } while (remaining > 0);
                timeout.set(true);
                Log.m104w(TAG, "Remote font fetch timed out: " + request.getProviderAuthority() + "/" + request.getQuery());
                lock.unlock();
                return null;
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getFontSync$0(FontRequest request, String id, AtomicReference holder, Lock lock, AtomicBoolean timeout, AtomicBoolean waiting, Condition cond) {
        try {
            FontFamilyResult result = fetchFonts(sContext, null, request);
            if (result.getStatusCode() == 0) {
                Typeface typeface = buildTypeface(sContext, null, result.getFonts());
                if (typeface != null) {
                    sTypefaceCache.put(id, typeface);
                }
                holder.set(typeface);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        lock.lock();
        try {
            if (!timeout.get()) {
                waiting.set(false);
                cond.signal();
            }
        } finally {
            lock.unlock();
        }
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public static class FontRequestCallback {
        public static final int FAIL_REASON_FONT_LOAD_ERROR = -3;
        public static final int FAIL_REASON_FONT_NOT_FOUND = 1;
        public static final int FAIL_REASON_FONT_UNAVAILABLE = 2;
        public static final int FAIL_REASON_MALFORMED_QUERY = 3;
        public static final int FAIL_REASON_PROVIDER_NOT_FOUND = -1;
        public static final int FAIL_REASON_WRONG_CERTIFICATES = -2;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        @interface FontRequestFailReason {
        }

        public void onTypefaceRetrieved(Typeface typeface) {
        }

        public void onTypefaceRequestFailed(int reason) {
        }
    }

    public static void requestFonts(final Context context, final FontRequest request, Handler handler, final CancellationSignal cancellationSignal, final FontRequestCallback callback) {
        final Handler callerThreadHandler = new Handler();
        final Typeface cachedTypeface = sTypefaceCache.get(request.getIdentifier());
        if (cachedTypeface != null) {
            callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    FontsContract.FontRequestCallback.this.onTypefaceRetrieved(cachedTypeface);
                }
            });
        } else {
            handler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    FontsContract.lambda$requestFonts$12(Context.this, cancellationSignal, request, callerThreadHandler, callback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$requestFonts$12(Context context, CancellationSignal cancellationSignal, FontRequest request, Handler callerThreadHandler, final FontRequestCallback callback) {
        try {
            FontFamilyResult result = fetchFonts(context, cancellationSignal, request);
            final Typeface anotherCachedTypeface = sTypefaceCache.get(request.getIdentifier());
            if (anotherCachedTypeface != null) {
                callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        FontsContract.FontRequestCallback.this.onTypefaceRetrieved(anotherCachedTypeface);
                    }
                });
            } else if (result.getStatusCode() != 0) {
                switch (result.getStatusCode()) {
                    case 1:
                        callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-2);
                            }
                        });
                        return;
                    case 2:
                        callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda5
                            @Override // java.lang.Runnable
                            public final void run() {
                                FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-3);
                            }
                        });
                        return;
                    default:
                        callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda6
                            @Override // java.lang.Runnable
                            public final void run() {
                                FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-3);
                            }
                        });
                        return;
                }
            } else {
                FontInfo[] fonts = result.getFonts();
                if (fonts == null || fonts.length == 0) {
                    callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(1);
                        }
                    });
                    return;
                }
                for (FontInfo font : fonts) {
                    if (font.getResultCode() != 0) {
                        final int resultCode = font.getResultCode();
                        if (resultCode < 0) {
                            callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda8
                                @Override // java.lang.Runnable
                                public final void run() {
                                    FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-3);
                                }
                            });
                            return;
                        } else {
                            callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda9
                                @Override // java.lang.Runnable
                                public final void run() {
                                    FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(resultCode);
                                }
                            });
                            return;
                        }
                    }
                }
                final Typeface typeface = buildTypeface(context, cancellationSignal, fonts);
                if (typeface == null) {
                    callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda10
                        @Override // java.lang.Runnable
                        public final void run() {
                            FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-3);
                        }
                    });
                    return;
                }
                sTypefaceCache.put(request.getIdentifier(), typeface);
                callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda11
                    @Override // java.lang.Runnable
                    public final void run() {
                        FontsContract.FontRequestCallback.this.onTypefaceRetrieved(typeface);
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            callerThreadHandler.post(new Runnable() { // from class: android.provider.FontsContract$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    FontsContract.FontRequestCallback.this.onTypefaceRequestFailed(-1);
                }
            });
        }
    }

    public static FontFamilyResult fetchFonts(Context context, CancellationSignal cancellationSignal, FontRequest request) throws PackageManager.NameNotFoundException {
        if (context.isRestricted()) {
            return new FontFamilyResult(3, null);
        }
        ProviderInfo providerInfo = getProvider(context.getPackageManager(), request);
        if (providerInfo == null) {
            return new FontFamilyResult(1, null);
        }
        try {
            FontInfo[] fonts = getFontFromProvider(context, request, providerInfo.authority, cancellationSignal);
            return new FontFamilyResult(0, fonts);
        } catch (IllegalArgumentException e) {
            return new FontFamilyResult(2, null);
        }
    }

    public static Typeface buildTypeface(Context context, CancellationSignal cancellationSignal, FontInfo[] fonts) {
        if (context.isRestricted()) {
            return null;
        }
        Map<Uri, ByteBuffer> uriBuffer = prepareFontData(context, fonts, cancellationSignal);
        if (uriBuffer.isEmpty()) {
            return null;
        }
        FontFamily.Builder familyBuilder = null;
        for (FontInfo fontInfo : fonts) {
            ByteBuffer buffer = uriBuffer.get(fontInfo.getUri());
            if (buffer != null) {
                try {
                    Font font = new Font.Builder(buffer).setWeight(fontInfo.getWeight()).setSlant(fontInfo.isItalic() ? 1 : 0).setTtcIndex(fontInfo.getTtcIndex()).setFontVariationSettings(fontInfo.getAxes()).build();
                    if (familyBuilder == null) {
                        familyBuilder = new FontFamily.Builder(font);
                    } else {
                        familyBuilder.addFont(font);
                    }
                } catch (IOException e) {
                } catch (IllegalArgumentException e2) {
                    return null;
                }
            }
        }
        if (familyBuilder == null) {
            return null;
        }
        FontFamily family = familyBuilder.build();
        FontStyle normal = new FontStyle(400, 0);
        Font bestFont = family.getFont(0);
        int bestScore = normal.getMatchScore(bestFont.getStyle());
        for (int i = 1; i < family.getSize(); i++) {
            Font candidate = family.getFont(i);
            int score = normal.getMatchScore(candidate.getStyle());
            if (score < bestScore) {
                bestFont = candidate;
                bestScore = score;
            }
        }
        return new Typeface.CustomFallbackBuilder(family).setStyle(bestFont.getStyle()).build();
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:45:0x0079
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:81)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:47)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    private static java.util.Map<android.net.Uri, java.nio.ByteBuffer> prepareFontData(android.content.Context r18, android.provider.FontsContract.FontInfo[] r19, android.p008os.CancellationSignal r20) {
        /*
            r1 = r19
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r2 = r0
            android.content.ContentResolver r3 = r18.getContentResolver()
            int r4 = r1.length
            r0 = 0
            r5 = r0
        Lf:
            if (r5 >= r4) goto L82
            r6 = r1[r5]
            int r0 = r6.getResultCode()
            if (r0 == 0) goto L1d
            r9 = r20
            goto L7f
        L1d:
            android.net.Uri r7 = r6.getUri()
            boolean r0 = r2.containsKey(r7)
            if (r0 == 0) goto L2a
            r9 = r20
            goto L7f
        L2a:
            r8 = 0
            java.lang.String r0 = "r"
            r9 = r20
            android.os.ParcelFileDescriptor r0 = r3.openFileDescriptor(r7, r0, r9)     // Catch: java.io.IOException -> L77
            r10 = r0
            if (r10 == 0) goto L71
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
            java.io.FileDescriptor r11 = r10.getFileDescriptor()     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
            r0.<init>(r11)     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
            r11 = r0
            java.nio.channels.FileChannel r12 = r11.getChannel()     // Catch: java.lang.Throwable -> L56
            long r16 = r12.size()     // Catch: java.lang.Throwable -> L56
            java.nio.channels.FileChannel$MapMode r13 = java.nio.channels.FileChannel.MapMode.READ_ONLY     // Catch: java.lang.Throwable -> L56
            r14 = 0
            java.nio.MappedByteBuffer r0 = r12.map(r13, r14, r16)     // Catch: java.lang.Throwable -> L56
            r8 = r0
            r11.close()     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
            goto L71
        L56:
            r0 = move-exception
            r12 = r0
            r11.close()     // Catch: java.lang.Throwable -> L5c
            goto L61
        L5c:
            r0 = move-exception
            r13 = r0
            r12.addSuppressed(r13)     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
        L61:
            throw r12     // Catch: java.lang.Throwable -> L62 java.io.IOException -> L70
        L62:
            r0 = move-exception
            r11 = r0
            if (r10 == 0) goto L6f
            r10.close()     // Catch: java.lang.Throwable -> L6a
            goto L6f
        L6a:
            r0 = move-exception
            r12 = r0
            r11.addSuppressed(r12)     // Catch: java.io.IOException -> L77
        L6f:
            throw r11     // Catch: java.io.IOException -> L77
        L70:
            r0 = move-exception
        L71:
            if (r10 == 0) goto L76
            r10.close()     // Catch: java.io.IOException -> L77
        L76:
            goto L7c
        L77:
            r0 = move-exception
            goto L7c
        L79:
            r0 = move-exception
            r9 = r20
        L7c:
            r2.put(r7, r8)
        L7f:
            int r5 = r5 + 1
            goto Lf
        L82:
            r9 = r20
            java.util.Map r0 = java.util.Collections.unmodifiableMap(r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: android.provider.FontsContract.prepareFontData(android.content.Context, android.provider.FontsContract$FontInfo[], android.os.CancellationSignal):java.util.Map");
    }

    public static ProviderInfo getProvider(PackageManager packageManager, FontRequest request) throws PackageManager.NameNotFoundException {
        String providerAuthority = request.getProviderAuthority();
        ProviderInfo info = packageManager.resolveContentProvider(providerAuthority, 0);
        if (info == null) {
            throw new PackageManager.NameNotFoundException("No package found for authority: " + providerAuthority);
        }
        if (!info.packageName.equals(request.getProviderPackage())) {
            throw new PackageManager.NameNotFoundException("Found content provider " + providerAuthority + ", but package was not " + request.getProviderPackage());
        }
        if (info.applicationInfo.isSystemApp()) {
            return info;
        }
        PackageInfo packageInfo = packageManager.getPackageInfo(info.packageName, 64);
        List<byte[]> signatures = convertToByteArrayList(packageInfo.signatures);
        Collections.sort(signatures, sByteArrayComparator);
        List<List<byte[]>> requestCertificatesList = request.getCertificates();
        for (int i = 0; i < requestCertificatesList.size(); i++) {
            List<byte[]> requestSignatures = new ArrayList<>(requestCertificatesList.get(i));
            Collections.sort(requestSignatures, sByteArrayComparator);
            if (equalsByteArrayList(signatures, requestSignatures)) {
                return info;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$static$13(byte[] l, byte[] r) {
        if (l.length != r.length) {
            return l.length - r.length;
        }
        for (int i = 0; i < l.length; i++) {
            if (l[i] != r[i]) {
                return l[i] - r[i];
            }
        }
        return 0;
    }

    private static boolean equalsByteArrayList(List<byte[]> signatures, List<byte[]> requestSignatures) {
        if (signatures.size() != requestSignatures.size()) {
            return false;
        }
        for (int i = 0; i < signatures.size(); i++) {
            if (!Arrays.equals(signatures.get(i), requestSignatures.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static List<byte[]> convertToByteArrayList(Signature[] signatures) {
        List<byte[]> shas = new ArrayList<>();
        for (Signature signature : signatures) {
            shas.add(signature.toByteArray());
        }
        return shas;
    }

    public static FontInfo[] getFontFromProvider(Context context, FontRequest request, String authority, CancellationSignal cancellationSignal) {
        Uri fileUri;
        int weight;
        boolean italic;
        ArrayList<FontInfo> result = new ArrayList<>();
        Uri uri = new Uri.Builder().scheme("content").authority(authority).build();
        Uri fileBaseUri = new Uri.Builder().scheme("content").authority(authority).appendPath("file").build();
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"_id", Columns.FILE_ID, Columns.TTC_INDEX, Columns.VARIATION_SETTINGS, Columns.WEIGHT, Columns.ITALIC, Columns.RESULT_CODE}, "query = ?", new String[]{request.getQuery()}, null, cancellationSignal);
        if (cursor != null) {
            try {
                if (cursor.getCount() > 0) {
                    int resultCodeColumnIndex = cursor.getColumnIndex(Columns.RESULT_CODE);
                    result = new ArrayList<>();
                    int idColumnIndex = cursor.getColumnIndexOrThrow("_id");
                    int fileIdColumnIndex = cursor.getColumnIndex(Columns.FILE_ID);
                    int ttcIndexColumnIndex = cursor.getColumnIndex(Columns.TTC_INDEX);
                    int vsColumnIndex = cursor.getColumnIndex(Columns.VARIATION_SETTINGS);
                    int weightColumnIndex = cursor.getColumnIndex(Columns.WEIGHT);
                    int italicColumnIndex = cursor.getColumnIndex(Columns.ITALIC);
                    while (cursor.moveToNext()) {
                        int resultCode = resultCodeColumnIndex != -1 ? cursor.getInt(resultCodeColumnIndex) : 0;
                        int ttcIndex = ttcIndexColumnIndex != -1 ? cursor.getInt(ttcIndexColumnIndex) : 0;
                        String variationSettings = vsColumnIndex != -1 ? cursor.getString(vsColumnIndex) : null;
                        if (fileIdColumnIndex == -1) {
                            long id = cursor.getLong(idColumnIndex);
                            fileUri = ContentUris.withAppendedId(uri, id);
                        } else {
                            long id2 = cursor.getLong(fileIdColumnIndex);
                            fileUri = ContentUris.withAppendedId(fileBaseUri, id2);
                        }
                        if (weightColumnIndex != -1 && italicColumnIndex != -1) {
                            weight = cursor.getInt(weightColumnIndex);
                            boolean z = true;
                            if (cursor.getInt(italicColumnIndex) != 1) {
                                z = false;
                            }
                            italic = z;
                        } else {
                            weight = 400;
                            italic = false;
                        }
                        FontVariationAxis[] axes = FontVariationAxis.fromFontVariationSettings(variationSettings);
                        result.add(new FontInfo(fileUri, ttcIndex, axes, weight, italic, resultCode));
                    }
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
        return (FontInfo[]) result.toArray(new FontInfo[0]);
    }
}
