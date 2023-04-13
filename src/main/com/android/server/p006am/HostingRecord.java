package com.android.server.p006am;

import android.content.ComponentName;
import android.net.INetd;
/* renamed from: com.android.server.am.HostingRecord */
/* loaded from: classes.dex */
public final class HostingRecord {
    public final String mAction;
    public final String mDefiningPackageName;
    public final String mDefiningProcessName;
    public final int mDefiningUid;
    public final String mHostingName;
    public final String mHostingType;
    public final int mHostingZygote;
    public final boolean mIsTopApp;
    public final String mTriggerType;

    public HostingRecord(String str) {
        this(str, null, 0, null, -1, false, null, null, "unknown");
    }

    public HostingRecord(String str, ComponentName componentName) {
        this(str, componentName, 0);
    }

    public HostingRecord(String str, ComponentName componentName, String str2, String str3) {
        this(str, componentName.toShortString(), 0, null, -1, false, null, str2, str3);
    }

    public HostingRecord(String str, ComponentName componentName, String str2, int i, String str3, String str4) {
        this(str, componentName.toShortString(), 0, str2, i, false, str3, null, str4);
    }

    public HostingRecord(String str, ComponentName componentName, boolean z) {
        this(str, componentName.toShortString(), 0, null, -1, z, null, null, "unknown");
    }

    public HostingRecord(String str, String str2) {
        this(str, str2, 0);
    }

    public HostingRecord(String str, ComponentName componentName, int i) {
        this(str, componentName.toShortString(), i);
    }

    public HostingRecord(String str, String str2, int i) {
        this(str, str2, i, null, -1, false, null, null, "unknown");
    }

    public HostingRecord(String str, String str2, int i, String str3, int i2, boolean z, String str4, String str5, String str6) {
        this.mHostingType = str;
        this.mHostingName = str2;
        this.mHostingZygote = i;
        this.mDefiningPackageName = str3;
        this.mDefiningUid = i2;
        this.mIsTopApp = z;
        this.mDefiningProcessName = str4;
        this.mAction = str5;
        this.mTriggerType = str6;
    }

    public String getType() {
        return this.mHostingType;
    }

    public String getName() {
        return this.mHostingName;
    }

    public boolean isTopApp() {
        return this.mIsTopApp;
    }

    public int getDefiningUid() {
        return this.mDefiningUid;
    }

    public String getDefiningPackageName() {
        return this.mDefiningPackageName;
    }

    public String getDefiningProcessName() {
        return this.mDefiningProcessName;
    }

    public String getAction() {
        return this.mAction;
    }

    public String getTriggerType() {
        return this.mTriggerType;
    }

    public static HostingRecord byWebviewZygote(ComponentName componentName, String str, int i, String str2) {
        return new HostingRecord("", componentName.toShortString(), 1, str, i, false, str2, null, "unknown");
    }

    public static HostingRecord byAppZygote(ComponentName componentName, String str, int i, String str2) {
        return new HostingRecord("", componentName.toShortString(), 2, str, i, false, str2, null, "unknown");
    }

    public boolean usesAppZygote() {
        return this.mHostingZygote == 2;
    }

    public boolean usesWebviewZygote() {
        return this.mHostingZygote == 1;
    }

    public static int getHostingTypeIdStatsd(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1726126969:
                if (str.equals("top-activity")) {
                    c = 0;
                    break;
                }
                break;
            case -1682898044:
                if (str.equals("link fail")) {
                    c = 1;
                    break;
                }
                break;
            case -1655966961:
                if (str.equals("activity")) {
                    c = 2;
                    break;
                }
                break;
            case -1618876223:
                if (str.equals(INetd.IF_FLAG_BROADCAST)) {
                    c = 3;
                    break;
                }
                break;
            case -1526161119:
                if (str.equals("next-top-activity")) {
                    c = 4;
                    break;
                }
                break;
            case -1396673086:
                if (str.equals("backup")) {
                    c = 5;
                    break;
                }
                break;
            case -1372333075:
                if (str.equals("on-hold")) {
                    c = 6;
                    break;
                }
                break;
            case -1355707223:
                if (str.equals("next-activity")) {
                    c = 7;
                    break;
                }
                break;
            case -887328209:
                if (str.equals("system")) {
                    c = '\b';
                    break;
                }
                break;
            case 0:
                if (str.equals("")) {
                    c = '\t';
                    break;
                }
                break;
            case 1097506319:
                if (str.equals("restart")) {
                    c = '\n';
                    break;
                }
                break;
            case 1418439096:
                if (str.equals("content provider")) {
                    c = 11;
                    break;
                }
                break;
            case 1637159472:
                if (str.equals("added application")) {
                    c = '\f';
                    break;
                }
                break;
            case 1984153269:
                if (str.equals("service")) {
                    c = '\r';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 13;
            case 1:
                return 6;
            case 2:
                return 1;
            case 3:
                return 4;
            case 4:
                return 9;
            case 5:
                return 3;
            case 6:
                return 7;
            case 7:
                return 8;
            case '\b':
                return 12;
            case '\t':
                return 14;
            case '\n':
                return 10;
            case 11:
                return 5;
            case '\f':
                return 2;
            case '\r':
                return 11;
            default:
                return 0;
        }
    }

    public static int getTriggerTypeForStatsd(String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2000959542:
                if (str.equals("push_message_over_quota")) {
                    c = 0;
                    break;
                }
                break;
            case 105405:
                if (str.equals("job")) {
                    c = 1;
                    break;
                }
                break;
            case 92895825:
                if (str.equals("alarm")) {
                    c = 2;
                    break;
                }
                break;
            case 679713762:
                if (str.equals("push_message")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                return 3;
            case 1:
                return 4;
            case 2:
                return 1;
            case 3:
                return 2;
            default:
                return 0;
        }
    }
}
