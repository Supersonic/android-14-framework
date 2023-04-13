package android.preference;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.TypedArray;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.C4057R;
@Deprecated
/* loaded from: classes3.dex */
public abstract class PreferenceFragment extends Fragment implements PreferenceManager.OnPreferenceTreeClickListener {
    private static final int FIRST_REQUEST_CODE = 100;
    private static final int MSG_BIND_PREFERENCES = 1;
    private static final String PREFERENCES_TAG = "android:preferences";
    private boolean mHavePrefs;
    private boolean mInitDone;
    private ListView mList;
    private PreferenceManager mPreferenceManager;
    private int mLayoutResId = C4057R.layout.preference_list_fragment;
    private Handler mHandler = new Handler() { // from class: android.preference.PreferenceFragment.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    PreferenceFragment.this.bindPreferences();
                    return;
                default:
                    return;
            }
        }
    };
    private final Runnable mRequestFocus = new Runnable() { // from class: android.preference.PreferenceFragment.2
        @Override // java.lang.Runnable
        public void run() {
            PreferenceFragment.this.mList.focusableViewAvailable(PreferenceFragment.this.mList);
        }
    };
    private View.OnKeyListener mListOnKeyListener = new View.OnKeyListener() { // from class: android.preference.PreferenceFragment.3
        @Override // android.view.View.OnKeyListener
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Object selectedItem = PreferenceFragment.this.mList.getSelectedItem();
            if (selectedItem instanceof Preference) {
                View selectedView = PreferenceFragment.this.mList.getSelectedView();
                return ((Preference) selectedItem).onKey(selectedView, keyCode, event);
            }
            return false;
        }
    };

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnPreferenceStartFragmentCallback {
        boolean onPreferenceStartFragment(PreferenceFragment preferenceFragment, Preference preference);
    }

    @Override // android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getActivity(), 100);
        this.mPreferenceManager = preferenceManager;
        preferenceManager.setFragment(this);
    }

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        TypedArray a = getActivity().obtainStyledAttributes(null, C4057R.styleable.PreferenceFragment, 16844038, 0);
        this.mLayoutResId = a.getResourceId(0, this.mLayoutResId);
        a.recycle();
        return inflater.inflate(this.mLayoutResId, container, false);
    }

    @Override // android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TypedArray a = getActivity().obtainStyledAttributes(null, C4057R.styleable.PreferenceFragment, 16844038, 0);
        ListView lv = (ListView) view.findViewById(16908298);
        if (lv != null && a.hasValueOrEmpty(1)) {
            lv.setDivider(a.getDrawable(1));
        }
        a.recycle();
    }

    @Override // android.app.Fragment
    public void onActivityCreated(Bundle savedInstanceState) {
        Bundle container;
        PreferenceScreen preferenceScreen;
        super.onActivityCreated(savedInstanceState);
        if (this.mHavePrefs) {
            bindPreferences();
        }
        this.mInitDone = true;
        if (savedInstanceState != null && (container = savedInstanceState.getBundle(PREFERENCES_TAG)) != null && (preferenceScreen = getPreferenceScreen()) != null) {
            preferenceScreen.restoreHierarchyState(container);
        }
    }

    @Override // android.app.Fragment
    public void onStart() {
        super.onStart();
        this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
    }

    @Override // android.app.Fragment
    public void onStop() {
        super.onStop();
        this.mPreferenceManager.dispatchActivityStop();
        this.mPreferenceManager.setOnPreferenceTreeClickListener(null);
    }

    @Override // android.app.Fragment
    public void onDestroyView() {
        ListView listView = this.mList;
        if (listView != null) {
            listView.setOnKeyListener(null);
        }
        this.mList = null;
        this.mHandler.removeCallbacks(this.mRequestFocus);
        this.mHandler.removeMessages(1);
        super.onDestroyView();
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        this.mPreferenceManager.dispatchActivityDestroy();
    }

    @Override // android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    @Override // android.app.Fragment
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mPreferenceManager.dispatchActivityResult(requestCode, resultCode, data);
    }

    public PreferenceManager getPreferenceManager() {
        return this.mPreferenceManager;
    }

    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        if (this.mPreferenceManager.setPreferences(preferenceScreen) && preferenceScreen != null) {
            onUnbindPreferences();
            this.mHavePrefs = true;
            if (this.mInitDone) {
                postBindPreferences();
            }
        }
    }

    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceManager.getPreferenceScreen();
    }

    public void addPreferencesFromIntent(Intent intent) {
        requirePreferenceManager();
        setPreferenceScreen(this.mPreferenceManager.inflateFromIntent(intent, getPreferenceScreen()));
    }

    public void addPreferencesFromResource(int preferencesResId) {
        requirePreferenceManager();
        setPreferenceScreen(this.mPreferenceManager.inflateFromResource(getActivity(), preferencesResId, getPreferenceScreen()));
    }

    @Override // android.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference.getFragment() != null && (getActivity() instanceof OnPreferenceStartFragmentCallback)) {
            return ((OnPreferenceStartFragmentCallback) getActivity()).onPreferenceStartFragment(this, preference);
        }
        return false;
    }

    public Preference findPreference(CharSequence key) {
        PreferenceManager preferenceManager = this.mPreferenceManager;
        if (preferenceManager == null) {
            return null;
        }
        return preferenceManager.findPreference(key);
    }

    private void requirePreferenceManager() {
        if (this.mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences() {
        if (this.mHandler.hasMessages(1)) {
            return;
        }
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindPreferences() {
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        if (preferenceScreen != null) {
            View root = getView();
            if (root != null) {
                View titleView = root.findViewById(16908310);
                if (titleView instanceof TextView) {
                    CharSequence title = preferenceScreen.getTitle();
                    if (TextUtils.isEmpty(title)) {
                        titleView.setVisibility(8);
                    } else {
                        ((TextView) titleView).setText(title);
                        titleView.setVisibility(0);
                    }
                }
            }
            preferenceScreen.bind(getListView());
        }
        onBindPreferences();
    }

    protected void onBindPreferences() {
    }

    protected void onUnbindPreferences() {
    }

    public ListView getListView() {
        ensureList();
        return this.mList;
    }

    public boolean hasListView() {
        if (this.mList != null) {
            return true;
        }
        View root = getView();
        if (root == null) {
            return false;
        }
        View rawListView = root.findViewById(16908298);
        if (!(rawListView instanceof ListView)) {
            return false;
        }
        ListView listView = (ListView) rawListView;
        this.mList = listView;
        return listView != null;
    }

    private void ensureList() {
        if (this.mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        View rawListView = root.findViewById(16908298);
        if (!(rawListView instanceof ListView)) {
            throw new RuntimeException("Content has view with id attribute 'android.R.id.list' that is not a ListView class");
        }
        ListView listView = (ListView) rawListView;
        this.mList = listView;
        if (listView == null) {
            throw new RuntimeException("Your content must have a ListView whose id attribute is 'android.R.id.list'");
        }
        listView.setOnKeyListener(this.mListOnKeyListener);
        this.mHandler.post(this.mRequestFocus);
    }
}
