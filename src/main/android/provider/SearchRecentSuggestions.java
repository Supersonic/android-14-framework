package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.concurrent.Semaphore;
/* loaded from: classes3.dex */
public class SearchRecentSuggestions {
    private static final String LOG_TAG = "SearchSuggestions";
    private static final int MAX_HISTORY_COUNT = 250;
    public static final int QUERIES_PROJECTION_DATE_INDEX = 1;
    public static final int QUERIES_PROJECTION_DISPLAY1_INDEX = 3;
    public static final int QUERIES_PROJECTION_DISPLAY2_INDEX = 4;
    public static final int QUERIES_PROJECTION_QUERY_INDEX = 2;
    private final String mAuthority;
    private final Context mContext;
    private final Uri mSuggestionsUri;
    private final boolean mTwoLineDisplay;
    public static final String[] QUERIES_PROJECTION_1LINE = {"_id", "date", "query", SuggestionColumns.DISPLAY1};
    public static final String[] QUERIES_PROJECTION_2LINE = {"_id", "date", "query", SuggestionColumns.DISPLAY1, SuggestionColumns.DISPLAY2};
    private static final Semaphore sWritesInProgress = new Semaphore(0);

    /* loaded from: classes3.dex */
    private static class SuggestionColumns implements BaseColumns {
        public static final String DATE = "date";
        public static final String DISPLAY1 = "display1";
        public static final String DISPLAY2 = "display2";
        public static final String QUERY = "query";

        private SuggestionColumns() {
        }
    }

    public SearchRecentSuggestions(Context context, String authority, int mode) {
        if (TextUtils.isEmpty(authority) || (mode & 1) == 0) {
            throw new IllegalArgumentException();
        }
        this.mTwoLineDisplay = (mode & 2) != 0;
        this.mContext = context;
        String str = new String(authority);
        this.mAuthority = str;
        this.mSuggestionsUri = Uri.parse("content://" + str + "/suggestions");
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [android.provider.SearchRecentSuggestions$1] */
    public void saveRecentQuery(final String queryString, final String line2) {
        if (TextUtils.isEmpty(queryString)) {
            return;
        }
        if (!this.mTwoLineDisplay && !TextUtils.isEmpty(line2)) {
            throw new IllegalArgumentException();
        }
        new Thread("saveRecentQuery") { // from class: android.provider.SearchRecentSuggestions.1
            @Override // java.lang.Thread, java.lang.Runnable
            public void run() {
                SearchRecentSuggestions.this.saveRecentQueryBlocking(queryString, line2);
                SearchRecentSuggestions.sWritesInProgress.release();
            }
        }.start();
    }

    void waitForSave() {
        Semaphore semaphore;
        do {
            semaphore = sWritesInProgress;
            semaphore.acquireUninterruptibly();
        } while (semaphore.availablePermits() > 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void saveRecentQueryBlocking(String queryString, String line2) {
        ContentResolver cr = this.mContext.getContentResolver();
        long now = System.currentTimeMillis();
        try {
            ContentValues values = new ContentValues();
            values.put(SuggestionColumns.DISPLAY1, queryString);
            if (this.mTwoLineDisplay) {
                values.put(SuggestionColumns.DISPLAY2, line2);
            }
            values.put("query", queryString);
            values.put("date", Long.valueOf(now));
            cr.insert(this.mSuggestionsUri, values);
        } catch (RuntimeException e) {
            Log.m109e(LOG_TAG, "saveRecentQuery", e);
        }
        truncateHistory(cr, 250);
    }

    public void clearHistory() {
        ContentResolver cr = this.mContext.getContentResolver();
        truncateHistory(cr, 0);
    }

    protected void truncateHistory(ContentResolver cr, int maxEntries) {
        if (maxEntries < 0) {
            throw new IllegalArgumentException();
        }
        String selection = null;
        if (maxEntries > 0) {
            try {
                selection = "_id IN (SELECT _id FROM suggestions ORDER BY date DESC LIMIT -1 OFFSET " + String.valueOf(maxEntries) + NavigationBarInflaterView.KEY_CODE_END;
            } catch (RuntimeException e) {
                Log.m109e(LOG_TAG, "truncateHistory", e);
                return;
            }
        }
        cr.delete(this.mSuggestionsUri, selection, null);
    }
}
