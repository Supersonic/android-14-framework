package com.android.server.audio;

import android.media.AudioAttributes;
import android.media.AudioFocusInfo;
import android.media.IAudioFocusDispatcher;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.audio.MediaFocusControl;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
/* loaded from: classes.dex */
public class FocusRequester {
    public final AudioAttributes mAttributes;
    public final int mCallingUid;
    public final String mClientId;
    public MediaFocusControl.AudioFocusDeathHandler mDeathHandler;
    public final MediaFocusControl mFocusController;
    public IAudioFocusDispatcher mFocusDispatcher;
    public final int mFocusGainRequest;
    public final int mGrantFlags;
    public final String mPackageName;
    public final int mSdkTarget;
    public final IBinder mSourceRef;
    public int mFocusLossReceived = 0;
    public boolean mFocusLossWasNotified = true;
    public boolean mFocusLossFadeLimbo = false;

    public FocusRequester(AudioAttributes audioAttributes, int i, int i2, IAudioFocusDispatcher iAudioFocusDispatcher, IBinder iBinder, String str, MediaFocusControl.AudioFocusDeathHandler audioFocusDeathHandler, String str2, int i3, MediaFocusControl mediaFocusControl, int i4) {
        this.mAttributes = audioAttributes;
        this.mFocusDispatcher = iAudioFocusDispatcher;
        this.mSourceRef = iBinder;
        this.mClientId = str;
        this.mDeathHandler = audioFocusDeathHandler;
        this.mPackageName = str2;
        this.mCallingUid = i3;
        this.mFocusGainRequest = i;
        this.mGrantFlags = i2;
        this.mFocusController = mediaFocusControl;
        this.mSdkTarget = i4;
    }

    public FocusRequester(AudioFocusInfo audioFocusInfo, IAudioFocusDispatcher iAudioFocusDispatcher, IBinder iBinder, MediaFocusControl.AudioFocusDeathHandler audioFocusDeathHandler, MediaFocusControl mediaFocusControl) {
        this.mAttributes = audioFocusInfo.getAttributes();
        this.mClientId = audioFocusInfo.getClientId();
        this.mPackageName = audioFocusInfo.getPackageName();
        this.mCallingUid = audioFocusInfo.getClientUid();
        this.mFocusGainRequest = audioFocusInfo.getGainRequest();
        this.mGrantFlags = audioFocusInfo.getFlags();
        this.mSdkTarget = audioFocusInfo.getSdkTarget();
        this.mFocusDispatcher = iAudioFocusDispatcher;
        this.mSourceRef = iBinder;
        this.mDeathHandler = audioFocusDeathHandler;
        this.mFocusController = mediaFocusControl;
    }

    public boolean hasSameClient(String str) {
        return this.mClientId.compareTo(str) == 0;
    }

    public boolean isLockedFocusOwner() {
        return (this.mGrantFlags & 4) != 0;
    }

    public boolean isInFocusLossLimbo() {
        return this.mFocusLossFadeLimbo;
    }

    public boolean hasSameBinder(IBinder iBinder) {
        IBinder iBinder2 = this.mSourceRef;
        return iBinder2 != null && iBinder2.equals(iBinder);
    }

