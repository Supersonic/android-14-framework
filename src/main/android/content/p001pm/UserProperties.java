package android.content.p001pm;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Slog;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParserException;
@SystemApi
/* renamed from: android.content.pm.UserProperties */
/* loaded from: classes.dex */
public final class UserProperties implements Parcelable {
    private static final String ATTR_CREDENTIAL_SHAREABLE_WITH_PARENT = "credentialShareableWithParent";
    private static final String ATTR_CROSS_PROFILE_INTENT_FILTER_ACCESS_CONTROL = "crossProfileIntentFilterAccessControl";
    private static final String ATTR_CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY = "crossProfileIntentResolutionStrategy";
    private static final String ATTR_DELETE_APP_WITH_PARENT = "deleteAppWithParent";
    private static final String ATTR_INHERIT_DEVICE_POLICY = "inheritDevicePolicy";
    private static final String ATTR_MEDIA_SHARED_WITH_PARENT = "mediaSharedWithParent";
    private static final String ATTR_SHOW_IN_LAUNCHER = "showInLauncher";
    private static final String ATTR_SHOW_IN_SETTINGS = "showInSettings";
    private static final String ATTR_START_WITH_PARENT = "startWithParent";
    private static final String ATTR_UPDATE_CROSS_PROFILE_INTENT_FILTERS_ON_OTA = "updateCrossProfileIntentFiltersOnOTA";
    private static final String ATTR_USE_PARENTS_CONTACTS = "useParentsContacts";
    public static final int CROSS_PROFILE_INTENT_FILTER_ACCESS_LEVEL_ALL = 0;
    public static final int CROSS_PROFILE_INTENT_FILTER_ACCESS_LEVEL_SYSTEM = 10;
    public static final int CROSS_PROFILE_INTENT_FILTER_ACCESS_LEVEL_SYSTEM_ADD_ONLY = 20;
    public static final int CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY_DEFAULT = 0;
    public static final int CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY_NO_FILTERING = 1;
    private static final int INDEX_CREDENTIAL_SHAREABLE_WITH_PARENT = 9;
    private static final int INDEX_CROSS_PROFILE_INTENT_FILTER_ACCESS_CONTROL = 6;
    private static final int INDEX_CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY = 7;
    private static final int INDEX_DELETE_APP_WITH_PARENT = 10;
    private static final int INDEX_INHERIT_DEVICE_POLICY = 3;
    private static final int INDEX_MEDIA_SHARED_WITH_PARENT = 8;
    private static final int INDEX_SHOW_IN_LAUNCHER = 0;
    private static final int INDEX_SHOW_IN_SETTINGS = 2;
    private static final int INDEX_START_WITH_PARENT = 1;
    private static final int INDEX_UPDATE_CROSS_PROFILE_INTENT_FILTERS_ON_OTA = 5;
    private static final int INDEX_USE_PARENTS_CONTACTS = 4;
    public static final int INHERIT_DEVICE_POLICY_FROM_PARENT = 1;
    public static final int INHERIT_DEVICE_POLICY_NO = 0;
    public static final int SHOW_IN_LAUNCHER_NO = 2;
    public static final int SHOW_IN_LAUNCHER_SEPARATE = 1;
    public static final int SHOW_IN_LAUNCHER_WITH_PARENT = 0;
    public static final int SHOW_IN_SETTINGS_NO = 2;
    public static final int SHOW_IN_SETTINGS_SEPARATE = 1;
    public static final int SHOW_IN_SETTINGS_WITH_PARENT = 0;
    private boolean mCredentialShareableWithParent;
    private int mCrossProfileIntentFilterAccessControl;
    private int mCrossProfileIntentResolutionStrategy;
    private final UserProperties mDefaultProperties;
    private boolean mDeleteAppWithParent;
    private int mInheritDevicePolicy;
    private boolean mMediaSharedWithParent;
    private long mPropertiesPresent;
    private int mShowInLauncher;
    private int mShowInSettings;
    private boolean mStartWithParent;
    private boolean mUpdateCrossProfileIntentFiltersOnOTA;
    private boolean mUseParentsContacts;
    private static final String LOG_TAG = UserProperties.class.getSimpleName();
    public static final Parcelable.Creator<UserProperties> CREATOR = new Parcelable.Creator<UserProperties>() { // from class: android.content.pm.UserProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserProperties createFromParcel(Parcel source) {
            return new UserProperties(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserProperties[] newArray(int size) {
            return new UserProperties[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$CrossProfileIntentFilterAccessControlLevel */
    /* loaded from: classes.dex */
    public @interface CrossProfileIntentFilterAccessControlLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$CrossProfileIntentResolutionStrategy */
    /* loaded from: classes.dex */
    public @interface CrossProfileIntentResolutionStrategy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$InheritDevicePolicy */
    /* loaded from: classes.dex */
    public @interface InheritDevicePolicy {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$PropertyIndex */
    /* loaded from: classes.dex */
    private @interface PropertyIndex {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$ShowInLauncher */
    /* loaded from: classes.dex */
    public @interface ShowInLauncher {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.UserProperties$ShowInSettings */
    /* loaded from: classes.dex */
    public @interface ShowInSettings {
    }

    public UserProperties(UserProperties defaultProperties) {
        this.mPropertiesPresent = 0L;
        this.mDefaultProperties = defaultProperties;
        this.mPropertiesPresent = 0L;
    }

    public UserProperties(UserProperties orig, boolean exposeAllFields, boolean hasManagePermission, boolean hasQueryOrManagePermission) {
        this.mPropertiesPresent = 0L;
        if (orig.mDefaultProperties == null) {
            throw new IllegalArgumentException("Attempting to copy a non-original UserProperties.");
        }
        this.mDefaultProperties = null;
        if (exposeAllFields) {
            setStartWithParent(orig.getStartWithParent());
            setInheritDevicePolicy(orig.getInheritDevicePolicy());
            setUpdateCrossProfileIntentFiltersOnOTA(orig.getUpdateCrossProfileIntentFiltersOnOTA());
            setCrossProfileIntentFilterAccessControl(orig.getCrossProfileIntentFilterAccessControl());
            setCrossProfileIntentResolutionStrategy(orig.getCrossProfileIntentResolutionStrategy());
            setDeleteAppWithParent(orig.getDeleteAppWithParent());
        }
        if (hasManagePermission) {
            setShowInSettings(orig.getShowInSettings());
            setUseParentsContacts(orig.getUseParentsContacts());
        }
        setShowInLauncher(orig.getShowInLauncher());
        setMediaSharedWithParent(orig.isMediaSharedWithParent());
        setCredentialShareableWithParent(orig.isCredentialShareableWithParent());
    }

    private boolean isPresent(long index) {
        return (this.mPropertiesPresent & (1 << ((int) index))) != 0;
    }

    private void setPresent(long index) {
        this.mPropertiesPresent |= 1 << ((int) index);
    }

    public long getPropertiesPresent() {
        return this.mPropertiesPresent;
    }

    public int getShowInLauncher() {
        if (isPresent(0L)) {
            return this.mShowInLauncher;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mShowInLauncher;
        }
        throw new SecurityException("You don't have permission to query showInLauncher");
    }

    public void setShowInLauncher(int val) {
        this.mShowInLauncher = val;
        setPresent(0L);
    }

    public int getShowInSettings() {
        if (isPresent(2L)) {
            return this.mShowInSettings;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mShowInSettings;
        }
        throw new SecurityException("You don't have permission to query mShowInSettings");
    }

    public void setShowInSettings(int val) {
        this.mShowInSettings = val;
        setPresent(2L);
    }

    public boolean getStartWithParent() {
        if (isPresent(1L)) {
            return this.mStartWithParent;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mStartWithParent;
        }
        throw new SecurityException("You don't have permission to query startWithParent");
    }

    public void setStartWithParent(boolean val) {
        this.mStartWithParent = val;
        setPresent(1L);
    }

    public boolean getDeleteAppWithParent() {
        if (isPresent(10L)) {
            return this.mDeleteAppWithParent;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mDeleteAppWithParent;
        }
        throw new SecurityException("You don't have permission to query deleteAppWithParent");
    }

    public void setDeleteAppWithParent(boolean val) {
        this.mDeleteAppWithParent = val;
        setPresent(10L);
    }

    public int getInheritDevicePolicy() {
        if (isPresent(3L)) {
            return this.mInheritDevicePolicy;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mInheritDevicePolicy;
        }
        throw new SecurityException("You don't have permission to query inheritDevicePolicy");
    }

    public void setInheritDevicePolicy(int val) {
        this.mInheritDevicePolicy = val;
        setPresent(3L);
    }

    public boolean getUseParentsContacts() {
        if (isPresent(4L)) {
            return this.mUseParentsContacts;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mUseParentsContacts;
        }
        throw new SecurityException("You don't have permission to query useParentsContacts");
    }

    public void setUseParentsContacts(boolean val) {
        this.mUseParentsContacts = val;
        setPresent(4L);
    }

    public boolean getUpdateCrossProfileIntentFiltersOnOTA() {
        if (isPresent(5L)) {
            return this.mUpdateCrossProfileIntentFiltersOnOTA;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mUpdateCrossProfileIntentFiltersOnOTA;
        }
        throw new SecurityException("You don't have permission to query updateCrossProfileIntentFiltersOnOTA");
    }

    public void setUpdateCrossProfileIntentFiltersOnOTA(boolean val) {
        this.mUpdateCrossProfileIntentFiltersOnOTA = val;
        setPresent(5L);
    }

    public boolean isMediaSharedWithParent() {
        if (isPresent(8L)) {
            return this.mMediaSharedWithParent;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mMediaSharedWithParent;
        }
        throw new SecurityException("You don't have permission to query mediaSharedWithParent");
    }

    public void setMediaSharedWithParent(boolean val) {
        this.mMediaSharedWithParent = val;
        setPresent(8L);
    }

    public boolean isCredentialShareableWithParent() {
        if (isPresent(9L)) {
            return this.mCredentialShareableWithParent;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mCredentialShareableWithParent;
        }
        throw new SecurityException("You don't have permission to query credentialShareableWithParent");
    }

    public void setCredentialShareableWithParent(boolean val) {
        this.mCredentialShareableWithParent = val;
        setPresent(9L);
    }

    public int getCrossProfileIntentFilterAccessControl() {
        if (isPresent(6L)) {
            return this.mCrossProfileIntentFilterAccessControl;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mCrossProfileIntentFilterAccessControl;
        }
        throw new SecurityException("You don't have permission to query crossProfileIntentFilterAccessControl");
    }

    public void setCrossProfileIntentFilterAccessControl(int val) {
        this.mCrossProfileIntentFilterAccessControl = val;
        setPresent(6L);
    }

    public int getCrossProfileIntentResolutionStrategy() {
        if (isPresent(7L)) {
            return this.mCrossProfileIntentResolutionStrategy;
        }
        UserProperties userProperties = this.mDefaultProperties;
        if (userProperties != null) {
            return userProperties.mCrossProfileIntentResolutionStrategy;
        }
        throw new SecurityException("You don't have permission to query crossProfileIntentResolutionStrategy");
    }

    public void setCrossProfileIntentResolutionStrategy(int val) {
        this.mCrossProfileIntentResolutionStrategy = val;
        setPresent(7L);
    }

    public String toString() {
        return "UserProperties{mPropertiesPresent=" + Long.toBinaryString(this.mPropertiesPresent) + ", mShowInLauncher=" + getShowInLauncher() + ", mStartWithParent=" + getStartWithParent() + ", mShowInSettings=" + getShowInSettings() + ", mInheritDevicePolicy=" + getInheritDevicePolicy() + ", mUseParentsContacts=" + getUseParentsContacts() + ", mUpdateCrossProfileIntentFiltersOnOTA=" + getUpdateCrossProfileIntentFiltersOnOTA() + ", mCrossProfileIntentFilterAccessControl=" + getCrossProfileIntentFilterAccessControl() + ", mCrossProfileIntentResolutionStrategy=" + getCrossProfileIntentResolutionStrategy() + ", mMediaSharedWithParent=" + isMediaSharedWithParent() + ", mCredentialShareableWithParent=" + isCredentialShareableWithParent() + ", mDeleteAppWithParent=" + getDeleteAppWithParent() + "}";
    }

    public void println(PrintWriter pw, String prefix) {
        pw.println(prefix + "UserProperties:");
        pw.println(prefix + "    mPropertiesPresent=" + Long.toBinaryString(this.mPropertiesPresent));
        pw.println(prefix + "    mShowInLauncher=" + getShowInLauncher());
        pw.println(prefix + "    mStartWithParent=" + getStartWithParent());
        pw.println(prefix + "    mShowInSettings=" + getShowInSettings());
        pw.println(prefix + "    mInheritDevicePolicy=" + getInheritDevicePolicy());
        pw.println(prefix + "    mUseParentsContacts=" + getUseParentsContacts());
        pw.println(prefix + "    mUpdateCrossProfileIntentFiltersOnOTA=" + getUpdateCrossProfileIntentFiltersOnOTA());
        pw.println(prefix + "    mCrossProfileIntentFilterAccessControl=" + getCrossProfileIntentFilterAccessControl());
        pw.println(prefix + "    mCrossProfileIntentResolutionStrategy=" + getCrossProfileIntentResolutionStrategy());
        pw.println(prefix + "    mMediaSharedWithParent=" + isMediaSharedWithParent());
        pw.println(prefix + "    mCredentialShareableWithParent=" + isCredentialShareableWithParent());
        pw.println(prefix + "    mDeleteAppWithParent=" + getDeleteAppWithParent());
    }

    public UserProperties(TypedXmlPullParser parser, UserProperties defaultUserPropertiesReference) throws IOException, XmlPullParserException {
        this(defaultUserPropertiesReference);
        updateFromXml(parser);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void updateFromXml(TypedXmlPullParser parser) throws IOException, XmlPullParserException {
        char c;
        int attributeCount = parser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = parser.getAttributeName(i);
            switch (attributeName.hashCode()) {
                case -1612179643:
                    if (attributeName.equals(ATTR_SHOW_IN_SETTINGS)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -1041116634:
                    if (attributeName.equals(ATTR_DELETE_APP_WITH_PARENT)) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -934956400:
                    if (attributeName.equals(ATTR_UPDATE_CROSS_PROFILE_INTENT_FILTERS_ON_OTA)) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -842277572:
                    if (attributeName.equals(ATTR_CROSS_PROFILE_INTENT_FILTER_ACCESS_CONTROL)) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case -627117223:
                    if (attributeName.equals(ATTR_MEDIA_SHARED_WITH_PARENT)) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case -317094126:
                    if (attributeName.equals(ATTR_START_WITH_PARENT)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case -159094078:
                    if (attributeName.equals(ATTR_SHOW_IN_LAUNCHER)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 428661202:
                    if (attributeName.equals(ATTR_CREDENTIAL_SHAREABLE_WITH_PARENT)) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case 490625987:
                    if (attributeName.equals(ATTR_INHERIT_DEVICE_POLICY)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 585305077:
                    if (attributeName.equals(ATTR_USE_PARENTS_CONTACTS)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 2082796132:
                    if (attributeName.equals(ATTR_CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY)) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    setShowInLauncher(parser.getAttributeInt(i));
                    break;
                case 1:
                    setStartWithParent(parser.getAttributeBoolean(i));
                    break;
                case 2:
                    setShowInSettings(parser.getAttributeInt(i));
                    break;
                case 3:
                    setInheritDevicePolicy(parser.getAttributeInt(i));
                    break;
                case 4:
                    setUseParentsContacts(parser.getAttributeBoolean(i));
                    break;
                case 5:
                    setUpdateCrossProfileIntentFiltersOnOTA(parser.getAttributeBoolean(i));
                    break;
                case 6:
                    setCrossProfileIntentFilterAccessControl(parser.getAttributeInt(i));
                    break;
                case 7:
                    setCrossProfileIntentResolutionStrategy(parser.getAttributeInt(i));
                    break;
                case '\b':
                    setMediaSharedWithParent(parser.getAttributeBoolean(i));
                    break;
                case '\t':
                    setCredentialShareableWithParent(parser.getAttributeBoolean(i));
                    break;
                case '\n':
                    setDeleteAppWithParent(parser.getAttributeBoolean(i));
                    break;
                default:
                    Slog.m90w(LOG_TAG, "Skipping unknown property " + attributeName);
                    break;
            }
        }
    }

    public void writeToXml(TypedXmlSerializer serializer) throws IOException, XmlPullParserException {
        if (isPresent(0L)) {
            serializer.attributeInt(null, ATTR_SHOW_IN_LAUNCHER, this.mShowInLauncher);
        }
        if (isPresent(1L)) {
            serializer.attributeBoolean(null, ATTR_START_WITH_PARENT, this.mStartWithParent);
        }
        if (isPresent(2L)) {
            serializer.attributeInt(null, ATTR_SHOW_IN_SETTINGS, this.mShowInSettings);
        }
        if (isPresent(3L)) {
            serializer.attributeInt(null, ATTR_INHERIT_DEVICE_POLICY, this.mInheritDevicePolicy);
        }
        if (isPresent(4L)) {
            serializer.attributeBoolean(null, ATTR_USE_PARENTS_CONTACTS, this.mUseParentsContacts);
        }
        if (isPresent(5L)) {
            serializer.attributeBoolean(null, ATTR_UPDATE_CROSS_PROFILE_INTENT_FILTERS_ON_OTA, this.mUpdateCrossProfileIntentFiltersOnOTA);
        }
        if (isPresent(6L)) {
            serializer.attributeInt(null, ATTR_CROSS_PROFILE_INTENT_FILTER_ACCESS_CONTROL, this.mCrossProfileIntentFilterAccessControl);
        }
        if (isPresent(7L)) {
            serializer.attributeInt(null, ATTR_CROSS_PROFILE_INTENT_RESOLUTION_STRATEGY, this.mCrossProfileIntentResolutionStrategy);
        }
        if (isPresent(8L)) {
            serializer.attributeBoolean(null, ATTR_MEDIA_SHARED_WITH_PARENT, this.mMediaSharedWithParent);
        }
        if (isPresent(9L)) {
            serializer.attributeBoolean(null, ATTR_CREDENTIAL_SHAREABLE_WITH_PARENT, this.mCredentialShareableWithParent);
        }
        if (isPresent(10L)) {
            serializer.attributeBoolean(null, ATTR_DELETE_APP_WITH_PARENT, this.mDeleteAppWithParent);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        dest.writeLong(this.mPropertiesPresent);
        dest.writeInt(this.mShowInLauncher);
        dest.writeBoolean(this.mStartWithParent);
        dest.writeInt(this.mShowInSettings);
        dest.writeInt(this.mInheritDevicePolicy);
        dest.writeBoolean(this.mUseParentsContacts);
        dest.writeBoolean(this.mUpdateCrossProfileIntentFiltersOnOTA);
        dest.writeInt(this.mCrossProfileIntentFilterAccessControl);
        dest.writeInt(this.mCrossProfileIntentResolutionStrategy);
        dest.writeBoolean(this.mMediaSharedWithParent);
        dest.writeBoolean(this.mCredentialShareableWithParent);
        dest.writeBoolean(this.mDeleteAppWithParent);
    }

    private UserProperties(Parcel source) {
        this.mPropertiesPresent = 0L;
        this.mDefaultProperties = null;
        this.mPropertiesPresent = source.readLong();
        this.mShowInLauncher = source.readInt();
        this.mStartWithParent = source.readBoolean();
        this.mShowInSettings = source.readInt();
        this.mInheritDevicePolicy = source.readInt();
        this.mUseParentsContacts = source.readBoolean();
        this.mUpdateCrossProfileIntentFiltersOnOTA = source.readBoolean();
        this.mCrossProfileIntentFilterAccessControl = source.readInt();
        this.mCrossProfileIntentResolutionStrategy = source.readInt();
        this.mMediaSharedWithParent = source.readBoolean();
        this.mCredentialShareableWithParent = source.readBoolean();
        this.mDeleteAppWithParent = source.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* renamed from: android.content.pm.UserProperties$Builder */
    /* loaded from: classes.dex */
    public static final class Builder {
        private int mShowInLauncher = 0;
        private boolean mStartWithParent = false;
        private int mShowInSettings = 0;
        private int mInheritDevicePolicy = 0;
        private boolean mUseParentsContacts = false;
        private boolean mUpdateCrossProfileIntentFiltersOnOTA = false;
        private int mCrossProfileIntentFilterAccessControl = 0;
        private int mCrossProfileIntentResolutionStrategy = 0;
        private boolean mMediaSharedWithParent = false;
        private boolean mCredentialShareableWithParent = false;
        private boolean mDeleteAppWithParent = false;

        public Builder setShowInLauncher(int showInLauncher) {
            this.mShowInLauncher = showInLauncher;
            return this;
        }

        public Builder setStartWithParent(boolean startWithParent) {
            this.mStartWithParent = startWithParent;
            return this;
        }

        public Builder setShowInSettings(int showInSettings) {
            this.mShowInSettings = showInSettings;
            return this;
        }

        public Builder setInheritDevicePolicy(int inheritRestrictionsDevicePolicy) {
            this.mInheritDevicePolicy = inheritRestrictionsDevicePolicy;
            return this;
        }

        public Builder setUseParentsContacts(boolean useParentsContacts) {
            this.mUseParentsContacts = useParentsContacts;
            return this;
        }

        public Builder setUpdateCrossProfileIntentFiltersOnOTA(boolean updateCrossProfileIntentFiltersOnOTA) {
            this.mUpdateCrossProfileIntentFiltersOnOTA = updateCrossProfileIntentFiltersOnOTA;
            return this;
        }

        public Builder setCrossProfileIntentFilterAccessControl(int crossProfileIntentFilterAccessControl) {
            this.mCrossProfileIntentFilterAccessControl = crossProfileIntentFilterAccessControl;
            return this;
        }

        public Builder setCrossProfileIntentResolutionStrategy(int crossProfileIntentResolutionStrategy) {
            this.mCrossProfileIntentResolutionStrategy = crossProfileIntentResolutionStrategy;
            return this;
        }

        public Builder setMediaSharedWithParent(boolean mediaSharedWithParent) {
            this.mMediaSharedWithParent = mediaSharedWithParent;
            return this;
        }

        public Builder setCredentialShareableWithParent(boolean credentialShareableWithParent) {
            this.mCredentialShareableWithParent = credentialShareableWithParent;
            return this;
        }

        public Builder setDeleteAppWithParent(boolean deleteAppWithParent) {
            this.mDeleteAppWithParent = deleteAppWithParent;
            return this;
        }

        public UserProperties build() {
            return new UserProperties(this.mShowInLauncher, this.mStartWithParent, this.mShowInSettings, this.mInheritDevicePolicy, this.mUseParentsContacts, this.mUpdateCrossProfileIntentFiltersOnOTA, this.mCrossProfileIntentFilterAccessControl, this.mCrossProfileIntentResolutionStrategy, this.mMediaSharedWithParent, this.mCredentialShareableWithParent, this.mDeleteAppWithParent);
        }
    }

    private UserProperties(int showInLauncher, boolean startWithParent, int showInSettings, int inheritDevicePolicy, boolean useParentsContacts, boolean updateCrossProfileIntentFiltersOnOTA, int crossProfileIntentFilterAccessControl, int crossProfileIntentResolutionStrategy, boolean mediaSharedWithParent, boolean credentialShareableWithParent, boolean deleteAppWithParent) {
        this.mPropertiesPresent = 0L;
        this.mDefaultProperties = null;
        setShowInLauncher(showInLauncher);
        setStartWithParent(startWithParent);
        setShowInSettings(showInSettings);
        setInheritDevicePolicy(inheritDevicePolicy);
        setUseParentsContacts(useParentsContacts);
        setUpdateCrossProfileIntentFiltersOnOTA(updateCrossProfileIntentFiltersOnOTA);
        setCrossProfileIntentFilterAccessControl(crossProfileIntentFilterAccessControl);
        setCrossProfileIntentResolutionStrategy(crossProfileIntentResolutionStrategy);
        setMediaSharedWithParent(mediaSharedWithParent);
        setCredentialShareableWithParent(credentialShareableWithParent);
        setDeleteAppWithParent(deleteAppWithParent);
    }
}
