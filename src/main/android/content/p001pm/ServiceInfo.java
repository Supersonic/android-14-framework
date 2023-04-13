package android.content.p001pm;

import android.content.Context;
import android.p008os.BatteryManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Printer;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.content.pm.ServiceInfo */
/* loaded from: classes.dex */
public class ServiceInfo extends ComponentInfo implements Parcelable {
    public static final Parcelable.Creator<ServiceInfo> CREATOR = new Parcelable.Creator<ServiceInfo>() { // from class: android.content.pm.ServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ServiceInfo createFromParcel(Parcel source) {
            return new ServiceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ServiceInfo[] newArray(int size) {
            return new ServiceInfo[size];
        }
    };
    public static final int FLAG_ALLOW_SHARED_ISOLATED_PROCESS = 16;
    public static final int FLAG_EXTERNAL_SERVICE = 4;
    public static final int FLAG_ISOLATED_PROCESS = 2;
    public static final int FLAG_SINGLE_USER = 1073741824;
    public static final int FLAG_STOP_WITH_TASK = 1;
    public static final int FLAG_USE_APP_ZYGOTE = 8;
    public static final int FLAG_VISIBLE_TO_INSTANT_APP = 1048576;
    public static final int FOREGROUND_SERVICE_TYPES_MAX_INDEX = 30;
    public static final int FOREGROUND_SERVICE_TYPE_CAMERA = 64;
    public static final int FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE = 16;
    public static final int FOREGROUND_SERVICE_TYPE_DATA_SYNC = 1;
    public static final int FOREGROUND_SERVICE_TYPE_FILE_MANAGEMENT = 4096;
    public static final int FOREGROUND_SERVICE_TYPE_HEALTH = 256;
    public static final int FOREGROUND_SERVICE_TYPE_LOCATION = 8;
    public static final int FOREGROUND_SERVICE_TYPE_MANIFEST = -1;
    public static final int FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK = 2;
    public static final int FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION = 32;
    public static final int FOREGROUND_SERVICE_TYPE_MICROPHONE = 128;
    @Deprecated
    public static final int FOREGROUND_SERVICE_TYPE_NONE = 0;
    public static final int FOREGROUND_SERVICE_TYPE_PHONE_CALL = 4;
    public static final int FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING = 512;
    public static final int FOREGROUND_SERVICE_TYPE_SHORT_SERVICE = 2048;
    public static final int FOREGROUND_SERVICE_TYPE_SPECIAL_USE = 1073741824;
    public static final int FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED = 1024;
    public int flags;
    public int mForegroundServiceType;
    public String permission;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ServiceInfo$ForegroundServiceType */
    /* loaded from: classes.dex */
    public @interface ForegroundServiceType {
    }

    public ServiceInfo() {
        this.mForegroundServiceType = 0;
    }

    public ServiceInfo(ServiceInfo orig) {
        super(orig);
        this.mForegroundServiceType = 0;
        this.permission = orig.permission;
        this.flags = orig.flags;
        this.mForegroundServiceType = orig.mForegroundServiceType;
    }

    public int getForegroundServiceType() {
        return this.mForegroundServiceType;
    }

    public void dump(Printer pw, String prefix) {
        dump(pw, prefix, 3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(Printer pw, String prefix, int dumpFlags) {
        super.dumpFront(pw, prefix);
        pw.println(prefix + "permission=" + this.permission);
        pw.println(prefix + "flags=0x" + Integer.toHexString(this.flags));
        super.dumpBack(pw, prefix, dumpFlags);
    }

    public String toString() {
        return "ServiceInfo{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.name + "}";
    }

    public static String foregroundServiceTypeToLabel(int type) {
        switch (type) {
            case -1:
                return PackageParser.TAG_MANIFEST;
            case 0:
                return "none";
            case 1:
                return "dataSync";
            case 2:
                return "mediaPlayback";
            case 4:
                return "phoneCall";
            case 8:
                return "location";
            case 16:
                return "connectedDevice";
            case 32:
                return "mediaProjection";
            case 64:
                return Context.CAMERA_SERVICE;
            case 128:
                return "microphone";
            case 256:
                return BatteryManager.EXTRA_HEALTH;
            case 512:
                return "remoteMessaging";
            case 1024:
                return "systemExempted";
            case 2048:
                return "shortService";
            case 4096:
                return "fileManagement";
            case 1073741824:
                return "specialUse";
            default:
                return "unknown";
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.content.p001pm.ComponentInfo, android.content.p001pm.PackageItemInfo, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        super.writeToParcel(dest, parcelableFlags);
        dest.writeString8(this.permission);
        dest.writeInt(this.flags);
        dest.writeInt(this.mForegroundServiceType);
    }

    private ServiceInfo(Parcel source) {
        super(source);
        this.mForegroundServiceType = 0;
        this.permission = source.readString8();
        this.flags = source.readInt();
        this.mForegroundServiceType = source.readInt();
    }
}
