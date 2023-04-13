package com.android.server.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioPlaybackConfiguration;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes2.dex */
public class AudioPlayerStateMonitor {
    public static final boolean DEBUG = MediaSessionService.DEBUG;
    public static String TAG = "AudioPlayerStateMonitor";
    public static AudioPlayerStateMonitor sInstance;
    public final Object mLock = new Object();
    @GuardedBy({"mLock"})
    public final Map<OnAudioPlayerActiveStateChangedListener, MessageHandler> mListenerMap = new ArrayMap();
    @GuardedBy({"mLock"})
    public final Set<Integer> mActiveAudioUids = new ArraySet();
    @GuardedBy({"mLock"})
    public ArrayMap<Integer, AudioPlaybackConfiguration> mPrevActiveAudioPlaybackConfigs = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public final List<Integer> mSortedAudioPlaybackClientUids = new ArrayList();

    /* loaded from: classes2.dex */
    public interface OnAudioPlayerActiveStateChangedListener {
        void onAudioPlayerActiveStateChanged(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z);
    }

    /* loaded from: classes2.dex */
    public static final class MessageHandler extends Handler {
        public final OnAudioPlayerActiveStateChangedListener mListener;

        public MessageHandler(Looper looper, OnAudioPlayerActiveStateChangedListener onAudioPlayerActiveStateChangedListener) {
            super(looper);
            this.mListener = onAudioPlayerActiveStateChangedListener;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 1) {
                return;
            }
            this.mListener.onAudioPlayerActiveStateChanged((AudioPlaybackConfiguration) message.obj, message.arg1 != 0);
        }

