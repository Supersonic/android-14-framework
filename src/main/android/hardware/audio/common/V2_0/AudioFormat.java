package android.hardware.audio.common.V2_0;
/* loaded from: classes.dex */
public final class AudioFormat {
    public static final String toString(int i) {
        if (i == -1) {
            return "INVALID";
        }
        if (i == 0) {
            return "DEFAULT";
        }
        if (i == 0) {
            return "PCM";
        }
        if (i == 16777216) {
            return "MP3";
        }
        if (i == 33554432) {
            return "AMR_NB";
        }
        if (i == 50331648) {
            return "AMR_WB";
        }
        if (i == 67108864) {
            return "AAC";
        }
        if (i == 83886080) {
            return "HE_AAC_V1";
        }
        if (i == 100663296) {
            return "HE_AAC_V2";
        }
        if (i == 117440512) {
            return "VORBIS";
        }
        if (i == 134217728) {
            return "OPUS";
        }
        if (i == 150994944) {
            return "AC3";
        }
        if (i == 167772160) {
            return "E_AC3";
        }
        if (i == 184549376) {
            return "DTS";
        }
        if (i == 201326592) {
            return "DTS_HD";
        }
        if (i == 218103808) {
            return "IEC61937";
        }
        if (i == 234881024) {
            return "DOLBY_TRUEHD";
        }
        if (i == 268435456) {
            return "EVRC";
        }
        if (i == 285212672) {
            return "EVRCB";
        }
        if (i == 301989888) {
            return "EVRCWB";
        }
        if (i == 318767104) {
            return "EVRCNW";
        }
        if (i == 335544320) {
            return "AAC_ADIF";
        }
        if (i == 352321536) {
            return "WMA";
        }
        if (i == 369098752) {
            return "WMA_PRO";
        }
        if (i == 385875968) {
            return "AMR_WB_PLUS";
        }
        if (i == 402653184) {
            return "MP2";
        }
        if (i == 419430400) {
            return "QCELP";
        }
        if (i == 436207616) {
            return "DSD";
        }
        if (i == 452984832) {
            return "FLAC";
        }
        if (i == 469762048) {
            return "ALAC";
        }
        if (i == 486539264) {
            return "APE";
        }
        if (i == 503316480) {
            return "AAC_ADTS";
        }
        if (i == 520093696) {
            return "SBC";
        }
        if (i == 536870912) {
            return "APTX";
        }
        if (i == 553648128) {
            return "APTX_HD";
        }
        if (i == 570425344) {
            return "AC4";
        }
        if (i == 587202560) {
            return "LDAC";
        }
        if (i == -16777216) {
            return "MAIN_MASK";
        }
        if (i == 16777215) {
            return "SUB_MASK";
        }
        if (i == 1) {
            return "PCM_SUB_16_BIT";
        }
        if (i == 2) {
            return "PCM_SUB_8_BIT";
        }
        if (i == 3) {
            return "PCM_SUB_32_BIT";
        }
        if (i == 4) {
            return "PCM_SUB_8_24_BIT";
        }
        if (i == 5) {
            return "PCM_SUB_FLOAT";
        }
        if (i == 6) {
            return "PCM_SUB_24_BIT_PACKED";
        }
        if (i == 0) {
            return "MP3_SUB_NONE";
        }
        if (i == 0) {
            return "AMR_SUB_NONE";
        }
        if (i == 1) {
            return "AAC_SUB_MAIN";
        }
        if (i == 2) {
            return "AAC_SUB_LC";
        }
        if (i == 4) {
            return "AAC_SUB_SSR";
        }
        if (i == 8) {
            return "AAC_SUB_LTP";
        }
        if (i == 16) {
            return "AAC_SUB_HE_V1";
        }
        if (i == 32) {
            return "AAC_SUB_SCALABLE";
        }
        if (i == 64) {
            return "AAC_SUB_ERLC";
        }
        if (i == 128) {
            return "AAC_SUB_LD";
        }
        if (i == 256) {
            return "AAC_SUB_HE_V2";
        }
        if (i == 512) {
            return "AAC_SUB_ELD";
        }
        if (i == 0) {
            return "VORBIS_SUB_NONE";
        }
        if (i == 1) {
            return "PCM_16_BIT";
        }
        if (i == 2) {
            return "PCM_8_BIT";
        }
        if (i == 3) {
            return "PCM_32_BIT";
        }
        if (i == 4) {
            return "PCM_8_24_BIT";
        }
        if (i == 5) {
            return "PCM_FLOAT";
        }
        if (i == 6) {
            return "PCM_24_BIT_PACKED";
        }
        if (i == 67108865) {
            return "AAC_MAIN";
        }
        if (i == 67108866) {
            return "AAC_LC";
        }
        if (i == 67108868) {
            return "AAC_SSR";
        }
        if (i == 67108872) {
            return "AAC_LTP";
        }
        if (i == 67108880) {
            return "AAC_HE_V1";
        }
        if (i == 67108896) {
            return "AAC_SCALABLE";
        }
        if (i == 67108928) {
            return "AAC_ERLC";
        }
        if (i == 67108992) {
            return "AAC_LD";
        }
        if (i == 67109120) {
            return "AAC_HE_V2";
        }
        if (i == 67109376) {
            return "AAC_ELD";
        }
        if (i == 503316481) {
            return "AAC_ADTS_MAIN";
        }
        if (i == 503316482) {
            return "AAC_ADTS_LC";
        }
        if (i == 503316484) {
            return "AAC_ADTS_SSR";
        }
        if (i == 503316488) {
            return "AAC_ADTS_LTP";
        }
        if (i == 503316496) {
            return "AAC_ADTS_HE_V1";
        }
        if (i == 503316512) {
            return "AAC_ADTS_SCALABLE";
        }
        if (i == 503316544) {
            return "AAC_ADTS_ERLC";
        }
        if (i == 503316608) {
            return "AAC_ADTS_LD";
        }
        if (i == 503316736) {
            return "AAC_ADTS_HE_V2";
        }
        if (i == 503316992) {
            return "AAC_ADTS_ELD";
        }
        return "0x" + Integer.toHexString(i);
    }
}
