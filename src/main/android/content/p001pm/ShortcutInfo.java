package android.content.p001pm;

import android.annotation.SystemApi;
import android.app.Person;
import android.app.appsearch.GenericDocument;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.LocusId;
import android.content.p001pm.CapabilityParams;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* renamed from: android.content.pm.ShortcutInfo */
/* loaded from: classes.dex */
public final class ShortcutInfo implements Parcelable {
    private static final String ANDROID_PACKAGE_NAME = "android";
    public static final int CLONE_REMOVE_FOR_APP_PREDICTION = 9;
    public static final int CLONE_REMOVE_FOR_CREATOR = 9;
    public static final int CLONE_REMOVE_FOR_LAUNCHER = 27;
    public static final int CLONE_REMOVE_FOR_LAUNCHER_APPROVAL = 26;
    private static final int CLONE_REMOVE_ICON = 1;
    private static final int CLONE_REMOVE_INTENT = 2;
    public static final int CLONE_REMOVE_NON_KEY_INFO = 4;
    public static final int CLONE_REMOVE_PERSON = 16;
    public static final int CLONE_REMOVE_RES_NAMES = 8;
    public static final Parcelable.Creator<ShortcutInfo> CREATOR = new Parcelable.Creator<ShortcutInfo>() { // from class: android.content.pm.ShortcutInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShortcutInfo createFromParcel(Parcel source) {
            return new ShortcutInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ShortcutInfo[] newArray(int size) {
            return new ShortcutInfo[size];
        }
    };
    public static final int DISABLED_REASON_APP_CHANGED = 2;
    public static final int DISABLED_REASON_BACKUP_NOT_SUPPORTED = 101;
    public static final int DISABLED_REASON_BY_APP = 1;
    public static final int DISABLED_REASON_NOT_DISABLED = 0;
    public static final int DISABLED_REASON_OTHER_RESTORE_ISSUE = 103;
    private static final int DISABLED_REASON_RESTORE_ISSUE_START = 100;
    public static final int DISABLED_REASON_SIGNATURE_MISMATCH = 102;
    public static final int DISABLED_REASON_UNKNOWN = 3;
    public static final int DISABLED_REASON_VERSION_LOWER = 100;
    public static final int FLAG_ADAPTIVE_BITMAP = 512;
    public static final int FLAG_CACHED_ALL = 1610629120;
    public static final int FLAG_CACHED_BUBBLES = 1073741824;
    public static final int FLAG_CACHED_NOTIFICATIONS = 16384;
    public static final int FLAG_CACHED_PEOPLE_TILE = 536870912;
    public static final int FLAG_DISABLED = 64;
    public static final int FLAG_DYNAMIC = 1;
    public static final int FLAG_HAS_ICON_FILE = 8;
    public static final int FLAG_HAS_ICON_RES = 4;
    public static final int FLAG_HAS_ICON_URI = 32768;
    public static final int FLAG_ICON_FILE_PENDING_SAVE = 2048;
    public static final int FLAG_IMMUTABLE = 256;
    public static final int FLAG_KEY_FIELDS_ONLY = 16;
    public static final int FLAG_LONG_LIVED = 8192;
    public static final int FLAG_MANIFEST = 32;
    public static final int FLAG_PINNED = 2;
    public static final int FLAG_RETURNED_BY_SERVICE = 1024;
    public static final int FLAG_SHADOW = 4096;
    public static final int FLAG_STRINGS_RESOLVED = 128;
    private static final int IMPLICIT_RANK_MASK = Integer.MAX_VALUE;
    public static final int RANK_CHANGED_BIT = Integer.MIN_VALUE;
    public static final int RANK_NOT_SET = Integer.MAX_VALUE;
    private static final String RES_TYPE_STRING = "string";
    public static final String SHORTCUT_CATEGORY_CONVERSATION = "android.shortcut.conversation";
    public static final int SURFACE_LAUNCHER = 1;
    static final String TAG = "Shortcut";
    public static final int VERSION_CODE_UNKNOWN = -1;
    private ComponentName mActivity;
    private String mBitmapPath;
    private Map<String, Map<String, List<String>>> mCapabilityBindings;
    private ArraySet<String> mCategories;
    private CharSequence mDisabledMessage;
    private int mDisabledMessageResId;
    private String mDisabledMessageResName;
    private int mDisabledReason;
    private int mExcludedSurfaces;
    private PersistableBundle mExtras;
    private int mFlags;
    private Icon mIcon;
    private int mIconResId;
    private String mIconResName;
    private String mIconUri;
    private final String mId;
    private int mImplicitRank;
    private PersistableBundle[] mIntentPersistableExtrases;
    private Intent[] mIntents;
    private long mLastChangedTimestamp;
    private LocusId mLocusId;
    private final String mPackageName;
    private Person[] mPersons;
    private int mRank;
    private String mStartingThemeResName;
    private CharSequence mText;
    private int mTextResId;
    private String mTextResName;
    private CharSequence mTitle;
    private int mTitleResId;
    private String mTitleResName;
    private final int mUserId;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ShortcutInfo$CloneFlags */
    /* loaded from: classes.dex */
    public @interface CloneFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ShortcutInfo$DisabledReason */
    /* loaded from: classes.dex */
    public @interface DisabledReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ShortcutInfo$ShortcutFlags */
    /* loaded from: classes.dex */
    public @interface ShortcutFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ShortcutInfo$Surface */
    /* loaded from: classes.dex */
    public @interface Surface {
    }

