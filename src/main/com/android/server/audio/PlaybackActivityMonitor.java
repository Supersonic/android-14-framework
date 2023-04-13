package com.android.server.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioDeviceAttributes;
import android.media.AudioDeviceInfo;
import android.media.AudioPlaybackConfiguration;
import android.media.AudioSystem;
import android.media.IPlaybackConfigDispatcher;
import android.media.PlayerBase;
import android.media.VolumeShaper;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class PlaybackActivityMonitor implements AudioPlaybackConfiguration.PlayerDeathMonitor, PlayerFocusEnforcer {
    public static final VolumeShaper.Configuration MUTE_AWAIT_CONNECTION_VSHAPE;
    public static final VolumeShaper.Operation PLAY_CREATE_IF_NEEDED;
    public static final VolumeShaper.Operation PLAY_SKIP_RAMP;
    public static final int[] UNDUCKABLE_PLAYER_TYPES;
    public static final EventLogger sEventLogger;
    public final Context mContext;
    public Handler mEventHandler;
    public HandlerThread mEventThread;
    public final int mMaxAlarmVolume;
    public final Consumer<AudioDeviceAttributes> mMuteAwaitConnectionTimeoutCb;
    public static final VolumeShaper.Configuration DUCK_VSHAPE = new VolumeShaper.Configuration.Builder().setId(1).setCurve(new float[]{0.0f, 1.0f}, new float[]{1.0f, 0.2f}).setOptionFlags(2).setDuration(MediaFocusControl.getFocusRampTimeMs(3, new AudioAttributes.Builder().setUsage(5).build())).build();
    public static final VolumeShaper.Configuration DUCK_ID = new VolumeShaper.Configuration(1);
    public static final VolumeShaper.Configuration STRONG_DUCK_VSHAPE = new VolumeShaper.Configuration.Builder().setId(4).setCurve(new float[]{0.0f, 1.0f}, new float[]{1.0f, 0.017783f}).setOptionFlags(2).setDuration(MediaFocusControl.getFocusRampTimeMs(3, new AudioAttributes.Builder().setUsage(5).build())).build();
    public static final VolumeShaper.Configuration STRONG_DUCK_ID = new VolumeShaper.Configuration(4);
    public final ConcurrentLinkedQueue<PlayMonitorClient> mClients = new ConcurrentLinkedQueue<>();
    public final Object mPlayerLock = new Object();
    @GuardedBy({"mPlayerLock"})
    public final HashMap<Integer, AudioPlaybackConfiguration> mPlayers = new HashMap<>();
    @GuardedBy({"mPlayerLock"})
    public final SparseIntArray mPortIdToPiid = new SparseIntArray();
    public int mSavedAlarmVolume = -1;
    public int mPrivilegedAlarmActiveCount = 0;
    public final ArrayList<Integer> mBannedUids = new ArrayList<>();
    @GuardedBy({"mPlayerLock"})
    public ArrayList<Integer> mDoNotLogPiidList = new ArrayList<>();
    public final HashMap<Integer, Integer> mAllowedCapturePolicies = new HashMap<>();
    public final ArrayList<Integer> mMutedPlayers = new ArrayList<>();
    public final DuckingManager mDuckingManager = new DuckingManager();
    public final FadeOutManager mFadingManager = new FadeOutManager();
    @GuardedBy({"mPlayerLock"})
    public final ArrayList<Integer> mMutedPlayersAwaitingConnection = new ArrayList<>();
    @GuardedBy({"mPlayerLock"})
    public int[] mMutedUsagesAwaitingConnection = null;

    static {
        VolumeShaper.Operation build = new VolumeShaper.Operation.Builder(VolumeShaper.Operation.PLAY).createIfNeeded().build();
        PLAY_CREATE_IF_NEEDED = build;
        MUTE_AWAIT_CONNECTION_VSHAPE = new VolumeShaper.Configuration.Builder().setId(3).setCurve(new float[]{0.0f, 1.0f}, new float[]{1.0f, 0.0f}).setOptionFlags(2).setDuration(100L).build();
        UNDUCKABLE_PLAYER_TYPES = new int[]{13, 3};
        PLAY_SKIP_RAMP = new VolumeShaper.Operation.Builder(build).setXOffset(1.0f).build();
        sEventLogger = new EventLogger(100, "playback activity as reported through PlayerBase");
    }

    public PlaybackActivityMonitor(Context context, int i, Consumer<AudioDeviceAttributes> consumer) {
        this.mContext = context;
        this.mMaxAlarmVolume = i;
        PlayMonitorClient.sListenerDeathMonitor = this;
        AudioPlaybackConfiguration.sPlayerDeathMonitor = this;
        this.mMuteAwaitConnectionTimeoutCb = consumer;
        initEventHandler();
    }

    public void disableAudioForUid(boolean z, int i) {
        synchronized (this.mPlayerLock) {
            int indexOf = this.mBannedUids.indexOf(new Integer(i));
            if (indexOf >= 0) {
                if (!z) {
                    this.mBannedUids.remove(indexOf);
                }
            } else if (z) {
                for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                    checkBanPlayer(audioPlaybackConfiguration, i);
                }
                this.mBannedUids.add(new Integer(i));
            }
        }
    }

    public final boolean checkBanPlayer(AudioPlaybackConfiguration audioPlaybackConfiguration, int i) {
        boolean z = audioPlaybackConfiguration.getClientUid() == i;
        if (z) {
            int playerInterfaceId = audioPlaybackConfiguration.getPlayerInterfaceId();
            try {
                Log.v("AS.PlaybackActivityMon", "banning player " + playerInterfaceId + " uid:" + i);
                audioPlaybackConfiguration.getPlayerProxy().pause();
            } catch (Exception e) {
                Log.e("AS.PlaybackActivityMon", "error banning player " + playerInterfaceId + " uid:" + i, e);
            }
        }
        return z;
    }

    public void ignorePlayerIId(int i) {
        synchronized (this.mPlayerLock) {
            this.mDoNotLogPiidList.add(Integer.valueOf(i));
        }
    }

    public int trackPlayer(PlayerBase.PlayerIdCard playerIdCard) {
        int newAudioPlayerId = AudioSystem.newAudioPlayerId();
        AudioPlaybackConfiguration audioPlaybackConfiguration = new AudioPlaybackConfiguration(playerIdCard, newAudioPlayerId, Binder.getCallingUid(), Binder.getCallingPid());
        audioPlaybackConfiguration.init();
        synchronized (this.mAllowedCapturePolicies) {
            int clientUid = audioPlaybackConfiguration.getClientUid();
            if (this.mAllowedCapturePolicies.containsKey(Integer.valueOf(clientUid))) {
                updateAllowedCapturePolicy(audioPlaybackConfiguration, this.mAllowedCapturePolicies.get(Integer.valueOf(clientUid)).intValue());
            }
        }
        sEventLogger.enqueue(new NewPlayerEvent(audioPlaybackConfiguration));
        synchronized (this.mPlayerLock) {
            this.mPlayers.put(Integer.valueOf(newAudioPlayerId), audioPlaybackConfiguration);
            maybeMutePlayerAwaitingConnection(audioPlaybackConfiguration);
        }
        return newAudioPlayerId;
    }

    public void playerAttributes(int i, AudioAttributes audioAttributes, int i2) {
        boolean z;
        synchronized (this.mAllowedCapturePolicies) {
            if (this.mAllowedCapturePolicies.containsKey(Integer.valueOf(i2)) && audioAttributes.getAllowedCapturePolicy() < this.mAllowedCapturePolicies.get(Integer.valueOf(i2)).intValue()) {
                audioAttributes = new AudioAttributes.Builder(audioAttributes).setAllowedCapturePolicy(this.mAllowedCapturePolicies.get(Integer.valueOf(i2)).intValue()).build();
            }
        }
        synchronized (this.mPlayerLock) {
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(new Integer(i));
            if (checkConfigurationCaller(i, audioPlaybackConfiguration, i2)) {
                sEventLogger.enqueue(new AudioAttrEvent(i, audioAttributes));
                z = audioPlaybackConfiguration.handleAudioAttributesEvent(audioAttributes);
            } else {
                Log.e("AS.PlaybackActivityMon", "Error updating audio attributes");
                z = false;
            }
        }
        if (z) {
            dispatchPlaybackChange(false);
        }
    }

    public void playerSessionId(int i, int i2, int i3) {
        boolean z;
        synchronized (this.mPlayerLock) {
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(new Integer(i));
            if (checkConfigurationCaller(i, audioPlaybackConfiguration, i3)) {
                z = audioPlaybackConfiguration.handleSessionIdEvent(i2);
            } else {
                Log.e("AS.PlaybackActivityMon", "Error updating audio session");
                z = false;
            }
        }
        if (z) {
            dispatchPlaybackChange(false);
        }
    }

    public final void checkVolumeForPrivilegedAlarm(AudioPlaybackConfiguration audioPlaybackConfiguration, int i) {
        if (i == 5) {
            return;
        }
        if ((i == 2 || audioPlaybackConfiguration.getPlayerState() == 2) && (audioPlaybackConfiguration.getAudioAttributes().getAllFlags() & FrameworkStatsLog.f392xcd34d435) == 192 && audioPlaybackConfiguration.getAudioAttributes().getUsage() == 4 && this.mContext.checkPermission("android.permission.MODIFY_PHONE_STATE", audioPlaybackConfiguration.getClientPid(), audioPlaybackConfiguration.getClientUid()) == 0) {
            if (i == 2 && audioPlaybackConfiguration.getPlayerState() != 2) {
                int i2 = this.mPrivilegedAlarmActiveCount;
                this.mPrivilegedAlarmActiveCount = i2 + 1;
                if (i2 == 0) {
                    this.mSavedAlarmVolume = AudioSystem.getStreamVolumeIndex(4, 2);
                    AudioSystem.setStreamVolumeIndexAS(4, this.mMaxAlarmVolume, 2);
                }
            } else if (i == 2 || audioPlaybackConfiguration.getPlayerState() != 2) {
            } else {
                int i3 = this.mPrivilegedAlarmActiveCount - 1;
                this.mPrivilegedAlarmActiveCount = i3;
                if (i3 == 0 && AudioSystem.getStreamVolumeIndex(4, 2) == this.mMaxAlarmVolume) {
                    AudioSystem.setStreamVolumeIndexAS(4, this.mSavedAlarmVolume, 2);
                }
            }
        }
    }

    public void playerEvent(int i, int i2, int i3, int i4) {
        boolean z;
        synchronized (this.mPlayerLock) {
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(new Integer(i));
            if (audioPlaybackConfiguration == null) {
                return;
            }
            boolean contains = this.mDoNotLogPiidList.contains(Integer.valueOf(i));
            if (!contains || i2 == 0) {
                sEventLogger.enqueue(new PlayerEvent(i, i2, i3));
                if (i2 == 6) {
                    Handler handler = this.mEventHandler;
                    handler.sendMessage(handler.obtainMessage(2, i3, i));
                    return;
                }
                if (i2 == 2) {
                    Iterator<Integer> it = this.mBannedUids.iterator();
                    while (it.hasNext()) {
                        if (checkBanPlayer(audioPlaybackConfiguration, it.next().intValue())) {
                            sEventLogger.enqueue(new EventLogger.StringEvent("not starting piid:" + i + " ,is banned"));
                            return;
                        }
                    }
                }
                if (audioPlaybackConfiguration.getPlayerType() != 3 || i2 == 0) {
                    if (checkConfigurationCaller(i, audioPlaybackConfiguration, i4)) {
                        checkVolumeForPrivilegedAlarm(audioPlaybackConfiguration, i2);
                        z = audioPlaybackConfiguration.handleStateEvent(i2, i3);
                    } else {
                        Log.e("AS.PlaybackActivityMon", "Error handling event " + i2);
                        z = false;
                    }
                    if (z) {
                        if (i2 == 2) {
                            this.mDuckingManager.checkDuck(audioPlaybackConfiguration);
                            this.mFadingManager.checkFade(audioPlaybackConfiguration);
                        }
                        if (contains) {
                            z = false;
                        }
                    }
                    if (z) {
                        dispatchPlaybackChange(i2 == 0);
                    }
                }
            }
        }
    }

    public void portEvent(int i, int i2, PersistableBundle persistableBundle, int i3) {
        if (!UserHandle.isCore(i3)) {
            Log.e("AS.PlaybackActivityMon", "Forbidden operation from uid " + i3);
            return;
        }
        synchronized (this.mPlayerLock) {
            int i4 = this.mPortIdToPiid.get(i, -1);
            if (i4 == -1) {
                return;
            }
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(Integer.valueOf(i4));
            if (audioPlaybackConfiguration == null) {
                return;
            }
            if (audioPlaybackConfiguration.getPlayerType() == 3) {
                return;
            }
            if (i2 == 7) {
                Handler handler = this.mEventHandler;
                handler.sendMessage(handler.obtainMessage(3, i4, i, persistableBundle));
            } else if (i2 == 8) {
                Handler handler2 = this.mEventHandler;
                handler2.sendMessage(handler2.obtainMessage(5, i4, i, persistableBundle));
            }
        }
    }

    public void playerHasOpPlayAudio(int i, boolean z, int i2) {
        sEventLogger.enqueue(new PlayerOpPlayAudioEvent(i, z, i2));
    }

    public void releasePlayer(int i, int i2) {
        boolean z;
        synchronized (this.mPlayerLock) {
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(new Integer(i));
            z = false;
            if (checkConfigurationCaller(i, audioPlaybackConfiguration, i2)) {
                EventLogger eventLogger = sEventLogger;
                eventLogger.enqueue(new EventLogger.StringEvent("releasing player piid:" + i));
                this.mPlayers.remove(new Integer(i));
                this.mDuckingManager.removeReleased(audioPlaybackConfiguration);
                this.mFadingManager.removeReleased(audioPlaybackConfiguration);
                this.mMutedPlayersAwaitingConnection.remove(Integer.valueOf(i));
                checkVolumeForPrivilegedAlarm(audioPlaybackConfiguration, 0);
                boolean handleStateEvent = audioPlaybackConfiguration.handleStateEvent(0, 0);
                Handler handler = this.mEventHandler;
                handler.sendMessage(handler.obtainMessage(4, i, 0));
                if (!handleStateEvent || !this.mDoNotLogPiidList.contains(Integer.valueOf(i))) {
                    z = handleStateEvent;
                }
            }
        }
        if (z) {
            dispatchPlaybackChange(true);
        }
    }

    public void onAudioServerDied() {
        sEventLogger.enqueue(new EventLogger.StringEvent("clear port id to piid map"));
        synchronized (this.mPlayerLock) {
            this.mPortIdToPiid.clear();
        }
    }

    public void setAllowedCapturePolicy(int i, int i2) {
        synchronized (this.mAllowedCapturePolicies) {
            if (i2 == 1) {
                this.mAllowedCapturePolicies.remove(Integer.valueOf(i));
                return;
            }
            this.mAllowedCapturePolicies.put(Integer.valueOf(i), Integer.valueOf(i2));
            synchronized (this.mPlayerLock) {
                for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                    if (audioPlaybackConfiguration.getClientUid() == i) {
                        updateAllowedCapturePolicy(audioPlaybackConfiguration, i2);
                    }
                }
            }
        }
    }

    public int getAllowedCapturePolicy(int i) {
        return this.mAllowedCapturePolicies.getOrDefault(Integer.valueOf(i), 1).intValue();
    }

    public HashMap<Integer, Integer> getAllAllowedCapturePolicies() {
        HashMap<Integer, Integer> hashMap;
        synchronized (this.mAllowedCapturePolicies) {
            hashMap = (HashMap) this.mAllowedCapturePolicies.clone();
        }
        return hashMap;
    }

    public final void updateAllowedCapturePolicy(AudioPlaybackConfiguration audioPlaybackConfiguration, int i) {
        if (audioPlaybackConfiguration.getAudioAttributes().getAllowedCapturePolicy() >= i) {
            return;
        }
        audioPlaybackConfiguration.handleAudioAttributesEvent(new AudioAttributes.Builder(audioPlaybackConfiguration.getAudioAttributes()).setAllowedCapturePolicy(i).build());
    }

    public void playerDeath(int i) {
        releasePlayer(i, 0);
    }

    public boolean isPlaybackActiveForUid(int i) {
        synchronized (this.mPlayerLock) {
            for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                if (audioPlaybackConfiguration.isActive() && audioPlaybackConfiguration.getClientUid() == i) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean hasActiveMediaPlaybackOnSubmixWithAddress(String str) {
        synchronized (this.mPlayerLock) {
            for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                AudioDeviceInfo audioDeviceInfo = audioPlaybackConfiguration.getAudioDeviceInfo();
                if (audioPlaybackConfiguration.getAudioAttributes().getUsage() == 1 && audioPlaybackConfiguration.isActive() && audioDeviceInfo != null && audioDeviceInfo.getInternalType() == 32768 && str.equals(audioDeviceInfo.getAddress())) {
                    return true;
                }
            }
            return false;
        }
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("\nPlaybackActivityMonitor dump time: " + DateFormat.getTimeInstance().format(new Date()));
        synchronized (this.mPlayerLock) {
            printWriter.println("\n  playback listeners:");
            Iterator<PlayMonitorClient> it = this.mClients.iterator();
            while (it.hasNext()) {
                PlayMonitorClient next = it.next();
                StringBuilder sb = new StringBuilder();
                sb.append(" ");
                sb.append(next.isPrivileged() ? "(S)" : "(P)");
                sb.append(next.toString());
                printWriter.print(sb.toString());
            }
            printWriter.println("\n");
            printWriter.println("\n  players:");
            ArrayList<Integer> arrayList = new ArrayList(this.mPlayers.keySet());
            Collections.sort(arrayList);
            for (Integer num : arrayList) {
                AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(num);
                if (audioPlaybackConfiguration != null) {
                    if (this.mDoNotLogPiidList.contains(Integer.valueOf(audioPlaybackConfiguration.getPlayerInterfaceId()))) {
                        printWriter.print("(not logged)");
                    }
                    audioPlaybackConfiguration.dump(printWriter);
                }
            }
            printWriter.println("\n  ducked players piids:");
            this.mDuckingManager.dump(printWriter);
            printWriter.println("\n  faded out players piids:");
            this.mFadingManager.dump(printWriter);
            printWriter.print("\n  muted player piids due to call/ring:");
            Iterator<Integer> it2 = this.mMutedPlayers.iterator();
            while (it2.hasNext()) {
                int intValue = it2.next().intValue();
                printWriter.print(" " + intValue);
            }
            printWriter.println();
            printWriter.print("\n  banned uids:");
            Iterator<Integer> it3 = this.mBannedUids.iterator();
            while (it3.hasNext()) {
                int intValue2 = it3.next().intValue();
                printWriter.print(" " + intValue2);
            }
            printWriter.println("\n");
            printWriter.print("\n  muted players (piids) awaiting device connection:");
            Iterator<Integer> it4 = this.mMutedPlayersAwaitingConnection.iterator();
            while (it4.hasNext()) {
                int intValue3 = it4.next().intValue();
                printWriter.print(" " + intValue3);
            }
            printWriter.println("\n");
            printWriter.println("\n  current portId to piid map:");
            for (int i = 0; i < this.mPortIdToPiid.size(); i++) {
                printWriter.println("  portId: " + this.mPortIdToPiid.keyAt(i) + " piid: " + this.mPortIdToPiid.valueAt(i));
            }
            printWriter.println("\n");
            sEventLogger.dump(printWriter);
        }
        synchronized (this.mAllowedCapturePolicies) {
            printWriter.println("\n  allowed capture policies:");
            for (Map.Entry<Integer, Integer> entry : this.mAllowedCapturePolicies.entrySet()) {
                printWriter.println("  uid: " + entry.getKey() + " policy: " + entry.getValue());
            }
        }
    }

    public static boolean checkConfigurationCaller(int i, AudioPlaybackConfiguration audioPlaybackConfiguration, int i2) {
        if (audioPlaybackConfiguration == null) {
            return false;
        }
        if (i2 == 0 || audioPlaybackConfiguration.getClientUid() == i2) {
            return true;
        }
        Log.e("AS.PlaybackActivityMon", "Forbidden operation from uid " + i2 + " for player " + i);
        return false;
    }

    public final void dispatchPlaybackChange(boolean z) {
        synchronized (this.mPlayerLock) {
            if (this.mPlayers.isEmpty()) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.mPlayers.values());
            Iterator<PlayMonitorClient> it = this.mClients.iterator();
            ArrayList<AudioPlaybackConfiguration> arrayList2 = null;
            while (it.hasNext()) {
                PlayMonitorClient next = it.next();
                if (!next.reachedMaxErrorCount()) {
                    if (next.isPrivileged()) {
                        next.dispatchPlaybackConfigChange(arrayList, z);
                    } else {
                        if (arrayList2 == null) {
                            arrayList2 = anonymizeForPublicConsumption(arrayList);
                        }
                        next.dispatchPlaybackConfigChange(arrayList2, false);
                    }
                }
            }
        }
    }

    public final ArrayList<AudioPlaybackConfiguration> anonymizeForPublicConsumption(List<AudioPlaybackConfiguration> list) {
        ArrayList<AudioPlaybackConfiguration> arrayList = new ArrayList<>();
        for (AudioPlaybackConfiguration audioPlaybackConfiguration : list) {
            if (audioPlaybackConfiguration.isActive()) {
                arrayList.add(AudioPlaybackConfiguration.anonymizedCopy(audioPlaybackConfiguration));
            }
        }
        return arrayList;
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public boolean duckPlayers(FocusRequester focusRequester, FocusRequester focusRequester2, boolean z) {
        synchronized (this.mPlayerLock) {
            if (this.mPlayers.isEmpty()) {
                return true;
            }
            ArrayList<AudioPlaybackConfiguration> arrayList = new ArrayList<>();
            for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                if (!focusRequester.hasSameUid(audioPlaybackConfiguration.getClientUid()) && focusRequester2.hasSameUid(audioPlaybackConfiguration.getClientUid()) && audioPlaybackConfiguration.getPlayerState() == 2) {
                    if (!z && audioPlaybackConfiguration.getAudioAttributes().getContentType() == 1) {
                        Log.v("AS.PlaybackActivityMon", "not ducking player " + audioPlaybackConfiguration.getPlayerInterfaceId() + " uid:" + audioPlaybackConfiguration.getClientUid() + " pid:" + audioPlaybackConfiguration.getClientPid() + " - SPEECH");
                        return false;
                    } else if (ArrayUtils.contains(UNDUCKABLE_PLAYER_TYPES, audioPlaybackConfiguration.getPlayerType())) {
                        Log.v("AS.PlaybackActivityMon", "not ducking player " + audioPlaybackConfiguration.getPlayerInterfaceId() + " uid:" + audioPlaybackConfiguration.getClientUid() + " pid:" + audioPlaybackConfiguration.getClientPid() + " due to type:" + AudioPlaybackConfiguration.toLogFriendlyPlayerType(audioPlaybackConfiguration.getPlayerType()));
                        return false;
                    } else {
                        arrayList.add(audioPlaybackConfiguration);
                    }
                }
            }
            this.mDuckingManager.duckUid(focusRequester2.getClientUid(), arrayList, reqCausesStrongDuck(focusRequester));
            return true;
        }
    }

    public final boolean reqCausesStrongDuck(FocusRequester focusRequester) {
        if (focusRequester.getGainRequest() != 3) {
            return false;
        }
        int usage = focusRequester.getAudioAttributes().getUsage();
        return usage == 16 || usage == 12;
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void restoreVShapedPlayers(FocusRequester focusRequester) {
        synchronized (this.mPlayerLock) {
            this.mDuckingManager.unduckUid(focusRequester.getClientUid(), this.mPlayers);
            this.mFadingManager.unfadeOutUid(focusRequester.getClientUid(), this.mPlayers);
        }
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void mutePlayersForCall(int[] iArr) {
        synchronized (this.mPlayerLock) {
            for (Integer num : this.mPlayers.keySet()) {
                AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(num);
                if (audioPlaybackConfiguration != null) {
                    int usage = audioPlaybackConfiguration.getAudioAttributes().getUsage();
                    int length = iArr.length;
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        } else if (usage == iArr[i]) {
                            z = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (z) {
                        try {
                            sEventLogger.enqueue(new EventLogger.StringEvent("call: muting piid:" + num + " uid:" + audioPlaybackConfiguration.getClientUid()).printLog("AS.PlaybackActivityMon"));
                            audioPlaybackConfiguration.getPlayerProxy().setVolume(0.0f);
                            this.mMutedPlayers.add(new Integer(num.intValue()));
                        } catch (Exception e) {
                            Log.e("AS.PlaybackActivityMon", "call: error muting player " + num, e);
                        }
                    }
                }
            }
        }
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void unmutePlayersForCall() {
        synchronized (this.mPlayerLock) {
            if (this.mMutedPlayers.isEmpty()) {
                return;
            }
            Iterator<Integer> it = this.mMutedPlayers.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(Integer.valueOf(intValue));
                if (audioPlaybackConfiguration != null) {
                    try {
                        EventLogger eventLogger = sEventLogger;
                        eventLogger.enqueue(new EventLogger.StringEvent("call: unmuting piid:" + intValue).printLog("AS.PlaybackActivityMon"));
                        audioPlaybackConfiguration.getPlayerProxy().setVolume(1.0f);
                    } catch (Exception e) {
                        Log.e("AS.PlaybackActivityMon", "call: error unmuting player " + intValue + " uid:" + audioPlaybackConfiguration.getClientUid(), e);
                    }
                }
            }
            this.mMutedPlayers.clear();
        }
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public boolean fadeOutPlayers(FocusRequester focusRequester, FocusRequester focusRequester2) {
        synchronized (this.mPlayerLock) {
            if (this.mPlayers.isEmpty()) {
                return false;
            }
            if (FadeOutManager.canCauseFadeOut(focusRequester, focusRequester2)) {
                ArrayList<AudioPlaybackConfiguration> arrayList = new ArrayList<>();
                boolean z = false;
                for (AudioPlaybackConfiguration audioPlaybackConfiguration : this.mPlayers.values()) {
                    if (!focusRequester.hasSameUid(audioPlaybackConfiguration.getClientUid()) && focusRequester2.hasSameUid(audioPlaybackConfiguration.getClientUid()) && audioPlaybackConfiguration.getPlayerState() == 2) {
                        if (!FadeOutManager.canBeFadedOut(audioPlaybackConfiguration)) {
                            Log.v("AS.PlaybackActivityMon", "not fading out player " + audioPlaybackConfiguration.getPlayerInterfaceId() + " uid:" + audioPlaybackConfiguration.getClientUid() + " pid:" + audioPlaybackConfiguration.getClientPid() + " type:" + AudioPlaybackConfiguration.toLogFriendlyPlayerType(audioPlaybackConfiguration.getPlayerType()) + " attr:" + audioPlaybackConfiguration.getAudioAttributes());
                            return false;
                        }
                        arrayList.add(audioPlaybackConfiguration);
                        z = true;
                    }
                }
                if (z) {
                    this.mFadingManager.fadeOutUid(focusRequester2.getClientUid(), arrayList);
                }
                return z;
            }
            return false;
        }
    }

    @Override // com.android.server.audio.PlayerFocusEnforcer
    public void forgetUid(int i) {
        HashMap<Integer, AudioPlaybackConfiguration> hashMap;
        synchronized (this.mPlayerLock) {
            hashMap = (HashMap) this.mPlayers.clone();
        }
        this.mFadingManager.unfadeOutUid(i, hashMap);
    }

    public void registerPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher, boolean z) {
        if (iPlaybackConfigDispatcher == null) {
            return;
        }
        PlayMonitorClient playMonitorClient = new PlayMonitorClient(iPlaybackConfigDispatcher, z);
        if (playMonitorClient.init()) {
            this.mClients.add(playMonitorClient);
        }
    }

    public void unregisterPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) {
        if (iPlaybackConfigDispatcher == null) {
            return;
        }
        Iterator<PlayMonitorClient> it = this.mClients.iterator();
        while (it.hasNext()) {
            PlayMonitorClient next = it.next();
            if (next.equalsDispatcher(iPlaybackConfigDispatcher)) {
                next.release();
                it.remove();
            }
        }
    }

    public List<AudioPlaybackConfiguration> getActivePlaybackConfigurations(boolean z) {
        ArrayList<AudioPlaybackConfiguration> anonymizeForPublicConsumption;
        synchronized (this.mPlayers) {
            if (z) {
                return new ArrayList(this.mPlayers.values());
            }
            synchronized (this.mPlayerLock) {
                anonymizeForPublicConsumption = anonymizeForPublicConsumption(new ArrayList(this.mPlayers.values()));
            }
            return anonymizeForPublicConsumption;
        }
    }

    /* loaded from: classes.dex */
    public static final class PlayMonitorClient implements IBinder.DeathRecipient {
        public static PlaybackActivityMonitor sListenerDeathMonitor;
        public final IPlaybackConfigDispatcher mDispatcherCb;
        @GuardedBy({"this"})
        public final boolean mIsPrivileged;
        @GuardedBy({"this"})
        public boolean mIsReleased = false;
        @GuardedBy({"this"})
        public int mErrorCount = 0;

        public PlayMonitorClient(IPlaybackConfigDispatcher iPlaybackConfigDispatcher, boolean z) {
            this.mDispatcherCb = iPlaybackConfigDispatcher;
            this.mIsPrivileged = z;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w("AS.PlaybackActivityMon", "client died");
            sListenerDeathMonitor.unregisterPlaybackCallback(this.mDispatcherCb);
        }

        public synchronized boolean init() {
            if (this.mIsReleased) {
                return false;
            }
            try {
                this.mDispatcherCb.asBinder().linkToDeath(this, 0);
                return true;
            } catch (RemoteException e) {
                Log.w("AS.PlaybackActivityMon", "Could not link to client death", e);
                return false;
            }
        }

        public synchronized void release() {
            this.mDispatcherCb.asBinder().unlinkToDeath(this, 0);
            this.mIsReleased = true;
        }

        public void dispatchPlaybackConfigChange(List<AudioPlaybackConfiguration> list, boolean z) {
            synchronized (this) {
                if (this.mIsReleased) {
                    return;
                }
                try {
                    this.mDispatcherCb.dispatchPlaybackConfigChange(list, z);
                } catch (RemoteException e) {
                    synchronized (this) {
                        this.mErrorCount++;
                        Log.e("AS.PlaybackActivityMon", "Error (" + this.mErrorCount + ") trying to dispatch playback config change to " + this, e);
                    }
                }
            }
        }

        public synchronized boolean isPrivileged() {
            return this.mIsPrivileged;
        }

        public synchronized boolean reachedMaxErrorCount() {
            return this.mErrorCount >= 5;
        }

        public synchronized boolean equalsDispatcher(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) {
            if (iPlaybackConfigDispatcher == null) {
                return false;
            }
            return iPlaybackConfigDispatcher.asBinder().equals(this.mDispatcherCb.asBinder());
        }
    }

    /* loaded from: classes.dex */
    public static final class DuckingManager {
        public final HashMap<Integer, DuckedApp> mDuckers;

        public DuckingManager() {
            this.mDuckers = new HashMap<>();
        }

        public synchronized void duckUid(int i, ArrayList<AudioPlaybackConfiguration> arrayList, boolean z) {
            if (!this.mDuckers.containsKey(Integer.valueOf(i))) {
                this.mDuckers.put(Integer.valueOf(i), new DuckedApp(i, z));
            }
            DuckedApp duckedApp = this.mDuckers.get(Integer.valueOf(i));
            Iterator<AudioPlaybackConfiguration> it = arrayList.iterator();
            while (it.hasNext()) {
                duckedApp.addDuck(it.next(), false);
            }
        }

        public synchronized void unduckUid(int i, HashMap<Integer, AudioPlaybackConfiguration> hashMap) {
            DuckedApp remove = this.mDuckers.remove(Integer.valueOf(i));
            if (remove == null) {
                return;
            }
            remove.removeUnduckAll(hashMap);
        }

        public synchronized void checkDuck(AudioPlaybackConfiguration audioPlaybackConfiguration) {
            DuckedApp duckedApp = this.mDuckers.get(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
            if (duckedApp == null) {
                return;
            }
            duckedApp.addDuck(audioPlaybackConfiguration, true);
        }

        public synchronized void dump(PrintWriter printWriter) {
            for (DuckedApp duckedApp : this.mDuckers.values()) {
                duckedApp.dump(printWriter);
            }
        }

        public synchronized void removeReleased(AudioPlaybackConfiguration audioPlaybackConfiguration) {
            DuckedApp duckedApp = this.mDuckers.get(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
            if (duckedApp == null) {
                return;
            }
            duckedApp.removeReleased(audioPlaybackConfiguration);
        }

        /* loaded from: classes.dex */
        public static final class DuckedApp {
            public final ArrayList<Integer> mDuckedPlayers = new ArrayList<>();
            public final int mUid;
            public final boolean mUseStrongDuck;

            public DuckedApp(int i, boolean z) {
                this.mUid = i;
                this.mUseStrongDuck = z;
            }

            public void dump(PrintWriter printWriter) {
                printWriter.print("\t uid:" + this.mUid + " piids:");
                Iterator<Integer> it = this.mDuckedPlayers.iterator();
                while (it.hasNext()) {
                    int intValue = it.next().intValue();
                    printWriter.print(" " + intValue);
                }
                printWriter.println("");
            }

            public void addDuck(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
                int intValue = new Integer(audioPlaybackConfiguration.getPlayerInterfaceId()).intValue();
                if (this.mDuckedPlayers.contains(Integer.valueOf(intValue))) {
                    return;
                }
                try {
                    PlaybackActivityMonitor.sEventLogger.enqueue(new DuckEvent(audioPlaybackConfiguration, z, this.mUseStrongDuck).printLog("AS.PlaybackActivityMon"));
                    audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(this.mUseStrongDuck ? PlaybackActivityMonitor.STRONG_DUCK_VSHAPE : PlaybackActivityMonitor.DUCK_VSHAPE, z ? PlaybackActivityMonitor.PLAY_SKIP_RAMP : PlaybackActivityMonitor.PLAY_CREATE_IF_NEEDED);
                    this.mDuckedPlayers.add(Integer.valueOf(intValue));
                } catch (Exception e) {
                    Log.e("AS.PlaybackActivityMon", "Error ducking player piid:" + intValue + " uid:" + this.mUid, e);
                }
            }

            public void removeUnduckAll(HashMap<Integer, AudioPlaybackConfiguration> hashMap) {
                Iterator<Integer> it = this.mDuckedPlayers.iterator();
                while (it.hasNext()) {
                    int intValue = it.next().intValue();
                    AudioPlaybackConfiguration audioPlaybackConfiguration = hashMap.get(Integer.valueOf(intValue));
                    if (audioPlaybackConfiguration != null) {
                        try {
                            EventLogger eventLogger = PlaybackActivityMonitor.sEventLogger;
                            eventLogger.enqueue(new EventLogger.StringEvent("unducking piid:" + intValue).printLog("AS.PlaybackActivityMon"));
                            audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(this.mUseStrongDuck ? PlaybackActivityMonitor.STRONG_DUCK_ID : PlaybackActivityMonitor.DUCK_ID, VolumeShaper.Operation.REVERSE);
                        } catch (Exception e) {
                            Log.e("AS.PlaybackActivityMon", "Error unducking player piid:" + intValue + " uid:" + this.mUid, e);
                        }
                    }
                }
                this.mDuckedPlayers.clear();
            }

            public void removeReleased(AudioPlaybackConfiguration audioPlaybackConfiguration) {
                this.mDuckedPlayers.remove(new Integer(audioPlaybackConfiguration.getPlayerInterfaceId()));
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class PlayerEvent extends EventLogger.Event {
        public final int mEvent;
        public final int mEventValue;
        public final int mPlayerIId;

        public PlayerEvent(int i, int i2, int i3) {
            this.mPlayerIId = i;
            this.mEvent = i2;
            this.mEventValue = i3;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            StringBuilder sb = new StringBuilder("player piid:");
            sb.append(this.mPlayerIId);
            sb.append(" event:");
            sb.append(AudioPlaybackConfiguration.toLogFriendlyPlayerState(this.mEvent));
            int i = this.mEvent;
            if (i == 5) {
                if (this.mEventValue != 0) {
                    sb.append(" deviceId:");
                    sb.append(this.mEventValue);
                }
                return sb.toString();
            } else if (i == 6) {
                return AudioPlaybackConfiguration.toLogFriendlyPlayerState(this.mEvent) + " portId:" + this.mEventValue + " mapped to player piid:" + this.mPlayerIId;
            } else if (i == 7) {
                sb.append(" source:");
                int i2 = this.mEventValue;
                if (i2 <= 0) {
                    sb.append("none ");
                } else {
                    if ((i2 & 1) != 0) {
                        sb.append("masterMute ");
                    }
                    if ((this.mEventValue & 2) != 0) {
                        sb.append("streamVolume ");
                    }
                    if ((this.mEventValue & 4) != 0) {
                        sb.append("streamMute ");
                    }
                    if ((this.mEventValue & 8) != 0) {
                        sb.append("appOps ");
                    }
                    if ((this.mEventValue & 16) != 0) {
                        sb.append("clientVolume ");
                    }
                    if ((this.mEventValue & 32) != 0) {
                        sb.append("volumeShaper ");
                    }
                }
                return sb.toString();
            } else {
                return sb.toString();
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class PlayerOpPlayAudioEvent extends EventLogger.Event {
        public final boolean mHasOp;
        public final int mPlayerIId;
        public final int mUid;

        public PlayerOpPlayAudioEvent(int i, boolean z, int i2) {
            this.mPlayerIId = i;
            this.mHasOp = z;
            this.mUid = i2;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return "player piid:" + this.mPlayerIId + " has OP_PLAY_AUDIO:" + this.mHasOp + " in uid:" + this.mUid;
        }
    }

    /* loaded from: classes.dex */
    public static final class NewPlayerEvent extends EventLogger.Event {
        public final int mClientPid;
        public final int mClientUid;
        public final AudioAttributes mPlayerAttr;
        public final int mPlayerIId;
        public final int mPlayerType;
        public final int mSessionId;

        public NewPlayerEvent(AudioPlaybackConfiguration audioPlaybackConfiguration) {
            this.mPlayerIId = audioPlaybackConfiguration.getPlayerInterfaceId();
            this.mPlayerType = audioPlaybackConfiguration.getPlayerType();
            this.mClientUid = audioPlaybackConfiguration.getClientUid();
            this.mClientPid = audioPlaybackConfiguration.getClientPid();
            this.mPlayerAttr = audioPlaybackConfiguration.getAudioAttributes();
            this.mSessionId = audioPlaybackConfiguration.getSessionId();
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return new String("new player piid:" + this.mPlayerIId + " uid/pid:" + this.mClientUid + "/" + this.mClientPid + " type:" + AudioPlaybackConfiguration.toLogFriendlyPlayerType(this.mPlayerType) + " attr:" + this.mPlayerAttr + " session:" + this.mSessionId);
        }
    }

    /* loaded from: classes.dex */
    public static abstract class VolumeShaperEvent extends EventLogger.Event {
        public final int mClientPid;
        public final int mClientUid;
        public final int mPlayerIId;
        public final boolean mSkipRamp;

        public abstract String getVSAction();

        public VolumeShaperEvent(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
            this.mPlayerIId = audioPlaybackConfiguration.getPlayerInterfaceId();
            this.mSkipRamp = z;
            this.mClientUid = audioPlaybackConfiguration.getClientUid();
            this.mClientPid = audioPlaybackConfiguration.getClientPid();
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return getVSAction() + " player piid:" + this.mPlayerIId + " uid/pid:" + this.mClientUid + "/" + this.mClientPid + " skip ramp:" + this.mSkipRamp;
        }
    }

    /* loaded from: classes.dex */
    public static final class DuckEvent extends VolumeShaperEvent {
        public final boolean mUseStrongDuck;

        @Override // com.android.server.audio.PlaybackActivityMonitor.VolumeShaperEvent
        public String getVSAction() {
            return this.mUseStrongDuck ? "ducking (strong)" : "ducking";
        }

        public DuckEvent(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z, boolean z2) {
            super(audioPlaybackConfiguration, z);
            this.mUseStrongDuck = z2;
        }
    }

    /* loaded from: classes.dex */
    public static final class FadeOutEvent extends VolumeShaperEvent {
        @Override // com.android.server.audio.PlaybackActivityMonitor.VolumeShaperEvent
        public String getVSAction() {
            return "fading out";
        }

        public FadeOutEvent(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
            super(audioPlaybackConfiguration, z);
        }
    }

    /* loaded from: classes.dex */
    public static final class AudioAttrEvent extends EventLogger.Event {
        public final AudioAttributes mPlayerAttr;
        public final int mPlayerIId;

        public AudioAttrEvent(int i, AudioAttributes audioAttributes) {
            this.mPlayerIId = i;
            this.mPlayerAttr = audioAttributes;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return new String("player piid:" + this.mPlayerIId + " new AudioAttributes:" + this.mPlayerAttr);
        }
    }

    /* loaded from: classes.dex */
    public static final class MuteAwaitConnectionEvent extends EventLogger.Event {
        public final int[] mUsagesToMute;

        public MuteAwaitConnectionEvent(int[] iArr) {
            this.mUsagesToMute = iArr;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return "muteAwaitConnection muting usages " + Arrays.toString(this.mUsagesToMute);
        }
    }

    /* loaded from: classes.dex */
    public static final class PlayerFormatEvent extends EventLogger.Event {
        public final AudioPlaybackConfiguration.FormatInfo mFormat;
        public final int mPlayerIId;

        public PlayerFormatEvent(int i, AudioPlaybackConfiguration.FormatInfo formatInfo) {
            this.mPlayerIId = i;
            this.mFormat = formatInfo;
        }

        @Override // com.android.server.utils.EventLogger.Event
        public String eventToString() {
            return new String("player piid:" + this.mPlayerIId + " format update:" + this.mFormat);
        }
    }

    public void muteAwaitConnection(int[] iArr, AudioDeviceAttributes audioDeviceAttributes, long j) {
        EventLogger eventLogger = sEventLogger;
        eventLogger.enqueueAndLog("muteAwaitConnection() dev:" + audioDeviceAttributes + " timeOutMs:" + j, 0, "AS.PlaybackActivityMon");
        synchronized (this.mPlayerLock) {
            mutePlayersExpectingDevice(iArr);
            this.mEventHandler.removeMessages(1);
            Handler handler = this.mEventHandler;
            handler.sendMessageDelayed(handler.obtainMessage(1, audioDeviceAttributes), j);
        }
    }

    public void cancelMuteAwaitConnection(String str) {
        EventLogger eventLogger = sEventLogger;
        eventLogger.enqueueAndLog("cancelMuteAwaitConnection() from:" + str, 0, "AS.PlaybackActivityMon");
        synchronized (this.mPlayerLock) {
            this.mEventHandler.removeMessages(1);
            unmutePlayersExpectingDevice();
        }
    }

    @GuardedBy({"mPlayerLock"})
    public final void mutePlayersExpectingDevice(int[] iArr) {
        sEventLogger.enqueue(new MuteAwaitConnectionEvent(iArr));
        this.mMutedUsagesAwaitingConnection = iArr;
        for (Integer num : this.mPlayers.keySet()) {
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(num);
            if (audioPlaybackConfiguration != null) {
                maybeMutePlayerAwaitingConnection(audioPlaybackConfiguration);
            }
        }
    }

    @GuardedBy({"mPlayerLock"})
    public final void maybeMutePlayerAwaitingConnection(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        int[] iArr = this.mMutedUsagesAwaitingConnection;
        if (iArr == null) {
            return;
        }
        for (int i : iArr) {
            if (i == audioPlaybackConfiguration.getAudioAttributes().getUsage()) {
                try {
                    sEventLogger.enqueue(new EventLogger.StringEvent("awaiting connection: muting piid:" + audioPlaybackConfiguration.getPlayerInterfaceId() + " uid:" + audioPlaybackConfiguration.getClientUid()).printLog("AS.PlaybackActivityMon"));
                    audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(MUTE_AWAIT_CONNECTION_VSHAPE, PLAY_SKIP_RAMP);
                    this.mMutedPlayersAwaitingConnection.add(Integer.valueOf(audioPlaybackConfiguration.getPlayerInterfaceId()));
                } catch (Exception e) {
                    Log.e("AS.PlaybackActivityMon", "awaiting connection: error muting player " + audioPlaybackConfiguration.getPlayerInterfaceId(), e);
                }
            }
        }
    }

    @GuardedBy({"mPlayerLock"})
    public final void unmutePlayersExpectingDevice() {
        this.mMutedUsagesAwaitingConnection = null;
        Iterator<Integer> it = this.mMutedPlayersAwaitingConnection.iterator();
        while (it.hasNext()) {
            int intValue = it.next().intValue();
            AudioPlaybackConfiguration audioPlaybackConfiguration = this.mPlayers.get(Integer.valueOf(intValue));
            if (audioPlaybackConfiguration != null) {
                try {
                    EventLogger eventLogger = sEventLogger;
                    eventLogger.enqueue(new EventLogger.StringEvent("unmuting piid:" + intValue).printLog("AS.PlaybackActivityMon"));
                    audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(MUTE_AWAIT_CONNECTION_VSHAPE, VolumeShaper.Operation.REVERSE);
                } catch (Exception e) {
                    Log.e("AS.PlaybackActivityMon", "Error unmuting player " + intValue + " uid:" + audioPlaybackConfiguration.getClientUid(), e);
                }
            }
        }
        this.mMutedPlayersAwaitingConnection.clear();
    }

    public final void initEventHandler() {
        HandlerThread handlerThread = new HandlerThread("AS.PlaybackActivityMon");
        this.mEventThread = handlerThread;
        handlerThread.start();
        this.mEventHandler = new Handler(this.mEventThread.getLooper()) { // from class: com.android.server.audio.PlaybackActivityMonitor.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                AudioPlaybackConfiguration audioPlaybackConfiguration;
                AudioPlaybackConfiguration audioPlaybackConfiguration2;
                int i = message.what;
                if (i == 1) {
                    EventLogger eventLogger = PlaybackActivityMonitor.sEventLogger;
                    eventLogger.enqueueAndLog("Timeout for muting waiting for " + ((AudioDeviceAttributes) message.obj) + ", unmuting", 0, "AS.PlaybackActivityMon");
                    synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                        PlaybackActivityMonitor.this.unmutePlayersExpectingDevice();
                    }
                    PlaybackActivityMonitor.this.mMuteAwaitConnectionTimeoutCb.accept((AudioDeviceAttributes) message.obj);
                } else if (i == 2) {
                    synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                        PlaybackActivityMonitor.this.mPortIdToPiid.put(message.arg1, message.arg2);
                    }
                } else if (i == 3) {
                    PersistableBundle persistableBundle = (PersistableBundle) message.obj;
                    if (persistableBundle == null) {
                        Log.w("AS.PlaybackActivityMon", "Received mute event with no extras");
                        return;
                    }
                    int i2 = persistableBundle.getInt("android.media.extra.PLAYER_EVENT_MUTE");
                    synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                        int i3 = message.arg1;
                        PlaybackActivityMonitor.sEventLogger.enqueue(new PlayerEvent(i3, 7, i2));
                        synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                            audioPlaybackConfiguration = (AudioPlaybackConfiguration) PlaybackActivityMonitor.this.mPlayers.get(Integer.valueOf(i3));
                        }
                        if (audioPlaybackConfiguration != null && audioPlaybackConfiguration.handleMutedEvent(i2)) {
                            PlaybackActivityMonitor.this.dispatchPlaybackChange(false);
                        }
                    }
                } else if (i == 4) {
                    int i4 = message.arg1;
                    if (i4 == -1) {
                        Log.w("AS.PlaybackActivityMon", "Received clear ports with invalid piid");
                        return;
                    }
                    synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                        while (true) {
                            int indexOfValue = PlaybackActivityMonitor.this.mPortIdToPiid.indexOfValue(i4);
                            if (indexOfValue >= 0) {
                                PlaybackActivityMonitor.this.mPortIdToPiid.removeAt(indexOfValue);
                            }
                        }
                    }
                } else if (i != 5) {
                } else {
                    PersistableBundle persistableBundle2 = (PersistableBundle) message.obj;
                    if (persistableBundle2 == null) {
                        Log.w("AS.PlaybackActivityMon", "Received format event with no extras");
                        return;
                    }
                    AudioPlaybackConfiguration.FormatInfo formatInfo = new AudioPlaybackConfiguration.FormatInfo(persistableBundle2.getBoolean("android.media.extra.PLAYER_EVENT_SPATIALIZED", false), persistableBundle2.getInt("android.media.extra.PLAYER_EVENT_CHANNEL_MASK", 0), persistableBundle2.getInt("android.media.extra.PLAYER_EVENT_SAMPLE_RATE", 0));
                    PlaybackActivityMonitor.sEventLogger.enqueue(new PlayerFormatEvent(message.arg1, formatInfo));
                    synchronized (PlaybackActivityMonitor.this.mPlayerLock) {
                        audioPlaybackConfiguration2 = (AudioPlaybackConfiguration) PlaybackActivityMonitor.this.mPlayers.get(Integer.valueOf(message.arg1));
                    }
                    if (audioPlaybackConfiguration2 == null || !audioPlaybackConfiguration2.handleFormatEvent(formatInfo)) {
                        return;
                    }
                    PlaybackActivityMonitor.this.dispatchPlaybackChange(false);
                }
            }
        };
    }
}
