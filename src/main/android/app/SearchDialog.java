package android.app;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.p008os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.internal.C4057R;
/* loaded from: classes.dex */
public class SearchDialog extends Dialog {
    private static final boolean DBG = false;
    private static final String IME_OPTION_NO_MICROPHONE = "nm";
    private static final String INSTANCE_KEY_APPDATA = "data";
    private static final String INSTANCE_KEY_COMPONENT = "comp";
    private static final String INSTANCE_KEY_USER_QUERY = "uQry";
    private static final String LOG_TAG = "SearchDialog";
    private static final int SEARCH_PLATE_LEFT_PADDING_NON_GLOBAL = 7;
    private Context mActivityContext;
    private ImageView mAppIcon;
    private Bundle mAppSearchData;
    private TextView mBadgeLabel;
    private View mCloseSearch;
    private BroadcastReceiver mConfChangeListener;
    private ComponentName mLaunchComponent;
    private final SearchView.OnCloseListener mOnCloseListener;
    private final SearchView.OnQueryTextListener mOnQueryChangeListener;
    private final SearchView.OnSuggestionListener mOnSuggestionSelectionListener;
    private AutoCompleteTextView mSearchAutoComplete;
    private int mSearchAutoCompleteImeOptions;
    private View mSearchPlate;
    private SearchView mSearchView;
    private SearchableInfo mSearchable;
    private String mUserQuery;
    private final Intent mVoiceAppSearchIntent;
    private final Intent mVoiceWebSearchIntent;
    private Drawable mWorkingSpinner;