    public static String getDisabledReasonDebugString(int disabledReason) {
        switch (disabledReason) {
            case 0:
                return "[Not disabled]";
            case 1:
                return "[Disabled: by app]";
            case 2:
                return "[Disabled: app changed]";
            case 100:
                return "[Disabled: lower version]";
            case 101:
                return "[Disabled: backup not supported]";
            case 102:
                return "[Disabled: signature mismatch]";
            case 103:
                return "[Disabled: unknown restore issue]";
            default:
                return "[Disabled: unknown reason:" + disabledReason + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    public static String getDisabledReasonForRestoreIssue(Context context, int disabledReason) {
        Resources res = context.getResources();
        switch (disabledReason) {
            case 3:
                return res.getString(C4057R.string.shortcut_disabled_reason_unknown);
            case 100:
                return res.getString(C4057R.string.shortcut_restored_on_lower_version);
            case 101:
                return res.getString(C4057R.string.shortcut_restore_not_supported);
            case 102:
                return res.getString(C4057R.string.shortcut_restore_signature_mismatch);
            case 103:
                return res.getString(C4057R.string.shortcut_restore_unknown_issue);
            default:
                return null;
        }
    }

    public static boolean isDisabledForRestoreIssue(int disabledReason) {
        return disabledReason >= 100;
    }

    private ShortcutInfo(Builder b) {
        this.mUserId = b.mContext.getUserId();
        this.mId = (String) Preconditions.checkStringNotEmpty(b.mId, "Shortcut ID must be provided");
        this.mPackageName = b.mContext.getPackageName();
        this.mActivity = b.mActivity;
        this.mIcon = b.mIcon;
        this.mTitle = b.mTitle;
        this.mTitleResId = b.mTitleResId;
        this.mText = b.mText;
        this.mTextResId = b.mTextResId;
        this.mDisabledMessage = b.mDisabledMessage;
        this.mDisabledMessageResId = b.mDisabledMessageResId;
        this.mCategories = cloneCategories(b.mCategories);
        this.mIntents = cloneIntents(b.mIntents);
        fixUpIntentExtras();
        this.mPersons = clonePersons(b.mPersons);
        if (b.mIsLongLived) {
            setLongLived();
        }
        this.mExcludedSurfaces = b.mExcludedSurfaces;
        this.mRank = b.mRank;
        this.mExtras = b.mExtras;
        this.mLocusId = b.mLocusId;
        this.mCapabilityBindings = cloneCapabilityBindings(b.mCapabilityBindings);
        this.mStartingThemeResName = b.mStartingThemeResId != 0 ? b.mContext.getResources().getResourceName(b.mStartingThemeResId) : null;
        updateTimestamp();
    }

    private void fixUpIntentExtras() {
        Intent[] intentArr = this.mIntents;
        if (intentArr == null) {
            this.mIntentPersistableExtrases = null;
            return;
        }
        this.mIntentPersistableExtrases = new PersistableBundle[intentArr.length];
        int i = 0;
        while (true) {
            Intent[] intentArr2 = this.mIntents;
            if (i < intentArr2.length) {
                Intent intent = intentArr2[i];
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    this.mIntentPersistableExtrases[i] = null;
                } else {
                    this.mIntentPersistableExtrases[i] = new PersistableBundle(extras);
                    intent.replaceExtras((Bundle) null);
                }
                i++;
            } else {
                return;
            }
        }
    }

    private static ArraySet<String> cloneCategories(Set<String> source) {
        if (source == null) {
            return null;
        }
        ArraySet<String> ret = new ArraySet<>(source.size());
        for (CharSequence s : source) {
            if (!TextUtils.isEmpty(s)) {
                ret.add(s.toString().intern());
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Intent[] cloneIntents(Intent[] intents) {
        if (intents == null) {
            return null;
        }
        Intent[] ret = new Intent[intents.length];
        for (int i = 0; i < ret.length; i++) {
            if (intents[i] != null) {
                ret[i] = new Intent(intents[i]);
            }
        }
        return ret;
    }

    private static PersistableBundle[] clonePersistableBundle(PersistableBundle[] bundle) {
        if (bundle == null) {
            return null;
        }
        PersistableBundle[] ret = new PersistableBundle[bundle.length];
        for (int i = 0; i < ret.length; i++) {
            if (bundle[i] != null) {
                ret[i] = new PersistableBundle(bundle[i]);
            }
        }
        return ret;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Person[] clonePersons(Person[] persons) {
        if (persons == null) {
            return null;
        }
        Person[] ret = new Person[persons.length];
        for (int i = 0; i < ret.length; i++) {
            if (persons[i] != null) {
                ret[i] = persons[i].toBuilder().setIcon(null).build();
            }
        }
        return ret;
    }

    public void enforceMandatoryFields(boolean forPinned) {
        Preconditions.checkStringNotEmpty(this.mId, "Shortcut ID must be provided");
        if (!forPinned) {
            Objects.requireNonNull(this.mActivity, "Activity must be provided");
        }
        if (this.mTitle == null && this.mTitleResId == 0) {
            throw new IllegalArgumentException("Short label must be provided");
        }
        Objects.requireNonNull(this.mIntents, "Shortcut Intent must be provided");
        Preconditions.checkArgument(this.mIntents.length > 0, "Shortcut Intent must be provided");
    }

    private ShortcutInfo(ShortcutInfo source, int cloneFlags) {
        this.mUserId = source.mUserId;
        this.mId = source.mId;
        this.mPackageName = source.mPackageName;
        this.mActivity = source.mActivity;
        int i = source.mFlags;
        this.mFlags = i;
        this.mLastChangedTimestamp = source.mLastChangedTimestamp;
        this.mDisabledReason = source.mDisabledReason;
        this.mLocusId = source.mLocusId;
        this.mExcludedSurfaces = source.mExcludedSurfaces;
        this.mIconResId = source.mIconResId;
        if ((cloneFlags & 4) == 0) {
            if ((cloneFlags & 1) == 0) {
                this.mIcon = source.mIcon;
                this.mBitmapPath = source.mBitmapPath;
                this.mIconUri = source.mIconUri;
            }
            this.mTitle = source.mTitle;
            this.mTitleResId = source.mTitleResId;
            this.mText = source.mText;
            this.mTextResId = source.mTextResId;
            this.mDisabledMessage = source.mDisabledMessage;
            this.mDisabledMessageResId = source.mDisabledMessageResId;
            this.mCategories = cloneCategories(source.mCategories);
            if ((cloneFlags & 16) == 0) {
                this.mPersons = clonePersons(source.mPersons);
            }
            if ((cloneFlags & 2) == 0) {
                this.mIntents = cloneIntents(source.mIntents);
                this.mIntentPersistableExtrases = clonePersistableBundle(source.mIntentPersistableExtrases);
            }
            this.mRank = source.mRank;
            this.mExtras = source.mExtras;
            if ((cloneFlags & 8) == 0) {
                this.mTitleResName = source.mTitleResName;
                this.mTextResName = source.mTextResName;
                this.mDisabledMessageResName = source.mDisabledMessageResName;
                this.mIconResName = source.mIconResName;
            }
        } else {
            this.mFlags = i | 16;
        }
        this.mCapabilityBindings = cloneCapabilityBindings(source.mCapabilityBindings);
        this.mStartingThemeResName = source.mStartingThemeResName;
    }

    public static ShortcutInfo createFromGenericDocument(Context context, GenericDocument document) {
        Objects.requireNonNull(context);
        Objects.requireNonNull(document);
        return createFromGenericDocument(context.getUserId(), document);
    }

    public static ShortcutInfo createFromGenericDocument(int userId, GenericDocument document) {
        return new AppSearchShortcutInfo(document).toShortcutInfo(userId);
    }

    private CharSequence getResourceString(Resources res, int resId, CharSequence defValue) {
        try {
            return res.getString(resId);
        } catch (Resources.NotFoundException e) {
            Log.m110e("Shortcut", "Resource for ID=" + resId + " not found in package " + this.mPackageName);
            return defValue;
        }
    }

    public void resolveResourceStrings(Resources res) {
        this.mFlags |= 128;
        int i = this.mTitleResId;
        if (i == 0 && this.mTextResId == 0 && this.mDisabledMessageResId == 0) {
            return;
        }
        if (i != 0) {
            this.mTitle = getResourceString(res, i, this.mTitle);
        }
        int i2 = this.mTextResId;
        if (i2 != 0) {
            this.mText = getResourceString(res, i2, this.mText);
        }
        int i3 = this.mDisabledMessageResId;
        if (i3 != 0) {
            this.mDisabledMessage = getResourceString(res, i3, this.mDisabledMessage);
        }
    }

    public static String lookUpResourceName(Resources res, int resId, boolean withType, String packageName) {
        if (resId == 0) {
            return null;
        }
        try {
            String fullName = res.getResourceName(resId);
            if ("android".equals(getResourcePackageName(fullName))) {
                return String.valueOf(resId);
            }
            return withType ? getResourceTypeAndEntryName(fullName) : getResourceEntryName(fullName);
        } catch (Resources.NotFoundException e) {
            Log.m110e("Shortcut", "Resource name for ID=" + resId + " not found in package " + packageName + ". Resource IDs may change when the application is upgraded, and the system may not be able to find the correct resource.");
            return null;
        }
    }

    public static String getResourcePackageName(String fullResourceName) {
        int p1 = fullResourceName.indexOf(58);
        if (p1 < 0) {
            return null;
        }
        return fullResourceName.substring(0, p1);
    }

    public static String getResourceTypeName(String fullResourceName) {
        int p2;
        int p1 = fullResourceName.indexOf(58);
        if (p1 < 0 || (p2 = fullResourceName.indexOf(47, p1 + 1)) < 0) {
            return null;
        }
        return fullResourceName.substring(p1 + 1, p2);
    }

    public static String getResourceTypeAndEntryName(String fullResourceName) {
        int p1 = fullResourceName.indexOf(58);
        if (p1 < 0) {
            return null;
        }
        return fullResourceName.substring(p1 + 1);
    }

    public static String getResourceEntryName(String fullResourceName) {
        int p1 = fullResourceName.indexOf(47);
        if (p1 < 0) {
            return null;
        }
        return fullResourceName.substring(p1 + 1);
    }

    public static int lookUpResourceId(Resources res, String resourceName, String resourceType, String packageName) {
        try {
            if (resourceName == null) {
                return 0;
            }
            try {
                return Integer.parseInt(resourceName);
            } catch (NumberFormatException e) {
                return res.getIdentifier(resourceName, resourceType, packageName);
            }
        } catch (Resources.NotFoundException e2) {
            Log.m110e("Shortcut", "Resource ID for name=" + resourceName + " not found in package " + packageName);
            return 0;
        }
    }

    public void lookupAndFillInResourceNames(Resources res) {
        int i = this.mTitleResId;
        if (i == 0 && this.mTextResId == 0 && this.mDisabledMessageResId == 0 && this.mIconResId == 0) {
            return;
        }
        this.mTitleResName = lookUpResourceName(res, i, false, this.mPackageName);
        this.mTextResName = lookUpResourceName(res, this.mTextResId, false, this.mPackageName);
        this.mDisabledMessageResName = lookUpResourceName(res, this.mDisabledMessageResId, false, this.mPackageName);
        this.mIconResName = lookUpResourceName(res, this.mIconResId, true, this.mPackageName);
    }

    public void lookupAndFillInResourceIds(Resources res) {
        String str = this.mTitleResName;
        if (str == null && this.mTextResName == null && this.mDisabledMessageResName == null && this.mIconResName == null) {
            return;
        }
        this.mTitleResId = lookUpResourceId(res, str, RES_TYPE_STRING, this.mPackageName);
        this.mTextResId = lookUpResourceId(res, this.mTextResName, RES_TYPE_STRING, this.mPackageName);
        this.mDisabledMessageResId = lookUpResourceId(res, this.mDisabledMessageResName, RES_TYPE_STRING, this.mPackageName);
        this.mIconResId = lookUpResourceId(res, this.mIconResName, null, this.mPackageName);
    }

    public ShortcutInfo clone(int cloneFlags) {
        return new ShortcutInfo(this, cloneFlags);
    }

    public void ensureUpdatableWith(ShortcutInfo source, boolean isUpdating) {
        if (isUpdating) {
            Preconditions.checkState(isVisibleToPublisher(), "[Framework BUG] Invisible shortcuts can't be updated");
        }
        Preconditions.checkState(this.mUserId == source.mUserId, "Owner User ID must match");
        Preconditions.checkState(this.mId.equals(source.mId), "ID must match");
        Preconditions.checkState(this.mPackageName.equals(source.mPackageName), "Package name must match");
        if (isVisibleToPublisher()) {
            Preconditions.checkState(!isImmutable(), "Target ShortcutInfo is immutable");
        }
    }

    public void copyNonNullFieldsFrom(ShortcutInfo source) {
        ensureUpdatableWith(source, true);
        ComponentName componentName = source.mActivity;
        if (componentName != null) {
            this.mActivity = componentName;
        }
        Icon icon = source.mIcon;
        if (icon != null) {
            this.mIcon = icon;
            this.mIconResId = 0;
            this.mIconResName = null;
            this.mBitmapPath = null;
            this.mIconUri = null;
        }
        CharSequence charSequence = source.mTitle;
        if (charSequence != null) {
            this.mTitle = charSequence;
            this.mTitleResId = 0;
            this.mTitleResName = null;
        } else {
            int i = source.mTitleResId;
            if (i != 0) {
                this.mTitle = null;
                this.mTitleResId = i;
                this.mTitleResName = null;
            }
        }
        CharSequence charSequence2 = source.mText;
        if (charSequence2 != null) {
            this.mText = charSequence2;
            this.mTextResId = 0;
            this.mTextResName = null;
        } else {
            int i2 = source.mTextResId;
            if (i2 != 0) {
                this.mText = null;
                this.mTextResId = i2;
                this.mTextResName = null;
            }
        }
        CharSequence charSequence3 = source.mDisabledMessage;
        if (charSequence3 != null) {
            this.mDisabledMessage = charSequence3;
            this.mDisabledMessageResId = 0;
            this.mDisabledMessageResName = null;
        } else {
            int i3 = source.mDisabledMessageResId;
            if (i3 != 0) {
                this.mDisabledMessage = null;
                this.mDisabledMessageResId = i3;
                this.mDisabledMessageResName = null;
            }
        }
        ArraySet<String> arraySet = source.mCategories;
        if (arraySet != null) {
            this.mCategories = cloneCategories(arraySet);
        }
        Person[] personArr = source.mPersons;
        if (personArr != null) {
            this.mPersons = clonePersons(personArr);
        }
        Intent[] intentArr = source.mIntents;
        if (intentArr != null) {
            this.mIntents = cloneIntents(intentArr);
            this.mIntentPersistableExtrases = clonePersistableBundle(source.mIntentPersistableExtrases);
        }
        int i4 = source.mRank;
        if (i4 != Integer.MAX_VALUE) {
            this.mRank = i4;
        }
        PersistableBundle persistableBundle = source.mExtras;
        if (persistableBundle != null) {
            this.mExtras = persistableBundle;
        }
        LocusId locusId = source.mLocusId;
        if (locusId != null) {
            this.mLocusId = locusId;
        }
        String str = source.mStartingThemeResName;
        if (str != null && !str.isEmpty()) {
            this.mStartingThemeResName = source.mStartingThemeResName;
        }
        Map<String, Map<String, List<String>>> map = source.mCapabilityBindings;
        if (map != null) {
            this.mCapabilityBindings = cloneCapabilityBindings(map);
        }
    }

    public static Icon validateIcon(Icon icon) {
        switch (icon.getType()) {
            case 1:
            case 2:
            case 4:
            case 5:
            case 6:
                if (icon.hasTint()) {
                    throw new IllegalArgumentException("Icons with tints are not supported");
                }
                return icon;
            case 3:
            default:
                throw getInvalidIconException();
        }
    }

    public static IllegalArgumentException getInvalidIconException() {
        return new IllegalArgumentException("Unsupported icon type: only the bitmap and resource types are supported");
    }

    /* renamed from: android.content.pm.ShortcutInfo$Builder */
    /* loaded from: classes.dex */
    public static class Builder {
        private ComponentName mActivity;
        private Map<String, Map<String, List<String>>> mCapabilityBindings;
        private Set<String> mCategories;
        private final Context mContext;
        private CharSequence mDisabledMessage;
        private int mDisabledMessageResId;
        private int mExcludedSurfaces;
        private PersistableBundle mExtras;
        private Icon mIcon;
        private String mId;
        private Intent[] mIntents;
        private boolean mIsLongLived;
        private LocusId mLocusId;
        private Person[] mPersons;
        private int mRank = Integer.MAX_VALUE;
        private int mStartingThemeResId;
        private CharSequence mText;
        private int mTextResId;
        private CharSequence mTitle;
        private int mTitleResId;

        @Deprecated
        public Builder(Context context) {
            this.mContext = context;
        }

        @Deprecated
        public Builder setId(String id) {
            this.mId = (String) Preconditions.checkStringNotEmpty(id, "id cannot be empty");
            return this;
        }

        public Builder(Context context, String id) {
            this.mContext = context;
            this.mId = (String) Preconditions.checkStringNotEmpty(id, "id cannot be empty");
        }

        public Builder setLocusId(LocusId locusId) {
            this.mLocusId = (LocusId) Objects.requireNonNull(locusId, "locusId cannot be null");
            return this;
        }

        public Builder setActivity(ComponentName activity) {
            this.mActivity = (ComponentName) Objects.requireNonNull(activity, "activity cannot be null");
            return this;
        }

        public Builder setIcon(Icon icon) {
            this.mIcon = ShortcutInfo.validateIcon(icon);
            return this;
        }

        public Builder setStartingTheme(int themeResId) {
            this.mStartingThemeResId = themeResId;
            return this;
        }

        @Deprecated
        public Builder setShortLabelResId(int shortLabelResId) {
            Preconditions.checkState(this.mTitle == null, "shortLabel already set");
            this.mTitleResId = shortLabelResId;
            return this;
        }

        public Builder setShortLabel(CharSequence shortLabel) {
            Preconditions.checkState(this.mTitleResId == 0, "shortLabelResId already set");
            this.mTitle = Preconditions.checkStringNotEmpty(shortLabel, "shortLabel cannot be empty");
            return this;
        }

        @Deprecated
        public Builder setLongLabelResId(int longLabelResId) {
            Preconditions.checkState(this.mText == null, "longLabel already set");
            this.mTextResId = longLabelResId;
            return this;
        }

        public Builder setLongLabel(CharSequence longLabel) {
            Preconditions.checkState(this.mTextResId == 0, "longLabelResId already set");
            this.mText = Preconditions.checkStringNotEmpty(longLabel, "longLabel cannot be empty");
            return this;
        }

        @Deprecated
        public Builder setTitle(CharSequence value) {
            return setShortLabel(value);
        }

        @Deprecated
        public Builder setTitleResId(int value) {
            return setShortLabelResId(value);
        }

        @Deprecated
        public Builder setText(CharSequence value) {
            return setLongLabel(value);
        }

        @Deprecated
        public Builder setTextResId(int value) {
            return setLongLabelResId(value);
        }

        @Deprecated
        public Builder setDisabledMessageResId(int disabledMessageResId) {
            Preconditions.checkState(this.mDisabledMessage == null, "disabledMessage already set");
            this.mDisabledMessageResId = disabledMessageResId;
            return this;
        }

        public Builder setDisabledMessage(CharSequence disabledMessage) {
            Preconditions.checkState(this.mDisabledMessageResId == 0, "disabledMessageResId already set");
            this.mDisabledMessage = Preconditions.checkStringNotEmpty(disabledMessage, "disabledMessage cannot be empty");
            return this;
        }

        public Builder setCategories(Set<String> categories) {
            this.mCategories = categories;
            return this;
        }

        public Builder setIntent(Intent intent) {
            return setIntents(new Intent[]{intent});
        }

        public Builder setIntents(Intent[] intents) {
            Objects.requireNonNull(intents, "intents cannot be null");
            if (intents.length == 0) {
                throw new IllegalArgumentException("intents cannot be empty");
            }
            for (Intent intent : intents) {
                Objects.requireNonNull(intent, "intents cannot contain null");
                Objects.requireNonNull(intent.getAction(), "intent's action must be set");
            }
            this.mIntents = ShortcutInfo.cloneIntents(intents);
            return this;
        }

        public Builder setPerson(Person person) {
            return setPersons(new Person[]{person});
        }

        public Builder setPersons(Person[] persons) {
            Objects.requireNonNull(persons, "persons cannot be null");
            if (persons.length == 0) {
                throw new IllegalArgumentException("persons cannot be empty");
            }
            for (Person person : persons) {
                Objects.requireNonNull(person, "persons cannot contain null");
            }
            this.mPersons = ShortcutInfo.clonePersons(persons);
            return this;
        }

        public Builder setLongLived(boolean longLived) {
            this.mIsLongLived = longLived;
            return this;
        }

        public Builder setRank(int rank) {
            Preconditions.checkArgument(rank >= 0, "Rank cannot be negative or bigger than MAX_RANK");
            this.mRank = rank;
            return this;
        }

        public Builder setExtras(PersistableBundle extras) {
            this.mExtras = extras;
            return this;
        }

        public Builder addCapabilityBinding(Capability capability, CapabilityParams capabilityParams) {
            Objects.requireNonNull(capability);
            if (this.mCapabilityBindings == null) {
                this.mCapabilityBindings = new ArrayMap(1);
            }
            if (!this.mCapabilityBindings.containsKey(capability.getName())) {
                this.mCapabilityBindings.put(capability.getName(), new ArrayMap(0));
            }
            if (capabilityParams == null) {
                return this;
            }
            Map<String, List<String>> params = this.mCapabilityBindings.get(capability.getName());
            params.put(capabilityParams.getName(), capabilityParams.getValues());
            return this;
        }

        public Builder setExcludedFromSurfaces(int surfaces) {
            this.mExcludedSurfaces = surfaces;
            return this;
        }

        public ShortcutInfo build() {
            return new ShortcutInfo(this);
        }
    }

    public String getId() {
        return this.mId;
    }

    public LocusId getLocusId() {
        return this.mLocusId;
    }

    public String getPackage() {
        return this.mPackageName;
    }

    public ComponentName getActivity() {
        return this.mActivity;
    }

    public void setActivity(ComponentName activity) {
        this.mActivity = activity;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public String getStartingThemeResName() {
        return this.mStartingThemeResName;
    }

    @Deprecated
    public CharSequence getTitle() {
        return this.mTitle;
    }

    @Deprecated
    public int getTitleResId() {
        return this.mTitleResId;
    }

    @Deprecated
    public CharSequence getText() {
        return this.mText;
    }

    @Deprecated
    public int getTextResId() {
        return this.mTextResId;
    }

    public CharSequence getShortLabel() {
        return this.mTitle;
    }

    public int getShortLabelResourceId() {
        return this.mTitleResId;
    }

    public CharSequence getLongLabel() {
        return this.mText;
    }

    public CharSequence getLabel() {
        CharSequence label = getLongLabel();
        if (TextUtils.isEmpty(label)) {
            return getShortLabel();
        }
        return label;
    }

    public int getLongLabelResourceId() {
        return this.mTextResId;
    }

    public CharSequence getDisabledMessage() {
        return this.mDisabledMessage;
    }

    public int getDisabledMessageResourceId() {
        return this.mDisabledMessageResId;
    }

    public void setDisabledReason(int reason) {
        this.mDisabledReason = reason;
    }

    public int getDisabledReason() {
        return this.mDisabledReason;
    }

    public Set<String> getCategories() {
        return this.mCategories;
    }

    public Intent getIntent() {
        Intent[] intentArr = this.mIntents;
        if (intentArr == null || intentArr.length == 0) {
            return null;
        }
        int last = intentArr.length - 1;
        Intent intent = new Intent(this.mIntents[last]);
        return setIntentExtras(intent, this.mIntentPersistableExtrases[last]);
    }

    public Intent[] getIntents() {
        Intent[] intentArr = this.mIntents;
        if (intentArr == null) {
            return null;
        }
        Intent[] ret = new Intent[intentArr.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = new Intent(this.mIntents[i]);
            setIntentExtras(ret[i], this.mIntentPersistableExtrases[i]);
        }
        return ret;
    }

    public Intent[] getIntentsNoExtras() {
        return this.mIntents;
    }

    @SystemApi
    public Person[] getPersons() {
        return clonePersons(this.mPersons);
    }

    public PersistableBundle[] getIntentPersistableExtrases() {
        return this.mIntentPersistableExtrases;
    }

    public int getRank() {
        return this.mRank;
    }

    public boolean hasRank() {
        return this.mRank != Integer.MAX_VALUE;
    }

    public void setRank(int rank) {
        this.mRank = rank;
    }

    public void clearImplicitRankAndRankChangedFlag() {
        this.mImplicitRank = 0;
    }

    public void setImplicitRank(int rank) {
        this.mImplicitRank = (this.mImplicitRank & Integer.MIN_VALUE) | (Integer.MAX_VALUE & rank);
    }

    public int getImplicitRank() {
        return this.mImplicitRank & Integer.MAX_VALUE;
    }

    public void setRankChanged() {
        this.mImplicitRank |= Integer.MIN_VALUE;
    }

    public boolean isRankChanged() {
        return (this.mImplicitRank & Integer.MIN_VALUE) != 0;
    }

    public PersistableBundle getExtras() {
        return this.mExtras;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public UserHandle getUserHandle() {
        return UserHandle.m145of(this.mUserId);
    }

    public long getLastChangedTimestamp() {
        return this.mLastChangedTimestamp;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public void replaceFlags(int flags) {
        this.mFlags = flags;
    }

    public void addFlags(int flags) {
        this.mFlags |= flags;
    }

    public void clearFlags(int flags) {
        this.mFlags &= ~flags;
    }

    public boolean hasFlags(int flags) {
        return (this.mFlags & flags) == flags;
    }

    public boolean isReturnedByServer() {
        return hasFlags(1024);
    }

    public void setReturnedByServer() {
        addFlags(1024);
    }

    public boolean isLongLived() {
        return hasFlags(8192);
    }

    public void setLongLived() {
        addFlags(8192);
    }

    public void setCached(int cacheFlag) {
        addFlags(cacheFlag);
    }

    public boolean isCached() {
        return (getFlags() & FLAG_CACHED_ALL) != 0;
    }

    public boolean isDynamic() {
        return hasFlags(1);
    }

    public boolean isPinned() {
        return hasFlags(2);
    }

    public boolean isDeclaredInManifest() {
        return hasFlags(32);
    }

    @Deprecated
    public boolean isManifestShortcut() {
        return isDeclaredInManifest();
    }

    public boolean isFloating() {
        return ((!isPinned() && !isCached()) || isDynamic() || isManifestShortcut()) ? false : true;
    }

    public boolean isOriginallyFromManifest() {
        return hasFlags(256);
    }

    public boolean isDynamicVisible() {
        return isDynamic() && isVisibleToPublisher();
    }

    public boolean isPinnedVisible() {
        return isPinned() && isVisibleToPublisher();
    }

    public boolean isManifestVisible() {
        return isDeclaredInManifest() && isVisibleToPublisher();
    }

    public boolean isNonManifestVisible() {
        return !isDeclaredInManifest() && isVisibleToPublisher() && (isPinned() || isCached() || isDynamic());
    }

    public boolean isImmutable() {
        return hasFlags(256);
    }

    public boolean isEnabled() {
        return !hasFlags(64);
    }

    public boolean isAlive() {
        return hasFlags(2) || hasFlags(1) || hasFlags(32) || isCached();
    }

    public boolean usesQuota() {
        return hasFlags(1) || hasFlags(32);
    }

    public boolean hasIconResource() {
        return hasFlags(4);
    }

    public boolean hasIconUri() {
        return hasFlags(32768);
    }

    public boolean hasStringResources() {
        return (this.mTitleResId == 0 && this.mTextResId == 0 && this.mDisabledMessageResId == 0) ? false : true;
    }

    public boolean hasAnyResources() {
        return hasIconResource() || hasStringResources();
    }

    public boolean hasIconFile() {
        return hasFlags(8);
    }

    public boolean hasAdaptiveBitmap() {
        return hasFlags(512);
    }

    public boolean isIconPendingSave() {
        return hasFlags(2048);
    }

    public void setIconPendingSave() {
        addFlags(2048);
    }

    public void clearIconPendingSave() {
        clearFlags(2048);
    }

    public boolean isVisibleToPublisher() {
        return !isDisabledForRestoreIssue(this.mDisabledReason);
    }

    public boolean hasKeyFieldsOnly() {
        return hasFlags(16);
    }

    public boolean hasStringResourcesResolved() {
        return hasFlags(128);
    }

    public void updateTimestamp() {
        this.mLastChangedTimestamp = System.currentTimeMillis();
    }

    public void setTimestamp(long value) {
        this.mLastChangedTimestamp = value;
    }

    public void clearIcon() {
        this.mIcon = null;
    }

    public void setIconResourceId(int iconResourceId) {
        if (this.mIconResId != iconResourceId) {
            this.mIconResName = null;
        }
        this.mIconResId = iconResourceId;
    }

    public int getIconResourceId() {
        return this.mIconResId;
    }

    public void setIconUri(String iconUri) {
        this.mIconUri = iconUri;
    }

    public String getIconUri() {
        return this.mIconUri;
    }

    public String getBitmapPath() {
        return this.mBitmapPath;
    }

    public void setBitmapPath(String bitmapPath) {
        this.mBitmapPath = bitmapPath;
    }

    public void setDisabledMessageResId(int disabledMessageResId) {
        if (this.mDisabledMessageResId != disabledMessageResId) {
            this.mDisabledMessageResName = null;
        }
        this.mDisabledMessageResId = disabledMessageResId;
        this.mDisabledMessage = null;
    }

    public void setDisabledMessage(String disabledMessage) {
        this.mDisabledMessage = disabledMessage;
        this.mDisabledMessageResId = 0;
        this.mDisabledMessageResName = null;
    }

    public String getTitleResName() {
        return this.mTitleResName;
    }

    public void setTitleResName(String titleResName) {
        this.mTitleResName = titleResName;
    }

    public String getTextResName() {
        return this.mTextResName;
    }

    public void setTextResName(String textResName) {
        this.mTextResName = textResName;
    }

    public String getDisabledMessageResName() {
        return this.mDisabledMessageResName;
    }

    public void setDisabledMessageResName(String disabledMessageResName) {
        this.mDisabledMessageResName = disabledMessageResName;
    }

    public String getIconResName() {
        return this.mIconResName;
    }

    public void setIconResName(String iconResName) {
        this.mIconResName = iconResName;
    }

    public void setIntents(Intent[] intents) throws IllegalArgumentException {
        Objects.requireNonNull(intents);
        Preconditions.checkArgument(intents.length > 0);
        this.mIntents = cloneIntents(intents);
        fixUpIntentExtras();
    }

    public static Intent setIntentExtras(Intent intent, PersistableBundle extras) {
        if (extras == null) {
            intent.replaceExtras((Bundle) null);
        } else {
            intent.replaceExtras(new Bundle(extras));
        }
        return intent;
    }

    public void setCategories(Set<String> categories) {
        this.mCategories = cloneCategories(categories);
    }

    public boolean isExcludedFromSurfaces(int surface) {
        return (this.mExcludedSurfaces & surface) != 0;
    }

    public int getExcludedFromSurfaces() {
        return this.mExcludedSurfaces;
    }

    public Map<String, Map<String, List<String>>> getCapabilityBindingsInternal() {
        return cloneCapabilityBindings(this.mCapabilityBindings);
    }

    private static Map<String, Map<String, List<String>>> cloneCapabilityBindings(Map<String, Map<String, List<String>>> orig) {
        Map<String, List<String>> clone;
        if (orig == null) {
            return null;
        }
        Map<String, Map<String, List<String>>> ret = new ArrayMap<>();
        for (String capability : orig.keySet()) {
            Map<String, List<String>> params = orig.get(capability);
            if (params == null) {
                clone = null;
            } else {
                clone = new ArrayMap<>(params.size());
                for (String paramName : params.keySet()) {
                    List<String> paramValues = params.get(paramName);
                    clone.put(paramName, Collections.unmodifiableList(paramValues));
                }
            }
            ret.put(capability, Collections.unmodifiableMap(clone));
        }
        return Collections.unmodifiableMap(ret);
    }

    public List<Capability> getCapabilities() {
        Map<String, Map<String, List<String>>> map = this.mCapabilityBindings;
        if (map == null) {
            return new ArrayList(0);
        }
        return (List) map.keySet().stream().map(new Function() { // from class: android.content.pm.ShortcutInfo$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return new Capability((String) obj);
            }
        }).collect(Collectors.toList());
    }

    public List<CapabilityParams> getCapabilityParams(Capability capability) {
        Objects.requireNonNull(capability);
        Map<String, Map<String, List<String>>> map = this.mCapabilityBindings;
        if (map == null) {
            return new ArrayList(0);
        }
        Map<String, List<String>> param = map.get(capability.getName());
        if (param == null) {
            return new ArrayList(0);
        }
        List<CapabilityParams> ret = new ArrayList<>(param.size());
        for (String key : param.keySet()) {
            List<String> values = param.get(key);
            String primaryValue = values.get(0);
            List<String> aliases = values.size() == 1 ? Collections.emptyList() : values.subList(1, values.size());
            CapabilityParams.Builder builder = new CapabilityParams.Builder(key, primaryValue);
            for (String alias : aliases) {
                builder = builder.addAlias(alias);
            }
            ret.add(builder.build());
        }
        return ret;
    }

    private ShortcutInfo(Parcel source) {
        ClassLoader cl = getClass().getClassLoader();
        this.mUserId = source.readInt();
        this.mId = source.readString8();
        this.mPackageName = source.readString8();
        this.mActivity = (ComponentName) source.readParcelable(cl, ComponentName.class);
        this.mFlags = source.readInt();
        this.mIconResId = source.readInt();
        this.mLastChangedTimestamp = source.readLong();
        this.mDisabledReason = source.readInt();
        if (source.readInt() == 0) {
            return;
        }
        this.mIcon = (Icon) source.readParcelable(cl, Icon.class);
        this.mTitle = source.readCharSequence();
        this.mTitleResId = source.readInt();
        this.mText = source.readCharSequence();
        this.mTextResId = source.readInt();
        this.mDisabledMessage = source.readCharSequence();
        this.mDisabledMessageResId = source.readInt();
        this.mIntents = (Intent[]) source.readParcelableArray(cl, Intent.class);
        this.mIntentPersistableExtrases = (PersistableBundle[]) source.readParcelableArray(cl, PersistableBundle.class);
        this.mRank = source.readInt();
        this.mExtras = (PersistableBundle) source.readParcelable(cl, PersistableBundle.class);
        this.mBitmapPath = source.readString8();
        this.mIconResName = source.readString8();
        this.mTitleResName = source.readString8();
        this.mTextResName = source.readString8();
        this.mDisabledMessageResName = source.readString8();
        int N = source.readInt();
        if (N == 0) {
            this.mCategories = null;
        } else {
            this.mCategories = new ArraySet<>(N);
            for (int i = 0; i < N; i++) {
                this.mCategories.add(source.readString8().intern());
            }
        }
        this.mPersons = (Person[]) source.readParcelableArray(cl, Person.class);
        this.mLocusId = (LocusId) source.readParcelable(cl, LocusId.class);
        this.mIconUri = source.readString8();
        this.mStartingThemeResName = source.readString8();
        this.mExcludedSurfaces = source.readInt();
        Map<String, Map> rawCapabilityBindings = source.readHashMap(null, String.class, HashMap.class);
        if (rawCapabilityBindings != null && !rawCapabilityBindings.isEmpty()) {
            final Map<String, Map<String, List<String>>> capabilityBindings = new ArrayMap<>(rawCapabilityBindings.size());
            Objects.requireNonNull(capabilityBindings);
            rawCapabilityBindings.forEach(new BiConsumer() { // from class: android.content.pm.ShortcutInfo$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    capabilityBindings.put((String) obj, (Map) obj2);
                }
            });
            this.mCapabilityBindings = cloneCapabilityBindings(capabilityBindings);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserId);
        dest.writeString8(this.mId);
        dest.writeString8(this.mPackageName);
        dest.writeParcelable(this.mActivity, flags);
        dest.writeInt(this.mFlags);
        dest.writeInt(this.mIconResId);
        dest.writeLong(this.mLastChangedTimestamp);
        dest.writeInt(this.mDisabledReason);
        if (hasKeyFieldsOnly()) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(1);
        dest.writeParcelable(this.mIcon, flags);
        dest.writeCharSequence(this.mTitle);
        dest.writeInt(this.mTitleResId);
        dest.writeCharSequence(this.mText);
        dest.writeInt(this.mTextResId);
        dest.writeCharSequence(this.mDisabledMessage);
        dest.writeInt(this.mDisabledMessageResId);
        dest.writeParcelableArray(this.mIntents, flags);
        dest.writeParcelableArray(this.mIntentPersistableExtrases, flags);
        dest.writeInt(this.mRank);
        dest.writeParcelable(this.mExtras, flags);
        dest.writeString8(this.mBitmapPath);
        dest.writeString8(this.mIconResName);
        dest.writeString8(this.mTitleResName);
        dest.writeString8(this.mTextResName);
        dest.writeString8(this.mDisabledMessageResName);
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            int N = arraySet.size();
            dest.writeInt(N);
            for (int i = 0; i < N; i++) {
                dest.writeString8(this.mCategories.valueAt(i));
            }
        } else {
            dest.writeInt(0);
        }
        dest.writeParcelableArray(this.mPersons, flags);
        dest.writeParcelable(this.mLocusId, flags);
        dest.writeString8(this.mIconUri);
        dest.writeString8(this.mStartingThemeResName);
        dest.writeInt(this.mExcludedSurfaces);
        dest.writeMap(this.mCapabilityBindings);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return toStringInner(true, false, null);
    }

    public String toInsecureString() {
        return toStringInner(false, true, null);
    }

    public String toDumpString(String indent) {
        return toStringInner(false, true, indent);
    }

    private void addIndentOrComma(StringBuilder sb, String indent) {
        if (indent != null) {
            sb.append("\n  ");
            sb.append(indent);
            return;
        }
        sb.append(", ");
    }

    private String toStringInner(boolean secure, boolean includeInternalData, String indent) {
        StringBuilder sb = new StringBuilder();
        if (indent != null) {
            sb.append(indent);
        }
        sb.append("ShortcutInfo {");
        sb.append("id=");
        sb.append(secure ? "***" : this.mId);
        sb.append(", flags=0x");
        sb.append(Integer.toHexString(this.mFlags));
        sb.append(" [");
        if ((this.mFlags & 4096) != 0) {
            sb.append("Sdw");
        }
        if (!isEnabled()) {
            sb.append(AppSearchShortcutInfo.IS_DISABLED);
        }
        if (isImmutable()) {
            sb.append(AppSearchShortcutInfo.IS_IMMUTABLE);
        }
        if (isManifestShortcut()) {
            sb.append(AppSearchShortcutInfo.IS_MANIFEST);
        }
        if (isDynamic()) {
            sb.append(AppSearchShortcutInfo.IS_DYNAMIC);
        }
        if (isPinned()) {
            sb.append("Pin");
        }
        if (hasIconFile()) {
            sb.append("Ic-f");
        }
        if (isIconPendingSave()) {
            sb.append("Pens");
        }
        if (hasIconResource()) {
            sb.append("Ic-r");
        }
        if (hasIconUri()) {
            sb.append("Ic-u");
        }
        if (hasAdaptiveBitmap()) {
            sb.append("Ic-a");
        }
        if (hasKeyFieldsOnly()) {
            sb.append("Key");
        }
        if (hasStringResourcesResolved()) {
            sb.append("Str");
        }
        if (isReturnedByServer()) {
            sb.append("Rets");
        }
        if (isLongLived()) {
            sb.append("Liv");
        }
        if (isExcludedFromSurfaces(1)) {
            sb.append("Hid-L");
        }
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        addIndentOrComma(sb, indent);
        sb.append("packageName=");
        sb.append(this.mPackageName);
        addIndentOrComma(sb, indent);
        sb.append("activity=");
        sb.append(this.mActivity);
        addIndentOrComma(sb, indent);
        sb.append("shortLabel=");
        sb.append(secure ? "***" : this.mTitle);
        sb.append(", resId=");
        sb.append(this.mTitleResId);
        sb.append(NavigationBarInflaterView.SIZE_MOD_START);
        sb.append(this.mTitleResName);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        addIndentOrComma(sb, indent);
        sb.append("longLabel=");
        sb.append(secure ? "***" : this.mText);
        sb.append(", resId=");
        sb.append(this.mTextResId);
        sb.append(NavigationBarInflaterView.SIZE_MOD_START);
        sb.append(this.mTextResName);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        addIndentOrComma(sb, indent);
        sb.append("disabledMessage=");
        sb.append(secure ? "***" : this.mDisabledMessage);
        sb.append(", resId=");
        sb.append(this.mDisabledMessageResId);
        sb.append(NavigationBarInflaterView.SIZE_MOD_START);
        sb.append(this.mDisabledMessageResName);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        addIndentOrComma(sb, indent);
        sb.append("disabledReason=");
        sb.append(getDisabledReasonDebugString(this.mDisabledReason));
        String str = this.mStartingThemeResName;
        if (str != null && !str.isEmpty()) {
            addIndentOrComma(sb, indent);
            sb.append("SplashScreenThemeResName=");
            sb.append(this.mStartingThemeResName);
        }
        addIndentOrComma(sb, indent);
        sb.append("categories=");
        sb.append(this.mCategories);
        addIndentOrComma(sb, indent);
        sb.append("persons=");
        sb.append(Arrays.toString(this.mPersons));
        addIndentOrComma(sb, indent);
        sb.append("icon=");
        sb.append(this.mIcon);
        addIndentOrComma(sb, indent);
        sb.append("rank=");
        sb.append(this.mRank);
        sb.append(", timestamp=");
        sb.append(this.mLastChangedTimestamp);
        addIndentOrComma(sb, indent);
        sb.append("intents=");
        Intent[] intentArr = this.mIntents;
        if (intentArr == null) {
            sb.append("null");
        } else if (secure) {
            sb.append("size:");
            sb.append(this.mIntents.length);
        } else {
            int size = intentArr.length;
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            String sep = "";
            for (int i = 0; i < size; i++) {
                sb.append(sep);
                sep = ", ";
                sb.append(this.mIntents[i]);
                sb.append("/");
                sb.append(this.mIntentPersistableExtrases[i]);
            }
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        }
        addIndentOrComma(sb, indent);
        sb.append("extras=");
        sb.append(this.mExtras);
        if (includeInternalData) {
            addIndentOrComma(sb, indent);
            sb.append("iconRes=");
            sb.append(this.mIconResId);
            sb.append(NavigationBarInflaterView.SIZE_MOD_START);
            sb.append(this.mIconResName);
            sb.append(NavigationBarInflaterView.SIZE_MOD_END);
            sb.append(", bitmapPath=");
            sb.append(this.mBitmapPath);
            sb.append(", iconUri=");
            sb.append(this.mIconUri);
        }
        if (this.mLocusId != null) {
            sb.append("locusId=");
            sb.append(this.mLocusId);
        }
        sb.append("}");
        return sb.toString();
    }

    public ShortcutInfo(int userId, String id, String packageName, ComponentName activity, Icon icon, CharSequence title, int titleResId, String titleResName, CharSequence text, int textResId, String textResName, CharSequence disabledMessage, int disabledMessageResId, String disabledMessageResName, Set<String> categories, Intent[] intentsWithExtras, int rank, PersistableBundle extras, long lastChangedTimestamp, int flags, int iconResId, String iconResName, String bitmapPath, String iconUri, int disabledReason, Person[] persons, LocusId locusId, String startingThemeResName, Map<String, Map<String, List<String>>> capabilityBindings) {
        this.mUserId = userId;
        this.mId = id;
        this.mPackageName = packageName;
        this.mActivity = activity;
        this.mIcon = icon;
        this.mTitle = title;
        this.mTitleResId = titleResId;
        this.mTitleResName = titleResName;
        this.mText = text;
        this.mTextResId = textResId;
        this.mTextResName = textResName;
        this.mDisabledMessage = disabledMessage;
        this.mDisabledMessageResId = disabledMessageResId;
        this.mDisabledMessageResName = disabledMessageResName;
        this.mCategories = cloneCategories(categories);
        this.mIntents = cloneIntents(intentsWithExtras);
        fixUpIntentExtras();
        this.mRank = rank;
        this.mExtras = extras;
        this.mLastChangedTimestamp = lastChangedTimestamp;
        this.mFlags = flags;
        this.mIconResId = iconResId;
        this.mIconResName = iconResName;
        this.mBitmapPath = bitmapPath;
        this.mIconUri = iconUri;
        this.mDisabledReason = disabledReason;
        this.mPersons = persons;
        this.mLocusId = locusId;
        this.mStartingThemeResName = startingThemeResName;
        this.mCapabilityBindings = cloneCapabilityBindings(capabilityBindings);
    }
}
