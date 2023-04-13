package com.android.server.media;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.IMediaResourceMonitor;
import android.os.Binder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.server.SystemService;
import java.util.List;
/* loaded from: classes2.dex */
public class MediaResourceMonitorService extends SystemService {
    public static final boolean DEBUG = Log.isLoggable("MediaResourceMonitor", 3);
    public final MediaResourceMonitorImpl mMediaResourceMonitorImpl;

    public MediaResourceMonitorService(Context context) {
        super(context);
        this.mMediaResourceMonitorImpl = new MediaResourceMonitorImpl();
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("media_resource_monitor", this.mMediaResourceMonitorImpl);
    }

    /* loaded from: classes2.dex */
    public class MediaResourceMonitorImpl extends IMediaResourceMonitor.Stub {
        public MediaResourceMonitorImpl() {
        }

        public void notifyResourceGranted(int i, int i2) throws RemoteException {
            if (MediaResourceMonitorService.DEBUG) {
                Log.d("MediaResourceMonitor", "notifyResourceGranted(pid=" + i + ", type=" + i2 + ")");
            }
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                String[] packageNamesFromPid = getPackageNamesFromPid(i);
                if (packageNamesFromPid == null) {
                    return;
                }
                List<UserHandle> enabledProfiles = ((UserManager) MediaResourceMonitorService.this.getContext().createContextAsUser(UserHandle.of(ActivityManager.getCurrentUser()), 0).getSystemService(UserManager.class)).getEnabledProfiles();
                if (enabledProfiles.isEmpty()) {
                    return;
                }
                Intent intent = new Intent("android.intent.action.MEDIA_RESOURCE_GRANTED");
                intent.putExtra("android.intent.extra.PACKAGES", packageNamesFromPid);
                intent.putExtra("android.intent.extra.MEDIA_RESOURCE_TYPE", i2);
                for (UserHandle userHandle : enabledProfiles) {
                    MediaResourceMonitorService.this.getContext().sendBroadcastAsUser(intent, userHandle, "android.permission.RECEIVE_MEDIA_RESOURCE_USAGE");
                }
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }

        public final String[] getPackageNamesFromPid(int i) {
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) MediaResourceMonitorService.this.getContext().getSystemService(ActivityManager.class)).getRunningAppProcesses()) {
                if (runningAppProcessInfo.pid == i) {
                    return runningAppProcessInfo.pkgList;
                }
            }
            return null;
        }
    }
}
