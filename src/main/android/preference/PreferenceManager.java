package android.preference;

import android.annotation.SystemApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.p008os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
@Deprecated
/* loaded from: classes3.dex */
public class PreferenceManager {
    public static final String KEY_HAS_SET_DEFAULT_VALUES = "_has_set_default_values";
    public static final String METADATA_KEY_PREFERENCES = "android.preference";
    private static final int STORAGE_CREDENTIAL_PROTECTED = 2;
    private static final int STORAGE_DEFAULT = 0;
    private static final int STORAGE_DEVICE_PROTECTED = 1;
    private static final String TAG = "PreferenceManager";
    private Activity mActivity;
    private List<OnActivityDestroyListener> mActivityDestroyListeners;
    private List<OnActivityResultListener> mActivityResultListeners;
    private List<OnActivityStopListener> mActivityStopListeners;
    private Context mContext;
    private SharedPreferences.Editor mEditor;
    private PreferenceFragment mFragment;
    private int mNextRequestCode;
    private boolean mNoCommit;
    private OnPreferenceTreeClickListener mOnPreferenceTreeClickListener;
    private PreferenceDataStore mPreferenceDataStore;
    private PreferenceScreen mPreferenceScreen;
    private List<DialogInterface> mPreferencesScreens;
    private SharedPreferences mSharedPreferences;
    private int mSharedPreferencesMode;
    private String mSharedPreferencesName;
    private long mNextId = 0;
    private int mStorage = 0;

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnActivityDestroyListener {
        void onActivityDestroy();
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnActivityResultListener {
        boolean onActivityResult(int i, int i2, Intent intent);
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnActivityStopListener {
        void onActivityStop();
    }

    @Deprecated
    /* loaded from: classes3.dex */
    public interface OnPreferenceTreeClickListener {
        boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference);
    }

    public PreferenceManager(Activity activity, int firstRequestCode) {
        this.mActivity = activity;
        this.mNextRequestCode = firstRequestCode;
        init(activity);
    }

    PreferenceManager(Context context) {
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setSharedPreferencesName(getDefaultSharedPreferencesName(context));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFragment(PreferenceFragment fragment) {
        this.mFragment = fragment;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferenceFragment getFragment() {
        return this.mFragment;
    }

    public void setPreferenceDataStore(PreferenceDataStore dataStore) {
        this.mPreferenceDataStore = dataStore;
    }

    public PreferenceDataStore getPreferenceDataStore() {
        return this.mPreferenceDataStore;
    }

    private List<ResolveInfo> queryIntentActivities(Intent queryIntent) {
        return this.mContext.getPackageManager().queryIntentActivities(queryIntent, 128);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferenceScreen inflateFromIntent(Intent queryIntent, PreferenceScreen rootPreferences) {
        List<ResolveInfo> activities = queryIntentActivities(queryIntent);
        HashSet<String> inflatedRes = new HashSet<>();
        for (int i = activities.size() - 1; i >= 0; i--) {
            ActivityInfo activityInfo = activities.get(i).activityInfo;
            Bundle metaData = activityInfo.metaData;
            if (metaData != null && metaData.containsKey(METADATA_KEY_PREFERENCES)) {
                String uniqueResId = activityInfo.packageName + ":" + activityInfo.metaData.getInt(METADATA_KEY_PREFERENCES);
                if (!inflatedRes.contains(uniqueResId)) {
                    inflatedRes.add(uniqueResId);
                    try {
                        Context context = this.mContext.createPackageContext(activityInfo.packageName, 0);
                        PreferenceInflater inflater = new PreferenceInflater(context, this);
                        XmlResourceParser parser = activityInfo.loadXmlMetaData(context.getPackageManager(), METADATA_KEY_PREFERENCES);
                        rootPreferences = (PreferenceScreen) inflater.inflate((XmlPullParser) parser, (XmlResourceParser) rootPreferences, true);
                        parser.close();
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.m104w(TAG, "Could not create context for " + activityInfo.packageName + ": " + Log.getStackTraceString(e));
                    }
                }
            }
        }
        rootPreferences.onAttachedToHierarchy(this);
        return rootPreferences;
    }

    public PreferenceScreen inflateFromResource(Context context, int resId, PreferenceScreen rootPreferences) {
        setNoCommit(true);
        PreferenceInflater inflater = new PreferenceInflater(context, this);
        PreferenceScreen rootPreferences2 = (PreferenceScreen) inflater.inflate(resId, (int) rootPreferences, true);
        rootPreferences2.onAttachedToHierarchy(this);
        setNoCommit(false);
        return rootPreferences2;
    }

    public PreferenceScreen createPreferenceScreen(Context context) {
        PreferenceScreen preferenceScreen = new PreferenceScreen(context, null);
        preferenceScreen.onAttachedToHierarchy(this);
        return preferenceScreen;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public long getNextId() {
        long j;
        synchronized (this) {
            j = this.mNextId;
            this.mNextId = 1 + j;
        }
        return j;
    }

    public String getSharedPreferencesName() {
        return this.mSharedPreferencesName;
    }

    public void setSharedPreferencesName(String sharedPreferencesName) {
        this.mSharedPreferencesName = sharedPreferencesName;
        this.mSharedPreferences = null;
    }

    public int getSharedPreferencesMode() {
        return this.mSharedPreferencesMode;
    }

    public void setSharedPreferencesMode(int sharedPreferencesMode) {
        this.mSharedPreferencesMode = sharedPreferencesMode;
        this.mSharedPreferences = null;
    }

    public void setStorageDefault() {
        this.mStorage = 0;
        this.mSharedPreferences = null;
    }

    public void setStorageDeviceProtected() {
        this.mStorage = 1;
        this.mSharedPreferences = null;
    }

    @SystemApi
    public void setStorageCredentialProtected() {
        this.mStorage = 2;
        this.mSharedPreferences = null;
    }

    public boolean isStorageDefault() {
        return this.mStorage == 0;
    }

    public boolean isStorageDeviceProtected() {
        return this.mStorage == 1;
    }

    @SystemApi
    public boolean isStorageCredentialProtected() {
        return this.mStorage == 2;
    }

    public SharedPreferences getSharedPreferences() {
        Context storageContext;
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (this.mSharedPreferences == null) {
            switch (this.mStorage) {
                case 1:
                    Context storageContext2 = this.mContext;
                    storageContext = storageContext2.createDeviceProtectedStorageContext();
                    break;
                case 2:
                    Context storageContext3 = this.mContext;
                    storageContext = storageContext3.createCredentialProtectedStorageContext();
                    break;
                default:
                    storageContext = this.mContext;
                    break;
            }
            this.mSharedPreferences = storageContext.getSharedPreferences(this.mSharedPreferencesName, this.mSharedPreferencesMode);
        }
        return this.mSharedPreferences;
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return context.getSharedPreferences(getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode());
    }

    public static String getDefaultSharedPreferencesName(Context context) {
        return context.getPackageName() + "_preferences";
    }

    private static int getDefaultSharedPreferencesMode() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public PreferenceScreen getPreferenceScreen() {
        return this.mPreferenceScreen;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean setPreferences(PreferenceScreen preferenceScreen) {
        if (preferenceScreen != this.mPreferenceScreen) {
            this.mPreferenceScreen = preferenceScreen;
            return true;
        }
        return false;
    }

    public Preference findPreference(CharSequence key) {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen == null) {
            return null;
        }
        return preferenceScreen.findPreference(key);
    }

    public static void setDefaultValues(Context context, int resId, boolean readAgain) {
        setDefaultValues(context, getDefaultSharedPreferencesName(context), getDefaultSharedPreferencesMode(), resId, readAgain);
    }

    public static void setDefaultValues(Context context, String sharedPreferencesName, int sharedPreferencesMode, int resId, boolean readAgain) {
        SharedPreferences defaultValueSp = context.getSharedPreferences(KEY_HAS_SET_DEFAULT_VALUES, 0);
        if (readAgain || !defaultValueSp.getBoolean(KEY_HAS_SET_DEFAULT_VALUES, false)) {
            PreferenceManager pm = new PreferenceManager(context);
            pm.setSharedPreferencesName(sharedPreferencesName);
            pm.setSharedPreferencesMode(sharedPreferencesMode);
            pm.inflateFromResource(context, resId, null);
            SharedPreferences.Editor editor = defaultValueSp.edit().putBoolean(KEY_HAS_SET_DEFAULT_VALUES, true);
            try {
                editor.apply();
            } catch (AbstractMethodError e) {
                editor.commit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public SharedPreferences.Editor getEditor() {
        if (this.mPreferenceDataStore != null) {
            return null;
        }
        if (this.mNoCommit) {
            if (this.mEditor == null) {
                this.mEditor = getSharedPreferences().edit();
            }
            return this.mEditor;
        }
        return getSharedPreferences().edit();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldCommit() {
        return !this.mNoCommit;
    }

    private void setNoCommit(boolean noCommit) {
        SharedPreferences.Editor editor;
        if (!noCommit && (editor = this.mEditor) != null) {
            try {
                editor.apply();
            } catch (AbstractMethodError e) {
                this.mEditor.commit();
            }
        }
        this.mNoCommit = noCommit;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Activity getActivity() {
        return this.mActivity;
    }

    Context getContext() {
        return this.mContext;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerOnActivityResultListener(OnActivityResultListener listener) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                this.mActivityResultListeners = new ArrayList();
            }
            if (!this.mActivityResultListeners.contains(listener)) {
                this.mActivityResultListeners.add(listener);
            }
        }
    }

    void unregisterOnActivityResultListener(OnActivityResultListener listener) {
        synchronized (this) {
            List<OnActivityResultListener> list = this.mActivityResultListeners;
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityResult(int requestCode, int resultCode, Intent data) {
        synchronized (this) {
            if (this.mActivityResultListeners == null) {
                return;
            }
            List<OnActivityResultListener> list = new ArrayList<>(this.mActivityResultListeners);
            int N = list.size();
            for (int i = 0; i < N && !list.get(i).onActivityResult(requestCode, resultCode, data); i++) {
            }
        }
    }

    public void registerOnActivityStopListener(OnActivityStopListener listener) {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                this.mActivityStopListeners = new ArrayList();
            }
            if (!this.mActivityStopListeners.contains(listener)) {
                this.mActivityStopListeners.add(listener);
            }
        }
    }

    public void unregisterOnActivityStopListener(OnActivityStopListener listener) {
        synchronized (this) {
            List<OnActivityStopListener> list = this.mActivityStopListeners;
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityStop() {
        synchronized (this) {
            if (this.mActivityStopListeners == null) {
                return;
            }
            List<OnActivityStopListener> list = new ArrayList<>(this.mActivityStopListeners);
            int N = list.size();
            for (int i = 0; i < N; i++) {
                list.get(i).onActivityStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerOnActivityDestroyListener(OnActivityDestroyListener listener) {
        synchronized (this) {
            if (this.mActivityDestroyListeners == null) {
                this.mActivityDestroyListeners = new ArrayList();
            }
            if (!this.mActivityDestroyListeners.contains(listener)) {
                this.mActivityDestroyListeners.add(listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterOnActivityDestroyListener(OnActivityDestroyListener listener) {
        synchronized (this) {
            List<OnActivityDestroyListener> list = this.mActivityDestroyListeners;
            if (list != null) {
                list.remove(listener);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityDestroy() {
        List<OnActivityDestroyListener> list = null;
        synchronized (this) {
            if (this.mActivityDestroyListeners != null) {
                list = new ArrayList<>(this.mActivityDestroyListeners);
            }
        }
        if (list != null) {
            int N = list.size();
            for (int i = 0; i < N; i++) {
                list.get(i).onActivityDestroy();
            }
        }
        dismissAllScreens();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getNextRequestCode() {
        int i;
        synchronized (this) {
            i = this.mNextRequestCode;
            this.mNextRequestCode = i + 1;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addPreferencesScreen(DialogInterface screen) {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                this.mPreferencesScreens = new ArrayList();
            }
            this.mPreferencesScreens.add(screen);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void removePreferencesScreen(DialogInterface screen) {
        synchronized (this) {
            List<DialogInterface> list = this.mPreferencesScreens;
            if (list == null) {
                return;
            }
            list.remove(screen);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchNewIntent(Intent intent) {
        dismissAllScreens();
    }

    private void dismissAllScreens() {
        synchronized (this) {
            if (this.mPreferencesScreens == null) {
                return;
            }
            ArrayList<DialogInterface> screensToDismiss = new ArrayList<>(this.mPreferencesScreens);
            this.mPreferencesScreens.clear();
            for (int i = screensToDismiss.size() - 1; i >= 0; i--) {
                screensToDismiss.get(i).dismiss();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnPreferenceTreeClickListener(OnPreferenceTreeClickListener listener) {
        this.mOnPreferenceTreeClickListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OnPreferenceTreeClickListener getOnPreferenceTreeClickListener() {
        return this.mOnPreferenceTreeClickListener;
    }
}