        public void sendAudioPlayerActiveStateChangedMessage(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
            obtainMessage(1, z ? 1 : 0, 0, audioPlaybackConfiguration).sendToTarget();
        }
    }

    public static AudioPlayerStateMonitor getInstance(Context context) {
        AudioPlayerStateMonitor audioPlayerStateMonitor;
        synchronized (AudioPlayerStateMonitor.class) {
            if (sInstance == null) {
                sInstance = new AudioPlayerStateMonitor(context);
            }
            audioPlayerStateMonitor = sInstance;
        }
        return audioPlayerStateMonitor;
    }

    public AudioPlayerStateMonitor(Context context) {
        ((AudioManager) context.getSystemService("audio")).registerAudioPlaybackCallback(new AudioManagerPlaybackListener(), null);
    }

    public void registerListener(OnAudioPlayerActiveStateChangedListener onAudioPlayerActiveStateChangedListener, Handler handler) {
        synchronized (this.mLock) {
            this.mListenerMap.put(onAudioPlayerActiveStateChangedListener, new MessageHandler(handler == null ? Looper.myLooper() : handler.getLooper(), onAudioPlayerActiveStateChangedListener));
        }
    }

    public List<Integer> getSortedAudioPlaybackClientUids() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mLock) {
            arrayList.addAll(this.mSortedAudioPlaybackClientUids);
        }
        return arrayList;
    }

    public boolean isPlaybackActive(int i) {
        boolean contains;
        synchronized (this.mLock) {
            contains = this.mActiveAudioUids.contains(Integer.valueOf(i));
        }
        return contains;
    }

    public void cleanUpAudioPlaybackUids(int i) {
        synchronized (this.mLock) {
            int identifier = UserHandle.getUserHandleForUid(i).getIdentifier();
            for (int size = this.mSortedAudioPlaybackClientUids.size() - 1; size >= 0 && this.mSortedAudioPlaybackClientUids.get(size).intValue() != i; size--) {
                int intValue = this.mSortedAudioPlaybackClientUids.get(size).intValue();
                if (identifier == UserHandle.getUserHandleForUid(intValue).getIdentifier() && !isPlaybackActive(intValue)) {
                    this.mSortedAudioPlaybackClientUids.remove(size);
                }
            }
        }
    }

    public void dump(Context context, PrintWriter printWriter, String str) {
        synchronized (this.mLock) {
            printWriter.println(str + "Audio playback (lastly played comes first)");
            String str2 = str + "  ";
            for (int i = 0; i < this.mSortedAudioPlaybackClientUids.size(); i++) {
                int intValue = this.mSortedAudioPlaybackClientUids.get(i).intValue();
                printWriter.print(str2 + "uid=" + intValue + " packages=");
                String[] packagesForUid = context.getPackageManager().getPackagesForUid(intValue);
                if (packagesForUid != null && packagesForUid.length > 0) {
                    for (int i2 = 0; i2 < packagesForUid.length; i2++) {
                        printWriter.print(packagesForUid[i2] + " ");
                    }
                }
                printWriter.println();
            }
        }
    }

    @GuardedBy({"mLock"})
    public final void sendAudioPlayerActiveStateChangedMessageLocked(AudioPlaybackConfiguration audioPlaybackConfiguration, boolean z) {
        for (MessageHandler messageHandler : this.mListenerMap.values()) {
            messageHandler.sendAudioPlayerActiveStateChangedMessage(audioPlaybackConfiguration, z);
        }
    }

    /* loaded from: classes2.dex */
    public class AudioManagerPlaybackListener extends AudioManager.AudioPlaybackCallback {
        public AudioManagerPlaybackListener() {
        }

        @Override // android.media.AudioManager.AudioPlaybackCallback
        public void onPlaybackConfigChanged(List<AudioPlaybackConfiguration> list) {
            int i;
            synchronized (AudioPlayerStateMonitor.this.mLock) {
                AudioPlayerStateMonitor.this.mActiveAudioUids.clear();
                ArrayMap<Integer, AudioPlaybackConfiguration> arrayMap = new ArrayMap<>();
                for (AudioPlaybackConfiguration audioPlaybackConfiguration : list) {
                    if (audioPlaybackConfiguration.isActive()) {
                        AudioPlayerStateMonitor.this.mActiveAudioUids.add(Integer.valueOf(audioPlaybackConfiguration.getClientUid()));
                        arrayMap.put(Integer.valueOf(audioPlaybackConfiguration.getPlayerInterfaceId()), audioPlaybackConfiguration);
                    }
                }
                for (int i2 = 0; i2 < arrayMap.size(); i2++) {
                    AudioPlaybackConfiguration valueAt = arrayMap.valueAt(i2);
                    int clientUid = valueAt.getClientUid();
                    if (!AudioPlayerStateMonitor.this.mPrevActiveAudioPlaybackConfigs.containsKey(Integer.valueOf(valueAt.getPlayerInterfaceId()))) {
                        if (AudioPlayerStateMonitor.DEBUG) {
                            Log.d(AudioPlayerStateMonitor.TAG, "Found a new active media playback. " + valueAt);
                        }
                        int indexOf = AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.indexOf(Integer.valueOf(clientUid));
                        if (indexOf != 0) {
                            if (indexOf > 0) {
                                AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.remove(indexOf);
                            }
                            AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.add(0, Integer.valueOf(clientUid));
                        }
                    }
                }
                if (AudioPlayerStateMonitor.this.mActiveAudioUids.size() > 0) {
                    AudioPlayerStateMonitor audioPlayerStateMonitor = AudioPlayerStateMonitor.this;
                    if (!audioPlayerStateMonitor.mActiveAudioUids.contains(audioPlayerStateMonitor.mSortedAudioPlaybackClientUids.get(0))) {
                        int i3 = 1;
                        while (true) {
                            if (i3 >= AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.size()) {
                                i3 = -1;
                                i = -1;
                                break;
                            }
                            i = AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.get(i3).intValue();
                            if (AudioPlayerStateMonitor.this.mActiveAudioUids.contains(Integer.valueOf(i))) {
                                break;
                            }
                            i3++;
                        }
                        while (i3 > 0) {
                            List<Integer> list2 = AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids;
                            list2.set(i3, list2.get(i3 - 1));
                            i3--;
                        }
                        AudioPlayerStateMonitor.this.mSortedAudioPlaybackClientUids.set(0, Integer.valueOf(i));
                    }
                }
                for (AudioPlaybackConfiguration audioPlaybackConfiguration2 : list) {
                    if ((AudioPlayerStateMonitor.this.mPrevActiveAudioPlaybackConfigs.remove(Integer.valueOf(audioPlaybackConfiguration2.getPlayerInterfaceId())) != null) != audioPlaybackConfiguration2.isActive()) {
                        AudioPlayerStateMonitor.this.sendAudioPlayerActiveStateChangedMessageLocked(audioPlaybackConfiguration2, false);
                    }
                }
                for (AudioPlaybackConfiguration audioPlaybackConfiguration3 : AudioPlayerStateMonitor.this.mPrevActiveAudioPlaybackConfigs.values()) {
                    AudioPlayerStateMonitor.this.sendAudioPlayerActiveStateChangedMessageLocked(audioPlaybackConfiguration3, true);
                }
                AudioPlayerStateMonitor.this.mPrevActiveAudioPlaybackConfigs = arrayMap;
            }
        }
    }
}
