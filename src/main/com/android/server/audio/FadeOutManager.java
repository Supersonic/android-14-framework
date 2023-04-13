package com.android.server.audio;

import android.media.AudioAttributes;
import android.media.AudioPlaybackConfiguration;
import android.media.VolumeShaper;
import android.util.Log;
import com.android.internal.util.ArrayUtils;
import com.android.server.audio.PlaybackActivityMonitor;
import com.android.server.utils.EventLogger;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
/* loaded from: classes.dex */
public final class FadeOutManager {
    public static final int[] FADEABLE_USAGES;
    public static final VolumeShaper.Configuration FADEOUT_VSHAPE = new VolumeShaper.Configuration.Builder().setId(2).setCurve(new float[]{0.0f, 0.25f, 1.0f}, new float[]{1.0f, 0.65f, 0.0f}).setOptionFlags(2).setDuration(2000).build();
    public static final VolumeShaper.Operation PLAY_CREATE_IF_NEEDED;
    public static final VolumeShaper.Operation PLAY_SKIP_RAMP;
    public static final int[] UNFADEABLE_CONTENT_TYPES;
    public static final int[] UNFADEABLE_PLAYER_TYPES;
    public final HashMap<Integer, FadedOutApp> mFadedApps = new HashMap<>();

    static {
        VolumeShaper.Operation build = new VolumeShaper.Operation.Builder(VolumeShaper.Operation.PLAY).createIfNeeded().build();
        PLAY_CREATE_IF_NEEDED = build;
        UNFADEABLE_PLAYER_TYPES = new int[]{13, 3};
        UNFADEABLE_CONTENT_TYPES = new int[]{1};
        FADEABLE_USAGES = new int[]{14, 1};
        PLAY_SKIP_RAMP = new VolumeShaper.Operation.Builder(build).setXOffset(1.0f).build();
    }

    public static boolean canCauseFadeOut(FocusRequester focusRequester, FocusRequester focusRequester2) {
        return focusRequester.getAudioAttributes().getContentType() != 1 && (focusRequester2.getGrantFlags() & 2) == 0;
    }

    public static boolean canBeFadedOut(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        return (ArrayUtils.contains(UNFADEABLE_PLAYER_TYPES, audioPlaybackConfiguration.getPlayerType()) || ArrayUtils.contains(UNFADEABLE_CONTENT_TYPES, audioPlaybackConfiguration.getAudioAttributes().getContentType()) || !ArrayUtils.contains(FADEABLE_USAGES, audioPlaybackConfiguration.getAudioAttributes().getUsage())) ? false : true;
    }

    public static long getFadeOutDurationOnFocusLossMillis(AudioAttributes audioAttributes) {
        return (!ArrayUtils.contains(UNFADEABLE_CONTENT_TYPES, audioAttributes.getContentType()) && ArrayUtils.contains(FADEABLE_USAGES, audioAttributes.getUsage())) ? 2000L : 0L;
    }

    public synchronized void fadeOutUid(int i, ArrayList<AudioPlaybackConfiguration> arrayList) {
        Log.i("AudioService.FadeOutManager", "fadeOutUid() uid:" + i);
        if (!this.mFadedApps.containsKey(Integer.valueOf(i))) {
            this.mFadedApps.put(Integer.valueOf(i), new FadedOutApp(i));
        }
        FadedOutApp fadedOutApp = this.mFadedApps.get(Integer.valueOf(i));
        Iterator<AudioPlaybackConfiguration> it = arrayList.iterator();
        while (it.hasNext()) {
            fadedOutApp.addFade(it.next(), false);
        }
    }

    public synchronized void unfadeOutUid(int i, HashMap<Integer, AudioPlaybackConfiguration> hashMap) {
        Log.i("AudioService.FadeOutManager", "unfadeOutUid() uid:" + i);
        FadedOutApp remove = this.mFadedApps.remove(Integer.valueOf(i));
        if (remove == null) {
            return;
        }
        remove.removeUnfadeAll(hashMap);
    }

    public synchronized void checkFade(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        FadedOutApp fadedOutApp = this.mFadedApps.get(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
        if (fadedOutApp == null) {
            return;
        }
        fadedOutApp.addFade(audioPlaybackConfiguration, true);
    }

    public synchronized void removeReleased(AudioPlaybackConfiguration audioPlaybackConfiguration) {
        FadedOutApp fadedOutApp = this.mFadedApps.get(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
        if (fadedOutApp == null) {
            return;
        }
        fadedOutApp.removeReleased(audioPlaybackConfiguration);
    }

    public synchronized void dump(PrintWriter printWriter) {
        for (FadedOutApp fadedOutApp : this.mFadedApps.values()) {
            fadedOutApp.dump(printWriter);
        }
    }

    /* loaded from: classes.dex */
    public static final class FadedOutApp {
        public final ArrayList<Integer> mFadedPlayers = new ArrayList<>();
        public final int mUid;

        public FadedOutApp(int i) {
            this.mUid = i;
        }

        public void dump(PrintWriter printWriter) {
            printWriter.print("\t uid:" + this.mUid + " piids:");
            Iterator<Integer> it = this.mFadedPlayers.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                printWriter.print(" " + intValue);
            }
            printWriter.println("");
        }

        public void addFade(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
            int intValue = new Integer(audioPlaybackConfiguration.getPlayerInterfaceId()).intValue();
            if (this.mFadedPlayers.contains(Integer.valueOf(intValue))) {
                return;
            }
            try {
                PlaybackActivityMonitor.sEventLogger.enqueue(new PlaybackActivityMonitor.FadeOutEvent(audioPlaybackConfiguration, z).printLog("AudioService.FadeOutManager"));
                audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(FadeOutManager.FADEOUT_VSHAPE, z ? FadeOutManager.PLAY_SKIP_RAMP : FadeOutManager.PLAY_CREATE_IF_NEEDED);
                this.mFadedPlayers.add(Integer.valueOf(intValue));
            } catch (Exception e) {
                Log.e("AudioService.FadeOutManager", "Error fading out player piid:" + intValue + " uid:" + audioPlaybackConfiguration.getClientUid(), e);
            }
        }

        public void removeUnfadeAll(HashMap<Integer, AudioPlaybackConfiguration> hashMap) {
            Iterator<Integer> it = this.mFadedPlayers.iterator();
            while (it.hasNext()) {
                int intValue = it.next().intValue();
                AudioPlaybackConfiguration audioPlaybackConfiguration = hashMap.get(Integer.valueOf(intValue));
                if (audioPlaybackConfiguration != null) {
                    try {
                        EventLogger eventLogger = PlaybackActivityMonitor.sEventLogger;
                        eventLogger.enqueue(new EventLogger.StringEvent("unfading out piid:" + intValue).printLog("AudioService.FadeOutManager"));
                        audioPlaybackConfiguration.getPlayerProxy().applyVolumeShaper(FadeOutManager.FADEOUT_VSHAPE, VolumeShaper.Operation.REVERSE);
                    } catch (Exception e) {
                        Log.e("AudioService.FadeOutManager", "Error unfading out player piid:" + intValue + " uid:" + this.mUid, e);
                    }
                }
            }
            this.mFadedPlayers.clear();
        }

        public void removeReleased(AudioPlaybackConfiguration audioPlaybackConfiguration) {
            this.mFadedPlayers.remove(new Integer(audioPlaybackConfiguration.getPlayerInterfaceId()));
        }
    }
}