    public boolean hasSameDispatcher(IAudioFocusDispatcher iAudioFocusDispatcher) {
        IAudioFocusDispatcher iAudioFocusDispatcher2 = this.mFocusDispatcher;
        return iAudioFocusDispatcher2 != null && iAudioFocusDispatcher2.equals(iAudioFocusDispatcher);
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public boolean hasSamePackage(String str) {
        return this.mPackageName.compareTo(str) == 0;
    }

    public boolean hasSameUid(int i) {
        return this.mCallingUid == i;
    }

    public int getClientUid() {
        return this.mCallingUid;
    }

    public String getClientId() {
        return this.mClientId;
    }

    public int getGainRequest() {
        return this.mFocusGainRequest;
    }

    public int getGrantFlags() {
        return this.mGrantFlags;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAttributes;
    }

    public int getSdkTarget() {
        return this.mSdkTarget;
    }

    public static String focusChangeToString(int i) {
        switch (i) {
            case -3:
                return "LOSS_TRANSIENT_CAN_DUCK";
            case -2:
                return "LOSS_TRANSIENT";
            case -1:
                return "LOSS";
            case 0:
                return "none";
            case 1:
                return "GAIN";
            case 2:
                return "GAIN_TRANSIENT";
            case 3:
                return "GAIN_TRANSIENT_MAY_DUCK";
            case 4:
                return "GAIN_TRANSIENT_EXCLUSIVE";
            default:
                return "[invalid focus change" + i + "]";
        }
    }

    public final String focusGainToString() {
        return focusChangeToString(this.mFocusGainRequest);
    }

    public final String focusLossToString() {
        return focusChangeToString(this.mFocusLossReceived);
    }

    public static String flagsToString(int i) {
        String str = new String();
        if ((i & 1) != 0) {
            str = str + "DELAY_OK";
        }
        if ((i & 4) != 0) {
            if (!str.isEmpty()) {
                str = str + "|";
            }
            str = str + "LOCK";
        }
        if ((i & 2) != 0) {
            if (!str.isEmpty()) {
                str = str + "|";
            }
            return str + "PAUSES_ON_DUCKABLE_LOSS";
        }
        return str;
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("  source:" + this.mSourceRef + " -- pack: " + this.mPackageName + " -- client: " + this.mClientId + " -- gain: " + focusGainToString() + " -- flags: " + flagsToString(this.mGrantFlags) + " -- loss: " + focusLossToString() + " -- notified: " + this.mFocusLossWasNotified + " -- limbo" + this.mFocusLossFadeLimbo + " -- uid: " + this.mCallingUid + " -- attr: " + this.mAttributes + " -- sdk:" + this.mSdkTarget);
    }

    public void maybeRelease() {
        if (this.mFocusLossFadeLimbo) {
            return;
        }
        release();
    }

    public void release() {
        IBinder iBinder = this.mSourceRef;
        MediaFocusControl.AudioFocusDeathHandler audioFocusDeathHandler = this.mDeathHandler;
        if (iBinder != null && audioFocusDeathHandler != null) {
            try {
                iBinder.unlinkToDeath(audioFocusDeathHandler, 0);
            } catch (NoSuchElementException unused) {
            }
        }
        this.mDeathHandler = null;
        this.mFocusDispatcher = null;
    }

    public void finalize() throws Throwable {
        release();
        super.finalize();
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0022, code lost:
        if (r0 != 0) goto L12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x002c, code lost:
        if (r4 != 0) goto L9;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x002e, code lost:
        android.util.Log.e("MediaFocusControl", "focusLossForGainRequest() for invalid focus request " + r5);
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0045, code lost:
        return 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:9:0x000d, code lost:
        if (r5 != 4) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int focusLossForGainRequest(int i) {
        if (i == 1) {
            int i2 = this.mFocusLossReceived;
            if (i2 == -3 || i2 == -2 || i2 == -1 || i2 == 0) {
                return -1;
            }
        } else if (i != 2) {
            if (i != 3) {
            }
            int i3 = this.mFocusLossReceived;
            if (i3 != -3) {
                if (i3 == -2) {
                    return -2;
                }
                if (i3 == -1) {
                    return -1;
                }
            }
            return -3;
        }
        int i4 = this.mFocusLossReceived;
        if (i4 != -3 && i4 != -2) {
            if (i4 == -1) {
                return -1;
            }
        }
        return -2;
    }

    @GuardedBy({"MediaFocusControl.mAudioFocusLock"})
    public boolean handleFocusLossFromGain(int i, FocusRequester focusRequester, boolean z) {
        int focusLossForGainRequest = focusLossForGainRequest(i);
        handleFocusLoss(focusLossForGainRequest, focusRequester, z);
        return focusLossForGainRequest == -1;
    }

    @GuardedBy({"MediaFocusControl.mAudioFocusLock"})
    public void handleFocusGain(int i) {
        try {
            this.mFocusLossReceived = 0;
            this.mFocusLossFadeLimbo = false;
            this.mFocusController.notifyExtPolicyFocusGrant_syncAf(toAudioFocusInfo(), 1);
            IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
            if (iAudioFocusDispatcher != null && this.mFocusLossWasNotified) {
                iAudioFocusDispatcher.dispatchAudioFocusChange(i, this.mClientId);
            }
            this.mFocusController.restoreVShapedPlayers(this);
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "Failure to signal gain of audio focus due to: ", e);
        }
    }

    @GuardedBy({"MediaFocusControl.mAudioFocusLock"})
    public void handleFocusGainFromRequest(int i) {
        if (i == 1) {
            this.mFocusController.restoreVShapedPlayers(this);
        }
    }

    @GuardedBy({"MediaFocusControl.mAudioFocusLock"})
    public void handleFocusLoss(int i, FocusRequester focusRequester, boolean z) {
        try {
            if (i != this.mFocusLossReceived) {
                this.mFocusLossReceived = i;
                this.mFocusLossWasNotified = false;
                if (!this.mFocusController.mustNotifyFocusOwnerOnDuck() && this.mFocusLossReceived == -3 && (this.mGrantFlags & 2) == 0) {
                    this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), false);
                    return;
                }
                if (focusRequester != null ? frameworkHandleFocusLoss(i, focusRequester, z) : false) {
                    this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), false);
                    return;
                }
                IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
                if (iAudioFocusDispatcher != null) {
                    this.mFocusController.notifyExtPolicyFocusLoss_syncAf(toAudioFocusInfo(), true);
                    this.mFocusLossWasNotified = true;
                    iAudioFocusDispatcher.dispatchAudioFocusChange(this.mFocusLossReceived, this.mClientId);
                }
            }
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "Failure to signal loss of audio focus due to:", e);
        }
    }

    @GuardedBy({"MediaFocusControl.mAudioFocusLock"})
    public final boolean frameworkHandleFocusLoss(int i, FocusRequester focusRequester, boolean z) {
        if (focusRequester.mCallingUid == this.mCallingUid) {
            return false;
        }
        if (i == -3) {
            if (!z && (this.mGrantFlags & 2) != 0) {
                Log.v("MediaFocusControl", "not ducking uid " + this.mCallingUid + " - flags");
                return false;
            } else if (!z && getSdkTarget() <= 25) {
                Log.v("MediaFocusControl", "not ducking uid " + this.mCallingUid + " - old SDK");
                return false;
            } else {
                return this.mFocusController.duckPlayers(focusRequester, this, z);
            }
        } else if (i == -1 && this.mFocusController.fadeOutPlayers(focusRequester, this)) {
            this.mFocusLossFadeLimbo = true;
            this.mFocusController.postDelayedLossAfterFade(this, 2000L);
            return true;
        } else {
            return false;
        }
    }

    public int dispatchFocusChange(int i) {
        IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
        if (iAudioFocusDispatcher == null || i == 0) {
            return 0;
        }
        if ((i == 3 || i == 4 || i == 2 || i == 1) && this.mFocusGainRequest != i) {
            Log.w("MediaFocusControl", "focus gain was requested with " + this.mFocusGainRequest + ", dispatching " + i);
        } else if (i == -3 || i == -2 || i == -1) {
            this.mFocusLossReceived = i;
        }
        try {
            iAudioFocusDispatcher.dispatchAudioFocusChange(i, this.mClientId);
            return 1;
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "dispatchFocusChange: error talking to focus listener " + this.mClientId, e);
            return 0;
        }
    }

    public void dispatchFocusResultFromExtPolicy(int i) {
        IAudioFocusDispatcher iAudioFocusDispatcher = this.mFocusDispatcher;
        if (iAudioFocusDispatcher == null) {
            return;
        }
        try {
            iAudioFocusDispatcher.dispatchFocusResultFromExtPolicy(i, this.mClientId);
        } catch (RemoteException e) {
            Log.e("MediaFocusControl", "dispatchFocusResultFromExtPolicy: error talking to focus listener" + this.mClientId, e);
        }
    }

    public AudioFocusInfo toAudioFocusInfo() {
        return new AudioFocusInfo(this.mAttributes, this.mCallingUid, this.mClientId, this.mPackageName, this.mFocusGainRequest, this.mFocusLossReceived, this.mGrantFlags, this.mSdkTarget);
    }
}
