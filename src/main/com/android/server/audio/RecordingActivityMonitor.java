package com.android.server.audio;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceInfo;
import android.media.AudioFormat;
import android.media.AudioRecordingConfiguration;
import android.media.AudioSystem;
import android.media.IRecordingConfigDispatcher;
import android.media.MediaRecorder;
import android.media.audiofx.AudioEffect;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public final class RecordingActivityMonitor implements AudioSystem.AudioRecordingCallback {
    public static final EventLogger sEventLogger = new EventLogger(50, "recording activity received by AudioService");
    public final PackageManager mPackMan;
    public ArrayList<RecMonitorClient> mClients = new ArrayList<>();
    public boolean mHasPublicClients = false;
    public AtomicInteger mLegacyRemoteSubmixRiid = new AtomicInteger(-1);
    public AtomicBoolean mLegacyRemoteSubmixActive = new AtomicBoolean(false);
    public List<RecordingState> mRecordStates = new ArrayList();

    /* loaded from: classes.dex */
    public static final class RecordingState {
        public AudioRecordingConfiguration mConfig;
        public final RecorderDeathHandler mDeathHandler;
        public boolean mIsActive;
        public final int mRiid;

        public RecordingState(int i, RecorderDeathHandler recorderDeathHandler) {
            this.mRiid = i;
            this.mDeathHandler = recorderDeathHandler;
        }

        public RecordingState(AudioRecordingConfiguration audioRecordingConfiguration) {
            this.mRiid = -1;
            this.mDeathHandler = null;
            this.mConfig = audioRecordingConfiguration;
        }

        public int getRiid() {
            return this.mRiid;
        }

        public int getPortId() {
            AudioRecordingConfiguration audioRecordingConfiguration = this.mConfig;
            if (audioRecordingConfiguration != null) {
                return audioRecordingConfiguration.getClientPortId();
            }
            return -1;
        }

        public AudioRecordingConfiguration getConfig() {
            return this.mConfig;
        }

        public boolean hasDeathHandler() {
            return this.mDeathHandler != null;
        }

        public boolean isActiveConfiguration() {
            return this.mIsActive && this.mConfig != null;
        }

        public void release() {
            RecorderDeathHandler recorderDeathHandler = this.mDeathHandler;
            if (recorderDeathHandler != null) {
                recorderDeathHandler.release();
            }
        }

        public boolean setActive(boolean z) {
            if (this.mIsActive == z) {
                return false;
            }
            this.mIsActive = z;
            return this.mConfig != null;
        }

        public boolean setConfig(AudioRecordingConfiguration audioRecordingConfiguration) {
            if (audioRecordingConfiguration.equals(this.mConfig)) {
                return false;
            }
            this.mConfig = audioRecordingConfiguration;
            return this.mIsActive;
        }

        public void dump(PrintWriter printWriter) {
            printWriter.println("riid " + this.mRiid + "; active? " + this.mIsActive);
            AudioRecordingConfiguration audioRecordingConfiguration = this.mConfig;
            if (audioRecordingConfiguration != null) {
                audioRecordingConfiguration.dump(printWriter);
            } else {
                printWriter.println("  no config");
            }
        }
    }

    public RecordingActivityMonitor(Context context) {
        RecMonitorClient.sMonitor = this;
        RecorderDeathHandler.sMonitor = this;
        this.mPackMan = context.getPackageManager();
    }

    public void onRecordingConfigurationChanged(int i, int i2, int i3, int i4, int i5, int i6, boolean z, int[] iArr, AudioEffect.Descriptor[] descriptorArr, AudioEffect.Descriptor[] descriptorArr2, int i7, String str) {
        AudioDeviceInfo audioDevice;
        AudioRecordingConfiguration createRecordingConfiguration = createRecordingConfiguration(i3, i4, i5, iArr, i6, z, i7, descriptorArr, descriptorArr2);
        if (i5 == 8 && ((i == 0 || i == 2) && (audioDevice = createRecordingConfiguration.getAudioDevice()) != null && "0".equals(audioDevice.getAddress()))) {
            this.mLegacyRemoteSubmixRiid.set(i2);
            this.mLegacyRemoteSubmixActive.set(true);
        }
        if (MediaRecorder.isSystemOnlyAudioSource(i5)) {
            sEventLogger.enqueue(new RecordingEvent(i, i2, createRecordingConfiguration).printLog("AudioService.RecordingActivityMonitor"));
        } else {
            dispatchCallbacks(updateSnapshot(i, i2, createRecordingConfiguration));
        }
    }

    public int trackRecorder(IBinder iBinder) {
        if (iBinder == null) {
            Log.e("AudioService.RecordingActivityMonitor", "trackRecorder called with null token");
            return -1;
        }
        int newAudioRecorderId = AudioSystem.newAudioRecorderId();
        RecorderDeathHandler recorderDeathHandler = new RecorderDeathHandler(newAudioRecorderId, iBinder);
        if (recorderDeathHandler.init()) {
            synchronized (this.mRecordStates) {
                this.mRecordStates.add(new RecordingState(newAudioRecorderId, recorderDeathHandler));
            }
            return newAudioRecorderId;
        }
        return -1;
    }

    public void recorderEvent(int i, int i2) {
        if (this.mLegacyRemoteSubmixRiid.get() == i) {
            this.mLegacyRemoteSubmixActive.set(i2 == 0);
        }
        int i3 = i2 != 0 ? i2 == 1 ? 1 : -1 : 0;
        if (i == -1 || i3 == -1) {
            sEventLogger.enqueue(new RecordingEvent(i2, i, null).printLog("AudioService.RecordingActivityMonitor"));
        } else {
            dispatchCallbacks(updateSnapshot(i3, i, null));
        }
    }

    public void releaseRecorder(int i) {
        dispatchCallbacks(updateSnapshot(3, i, null));
    }

    public boolean isRecordingActiveForUid(int i) {
        synchronized (this.mRecordStates) {
            for (RecordingState recordingState : this.mRecordStates) {
                if (recordingState.isActiveConfiguration() && recordingState.getConfig().getClientUid() == i) {
                    return true;
                }
            }
            return false;
        }
    }

    public final void dispatchCallbacks(List<AudioRecordingConfiguration> list) {
        ArrayList<AudioRecordingConfiguration> arrayList;
        if (list == null) {
            return;
        }
        synchronized (this.mClients) {
            if (this.mHasPublicClients) {
                arrayList = anonymizeForPublicConsumption(list);
            } else {
                arrayList = new ArrayList<>();
            }
            Iterator<RecMonitorClient> it = this.mClients.iterator();
            while (it.hasNext()) {
                RecMonitorClient next = it.next();
                try {
                    if (next.mIsPrivileged) {
                        next.mDispatcherCb.dispatchRecordingConfigChange(list);
                    } else {
                        next.mDispatcherCb.dispatchRecordingConfigChange(arrayList);
                    }
                } catch (RemoteException e) {
                    Log.w("AudioService.RecordingActivityMonitor", "Could not call dispatchRecordingConfigChange() on client", e);
                }
            }
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("\nRecordActivityMonitor dump time: " + DateFormat.getTimeInstance().format(new Date()));
        synchronized (this.mRecordStates) {
            for (RecordingState recordingState : this.mRecordStates) {
                recordingState.dump(printWriter);
            }
        }
        printWriter.println("\n");
        sEventLogger.dump(printWriter);
    }

    public static ArrayList<AudioRecordingConfiguration> anonymizeForPublicConsumption(List<AudioRecordingConfiguration> list) {
        ArrayList<AudioRecordingConfiguration> arrayList = new ArrayList<>();
        for (AudioRecordingConfiguration audioRecordingConfiguration : list) {
            arrayList.add(AudioRecordingConfiguration.anonymizedCopy(audioRecordingConfiguration));
        }
        return arrayList;
    }

    public void initMonitor() {
        AudioSystem.setRecordingCallback(this);
    }

    public void onAudioServerDied() {
        List<AudioRecordingConfiguration> activeRecordingConfigurations;
        synchronized (this.mRecordStates) {
            Iterator<RecordingState> it = this.mRecordStates.iterator();
            boolean z = false;
            while (it.hasNext()) {
                RecordingState next = it.next();
                if (!next.hasDeathHandler()) {
                    if (next.isActiveConfiguration()) {
                        sEventLogger.enqueue(new RecordingEvent(3, next.getRiid(), next.getConfig()));
                        z = true;
                    }
                    it.remove();
                }
            }
            activeRecordingConfigurations = z ? getActiveRecordingConfigurations(true) : null;
        }
        dispatchCallbacks(activeRecordingConfigurations);
    }

    public void registerRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher, boolean z) {
        if (iRecordingConfigDispatcher == null) {
            return;
        }
        synchronized (this.mClients) {
            RecMonitorClient recMonitorClient = new RecMonitorClient(iRecordingConfigDispatcher, z);
            if (recMonitorClient.init()) {
                if (!z) {
                    this.mHasPublicClients = true;
                }
                this.mClients.add(recMonitorClient);
            }
        }
    }

    public void unregisterRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher) {
        if (iRecordingConfigDispatcher == null) {
            return;
        }
        synchronized (this.mClients) {
            Iterator<RecMonitorClient> it = this.mClients.iterator();
            boolean z = false;
            while (it.hasNext()) {
                RecMonitorClient next = it.next();
                if (iRecordingConfigDispatcher.asBinder().equals(next.mDispatcherCb.asBinder())) {
                    next.release();
                    it.remove();
                } else if (!next.mIsPrivileged) {
                    z = true;
                }
            }
            this.mHasPublicClients = z;
        }
    }

    public List<AudioRecordingConfiguration> getActiveRecordingConfigurations(boolean z) {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mRecordStates) {
            for (RecordingState recordingState : this.mRecordStates) {
                if (recordingState.isActiveConfiguration()) {
                    arrayList.add(recordingState.getConfig());
                }
            }
        }
        return !z ? anonymizeForPublicConsumption(arrayList) : arrayList;
    }

    public boolean isLegacyRemoteSubmixActive() {
        return this.mLegacyRemoteSubmixActive.get();
    }

    public final AudioRecordingConfiguration createRecordingConfiguration(int i, int i2, int i3, int[] iArr, int i4, boolean z, int i5, AudioEffect.Descriptor[] descriptorArr, AudioEffect.Descriptor[] descriptorArr2) {
        AudioFormat build = new AudioFormat.Builder().setEncoding(iArr[0]).setChannelMask(iArr[1]).setSampleRate(iArr[2]).build();
        AudioFormat build2 = new AudioFormat.Builder().setEncoding(iArr[3]).setChannelMask(iArr[4]).setSampleRate(iArr[5]).build();
        int i6 = iArr[6];
        String[] packagesForUid = this.mPackMan.getPackagesForUid(i);
        return new AudioRecordingConfiguration(i, i2, i3, build, build2, i6, (packagesForUid == null || packagesForUid.length <= 0) ? "" : packagesForUid[0], i4, z, i5, descriptorArr, descriptorArr2);
    }

    public final List<AudioRecordingConfiguration> updateSnapshot(int i, int i2, AudioRecordingConfiguration audioRecordingConfiguration) {
        int findStateByPortId;
        synchronized (this.mRecordStates) {
            try {
                if (i2 != -1) {
                    findStateByPortId = findStateByRiid(i2);
                } else {
                    findStateByPortId = audioRecordingConfiguration != null ? findStateByPortId(audioRecordingConfiguration.getClientPortId()) : -1;
                }
                boolean z = false;
                List<AudioRecordingConfiguration> list = null;
                if (findStateByPortId == -1) {
                    if (i != 0 || audioRecordingConfiguration == null) {
                        if (audioRecordingConfiguration == null) {
                            Log.e("AudioService.RecordingActivityMonitor", String.format("Unexpected event %d for riid %d", Integer.valueOf(i), Integer.valueOf(i2)));
                        }
                        return null;
                    }
                    this.mRecordStates.add(new RecordingState(audioRecordingConfiguration));
                    findStateByPortId = this.mRecordStates.size() - 1;
                }
                RecordingState recordingState = this.mRecordStates.get(findStateByPortId);
                if (i == 0) {
                    boolean active = recordingState.setActive(true);
                    if (audioRecordingConfiguration == null) {
                        z = active;
                    } else if (recordingState.setConfig(audioRecordingConfiguration) || active) {
                        z = true;
                    }
                } else if (i == 1) {
                    z = recordingState.setActive(false);
                    if (!recordingState.hasDeathHandler()) {
                        this.mRecordStates.remove(findStateByPortId);
                    }
                } else if (i == 2) {
                    z = recordingState.setConfig(audioRecordingConfiguration);
                } else if (i == 3) {
                    z = recordingState.isActiveConfiguration();
                    recordingState.release();
                    this.mRecordStates.remove(findStateByPortId);
                } else {
                    Log.e("AudioService.RecordingActivityMonitor", String.format("Unknown event %d for riid %d / portid %d", Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(recordingState.getPortId())));
                }
                if (z) {
                    sEventLogger.enqueue(new RecordingEvent(i, i2, recordingState.getConfig()));
                    list = getActiveRecordingConfigurations(true);
                }
                return list;
            } finally {
            }
        }
    }

    public final int findStateByRiid(int i) {
        synchronized (this.mRecordStates) {
            for (int i2 = 0; i2 < this.mRecordStates.size(); i2++) {
                if (this.mRecordStates.get(i2).getRiid() == i) {
                    return i2;
                }
            }
            return -1;
        }
    }

    public final int findStateByPortId(int i) {
        synchronized (this.mRecordStates) {
            for (int i2 = 0; i2 < this.mRecordStates.size(); i2++) {
                if (!this.mRecordStates.get(i2).hasDeathHandler() && this.mRecordStates.get(i2).getPortId() == i) {
                    return i2;
                }
            }
            return -1;
        }
    }

    /* loaded from: classes.dex */
    public static final class RecMonitorClient implements IBinder.DeathRecipient {
        public static RecordingActivityMonitor sMonitor;
        public final IRecordingConfigDispatcher mDispatcherCb;
        public final boolean mIsPrivileged;

        public RecMonitorClient(IRecordingConfigDispatcher iRecordingConfigDispatcher, boolean z) {
            this.mDispatcherCb = iRecordingConfigDispatcher;
            this.mIsPrivileged = z;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w("AudioService.RecordingActivityMonitor", "client died");
            sMonitor.unregisterRecordingCallback(this.mDispatcherCb);
        }

        public boolean init() {
            try {
                this.mDispatcherCb.asBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w("AudioService.RecordingActivityMonitor", "Could not link to client death", e);
                return false;
            }
        }

        public void release() {
            this.mDispatcherCb.asBinder().unlinkToDeath(this, 0);
        }
    }

    /* loaded from: classes.dex */
    public static final class RecorderDeathHandler implements IBinder.DeathRecipient {
        public static RecordingActivityMonitor sMonitor;
        public final IBinder mRecorderToken;
        public final int mRiid;

        public RecorderDeathHandler(int i, IBinder iBinder) {
            this.mRiid = i;
            this.mRecorderToken = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            sMonitor.releaseRecorder(this.mRiid);
        }

        public boolean init() {
            try {
                this.mRecorderToken.linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w("AudioService.RecordingActivityMonitor", "Could not link to recorder death", e);
                return false;
            }
        }

        public void release() {
            this.mRecorderToken.unlinkToDeath(this, 0);
        }
    }

    /* loaded from: classes.dex */
    public static final class RecordingEvent extends EventLogger.Event {
        public final int mClientUid;
        public final String mPackName;
        public final int mRIId;
        public final int mRecEvent;
        public final int mSession;
        public final boolean mSilenced;
        public final int mSource;

        public RecordingEvent(int i, int i2, AudioRecordingConfiguration audioRecordingConfiguration) {
            this.mRecEvent = i;
            this.mRIId = i2;
            if (audioRecordingConfiguration != null) {
                this.mClientUid = audioRecordingConfiguration.getClientUid();
                this.mSession = audioRecordingConfiguration.getClientAudioSessionId();
                this.mSource = audioRecordingConfiguration.getClientAudioSource();
                this.mPackName = audioRecordingConfiguration.getClientPackageName();
                this.mSilenced = audioRecordingConfiguration.isClientSilenced();
                return;
            }
            this.mClientUid = -1;
            this.mSession = -1;
            this.mSource = -1;
            this.mPackName = null;
            this.mSilenced = false;
        }

        public static String recordEventToString(int i) {
            if (i != 0) {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            return "unknown (" + i + ")";
                        }
                        return "release";
                    }
                    return "update";
                }
                return "stop";
            }
            return "start";
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            String str;
            StringBuilder sb = new StringBuilder("rec ");
            sb.append(recordEventToString(this.mRecEvent));
            sb.append(" riid:");
            sb.append(this.mRIId);
            sb.append(" uid:");
            sb.append(this.mClientUid);
            sb.append(" session:");
            sb.append(this.mSession);
            sb.append(" src:");
            sb.append(MediaRecorder.toLogFriendlyAudioSource(this.mSource));
            sb.append(this.mSilenced ? " silenced" : " not silenced");
            if (this.mPackName == null) {
                str = "";
            } else {
                str = " pack:" + this.mPackName;
            }
            sb.append(str);
            return sb.toString();
        }
    }
}
