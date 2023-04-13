package android.hardware.radio.V1_5;

import java.util.ArrayList;
/* loaded from: classes2.dex */
public final class NgranBands {
    public static final int BAND_1 = 1;
    public static final int BAND_12 = 12;
    public static final int BAND_14 = 14;
    public static final int BAND_18 = 18;
    public static final int BAND_2 = 2;
    public static final int BAND_20 = 20;
    public static final int BAND_25 = 25;
    public static final int BAND_257 = 257;
    public static final int BAND_258 = 258;
    public static final int BAND_260 = 260;
    public static final int BAND_261 = 261;
    public static final int BAND_28 = 28;
    public static final int BAND_29 = 29;
    public static final int BAND_3 = 3;
    public static final int BAND_30 = 30;
    public static final int BAND_34 = 34;
    public static final int BAND_38 = 38;
    public static final int BAND_39 = 39;
    public static final int BAND_40 = 40;
    public static final int BAND_41 = 41;
    public static final int BAND_48 = 48;
    public static final int BAND_5 = 5;
    public static final int BAND_50 = 50;
    public static final int BAND_51 = 51;
    public static final int BAND_65 = 65;
    public static final int BAND_66 = 66;
    public static final int BAND_7 = 7;
    public static final int BAND_70 = 70;
    public static final int BAND_71 = 71;
    public static final int BAND_74 = 74;
    public static final int BAND_75 = 75;
    public static final int BAND_76 = 76;
    public static final int BAND_77 = 77;
    public static final int BAND_78 = 78;
    public static final int BAND_79 = 79;
    public static final int BAND_8 = 8;
    public static final int BAND_80 = 80;
    public static final int BAND_81 = 81;
    public static final int BAND_82 = 82;
    public static final int BAND_83 = 83;
    public static final int BAND_84 = 84;
    public static final int BAND_86 = 86;
    public static final int BAND_89 = 89;
    public static final int BAND_90 = 90;
    public static final int BAND_91 = 91;
    public static final int BAND_92 = 92;
    public static final int BAND_93 = 93;
    public static final int BAND_94 = 94;
    public static final int BAND_95 = 95;

    public static final String toString(int o) {
        if (o == 1) {
            return "BAND_1";
        }
        if (o == 2) {
            return "BAND_2";
        }
        if (o == 3) {
            return "BAND_3";
        }
        if (o == 5) {
            return "BAND_5";
        }
        if (o == 7) {
            return "BAND_7";
        }
        if (o == 8) {
            return "BAND_8";
        }
        if (o == 12) {
            return "BAND_12";
        }
        if (o == 14) {
            return "BAND_14";
        }
        if (o == 18) {
            return "BAND_18";
        }
        if (o == 20) {
            return "BAND_20";
        }
        if (o == 25) {
            return "BAND_25";
        }
        if (o == 28) {
            return "BAND_28";
        }
        if (o == 29) {
            return "BAND_29";
        }
        if (o == 30) {
            return "BAND_30";
        }
        if (o == 34) {
            return "BAND_34";
        }
        if (o == 38) {
            return "BAND_38";
        }
        if (o == 39) {
            return "BAND_39";
        }
        if (o == 40) {
            return "BAND_40";
        }
        if (o == 41) {
            return "BAND_41";
        }
        if (o == 48) {
            return "BAND_48";
        }
        if (o == 50) {
            return "BAND_50";
        }
        if (o == 51) {
            return "BAND_51";
        }
        if (o == 65) {
            return "BAND_65";
        }
        if (o == 66) {
            return "BAND_66";
        }
        if (o == 70) {
            return "BAND_70";
        }
        if (o == 71) {
            return "BAND_71";
        }
        if (o == 74) {
            return "BAND_74";
        }
        if (o == 75) {
            return "BAND_75";
        }
        if (o == 76) {
            return "BAND_76";
        }
        if (o == 77) {
            return "BAND_77";
        }
        if (o == 78) {
            return "BAND_78";
        }
        if (o == 79) {
            return "BAND_79";
        }
        if (o == 80) {
            return "BAND_80";
        }
        if (o == 81) {
            return "BAND_81";
        }
        if (o == 82) {
            return "BAND_82";
        }
        if (o == 83) {
            return "BAND_83";
        }
        if (o == 84) {
            return "BAND_84";
        }
        if (o == 86) {
            return "BAND_86";
        }
        if (o == 89) {
            return "BAND_89";
        }
        if (o == 90) {
            return "BAND_90";
        }
        if (o == 91) {
            return "BAND_91";
        }
        if (o == 92) {
            return "BAND_92";
        }
        if (o == 93) {
            return "BAND_93";
        }
        if (o == 94) {
            return "BAND_94";
        }
        if (o == 95) {
            return "BAND_95";
        }
        if (o == 257) {
            return "BAND_257";
        }
        if (o == 258) {
            return "BAND_258";
        }
        if (o == 260) {
            return "BAND_260";
        }
        if (o == 261) {
            return "BAND_261";
        }
        return "0x" + Integer.toHexString(o);
    }

