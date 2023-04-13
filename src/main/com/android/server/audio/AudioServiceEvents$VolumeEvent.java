package com.android.server.audio;

import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.MediaMetrics;
import android.net.INetd;
import com.android.server.utils.EventLogger;
/* loaded from: classes.dex */
public final class AudioServiceEvents$VolumeEvent extends EventLogger.Event {
    public final String mCaller;
    public final String mGroupName;
    public final int mOp;
    public final int mStream;
    public final int mVal1;
    public final int mVal2;

    public AudioServiceEvents$VolumeEvent(int i, int i2, int i3, int i4, String str) {
        this.mOp = i;
        this.mStream = i2;
        this.mVal1 = i3;
        this.mVal2 = i4;
        this.mCaller = str;
        this.mGroupName = null;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, int i2, int i3) {
        this.mOp = i;
        this.mVal1 = i2;
        this.mVal2 = i3;
        this.mStream = -1;
        this.mCaller = null;
        this.mGroupName = null;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, int i2) {
        this.mOp = i;
        this.mVal1 = i2;
        this.mVal2 = 0;
        this.mStream = -1;
        this.mCaller = null;
        this.mGroupName = null;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, boolean z, int i2, int i3) {
        this.mOp = i;
        this.mStream = i2;
        this.mVal1 = i3;
        this.mVal2 = z ? 1 : 0;
        this.mCaller = null;
        this.mGroupName = null;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, int i2, int i3, int i4) {
        this.mOp = i;
        this.mStream = i3;
        this.mVal1 = i4;
        this.mVal2 = i2;
        this.mCaller = null;
        this.mGroupName = null;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, String str, int i2, int i3, String str2) {
        this.mOp = i;
        this.mStream = -1;
        this.mVal1 = i2;
        this.mVal2 = i3;
        this.mCaller = str2;
        this.mGroupName = str;
        logMetricEvent();
    }

    public AudioServiceEvents$VolumeEvent(int i, int i2, boolean z) {
        this.mOp = i;
        this.mStream = i2;
        this.mVal1 = z ? 1 : 0;
        this.mVal2 = 0;
        this.mCaller = null;
        this.mGroupName = null;
        logMetricEvent();
    }

