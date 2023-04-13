package android.net.rtp;

import java.util.Arrays;
/* loaded from: classes.dex */
public class AudioCodec {
    public static final AudioCodec AMR;
    public static final AudioCodec GSM;
    public static final AudioCodec GSM_EFR;
    public static final AudioCodec PCMA;
    public static final AudioCodec PCMU;
    private static final AudioCodec[] sCodecs;
    public final String fmtp;
    public final String rtpmap;
    public final int type;

    static {
        AudioCodec audioCodec = new AudioCodec(0, "PCMU/8000", null);
        PCMU = audioCodec;
        AudioCodec audioCodec2 = new AudioCodec(8, "PCMA/8000", null);
        PCMA = audioCodec2;
        AudioCodec audioCodec3 = new AudioCodec(3, "GSM/8000", null);
        GSM = audioCodec3;
        AudioCodec audioCodec4 = new AudioCodec(96, "GSM-EFR/8000", null);
        GSM_EFR = audioCodec4;
        AudioCodec audioCodec5 = new AudioCodec(97, "AMR/8000", null);
        AMR = audioCodec5;
        sCodecs = new AudioCodec[]{audioCodec4, audioCodec5, audioCodec3, audioCodec, audioCodec2};
    }

    private AudioCodec(int type, String rtpmap, String fmtp) {
        this.type = type;
        this.rtpmap = rtpmap;
        this.fmtp = fmtp;
    }

    public static AudioCodec[] getCodecs() {
        AudioCodec[] audioCodecArr = sCodecs;
        return (AudioCodec[]) Arrays.copyOf(audioCodecArr, audioCodecArr.length);
    }

    public static AudioCodec getCodec(int type, String rtpmap, String fmtp) {
        if (type < 0 || type > 127) {
            return null;
        }
        AudioCodec hint = null;
        int i = 0;
        if (rtpmap != null) {
            String clue = rtpmap.trim().toUpperCase();
            AudioCodec[] audioCodecArr = sCodecs;
            int length = audioCodecArr.length;
            while (true) {
                if (i >= length) {
                    break;
                }
                AudioCodec codec = audioCodecArr[i];
                if (!clue.startsWith(codec.rtpmap)) {
                    i++;
                } else {
                    String channels = clue.substring(codec.rtpmap.length());
                    if (channels.length() == 0 || channels.equals("/1")) {
                        hint = codec;
                    }
                }
            }
        } else if (type < 96) {
            AudioCodec[] audioCodecArr2 = sCodecs;
            int length2 = audioCodecArr2.length;
            while (true) {
                if (i >= length2) {
                    break;
                }
                AudioCodec codec2 = audioCodecArr2[i];
                if (type != codec2.type) {
                    i++;
                } else {
                    hint = codec2;
                    rtpmap = codec2.rtpmap;
                    break;
                }
            }
        }
        if (hint == null) {
            return null;
        }
        if (hint == AMR && fmtp != null) {
            String clue2 = fmtp.toLowerCase();
            if (clue2.contains("crc=1") || clue2.contains("robust-sorting=1") || clue2.contains("interleaving=")) {
                return null;
            }
        }
        return new AudioCodec(type, rtpmap, fmtp);
    }
}
