package com.android.server.hdmi;

import android.media.AudioDescriptor;
import android.media.AudioDeviceAttributes;
import android.os.Handler;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class HdmiEarcLocalDeviceTx extends HdmiEarcLocalDevice {
    public Handler mReportCapsHandler;
    public ReportCapsRunnable mReportCapsRunnable;

    public HdmiEarcLocalDeviceTx(HdmiControlService hdmiControlService) {
        super(hdmiControlService, 0);
        synchronized (this.mLock) {
            this.mEarcStatus = 1;
        }
        this.mReportCapsHandler = new Handler(hdmiControlService.getServiceLooper());
        this.mReportCapsRunnable = new ReportCapsRunnable();
    }

    @Override // com.android.server.hdmi.HdmiEarcLocalDevice
    public void handleEarcStateChange(int i) {
        int i2;
        synchronized (this.mLock) {
            HdmiLogger.debug("HdmiEarcLocalDeviceTx", "eARC state change [old:%b new %b]", Integer.valueOf(this.mEarcStatus), Integer.valueOf(i));
            i2 = this.mEarcStatus;
            this.mEarcStatus = i;
        }
        this.mReportCapsHandler.removeCallbacksAndMessages(null);
        if (i == 0) {
            notifyEarcStatusToAudioService(false, new ArrayList());
            this.mService.startArcAction(false, null);
        } else if (i == 2) {
            notifyEarcStatusToAudioService(false, new ArrayList());
            this.mService.startArcAction(true, null);
        } else if (i == 1 && i2 == 2) {
            this.mService.startArcAction(false, null);
        } else if (i == 3) {
            if (i2 == 2) {
                this.mService.startArcAction(false, null);
            }
            this.mReportCapsHandler.postDelayed(this.mReportCapsRunnable, 2000L);
        }
    }

    @Override // com.android.server.hdmi.HdmiEarcLocalDevice
    public void handleEarcCapabilitiesReported(byte[] bArr) {
        synchronized (this.mLock) {
            if (this.mEarcStatus == 3 && this.mReportCapsHandler.hasCallbacks(this.mReportCapsRunnable)) {
                this.mReportCapsHandler.removeCallbacksAndMessages(null);
                notifyEarcStatusToAudioService(true, parseCapabilities(bArr));
            }
        }
    }

    public final void notifyEarcStatusToAudioService(boolean z, List<AudioDescriptor> list) {
        this.mService.getAudioManager().setWiredDeviceConnectionState(new AudioDeviceAttributes(2, 29, "", "", new ArrayList(), list), z ? 1 : 0);
    }

    /* loaded from: classes.dex */
    public class ReportCapsRunnable implements Runnable {
        public ReportCapsRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            synchronized (HdmiEarcLocalDeviceTx.this.mLock) {
                HdmiEarcLocalDeviceTx hdmiEarcLocalDeviceTx = HdmiEarcLocalDeviceTx.this;
                if (hdmiEarcLocalDeviceTx.mEarcStatus == 3) {
                    hdmiEarcLocalDeviceTx.notifyEarcStatusToAudioService(true, new ArrayList());
                }
            }
        }
    }

    public final List<AudioDescriptor> parseCapabilities(byte[] bArr) {
        ArrayList arrayList = new ArrayList();
        if (bArr.length < 4) {
            Slog.i("HdmiEarcLocalDeviceTx", "Raw eARC capabilities array doesn´t contain any blocks.");
            return arrayList;
        }
        int i = bArr[2];
        if (bArr.length < i) {
            Slog.i("HdmiEarcLocalDeviceTx", "Raw eARC capabilities array is shorter than the reported payload length.");
            return arrayList;
        }
        int i2 = 3;
        while (i2 < i) {
            int i3 = bArr[i2];
            int i4 = (i3 & 224) >> 5;
            int i5 = i3 & 31;
            if (i5 == 0) {
                break;
            }
            if (i4 == 1) {
                int i6 = i5 % 3;
                if (i6 != 0) {
                    Slog.e("HdmiEarcLocalDeviceTx", "Invalid length of SAD block: expected a factor of 3 but got " + i6);
                } else {
                    byte[] bArr2 = new byte[i5];
                    System.arraycopy(bArr, i2 + 1, bArr2, 0, i5);
                    int i7 = 0;
                    while (i7 < i5) {
                        int i8 = i7 + 3;
                        arrayList.add(new AudioDescriptor(1, 0, Arrays.copyOfRange(bArr2, i7, i8)));
                        i7 = i8;
                    }
                }
            } else if (i4 == 4) {
                int i9 = i5 + 1;
                byte[] bArr3 = new byte[i9];
                System.arraycopy(bArr, i2, bArr3, 0, i9);
                arrayList.add(new AudioDescriptor(2, 0, bArr3));
            } else if (i4 == 7) {
                if (bArr[i2 + 1] == 17) {
                    int i10 = i5 + 1;
                    byte[] bArr4 = new byte[i10];
                    System.arraycopy(bArr, i2, bArr4, 0, i10);
                    arrayList.add(new AudioDescriptor(3, 0, bArr4));
                }
            } else {
                Slog.w("HdmiEarcLocalDeviceTx", "This tagcode was not handled: " + i4);
            }
            i2 += i5 + 1;
        }
        return arrayList;
    }

    @Override // com.android.server.hdmi.HdmiEarcLocalDevice
    public void dump(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("TX, mEarcStatus: " + this.mEarcStatus);
        }
    }
}