    public final void logMetricEvent() {
        String str;
        int i = this.mOp;
        String str2 = INetd.IF_STATE_UP;
        switch (i) {
            case 0:
            case 1:
            case 5:
                if (i == 0) {
                    str = "adjustSuggestedStreamVolume";
                } else if (i == 1) {
                    str = "adjustStreamVolume";
                } else if (i != 5) {
                    return;
                } else {
                    str = "adjustStreamVolumeForUid";
                }
                MediaMetrics.Item item = new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.CALLING_PACKAGE, this.mCaller);
                MediaMetrics.Key key = MediaMetrics.Property.DIRECTION;
                if (this.mVal1 <= 0) {
                    str2 = INetd.IF_STATE_DOWN;
                }
                item.set(key, str2).set(MediaMetrics.Property.EVENT, str).set(MediaMetrics.Property.FLAGS, Integer.valueOf(this.mVal2)).set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(this.mStream)).record();
                return;
            case 2:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.CALLING_PACKAGE, this.mCaller).set(MediaMetrics.Property.EVENT, "setStreamVolume").set(MediaMetrics.Property.FLAGS, Integer.valueOf(this.mVal2)).set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(this.mStream)).record();
                return;
            case 3:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "setHearingAidVolume").set(MediaMetrics.Property.GAIN_DB, Double.valueOf(this.mVal2)).set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).record();
                return;
            case 4:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "setAvrcpVolume").set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).record();
                return;
            case 6:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "voiceActivityHearingAid").set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).set(MediaMetrics.Property.STATE, this.mVal2 == 1 ? "active" : "inactive").set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(this.mStream)).record();
                return;
            case 7:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "modeChangeHearingAid").set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).set(MediaMetrics.Property.MODE, AudioSystem.modeToString(this.mVal2)).set(MediaMetrics.Property.STREAM_TYPE, AudioSystem.streamToString(this.mStream)).record();
                return;
            case 8:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.CALLING_PACKAGE, this.mCaller).set(MediaMetrics.Property.EVENT, "setVolumeIndexForAttributes").set(MediaMetrics.Property.FLAGS, Integer.valueOf(this.mVal2)).set(MediaMetrics.Property.GROUP, this.mGroupName).set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).record();
                return;
            case 9:
            default:
                return;
            case 10:
                new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.EVENT, "setLeAudioVolume").set(MediaMetrics.Property.INDEX, Integer.valueOf(this.mVal1)).set(MediaMetrics.Property.MAX_INDEX, Integer.valueOf(this.mVal2)).record();
                return;
            case 11:
                MediaMetrics.Item item2 = new MediaMetrics.Item("audio.volume.event").set(MediaMetrics.Property.CALLING_PACKAGE, this.mCaller);
                MediaMetrics.Key key2 = MediaMetrics.Property.DIRECTION;
                if (this.mVal1 <= 0) {
                    str2 = INetd.IF_STATE_DOWN;
                }
                item2.set(key2, str2).set(MediaMetrics.Property.EVENT, "adjustVolumeGroupVolume").set(MediaMetrics.Property.FLAGS, Integer.valueOf(this.mVal2)).set(MediaMetrics.Property.GROUP, this.mGroupName).record();
                return;
        }
    }

    @Override // com.android.server.utils.EventLogger.Event
    public String eventToString() {
        switch (this.mOp) {
            case 0:
                return "adjustSuggestedStreamVolume(sugg:" + AudioSystem.streamToString(this.mStream) + " dir:" + AudioManager.adjustToString(this.mVal1) + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            case 1:
                return "adjustStreamVolume(stream:" + AudioSystem.streamToString(this.mStream) + " dir:" + AudioManager.adjustToString(this.mVal1) + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            case 2:
                return "setStreamVolume(stream:" + AudioSystem.streamToString(this.mStream) + " index:" + this.mVal1 + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            case 3:
                return "setHearingAidVolume: index:" + this.mVal1 + " gain dB:" + this.mVal2;
            case 4:
                return "setAvrcpVolume: index:" + this.mVal1;
            case 5:
                return "adjustStreamVolumeForUid(stream:" + AudioSystem.streamToString(this.mStream) + " dir:" + AudioManager.adjustToString(this.mVal1) + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            case 6:
                StringBuilder sb = new StringBuilder("Voice activity change (");
                sb.append(this.mVal2 == 1 ? "active" : "inactive");
                sb.append(") causes setting HEARING_AID volume to idx:");
                sb.append(this.mVal1);
                sb.append(" stream:");
                sb.append(AudioSystem.streamToString(this.mStream));
                return sb.toString();
            case 7:
                return "setMode(" + AudioSystem.modeToString(this.mVal2) + ") causes setting HEARING_AID volume to idx:" + this.mVal1 + " stream:" + AudioSystem.streamToString(this.mStream);
            case 8:
                return "setVolumeIndexForAttributes(group: group: " + this.mGroupName + " index:" + this.mVal1 + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            case 9:
                StringBuilder sb2 = new StringBuilder("VolumeStreamState.muteInternally(stream:");
                sb2.append(AudioSystem.streamToString(this.mStream));
                sb2.append(this.mVal1 == 1 ? ", muted)" : ", unmuted)");
                return sb2.toString();
            case 10:
                return "setLeAudioVolume: index:" + this.mVal1 + " maxIndex:" + this.mVal2;
            case 11:
                return "adjustVolumeGroupVolume(group:" + this.mGroupName + " dir:" + AudioManager.adjustToString(this.mVal1) + " flags:0x" + Integer.toHexString(this.mVal2) + ") from " + this.mCaller;
            default:
                return "FIXME invalid op:" + this.mOp;
        }
    }
}
