package android.app.smartspace;

import android.annotation.SystemApi;
import android.app.smartspace.uitemplatedata.BaseTemplateData;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.format.DateFormat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SmartspaceTarget implements Parcelable {
    public static final Parcelable.Creator<SmartspaceTarget> CREATOR = new Parcelable.Creator<SmartspaceTarget>() { // from class: android.app.smartspace.SmartspaceTarget.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceTarget createFromParcel(Parcel source) {
            return new SmartspaceTarget(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceTarget[] newArray(int size) {
            return new SmartspaceTarget[size];
        }
    };
    public static final int FEATURE_ALARM = 7;
    public static final int FEATURE_BEDTIME_ROUTINE = 16;
    public static final int FEATURE_BLAZE_BUILD_PROGRESS = 40;
    public static final int FEATURE_CALENDAR = 2;
    public static final int FEATURE_COMMUTE_TIME = 3;
    public static final int FEATURE_CONSENT = 11;
    public static final int FEATURE_CROSS_DEVICE_TIMER = 32;
    public static final int FEATURE_DOORBELL = 30;
    public static final int FEATURE_DRIVING_MODE = 26;
    public static final int FEATURE_EARTHQUAKE_ALERT = 38;
    public static final int FEATURE_EARTHQUAKE_OCCURRED = 41;
    public static final int FEATURE_ETA_MONITORING = 18;
    public static final int FEATURE_FITNESS_TRACKING = 17;
    public static final int FEATURE_FLASHLIGHT = 28;
    public static final int FEATURE_FLIGHT = 4;
    public static final int FEATURE_GAS_STATION_PAYMENT = 24;
    public static final int FEATURE_HOLIDAY_ALARM = 34;
    public static final int FEATURE_LOYALTY_CARD = 14;
    public static final int FEATURE_MEDIA = 15;
    public static final int FEATURE_MEDIA_HEADS_UP = 36;
    public static final int FEATURE_MEDIA_RESUME = 31;
    public static final int FEATURE_MISSED_CALL = 19;
    public static final int FEATURE_ONBOARDING = 8;
    public static final int FEATURE_PACKAGE_TRACKING = 20;
    public static final int FEATURE_PAIRED_DEVICE_STATE = 25;
    public static final int FEATURE_REMINDER = 6;
    public static final int FEATURE_SAFETY_CHECK = 35;
    public static final int FEATURE_SEVERE_WEATHER_ALERT = 33;
    public static final int FEATURE_SHOPPING_LIST = 13;
    public static final int FEATURE_SLEEP_SUMMARY = 27;
    public static final int FEATURE_SPORTS = 9;
    public static final int FEATURE_STEP_COUNTING = 37;
    public static final int FEATURE_STEP_DATE = 39;
    public static final int FEATURE_STOCK_PRICE_CHANGE = 12;
    public static final int FEATURE_STOPWATCH = 22;
    public static final int FEATURE_TIMER = 21;
    public static final int FEATURE_TIME_TO_LEAVE = 29;
    public static final int FEATURE_TIPS = 5;
    public static final int FEATURE_UNDEFINED = 0;
    public static final int FEATURE_UPCOMING_ALARM = 23;
    public static final int FEATURE_WEATHER = 1;
    public static final int FEATURE_WEATHER_ALERT = 10;
    public static final int UI_TEMPLATE_CAROUSEL = 4;
    public static final int UI_TEMPLATE_COMBINED_CARDS = 6;
    public static final int UI_TEMPLATE_DEFAULT = 1;
    public static final int UI_TEMPLATE_HEAD_TO_HEAD = 5;
    public static final int UI_TEMPLATE_SUB_CARD = 7;
    public static final int UI_TEMPLATE_SUB_IMAGE = 2;
    public static final int UI_TEMPLATE_SUB_LIST = 3;
    public static final int UI_TEMPLATE_UNDEFINED = 0;
    private final List<SmartspaceAction> mActionChips;
    private final String mAssociatedSmartspaceTargetId;
    private final SmartspaceAction mBaseAction;
    private final ComponentName mComponentName;
    private final long mCreationTimeMillis;
    private final long mExpiryTimeMillis;
    private final int mFeatureType;
    private final SmartspaceAction mHeaderAction;
    private final List<SmartspaceAction> mIconGrid;
    private final float mScore;
    private final boolean mSensitive;
    private final boolean mShouldShowExpanded;
    private final Uri mSliceUri;
    private final String mSmartspaceTargetId;
    private final String mSourceNotificationKey;
    private final BaseTemplateData mTemplateData;
    private final UserHandle mUserHandle;
    private final AppWidgetProviderInfo mWidget;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface FeatureType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface UiTemplateType {
    }

    private SmartspaceTarget(Parcel in) {
        this.mSmartspaceTargetId = in.readString();
        this.mHeaderAction = (SmartspaceAction) in.readTypedObject(SmartspaceAction.CREATOR);
        this.mBaseAction = (SmartspaceAction) in.readTypedObject(SmartspaceAction.CREATOR);
        this.mCreationTimeMillis = in.readLong();
        this.mExpiryTimeMillis = in.readLong();
        this.mScore = in.readFloat();
        this.mActionChips = in.createTypedArrayList(SmartspaceAction.CREATOR);
        this.mIconGrid = in.createTypedArrayList(SmartspaceAction.CREATOR);
        this.mFeatureType = in.readInt();
        this.mSensitive = in.readBoolean();
        this.mShouldShowExpanded = in.readBoolean();
        this.mSourceNotificationKey = in.readString();
        this.mComponentName = (ComponentName) in.readTypedObject(ComponentName.CREATOR);
        this.mUserHandle = (UserHandle) in.readTypedObject(UserHandle.CREATOR);
        this.mAssociatedSmartspaceTargetId = in.readString();
        this.mSliceUri = (Uri) in.readTypedObject(Uri.CREATOR);
        this.mWidget = (AppWidgetProviderInfo) in.readTypedObject(AppWidgetProviderInfo.CREATOR);
        this.mTemplateData = (BaseTemplateData) in.readParcelable(null, BaseTemplateData.class);
    }

    private SmartspaceTarget(String smartspaceTargetId, SmartspaceAction headerAction, SmartspaceAction baseAction, long creationTimeMillis, long expiryTimeMillis, float score, List<SmartspaceAction> actionChips, List<SmartspaceAction> iconGrid, int featureType, boolean sensitive, boolean shouldShowExpanded, String sourceNotificationKey, ComponentName componentName, UserHandle userHandle, String associatedSmartspaceTargetId, Uri sliceUri, AppWidgetProviderInfo widget, BaseTemplateData templateData) {
        this.mSmartspaceTargetId = smartspaceTargetId;
        this.mHeaderAction = headerAction;
        this.mBaseAction = baseAction;
        this.mCreationTimeMillis = creationTimeMillis;
        this.mExpiryTimeMillis = expiryTimeMillis;
        this.mScore = score;
        this.mActionChips = actionChips;
        this.mIconGrid = iconGrid;
        this.mFeatureType = featureType;
        this.mSensitive = sensitive;
        this.mShouldShowExpanded = shouldShowExpanded;
        this.mSourceNotificationKey = sourceNotificationKey;
        this.mComponentName = componentName;
        this.mUserHandle = userHandle;
        this.mAssociatedSmartspaceTargetId = associatedSmartspaceTargetId;
        this.mSliceUri = sliceUri;
        this.mWidget = widget;
        this.mTemplateData = templateData;
    }

    public String getSmartspaceTargetId() {
        return this.mSmartspaceTargetId;
    }

    public SmartspaceAction getHeaderAction() {
        return this.mHeaderAction;
    }

    public SmartspaceAction getBaseAction() {
        return this.mBaseAction;
    }

    public long getCreationTimeMillis() {
        return this.mCreationTimeMillis;
    }

    public long getExpiryTimeMillis() {
        return this.mExpiryTimeMillis;
    }

    public float getScore() {
        return this.mScore;
    }

    public List<SmartspaceAction> getActionChips() {
        return this.mActionChips;
    }

    public List<SmartspaceAction> getIconGrid() {
        return this.mIconGrid;
    }

    public int getFeatureType() {
        return this.mFeatureType;
    }

    public boolean isSensitive() {
        return this.mSensitive;
    }

    public boolean shouldShowExpanded() {
        return this.mShouldShowExpanded;
    }

    public String getSourceNotificationKey() {
        return this.mSourceNotificationKey;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public String getAssociatedSmartspaceTargetId() {
        return this.mAssociatedSmartspaceTargetId;
    }

    public Uri getSliceUri() {
        return this.mSliceUri;
    }

    public AppWidgetProviderInfo getWidget() {
        return this.mWidget;
    }

    public BaseTemplateData getTemplateData() {
        return this.mTemplateData;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSmartspaceTargetId);
        dest.writeTypedObject(this.mHeaderAction, flags);
        dest.writeTypedObject(this.mBaseAction, flags);
        dest.writeLong(this.mCreationTimeMillis);
        dest.writeLong(this.mExpiryTimeMillis);
        dest.writeFloat(this.mScore);
        dest.writeTypedList(this.mActionChips);
        dest.writeTypedList(this.mIconGrid);
        dest.writeInt(this.mFeatureType);
        dest.writeBoolean(this.mSensitive);
        dest.writeBoolean(this.mShouldShowExpanded);
        dest.writeString(this.mSourceNotificationKey);
        dest.writeTypedObject(this.mComponentName, flags);
        dest.writeTypedObject(this.mUserHandle, flags);
        dest.writeString(this.mAssociatedSmartspaceTargetId);
        dest.writeTypedObject(this.mSliceUri, flags);
        dest.writeTypedObject(this.mWidget, flags);
        dest.writeParcelable(this.mTemplateData, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "SmartspaceTarget{mSmartspaceTargetId='" + this.mSmartspaceTargetId + DateFormat.QUOTE + ", mHeaderAction=" + this.mHeaderAction + ", mBaseAction=" + this.mBaseAction + ", mCreationTimeMillis=" + this.mCreationTimeMillis + ", mExpiryTimeMillis=" + this.mExpiryTimeMillis + ", mScore=" + this.mScore + ", mActionChips=" + this.mActionChips + ", mIconGrid=" + this.mIconGrid + ", mFeatureType=" + this.mFeatureType + ", mSensitive=" + this.mSensitive + ", mShouldShowExpanded=" + this.mShouldShowExpanded + ", mSourceNotificationKey='" + this.mSourceNotificationKey + DateFormat.QUOTE + ", mComponentName=" + this.mComponentName + ", mUserHandle=" + this.mUserHandle + ", mAssociatedSmartspaceTargetId='" + this.mAssociatedSmartspaceTargetId + DateFormat.QUOTE + ", mSliceUri=" + this.mSliceUri + ", mWidget=" + this.mWidget + ", mTemplateData=" + this.mTemplateData + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SmartspaceTarget that = (SmartspaceTarget) o;
        if (this.mCreationTimeMillis == that.mCreationTimeMillis && this.mExpiryTimeMillis == that.mExpiryTimeMillis && Float.compare(that.mScore, this.mScore) == 0 && this.mFeatureType == that.mFeatureType && this.mSensitive == that.mSensitive && this.mShouldShowExpanded == that.mShouldShowExpanded && this.mSmartspaceTargetId.equals(that.mSmartspaceTargetId) && Objects.equals(this.mHeaderAction, that.mHeaderAction) && Objects.equals(this.mBaseAction, that.mBaseAction) && Objects.equals(this.mActionChips, that.mActionChips) && Objects.equals(this.mIconGrid, that.mIconGrid) && Objects.equals(this.mSourceNotificationKey, that.mSourceNotificationKey) && this.mComponentName.equals(that.mComponentName) && this.mUserHandle.equals(that.mUserHandle) && Objects.equals(this.mAssociatedSmartspaceTargetId, that.mAssociatedSmartspaceTargetId) && Objects.equals(this.mSliceUri, that.mSliceUri) && Objects.equals(this.mWidget, that.mWidget) && Objects.equals(this.mTemplateData, that.mTemplateData)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mSmartspaceTargetId, this.mHeaderAction, this.mBaseAction, Long.valueOf(this.mCreationTimeMillis), Long.valueOf(this.mExpiryTimeMillis), Float.valueOf(this.mScore), this.mActionChips, this.mIconGrid, Integer.valueOf(this.mFeatureType), Boolean.valueOf(this.mSensitive), Boolean.valueOf(this.mShouldShowExpanded), this.mSourceNotificationKey, this.mComponentName, this.mUserHandle, this.mAssociatedSmartspaceTargetId, this.mSliceUri, this.mWidget, this.mTemplateData);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private String mAssociatedSmartspaceTargetId;
        private SmartspaceAction mBaseAction;
        private final ComponentName mComponentName;
        private long mCreationTimeMillis;
        private long mExpiryTimeMillis;
        private int mFeatureType;
        private SmartspaceAction mHeaderAction;
        private float mScore;
        private boolean mSensitive;
        private boolean mShouldShowExpanded;
        private Uri mSliceUri;
        private final String mSmartspaceTargetId;
        private String mSourceNotificationKey;
        private BaseTemplateData mTemplateData;
        private final UserHandle mUserHandle;
        private AppWidgetProviderInfo mWidget;
        private List<SmartspaceAction> mActionChips = new ArrayList();
        private List<SmartspaceAction> mIconGrid = new ArrayList();

        public Builder(String smartspaceTargetId, ComponentName componentName, UserHandle userHandle) {
            this.mSmartspaceTargetId = smartspaceTargetId;
            this.mComponentName = componentName;
            this.mUserHandle = userHandle;
        }

        public Builder setHeaderAction(SmartspaceAction headerAction) {
            this.mHeaderAction = headerAction;
            return this;
        }

        public Builder setBaseAction(SmartspaceAction baseAction) {
            this.mBaseAction = baseAction;
            return this;
        }

        public Builder setCreationTimeMillis(long creationTimeMillis) {
            this.mCreationTimeMillis = creationTimeMillis;
            return this;
        }

        public Builder setExpiryTimeMillis(long expiryTimeMillis) {
            this.mExpiryTimeMillis = expiryTimeMillis;
            return this;
        }

        public Builder setScore(float score) {
            this.mScore = score;
            return this;
        }

        public Builder setActionChips(List<SmartspaceAction> actionChips) {
            this.mActionChips = actionChips;
            return this;
        }

        public Builder setIconGrid(List<SmartspaceAction> iconGrid) {
            this.mIconGrid = iconGrid;
            return this;
        }

        public Builder setFeatureType(int featureType) {
            this.mFeatureType = featureType;
            return this;
        }

        public Builder setSensitive(boolean sensitive) {
            this.mSensitive = sensitive;
            return this;
        }

        public Builder setShouldShowExpanded(boolean shouldShowExpanded) {
            this.mShouldShowExpanded = shouldShowExpanded;
            return this;
        }

        public Builder setSourceNotificationKey(String sourceNotificationKey) {
            this.mSourceNotificationKey = sourceNotificationKey;
            return this;
        }

        public Builder setAssociatedSmartspaceTargetId(String associatedSmartspaceTargetId) {
            this.mAssociatedSmartspaceTargetId = associatedSmartspaceTargetId;
            return this;
        }

        public Builder setSliceUri(Uri sliceUri) {
            this.mSliceUri = sliceUri;
            return this;
        }

        public Builder setWidget(AppWidgetProviderInfo widget) {
            this.mWidget = widget;
            return this;
        }

        public Builder setTemplateData(BaseTemplateData templateData) {
            this.mTemplateData = templateData;
            return this;
        }

        public SmartspaceTarget build() {
            if (this.mSmartspaceTargetId == null || this.mComponentName == null || this.mUserHandle == null) {
                throw new IllegalStateException("Please assign a value to all @NonNull args.");
            }
            return new SmartspaceTarget(this.mSmartspaceTargetId, this.mHeaderAction, this.mBaseAction, this.mCreationTimeMillis, this.mExpiryTimeMillis, this.mScore, this.mActionChips, this.mIconGrid, this.mFeatureType, this.mSensitive, this.mShouldShowExpanded, this.mSourceNotificationKey, this.mComponentName, this.mUserHandle, this.mAssociatedSmartspaceTargetId, this.mSliceUri, this.mWidget, this.mTemplateData);
        }
    }
}
