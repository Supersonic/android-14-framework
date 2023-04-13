package android.app;

import android.annotation.SystemApi;
import android.app.ISearchManager;
import android.app.slice.Slice;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.p001pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.List;
/* loaded from: classes.dex */
public class SearchManager implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener {
    public static final String ACTION_KEY = "action_key";
    public static final String ACTION_MSG = "action_msg";
    public static final String APP_DATA = "app_data";
    public static final String CONTEXT_IS_VOICE = "android.search.CONTEXT_IS_VOICE";
    public static final String CURSOR_EXTRA_KEY_IN_PROGRESS = "in_progress";
    private static final boolean DBG = false;
    public static final String DISABLE_VOICE_SEARCH = "android.search.DISABLE_VOICE_SEARCH";
    public static final String EXTRA_DATA_KEY = "intent_extra_data_key";
    public static final String EXTRA_NEW_SEARCH = "new_search";
    public static final String EXTRA_SELECT_QUERY = "select_query";
    public static final String EXTRA_WEB_SEARCH_PENDINGINTENT = "web_search_pendingintent";
    public static final int FLAG_QUERY_REFINEMENT = 1;
    public static final String INTENT_ACTION_GLOBAL_SEARCH = "android.search.action.GLOBAL_SEARCH";
    public static final String INTENT_ACTION_SEARCHABLES_CHANGED = "android.search.action.SEARCHABLES_CHANGED";
    public static final String INTENT_ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS";
    public static final String INTENT_ACTION_SEARCH_SETTINGS_CHANGED = "android.search.action.SETTINGS_CHANGED";
    public static final String INTENT_ACTION_WEB_SEARCH_SETTINGS = "android.search.action.WEB_SEARCH_SETTINGS";
    public static final String INTENT_GLOBAL_SEARCH_ACTIVITY_CHANGED = "android.search.action.GLOBAL_SEARCH_ACTIVITY_CHANGED";
    public static final char MENU_KEY = 's';
    public static final int MENU_KEYCODE = 47;
    public static final String QUERY = "query";
    public static final String SEARCH_MODE = "search_mode";
    public static final String SHORTCUT_MIME_TYPE = "vnd.android.cursor.item/vnd.android.search.suggest";
    public static final String SUGGEST_COLUMN_AUDIO_CHANNEL_CONFIG = "suggest_audio_channel_config";
    public static final String SUGGEST_COLUMN_CONTENT_TYPE = "suggest_content_type";
    public static final String SUGGEST_COLUMN_DURATION = "suggest_duration";
    public static final String SUGGEST_COLUMN_FLAGS = "suggest_flags";
    public static final String SUGGEST_COLUMN_FORMAT = "suggest_format";
    public static final String SUGGEST_COLUMN_ICON_1 = "suggest_icon_1";
    public static final String SUGGEST_COLUMN_ICON_2 = "suggest_icon_2";
    public static final String SUGGEST_COLUMN_INTENT_ACTION = "suggest_intent_action";
    public static final String SUGGEST_COLUMN_INTENT_DATA = "suggest_intent_data";
    public static final String SUGGEST_COLUMN_INTENT_DATA_ID = "suggest_intent_data_id";
    public static final String SUGGEST_COLUMN_INTENT_EXTRA_DATA = "suggest_intent_extra_data";
    public static final String SUGGEST_COLUMN_IS_LIVE = "suggest_is_live";
    public static final String SUGGEST_COLUMN_LAST_ACCESS_HINT = "suggest_last_access_hint";
    public static final String SUGGEST_COLUMN_PRODUCTION_YEAR = "suggest_production_year";
    public static final String SUGGEST_COLUMN_PURCHASE_PRICE = "suggest_purchase_price";
    public static final String SUGGEST_COLUMN_QUERY = "suggest_intent_query";
    public static final String SUGGEST_COLUMN_RATING_SCORE = "suggest_rating_score";
    public static final String SUGGEST_COLUMN_RATING_STYLE = "suggest_rating_style";
    public static final String SUGGEST_COLUMN_RENTAL_PRICE = "suggest_rental_price";
    public static final String SUGGEST_COLUMN_RESULT_CARD_IMAGE = "suggest_result_card_image";
    public static final String SUGGEST_COLUMN_SHORTCUT_ID = "suggest_shortcut_id";
    public static final String SUGGEST_COLUMN_SPINNER_WHILE_REFRESHING = "suggest_spinner_while_refreshing";
    public static final String SUGGEST_COLUMN_TEXT_1 = "suggest_text_1";
    public static final String SUGGEST_COLUMN_TEXT_2 = "suggest_text_2";
    public static final String SUGGEST_COLUMN_TEXT_2_URL = "suggest_text_2_url";
    public static final String SUGGEST_COLUMN_VIDEO_HEIGHT = "suggest_video_height";
    public static final String SUGGEST_COLUMN_VIDEO_WIDTH = "suggest_video_width";
    public static final String SUGGEST_MIME_TYPE = "vnd.android.cursor.dir/vnd.android.search.suggest";
    public static final String SUGGEST_NEVER_MAKE_SHORTCUT = "_-1";
    public static final String SUGGEST_PARAMETER_LIMIT = "limit";
    public static final String SUGGEST_URI_PATH_QUERY = "search_suggest_query";
    public static final String SUGGEST_URI_PATH_SHORTCUT = "search_suggest_shortcut";
    private static final String TAG = "SearchManager";
    public static final String USER_QUERY = "user_query";
    private final Context mContext;
    final Handler mHandler;
    private SearchDialog mSearchDialog;
    OnDismissListener mDismissListener = null;
    OnCancelListener mCancelListener = null;
    private final ISearchManager mService = ISearchManager.Stub.asInterface(ServiceManager.getServiceOrThrow("search"));

