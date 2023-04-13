package android.hardware.radio;

import android.graphics.Bitmap;
import android.hardware.radio.ProgramList;
import android.hardware.radio.RadioManager;
import android.p008os.RemoteException;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public final class TunerAdapter extends RadioTuner {
    private static final String TAG = "BroadcastRadio.TunerAdapter";
    private int mBand;
    private final TunerCallbackAdapter mCallback;
    private boolean mIsClosed;
    private Map<String, String> mLegacyListFilter;
    private ProgramList mLegacyListProxy;
    private final Object mLock = new Object();
    private final ITuner mTuner;

    /* JADX INFO: Access modifiers changed from: package-private */
    public TunerAdapter(ITuner tuner, TunerCallbackAdapter callback, int band) {
        this.mTuner = (ITuner) Objects.requireNonNull(tuner, "Tuner cannot be null");
        this.mCallback = (TunerCallbackAdapter) Objects.requireNonNull(callback, "Callback cannot be null");
        this.mBand = band;
    }

    @Override // android.hardware.radio.RadioTuner
    public void close() {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                Log.m106v(TAG, "Tuner is already closed");
                return;
            }
            this.mIsClosed = true;
            ProgramList programList = this.mLegacyListProxy;
            if (programList != null) {
                programList.close();
                this.mLegacyListProxy = null;
            }
            this.mCallback.close();
            try {
                this.mTuner.close();
            } catch (RemoteException e) {
                Log.m109e(TAG, "Exception trying to close tuner", e);
            }
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int setConfiguration(RadioManager.BandConfig config) {
        if (config == null) {
            return -22;
        }
        try {
            this.mTuner.setConfiguration(config);
            synchronized (this.mLock) {
                this.mBand = config.getType();
            }
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalArgumentException e2) {
            Log.m109e(TAG, "Can't set configuration", e2);
            return -22;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int getConfiguration(RadioManager.BandConfig[] config) {
        if (config == null || config.length != 1) {
            throw new IllegalArgumentException("The argument must be an array of length 1");
        }
        try {
            config[0] = this.mTuner.getConfiguration();
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int setMute(boolean mute) {
        try {
            this.mTuner.setMuted(mute);
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalStateException e2) {
            Log.m109e(TAG, "Can't set muted", e2);
            return Integer.MIN_VALUE;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean getMute() {
        try {
            return this.mTuner.isMuted();
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return true;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int step(int direction, boolean skipSubChannel) {
        try {
            ITuner iTuner = this.mTuner;
            boolean z = true;
            if (direction != 1) {
                z = false;
            }
            iTuner.step(z, skipSubChannel);
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalStateException e2) {
            Log.m109e(TAG, "Can't step", e2);
            return -38;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int scan(int direction, boolean skipSubChannel) {
        try {
            ITuner iTuner = this.mTuner;
            boolean z = true;
            if (direction != 1) {
                z = false;
            }
            iTuner.seek(z, skipSubChannel);
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalStateException e2) {
            Log.m109e(TAG, "Can't scan", e2);
            return -38;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int seek(int direction, boolean skipSubChannel) {
        try {
            ITuner iTuner = this.mTuner;
            boolean z = true;
            if (direction != 1) {
                z = false;
            }
            iTuner.seek(z, skipSubChannel);
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalStateException e2) {
            Log.m109e(TAG, "Can't seek", e2);
            return -38;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int tune(int channel, int subChannel) {
        int band;
        try {
            synchronized (this.mLock) {
                band = this.mBand;
            }
            this.mTuner.tune(ProgramSelector.createAmFmSelector(band, channel, subChannel));
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalArgumentException e2) {
            Log.m109e(TAG, "Can't tune", e2);
            return -22;
        } catch (IllegalStateException e3) {
            Log.m109e(TAG, "Can't tune", e3);
            return -38;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public void tune(ProgramSelector selector) {
        try {
            this.mTuner.tune(selector);
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int cancel() {
        try {
            this.mTuner.cancel();
            return 0;
        } catch (RemoteException e) {
            Log.m109e(TAG, "Service died", e);
            return -32;
        } catch (IllegalStateException e2) {
            Log.m109e(TAG, "Can't cancel", e2);
            return -38;
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public void cancelAnnouncement() {
        try {
            this.mTuner.cancelAnnouncement();
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public int getProgramInformation(RadioManager.ProgramInfo[] info) {
        if (info == null || info.length != 1) {
            Log.m110e(TAG, "The argument must be an array of length 1");
            return -22;
        }
        RadioManager.ProgramInfo current = this.mCallback.getCurrentProgramInformation();
        if (current == null) {
            Log.m104w(TAG, "Didn't get program info yet");
            return -38;
        }
        info[0] = current;
        return 0;
    }

    @Override // android.hardware.radio.RadioTuner
    public Bitmap getMetadataImage(int id) {
        try {
            return this.mTuner.getImage(id);
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean startBackgroundScan() {
        try {
            return this.mTuner.startBackgroundScan();
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public List<RadioManager.ProgramInfo> getProgramList(Map<String, String> vendorFilter) {
        synchronized (this.mLock) {
            if (this.mLegacyListProxy == null || !Objects.equals(this.mLegacyListFilter, vendorFilter)) {
                Log.m108i(TAG, "Program list filter has changed, requesting new list");
                this.mLegacyListProxy = new ProgramList();
                this.mLegacyListFilter = vendorFilter;
                this.mCallback.clearLastCompleteList();
                this.mCallback.setProgramListObserver(this.mLegacyListProxy, new ProgramList.OnCloseListener() { // from class: android.hardware.radio.TunerAdapter$$ExternalSyntheticLambda0
                    @Override // android.hardware.radio.ProgramList.OnCloseListener
                    public final void onClose() {
                        Log.m108i(TunerAdapter.TAG, "Empty closeListener in programListObserver");
                    }
                });
            }
        }
        try {
            this.mTuner.startProgramListUpdates(new ProgramList.Filter(vendorFilter));
            List<RadioManager.ProgramInfo> list = this.mCallback.getLastCompleteList();
            if (list == null) {
                throw new IllegalStateException("Program list is not ready yet");
            }
            return list;
        } catch (RemoteException ex) {
            throw new RuntimeException("Service died", ex);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public ProgramList getDynamicProgramList(ProgramList.Filter filter) {
        synchronized (this.mLock) {
            ProgramList programList = this.mLegacyListProxy;
            if (programList != null) {
                programList.close();
                this.mLegacyListProxy = null;
            }
            this.mLegacyListFilter = null;
        }
        ProgramList list = new ProgramList();
        this.mCallback.setProgramListObserver(list, new ProgramList.OnCloseListener() { // from class: android.hardware.radio.TunerAdapter$$ExternalSyntheticLambda1
            @Override // android.hardware.radio.ProgramList.OnCloseListener
            public final void onClose() {
                TunerAdapter.this.lambda$getDynamicProgramList$1();
            }
        });
        try {
            this.mTuner.startProgramListUpdates(filter);
            return list;
        } catch (RemoteException ex) {
            this.mCallback.setProgramListObserver(null, new ProgramList.OnCloseListener() { // from class: android.hardware.radio.TunerAdapter$$ExternalSyntheticLambda2
                @Override // android.hardware.radio.ProgramList.OnCloseListener
                public final void onClose() {
                    Log.m108i(TunerAdapter.TAG, "Empty closeListener in programListObserver");
                }
            });
            throw new RuntimeException("Service died", ex);
        } catch (UnsupportedOperationException e) {
            Log.m108i(TAG, "Program list is not supported with this hardware");
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDynamicProgramList$1() {
        try {
            this.mTuner.stopProgramListUpdates();
        } catch (RemoteException ex) {
            Log.m109e(TAG, "Couldn't stop program list updates", ex);
        } catch (IllegalStateException ex2) {
            Log.m109e(TAG, "Tuner may already be closed", ex2);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean isAnalogForced() {
        try {
            return isConfigFlagSet(2);
        } catch (UnsupportedOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public void setAnalogForced(boolean isForced) {
        try {
            setConfigFlag(2, isForced);
        } catch (UnsupportedOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean isConfigFlagSupported(int flag) {
        try {
            return this.mTuner.isConfigFlagSupported(flag);
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean isConfigFlagSet(int flag) {
        try {
            return this.mTuner.isConfigFlagSet(flag);
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public void setConfigFlag(int flag, boolean value) {
        try {
            this.mTuner.setConfigFlag(flag, value);
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public Map<String, String> setParameters(Map<String, String> parameters) {
        try {
            return this.mTuner.setParameters((Map) Objects.requireNonNull(parameters, "Parameters cannot be null"));
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public Map<String, String> getParameters(List<String> keys) {
        try {
            return this.mTuner.getParameters((List) Objects.requireNonNull(keys, "Keys cannot be null"));
        } catch (RemoteException e) {
            throw new RuntimeException("Service died", e);
        }
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean isAntennaConnected() {
        return this.mCallback.isAntennaConnected();
    }

    @Override // android.hardware.radio.RadioTuner
    public boolean hasControl() {
        try {
            return !this.mTuner.isClosed();
        } catch (RemoteException e) {
            return false;
        }
    }
}