    public static final String dumpBitfield(int o) {
        ArrayList<String> list = new ArrayList<>();
        int flipped = 0;
        if ((o & 1) == 1) {
            list.add("BAND_1");
            flipped = 0 | 1;
        }
        if ((o & 2) == 2) {
            list.add("BAND_2");
            flipped |= 2;
        }
        if ((o & 3) == 3) {
            list.add("BAND_3");
            flipped |= 3;
        }
        if ((o & 5) == 5) {
            list.add("BAND_5");
            flipped |= 5;
        }
        if ((o & 7) == 7) {
            list.add("BAND_7");
            flipped |= 7;
        }
        if ((o & 8) == 8) {
            list.add("BAND_8");
            flipped |= 8;
        }
        if ((o & 12) == 12) {
            list.add("BAND_12");
            flipped |= 12;
        }
        if ((o & 14) == 14) {
            list.add("BAND_14");
            flipped |= 14;
        }
        if ((o & 18) == 18) {
            list.add("BAND_18");
            flipped |= 18;
        }
        if ((o & 20) == 20) {
            list.add("BAND_20");
            flipped |= 20;
        }
        if ((o & 25) == 25) {
            list.add("BAND_25");
            flipped |= 25;
        }
        if ((o & 28) == 28) {
            list.add("BAND_28");
            flipped |= 28;
        }
        if ((o & 29) == 29) {
            list.add("BAND_29");
            flipped |= 29;
        }
        if ((o & 30) == 30) {
            list.add("BAND_30");
            flipped |= 30;
        }
        if ((o & 34) == 34) {
            list.add("BAND_34");
            flipped |= 34;
        }
        if ((o & 38) == 38) {
            list.add("BAND_38");
            flipped |= 38;
        }
        if ((o & 39) == 39) {
            list.add("BAND_39");
            flipped |= 39;
        }
        if ((o & 40) == 40) {
            list.add("BAND_40");
            flipped |= 40;
        }
        if ((o & 41) == 41) {
            list.add("BAND_41");
            flipped |= 41;
        }
        if ((o & 48) == 48) {
            list.add("BAND_48");
            flipped |= 48;
        }
        if ((o & 50) == 50) {
            list.add("BAND_50");
            flipped |= 50;
        }
        if ((o & 51) == 51) {
            list.add("BAND_51");
            flipped |= 51;
        }
        if ((o & 65) == 65) {
            list.add("BAND_65");
            flipped |= 65;
        }
        if ((o & 66) == 66) {
            list.add("BAND_66");
            flipped |= 66;
        }
        if ((o & 70) == 70) {
            list.add("BAND_70");
            flipped |= 70;
        }
        if ((o & 71) == 71) {
            list.add("BAND_71");
            flipped |= 71;
        }
        if ((o & 74) == 74) {
            list.add("BAND_74");
            flipped |= 74;
        }
        if ((o & 75) == 75) {
            list.add("BAND_75");
            flipped |= 75;
        }
        if ((o & 76) == 76) {
            list.add("BAND_76");
            flipped |= 76;
        }
        if ((o & 77) == 77) {
            list.add("BAND_77");
            flipped |= 77;
        }
        if ((o & 78) == 78) {
            list.add("BAND_78");
            flipped |= 78;
        }
        if ((o & 79) == 79) {
            list.add("BAND_79");
            flipped |= 79;
        }
        if ((o & 80) == 80) {
            list.add("BAND_80");
            flipped |= 80;
        }
        if ((o & 81) == 81) {
            list.add("BAND_81");
            flipped |= 81;
        }
        if ((o & 82) == 82) {
            list.add("BAND_82");
            flipped |= 82;
        }
        if ((o & 83) == 83) {
            list.add("BAND_83");
            flipped |= 83;
        }
        if ((o & 84) == 84) {
            list.add("BAND_84");
            flipped |= 84;
        }
        if ((o & 86) == 86) {
            list.add("BAND_86");
            flipped |= 86;
        }
        if ((o & 89) == 89) {
            list.add("BAND_89");
            flipped |= 89;
        }
        if ((o & 90) == 90) {
            list.add("BAND_90");
            flipped |= 90;
        }
        if ((o & 91) == 91) {
            list.add("BAND_91");
            flipped |= 91;
        }
        if ((o & 92) == 92) {
            list.add("BAND_92");
            flipped |= 92;
        }
        if ((o & 93) == 93) {
            list.add("BAND_93");
            flipped |= 93;
        }
        if ((o & 94) == 94) {
            list.add("BAND_94");
            flipped |= 94;
        }
        if ((o & 95) == 95) {
            list.add("BAND_95");
            flipped |= 95;
        }
        if ((o & 257) == 257) {
            list.add("BAND_257");
            flipped |= 257;
        }
        if ((o & 258) == 258) {
            list.add("BAND_258");
            flipped |= 258;
        }
        if ((o & 260) == 260) {
            list.add("BAND_260");
            flipped |= 260;
        }
        if ((o & 261) == 261) {
            list.add("BAND_261");
            flipped |= 261;
        }
        if (o != flipped) {
            list.add("0x" + Integer.toHexString((~flipped) & o));
        }
        return String.join(" | ", list);
    }
}
