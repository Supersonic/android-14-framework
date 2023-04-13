package android.app.admin;

import android.content.ComponentName;
import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.C4057R;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class DeviceAdminInfo implements Parcelable {
    public static final Parcelable.Creator<DeviceAdminInfo> CREATOR;
    public static final int HEADLESS_DEVICE_OWNER_MODE_AFFILIATED = 1;
    public static final int HEADLESS_DEVICE_OWNER_MODE_UNSUPPORTED = 0;
    static final String TAG = "DeviceAdminInfo";
    public static final int USES_ENCRYPTED_STORAGE = 7;
    public static final int USES_POLICY_DISABLE_CAMERA = 8;
    public static final int USES_POLICY_DISABLE_KEYGUARD_FEATURES = 9;
    public static final int USES_POLICY_EXPIRE_PASSWORD = 6;
    public static final int USES_POLICY_FORCE_LOCK = 3;
    public static final int USES_POLICY_LIMIT_PASSWORD = 0;
    public static final int USES_POLICY_RESET_PASSWORD = 2;
    public static final int USES_POLICY_SETS_GLOBAL_PROXY = 5;
    public static final int USES_POLICY_WATCH_LOGIN = 1;
    public static final int USES_POLICY_WIPE_DATA = 4;
    final ActivityInfo mActivityInfo;
    int mHeadlessDeviceOwnerMode;
    boolean mSupportsTransferOwnership;
    int mUsesPolicies;
    boolean mVisible;
    static ArrayList<PolicyInfo> sPoliciesDisplayOrder = new ArrayList<>();
    static HashMap<String, Integer> sKnownPolicies = new HashMap<>();
    static SparseArray<PolicyInfo> sRevKnownPolicies = new SparseArray<>();

    /* loaded from: classes.dex */
    private @interface HeadlessDeviceOwnerMode {
    }

    /* loaded from: classes.dex */
    public static class PolicyInfo {
        public final int description;
        public final int descriptionForSecondaryUsers;
        public final int ident;
        public final int label;
        public final int labelForSecondaryUsers;
        public final String tag;

        public PolicyInfo(int ident, String tag, int label, int description) {
            this(ident, tag, label, description, label, description);
        }

        public PolicyInfo(int ident, String tag, int label, int description, int labelForSecondaryUsers, int descriptionForSecondaryUsers) {
            this.ident = ident;
            this.tag = tag;
            this.label = label;
            this.description = description;
            this.labelForSecondaryUsers = labelForSecondaryUsers;
            this.descriptionForSecondaryUsers = descriptionForSecondaryUsers;
        }
    }

    static {
        sPoliciesDisplayOrder.add(new PolicyInfo(4, "wipe-data", C4057R.string.policylab_wipeData, C4057R.string.policydesc_wipeData, C4057R.string.policylab_wipeData_secondaryUser, C4057R.string.policydesc_wipeData_secondaryUser));
        sPoliciesDisplayOrder.add(new PolicyInfo(2, "reset-password", C4057R.string.policylab_resetPassword, C4057R.string.policydesc_resetPassword));
        sPoliciesDisplayOrder.add(new PolicyInfo(0, "limit-password", C4057R.string.policylab_limitPassword, C4057R.string.policydesc_limitPassword));
        sPoliciesDisplayOrder.add(new PolicyInfo(1, "watch-login", C4057R.string.policylab_watchLogin, C4057R.string.policydesc_watchLogin, C4057R.string.policylab_watchLogin, C4057R.string.policydesc_watchLogin_secondaryUser));
        sPoliciesDisplayOrder.add(new PolicyInfo(3, "force-lock", C4057R.string.policylab_forceLock, C4057R.string.policydesc_forceLock));
        sPoliciesDisplayOrder.add(new PolicyInfo(5, "set-global-proxy", C4057R.string.policylab_setGlobalProxy, C4057R.string.policydesc_setGlobalProxy));
        sPoliciesDisplayOrder.add(new PolicyInfo(6, "expire-password", C4057R.string.policylab_expirePassword, C4057R.string.policydesc_expirePassword));
        sPoliciesDisplayOrder.add(new PolicyInfo(7, "encrypted-storage", C4057R.string.policylab_encryptedStorage, C4057R.string.policydesc_encryptedStorage));
        sPoliciesDisplayOrder.add(new PolicyInfo(8, "disable-camera", C4057R.string.policylab_disableCamera, C4057R.string.policydesc_disableCamera));
        sPoliciesDisplayOrder.add(new PolicyInfo(9, "disable-keyguard-features", C4057R.string.policylab_disableKeyguardFeatures, C4057R.string.policydesc_disableKeyguardFeatures));
        for (int i = 0; i < sPoliciesDisplayOrder.size(); i++) {
            PolicyInfo pi = sPoliciesDisplayOrder.get(i);
            sRevKnownPolicies.put(pi.ident, pi);
            sKnownPolicies.put(pi.tag, Integer.valueOf(pi.ident));
        }
        CREATOR = new Parcelable.Creator<DeviceAdminInfo>() { // from class: android.app.admin.DeviceAdminInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DeviceAdminInfo createFromParcel(Parcel source) {
                return new DeviceAdminInfo(source);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public DeviceAdminInfo[] newArray(int size) {
                return new DeviceAdminInfo[size];
            }
        };
    }

    public DeviceAdminInfo(Context context, ResolveInfo resolveInfo) throws XmlPullParserException, IOException {
        this(context, resolveInfo.activityInfo);
    }

    /* JADX WARN: Code restructure failed: missing block: B:65:0x013b, code lost:
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:?, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DeviceAdminInfo(Context context, ActivityInfo activityInfo) throws XmlPullParserException, IOException {
        int i;
        boolean z;
        boolean z2;
        boolean z3 = false;
        this.mHeadlessDeviceOwnerMode = 0;
        this.mActivityInfo = activityInfo;
        PackageManager pm = context.getPackageManager();
        XmlResourceParser parser = null;
        try {
            try {
                parser = activityInfo.loadXmlMetaData(pm, DeviceAdminReceiver.DEVICE_ADMIN_META_DATA);
                if (parser == null) {
                    throw new XmlPullParserException("No android.app.device_admin meta-data");
                }
                Resources res = pm.getResourcesForApplication(activityInfo.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser);
                while (true) {
                    int type = parser.next();
                    i = 1;
                    if (type == 1 || type == 2) {
                        break;
                    }
                }
                String nodeName = parser.getName();
                if (!"device-admin".equals(nodeName)) {
                    throw new XmlPullParserException("Meta-data does not start with device-admin tag");
                }
                TypedArray sa = res.obtainAttributes(attrs, C4057R.styleable.DeviceAdmin);
                this.mVisible = sa.getBoolean(0, true);
                sa.recycle();
                int outerDepth = parser.getDepth();
                while (true) {
                    int type2 = parser.next();
                    if (type2 == i) {
                        break;
                    }
                    int i2 = 3;
                    if (type2 == 3 && parser.getDepth() <= outerDepth) {
                        break;
                    }
                    if (type2 != 3) {
                        int i3 = 4;
                        if (type2 == 4) {
                            z = z3;
                        } else {
                            String tagName = parser.getName();
                            if (tagName.equals("uses-policies")) {
                                int innerDepth = parser.getDepth();
                                while (true) {
                                    int type3 = parser.next();
                                    if (type3 == i || (type3 == i2 && parser.getDepth() <= innerDepth)) {
                                        break;
                                    }
                                    if (type3 != i2 && type3 != i3) {
                                        String policyName = parser.getName();
                                        Integer val = sKnownPolicies.get(policyName);
                                        if (val != null) {
                                            this.mUsesPolicies |= i << val.intValue();
                                        } else {
                                            Log.m104w(TAG, "Unknown tag under uses-policies of " + getComponent() + ": " + policyName);
                                        }
                                    }
                                    i = 1;
                                    i2 = 3;
                                    i3 = 4;
                                }
                                z2 = false;
                                i = 1;
                            } else if (tagName.equals("support-transfer-ownership")) {
                                if (parser.next() == 3) {
                                    this.mSupportsTransferOwnership = true;
                                    z2 = false;
                                    i = 1;
                                } else {
                                    throw new XmlPullParserException("support-transfer-ownership tag must be empty.");
                                }
                            } else if (!tagName.equals("headless-system-user")) {
                                z2 = false;
                                i = 1;
                            } else {
                                String deviceOwnerModeStringValue = parser.getAttributeValue(null, "device-owner-mode");
                                if (deviceOwnerModeStringValue.equalsIgnoreCase("unsupported")) {
                                    z2 = false;
                                    this.mHeadlessDeviceOwnerMode = 0;
                                    i = 1;
                                } else {
                                    z2 = false;
                                    if (deviceOwnerModeStringValue.equalsIgnoreCase("affiliated")) {
                                        i = 1;
                                        this.mHeadlessDeviceOwnerMode = 1;
                                    } else {
                                        throw new XmlPullParserException("headless-system-user mode must be valid");
                                    }
                                }
                            }
                            z3 = z2;
                        }
                    } else {
                        z = z3;
                    }
                    z3 = z;
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + this.mActivityInfo.packageName);
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    DeviceAdminInfo(Parcel source) {
        this.mHeadlessDeviceOwnerMode = 0;
        this.mActivityInfo = ActivityInfo.CREATOR.createFromParcel(source);
        this.mUsesPolicies = source.readInt();
        this.mSupportsTransferOwnership = source.readBoolean();
        this.mHeadlessDeviceOwnerMode = source.readInt();
    }

    public String getPackageName() {
        return this.mActivityInfo.packageName;
    }

    public String getReceiverName() {
        return this.mActivityInfo.name;
    }

    public ActivityInfo getActivityInfo() {
        return this.mActivityInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mActivityInfo.packageName, this.mActivityInfo.name);
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mActivityInfo.loadLabel(pm);
    }

    public CharSequence loadDescription(PackageManager pm) throws Resources.NotFoundException {
        if (this.mActivityInfo.descriptionRes != 0) {
            return pm.getText(this.mActivityInfo.packageName, this.mActivityInfo.descriptionRes, this.mActivityInfo.applicationInfo);
        }
        throw new Resources.NotFoundException();
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mActivityInfo.loadIcon(pm);
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public boolean usesPolicy(int policyIdent) {
        return (this.mUsesPolicies & (1 << policyIdent)) != 0;
    }

    public String getTagForPolicy(int policyIdent) {
        return sRevKnownPolicies.get(policyIdent).tag;
    }

    public boolean supportsTransferOwnership() {
        return this.mSupportsTransferOwnership;
    }

    public int getHeadlessDeviceOwnerMode() {
        return this.mHeadlessDeviceOwnerMode;
    }

    public ArrayList<PolicyInfo> getUsedPolicies() {
        ArrayList<PolicyInfo> res = new ArrayList<>();
        for (int i = 0; i < sPoliciesDisplayOrder.size(); i++) {
            PolicyInfo pi = sPoliciesDisplayOrder.get(i);
            if (usesPolicy(pi.ident)) {
                res.add(pi);
            }
        }
        return res;
    }

    public void writePoliciesToXml(TypedXmlSerializer out) throws IllegalArgumentException, IllegalStateException, IOException {
        out.attributeInt(null, "flags", this.mUsesPolicies);
    }

    public void readPoliciesFromXml(TypedXmlPullParser parser) throws XmlPullParserException, IOException {
        this.mUsesPolicies = parser.getAttributeInt(null, "flags");
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + "Receiver:");
        this.mActivityInfo.dump(pw, prefix + "  ");
    }

    public String toString() {
        return "DeviceAdminInfo{" + this.mActivityInfo.name + "}";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mActivityInfo.writeToParcel(dest, flags);
        dest.writeInt(this.mUsesPolicies);
        dest.writeBoolean(this.mSupportsTransferOwnership);
        dest.writeInt(this.mHeadlessDeviceOwnerMode);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
