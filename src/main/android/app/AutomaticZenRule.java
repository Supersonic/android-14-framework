package android.app;

import android.content.ComponentName;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.service.notification.ZenPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AutomaticZenRule implements Parcelable {
    public static final Parcelable.Creator<AutomaticZenRule> CREATOR = new Parcelable.Creator<AutomaticZenRule>() { // from class: android.app.AutomaticZenRule.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutomaticZenRule createFromParcel(Parcel source) {
            return new AutomaticZenRule(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AutomaticZenRule[] newArray(int size) {
            return new AutomaticZenRule[size];
        }
    };
    private static final int DISABLED = 0;
    private static final int ENABLED = 1;
    public static final int MAX_STRING_LENGTH = 1000;
    private Uri conditionId;
    private ComponentName configurationActivity;
    private long creationTime;
    private boolean enabled;
    private int interruptionFilter;
    private boolean mModified;
    private String mPkg;
    private ZenPolicy mZenPolicy;
    private String name;
    private ComponentName owner;

    @Deprecated
    public AutomaticZenRule(String name, ComponentName owner, Uri conditionId, int interruptionFilter, boolean enabled) {
        this(name, owner, null, conditionId, null, interruptionFilter, enabled);
    }

    public AutomaticZenRule(String name, ComponentName owner, ComponentName configurationActivity, Uri conditionId, ZenPolicy policy, int interruptionFilter, boolean enabled) {
        this.enabled = false;
        this.mModified = false;
        this.name = getTrimmedString(name);
        this.owner = getTrimmedComponentName(owner);
        this.configurationActivity = getTrimmedComponentName(configurationActivity);
        this.conditionId = getTrimmedUri(conditionId);
        this.interruptionFilter = interruptionFilter;
        this.enabled = enabled;
        this.mZenPolicy = policy;
    }

    public AutomaticZenRule(String name, ComponentName owner, ComponentName configurationActivity, Uri conditionId, ZenPolicy policy, int interruptionFilter, boolean enabled, long creationTime) {
        this(name, owner, configurationActivity, conditionId, policy, interruptionFilter, enabled);
        this.creationTime = creationTime;
    }

    public AutomaticZenRule(Parcel source) {
        this.enabled = false;
        this.mModified = false;
        this.enabled = source.readInt() == 1;
        if (source.readInt() == 1) {
            this.name = getTrimmedString(source.readString());
        }
        this.interruptionFilter = source.readInt();
        this.conditionId = getTrimmedUri((Uri) source.readParcelable(null, Uri.class));
        this.owner = getTrimmedComponentName((ComponentName) source.readParcelable(null, ComponentName.class));
        this.configurationActivity = getTrimmedComponentName((ComponentName) source.readParcelable(null, ComponentName.class));
        this.creationTime = source.readLong();
        this.mZenPolicy = (ZenPolicy) source.readParcelable(null, ZenPolicy.class);
        this.mModified = source.readInt() == 1;
        this.mPkg = source.readString();
    }

    public ComponentName getOwner() {
        return this.owner;
    }

    public ComponentName getConfigurationActivity() {
        return this.configurationActivity;
    }

    public Uri getConditionId() {
        return this.conditionId;
    }

    public int getInterruptionFilter() {
        return this.interruptionFilter;
    }

    public String getName() {
        return this.name;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isModified() {
        return this.mModified;
    }

    public ZenPolicy getZenPolicy() {
        ZenPolicy zenPolicy = this.mZenPolicy;
        if (zenPolicy == null) {
            return null;
        }
        return zenPolicy.copy();
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public void setConditionId(Uri conditionId) {
        this.conditionId = getTrimmedUri(conditionId);
    }

    public void setInterruptionFilter(int interruptionFilter) {
        this.interruptionFilter = interruptionFilter;
    }

    public void setName(String name) {
        this.name = getTrimmedString(name);
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setModified(boolean modified) {
        this.mModified = modified;
    }

    public void setZenPolicy(ZenPolicy zenPolicy) {
        this.mZenPolicy = zenPolicy == null ? null : zenPolicy.copy();
    }

    public void setConfigurationActivity(ComponentName componentName) {
        this.configurationActivity = getTrimmedComponentName(componentName);
    }

    public void setPackageName(String pkgName) {
        this.mPkg = pkgName;
    }

    public String getPackageName() {
        return this.mPkg;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.enabled ? 1 : 0);
        if (this.name != null) {
            dest.writeInt(1);
            dest.writeString(this.name);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.interruptionFilter);
        dest.writeParcelable(this.conditionId, 0);
        dest.writeParcelable(this.owner, 0);
        dest.writeParcelable(this.configurationActivity, 0);
        dest.writeLong(this.creationTime);
        dest.writeParcelable(this.mZenPolicy, 0);
        dest.writeInt(this.mModified ? 1 : 0);
        dest.writeString(this.mPkg);
    }

    public String toString() {
        return AutomaticZenRule.class.getSimpleName() + "[enabled=" + this.enabled + ",name=" + this.name + ",interruptionFilter=" + this.interruptionFilter + ",pkg=" + this.mPkg + ",conditionId=" + this.conditionId + ",owner=" + this.owner + ",configActivity=" + this.configurationActivity + ",creationTime=" + this.creationTime + ",mZenPolicy=" + this.mZenPolicy + ']';
    }

    public boolean equals(Object o) {
        if (o instanceof AutomaticZenRule) {
            if (o == this) {
                return true;
            }
            AutomaticZenRule other = (AutomaticZenRule) o;
            return other.enabled == this.enabled && other.mModified == this.mModified && Objects.equals(other.name, this.name) && other.interruptionFilter == this.interruptionFilter && Objects.equals(other.conditionId, this.conditionId) && Objects.equals(other.owner, this.owner) && Objects.equals(other.mZenPolicy, this.mZenPolicy) && Objects.equals(other.configurationActivity, this.configurationActivity) && Objects.equals(other.mPkg, this.mPkg) && other.creationTime == this.creationTime;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.enabled), this.name, Integer.valueOf(this.interruptionFilter), this.conditionId, this.owner, this.configurationActivity, this.mZenPolicy, Boolean.valueOf(this.mModified), Long.valueOf(this.creationTime), this.mPkg);
    }

    private static ComponentName getTrimmedComponentName(ComponentName cn) {
        if (cn == null) {
            return null;
        }
        return new ComponentName(getTrimmedString(cn.getPackageName()), getTrimmedString(cn.getClassName()));
    }

    private static String getTrimmedString(String input) {
        if (input != null && input.length() > 1000) {
            return input.substring(0, 1000);
        }
        return input;
    }

    private static Uri getTrimmedUri(Uri input) {
        if (input != null && input.toString().length() > 1000) {
            return Uri.parse(getTrimmedString(input.toString()));
        }
        return input;
    }
}
