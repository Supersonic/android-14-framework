package com.android.server.wallpapereffectsgeneration;

import android.app.ActivityManagerInternal;
import android.app.wallpapereffectsgeneration.CinematicEffectRequest;
import android.app.wallpapereffectsgeneration.CinematicEffectResponse;
import android.app.wallpapereffectsgeneration.ICinematicEffectListener;
import android.app.wallpapereffectsgeneration.IWallpaperEffectsGenerationManager;
import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import java.io.FileDescriptor;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class WallpaperEffectsGenerationManagerService extends AbstractMasterSystemService<WallpaperEffectsGenerationManagerService, WallpaperEffectsGenerationPerUserService> {
    public static final String TAG = "WallpaperEffectsGenerationManagerService";
    public final ActivityTaskManagerInternal mActivityTaskManagerInternal;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public int getMaximumTemporaryServiceDurationMs() {
        return 120000;
    }

    public WallpaperEffectsGenerationManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039907), null, 17);
        this.mActivityTaskManagerInternal = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public WallpaperEffectsGenerationPerUserService newServiceLocked(int i, boolean z) {
        return new WallpaperEffectsGenerationPerUserService(this, this.mLock, i);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("wallpaper_effects_generation", new WallpaperEffectsGenerationManagerStub());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_WALLPAPER_EFFECTS_GENERATION", TAG);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageUpdatedLocked(int i) {
        WallpaperEffectsGenerationPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageUpdatedLocked();
        }
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServicePackageRestartedLocked(int i) {
        WallpaperEffectsGenerationPerUserService peekServiceForUserLocked = peekServiceForUserLocked(i);
        if (peekServiceForUserLocked != null) {
            peekServiceForUserLocked.onPackageRestartedLocked();
        }
    }

    /* loaded from: classes2.dex */
    public class WallpaperEffectsGenerationManagerStub extends IWallpaperEffectsGenerationManager.Stub {
        public WallpaperEffectsGenerationManagerStub() {
        }

        public void generateCinematicEffect(final CinematicEffectRequest cinematicEffectRequest, final ICinematicEffectListener iCinematicEffectListener) {
            if (runForUser("generateCinematicEffect", true, new Consumer() { // from class: com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService$WallpaperEffectsGenerationManagerStub$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((WallpaperEffectsGenerationPerUserService) obj).onGenerateCinematicEffectLocked(cinematicEffectRequest, iCinematicEffectListener);
                }
            })) {
                return;
            }
            try {
                iCinematicEffectListener.onCinematicEffectGenerated(new CinematicEffectResponse.Builder(0, cinematicEffectRequest.getTaskId()).build());
            } catch (RemoteException unused) {
            }
        }

        public void returnCinematicEffectResponse(final CinematicEffectResponse cinematicEffectResponse) {
            runForUser("returnCinematicResponse", false, new Consumer() { // from class: com.android.server.wallpapereffectsgeneration.WallpaperEffectsGenerationManagerService$WallpaperEffectsGenerationManagerStub$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((WallpaperEffectsGenerationPerUserService) obj).onReturnCinematicEffectResponseLocked(cinematicEffectResponse);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new WallpaperEffectsGenerationManagerServiceShellCommand(WallpaperEffectsGenerationManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }

        public final boolean runForUser(String str, boolean z, Consumer<WallpaperEffectsGenerationPerUserService> consumer) {
            boolean z2;
            int handleIncomingUser = ((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)).handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), Binder.getCallingUserHandle().getIdentifier(), false, 0, (String) null, (String) null);
            if (z && WallpaperEffectsGenerationManagerService.this.getContext().checkCallingPermission("android.permission.MANAGE_WALLPAPER_EFFECTS_GENERATION") != 0 && !WallpaperEffectsGenerationManagerService.this.mServiceNameResolver.isTemporary(handleIncomingUser) && !WallpaperEffectsGenerationManagerService.this.mActivityTaskManagerInternal.isCallerRecents(Binder.getCallingUid())) {
                String str2 = "Permission Denial: Cannot call " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
                Slog.w(WallpaperEffectsGenerationManagerService.TAG, str2);
                throw new SecurityException(str2);
            }
            int callingUid = Binder.getCallingUid();
            long clearCallingIdentity = Binder.clearCallingIdentity();
            try {
                synchronized (WallpaperEffectsGenerationManagerService.this.mLock) {
                    WallpaperEffectsGenerationPerUserService wallpaperEffectsGenerationPerUserService = (WallpaperEffectsGenerationPerUserService) WallpaperEffectsGenerationManagerService.this.getServiceForUserLocked(handleIncomingUser);
                    if (wallpaperEffectsGenerationPerUserService != null) {
                        if (!z && !wallpaperEffectsGenerationPerUserService.isCallingUidAllowed(callingUid)) {
                            String str3 = "Permission Denial: cannot call " + str + ", uid[" + callingUid + "] doesn't match service implementation";
                            Slog.w(WallpaperEffectsGenerationManagerService.TAG, str3);
                            throw new SecurityException(str3);
                        }
                        consumer.accept(wallpaperEffectsGenerationPerUserService);
                        z2 = true;
                    } else {
                        z2 = false;
                    }
                }
                return z2;
            } finally {
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
        }
    }
}