    /* loaded from: classes.dex */
    public interface OnCancelListener {
        void onCancel();
    }

    /* loaded from: classes.dex */
    public interface OnDismissListener {
        void onDismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SearchManager(Context context, Handler handler) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, ComponentName launchActivity, Bundle appSearchData, boolean globalSearch) {
        startSearch(initialQuery, selectInitialQuery, launchActivity, appSearchData, globalSearch, null);
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, ComponentName launchActivity, Bundle appSearchData, boolean globalSearch, Rect sourceBounds) {
        if (globalSearch) {
            startGlobalSearch(initialQuery, selectInitialQuery, appSearchData, sourceBounds);
            return;
        }
        UiModeManager uiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        if (uiModeManager.getCurrentModeType() != 4) {
            ensureSearchDialog();
            this.mSearchDialog.show(initialQuery, selectInitialQuery, launchActivity, appSearchData);
        }
    }

    private void ensureSearchDialog() {
        if (this.mSearchDialog == null) {
            SearchDialog searchDialog = new SearchDialog(this.mContext, this);
            this.mSearchDialog = searchDialog;
            searchDialog.setOnCancelListener(this);
            this.mSearchDialog.setOnDismissListener(this);
        }
    }

    void startGlobalSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, Rect sourceBounds) {
        Bundle appSearchData2;
        ComponentName globalSearchActivity = getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            Log.m104w(TAG, "No global search activity found.");
            return;
        }
        Intent intent = new Intent(INTENT_ACTION_GLOBAL_SEARCH);
        intent.addFlags(268435456);
        intent.setComponent(globalSearchActivity);
        if (appSearchData == null) {
            appSearchData2 = new Bundle();
        } else {
            appSearchData2 = new Bundle(appSearchData);
        }
        if (!appSearchData2.containsKey(Slice.SUBTYPE_SOURCE)) {
            appSearchData2.putString(Slice.SUBTYPE_SOURCE, this.mContext.getPackageName());
        }
        intent.putExtra(APP_DATA, appSearchData2);
        if (!TextUtils.isEmpty(initialQuery)) {
            intent.putExtra("query", initialQuery);
        }
        if (selectInitialQuery) {
            intent.putExtra(EXTRA_SELECT_QUERY, selectInitialQuery);
        }
        intent.setSourceBounds(sourceBounds);
        try {
            this.mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.m110e(TAG, "Global search activity not found: " + globalSearchActivity);
        }
    }

    public List<ResolveInfo> getGlobalSearchActivities() {
        try {
            return this.mService.getGlobalSearchActivities();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public ComponentName getGlobalSearchActivity() {
        try {
            return this.mService.getGlobalSearchActivity();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public ComponentName getWebSearchActivity() {
        try {
            return this.mService.getWebSearchActivity();
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void triggerSearch(String query, ComponentName launchActivity, Bundle appSearchData) {
        if (query == null || TextUtils.getTrimmedLength(query) == 0) {
            Log.m104w(TAG, "triggerSearch called with empty query, ignoring.");
            return;
        }
        startSearch(query, false, launchActivity, appSearchData, false);
        this.mSearchDialog.launchQuerySearch();
    }

    public void stopSearch() {
        SearchDialog searchDialog = this.mSearchDialog;
        if (searchDialog != null) {
            searchDialog.cancel();
        }
    }

    public boolean isVisible() {
        SearchDialog searchDialog = this.mSearchDialog;
        if (searchDialog == null) {
            return false;
        }
        return searchDialog.isShowing();
    }

    public void setOnDismissListener(OnDismissListener listener) {
        this.mDismissListener = listener;
    }

    public void setOnCancelListener(OnCancelListener listener) {
        this.mCancelListener = listener;
    }

    @Override // android.content.DialogInterface.OnCancelListener
    @Deprecated
    public void onCancel(DialogInterface dialog) {
        OnCancelListener onCancelListener = this.mCancelListener;
        if (onCancelListener != null) {
            onCancelListener.onCancel();
        }
    }

    @Override // android.content.DialogInterface.OnDismissListener
    @Deprecated
    public void onDismiss(DialogInterface dialog) {
        OnDismissListener onDismissListener = this.mDismissListener;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public SearchableInfo getSearchableInfo(ComponentName componentName) {
        try {
            return this.mService.getSearchableInfo(componentName);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public Cursor getSuggestions(SearchableInfo searchable, String query) {
        return getSuggestions(searchable, query, -1);
    }

    public Cursor getSuggestions(SearchableInfo searchable, String query, int limit) {
        String authority;
        String[] selArgs;
        if (searchable == null || (authority = searchable.getSuggestAuthority()) == null) {
            return null;
        }
        Uri.Builder uriBuilder = new Uri.Builder().scheme("content").authority(authority).query("").fragment("");
        String contentPath = searchable.getSuggestPath();
        if (contentPath != null) {
            uriBuilder.appendEncodedPath(contentPath);
        }
        uriBuilder.appendPath(SUGGEST_URI_PATH_QUERY);
        String selection = searchable.getSuggestSelection();
        if (selection != null) {
            String[] selArgs2 = {query};
            selArgs = selArgs2;
        } else {
            uriBuilder.appendPath(query);
            selArgs = null;
        }
        if (limit > 0) {
            uriBuilder.appendQueryParameter("limit", String.valueOf(limit));
        }
        Uri uri = uriBuilder.build();
        return this.mContext.getContentResolver().query(uri, null, selection, selArgs, null);
    }

    public List<SearchableInfo> getSearchablesInGlobalSearch() {
        try {
            return this.mService.getSearchablesInGlobalSearch();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Intent getAssistIntent(boolean inclContext) {
        try {
            Intent intent = new Intent(Intent.ACTION_ASSIST);
            if (inclContext) {
                IActivityTaskManager am = ActivityTaskManager.getService();
                Bundle extras = am.getAssistContextExtras(0);
                if (extras != null) {
                    intent.replaceExtras(extras);
                }
            }
            return intent;
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void launchAssist(Bundle args) {
        try {
            ISearchManager iSearchManager = this.mService;
            if (iSearchManager == null) {
                return;
            }
            iSearchManager.launchAssist(this.mContext.getUserId(), args);
        } catch (RemoteException re) {
            throw re.rethrowFromSystemServer();
        }
    }
}
