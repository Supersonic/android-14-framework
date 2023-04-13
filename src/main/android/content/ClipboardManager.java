package android.content;

import android.annotation.SystemApi;
import android.content.ClipboardManager;
import android.content.IClipboard;
import android.content.IOnPrimaryClipChangedListener;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import java.util.ArrayList;
import java.util.Objects;
/* loaded from: classes.dex */
public class ClipboardManager extends android.text.ClipboardManager {
    public static final String DEVICE_CONFIG_ALLOW_VIRTUALDEVICE_SILOS = "allow_virtualdevice_silos";
    public static final boolean DEVICE_CONFIG_DEFAULT_ALLOW_VIRTUALDEVICE_SILOS = false;
    public static final boolean DEVICE_CONFIG_DEFAULT_SHOW_ACCESS_NOTIFICATIONS = true;
    public static final String DEVICE_CONFIG_SHOW_ACCESS_NOTIFICATIONS = "show_access_notifications";
    private final Context mContext;
    private final Handler mHandler;
    private final ArrayList<OnPrimaryClipChangedListener> mPrimaryClipChangedListeners = new ArrayList<>();
    private final IOnPrimaryClipChangedListener.Stub mPrimaryClipChangedServiceListener = new BinderC06031();
    private final IClipboard mService = IClipboard.Stub.asInterface(ServiceManager.getServiceOrThrow("clipboard"));

    /* loaded from: classes.dex */
    public interface OnPrimaryClipChangedListener {
        void onPrimaryClipChanged();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.content.ClipboardManager$1 */
    /* loaded from: classes.dex */
    public class BinderC06031 extends IOnPrimaryClipChangedListener.Stub {
        BinderC06031() {
        }

        @Override // android.content.IOnPrimaryClipChangedListener
        public void dispatchPrimaryClipChanged() {
            ClipboardManager.this.mHandler.post(new Runnable() { // from class: android.content.ClipboardManager$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ClipboardManager.BinderC06031.this.lambda$dispatchPrimaryClipChanged$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$dispatchPrimaryClipChanged$0() {
            ClipboardManager.this.reportPrimaryClipChanged();
        }
    }

    public ClipboardManager(Context context, Handler handler) throws ServiceManager.ServiceNotFoundException {
        this.mContext = context;
        this.mHandler = handler;
    }

    @SystemApi
    public boolean areClipboardAccessNotificationsEnabled() {
        try {
            return this.mService.areClipboardAccessNotificationsEnabledForUser(this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setClipboardAccessNotificationsEnabled(boolean enable) {
        try {
            this.mService.setClipboardAccessNotificationsEnabledForUser(enable, this.mContext.getUserId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setPrimaryClip(ClipData clip) {
        try {
            Objects.requireNonNull(clip);
            clip.prepareToLeaveProcess(true);
            this.mService.setPrimaryClip(clip, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setPrimaryClipAsPackage(ClipData clip, String sourcePackage) {
        try {
            Objects.requireNonNull(clip);
            Objects.requireNonNull(sourcePackage);
            clip.prepareToLeaveProcess(true);
            this.mService.setPrimaryClipAsPackage(clip, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId(), sourcePackage);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void clearPrimaryClip() {
        try {
            this.mService.clearPrimaryClip(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ClipData getPrimaryClip() {
        try {
            return this.mService.getPrimaryClip(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public ClipDescription getPrimaryClipDescription() {
        try {
            return this.mService.getPrimaryClipDescription(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean hasPrimaryClip() {
        try {
            return this.mService.hasPrimaryClip(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void addPrimaryClipChangedListener(OnPrimaryClipChangedListener what) {
        synchronized (this.mPrimaryClipChangedListeners) {
            if (this.mPrimaryClipChangedListeners.isEmpty()) {
                try {
                    this.mService.addPrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
            this.mPrimaryClipChangedListeners.add(what);
        }
    }

    public void removePrimaryClipChangedListener(OnPrimaryClipChangedListener what) {
        synchronized (this.mPrimaryClipChangedListeners) {
            this.mPrimaryClipChangedListeners.remove(what);
            if (this.mPrimaryClipChangedListeners.isEmpty()) {
                try {
                    this.mService.removePrimaryClipChangedListener(this.mPrimaryClipChangedServiceListener, this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            }
        }
    }

    @Override // android.text.ClipboardManager
    @Deprecated
    public CharSequence getText() {
        ClipData clip = getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(this.mContext);
        }
        return null;
    }

    @Override // android.text.ClipboardManager
    @Deprecated
    public void setText(CharSequence text) {
        setPrimaryClip(ClipData.newPlainText(null, text));
    }

    @Override // android.text.ClipboardManager
    @Deprecated
    public boolean hasText() {
        try {
            return this.mService.hasClipboardText(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getPrimaryClipSource() {
        try {
            return this.mService.getPrimaryClipSource(this.mContext.getOpPackageName(), this.mContext.getAttributionTag(), this.mContext.getUserId(), this.mContext.getDeviceId());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    void reportPrimaryClipChanged() {
        synchronized (this.mPrimaryClipChangedListeners) {
            int N = this.mPrimaryClipChangedListeners.size();
            if (N <= 0) {
                return;
            }
            Object[] listeners = this.mPrimaryClipChangedListeners.toArray();
            for (Object obj : listeners) {
                ((OnPrimaryClipChangedListener) obj).onPrimaryClipChanged();
            }
        }
    }
}
