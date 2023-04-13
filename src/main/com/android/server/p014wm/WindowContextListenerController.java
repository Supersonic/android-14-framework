package com.android.server.p014wm;

import android.app.IWindowToken;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.view.Display;
import android.window.WindowProviderService;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import java.util.Objects;
/* renamed from: com.android.server.wm.WindowContextListenerController */
/* loaded from: classes2.dex */
public class WindowContextListenerController {
    @VisibleForTesting
    final ArrayMap<IBinder, WindowContextListenerImpl> mListeners = new ArrayMap<>();

    public void registerWindowContainerListener(IBinder iBinder, WindowContainer<?> windowContainer, int i, int i2, Bundle bundle) {
        registerWindowContainerListener(iBinder, windowContainer, i, i2, bundle, true);
    }

    public void registerWindowContainerListener(IBinder iBinder, WindowContainer<?> windowContainer, int i, int i2, Bundle bundle, boolean z) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl == null) {
            new WindowContextListenerImpl(iBinder, windowContainer, i, i2, bundle).register(z);
        } else {
            windowContextListenerImpl.updateContainer(windowContainer);
        }
    }

    public void unregisterWindowContainerListener(IBinder iBinder) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl == null) {
            return;
        }
        windowContextListenerImpl.unregister();
        if (windowContextListenerImpl.mDeathRecipient != null) {
            windowContextListenerImpl.mDeathRecipient.unlinkToDeath();
        }
    }

    public void dispatchPendingConfigurationIfNeeded(int i) {
        for (int size = this.mListeners.size() - 1; size >= 0; size--) {
            WindowContextListenerImpl valueAt = this.mListeners.valueAt(size);
            if (valueAt.getWindowContainer().getDisplayContent().getDisplayId() == i && valueAt.mHasPendingConfiguration) {
                valueAt.reportConfigToWindowTokenClient();
            }
        }
    }

    public boolean assertCallerCanModifyListener(IBinder iBinder, boolean z, int i) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl == null) {
            if (ProtoLogCache.WM_DEBUG_ADD_REMOVE_enabled) {
                ProtoLogImpl.i(ProtoLogGroup.WM_DEBUG_ADD_REMOVE, -1136467585, 0, (String) null, (Object[]) null);
            }
            return false;
        } else if (z || i == windowContextListenerImpl.mOwnerUid) {
            return true;
        } else {
            throw new UnsupportedOperationException("Uid mismatch. Caller uid is " + i + ", while the listener's owner is from " + windowContextListenerImpl.mOwnerUid);
        }
    }

    public boolean hasListener(IBinder iBinder) {
        return this.mListeners.containsKey(iBinder);
    }

    public int getWindowType(IBinder iBinder) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl != null) {
            return windowContextListenerImpl.mType;
        }
        return -1;
    }

    public Bundle getOptions(IBinder iBinder) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl != null) {
            return windowContextListenerImpl.mOptions;
        }
        return null;
    }

    public WindowContainer<?> getContainer(IBinder iBinder) {
        WindowContextListenerImpl windowContextListenerImpl = this.mListeners.get(iBinder);
        if (windowContextListenerImpl != null) {
            return windowContextListenerImpl.mContainer;
        }
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("WindowContextListenerController{");
        sb.append("mListeners=[");
        int size = this.mListeners.values().size();
        for (int i = 0; i < size; i++) {
            sb.append(this.mListeners.valueAt(i));
            if (i != size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    @VisibleForTesting
    /* renamed from: com.android.server.wm.WindowContextListenerController$WindowContextListenerImpl */
    /* loaded from: classes2.dex */
    public class WindowContextListenerImpl implements WindowContainerListener {
        public final IWindowToken mClientToken;
        public WindowContainer<?> mContainer;
        public DeathRecipient mDeathRecipient;
        public boolean mHasPendingConfiguration;
        public Configuration mLastReportedConfig;
        public int mLastReportedDisplay;
        public final Bundle mOptions;
        public final int mOwnerUid;
        public final int mType;

        public WindowContextListenerImpl(IBinder iBinder, WindowContainer<?> windowContainer, int i, int i2, Bundle bundle) {
            this.mLastReportedDisplay = -1;
            this.mClientToken = IWindowToken.Stub.asInterface(iBinder);
            Objects.requireNonNull(windowContainer);
            this.mContainer = windowContainer;
            this.mOwnerUid = i;
            this.mType = i2;
            this.mOptions = bundle;
            DeathRecipient deathRecipient = new DeathRecipient();
            try {
                deathRecipient.linkToDeath();
                this.mDeathRecipient = deathRecipient;
            } catch (RemoteException unused) {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.e(ProtoLogGroup.WM_ERROR, -2014162875, 0, "Could not register window container listener token=%s, container=%s", new Object[]{String.valueOf(iBinder), String.valueOf(this.mContainer)});
                }
            }
        }

        @VisibleForTesting
        public WindowContainer<?> getWindowContainer() {
            return this.mContainer;
        }

        public final void updateContainer(WindowContainer<?> windowContainer) {
            Objects.requireNonNull(windowContainer);
            if (this.mContainer.equals(windowContainer)) {
                return;
            }
            this.mContainer.unregisterWindowContainerListener(this);
            this.mContainer = windowContainer;
            clear();
            register();
        }

        public final void register() {
            register(true);
        }

        public final void register(boolean z) {
            IBinder asBinder = this.mClientToken.asBinder();
            if (this.mDeathRecipient == null) {
                throw new IllegalStateException("Invalid client token: " + asBinder);
            }
            WindowContextListenerController.this.mListeners.putIfAbsent(asBinder, this);
            this.mContainer.registerWindowContainerListener(this, z);
        }

        public final void unregister() {
            this.mContainer.unregisterWindowContainerListener(this);
            WindowContextListenerController.this.mListeners.remove(this.mClientToken.asBinder());
        }

        public final void clear() {
            this.mLastReportedConfig = null;
            this.mLastReportedDisplay = -1;
        }

        @Override // com.android.server.p014wm.ConfigurationContainerListener
        public void onMergedOverrideConfigurationChanged(Configuration configuration) {
            reportConfigToWindowTokenClient();
        }

        @Override // com.android.server.p014wm.WindowContainerListener
        public void onDisplayChanged(DisplayContent displayContent) {
            reportConfigToWindowTokenClient();
        }

        public final void reportConfigToWindowTokenClient() {
            if (this.mDeathRecipient == null) {
                throw new IllegalStateException("Invalid client token: " + this.mClientToken.asBinder());
            }
            DisplayContent displayContent = this.mContainer.getDisplayContent();
            if (displayContent.isReady()) {
                if (!WindowProviderService.isWindowProviderService(this.mOptions) && Display.isSuspendedState(displayContent.getDisplayInfo().state)) {
                    this.mHasPendingConfiguration = true;
                    return;
                }
                Configuration configuration = this.mContainer.getConfiguration();
                int displayId = displayContent.getDisplayId();
                if (this.mLastReportedConfig == null) {
                    this.mLastReportedConfig = new Configuration();
                }
                if (configuration.equals(this.mLastReportedConfig) && displayId == this.mLastReportedDisplay) {
                    return;
                }
                this.mLastReportedConfig.setTo(configuration);
                this.mLastReportedDisplay = displayId;
                try {
                    this.mClientToken.onConfigurationChanged(configuration, displayId);
                } catch (RemoteException unused) {
                    if (ProtoLogCache.WM_ERROR_enabled) {
                        ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 1948483534, 0, "Could not report config changes to the window token client.", (Object[]) null);
                    }
                }
                this.mHasPendingConfiguration = false;
            }
        }

        @Override // com.android.server.p014wm.WindowContainerListener
        public void onRemoved() {
            DisplayContent displayContent;
            if (this.mDeathRecipient == null) {
                throw new IllegalStateException("Invalid client token: " + this.mClientToken.asBinder());
            }
            WindowToken asWindowToken = this.mContainer.asWindowToken();
            if (asWindowToken != null && asWindowToken.isFromClient() && (displayContent = asWindowToken.mWmService.mRoot.getDisplayContent(this.mLastReportedDisplay)) != null) {
                updateContainer(displayContent.findAreaForToken(asWindowToken));
                return;
            }
            this.mDeathRecipient.unlinkToDeath();
            try {
                this.mClientToken.onWindowTokenRemoved();
            } catch (RemoteException unused) {
                if (ProtoLogCache.WM_ERROR_enabled) {
                    ProtoLogImpl.w(ProtoLogGroup.WM_ERROR, 90764070, 0, "Could not report token removal to the window token client.", (Object[]) null);
                }
            }
            unregister();
        }

        public String toString() {
            return "WindowContextListenerImpl{clientToken=" + this.mClientToken.asBinder() + ", container=" + this.mContainer + "}";
        }

        /* renamed from: com.android.server.wm.WindowContextListenerController$WindowContextListenerImpl$DeathRecipient */
        /* loaded from: classes2.dex */
        public class DeathRecipient implements IBinder.DeathRecipient {
            public DeathRecipient() {
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                synchronized (WindowContextListenerImpl.this.mContainer.mWmService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        WindowContextListenerImpl.this.mDeathRecipient = null;
                        WindowContextListenerImpl.this.unregister();
                    } catch (Throwable th) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }

            public void linkToDeath() throws RemoteException {
                WindowContextListenerImpl.this.mClientToken.asBinder().linkToDeath(this, 0);
            }

            public void unlinkToDeath() {
                WindowContextListenerImpl.this.mClientToken.asBinder().unlinkToDeath(this, 0);
            }
        }
    }
}
