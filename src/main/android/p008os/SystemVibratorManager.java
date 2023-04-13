package android.p008os;

import android.content.Context;
import android.p008os.IVibratorManagerService;
import android.p008os.IVibratorStateListener;
import android.p008os.SystemVibratorManager;
import android.p008os.Vibrator;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.util.Objects;
import java.util.concurrent.Executor;
/* renamed from: android.os.SystemVibratorManager */
/* loaded from: classes3.dex */
public class SystemVibratorManager extends VibratorManager {
    private static final String TAG = "VibratorManager";
    private final Context mContext;
    private final ArrayMap<Vibrator.OnVibratorStateChangedListener, OnVibratorStateChangedListenerDelegate> mListeners;
    private final Object mLock;
    private final IVibratorManagerService mService;
    private final Binder mToken;
    private int[] mVibratorIds;
    private final SparseArray<Vibrator> mVibrators;

    public SystemVibratorManager(Context context) {
        super(context);
        this.mToken = new Binder();
        this.mLock = new Object();
        this.mVibrators = new SparseArray<>();
        this.mListeners = new ArrayMap<>();
        this.mContext = context;
        this.mService = IVibratorManagerService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_MANAGER_SERVICE));
    }

    @Override // android.p008os.VibratorManager
    public int[] getVibratorIds() {
        IVibratorManagerService iVibratorManagerService;
        synchronized (this.mLock) {
            int[] iArr = this.mVibratorIds;
            if (iArr != null) {
                return iArr;
            }
            try {
                iVibratorManagerService = this.mService;
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
            if (iVibratorManagerService == null) {
                Log.m104w(TAG, "Failed to retrieve vibrator ids; no vibrator manager service.");
                return new int[0];
            }
            int[] vibratorIds = iVibratorManagerService.getVibratorIds();
            this.mVibratorIds = vibratorIds;
            return vibratorIds;
        }
    }

    @Override // android.p008os.VibratorManager
    public Vibrator getVibrator(int vibratorId) {
        Vibrator vibrator;
        synchronized (this.mLock) {
            Vibrator vibrator2 = this.mVibrators.get(vibratorId);
            if (vibrator2 != null) {
                return vibrator2;
            }
            VibratorInfo info = null;
            try {
                IVibratorManagerService iVibratorManagerService = this.mService;
                if (iVibratorManagerService == null) {
                    Log.m104w(TAG, "Failed to retrieve vibrator; no vibrator manager service.");
                } else {
                    info = iVibratorManagerService.getVibratorInfo(vibratorId);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
            if (info != null) {
                vibrator = new SingleVibrator(info);
                this.mVibrators.put(vibratorId, vibrator);
            } else {
                vibrator = NullVibrator.getInstance();
            }
            return vibrator;
        }
    }

    @Override // android.p008os.VibratorManager
    public Vibrator getDefaultVibrator() {
        return (Vibrator) this.mContext.getSystemService(Vibrator.class);
    }

    @Override // android.p008os.VibratorManager
    public boolean setAlwaysOnEffect(int uid, String opPkg, int alwaysOnId, CombinedVibration effect, VibrationAttributes attributes) {
        IVibratorManagerService iVibratorManagerService = this.mService;
        if (iVibratorManagerService == null) {
            Log.m104w(TAG, "Failed to set always-on effect; no vibrator manager service.");
            return false;
        }
        try {
            return iVibratorManagerService.setAlwaysOnEffect(uid, opPkg, alwaysOnId, effect, attributes);
        } catch (RemoteException e) {
            Log.m103w(TAG, "Failed to set always-on effect.", e);
            return false;
        }
    }

    @Override // android.p008os.VibratorManager
    public void vibrate(int uid, String opPkg, CombinedVibration effect, String reason, VibrationAttributes attributes) {
        IVibratorManagerService iVibratorManagerService = this.mService;
        if (iVibratorManagerService == null) {
            Log.m104w(TAG, "Failed to vibrate; no vibrator manager service.");
            return;
        }
        try {
            iVibratorManagerService.vibrate(uid, this.mContext.getAssociatedDisplayId(), opPkg, effect, attributes, reason, this.mToken);
        } catch (RemoteException e) {
            Log.m103w(TAG, "Failed to vibrate.", e);
        }
    }

    @Override // android.p008os.VibratorManager
    public void cancel() {
        cancelVibration(-1);
    }

    @Override // android.p008os.VibratorManager
    public void cancel(int usageFilter) {
        cancelVibration(usageFilter);
    }

    private void cancelVibration(int usageFilter) {
        IVibratorManagerService iVibratorManagerService = this.mService;
        if (iVibratorManagerService == null) {
            Log.m104w(TAG, "Failed to cancel vibration; no vibrator manager service.");
            return;
        }
        try {
            iVibratorManagerService.cancelVibrate(usageFilter, this.mToken);
        } catch (RemoteException e) {
            Log.m103w(TAG, "Failed to cancel vibration.", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: android.os.SystemVibratorManager$OnVibratorStateChangedListenerDelegate */
    /* loaded from: classes3.dex */
    public static class OnVibratorStateChangedListenerDelegate extends IVibratorStateListener.Stub {
        private final Executor mExecutor;
        private final Vibrator.OnVibratorStateChangedListener mListener;

        OnVibratorStateChangedListenerDelegate(Vibrator.OnVibratorStateChangedListener listener, Executor executor) {
            this.mExecutor = executor;
            this.mListener = listener;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onVibrating$0(boolean isVibrating) {
            this.mListener.onVibratorStateChanged(isVibrating);
        }

        @Override // android.p008os.IVibratorStateListener
        public void onVibrating(final boolean isVibrating) {
            this.mExecutor.execute(new Runnable() { // from class: android.os.SystemVibratorManager$OnVibratorStateChangedListenerDelegate$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SystemVibratorManager.OnVibratorStateChangedListenerDelegate.this.lambda$onVibrating$0(isVibrating);
                }
            });
        }
    }

    /* renamed from: android.os.SystemVibratorManager$SingleVibrator */
    /* loaded from: classes3.dex */
    private final class SingleVibrator extends Vibrator {
        private final VibratorInfo mVibratorInfo;

        SingleVibrator(VibratorInfo vibratorInfo) {
            this.mVibratorInfo = vibratorInfo;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.Vibrator
        public VibratorInfo getInfo() {
            return this.mVibratorInfo;
        }

        @Override // android.p008os.Vibrator
        public boolean hasVibrator() {
            return true;
        }

        @Override // android.p008os.Vibrator
        public boolean hasAmplitudeControl() {
            return this.mVibratorInfo.hasAmplitudeControl();
        }

        @Override // android.p008os.Vibrator
        public boolean setAlwaysOnEffect(int uid, String opPkg, int alwaysOnId, VibrationEffect effect, VibrationAttributes attrs) {
            CombinedVibration combined = CombinedVibration.startParallel().addVibrator(this.mVibratorInfo.getId(), effect).combine();
            return SystemVibratorManager.this.setAlwaysOnEffect(uid, opPkg, alwaysOnId, combined, attrs);
        }

        @Override // android.p008os.Vibrator
        public void vibrate(int uid, String opPkg, VibrationEffect vibe, String reason, VibrationAttributes attributes) {
            CombinedVibration combined = CombinedVibration.startParallel().addVibrator(this.mVibratorInfo.getId(), vibe).combine();
            SystemVibratorManager.this.vibrate(uid, opPkg, combined, reason, attributes);
        }

        @Override // android.p008os.Vibrator
        public void cancel() {
            SystemVibratorManager.this.cancel();
        }

        @Override // android.p008os.Vibrator
        public void cancel(int usageFilter) {
            SystemVibratorManager.this.cancel(usageFilter);
        }

        @Override // android.p008os.Vibrator
        public boolean isVibrating() {
            if (SystemVibratorManager.this.mService == null) {
                Log.m104w(SystemVibratorManager.TAG, "Failed to check status of vibrator " + this.mVibratorInfo.getId() + "; no vibrator service.");
                return false;
            }
            try {
                return SystemVibratorManager.this.mService.isVibrating(this.mVibratorInfo.getId());
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return false;
            }
        }

        @Override // android.p008os.Vibrator
        public void addVibratorStateListener(Vibrator.OnVibratorStateChangedListener listener) {
            Objects.requireNonNull(listener);
            if (SystemVibratorManager.this.mContext == null) {
                Log.m104w(SystemVibratorManager.TAG, "Failed to add vibrate state listener; no vibrator context.");
            } else {
                addVibratorStateListener(SystemVibratorManager.this.mContext.getMainExecutor(), listener);
            }
        }

        @Override // android.p008os.Vibrator
        public void addVibratorStateListener(Executor executor, Vibrator.OnVibratorStateChangedListener listener) {
            OnVibratorStateChangedListenerDelegate delegate;
            Objects.requireNonNull(listener);
            Objects.requireNonNull(executor);
            if (SystemVibratorManager.this.mService == null) {
                Log.m104w(SystemVibratorManager.TAG, "Failed to add vibrate state listener to vibrator " + this.mVibratorInfo.getId() + "; no vibrator service.");
                return;
            }
            synchronized (SystemVibratorManager.this.mLock) {
                if (SystemVibratorManager.this.mListeners.containsKey(listener)) {
                    Log.m104w(SystemVibratorManager.TAG, "Listener already registered.");
                    return;
                }
                try {
                    delegate = new OnVibratorStateChangedListenerDelegate(listener, executor);
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
                if (!SystemVibratorManager.this.mService.registerVibratorStateListener(this.mVibratorInfo.getId(), delegate)) {
                    Log.m104w(SystemVibratorManager.TAG, "Failed to add vibrate state listener to vibrator " + this.mVibratorInfo.getId());
                } else {
                    SystemVibratorManager.this.mListeners.put(listener, delegate);
                }
            }
        }

        @Override // android.p008os.Vibrator
        public void removeVibratorStateListener(Vibrator.OnVibratorStateChangedListener listener) {
            Objects.requireNonNull(listener);
            if (SystemVibratorManager.this.mService == null) {
                Log.m104w(SystemVibratorManager.TAG, "Failed to remove vibrate state listener from vibrator " + this.mVibratorInfo.getId() + "; no vibrator service.");
                return;
            }
            synchronized (SystemVibratorManager.this.mLock) {
                if (SystemVibratorManager.this.mListeners.containsKey(listener)) {
                    OnVibratorStateChangedListenerDelegate delegate = (OnVibratorStateChangedListenerDelegate) SystemVibratorManager.this.mListeners.get(listener);
                    try {
                        if (!SystemVibratorManager.this.mService.unregisterVibratorStateListener(this.mVibratorInfo.getId(), delegate)) {
                            Log.m104w(SystemVibratorManager.TAG, "Failed to remove vibrate state listener from vibrator " + this.mVibratorInfo.getId());
                            return;
                        }
                        SystemVibratorManager.this.mListeners.remove(listener);
                    } catch (RemoteException e) {
                        e.rethrowFromSystemServer();
                    }
                }
            }
        }
    }
}