    static int resolveDialogTheme(Context context) {
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(C4057R.attr.searchDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    public SearchDialog(Context context, SearchManager searchManager) {
        super(context, resolveDialogTheme(context));
        this.mConfChangeListener = new BroadcastReceiver() { // from class: android.app.SearchDialog.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                    SearchDialog.this.onConfigurationChanged();
                }
            }
        };
        this.mOnCloseListener = new SearchView.OnCloseListener() { // from class: android.app.SearchDialog.3
            @Override // android.widget.SearchView.OnCloseListener
            public boolean onClose() {
                return SearchDialog.this.onClosePressed();
            }
        };
        this.mOnQueryChangeListener = new SearchView.OnQueryTextListener() { // from class: android.app.SearchDialog.4
            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextSubmit(String query) {
                SearchDialog.this.dismiss();
                return false;
            }

            @Override // android.widget.SearchView.OnQueryTextListener
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
        this.mOnSuggestionSelectionListener = new SearchView.OnSuggestionListener() { // from class: android.app.SearchDialog.5
            @Override // android.widget.SearchView.OnSuggestionListener
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override // android.widget.SearchView.OnSuggestionListener
            public boolean onSuggestionClick(int position) {
                SearchDialog.this.dismiss();
                return false;
            }
        };
        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
        this.mVoiceWebSearchIntent = intent;
        intent.addFlags(268435456);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        Intent intent2 = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        this.mVoiceAppSearchIntent = intent2;
        intent2.addFlags(268435456);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Dialog
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window theWindow = getWindow();
        WindowManager.LayoutParams lp = theWindow.getAttributes();
        lp.width = -1;
        lp.height = -1;
        lp.gravity = 55;
        lp.softInputMode = 16;
        theWindow.setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }

    private void createContentView() {
        setContentView(C4057R.layout.search_bar);
        SearchView searchView = (SearchView) findViewById(C4057R.C4059id.search_view);
        this.mSearchView = searchView;
        searchView.setIconified(false);
        this.mSearchView.setOnCloseListener(this.mOnCloseListener);
        this.mSearchView.setOnQueryTextListener(this.mOnQueryChangeListener);
        this.mSearchView.setOnSuggestionListener(this.mOnSuggestionSelectionListener);
        this.mSearchView.onActionViewExpanded();
        View findViewById = findViewById(16908327);
        this.mCloseSearch = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: android.app.SearchDialog.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                SearchDialog.this.dismiss();
            }
        });
        this.mBadgeLabel = (TextView) this.mSearchView.findViewById(C4057R.C4059id.search_badge);
        this.mSearchAutoComplete = (AutoCompleteTextView) this.mSearchView.findViewById(C4057R.C4059id.search_src_text);
        this.mAppIcon = (ImageView) findViewById(C4057R.C4059id.search_app_icon);
        this.mSearchPlate = this.mSearchView.findViewById(C4057R.C4059id.search_plate);
        this.mWorkingSpinner = getContext().getDrawable(C4057R.C4058drawable.search_spinner);
        setWorking(false);
        this.mBadgeLabel.setVisibility(8);
        this.mSearchAutoCompleteImeOptions = this.mSearchAutoComplete.getImeOptions();
    }

    public boolean show(String initialQuery, boolean selectInitialQuery, ComponentName componentName, Bundle appSearchData) {
        boolean success = doShow(initialQuery, selectInitialQuery, componentName, appSearchData);
        if (success) {
            this.mSearchAutoComplete.showDropDownAfterLayout();
        }
        return success;
    }

    private boolean doShow(String initialQuery, boolean selectInitialQuery, ComponentName componentName, Bundle appSearchData) {
        if (!show(componentName, appSearchData)) {
            return false;
        }
        setUserQuery(initialQuery);
        if (selectInitialQuery) {
            this.mSearchAutoComplete.selectAll();
            return true;
        }
        return true;
    }

    private boolean show(ComponentName componentName, Bundle appSearchData) {
        SearchManager searchManager = (SearchManager) this.mContext.getSystemService("search");
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(componentName);
        this.mSearchable = searchableInfo;
        if (searchableInfo == null) {
            return false;
        }
        this.mLaunchComponent = componentName;
        this.mAppSearchData = appSearchData;
        this.mActivityContext = searchableInfo.getActivityContext(getContext());
        if (!isShowing()) {
            createContentView();
            this.mSearchView.setSearchableInfo(this.mSearchable);
            this.mSearchView.setAppSearchData(this.mAppSearchData);
            show();
        }
        updateUI();
        return true;
    }

    @Override // android.app.Dialog
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        getContext().registerReceiver(this.mConfChangeListener, filter);
    }

    @Override // android.app.Dialog
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(this.mConfChangeListener);
        this.mLaunchComponent = null;
        this.mAppSearchData = null;
        this.mSearchable = null;
        this.mUserQuery = null;
    }

    public void setWorking(boolean working) {
        this.mWorkingSpinner.setAlpha(working ? 255 : 0);
        this.mWorkingSpinner.setVisible(working, false);
        this.mWorkingSpinner.invalidateSelf();
    }

    @Override // android.app.Dialog
    public Bundle onSaveInstanceState() {
        if (isShowing()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(INSTANCE_KEY_COMPONENT, this.mLaunchComponent);
            bundle.putBundle("data", this.mAppSearchData);
            bundle.putString(INSTANCE_KEY_USER_QUERY, this.mUserQuery);
            return bundle;
        }
        return null;
    }

    @Override // android.app.Dialog
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        ComponentName launchComponent = (ComponentName) savedInstanceState.getParcelable(INSTANCE_KEY_COMPONENT, ComponentName.class);
        Bundle appSearchData = savedInstanceState.getBundle("data");
        String userQuery = savedInstanceState.getString(INSTANCE_KEY_USER_QUERY);
        doShow(userQuery, false, launchComponent, appSearchData);
    }

    public void onConfigurationChanged() {
        if (this.mSearchable != null && isShowing()) {
            updateSearchAppIcon();
            updateSearchBadge();
            if (isLandscapeMode(getContext())) {
                this.mSearchAutoComplete.setInputMethodMode(1);
                if (this.mSearchAutoComplete.isDropDownAlwaysVisible() || enoughToFilter()) {
                    this.mSearchAutoComplete.showDropDown();
                }
            }
        }
    }

    static boolean isLandscapeMode(Context context) {
        return context.getResources().getConfiguration().orientation == 2;
    }

    private boolean enoughToFilter() {
        Filterable filterableAdapter = (Filterable) this.mSearchAutoComplete.getAdapter();
        if (filterableAdapter == null || filterableAdapter.getFilter() == null) {
            return false;
        }
        return this.mSearchAutoComplete.enoughToFilter();
    }

    private void updateUI() {
        if (this.mSearchable != null) {
            this.mDecor.setVisibility(0);
            updateSearchAutoComplete();
            updateSearchAppIcon();
            updateSearchBadge();
            int inputType = this.mSearchable.getInputType();
            if ((inputType & 15) == 1) {
                inputType &= -65537;
                if (this.mSearchable.getSuggestAuthority() != null) {
                    inputType |= 65536;
                }
            }
            this.mSearchAutoComplete.setInputType(inputType);
            int imeOptions = this.mSearchable.getImeOptions();
            this.mSearchAutoCompleteImeOptions = imeOptions;
            this.mSearchAutoComplete.setImeOptions(imeOptions);
            if (this.mSearchable.getVoiceSearchEnabled()) {
                this.mSearchAutoComplete.setPrivateImeOptions(IME_OPTION_NO_MICROPHONE);
            } else {
                this.mSearchAutoComplete.setPrivateImeOptions(null);
            }
        }
    }

    private void updateSearchAutoComplete() {
        this.mSearchAutoComplete.setDropDownDismissedOnCompletion(false);
        this.mSearchAutoComplete.setForceIgnoreOutsideTouch(false);
    }

    private void updateSearchAppIcon() {
        Drawable icon;
        PackageManager pm = getContext().getPackageManager();
        try {
            ActivityInfo info = pm.getActivityInfo(this.mLaunchComponent, 0);
            icon = pm.getApplicationIcon(info.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Drawable icon2 = pm.getDefaultActivityIcon();
            Log.m104w(LOG_TAG, this.mLaunchComponent + " not found, using generic app icon");
            icon = icon2;
        }
        this.mAppIcon.setImageDrawable(icon);
        this.mAppIcon.setVisibility(0);
        View view = this.mSearchPlate;
        view.setPadding(7, view.getPaddingTop(), this.mSearchPlate.getPaddingRight(), this.mSearchPlate.getPaddingBottom());
    }

    private void updateSearchBadge() {
        int visibility = 8;
        Drawable icon = null;
        CharSequence text = null;
        if (this.mSearchable.useBadgeIcon()) {
            icon = this.mActivityContext.getDrawable(this.mSearchable.getIconId());
            visibility = 0;
        } else if (this.mSearchable.useBadgeLabel()) {
            text = this.mActivityContext.getResources().getText(this.mSearchable.getLabelId()).toString();
            visibility = 0;
        }
        this.mBadgeLabel.setCompoundDrawablesWithIntrinsicBounds(icon, (Drawable) null, (Drawable) null, (Drawable) null);
        this.mBadgeLabel.setText(text);
        this.mBadgeLabel.setVisibility(visibility);
    }

    @Override // android.app.Dialog
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.mSearchAutoComplete.isPopupShowing() && isOutOfBounds(this.mSearchPlate, event)) {
            cancel();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(View v, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(this.mContext).getScaledWindowTouchSlop();
        return x < (-slop) || y < (-slop) || x > v.getWidth() + slop || y > v.getHeight() + slop;
    }

    @Override // android.app.Dialog
    public void hide() {
        if (isShowing()) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            super.hide();
        }
    }

    public void launchQuerySearch() {
        launchQuerySearch(0, null);
    }

    protected void launchQuerySearch(int actionKey, String actionMsg) {
        String query = this.mSearchAutoComplete.getText().toString();
        Intent intent = createIntent(Intent.ACTION_SEARCH, null, null, query, actionKey, actionMsg);
        launchIntent(intent);
    }

    private void launchIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        Log.m112d(LOG_TAG, "launching " + intent);
        try {
            getContext().startActivity(intent);
            dismiss();
        } catch (RuntimeException ex) {
            Log.m109e(LOG_TAG, "Failed launch activity: " + intent, ex);
        }
    }

    public void setListSelection(int index) {
        this.mSearchAutoComplete.setListSelection(index);
    }

    private Intent createIntent(String action, Uri data, String extraData, String query, int actionKey, String actionMsg) {
        Intent intent = new Intent(action);
        intent.addFlags(268435456);
        if (data != null) {
            intent.setData(data);
        }
        intent.putExtra(SearchManager.USER_QUERY, this.mUserQuery);
        if (query != null) {
            intent.putExtra("query", query);
        }
        if (extraData != null) {
            intent.putExtra(SearchManager.EXTRA_DATA_KEY, extraData);
        }
        Bundle bundle = this.mAppSearchData;
        if (bundle != null) {
            intent.putExtra(SearchManager.APP_DATA, bundle);
        }
        if (actionKey != 0) {
            intent.putExtra(SearchManager.ACTION_KEY, actionKey);
            intent.putExtra(SearchManager.ACTION_MSG, actionMsg);
        }
        intent.setComponent(this.mSearchable.getSearchActivity());
        return intent;
    }

    /* loaded from: classes.dex */
    public static class SearchBar extends LinearLayout {
        public SearchBar(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SearchBar(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public ActionMode startActionModeForChild(View child, ActionMode.Callback callback, int type) {
            if (type != 0) {
                return super.startActionModeForChild(child, callback, type);
            }
            return null;
        }
    }

    private boolean isEmpty(AutoCompleteTextView actv) {
        return TextUtils.getTrimmedLength(actv.getText()) == 0;
    }

    @Override // android.app.Dialog
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(InputMethodManager.class);
        if (imm != null && imm.isFullscreenMode() && imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0)) {
            return;
        }
        cancel();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onClosePressed() {
        if (isEmpty(this.mSearchAutoComplete)) {
            dismiss();
            return true;
        }
        return false;
    }

    private void setUserQuery(String query) {
        if (query == null) {
            query = "";
        }
        this.mUserQuery = query;
        this.mSearchAutoComplete.setText(query);
        this.mSearchAutoComplete.setSelection(query.length());
    }
}
