package android.content.p001pm;

import android.annotation.SystemApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.p001pm.IShortcutService;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import com.android.internal.infra.AndroidFuture;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.concurrent.ExecutionException;
/* renamed from: android.content.pm.ShortcutManager */
/* loaded from: classes.dex */
public class ShortcutManager {
    public static final int FLAG_MATCH_CACHED = 8;
    public static final int FLAG_MATCH_DYNAMIC = 2;
    public static final int FLAG_MATCH_MANIFEST = 1;
    public static final int FLAG_MATCH_PINNED = 4;
    private static final String TAG = "ShortcutManager";
    private final Context mContext;
    private final IShortcutService mService;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.content.pm.ShortcutManager$ShortcutMatchFlags */
    /* loaded from: classes.dex */
    public @interface ShortcutMatchFlags {
    }

    public ShortcutManager(Context context, IShortcutService service) {
        this.mContext = context;
        this.mService = service;
    }

    public ShortcutManager(Context context) {
        this(context, IShortcutService.Stub.asInterface(ServiceManager.getService("shortcut")));
    }

    public boolean setDynamicShortcuts(List<ShortcutInfo> shortcutInfoList) {
        try {
            return this.mService.setDynamicShortcuts(this.mContext.getPackageName(), new ParceledListSlice(shortcutInfoList), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ShortcutInfo> getDynamicShortcuts() {
        try {
            return this.mService.getShortcuts(this.mContext.getPackageName(), 2, injectMyUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ShortcutInfo> getManifestShortcuts() {
        try {
            return this.mService.getShortcuts(this.mContext.getPackageName(), 1, injectMyUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ShortcutInfo> getShortcuts(int matchFlags) {
        try {
            return this.mService.getShortcuts(this.mContext.getPackageName(), matchFlags, injectMyUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean addDynamicShortcuts(List<ShortcutInfo> shortcutInfoList) {
        try {
            return this.mService.addDynamicShortcuts(this.mContext.getPackageName(), new ParceledListSlice(shortcutInfoList), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeDynamicShortcuts(List<String> shortcutIds) {
        try {
            this.mService.removeDynamicShortcuts(this.mContext.getPackageName(), shortcutIds, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeAllDynamicShortcuts() {
        try {
            this.mService.removeAllDynamicShortcuts(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void removeLongLivedShortcuts(List<String> shortcutIds) {
        try {
            this.mService.removeLongLivedShortcuts(this.mContext.getPackageName(), shortcutIds, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public List<ShortcutInfo> getPinnedShortcuts() {
        try {
            return this.mService.getShortcuts(this.mContext.getPackageName(), 4, injectMyUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean updateShortcuts(List<ShortcutInfo> shortcutInfoList) {
        try {
            return this.mService.updateShortcuts(this.mContext.getPackageName(), new ParceledListSlice(shortcutInfoList), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void disableShortcuts(List<String> shortcutIds) {
        try {
            this.mService.disableShortcuts(this.mContext.getPackageName(), shortcutIds, null, 0, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void disableShortcuts(List<String> shortcutIds, int disabledMessageResId) {
        try {
            this.mService.disableShortcuts(this.mContext.getPackageName(), shortcutIds, null, disabledMessageResId, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void disableShortcuts(List<String> shortcutIds, String disabledMessage) {
        disableShortcuts(shortcutIds, (CharSequence) disabledMessage);
    }

    public void disableShortcuts(List<String> shortcutIds, CharSequence disabledMessage) {
        try {
            this.mService.disableShortcuts(this.mContext.getPackageName(), shortcutIds, disabledMessage, 0, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void enableShortcuts(List<String> shortcutIds) {
        try {
            this.mService.enableShortcuts(this.mContext.getPackageName(), shortcutIds, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getMaxShortcutCountForActivity() {
        return getMaxShortcutCountPerActivity();
    }

    public int getMaxShortcutCountPerActivity() {
        try {
            return this.mService.getMaxShortcutCountPerActivity(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getRemainingCallCount() {
        try {
            return this.mService.getRemainingCallCount(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public long getRateLimitResetTime() {
        try {
            return this.mService.getRateLimitResetTime(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isRateLimitingActive() {
        try {
            return this.mService.getRemainingCallCount(this.mContext.getPackageName(), injectMyUserId()) == 0;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getIconMaxWidth() {
        try {
            return this.mService.getIconMaxDimensions(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getIconMaxHeight() {
        try {
            return this.mService.getIconMaxDimensions(this.mContext.getPackageName(), injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void reportShortcutUsed(String shortcutId) {
        try {
            this.mService.reportShortcutUsed(this.mContext.getPackageName(), shortcutId, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isRequestPinShortcutSupported() {
        try {
            return this.mService.isRequestPinItemSupported(injectMyUserId(), 1);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean requestPinShortcut(ShortcutInfo shortcut, IntentSender resultIntent) {
        try {
            AndroidFuture<String> ret = new AndroidFuture<>();
            this.mService.requestPinShortcut(this.mContext.getPackageName(), shortcut, resultIntent, injectMyUserId(), ret);
            return Boolean.parseBoolean((String) getFutureOrThrow(ret));
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Intent createShortcutResultIntent(ShortcutInfo shortcut) {
        AndroidFuture<Intent> ret = new AndroidFuture<>();
        try {
            this.mService.createShortcutResultIntent(this.mContext.getPackageName(), shortcut, injectMyUserId(), ret);
            Intent result = (Intent) getFutureOrThrow(ret);
            if (result != null) {
                result.prepareToEnterProcess(32, this.mContext.getAttributionSource());
            }
            return result;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void onApplicationActive(String packageName, int userId) {
        try {
            this.mService.onApplicationActive(packageName, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    protected int injectMyUserId() {
        return this.mContext.getUserId();
    }

    @SystemApi
    public List<ShareShortcutInfo> getShareTargets(IntentFilter filter) {
        try {
            return this.mService.getShareTargets(this.mContext.getPackageName(), filter, injectMyUserId()).getList();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    /* renamed from: android.content.pm.ShortcutManager$ShareShortcutInfo */
    /* loaded from: classes.dex */
    public static final class ShareShortcutInfo implements Parcelable {
        public static final Parcelable.Creator<ShareShortcutInfo> CREATOR = new Parcelable.Creator<ShareShortcutInfo>() { // from class: android.content.pm.ShortcutManager.ShareShortcutInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ShareShortcutInfo createFromParcel(Parcel in) {
                return new ShareShortcutInfo(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public ShareShortcutInfo[] newArray(int size) {
                return new ShareShortcutInfo[size];
            }
        };
        private final ShortcutInfo mShortcutInfo;
        private final ComponentName mTargetComponent;

        public ShareShortcutInfo(ShortcutInfo shortcutInfo, ComponentName targetComponent) {
            if (shortcutInfo == null) {
                throw new NullPointerException("shortcut info is null");
            }
            if (targetComponent == null) {
                throw new NullPointerException("target component is null");
            }
            this.mShortcutInfo = shortcutInfo;
            this.mTargetComponent = targetComponent;
        }

        private ShareShortcutInfo(Parcel in) {
            this.mShortcutInfo = (ShortcutInfo) in.readParcelable(ShortcutInfo.class.getClassLoader(), ShortcutInfo.class);
            this.mTargetComponent = (ComponentName) in.readParcelable(ComponentName.class.getClassLoader(), ComponentName.class);
        }

        public ShortcutInfo getShortcutInfo() {
            return this.mShortcutInfo;
        }

        public ComponentName getTargetComponent() {
            return this.mTargetComponent;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.mShortcutInfo, flags);
            dest.writeParcelable(this.mTargetComponent, flags);
        }
    }

    @SystemApi
    public boolean hasShareTargets(String packageName) {
        try {
            return this.mService.hasShareTargets(this.mContext.getPackageName(), packageName, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void pushDynamicShortcut(ShortcutInfo shortcut) {
        try {
            this.mService.pushDynamicShortcut(this.mContext.getPackageName(), shortcut, injectMyUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private static <T> T getFutureOrThrow(AndroidFuture<T> future) {
        try {
            return future.get();
        } catch (Throwable th) {
            e = th;
            if (e instanceof ExecutionException) {
                e = e.getCause();
            }
            if (e instanceof RuntimeException) {
                throw ((RuntimeException) e);
            }
            if (e instanceof Error) {
                throw ((Error) e);
            }
            throw new RuntimeException(e);
        }
    }
}
