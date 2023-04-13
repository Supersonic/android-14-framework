package android.app;

import android.content.ComponentName;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class ForegroundServiceDelegationOptions {
    public static final int DELEGATION_SERVICE_CAMERA = 7;
    public static final int DELEGATION_SERVICE_CONNECTED_DEVICE = 5;
    public static final int DELEGATION_SERVICE_DATA_SYNC = 1;
    public static final int DELEGATION_SERVICE_DEFAULT = 0;
    public static final int DELEGATION_SERVICE_HEALTH = 9;
    public static final int DELEGATION_SERVICE_LOCATION = 4;
    public static final int DELEGATION_SERVICE_MEDIA_PLAYBACK = 2;
    public static final int DELEGATION_SERVICE_MEDIA_PROJECTION = 6;
    public static final int DELEGATION_SERVICE_MICROPHONE = 8;
    public static final int DELEGATION_SERVICE_PHONE_CALL = 3;
    public static final int DELEGATION_SERVICE_REMOTE_MESSAGING = 10;
    public static final int DELEGATION_SERVICE_SPECIAL_USE = 12;
    public static final int DELEGATION_SERVICE_SYSTEM_EXEMPTED = 11;
    public final IApplicationThread mClientAppThread;
    public String mClientInstanceName;
    public final String mClientPackageName;
    public final int mClientPid;
    public final int mClientUid;
    public final int mDelegationService;
    public final int mForegroundServiceTypes;
    public final boolean mSticky;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DelegationService {
    }

    public ForegroundServiceDelegationOptions(int clientPid, int clientUid, String clientPackageName, IApplicationThread clientAppThread, boolean isSticky, String clientInstanceName, int foregroundServiceTypes, int delegationService) {
        this.mClientPid = clientPid;
        this.mClientUid = clientUid;
        this.mClientPackageName = clientPackageName;
        this.mClientAppThread = clientAppThread;
        this.mSticky = isSticky;
        this.mClientInstanceName = clientInstanceName;
        this.mForegroundServiceTypes = foregroundServiceTypes;
        this.mDelegationService = delegationService;
    }

    public boolean isSameDelegate(ForegroundServiceDelegationOptions that) {
        return this.mDelegationService == that.mDelegationService && this.mClientUid == that.mClientUid && this.mClientPid == that.mClientPid && this.mClientInstanceName.equals(that.mClientInstanceName);
    }

    public ComponentName getComponentName() {
        return new ComponentName(this.mClientPackageName, serviceCodeToString(this.mDelegationService) + ":" + this.mClientInstanceName);
    }

    public String getDescription() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ForegroundServiceDelegate{").append("package:").append(this.mClientPackageName).append(",").append("service:").append(serviceCodeToString(this.mDelegationService)).append(",").append("uid:").append(this.mClientUid).append(",").append("pid:").append(this.mClientPid).append(",").append("instance:").append(this.mClientInstanceName).append("}");
        return sb.toString();
    }

    public static String serviceCodeToString(int serviceCode) {
        switch (serviceCode) {
            case 0:
                return "DEFAULT";
            case 1:
                return "DATA_SYNC";
            case 2:
                return "MEDIA_PLAYBACK";
            case 3:
                return "PHONE_CALL";
            case 4:
                return "LOCATION";
            case 5:
                return "CONNECTED_DEVICE";
            case 6:
                return "MEDIA_PROJECTION";
            case 7:
                return "CAMERA";
            case 8:
                return "MICROPHONE";
            case 9:
                return "HEALTH";
            case 10:
                return "REMOTE_MESSAGING";
            case 11:
                return "SYSTEM_EXEMPTED";
            case 12:
                return "SPECIAL_USE";
            default:
                return "(unknown:" + serviceCode + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    /* loaded from: classes.dex */
    public static class Builder {
        IApplicationThread mClientAppThread;
        String mClientInstanceName;
        int mClientNotificationId;
        String mClientPackageName;
        int mClientPid;
        int mClientUid;
        int mDelegationService;
        int mForegroundServiceTypes;
        boolean mSticky;

        public Builder setClientPid(int clientPid) {
            this.mClientPid = clientPid;
            return this;
        }

        public Builder setClientUid(int clientUid) {
            this.mClientUid = clientUid;
            return this;
        }

        public Builder setClientPackageName(String clientPackageName) {
            this.mClientPackageName = clientPackageName;
            return this;
        }

        public Builder setClientNotificationId(int clientNotificationId) {
            this.mClientNotificationId = clientNotificationId;
            return this;
        }

        public Builder setClientAppThread(IApplicationThread clientAppThread) {
            this.mClientAppThread = clientAppThread;
            return this;
        }

        public Builder setClientInstanceName(String clientInstanceName) {
            this.mClientInstanceName = clientInstanceName;
            return this;
        }

        public Builder setSticky(boolean isSticky) {
            this.mSticky = isSticky;
            return this;
        }

        public Builder setForegroundServiceTypes(int foregroundServiceTypes) {
            this.mForegroundServiceTypes = foregroundServiceTypes;
            return this;
        }

        public Builder setDelegationService(int delegationService) {
            this.mDelegationService = delegationService;
            return this;
        }

        public ForegroundServiceDelegationOptions build() {
            return new ForegroundServiceDelegationOptions(this.mClientPid, this.mClientUid, this.mClientPackageName, this.mClientAppThread, this.mSticky, this.mClientInstanceName, this.mForegroundServiceTypes, this.mDelegationService);
        }
    }
}
