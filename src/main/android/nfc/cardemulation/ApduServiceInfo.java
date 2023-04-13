package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.C4057R;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public final class ApduServiceInfo implements Parcelable {
    public static final Parcelable.Creator<ApduServiceInfo> CREATOR = new Parcelable.Creator<ApduServiceInfo>() { // from class: android.nfc.cardemulation.ApduServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApduServiceInfo createFromParcel(Parcel source) {
            ResolveInfo info = ResolveInfo.CREATOR.createFromParcel(source);
            String description = source.readString();
            boolean onHost = source.readInt() != 0;
            String offHostName = source.readString();
            String staticOffHostName = source.readString();
            ArrayList<AidGroup> staticAidGroups = new ArrayList<>();
            int numStaticGroups = source.readInt();
            if (numStaticGroups > 0) {
                source.readTypedList(staticAidGroups, AidGroup.CREATOR);
            }
            ArrayList<AidGroup> dynamicAidGroups = new ArrayList<>();
            int numDynamicGroups = source.readInt();
            if (numDynamicGroups > 0) {
                source.readTypedList(dynamicAidGroups, AidGroup.CREATOR);
            }
            boolean requiresUnlock = source.readInt() != 0;
            boolean requiresScreenOn = source.readInt() != 0;
            int bannerResource = source.readInt();
            int uid = source.readInt();
            String settingsActivityName = source.readString();
            return new ApduServiceInfo(info, onHost, description, staticAidGroups, dynamicAidGroups, requiresUnlock, requiresScreenOn, bannerResource, uid, settingsActivityName, offHostName, staticOffHostName);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ApduServiceInfo[] newArray(int size) {
            return new ApduServiceInfo[size];
        }
    };
    static final String TAG = "ApduServiceInfo";
    final int mBannerResourceId;
    final String mDescription;
    final HashMap<String, AidGroup> mDynamicAidGroups;
    String mOffHostName;
    final boolean mOnHost;
    final boolean mRequiresDeviceScreenOn;
    final boolean mRequiresDeviceUnlock;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    final HashMap<String, AidGroup> mStaticAidGroups;
    final String mStaticOffHostName;
    final int mUid;

    public ApduServiceInfo(ResolveInfo info, boolean onHost, String description, ArrayList<AidGroup> staticAidGroups, ArrayList<AidGroup> dynamicAidGroups, boolean requiresUnlock, int bannerResource, int uid, String settingsActivityName, String offHost, String staticOffHost) {
        this(info, onHost, description, staticAidGroups, dynamicAidGroups, requiresUnlock, onHost, bannerResource, uid, settingsActivityName, offHost, staticOffHost);
    }

    public ApduServiceInfo(ResolveInfo info, boolean onHost, String description, ArrayList<AidGroup> staticAidGroups, ArrayList<AidGroup> dynamicAidGroups, boolean requiresUnlock, boolean requiresScreenOn, int bannerResource, int uid, String settingsActivityName, String offHost, String staticOffHost) {
        this.mService = info;
        this.mDescription = description;
        this.mStaticAidGroups = new HashMap<>();
        this.mDynamicAidGroups = new HashMap<>();
        this.mOffHostName = offHost;
        this.mStaticOffHostName = staticOffHost;
        this.mOnHost = onHost;
        this.mRequiresDeviceUnlock = requiresUnlock;
        this.mRequiresDeviceScreenOn = requiresScreenOn;
        Iterator<AidGroup> it = staticAidGroups.iterator();
        while (it.hasNext()) {
            AidGroup aidGroup = it.next();
            this.mStaticAidGroups.put(aidGroup.category, aidGroup);
        }
        Iterator<AidGroup> it2 = dynamicAidGroups.iterator();
        while (it2.hasNext()) {
            AidGroup aidGroup2 = it2.next();
            this.mDynamicAidGroups.put(aidGroup2.category, aidGroup2);
        }
        this.mBannerResourceId = bannerResource;
        this.mUid = uid;
        this.mSettingsActivityName = settingsActivityName;
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x005a, code lost:
        if ("offhost-apdu-service".equals(r9) == false) goto L25;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0064, code lost:
        throw new org.xmlpull.v1.XmlPullParserException("Meta-data does not start with <offhost-apdu-service> tag");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public ApduServiceInfo(PackageManager pm, ResolveInfo info, boolean onHost) throws XmlPullParserException, IOException {
        XmlResourceParser parser;
        int i;
        int i2;
        String groupCategory;
        ServiceInfo si = info.serviceInfo;
        XmlResourceParser parser2 = null;
        try {
            try {
                if (onHost) {
                    parser = si.loadXmlMetaData(pm, HostApduService.SERVICE_META_DATA);
                    if (parser == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.host_apdu_service meta-data");
                    }
                } else {
                    parser = si.loadXmlMetaData(pm, OffHostApduService.SERVICE_META_DATA);
                    if (parser == null) {
                        throw new XmlPullParserException("No android.nfc.cardemulation.off_host_apdu_service meta-data");
                    }
                }
                int eventType = parser2.getEventType();
                while (true) {
                    i = 1;
                    i2 = 2;
                    if (eventType == 2 || eventType == 1) {
                        break;
                    }
                    eventType = parser2.next();
                }
                String tagName = parser2.getName();
                if (onHost && !"host-apdu-service".equals(tagName)) {
                    throw new XmlPullParserException("Meta-data does not start with <host-apdu-service> tag");
                }
                Resources res = pm.getResourcesForApplication(si.applicationInfo);
                AttributeSet attrs = Xml.asAttributeSet(parser2);
                if (onHost) {
                    TypedArray sa = res.obtainAttributes(attrs, C4057R.styleable.HostApduService);
                    this.mService = info;
                    this.mDescription = sa.getString(0);
                    this.mRequiresDeviceUnlock = sa.getBoolean(2, false);
                    this.mRequiresDeviceScreenOn = sa.getBoolean(4, true);
                    this.mBannerResourceId = sa.getResourceId(3, -1);
                    this.mSettingsActivityName = sa.getString(1);
                    this.mOffHostName = null;
                    this.mStaticOffHostName = null;
                    sa.recycle();
                } else {
                    TypedArray sa2 = res.obtainAttributes(attrs, C4057R.styleable.OffHostApduService);
                    this.mService = info;
                    this.mDescription = sa2.getString(0);
                    this.mRequiresDeviceUnlock = sa2.getBoolean(2, false);
                    this.mRequiresDeviceScreenOn = sa2.getBoolean(5, false);
                    this.mBannerResourceId = sa2.getResourceId(3, -1);
                    this.mSettingsActivityName = sa2.getString(1);
                    String string = sa2.getString(4);
                    this.mOffHostName = string;
                    if (string != null) {
                        if (string.equals("eSE")) {
                            this.mOffHostName = "eSE1";
                        } else if (this.mOffHostName.equals("SIM")) {
                            this.mOffHostName = "SIM1";
                        }
                    }
                    this.mStaticOffHostName = this.mOffHostName;
                    sa2.recycle();
                }
                this.mStaticAidGroups = new HashMap<>();
                this.mDynamicAidGroups = new HashMap<>();
                this.mOnHost = onHost;
                int depth = parser2.getDepth();
                AidGroup currentGroup = null;
                while (true) {
                    int eventType2 = parser2.next();
                    if ((eventType2 != 3 || parser2.getDepth() > depth) && eventType2 != i) {
                        String tagName2 = parser2.getName();
                        if (eventType2 != i2 || !"aid-group".equals(tagName2) || currentGroup != null) {
                            if (eventType2 == 3 && "aid-group".equals(tagName2) && currentGroup != null) {
                                if (currentGroup.aids.size() > 0) {
                                    if (!this.mStaticAidGroups.containsKey(currentGroup.category)) {
                                        this.mStaticAidGroups.put(currentGroup.category, currentGroup);
                                    }
                                } else {
                                    Log.m110e(TAG, "Not adding <aid-group> with empty or invalid AIDs");
                                }
                                currentGroup = null;
                                i = 1;
                                i2 = 2;
                            } else {
                                if (eventType2 == 2 && "aid-filter".equals(tagName2) && currentGroup != null) {
                                    TypedArray a = res.obtainAttributes(attrs, C4057R.styleable.AidFilter);
                                    String aid = a.getString(0).toUpperCase();
                                    if (!CardEmulation.isValidAid(aid) || currentGroup.aids.contains(aid)) {
                                        Log.m110e(TAG, "Ignoring invalid or duplicate aid: " + aid);
                                    } else {
                                        currentGroup.aids.add(aid);
                                    }
                                    a.recycle();
                                } else if (eventType2 == 2 && "aid-prefix-filter".equals(tagName2) && currentGroup != null) {
                                    TypedArray a2 = res.obtainAttributes(attrs, C4057R.styleable.AidFilter);
                                    String aid2 = a2.getString(0).toUpperCase().concat("*");
                                    if (!CardEmulation.isValidAid(aid2) || currentGroup.aids.contains(aid2)) {
                                        Log.m110e(TAG, "Ignoring invalid or duplicate aid: " + aid2);
                                    } else {
                                        currentGroup.aids.add(aid2);
                                    }
                                    a2.recycle();
                                } else if (eventType2 == 2) {
                                    if (tagName2.equals("aid-suffix-filter") && currentGroup != null) {
                                        TypedArray a3 = res.obtainAttributes(attrs, C4057R.styleable.AidFilter);
                                        String aid3 = a3.getString(0).toUpperCase().concat("#");
                                        if (!CardEmulation.isValidAid(aid3) || currentGroup.aids.contains(aid3)) {
                                            Log.m110e(TAG, "Ignoring invalid or duplicate aid: " + aid3);
                                        } else {
                                            currentGroup.aids.add(aid3);
                                        }
                                        a3.recycle();
                                    }
                                }
                                i = 1;
                                i2 = 2;
                            }
                        } else {
                            TypedArray groupAttrs = res.obtainAttributes(attrs, C4057R.styleable.AidGroup);
                            String groupCategory2 = groupAttrs.getString(i);
                            String groupDescription = groupAttrs.getString(0);
                            if (CardEmulation.CATEGORY_PAYMENT.equals(groupCategory2)) {
                                groupCategory = groupCategory2;
                            } else {
                                groupCategory = "other";
                            }
                            AidGroup currentGroup2 = this.mStaticAidGroups.get(groupCategory);
                            if (currentGroup2 != null) {
                                if ("other".equals(groupCategory)) {
                                    currentGroup = currentGroup2;
                                } else {
                                    Log.m110e(TAG, "Not allowing multiple aid-groups in the " + groupCategory + " category");
                                    currentGroup = null;
                                }
                            } else {
                                AidGroup currentGroup3 = new AidGroup(groupCategory, groupDescription);
                                currentGroup = currentGroup3;
                            }
                            groupAttrs.recycle();
                            i = 1;
                            i2 = 2;
                        }
                    }
                }
                this.mUid = si.applicationInfo.uid;
            } catch (PackageManager.NameNotFoundException e) {
                throw new XmlPullParserException("Unable to create context for: " + si.packageName);
            }
        } finally {
            if (parser2 != null) {
                parser2.close();
            }
        }
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public String getOffHostSecureElement() {
        return this.mOffHostName;
    }

    public List<String> getAids() {
        ArrayList<String> aids = new ArrayList<>();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            AidGroup group = it.next();
            aids.addAll(group.aids);
        }
        return aids;
    }

    public List<String> getPrefixAids() {
        ArrayList<String> prefixAids = new ArrayList<>();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            AidGroup group = it.next();
            for (String aid : group.aids) {
                if (aid.endsWith("*")) {
                    prefixAids.add(aid);
                }
            }
        }
        return prefixAids;
    }

    public List<String> getSubsetAids() {
        ArrayList<String> subsetAids = new ArrayList<>();
        Iterator<AidGroup> it = getAidGroups().iterator();
        while (it.hasNext()) {
            AidGroup group = it.next();
            for (String aid : group.aids) {
                if (aid.endsWith("#")) {
                    subsetAids.add(aid);
                }
            }
        }
        return subsetAids;
    }

    public AidGroup getDynamicAidGroupForCategory(String category) {
        return this.mDynamicAidGroups.get(category);
    }

    public boolean removeDynamicAidGroupForCategory(String category) {
        return this.mDynamicAidGroups.remove(category) != null;
    }

    public ArrayList<AidGroup> getAidGroups() {
        ArrayList<AidGroup> groups = new ArrayList<>();
        for (Map.Entry<String, AidGroup> entry : this.mDynamicAidGroups.entrySet()) {
            groups.add(entry.getValue());
        }
        for (Map.Entry<String, AidGroup> entry2 : this.mStaticAidGroups.entrySet()) {
            if (!this.mDynamicAidGroups.containsKey(entry2.getKey())) {
                groups.add(entry2.getValue());
            }
        }
        return groups;
    }

    public String getCategoryForAid(String aid) {
        ArrayList<AidGroup> groups = getAidGroups();
        Iterator<AidGroup> it = groups.iterator();
        while (it.hasNext()) {
            AidGroup group = it.next();
            if (group.aids.contains(aid.toUpperCase())) {
                return group.category;
            }
        }
        return null;
    }

    public boolean hasCategory(String category) {
        return this.mStaticAidGroups.containsKey(category) || this.mDynamicAidGroups.containsKey(category);
    }

    public boolean isOnHost() {
        return this.mOnHost;
    }

    public boolean requiresUnlock() {
        return this.mRequiresDeviceUnlock;
    }

    public boolean requiresScreenOn() {
        return this.mRequiresDeviceScreenOn;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public int getUid() {
        return this.mUid;
    }

    public void setOrReplaceDynamicAidGroup(AidGroup aidGroup) {
        this.mDynamicAidGroups.put(aidGroup.getCategory(), aidGroup);
    }

    public void setOffHostSecureElement(String offHost) {
        this.mOffHostName = offHost;
    }

    public void unsetOffHostSecureElement() {
        this.mOffHostName = this.mStaticOffHostName;
    }

    public CharSequence loadLabel(PackageManager pm) {
        return this.mService.loadLabel(pm);
    }

    public CharSequence loadAppLabel(PackageManager pm) {
        try {
            return pm.getApplicationLabel(pm.getApplicationInfo(this.mService.resolvePackageName, 128));
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public Drawable loadIcon(PackageManager pm) {
        return this.mService.loadIcon(pm);
    }

    public Drawable loadBanner(PackageManager pm) {
        try {
            Resources res = pm.getResourcesForApplication(this.mService.serviceInfo.packageName);
            Drawable banner = res.getDrawable(this.mBannerResourceId);
            return banner;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, "Could not load banner.");
            return null;
        } catch (Resources.NotFoundException e2) {
            Log.m110e(TAG, "Could not load banner.");
            return null;
        }
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public String toString() {
        StringBuilder out = new StringBuilder("ApduService: ");
        out.append(getComponent());
        out.append(", UID: " + this.mUid);
        out.append(", description: " + this.mDescription);
        out.append(", Static AID Groups: ");
        for (AidGroup aidGroup : this.mStaticAidGroups.values()) {
            out.append(aidGroup.toString());
        }
        out.append(", Dynamic AID Groups: ");
        for (AidGroup aidGroup2 : this.mDynamicAidGroups.values()) {
            out.append(aidGroup2.toString());
        }
        return out.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ApduServiceInfo) {
            ApduServiceInfo thatService = (ApduServiceInfo) o;
            return thatService.getComponent().equals(getComponent()) && thatService.getUid() == getUid();
        }
        return false;
    }

    public int hashCode() {
        return getComponent().hashCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mService.writeToParcel(dest, flags);
        dest.writeString(this.mDescription);
        dest.writeInt(this.mOnHost ? 1 : 0);
        dest.writeString(this.mOffHostName);
        dest.writeString(this.mStaticOffHostName);
        dest.writeInt(this.mStaticAidGroups.size());
        if (this.mStaticAidGroups.size() > 0) {
            dest.writeTypedList(new ArrayList(this.mStaticAidGroups.values()));
        }
        dest.writeInt(this.mDynamicAidGroups.size());
        if (this.mDynamicAidGroups.size() > 0) {
            dest.writeTypedList(new ArrayList(this.mDynamicAidGroups.values()));
        }
        dest.writeInt(this.mRequiresDeviceUnlock ? 1 : 0);
        dest.writeInt(this.mRequiresDeviceScreenOn ? 1 : 0);
        dest.writeInt(this.mBannerResourceId);
        dest.writeInt(this.mUid);
        dest.writeString(this.mSettingsActivityName);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("    " + getComponent() + " (Description: " + getDescription() + ") (UID: " + getUid() + NavigationBarInflaterView.KEY_CODE_END);
        if (this.mOnHost) {
            pw.println("    On Host Service");
        } else {
            pw.println("    Off-host Service");
            pw.println("        Current off-host SE:" + this.mOffHostName + " static off-host SE:" + this.mStaticOffHostName);
        }
        pw.println("    Static AID groups:");
        for (AidGroup group : this.mStaticAidGroups.values()) {
            pw.println("        Category: " + group.category);
            for (String aid : group.aids) {
                pw.println("            AID: " + aid);
            }
        }
        pw.println("    Dynamic AID groups:");
        for (AidGroup group2 : this.mDynamicAidGroups.values()) {
            pw.println("        Category: " + group2.category);
            for (String aid2 : group2.aids) {
                pw.println("            AID: " + aid2);
            }
        }
        pw.println("    Settings Activity: " + this.mSettingsActivityName);
        pw.println("    Requires Device Unlock: " + this.mRequiresDeviceUnlock);
        pw.println("    Requires Device ScreenOn: " + this.mRequiresDeviceScreenOn);
    }

    public void dumpDebug(ProtoOutputStream proto) {
        Utils.dumpDebugComponentName(getComponent(), proto, 1146756268033L);
        proto.write(1138166333442L, getDescription());
        proto.write(1133871366147L, this.mOnHost);
        if (!this.mOnHost) {
            proto.write(1138166333444L, this.mOffHostName);
            proto.write(1138166333445L, this.mStaticOffHostName);
        }
        for (AidGroup group : this.mStaticAidGroups.values()) {
            long token = proto.start(2246267895814L);
            group.dump(proto);
            proto.end(token);
        }
        for (AidGroup group2 : this.mDynamicAidGroups.values()) {
            long token2 = proto.start(2246267895814L);
            group2.dump(proto);
            proto.end(token2);
        }
        proto.write(1138166333448L, this.mSettingsActivityName);
    }
}
