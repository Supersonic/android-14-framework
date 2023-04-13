package com.android.server.hdmi;

import java.util.Objects;
/* loaded from: classes.dex */
public class AudioStatus {
    public boolean mMute;
    public int mVolume;

    public AudioStatus(int i, boolean z) {
        this.mVolume = i;
        this.mMute = z;
    }

    public int getVolume() {
        return this.mVolume;
    }

    public boolean getMute() {
        return this.mMute;
    }

    public boolean equals(Object obj) {
        if (obj instanceof AudioStatus) {
            AudioStatus audioStatus = (AudioStatus) obj;
            return this.mVolume == audioStatus.mVolume && this.mMute == audioStatus.mMute;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mVolume), Boolean.valueOf(this.mMute));
    }

    public String toString() {
        return "AudioStatus mVolume:" + this.mVolume + " mMute:" + this.mMute;
    }
}
