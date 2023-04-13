package com.android.server.p011pm;

import android.app.role.RoleManager;
import android.os.Binder;
import android.os.UserHandle;
import android.util.Slog;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.util.CollectionUtils;
import com.android.server.FgThread;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.DefaultAppProvider */
/* loaded from: classes2.dex */
public class DefaultAppProvider {
    public final Supplier<RoleManager> mRoleManagerSupplier;
    public final Supplier<UserManagerInternal> mUserManagerInternalSupplier;

    public DefaultAppProvider(Supplier<RoleManager> supplier, Supplier<UserManagerInternal> supplier2) {
        this.mRoleManagerSupplier = supplier;
        this.mUserManagerInternalSupplier = supplier2;
    }

    public String getDefaultBrowser(int i) {
        return getRoleHolder("android.app.role.BROWSER", i);
    }

    public boolean setDefaultBrowser(String str, boolean z, int i) {
        RoleManager roleManager;
        if (i == -1 || (roleManager = this.mRoleManagerSupplier.get()) == null) {
            return false;
        }
        UserHandle of = UserHandle.of(i);
        Executor executor = FgThread.getExecutor();
        final AndroidFuture androidFuture = new AndroidFuture();
        Consumer consumer = new Consumer() { // from class: com.android.server.pm.DefaultAppProvider$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                DefaultAppProvider.lambda$setDefaultBrowser$0(androidFuture, (Boolean) obj);
            }
        };
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (str != null) {
                roleManager.addRoleHolderAsUser("android.app.role.BROWSER", str, 0, of, executor, consumer);
            } else {
                roleManager.clearRoleHoldersAsUser("android.app.role.BROWSER", 0, of, executor, consumer);
            }
            if (!z) {
                try {
                    androidFuture.get(5L, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    Slog.e("PackageManager", "Exception while setting default browser: " + str, e);
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                    return false;
                }
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public static /* synthetic */ void lambda$setDefaultBrowser$0(AndroidFuture androidFuture, Boolean bool) {
        if (bool.booleanValue()) {
            androidFuture.complete((Object) null);
        } else {
            androidFuture.completeExceptionally(new RuntimeException());
        }
    }

    public String getDefaultDialer(int i) {
        return getRoleHolder("android.app.role.DIALER", i);
    }

    public String getDefaultHome(int i) {
        return getRoleHolder("android.app.role.HOME", this.mUserManagerInternalSupplier.get().getProfileParentId(i));
    }

    public boolean setDefaultHome(String str, int i, Executor executor, Consumer<Boolean> consumer) {
        RoleManager roleManager = this.mRoleManagerSupplier.get();
        if (roleManager == null) {
            return false;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            roleManager.addRoleHolderAsUser("android.app.role.HOME", str, 0, UserHandle.of(i), executor, consumer);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final String getRoleHolder(String str, int i) {
        RoleManager roleManager = this.mRoleManagerSupplier.get();
        if (roleManager == null) {
            return null;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return (String) CollectionUtils.firstOrNull(roleManager.getRoleHoldersAsUser(str, UserHandle.of(i)));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
