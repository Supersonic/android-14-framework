package com.android.server.audio;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioSystem;
import android.media.ISoundDose;
import android.media.ISoundDoseCallback;
import android.media.SoundDoseRecord;
import android.os.Binder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.util.MathUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.server.audio.AudioService;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SoundDoseHelper {
    public final AlarmManager mAlarmManager;
    public final AudioService.AudioHandler mAudioHandler;
    public final AudioService mAudioService;
    public final Context mContext;
    public final boolean mEnableCsd;
    public int mMusicActiveMs;
    public StreamVolumeCommand mPendingVolumeCommand;
    public float mSafeMediaVolumeDbfs;
    public int mSafeMediaVolumeIndex;
    public int mSafeMediaVolumeState;
    public final SettingsAdapter mSettings;
    public final AudioService.ISafeHearingVolumeController mVolumeController;
    public final EventLogger mLogger = new EventLogger(30, "CSD updates");
    public int mMcc = 0;
    public final Object mSafeMediaVolumeStateLock = new Object();
    public final HashMap<Integer, SafeDeviceVolumeInfo> mSafeMediaVolumeDevices = new HashMap<Integer, SafeDeviceVolumeInfo>() { // from class: com.android.server.audio.SoundDoseHelper.1
        {
            put(4, new SafeDeviceVolumeInfo(4));
            put(8, new SafeDeviceVolumeInfo(8));
            put(67108864, new SafeDeviceVolumeInfo(67108864));
            put(536870912, new SafeDeviceVolumeInfo(536870912));
            put(536870914, new SafeDeviceVolumeInfo(536870914));
            put(134217728, new SafeDeviceVolumeInfo(134217728));
            put(256, new SafeDeviceVolumeInfo(256));
            put(128, new SafeDeviceVolumeInfo(128));
        }
    };
    public long mLastMusicActiveTimeMs = 0;
    public PendingIntent mMusicActiveIntent = null;
    public final Object mCsdStateLock = new Object();
    public final AtomicReference<ISoundDose> mSoundDose = new AtomicReference<>();
    @GuardedBy({"mCsdStateLock"})
    public float mCurrentCsd = 0.0f;
    @GuardedBy({"mCsdStateLock"})
    public long mLastMomentaryExposureTimeMs = -1;
    @GuardedBy({"mCsdStateLock"})
    public float mNextCsdWarning = 1.0f;
    @GuardedBy({"mCsdStateLock"})
    public final List<SoundDoseRecord> mDoseRecords = new ArrayList();
    @GuardedBy({"mCsdStateLock"})
    public long mGlobalTimeOffsetInSecs = -1;
    public final ISoundDoseCallback.Stub mSoundDoseCallback = new ISoundDoseCallback.Stub() { // from class: com.android.server.audio.SoundDoseHelper.2
        public void onMomentaryExposure(float f, int i) {
            boolean z;
            if (!SoundDoseHelper.this.mEnableCsd) {
                Log.w("AS.SoundDoseHelper", "onMomentaryExposure: csd not supported, ignoring callback");
                return;
            }
            Log.w("AS.SoundDoseHelper", "DeviceId " + i + " triggered momentary exposure with value: " + f);
            SoundDoseHelper.this.mLogger.enqueue(AudioServiceEvents$SoundDoseEvent.getMomentaryExposureEvent(f));
            synchronized (SoundDoseHelper.this.mCsdStateLock) {
                z = SoundDoseHelper.this.mLastMomentaryExposureTimeMs < 0 || System.currentTimeMillis() - SoundDoseHelper.this.mLastMomentaryExposureTimeMs >= 72000000;
                SoundDoseHelper.this.mLastMomentaryExposureTimeMs = System.currentTimeMillis();
            }
            if (z) {
                SoundDoseHelper.this.mVolumeController.postDisplayCsdWarning(3, SoundDoseHelper.this.getTimeoutMsForWarning(3));
            }
        }

        public void onNewCsdValue(float f, SoundDoseRecord[] soundDoseRecordArr) {
            if (!SoundDoseHelper.this.mEnableCsd) {
                Log.w("AS.SoundDoseHelper", "onNewCsdValue: csd not supported, ignoring value");
                return;
            }
            Log.i("AS.SoundDoseHelper", "onNewCsdValue: " + f);
            synchronized (SoundDoseHelper.this.mCsdStateLock) {
                if (SoundDoseHelper.this.mCurrentCsd < f) {
                    if (SoundDoseHelper.this.mCurrentCsd < SoundDoseHelper.this.mNextCsdWarning && f >= SoundDoseHelper.this.mNextCsdWarning) {
                        if (SoundDoseHelper.this.mNextCsdWarning == 5.0f) {
                            SoundDoseHelper.this.mVolumeController.postDisplayCsdWarning(2, SoundDoseHelper.this.getTimeoutMsForWarning(2));
                            SoundDoseHelper.this.mAudioService.postLowerVolumeToRs1();
                        } else {
                            SoundDoseHelper.this.mVolumeController.postDisplayCsdWarning(1, SoundDoseHelper.this.getTimeoutMsForWarning(1));
                        }
                        SoundDoseHelper.this.mNextCsdWarning += 1.0f;
                    }
                } else if (f < SoundDoseHelper.this.mNextCsdWarning - 1.0f && SoundDoseHelper.this.mNextCsdWarning >= 2.0f) {
                    SoundDoseHelper.this.mNextCsdWarning -= 1.0f;
                }
                SoundDoseHelper.this.mCurrentCsd = f;
                SoundDoseHelper.this.updateSoundDoseRecords_l(soundDoseRecordArr, f);
            }
        }
    };

    public static long convertToBootTime(long j, long j2) {
        return j - j2;
    }

    public static long convertToGlobalTime(long j, long j2) {
        return j + j2;
    }

    public static String safeMediaVolumeStateToString(int i) {
        if (i != 0) {
            if (i != 1) {
                if (i != 2) {
                    if (i != 3) {
                        return null;
                    }
                    return "SAFE_MEDIA_VOLUME_ACTIVE";
                }
                return "SAFE_MEDIA_VOLUME_INACTIVE";
            }
            return "SAFE_MEDIA_VOLUME_DISABLED";
        }
        return "SAFE_MEDIA_VOLUME_NOT_CONFIGURED";
    }

    /* loaded from: classes.dex */
    public static class SafeDeviceVolumeInfo {
        public int mDeviceType;
        public int mSafeVolumeIndex = -1;

        public SafeDeviceVolumeInfo(int i) {
            this.mDeviceType = i;
        }
    }

    public SoundDoseHelper(AudioService audioService, Context context, AudioService.AudioHandler audioHandler, SettingsAdapter settingsAdapter, AudioService.ISafeHearingVolumeController iSafeHearingVolumeController) {
        this.mAudioService = audioService;
        this.mAudioHandler = audioHandler;
        this.mSettings = settingsAdapter;
        this.mVolumeController = iSafeHearingVolumeController;
        this.mContext = context;
        this.mEnableCsd = context.getResources().getBoolean(17891372);
        initCsd();
        this.mSafeMediaVolumeState = settingsAdapter.getGlobalInt(audioService.getContentResolver(), "audio_safe_volume_state", 0);
        this.mSafeMediaVolumeIndex = context.getResources().getInteger(17694938) * 10;
        this.mAlarmManager = (AlarmManager) context.getSystemService("alarm");
    }

    public float getRs2Value() {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return 0.0f;
            }
            try {
                return iSoundDose.getOutputRs2();
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while getting the RS2 exposure value", e);
                return 0.0f;
            }
        }
        return 0.0f;
    }

    public void setRs2Value(float f) {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return;
            }
            try {
                iSoundDose.setOutputRs2(f);
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while setting the RS2 exposure value", e);
            }
        }
    }

    public float getCsd() {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return -1.0f;
            }
            try {
                return iSoundDose.getCsd();
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while getting the CSD value", e);
                return -1.0f;
            }
        }
        return -1.0f;
    }

    public void setCsd(float f) {
        SoundDoseRecord[] soundDoseRecordArr;
        if (this.mEnableCsd) {
            synchronized (this.mCsdStateLock) {
                this.mCurrentCsd = f;
                this.mDoseRecords.clear();
                if (this.mCurrentCsd > 0.0f) {
                    SoundDoseRecord soundDoseRecord = new SoundDoseRecord();
                    soundDoseRecord.timestamp = SystemClock.elapsedRealtime();
                    soundDoseRecord.value = f;
                    this.mDoseRecords.add(soundDoseRecord);
                }
                soundDoseRecordArr = (SoundDoseRecord[]) this.mDoseRecords.toArray(new SoundDoseRecord[0]);
            }
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return;
            }
            try {
                iSoundDose.resetCsd(f, soundDoseRecordArr);
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while setting the CSD value", e);
            }
        }
    }

    public void forceUseFrameworkMel(boolean z) {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return;
            }
            try {
                iSoundDose.forceUseFrameworkMel(z);
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while forcing the internal MEL computation", e);
            }
        }
    }

    public void forceComputeCsdOnAllDevices(boolean z) {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Sound dose interface not initialized");
                return;
            }
            try {
                iSoundDose.forceComputeCsdOnAllDevices(z);
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Exception while forcing CSD computation on all devices", e);
            }
        }
    }

    public boolean isCsdEnabled() {
        return this.mEnableCsd;
    }

    public int safeMediaVolumeIndex(int i) {
        SafeDeviceVolumeInfo safeDeviceVolumeInfo = this.mSafeMediaVolumeDevices.get(Integer.valueOf(i));
        if (safeDeviceVolumeInfo == null) {
            return AudioService.MAX_STREAM_VOLUME[3];
        }
        return safeDeviceVolumeInfo.mSafeVolumeIndex;
    }

    public void restoreMusicActiveMs() {
        synchronized (this.mSafeMediaVolumeStateLock) {
            this.mMusicActiveMs = MathUtils.constrain(this.mSettings.getSecureIntForUser(this.mAudioService.getContentResolver(), "unsafe_volume_music_active_ms", 0, -2), 0, 72000000);
        }
    }

    public void enforceSafeMediaVolumeIfActive(String str) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (this.mSafeMediaVolumeState == 3) {
                enforceSafeMediaVolume(str);
            }
        }
    }

    public void enforceSafeMediaVolume(String str) {
        AudioService.VolumeStreamState vssVolumeForStream = this.mAudioService.getVssVolumeForStream(3);
        for (SafeDeviceVolumeInfo safeDeviceVolumeInfo : this.mSafeMediaVolumeDevices.values()) {
            int index = vssVolumeForStream.getIndex(safeDeviceVolumeInfo.mDeviceType);
            int safeMediaVolumeIndex = safeMediaVolumeIndex(safeDeviceVolumeInfo.mDeviceType);
            if (index > safeMediaVolumeIndex) {
                vssVolumeForStream.setIndex(safeMediaVolumeIndex, safeDeviceVolumeInfo.mDeviceType, str, true);
                AudioService.AudioHandler audioHandler = this.mAudioHandler;
                audioHandler.sendMessageAtTime(audioHandler.obtainMessage(0, safeDeviceVolumeInfo.mDeviceType, 0, vssVolumeForStream), 0L);
            }
        }
    }

    public boolean checkSafeMediaVolume(int i, int i2, int i3) {
        boolean checkSafeMediaVolume_l;
        synchronized (this.mSafeMediaVolumeStateLock) {
            checkSafeMediaVolume_l = checkSafeMediaVolume_l(i, i2, i3);
        }
        return checkSafeMediaVolume_l;
    }

    @GuardedBy({"mSafeMediaVolumeStateLock"})
    public final boolean checkSafeMediaVolume_l(int i, int i2, int i3) {
        return this.mSafeMediaVolumeState == 3 && AudioService.mStreamVolumeAlias[i] == 3 && this.mSafeMediaVolumeDevices.containsKey(Integer.valueOf(i3)) && i2 > safeMediaVolumeIndex(i3);
    }

    public boolean willDisplayWarningAfterCheckVolume(int i, int i2, int i3, int i4) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (checkSafeMediaVolume_l(i, i2, i3)) {
                this.mVolumeController.postDisplaySafeVolumeWarning(i4);
                this.mPendingVolumeCommand = new StreamVolumeCommand(i, i2, i4, i3);
                return true;
            }
            return false;
        }
    }

    public void disableSafeMediaVolume(String str) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            setSafeMediaVolumeEnabled(false, str);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            StreamVolumeCommand streamVolumeCommand = this.mPendingVolumeCommand;
            if (streamVolumeCommand != null) {
                this.mAudioService.onSetStreamVolume(streamVolumeCommand.mStreamType, streamVolumeCommand.mIndex, streamVolumeCommand.mFlags, streamVolumeCommand.mDevice, str, true, true);
                this.mPendingVolumeCommand = null;
            }
        }
    }

    public void scheduleMusicActiveCheck() {
        synchronized (this.mSafeMediaVolumeStateLock) {
            cancelMusicActiveCheck();
            this.mMusicActiveIntent = PendingIntent.getBroadcast(this.mContext, 1, new Intent("com.android.server.audio.action.CHECK_MUSIC_ACTIVE"), 201326592);
            this.mAlarmManager.setExactAndAllowWhileIdle(2, SystemClock.elapsedRealtime() + 60000, this.mMusicActiveIntent);
        }
    }

    public void onCheckMusicActive(String str, boolean z) {
        synchronized (this.mSafeMediaVolumeStateLock) {
            if (this.mSafeMediaVolumeState == 2) {
                int deviceForStream = this.mAudioService.getDeviceForStream(3);
                if (this.mSafeMediaVolumeDevices.containsKey(Integer.valueOf(deviceForStream)) && z) {
                    scheduleMusicActiveCheck();
                    if (this.mAudioService.getVssVolumeForDevice(3, deviceForStream) > safeMediaVolumeIndex(deviceForStream)) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        long j = this.mLastMusicActiveTimeMs;
                        if (j != 0) {
                            this.mMusicActiveMs += (int) (elapsedRealtime - j);
                        }
                        this.mLastMusicActiveTimeMs = elapsedRealtime;
                        Log.i("AS.SoundDoseHelper", "onCheckMusicActive() mMusicActiveMs: " + this.mMusicActiveMs);
                        if (this.mMusicActiveMs > 72000000) {
                            setSafeMediaVolumeEnabled(true, str);
                            this.mMusicActiveMs = 0;
                        }
                        saveMusicActiveMs();
                    }
                } else {
                    cancelMusicActiveCheck();
                    this.mLastMusicActiveTimeMs = 0L;
                }
            }
        }
    }

    public void configureSafeMedia(boolean z, String str) {
        long j;
        if (z) {
            this.mAudioHandler.removeMessages(1001);
        }
        if (z) {
            j = SystemClock.uptimeMillis() + (SystemProperties.getBoolean("audio.safemedia.bypass", false) ? 0 : 30000);
        } else {
            j = 0;
        }
        AudioService.AudioHandler audioHandler = this.mAudioHandler;
        audioHandler.sendMessageAtTime(audioHandler.obtainMessage(1001, z ? 1 : 0, 0, str), j);
    }

    public void initSafeMediaVolumeIndex() {
        for (SafeDeviceVolumeInfo safeDeviceVolumeInfo : this.mSafeMediaVolumeDevices.values()) {
            safeDeviceVolumeInfo.mSafeVolumeIndex = getSafeDeviceMediaVolumeIndex(safeDeviceVolumeInfo.mDeviceType);
        }
    }

    public int getSafeMediaVolumeIndex(int i) {
        if (this.mSafeMediaVolumeState == 3 && this.mSafeMediaVolumeDevices.containsKey(Integer.valueOf(i))) {
            return safeMediaVolumeIndex(i);
        }
        return -1;
    }

    public boolean raiseVolumeDisplaySafeMediaVolume(int i, int i2, int i3, int i4) {
        if (checkSafeMediaVolume(i, i2, i3)) {
            this.mVolumeController.postDisplaySafeVolumeWarning(i4);
            return true;
        }
        return false;
    }

    public boolean safeDevicesContains(int i) {
        return this.mSafeMediaVolumeDevices.containsKey(Integer.valueOf(i));
    }

    public void invalidatPendingVolumeCommand() {
        synchronized (this.mSafeMediaVolumeStateLock) {
            this.mPendingVolumeCommand = null;
        }
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 1001:
                onConfigureSafeMedia(message.arg1 == 1, (String) message.obj);
                return;
            case 1002:
                onPersistSafeVolumeState(message.arg1);
                return;
            case 1003:
                this.mSettings.putSecureIntForUser(this.mAudioService.getContentResolver(), "unsafe_volume_music_active_ms", message.arg1, -2);
                return;
            case 1004:
                onPersistSoundDoseRecords();
                return;
            case 1005:
                int i = message.arg1;
                boolean z = message.arg2 == 1;
                AudioService.VolumeStreamState volumeStreamState = (AudioService.VolumeStreamState) message.obj;
                updateDoseAttenuation(volumeStreamState.getIndex(i), i, volumeStreamState.getStreamType(), z);
                return;
            default:
                Log.e("AS.SoundDoseHelper", "Unexpected msg to handle: " + message.what);
                return;
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("  mEnableCsd=");
        printWriter.println(this.mEnableCsd);
        if (this.mEnableCsd) {
            synchronized (this.mCsdStateLock) {
                printWriter.print("  mCurrentCsd=");
                printWriter.println(this.mCurrentCsd);
            }
        }
        printWriter.print("  mSafeMediaVolumeState=");
        printWriter.println(safeMediaVolumeStateToString(this.mSafeMediaVolumeState));
        printWriter.print("  mSafeMediaVolumeIndex=");
        printWriter.println(this.mSafeMediaVolumeIndex);
        for (SafeDeviceVolumeInfo safeDeviceVolumeInfo : this.mSafeMediaVolumeDevices.values()) {
            printWriter.print("  mSafeMediaVolumeIndex[");
            printWriter.print(safeDeviceVolumeInfo.mDeviceType);
            printWriter.print("]=");
            printWriter.println(safeDeviceVolumeInfo.mSafeVolumeIndex);
        }
        printWriter.print("  mSafeMediaVolumeDbfs=");
        printWriter.println(this.mSafeMediaVolumeDbfs);
        printWriter.print("  mMusicActiveMs=");
        printWriter.println(this.mMusicActiveMs);
        printWriter.print("  mMcc=");
        printWriter.println(this.mMcc);
        printWriter.print("  mPendingVolumeCommand=");
        printWriter.println(this.mPendingVolumeCommand);
        printWriter.println();
        this.mLogger.dump(printWriter);
        printWriter.println();
    }

    public void reset() {
        Log.d("AS.SoundDoseHelper", "Reset the sound dose helper");
        this.mSoundDose.set(AudioSystem.getSoundDoseInterface(this.mSoundDoseCallback));
        synchronized (this.mCsdStateLock) {
            try {
                ISoundDose iSoundDose = this.mSoundDose.get();
                if (iSoundDose != null && iSoundDose.asBinder().isBinderAlive() && this.mCurrentCsd != 0.0f) {
                    Log.d("AS.SoundDoseHelper", "Resetting the saved sound dose value " + this.mCurrentCsd);
                    iSoundDose.resetCsd(this.mCurrentCsd, (SoundDoseRecord[]) this.mDoseRecords.toArray(new SoundDoseRecord[0]));
                }
            } catch (RemoteException unused) {
            }
        }
    }

    public final void updateDoseAttenuation(int i, int i2, int i3, boolean z) {
        if (this.mEnableCsd) {
            ISoundDose iSoundDose = this.mSoundDose.get();
            if (iSoundDose == null) {
                Log.w("AS.SoundDoseHelper", "Can not apply attenuation. ISoundDose itf is null.");
                return;
            }
            try {
                if (!z) {
                    iSoundDose.updateAttenuation(0.0f, i2);
                } else if (AudioService.mStreamVolumeAlias[i3] == 3 && this.mSafeMediaVolumeDevices.containsKey(Integer.valueOf(i2))) {
                    iSoundDose.updateAttenuation(AudioSystem.getStreamVolumeDB(3, (i + 5) / 10, i2), i2);
                }
            } catch (RemoteException e) {
                Log.e("AS.SoundDoseHelper", "Could not apply the attenuation for MEL calculation with volume index " + i, e);
            }
        }
    }

    public final void initCsd() {
        if (this.mEnableCsd) {
            Log.v("AS.SoundDoseHelper", "Initializing sound dose");
            synchronized (this.mCsdStateLock) {
                if (this.mGlobalTimeOffsetInSecs == -1) {
                    this.mGlobalTimeOffsetInSecs = System.currentTimeMillis() / 1000;
                }
                float f = this.mCurrentCsd;
                float parseGlobalSettingFloat = parseGlobalSettingFloat("audio_safe_csd_current_value", 0.0f);
                this.mCurrentCsd = parseGlobalSettingFloat;
                if (parseGlobalSettingFloat != f) {
                    this.mNextCsdWarning = parseGlobalSettingFloat("audio_safe_csd_next_warning", 1.0f);
                    List<SoundDoseRecord> persistedStringToRecordList = persistedStringToRecordList(this.mSettings.getGlobalString(this.mAudioService.getContentResolver(), "audio_safe_csd_dose_records"), this.mGlobalTimeOffsetInSecs);
                    if (persistedStringToRecordList != null) {
                        this.mDoseRecords.addAll(persistedStringToRecordList);
                    }
                }
            }
            reset();
        }
    }

    public final void onConfigureSafeMedia(boolean z, String str) {
        boolean z2;
        synchronized (this.mSafeMediaVolumeStateLock) {
            int i = this.mContext.getResources().getConfiguration().mcc;
            int i2 = this.mMcc;
            if (i2 != i || (i2 == 0 && z)) {
                this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(17694938) * 10;
                initSafeMediaVolumeIndex();
                int i3 = 1;
                if (!SystemProperties.getBoolean("audio.safemedia.force", false) && !this.mContext.getResources().getBoolean(17891779)) {
                    z2 = false;
                    boolean z3 = SystemProperties.getBoolean("audio.safemedia.bypass", false);
                    if (!z2 && !z3) {
                        if (this.mSafeMediaVolumeState != 2) {
                            if (this.mMusicActiveMs == 0) {
                                this.mSafeMediaVolumeState = 3;
                                enforceSafeMediaVolume(str);
                            } else {
                                this.mSafeMediaVolumeState = 2;
                                this.mLastMusicActiveTimeMs = 0L;
                            }
                        }
                        i3 = 3;
                    } else {
                        this.mSafeMediaVolumeState = 1;
                    }
                    this.mMcc = i;
                    AudioService.AudioHandler audioHandler = this.mAudioHandler;
                    audioHandler.sendMessageAtTime(audioHandler.obtainMessage(1002, i3, 0, null), 0L);
                }
                z2 = true;
                boolean z32 = SystemProperties.getBoolean("audio.safemedia.bypass", false);
                if (!z2) {
                }
                this.mSafeMediaVolumeState = 1;
                this.mMcc = i;
                AudioService.AudioHandler audioHandler2 = this.mAudioHandler;
                audioHandler2.sendMessageAtTime(audioHandler2.obtainMessage(1002, i3, 0, null), 0L);
            }
        }
    }

    public final int getTimeoutMsForWarning(int i) {
        if (i != 1) {
            int i2 = 5000;
            if (i != 2 && i != 3) {
                i2 = -1;
                if (i != 4) {
                    Log.e("AS.SoundDoseHelper", "Invalid CSD warning " + i, new Exception());
                }
            }
            return i2;
        }
        return 7000;
    }

    @GuardedBy({"mSafeMediaVolumeStateLock"})
    public final void setSafeMediaVolumeEnabled(boolean z, String str) {
        int i = this.mSafeMediaVolumeState;
        if (i == 0 || i == 1) {
            return;
        }
        if (z && i == 2) {
            this.mSafeMediaVolumeState = 3;
            enforceSafeMediaVolume(str);
        } else if (z || i != 3) {
        } else {
            this.mSafeMediaVolumeState = 2;
            this.mMusicActiveMs = 1;
            this.mLastMusicActiveTimeMs = 0L;
            saveMusicActiveMs();
            scheduleMusicActiveCheck();
        }
    }

    @GuardedBy({"mSafeMediaVolumeStateLock"})
    public final void cancelMusicActiveCheck() {
        PendingIntent pendingIntent = this.mMusicActiveIntent;
        if (pendingIntent != null) {
            this.mAlarmManager.cancel(pendingIntent);
            this.mMusicActiveIntent = null;
        }
    }

    @GuardedBy({"mSafeMediaVolumeStateLock"})
    public final void saveMusicActiveMs() {
        this.mAudioHandler.obtainMessage(1003, this.mMusicActiveMs, 0).sendToTarget();
    }

    public final int getSafeDeviceMediaVolumeIndex(int i) {
        if ((i == 8 || i == 4) && !this.mEnableCsd) {
            return this.mSafeMediaVolumeIndex;
        }
        int i2 = AudioService.MIN_STREAM_VOLUME[3];
        int i3 = AudioService.MAX_STREAM_VOLUME[3];
        this.mSafeMediaVolumeDbfs = this.mContext.getResources().getInteger(17694939) / 100.0f;
        while (true) {
            if (Math.abs(i3 - i2) <= 1) {
                break;
            }
            int i4 = (i3 + i2) / 2;
            float streamVolumeDB = AudioSystem.getStreamVolumeDB(3, i4, i);
            if (Float.isNaN(streamVolumeDB)) {
                break;
            }
            float f = this.mSafeMediaVolumeDbfs;
            if (streamVolumeDB == f) {
                i2 = i4;
                break;
            } else if (streamVolumeDB < f) {
                i2 = i4;
            } else {
                i3 = i4;
            }
        }
        return i2 * 10;
    }

    public final void onPersistSafeVolumeState(int i) {
        this.mSettings.putGlobalInt(this.mAudioService.getContentResolver(), "audio_safe_volume_state", i);
    }

    @GuardedBy({"mCsdStateLock"})
    public final void updateSoundDoseRecords_l(SoundDoseRecord[] soundDoseRecordArr, float f) {
        long j = 0;
        for (final SoundDoseRecord soundDoseRecord : soundDoseRecordArr) {
            Log.i("AS.SoundDoseHelper", "  new record: " + soundDoseRecord);
            j += (long) soundDoseRecord.duration;
            if (soundDoseRecord.value < 0.0f) {
                if (!this.mDoseRecords.removeIf(new Predicate() { // from class: com.android.server.audio.SoundDoseHelper$$ExternalSyntheticLambda2
                    @Override // java.util.function.Predicate
                    public final boolean test(Object obj) {
                        boolean lambda$updateSoundDoseRecords_l$0;
                        lambda$updateSoundDoseRecords_l$0 = SoundDoseHelper.lambda$updateSoundDoseRecords_l$0(soundDoseRecord, (SoundDoseRecord) obj);
                        return lambda$updateSoundDoseRecords_l$0;
                    }
                })) {
                    Log.w("AS.SoundDoseHelper", "Could not find cached record to remove: " + soundDoseRecord);
                }
            } else {
                this.mDoseRecords.add(soundDoseRecord);
            }
        }
        AudioService.AudioHandler audioHandler = this.mAudioHandler;
        audioHandler.sendMessageAtTime(audioHandler.obtainMessage(1004, 0, 0, null), 0L);
        this.mLogger.enqueue(AudioServiceEvents$SoundDoseEvent.getDoseUpdateEvent(f, j));
    }

    public static /* synthetic */ boolean lambda$updateSoundDoseRecords_l$0(SoundDoseRecord soundDoseRecord, SoundDoseRecord soundDoseRecord2) {
        return soundDoseRecord2.value == (-soundDoseRecord.value) && soundDoseRecord2.timestamp == soundDoseRecord.timestamp && soundDoseRecord2.averageMel == soundDoseRecord.averageMel && soundDoseRecord2.duration == soundDoseRecord.duration;
    }

    public final void onPersistSoundDoseRecords() {
        synchronized (this.mCsdStateLock) {
            if (this.mGlobalTimeOffsetInSecs == -1) {
                this.mGlobalTimeOffsetInSecs = System.currentTimeMillis() / 1000;
            }
            this.mSettings.putGlobalString(this.mAudioService.getContentResolver(), "audio_safe_csd_current_value", Float.toString(this.mCurrentCsd));
            this.mSettings.putGlobalString(this.mAudioService.getContentResolver(), "audio_safe_csd_next_warning", Float.toString(this.mNextCsdWarning));
            this.mSettings.putGlobalString(this.mAudioService.getContentResolver(), "audio_safe_csd_dose_records", (String) this.mDoseRecords.stream().map(new Function() { // from class: com.android.server.audio.SoundDoseHelper$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String lambda$onPersistSoundDoseRecords$1;
                    lambda$onPersistSoundDoseRecords$1 = SoundDoseHelper.this.lambda$onPersistSoundDoseRecords$1((SoundDoseRecord) obj);
                    return lambda$onPersistSoundDoseRecords$1;
                }
            }).collect(Collectors.joining("|")));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onPersistSoundDoseRecords$1(SoundDoseRecord soundDoseRecord) {
        return recordToPersistedString(soundDoseRecord, this.mGlobalTimeOffsetInSecs);
    }

    public static String recordToPersistedString(SoundDoseRecord soundDoseRecord, long j) {
        return convertToGlobalTime(soundDoseRecord.timestamp, j) + "," + soundDoseRecord.duration + "," + soundDoseRecord.value + "," + soundDoseRecord.averageMel;
    }

    public static List<SoundDoseRecord> persistedStringToRecordList(String str, final long j) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return (List) Arrays.stream(TextUtils.split(str, "\\|")).map(new Function() { // from class: com.android.server.audio.SoundDoseHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                SoundDoseRecord persistedStringToRecord;
                persistedStringToRecord = SoundDoseHelper.persistedStringToRecord((String) obj, j);
                return persistedStringToRecord;
            }
        }).filter(new Predicate() { // from class: com.android.server.audio.SoundDoseHelper$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((SoundDoseRecord) obj);
            }
        }).collect(Collectors.toList());
    }

    public static SoundDoseRecord persistedStringToRecord(String str, long j) {
        if (str != null && !str.isEmpty()) {
            String[] split = TextUtils.split(str, ",");
            if (split.length != 4) {
                Log.w("AS.SoundDoseHelper", "Expecting 4 fields for a SoundDoseRecord, parsed " + split.length);
                return null;
            }
            SoundDoseRecord soundDoseRecord = new SoundDoseRecord();
            try {
                soundDoseRecord.timestamp = convertToBootTime(Long.parseLong(split[0]), j);
                soundDoseRecord.duration = Integer.parseInt(split[1]);
                soundDoseRecord.value = Float.parseFloat(split[2]);
                soundDoseRecord.averageMel = Float.parseFloat(split[3]);
                return soundDoseRecord;
            } catch (NumberFormatException e) {
                Log.e("AS.SoundDoseHelper", "Unable to parse persisted SoundDoseRecord: " + str, e);
            }
        }
        return null;
    }

    public final float parseGlobalSettingFloat(String str, float f) {
        String globalString = this.mSettings.getGlobalString(this.mAudioService.getContentResolver(), str);
        if (globalString == null || globalString.isEmpty()) {
            Log.v("AS.SoundDoseHelper", "No value stored in settings " + str);
            return f;
        }
        try {
            return Float.parseFloat(globalString);
        } catch (NumberFormatException e) {
            Log.e("AS.SoundDoseHelper", "Error parsing float from settings " + str, e);
            return f;
        }
    }

    /* loaded from: classes.dex */
    public static class StreamVolumeCommand {
        public final int mDevice;
        public final int mFlags;
        public final int mIndex;
        public final int mStreamType;

        public StreamVolumeCommand(int i, int i2, int i3, int i4) {
            this.mStreamType = i;
            this.mIndex = i2;
            this.mFlags = i3;
            this.mDevice = i4;
        }

        public String toString() {
            return "{streamType=" + this.mStreamType + ",index=" + this.mIndex + ",flags=" + this.mFlags + ",device=" + this.mDevice + '}';
        }
    }
}
