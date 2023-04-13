package android.media;

import android.security.keystore.KeyProperties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/* loaded from: classes2.dex */
public class AudioPort {
    public static final int ROLE_NONE = 0;
    public static final int ROLE_SINK = 2;
    public static final int ROLE_SOURCE = 1;
    private static final String TAG = "AudioPort";
    public static final int TYPE_DEVICE = 1;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_SESSION = 3;
    public static final int TYPE_SUBMIX = 2;
    private AudioPortConfig mActiveConfig;
    private final int[] mChannelIndexMasks;
    private final int[] mChannelMasks;
    private final List<AudioDescriptor> mDescriptors;
    private final int[] mFormats;
    private final AudioGain[] mGains;
    AudioHandle mHandle;
    private final String mName;
    private final List<AudioProfile> mProfiles;
    protected final int mRole;
    private final int[] mSamplingRates;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioPort(AudioHandle handle, int role, String name, int[] samplingRates, int[] channelMasks, int[] channelIndexMasks, int[] formats, AudioGain[] gains) {
        int[] iArr = formats;
        this.mHandle = handle;
        this.mRole = role;
        this.mName = name;
        this.mSamplingRates = samplingRates;
        this.mChannelMasks = channelMasks;
        this.mChannelIndexMasks = channelIndexMasks;
        this.mFormats = iArr;
        this.mGains = gains;
        this.mProfiles = new ArrayList();
        if (iArr != null) {
            int length = iArr.length;
            int i = 0;
            while (i < length) {
                int format = iArr[i];
                this.mProfiles.add(new AudioProfile(format, samplingRates, channelMasks, channelIndexMasks, 0));
                i++;
                iArr = formats;
            }
        }
        this.mDescriptors = new ArrayList();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioPort(AudioHandle handle, int role, String name, List<AudioProfile> profiles, AudioGain[] gains, List<AudioDescriptor> descriptors) {
        this.mHandle = handle;
        this.mRole = role;
        this.mName = name;
        this.mProfiles = profiles;
        this.mDescriptors = descriptors;
        this.mGains = gains;
        Set<Integer> formats = new HashSet<>();
        Set<Integer> samplingRates = new HashSet<>();
        Set<Integer> channelMasks = new HashSet<>();
        Set<Integer> channelIndexMasks = new HashSet<>();
        for (AudioProfile profile : profiles) {
            formats.add(Integer.valueOf(profile.getFormat()));
            samplingRates.addAll((Collection) Arrays.stream(profile.getSampleRates()).boxed().collect(Collectors.toList()));
            channelMasks.addAll((Collection) Arrays.stream(profile.getChannelMasks()).boxed().collect(Collectors.toList()));
            channelIndexMasks.addAll((Collection) Arrays.stream(profile.getChannelIndexMasks()).boxed().collect(Collectors.toList()));
        }
        this.mSamplingRates = samplingRates.stream().mapToInt(new AudioPort$$ExternalSyntheticLambda0()).toArray();
        this.mChannelMasks = channelMasks.stream().mapToInt(new AudioPort$$ExternalSyntheticLambda0()).toArray();
        this.mChannelIndexMasks = channelIndexMasks.stream().mapToInt(new AudioPort$$ExternalSyntheticLambda0()).toArray();
        this.mFormats = formats.stream().mapToInt(new AudioPort$$ExternalSyntheticLambda0()).toArray();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioHandle handle() {
        return this.mHandle;
    }

    /* renamed from: id */
    public int m150id() {
        return this.mHandle.m152id();
    }

    public int role() {
        return this.mRole;
    }

    public String name() {
        return this.mName;
    }

    public int[] samplingRates() {
        return this.mSamplingRates;
    }

    public int[] channelMasks() {
        return this.mChannelMasks;
    }

    public int[] channelIndexMasks() {
        return this.mChannelIndexMasks;
    }

    public int[] formats() {
        return this.mFormats;
    }

    public List<AudioProfile> profiles() {
        return this.mProfiles;
    }

    public List<AudioDescriptor> audioDescriptors() {
        return this.mDescriptors;
    }

    public AudioGain[] gains() {
        return this.mGains;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AudioGain gain(int index) {
        if (index >= 0) {
            AudioGain[] audioGainArr = this.mGains;
            if (index >= audioGainArr.length) {
                return null;
            }
            return audioGainArr[index];
        }
        return null;
    }

    public AudioPortConfig buildConfig(int samplingRate, int channelMask, int format, AudioGainConfig gain) {
        return new AudioPortConfig(this, samplingRate, channelMask, format, gain);
    }

    public AudioPortConfig activeConfig() {
        return this.mActiveConfig;
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof AudioPort)) {
            return false;
        }
        AudioPort ap = (AudioPort) o;
        return this.mHandle.equals(ap.handle());
    }

    public int hashCode() {
        return this.mHandle.hashCode();
    }

    public String toString() {
        String role = Integer.toString(this.mRole);
        switch (this.mRole) {
            case 0:
                role = KeyProperties.DIGEST_NONE;
                break;
            case 1:
                role = "SOURCE";
                break;
            case 2:
                role = "SINK";
                break;
        }
        return "{mHandle: " + this.mHandle + ", mRole: " + role + "}";
    }
}
