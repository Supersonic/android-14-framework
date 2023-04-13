package com.android.server.musicrecognition;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.musicrecognition.IMusicRecognitionManager;
import android.media.musicrecognition.IMusicRecognitionManagerCallback;
import android.media.musicrecognition.RecognitionRequest;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import java.io.FileDescriptor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes2.dex */
public class MusicRecognitionManagerService extends AbstractMasterSystemService<MusicRecognitionManagerService, MusicRecognitionManagerPerUserService> {
    public static final String TAG = "MusicRecognitionManagerService";
    public final ExecutorService mExecutorService;
    public MusicRecognitionManagerStub mMusicRecognitionManagerStub;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 60000;
    }

    public MusicRecognitionManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039888), null);
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public MusicRecognitionManagerPerUserService newServiceLocked(int i, boolean z) {
        return new MusicRecognitionManagerPerUserService(this, this.mLock, i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [android.os.IBinder, com.android.server.musicrecognition.MusicRecognitionManagerService$MusicRecognitionManagerStub] */
    @Override // com.android.server.SystemService
    public void onStart() {
        ?? musicRecognitionManagerStub = new MusicRecognitionManagerStub();
        this.mMusicRecognitionManagerStub = musicRecognitionManagerStub;
        publishBinderService("music_recognition", musicRecognitionManagerStub);
    }

    public final void enforceCaller(String str) {
        if (getContext().checkCallingPermission("android.permission.MANAGE_MUSIC_RECOGNITION") == 0) {
            return;
        }
        throw new SecurityException("Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " doesn't hold android.permission.MANAGE_MUSIC_RECOGNITION");
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_MUSIC_RECOGNITION", TAG);
    }

    /* loaded from: classes2.dex */
    public final class MusicRecognitionManagerStub extends IMusicRecognitionManager.Stub {
        public MusicRecognitionManagerStub() {
        }

        public void beginRecognition(RecognitionRequest recognitionRequest, IBinder iBinder) {
            MusicRecognitionManagerService.this.enforceCaller("beginRecognition");
            synchronized (MusicRecognitionManagerService.this.mLock) {
                int callingUserId = UserHandle.getCallingUserId();
                MusicRecognitionManagerPerUserService musicRecognitionManagerPerUserService = (MusicRecognitionManagerPerUserService) MusicRecognitionManagerService.this.getServiceForUserLocked(callingUserId);
                if (musicRecognitionManagerPerUserService != null && (isDefaultServiceLocked(callingUserId) || isCalledByServiceAppLocked("beginRecognition"))) {
                    musicRecognitionManagerPerUserService.beginRecognitionLocked(recognitionRequest, iBinder);
                } else {
                    try {
                        IMusicRecognitionManagerCallback.Stub.asInterface(iBinder).onRecognitionFailed(3);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) throws RemoteException {
            new MusicRecognitionManagerServiceShellCommand(MusicRecognitionManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        @GuardedBy({"mLock"})
        public final boolean isDefaultServiceLocked(int i) {
            String defaultServiceName = MusicRecognitionManagerService.this.mServiceNameResolver.getDefaultServiceName(i);
            if (defaultServiceName == null) {
                return false;
            }
            return defaultServiceName.equals(MusicRecognitionManagerService.this.mServiceNameResolver.getServiceName(i));
        }

        @GuardedBy({"mLock"})
        public final boolean isCalledByServiceAppLocked(String str) {
            int callingUserId = UserHandle.getCallingUserId();
            int callingUid = Binder.getCallingUid();
            String serviceName = MusicRecognitionManagerService.this.mServiceNameResolver.getServiceName(callingUserId);
            if (serviceName == null) {
                String str2 = MusicRecognitionManagerService.TAG;
                Slog.e(str2, str + ": called by UID " + callingUid + ", but there's no service set for user " + callingUserId);
                return false;
            }
            ComponentName unflattenFromString = ComponentName.unflattenFromString(serviceName);
            if (unflattenFromString == null) {
                String str3 = MusicRecognitionManagerService.TAG;
                Slog.w(str3, str + ": invalid service name: " + serviceName);
                return false;
            }
            String packageName = unflattenFromString.getPackageName();
            try {
                int packageUidAsUser = MusicRecognitionManagerService.this.getContext().getPackageManager().getPackageUidAsUser(packageName, UserHandle.getCallingUserId());
                if (callingUid != packageUidAsUser) {
                    String str4 = MusicRecognitionManagerService.TAG;
                    Slog.e(str4, str + ": called by UID " + callingUid + ", but service UID is " + packageUidAsUser);
                    return false;
                }
                return true;
            } catch (PackageManager.NameNotFoundException unused) {
                String str5 = MusicRecognitionManagerService.TAG;
                Slog.w(str5, str + ": could not verify UID for " + serviceName);
                return false;
            }
        }
    }
}
