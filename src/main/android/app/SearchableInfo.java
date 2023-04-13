package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ProviderInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.C4057R;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class SearchableInfo implements Parcelable {
    public static final Parcelable.Creator<SearchableInfo> CREATOR = new Parcelable.Creator<SearchableInfo>() { // from class: android.app.SearchableInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchableInfo createFromParcel(Parcel in) {
            return new SearchableInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchableInfo[] newArray(int size) {
            return new SearchableInfo[size];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "SearchableInfo";
    private static final String MD_LABEL_SEARCHABLE = "android.app.searchable";
    private static final String MD_XML_ELEMENT_SEARCHABLE = "searchable";
    private static final String MD_XML_ELEMENT_SEARCHABLE_ACTION_KEY = "actionkey";
    private static final int SEARCH_MODE_BADGE_ICON = 8;
    private static final int SEARCH_MODE_BADGE_LABEL = 4;
    private static final int SEARCH_MODE_QUERY_REWRITE_FROM_DATA = 16;
    private static final int SEARCH_MODE_QUERY_REWRITE_FROM_TEXT = 32;
    private static final int VOICE_SEARCH_LAUNCH_RECOGNIZER = 4;
    private static final int VOICE_SEARCH_LAUNCH_WEB_SEARCH = 2;
    private static final int VOICE_SEARCH_SHOW_BUTTON = 1;
    private HashMap<Integer, ActionKeyInfo> mActionKeys = null;
    private final boolean mAutoUrlDetect;
    private final int mHintId;
    private final int mIconId;
    private final boolean mIncludeInGlobalSearch;
    private final int mLabelId;
    private final boolean mQueryAfterZeroResults;
    private final ComponentName mSearchActivity;
    private final int mSearchButtonText;
    private final int mSearchImeOptions;
    private final int mSearchInputType;
    private final int mSearchMode;
    private final int mSettingsDescriptionId;
    private final String mSuggestAuthority;
    private final String mSuggestIntentAction;
    private final String mSuggestIntentData;
    private final String mSuggestPath;
    private final String mSuggestProviderPackage;
    private final String mSuggestSelection;
    private final int mSuggestThreshold;
    private final int mVoiceLanguageId;
    private final int mVoiceLanguageModeId;
    private final int mVoiceMaxResults;
    private final int mVoicePromptTextId;
    private final int mVoiceSearchMode;

    public String getSuggestAuthority() {
        return this.mSuggestAuthority;
    }

    public String getSuggestPackage() {
        return this.mSuggestProviderPackage;
    }

    public ComponentName getSearchActivity() {
        return this.mSearchActivity;
    }

    public boolean useBadgeLabel() {
        return (this.mSearchMode & 4) != 0;
    }

    public boolean useBadgeIcon() {
        return ((this.mSearchMode & 8) == 0 || this.mIconId == 0) ? false : true;
    }

    public boolean shouldRewriteQueryFromData() {
        return (this.mSearchMode & 16) != 0;
    }

    public boolean shouldRewriteQueryFromText() {
        return (this.mSearchMode & 32) != 0;
    }

    public int getSettingsDescriptionId() {
        return this.mSettingsDescriptionId;
    }

    public String getSuggestPath() {
        return this.mSuggestPath;
    }

    public String getSuggestSelection() {
        return this.mSuggestSelection;
    }

    public String getSuggestIntentAction() {
        return this.mSuggestIntentAction;
    }

    public String getSuggestIntentData() {
        return this.mSuggestIntentData;
    }

    public int getSuggestThreshold() {
        return this.mSuggestThreshold;
    }

    public Context getActivityContext(Context context) {
        return createActivityContext(context, this.mSearchActivity);
    }

    private static Context createActivityContext(Context context, ComponentName activity) {
        try {
            Context theirContext = context.createPackageContext(activity.getPackageName(), 0);
            return theirContext;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(LOG_TAG, "Package not found " + activity.getPackageName());
            return null;
        } catch (SecurityException e2) {
            Log.m109e(LOG_TAG, "Can't make context for " + activity.getPackageName(), e2);
            return null;
        }
    }

    public Context getProviderContext(Context context, Context activityContext) {
        if (this.mSearchActivity.getPackageName().equals(this.mSuggestProviderPackage)) {
            return activityContext;
        }
        String str = this.mSuggestProviderPackage;
        if (str == null) {
            return null;
        }
        try {
            Context theirContext = context.createPackageContext(str, 0);
            return theirContext;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        } catch (SecurityException e2) {
            return null;
        }
    }

    private SearchableInfo(Context activityContext, AttributeSet attr, ComponentName cName) {
        this.mSearchActivity = cName;
        TypedArray a = activityContext.obtainStyledAttributes(attr, C4057R.styleable.Searchable);
        this.mSearchMode = a.getInt(3, 0);
        int resourceId = a.getResourceId(0, 0);
        this.mLabelId = resourceId;
        this.mHintId = a.getResourceId(2, 0);
        this.mIconId = a.getResourceId(1, 0);
        this.mSearchButtonText = a.getResourceId(9, 0);
        this.mSearchInputType = a.getInt(10, 1);
        this.mSearchImeOptions = a.getInt(16, 2);
        this.mIncludeInGlobalSearch = a.getBoolean(18, false);
        this.mQueryAfterZeroResults = a.getBoolean(19, false);
        this.mAutoUrlDetect = a.getBoolean(21, false);
        this.mSettingsDescriptionId = a.getResourceId(20, 0);
        String string = a.getString(4);
        this.mSuggestAuthority = string;
        this.mSuggestPath = a.getString(5);
        this.mSuggestSelection = a.getString(6);
        this.mSuggestIntentAction = a.getString(7);
        this.mSuggestIntentData = a.getString(8);
        this.mSuggestThreshold = a.getInt(17, 0);
        this.mVoiceSearchMode = a.getInt(11, 0);
        this.mVoiceLanguageModeId = a.getResourceId(12, 0);
        this.mVoicePromptTextId = a.getResourceId(13, 0);
        this.mVoiceLanguageId = a.getResourceId(14, 0);
        this.mVoiceMaxResults = a.getInt(15, 0);
        a.recycle();
        String suggestProviderPackage = null;
        if (string != null) {
            PackageManager pm = activityContext.getPackageManager();
            ProviderInfo pi = pm.resolveContentProvider(string, 268435456);
            if (pi != null) {
                suggestProviderPackage = pi.packageName;
            }
        }
        this.mSuggestProviderPackage = suggestProviderPackage;
        if (resourceId == 0) {
            throw new IllegalArgumentException("Search label must be a resource reference.");
        }
    }

    /* loaded from: classes.dex */
    public static class ActionKeyInfo implements Parcelable {
        public static final Parcelable.Creator<ActionKeyInfo> CREATOR = new Parcelable.Creator<ActionKeyInfo>() { // from class: android.app.SearchableInfo.ActionKeyInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ActionKeyInfo createFromParcel(Parcel in) {
                return new ActionKeyInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ActionKeyInfo[] newArray(int size) {
                return new ActionKeyInfo[size];
            }
        };
        private final int mKeyCode;
        private final String mQueryActionMsg;
        private final String mSuggestActionMsg;
        private final String mSuggestActionMsgColumn;

        ActionKeyInfo(Context activityContext, AttributeSet attr) {
            TypedArray a = activityContext.obtainStyledAttributes(attr, C4057R.styleable.SearchableActionKey);
            int i = a.getInt(0, 0);
            this.mKeyCode = i;
            String string = a.getString(1);
            this.mQueryActionMsg = string;
            String string2 = a.getString(2);
            this.mSuggestActionMsg = string2;
            String string3 = a.getString(3);
            this.mSuggestActionMsgColumn = string3;
            a.recycle();
            if (i == 0) {
                throw new IllegalArgumentException("No keycode.");
            }
            if (string == null && string2 == null && string3 == null) {
                throw new IllegalArgumentException("No message information.");
            }
        }

        private ActionKeyInfo(Parcel in) {
            this.mKeyCode = in.readInt();
            this.mQueryActionMsg = in.readString();
            this.mSuggestActionMsg = in.readString();
            this.mSuggestActionMsgColumn = in.readString();
        }

        public int getKeyCode() {
            return this.mKeyCode;
        }

        public String getQueryActionMsg() {
            return this.mQueryActionMsg;
        }

        public String getSuggestActionMsg() {
            return this.mSuggestActionMsg;
        }

        public String getSuggestActionMsgColumn() {
            return this.mSuggestActionMsgColumn;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mKeyCode);
            dest.writeString(this.mQueryActionMsg);
            dest.writeString(this.mSuggestActionMsg);
            dest.writeString(this.mSuggestActionMsgColumn);
        }
    }

    public ActionKeyInfo findActionKey(int keyCode) {
        HashMap<Integer, ActionKeyInfo> hashMap = this.mActionKeys;
        if (hashMap == null) {
            return null;
        }
        return hashMap.get(Integer.valueOf(keyCode));
    }

    private void addActionKey(ActionKeyInfo keyInfo) {
        if (this.mActionKeys == null) {
            this.mActionKeys = new HashMap<>();
        }
        this.mActionKeys.put(Integer.valueOf(keyInfo.getKeyCode()), keyInfo);
    }

    public static SearchableInfo getActivityMetaData(Context context, ActivityInfo activityInfo, int userId) {
        try {
            Context userContext = context.createPackageContextAsUser("system", 0, new UserHandle(userId));
            XmlResourceParser xml = activityInfo.loadXmlMetaData(userContext.getPackageManager(), MD_LABEL_SEARCHABLE);
            if (xml == null) {
                return null;
            }
            ComponentName cName = new ComponentName(activityInfo.packageName, activityInfo.name);
            SearchableInfo searchable = getActivityMetaData(userContext, xml, cName);
            xml.close();
            return searchable;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(LOG_TAG, "Couldn't create package context for user " + userId);
            return null;
        }
    }

    private static SearchableInfo getActivityMetaData(Context context, XmlPullParser xml, ComponentName cName) {
        SearchableInfo result = null;
        Context activityContext = createActivityContext(context, cName);
        if (activityContext == null) {
            return null;
        }
        try {
            int tagType = xml.next();
            while (tagType != 1) {
                if (tagType == 2) {
                    if (xml.getName().equals("searchable")) {
                        AttributeSet attr = Xml.asAttributeSet(xml);
                        if (attr != null) {
                            try {
                                result = new SearchableInfo(activityContext, attr, cName);
                            } catch (IllegalArgumentException ex) {
                                Log.m104w(LOG_TAG, "Invalid searchable metadata for " + cName.flattenToShortString() + ": " + ex.getMessage());
                                return null;
                            }
                        }
                    } else if (xml.getName().equals(MD_XML_ELEMENT_SEARCHABLE_ACTION_KEY)) {
                        if (result == null) {
                            return null;
                        }
                        AttributeSet attr2 = Xml.asAttributeSet(xml);
                        if (attr2 != null) {
                            try {
                                result.addActionKey(new ActionKeyInfo(activityContext, attr2));
                            } catch (IllegalArgumentException ex2) {
                                Log.m104w(LOG_TAG, "Invalid action key for " + cName.flattenToShortString() + ": " + ex2.getMessage());
                                return null;
                            }
                        }
                    }
                }
                tagType = xml.next();
            }
            return result;
        } catch (IOException e) {
            Log.m103w(LOG_TAG, "Reading searchable metadata for " + cName.flattenToShortString(), e);
            return null;
        } catch (XmlPullParserException e2) {
            Log.m103w(LOG_TAG, "Reading searchable metadata for " + cName.flattenToShortString(), e2);
            return null;
        }
    }

    public int getLabelId() {
        return this.mLabelId;
    }

    public int getHintId() {
        return this.mHintId;
    }

    public int getIconId() {
        return this.mIconId;
    }

    public boolean getVoiceSearchEnabled() {
        return (this.mVoiceSearchMode & 1) != 0;
    }

    public boolean getVoiceSearchLaunchWebSearch() {
        return (this.mVoiceSearchMode & 2) != 0;
    }

    public boolean getVoiceSearchLaunchRecognizer() {
        return (this.mVoiceSearchMode & 4) != 0;
    }

    public int getVoiceLanguageModeId() {
        return this.mVoiceLanguageModeId;
    }

    public int getVoicePromptTextId() {
        return this.mVoicePromptTextId;
    }

    public int getVoiceLanguageId() {
        return this.mVoiceLanguageId;
    }

    public int getVoiceMaxResults() {
        return this.mVoiceMaxResults;
    }

    public int getSearchButtonText() {
        return this.mSearchButtonText;
    }

    public int getInputType() {
        return this.mSearchInputType;
    }

    public int getImeOptions() {
        return this.mSearchImeOptions;
    }

    public boolean shouldIncludeInGlobalSearch() {
        return this.mIncludeInGlobalSearch;
    }

    public boolean queryAfterZeroResults() {
        return this.mQueryAfterZeroResults;
    }

    public boolean autoUrlDetect() {
        return this.mAutoUrlDetect;
    }

    SearchableInfo(Parcel in) {
        this.mLabelId = in.readInt();
        this.mSearchActivity = ComponentName.readFromParcel(in);
        this.mHintId = in.readInt();
        this.mSearchMode = in.readInt();
        this.mIconId = in.readInt();
        this.mSearchButtonText = in.readInt();
        this.mSearchInputType = in.readInt();
        this.mSearchImeOptions = in.readInt();
        this.mIncludeInGlobalSearch = in.readInt() != 0;
        this.mQueryAfterZeroResults = in.readInt() != 0;
        this.mAutoUrlDetect = in.readInt() != 0;
        this.mSettingsDescriptionId = in.readInt();
        this.mSuggestAuthority = in.readString();
        this.mSuggestPath = in.readString();
        this.mSuggestSelection = in.readString();
        this.mSuggestIntentAction = in.readString();
        this.mSuggestIntentData = in.readString();
        this.mSuggestThreshold = in.readInt();
        for (int count = in.readInt(); count > 0; count--) {
            addActionKey(new ActionKeyInfo(in));
        }
        this.mSuggestProviderPackage = in.readString();
        this.mVoiceSearchMode = in.readInt();
        this.mVoiceLanguageModeId = in.readInt();
        this.mVoicePromptTextId = in.readInt();
        this.mVoiceLanguageId = in.readInt();
        this.mVoiceMaxResults = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mLabelId);
        this.mSearchActivity.writeToParcel(dest, flags);
        dest.writeInt(this.mHintId);
        dest.writeInt(this.mSearchMode);
        dest.writeInt(this.mIconId);
        dest.writeInt(this.mSearchButtonText);
        dest.writeInt(this.mSearchInputType);
        dest.writeInt(this.mSearchImeOptions);
        dest.writeInt(this.mIncludeInGlobalSearch ? 1 : 0);
        dest.writeInt(this.mQueryAfterZeroResults ? 1 : 0);
        dest.writeInt(this.mAutoUrlDetect ? 1 : 0);
        dest.writeInt(this.mSettingsDescriptionId);
        dest.writeString(this.mSuggestAuthority);
        dest.writeString(this.mSuggestPath);
        dest.writeString(this.mSuggestSelection);
        dest.writeString(this.mSuggestIntentAction);
        dest.writeString(this.mSuggestIntentData);
        dest.writeInt(this.mSuggestThreshold);
        HashMap<Integer, ActionKeyInfo> hashMap = this.mActionKeys;
        if (hashMap == null) {
            dest.writeInt(0);
        } else {
            dest.writeInt(hashMap.size());
            for (ActionKeyInfo actionKey : this.mActionKeys.values()) {
                actionKey.writeToParcel(dest, flags);
            }
        }
        dest.writeString(this.mSuggestProviderPackage);
        dest.writeInt(this.mVoiceSearchMode);
        dest.writeInt(this.mVoiceLanguageModeId);
        dest.writeInt(this.mVoicePromptTextId);
        dest.writeInt(this.mVoiceLanguageId);
        dest.writeInt(this.mVoiceMaxResults);
    }
}
