package android.hardware.hdmi;

import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.util.Log;
/* loaded from: classes2.dex */
public final class HdmiAudioSystemClient extends HdmiClient {
    private static final int REPORT_AUDIO_STATUS_INTERVAL_MS = 500;
    private static final String TAG = "HdmiAudioSystemClient";
    private boolean mCanSendAudioStatus;
    private final Handler mHandler;
    private boolean mLastIsMute;
    private int mLastMaxVolume;
    private int mLastVolume;
    private boolean mPendingReportAudioStatus;

    /* loaded from: classes2.dex */
    public interface SetSystemAudioModeCallback {
        void onComplete(int i);
    }

    public HdmiAudioSystemClient(IHdmiControlService service) {
        this(service, null);
    }

    public HdmiAudioSystemClient(IHdmiControlService service, Handler handler) {
        super(service);
        this.mCanSendAudioStatus = true;
        this.mHandler = handler == null ? new Handler(Looper.getMainLooper()) : handler;
    }

    @Override // android.hardware.hdmi.HdmiClient
    public int getDeviceType() {
        return 5;
    }

    public void sendReportAudioStatusCecCommand(boolean isMuteAdjust, int volume, int maxVolume, boolean isMute) {
        if (isMuteAdjust) {
            try {
                this.mService.reportAudioStatus(getDeviceType(), volume, maxVolume, isMute);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mLastVolume = volume;
        this.mLastMaxVolume = maxVolume;
        this.mLastIsMute = isMute;
        if (this.mCanSendAudioStatus) {
            try {
                this.mService.reportAudioStatus(getDeviceType(), volume, maxVolume, isMute);
                this.mCanSendAudioStatus = false;
                this.mHandler.postDelayed(new Runnable() { // from class: android.hardware.hdmi.HdmiAudioSystemClient.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (HdmiAudioSystemClient.this.mPendingReportAudioStatus) {
                            try {
                                try {
                                    HdmiAudioSystemClient.this.mService.reportAudioStatus(HdmiAudioSystemClient.this.getDeviceType(), HdmiAudioSystemClient.this.mLastVolume, HdmiAudioSystemClient.this.mLastMaxVolume, HdmiAudioSystemClient.this.mLastIsMute);
                                    HdmiAudioSystemClient.this.mHandler.postDelayed(this, 500L);
                                } catch (RemoteException e2) {
                                    HdmiAudioSystemClient.this.mCanSendAudioStatus = true;
                                }
                                return;
                            } finally {
                                HdmiAudioSystemClient.this.mPendingReportAudioStatus = false;
                            }
                        }
                        HdmiAudioSystemClient.this.mCanSendAudioStatus = true;
                    }
                }, 500L);
                return;
            } catch (RemoteException e2) {
                return;
            }
        }
        this.mPendingReportAudioStatus = true;
    }

    public void setSystemAudioMode(boolean state, SetSystemAudioModeCallback callback) {
    }

    public void setSystemAudioModeOnForAudioOnlySource() {
        try {
            this.mService.setSystemAudioModeOnForAudioOnlySource();
        } catch (RemoteException e) {
            Log.m112d(TAG, "Failed to set System Audio Mode on for Audio Only source");
        }
    }
}
