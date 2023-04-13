package com.android.internal.org.bouncycastle.crypto.engines;

import android.hardware.biometrics.fingerprint.AcquiredInfo;
import android.hardware.gnss.visibility_control.V1_0.IGnssVisibilityControlCallback;
import android.security.keystore.KeyProperties;
import com.android.internal.midi.MidiConstants;
import com.android.internal.org.bouncycastle.crypto.BlockCipher;
import com.android.internal.org.bouncycastle.crypto.CipherParameters;
import com.android.internal.org.bouncycastle.crypto.DataLengthException;
import com.android.internal.org.bouncycastle.crypto.OutputLengthException;
import com.android.internal.org.bouncycastle.crypto.params.KeyParameter;
import com.android.internal.org.bouncycastle.util.Arrays;
import com.android.internal.org.bouncycastle.util.Pack;
import com.android.internal.telephony.GsmAlphabet;
import com.android.net.module.util.NetworkStackConstants;
import java.lang.reflect.Array;
/* loaded from: classes4.dex */
public class AESEngine implements BlockCipher {
    private static final int BLOCK_SIZE = 16;

    /* renamed from: m1 */
    private static final int f719m1 = -2139062144;

    /* renamed from: m2 */
    private static final int f720m2 = 2139062143;

    /* renamed from: m3 */
    private static final int f721m3 = 27;

    /* renamed from: m4 */
    private static final int f722m4 = -1061109568;

    /* renamed from: m5 */
    private static final int f723m5 = 1061109567;

    /* renamed from: C0 */
    private int f724C0;

    /* renamed from: C1 */
    private int f725C1;

    /* renamed from: C2 */
    private int f726C2;

    /* renamed from: C3 */
    private int f727C3;
    private int ROUNDS;
    private int[][] WorkingKey = null;
    private boolean forEncryption;

    /* renamed from: s */
    private byte[] f728s;

    /* renamed from: S */
    private static final byte[] f716S = {99, 124, 119, 123, MidiConstants.STATUS_SONG_POSITION, 107, 111, -59, 48, 1, 103, 43, -2, -41, -85, 118, -54, -126, -55, 125, -6, 89, 71, -16, -83, -44, -94, -81, -100, -92, 114, MidiConstants.STATUS_PROGRAM_CHANGE, -73, -3, -109, 38, 54, 63, -9, -52, 52, -91, -27, MidiConstants.STATUS_MIDI_TIME_CODE, 113, -40, 49, 21, 4, -57, 35, -61, 24, -106, 5, -102, 7, 18, Byte.MIN_VALUE, -30, -21, 39, -78, 117, 9, -125, 44, 26, GsmAlphabet.GSM_EXTENDED_ESCAPE, 110, 90, MidiConstants.STATUS_POLYPHONIC_AFTERTOUCH, 82, 59, -42, -77, 41, -29, 47, -124, 83, -47, 0, -19, NetworkStackConstants.TCPHDR_URG, -4, -79, 91, 106, -53, -66, 57, 74, 76, 88, -49, MidiConstants.STATUS_CHANNEL_PRESSURE, -17, -86, -5, 67, 77, 51, -123, 69, -7, 2, Byte.MAX_VALUE, 80, 60, -97, -88, 81, -93, 64, -113, -110, -99, 56, -11, -68, -74, -38, 33, 16, -1, MidiConstants.STATUS_SONG_SELECT, -46, -51, 12, 19, -20, 95, -105, 68, 23, -60, -89, 126, 61, 100, 93, 25, 115, 96, -127, 79, -36, 34, 42, MidiConstants.STATUS_NOTE_ON, -120, 70, -18, -72, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, -34, 94, 11, -37, MidiConstants.STATUS_PITCH_BEND, 50, 58, 10, 73, 6, 36, 92, -62, -45, -84, 98, -111, -107, -28, 121, -25, -56, 55, 109, -115, -43, 78, -87, 108, 86, -12, -22, 101, 122, -82, 8, -70, 120, 37, 46, 28, -90, -76, -58, -24, -35, 116, 31, 75, -67, -117, -118, 112, 62, -75, 102, 72, 3, -10, AcquiredInfo.POWER_PRESS, 97, 53, 87, -71, -122, -63, 29, -98, -31, -8, -104, 17, 105, -39, -114, -108, -101, 30, -121, -23, -50, 85, 40, -33, -116, -95, -119, 13, -65, -26, 66, 104, 65, -103, 45, MidiConstants.STATUS_CHANNEL_MASK, MidiConstants.STATUS_CONTROL_CHANGE, 84, -69, 22};

    /* renamed from: Si */
    private static final byte[] f717Si = {82, 9, 106, -43, 48, 54, -91, 56, -65, 64, -93, -98, -127, MidiConstants.STATUS_SONG_SELECT, -41, -5, 124, -29, 57, -126, -101, 47, -1, -121, 52, -114, 67, 68, -60, -34, -23, -53, 84, 123, -108, 50, -90, -62, 35, 61, -18, 76, -107, 11, 66, -6, -61, 78, 8, 46, -95, 102, 40, -39, 36, -78, 118, 91, -94, 73, 109, -117, -47, 37, 114, -8, -10, 100, -122, 104, -104, 22, -44, -92, 92, -52, 93, 101, -74, -110, 108, 112, 72, 80, -3, -19, -71, -38, 94, 21, 70, 87, -89, -115, -99, -124, MidiConstants.STATUS_NOTE_ON, -40, -85, 0, -116, -68, -45, 10, -9, -28, 88, 5, -72, -77, 69, 6, MidiConstants.STATUS_CHANNEL_PRESSURE, 44, 30, -113, -54, 63, MidiConstants.STATUS_CHANNEL_MASK, 2, -63, -81, -67, 3, 1, 19, -118, 107, 58, -111, 17, 65, 79, 103, -36, -22, -105, MidiConstants.STATUS_SONG_POSITION, -49, -50, -16, -76, -26, 115, -106, -84, 116, 34, -25, -83, 53, -123, -30, -7, 55, -24, 28, 117, -33, 110, 71, MidiConstants.STATUS_MIDI_TIME_CODE, 26, 113, 29, 41, -59, -119, 111, -73, 98, AcquiredInfo.POWER_PRESS, -86, 24, -66, GsmAlphabet.GSM_EXTENDED_ESCAPE, -4, 86, 62, 75, -58, -46, 121, NetworkStackConstants.TCPHDR_URG, -102, -37, MidiConstants.STATUS_PROGRAM_CHANGE, -2, 120, -51, 90, -12, 31, -35, -88, 51, -120, 7, -57, 49, -79, 18, 16, 89, 39, Byte.MIN_VALUE, -20, 95, 96, 81, Byte.MAX_VALUE, -87, 25, -75, 74, 13, 45, -27, 122, -97, -109, -55, -100, -17, MidiConstants.STATUS_POLYPHONIC_AFTERTOUCH, MidiConstants.STATUS_PITCH_BEND, 59, 77, -82, 42, -11, MidiConstants.STATUS_CONTROL_CHANGE, -56, -21, -69, 60, -125, 83, -103, 97, 23, 43, 4, 126, -70, 119, -42, 38, -31, 105, IGnssVisibilityControlCallback.NfwRequestor.AUTOMOBILE_CLIENT, 99, 85, 33, 12, 125};
    private static final int[] rcon = {1, 2, 4, 8, 16, 32, 64, 128, 27, 54, 108, 216, 171, 77, 154, 47, 94, 188, 99, 198, 151, 53, 106, 212, 179, 125, 250, 239, 197, 145};

    /* renamed from: T0 */
    private static final int[] f718T0 = {-1520213050, -2072216328, -1720223762, -1921287178, 234025727, -1117033514, -1318096930, 1422247313, 1345335392, 50397442, -1452841010, 2099981142, 436141799, 1658312629, -424957107, -1703512340, 1170918031, -1652391393, 1086966153, -2021818886, 368769775, -346465870, -918075506, 200339707, -324162239, 1742001331, -39673249, -357585083, -1080255453, -140204973, -1770884380, 1539358875, -1028147339, 486407649, -1366060227, 1780885068, 1513502316, 1094664062, 49805301, 1338821763, 1546925160, -190470831, 887481809, 150073849, -1821281822, 1943591083, 1395732834, 1058346282, 201589768, 1388824469, 1696801606, 1589887901, 672667696, -1583966665, 251987210, -1248159185, 151455502, 907153956, -1686077413, 1038279391, 652995533, 1764173646, -843926913, -1619692054, 453576978, -1635548387, 1949051992, 773462580, 756751158, -1301385508, -296068428, -73359269, -162377052, 1295727478, 1641469623, -827083907, 2066295122, 1055122397, 1898917726, -1752923117, -179088474, 1758581177, 0, 753790401, 1612718144, 536673507, -927878791, -312779850, -1100322092, 1187761037, -641810841, 1262041458, -565556588, -733197160, -396863312, 1255133061, 1808847035, 720367557, -441800113, 385612781, -985447546, -682799718, 1429418854, -1803188975, -817543798, 284817897, 100794884, -2122350594, -263171936, 1144798328, -1163944155, -475486133, -212774494, -22830243, -1069531008, -1970303227, -1382903233, -1130521311, 1211644016, 83228145, -541279133, -1044990345, 1977277103, 1663115586, 806359072, 452984805, 250868733, 1842533055, 1288555905, 336333848, 890442534, 804056259, -513843266, -1567123659, -867941240, 957814574, 1472513171, -223893675, -2105639172, 1195195770, -1402706744, -413311558, 723065138, -1787595802, -1604296512, -1736343271, -783331426, 2145180835, 1713513028, 2116692564, -1416589253, -2088204277, -901364084, 703524551, -742868885, 1007948840, 2044649127, -497131844, 487262998, 1994120109, 1004593371, 1446130276, 1312438900, 503974420, -615954030, 168166924, 1814307912, -463709000, 1573044895, 1859376061, -273896381, -1503501628, -1466855111, -1533700815, 937747667, -1954973198, 854058965, 1137232011, 1496790894, -1217565222, -1936880383, 1691735473, -766620004, -525751991, -1267962664, -95005012, 133494003, 636152527, -1352309302, -1904575756, -374428089, 403179536, -709182865, -2005370640, 1864705354, 1915629148, 605822008, -240736681, -944458637, 1371981463, 602466507, 2094914977, -1670089496, 555687742, -582268010, -591544991, -2037675251, -2054518257, -1871679264, 1111375484, -994724495, -1436129588, -666351472, 84083462, 32962295, 302911004, -1553899070, 1597322602, -111716434, -793134743, -1853454825, 1489093017, 656219450, -1180787161, 954327513, 335083755, -1281845205, 856756514, -1150719534, 1893325225, -1987146233, -1483434957, -1231316179, 572399164, -1836611819, 552200649, 1238290055, -11184726, 2015897680, 2061492133, -1886614525, -123625127, -2138470135, 386731290, -624967835, 837215959, -968736124, -1201116976, -1019133566, -1332111063, 1999449434, 286199582, -877612933, -61582168, -692339859, 974525996};
    private static final int[] Tinv0 = {1353184337, 1399144830, -1012656358, -1772214470, -882136261, -247096033, -1420232020, -1828461749, 1442459680, -160598355, -1854485368, 625738485, -52959921, -674551099, -2143013594, -1885117771, 1230680542, 1729870373, -1743852987, -507445667, 41234371, 317738113, -1550367091, -956705941, -413167869, -1784901099, -344298049, -631680363, 763608788, -752782248, 694804553, 1154009486, 1787413109, 2021232372, 1799248025, -579749593, -1236278850, 397248752, 1722556617, -1271214467, 407560035, -2110711067, 1613975959, 1165972322, -529046351, -2068943941, 480281086, -1809118983, 1483229296, 436028815, -2022908268, -1208452270, 601060267, -503166094, 1468997603, 715871590, 120122290, 63092015, -1703164538, -1526188077, -226023376, -1297760477, -1167457534, 1552029421, 723308426, -1833666137, -252573709, -1578997426, -839591323, -708967162, 526529745, -1963022652, -1655493068, -1604979806, 853641733, 1978398372, 971801355, -1427152832, 111112542, 1360031421, -108388034, 1023860118, -1375387939, 1186850381, -1249028975, 90031217, 1876166148, -15380384, 620468249, -1746289194, -868007799, 2006899047, -1119688528, -2004121337, 945494503, -605108103, 1191869601, -384875908, -920746760, 0, -2088337399, 1223502642, -1401941730, 1316117100, -67170563, 1446544655, 517320253, 658058550, 1691946762, 564550760, -783000677, 976107044, -1318647284, 266819475, -761860428, -1634624741, 1338359936, -1574904735, 1766553434, 370807324, 179999714, -450191168, 1138762300, 488053522, 185403662, -1379431438, -1180125651, -928440812, -2061897385, 1275557295, -1143105042, -44007517, -1624899081, -1124765092, -985962940, 880737115, 1982415755, -590994485, 1761406390, 1676797112, -891538985, 277177154, 1076008723, 538035844, 2099530373, -130171950, 288553390, 1839278535, 1261411869, -214912292, -330136051, -790380169, 1813426987, -1715900247, -95906799, 577038663, -997393240, 440397984, -668172970, -275762398, -951170681, -1043253031, -22885748, 906744984, -813566554, 685669029, 646887386, -1530942145, -459458004, 227702864, -1681105046, 1648787028, -1038905866, -390539120, 1593260334, -173030526, -1098883681, 2090061929, -1456614033, -1290656305, 999926984, -1484974064, 1852021992, 2075868123, 158869197, -199730834, 28809964, -1466282109, 1701746150, 2129067946, 147831841, -420997649, -644094022, -835293366, -737566742, -696471511, -1347247055, 824393514, 815048134, -1067015627, 935087732, -1496677636, -1328508704, 366520115, 1251476721, -136647615, 240176511, 804688151, -1915335306, 1303441219, 1414376140, -553347356, -474623586, 461924940, -1205916479, 2136040774, 82468509, 1563790337, 1937016826, 776014843, 1511876531, 1389550482, 861278441, 323475053, -1939744870, 2047648055, -1911228327, -1992551445, -299390514, 902390199, -303751967, 1018251130, 1507840668, 1064563285, 2043548696, -1086863501, -355600557, 1537932639, 342834655, -2032450440, -2114736182, 1053059257, 741614648, 1598071746, 1925389590, 203809468, -1958134744, 1100287487, 1895934009, -558691320, -1662733096, -1866377628, 1636092795, 1890988757, 1952214088, 1113045200};

    private static int shift(int r, int shift) {
        return (r >>> shift) | (r << (-shift));
    }

    private static int FFmulX(int x) {
        return ((f720m2 & x) << 1) ^ (((f719m1 & x) >>> 7) * 27);
    }

    private static int FFmulX2(int x) {
        int t0 = (f723m5 & x) << 2;
        int t1 = f722m4 & x;
        int t12 = t1 ^ (t1 >>> 1);
        return ((t12 >>> 2) ^ t0) ^ (t12 >>> 5);
    }

    private static int inv_mcol(int x) {
        int t1 = shift(x, 8) ^ x;
        int t0 = x ^ FFmulX(t1);
        int t12 = t1 ^ FFmulX2(t0);
        return t0 ^ (shift(t12, 16) ^ t12);
    }

    private static int subWord(int x) {
        byte[] bArr = f716S;
        return (bArr[(x >> 24) & 255] << 24) | (bArr[x & 255] & 255) | ((bArr[(x >> 8) & 255] & 255) << 8) | ((bArr[(x >> 16) & 255] & 255) << 16);
    }

    private int[][] generateWorkingKey(byte[] key, boolean forEncryption) {
        int keyLen = key.length;
        if (keyLen < 16 || keyLen > 32 || (keyLen & 7) != 0) {
            throw new IllegalArgumentException("Key length not 128/192/256 bits.");
        }
        int KC = keyLen >>> 2;
        int i = KC + 6;
        this.ROUNDS = i;
        int[][] W = (int[][]) Array.newInstance(Integer.TYPE, i + 1, 4);
        int i2 = 8;
        int colx = 3;
        int colx2 = 0;
        switch (KC) {
            case 4:
                int col0 = Pack.littleEndianToInt(key, 0);
                W[0][0] = col0;
                int col1 = Pack.littleEndianToInt(key, 4);
                W[0][1] = col1;
                int col2 = Pack.littleEndianToInt(key, 8);
                W[0][2] = col2;
                int col3 = Pack.littleEndianToInt(key, 12);
                W[0][3] = col3;
                for (int i3 = 1; i3 <= 10; i3++) {
                    int colx3 = subWord(shift(col3, 8)) ^ rcon[i3 - 1];
                    col0 ^= colx3;
                    W[i3][0] = col0;
                    col1 ^= col0;
                    W[i3][1] = col1;
                    col2 ^= col1;
                    W[i3][2] = col2;
                    col3 ^= col2;
                    W[i3][3] = col3;
                }
                break;
            case 5:
            case 7:
            default:
                throw new IllegalStateException("Should never get here");
            case 6:
                int col02 = Pack.littleEndianToInt(key, 0);
                W[0][0] = col02;
                int col12 = Pack.littleEndianToInt(key, 4);
                W[0][1] = col12;
                int col22 = Pack.littleEndianToInt(key, 8);
                W[0][2] = col22;
                int col32 = Pack.littleEndianToInt(key, 12);
                W[0][3] = col32;
                int col4 = Pack.littleEndianToInt(key, 16);
                int col5 = Pack.littleEndianToInt(key, 20);
                int i4 = 1;
                int rcon2 = 1;
                while (true) {
                    W[i4][colx2] = col4;
                    W[i4][1] = col5;
                    int colx4 = subWord(shift(col5, 8)) ^ rcon2;
                    int rcon3 = rcon2 << 1;
                    int col03 = col02 ^ colx4;
                    W[i4][2] = col03;
                    int col13 = col12 ^ col03;
                    W[i4][3] = col13;
                    int col23 = col22 ^ col13;
                    W[i4 + 1][colx2] = col23;
                    int col33 = col32 ^ col23;
                    W[i4 + 1][1] = col33;
                    int col42 = col4 ^ col33;
                    W[i4 + 1][2] = col42;
                    int col52 = col5 ^ col42;
                    W[i4 + 1][3] = col52;
                    int colx5 = subWord(shift(col52, 8)) ^ rcon3;
                    rcon2 = rcon3 << 1;
                    col02 = col03 ^ colx5;
                    W[i4 + 2][0] = col02;
                    col12 = col13 ^ col02;
                    W[i4 + 2][1] = col12;
                    col22 = col23 ^ col12;
                    W[i4 + 2][2] = col22;
                    col32 = col33 ^ col22;
                    W[i4 + 2][3] = col32;
                    i4 += 3;
                    if (i4 >= 13) {
                        break;
                    } else {
                        col4 = col42 ^ col32;
                        col5 = col52 ^ col4;
                        colx2 = 0;
                    }
                }
            case 8:
                int col04 = Pack.littleEndianToInt(key, 0);
                W[0][0] = col04;
                int col14 = Pack.littleEndianToInt(key, 4);
                W[0][1] = col14;
                int col24 = Pack.littleEndianToInt(key, 8);
                W[0][2] = col24;
                int col34 = Pack.littleEndianToInt(key, 12);
                W[0][3] = col34;
                int col43 = Pack.littleEndianToInt(key, 16);
                W[1][0] = col43;
                int col53 = Pack.littleEndianToInt(key, 20);
                W[1][1] = col53;
                int col6 = Pack.littleEndianToInt(key, 24);
                W[1][2] = col6;
                int col7 = Pack.littleEndianToInt(key, 28);
                W[1][3] = col7;
                int i5 = 2;
                int rcon4 = 1;
                while (true) {
                    int colx6 = subWord(shift(col7, i2)) ^ rcon4;
                    rcon4 <<= 1;
                    col04 ^= colx6;
                    W[i5][0] = col04;
                    col14 ^= col04;
                    W[i5][1] = col14;
                    col24 ^= col14;
                    W[i5][2] = col24;
                    col34 ^= col24;
                    W[i5][colx] = col34;
                    int i6 = i5 + 1;
                    if (i6 >= 15) {
                        break;
                    } else {
                        int colx7 = subWord(col34);
                        col43 ^= colx7;
                        W[i6][0] = col43;
                        col53 ^= col43;
                        W[i6][1] = col53;
                        col6 ^= col53;
                        W[i6][2] = col6;
                        col7 ^= col6;
                        W[i6][3] = col7;
                        i5 = i6 + 1;
                        i2 = 8;
                        colx = 3;
                    }
                }
        }
        if (!forEncryption) {
            for (int j = 1; j < this.ROUNDS; j++) {
                for (int i7 = 0; i7 < 4; i7++) {
                    W[j][i7] = inv_mcol(W[j][i7]);
                }
            }
        }
        return W;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void init(boolean forEncryption, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.WorkingKey = generateWorkingKey(((KeyParameter) params).getKey(), forEncryption);
            this.forEncryption = forEncryption;
            if (forEncryption) {
                this.f728s = Arrays.clone(f716S);
                return;
            } else {
                this.f728s = Arrays.clone(f717Si);
                return;
            }
        }
        throw new IllegalArgumentException("invalid parameter passed to AES init - " + params.getClass().getName());
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public String getAlgorithmName() {
        return KeyProperties.KEY_ALGORITHM_AES;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int getBlockSize() {
        return 16;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.WorkingKey == null) {
            throw new IllegalStateException("AES engine not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            unpackBlock(in, inOff);
            encryptBlock(this.WorkingKey);
            packBlock(out, outOff);
            return 16;
        }
        unpackBlock(in, inOff);
        decryptBlock(this.WorkingKey);
        packBlock(out, outOff);
        return 16;
    }

    @Override // com.android.internal.org.bouncycastle.crypto.BlockCipher
    public void reset() {
    }

    private void unpackBlock(byte[] bytes, int off) {
        int index = off + 1;
        int i = bytes[off] & 255;
        this.f724C0 = i;
        int index2 = index + 1;
        int i2 = i | ((bytes[index] & 255) << 8);
        this.f724C0 = i2;
        int index3 = index2 + 1;
        int i3 = i2 | ((bytes[index2] & 255) << 16);
        this.f724C0 = i3;
        int index4 = index3 + 1;
        this.f724C0 = i3 | (bytes[index3] << 24);
        int index5 = index4 + 1;
        int i4 = bytes[index4] & 255;
        this.f725C1 = i4;
        int index6 = index5 + 1;
        int i5 = ((bytes[index5] & 255) << 8) | i4;
        this.f725C1 = i5;
        int index7 = index6 + 1;
        int i6 = i5 | ((bytes[index6] & 255) << 16);
        this.f725C1 = i6;
        int index8 = index7 + 1;
        this.f725C1 = i6 | (bytes[index7] << 24);
        int index9 = index8 + 1;
        int i7 = bytes[index8] & 255;
        this.f726C2 = i7;
        int index10 = index9 + 1;
        int i8 = ((bytes[index9] & 255) << 8) | i7;
        this.f726C2 = i8;
        int index11 = index10 + 1;
        int i9 = i8 | ((bytes[index10] & 255) << 16);
        this.f726C2 = i9;
        int index12 = index11 + 1;
        this.f726C2 = i9 | (bytes[index11] << 24);
        int index13 = index12 + 1;
        int i10 = bytes[index12] & 255;
        this.f727C3 = i10;
        int index14 = index13 + 1;
        int i11 = ((bytes[index13] & 255) << 8) | i10;
        this.f727C3 = i11;
        int index15 = index14 + 1;
        int i12 = i11 | ((bytes[index14] & 255) << 16);
        this.f727C3 = i12;
        int i13 = index15 + 1;
        this.f727C3 = i12 | (bytes[index15] << 24);
    }

    private void packBlock(byte[] bytes, int off) {
        int index = off + 1;
        int i = this.f724C0;
        bytes[off] = (byte) i;
        int index2 = index + 1;
        bytes[index] = (byte) (i >> 8);
        int index3 = index2 + 1;
        bytes[index2] = (byte) (i >> 16);
        int index4 = index3 + 1;
        bytes[index3] = (byte) (i >> 24);
        int index5 = index4 + 1;
        int i2 = this.f725C1;
        bytes[index4] = (byte) i2;
        int index6 = index5 + 1;
        bytes[index5] = (byte) (i2 >> 8);
        int index7 = index6 + 1;
        bytes[index6] = (byte) (i2 >> 16);
        int index8 = index7 + 1;
        bytes[index7] = (byte) (i2 >> 24);
        int index9 = index8 + 1;
        int i3 = this.f726C2;
        bytes[index8] = (byte) i3;
        int index10 = index9 + 1;
        bytes[index9] = (byte) (i3 >> 8);
        int index11 = index10 + 1;
        bytes[index10] = (byte) (i3 >> 16);
        int index12 = index11 + 1;
        bytes[index11] = (byte) (i3 >> 24);
        int index13 = index12 + 1;
        int i4 = this.f727C3;
        bytes[index12] = (byte) i4;
        int index14 = index13 + 1;
        bytes[index13] = (byte) (i4 >> 8);
        int index15 = index14 + 1;
        bytes[index14] = (byte) (i4 >> 16);
        int i5 = index15 + 1;
        bytes[index15] = (byte) (i4 >> 24);
    }

    private void encryptBlock(int[][] KW) {
        int r1 = 0;
        int t0 = this.f724C0 ^ KW[0][0];
        int r2 = 1;
        int t1 = this.f725C1 ^ KW[0][1];
        char c = 2;
        int t2 = this.f726C2 ^ KW[0][2];
        int r3 = 1;
        int r = this.f727C3 ^ KW[0][3];
        while (r3 < this.ROUNDS - r2) {
            int[] iArr = f718T0;
            int r0 = (((iArr[t0 & 255] ^ shift(iArr[(t1 >> 8) & 255], 24)) ^ shift(iArr[(t2 >> 16) & 255], 16)) ^ shift(iArr[(r >> 24) & 255], 8)) ^ KW[r3][r1];
            int r12 = (((shift(iArr[(t2 >> 8) & 255], 24) ^ iArr[t1 & 255]) ^ shift(iArr[(r >> 16) & 255], 16)) ^ shift(iArr[(t0 >> 24) & 255], 8)) ^ KW[r3][r2];
            int r22 = (((shift(iArr[(r >> 8) & 255], 24) ^ iArr[t2 & 255]) ^ shift(iArr[(t0 >> 16) & 255], 16)) ^ shift(iArr[(t1 >> 24) & 255], 8)) ^ KW[r3][c];
            int shift = ((shift(iArr[(t0 >> 8) & 255], 24) ^ iArr[r & 255]) ^ shift(iArr[(t1 >> 16) & 255], 16)) ^ shift(iArr[(t2 >> 24) & 255], 8);
            int r4 = r3 + 1;
            int r32 = KW[r3][3] ^ shift;
            t0 = (((iArr[r0 & 255] ^ shift(iArr[(r12 >> 8) & 255], 24)) ^ shift(iArr[(r22 >> 16) & 255], 16)) ^ shift(iArr[(r32 >> 24) & 255], 8)) ^ KW[r4][0];
            t1 = (((iArr[r12 & 255] ^ shift(iArr[(r22 >> 8) & 255], 24)) ^ shift(iArr[(r32 >> 16) & 255], 16)) ^ shift(iArr[(r0 >> 24) & 255], 8)) ^ KW[r4][1];
            t2 = (((iArr[r22 & 255] ^ shift(iArr[(r32 >> 8) & 255], 24)) ^ shift(iArr[(r0 >> 16) & 255], 16)) ^ shift(iArr[(r12 >> 24) & 255], 8)) ^ KW[r4][2];
            int r33 = (((iArr[r32 & 255] ^ shift(iArr[(r0 >> 8) & 255], 24)) ^ shift(iArr[(r12 >> 16) & 255], 16)) ^ shift(iArr[(r22 >> 24) & 255], 8)) ^ KW[r4][3];
            r1 = 0;
            r2 = 1;
            c = 2;
            r = r33;
            r3 = r4 + 1;
        }
        int[] iArr2 = f718T0;
        int r02 = (((iArr2[t0 & 255] ^ shift(iArr2[(t1 >> 8) & 255], 24)) ^ shift(iArr2[(t2 >> 16) & 255], 16)) ^ shift(iArr2[(r >> 24) & 255], 8)) ^ KW[r3][0];
        int r13 = (((iArr2[t1 & 255] ^ shift(iArr2[(t2 >> 8) & 255], 24)) ^ shift(iArr2[(r >> 16) & 255], 16)) ^ shift(iArr2[(t0 >> 24) & 255], 8)) ^ KW[r3][1];
        int r23 = (((iArr2[t2 & 255] ^ shift(iArr2[(r >> 8) & 255], 24)) ^ shift(iArr2[(t0 >> 16) & 255], 16)) ^ shift(iArr2[(t1 >> 24) & 255], 8)) ^ KW[r3][2];
        int shift2 = shift(iArr2[(t2 >> 24) & 255], 8) ^ ((iArr2[r & 255] ^ shift(iArr2[(t0 >> 8) & 255], 24)) ^ shift(iArr2[(t1 >> 16) & 255], 16));
        int r5 = r3 + 1;
        int r34 = shift2 ^ KW[r3][3];
        byte[] bArr = f716S;
        int i = (bArr[r02 & 255] & 255) ^ ((bArr[(r13 >> 8) & 255] & 255) << 8);
        byte[] bArr2 = this.f728s;
        this.f724C0 = ((i ^ ((bArr2[(r23 >> 16) & 255] & 255) << 16)) ^ (bArr2[(r34 >> 24) & 255] << 24)) ^ KW[r5][0];
        this.f725C1 = ((((bArr2[r13 & 255] & 255) ^ ((bArr[(r23 >> 8) & 255] & 255) << 8)) ^ ((bArr[(r34 >> 16) & 255] & 255) << 16)) ^ (bArr2[(r02 >> 24) & 255] << 24)) ^ KW[r5][1];
        this.f726C2 = ((((bArr2[r23 & 255] & 255) ^ ((bArr[(r34 >> 8) & 255] & 255) << 8)) ^ ((bArr[(r02 >> 16) & 255] & 255) << 16)) ^ (bArr[(r13 >> 24) & 255] << 24)) ^ KW[r5][2];
        this.f727C3 = ((bArr[(r23 >> 24) & 255] << 24) ^ (((bArr2[r34 & 255] & 255) ^ ((bArr2[(r02 >> 8) & 255] & 255) << 8)) ^ ((bArr2[(r13 >> 16) & 255] & 255) << 16))) ^ KW[r5][3];
    }

    private void decryptBlock(int[][] KW) {
        int i = this.f724C0;
        int i2 = this.ROUNDS;
        int r1 = 0;
        int t0 = i ^ KW[i2][0];
        int r2 = 1;
        int t1 = this.f725C1 ^ KW[i2][1];
        int r = 2;
        int t2 = this.f726C2 ^ KW[i2][2];
        int r3 = i2 - 1;
        int r32 = KW[i2][3] ^ this.f727C3;
        while (r3 > r2) {
            int[] iArr = Tinv0;
            int r0 = (((iArr[t0 & 255] ^ shift(iArr[(r32 >> 8) & 255], 24)) ^ shift(iArr[(t2 >> 16) & 255], 16)) ^ shift(iArr[(t1 >> 24) & 255], 8)) ^ KW[r3][r1];
            int r12 = (((shift(iArr[(t0 >> 8) & 255], 24) ^ iArr[t1 & 255]) ^ shift(iArr[(r32 >> 16) & 255], 16)) ^ shift(iArr[(t2 >> 24) & 255], 8)) ^ KW[r3][r2];
            int r22 = (((shift(iArr[(t1 >> 8) & 255], 24) ^ iArr[t2 & 255]) ^ shift(iArr[(t0 >> 16) & 255], 16)) ^ shift(iArr[(r32 >> 24) & 255], 8)) ^ KW[r3][r];
            int r4 = r3 - 1;
            int r33 = (((shift(iArr[(t2 >> 8) & 255], 24) ^ iArr[r32 & 255]) ^ shift(iArr[(t1 >> 16) & 255], 16)) ^ shift(iArr[(t0 >> 24) & 255], 8)) ^ KW[r3][3];
            t0 = (((iArr[r0 & 255] ^ shift(iArr[(r33 >> 8) & 255], 24)) ^ shift(iArr[(r22 >> 16) & 255], 16)) ^ shift(iArr[(r12 >> 24) & 255], 8)) ^ KW[r4][0];
            t1 = (((iArr[r12 & 255] ^ shift(iArr[(r0 >> 8) & 255], 24)) ^ shift(iArr[(r33 >> 16) & 255], 16)) ^ shift(iArr[(r22 >> 24) & 255], 8)) ^ KW[r4][1];
            t2 = (((iArr[r22 & 255] ^ shift(iArr[(r12 >> 8) & 255], 24)) ^ shift(iArr[(r0 >> 16) & 255], 16)) ^ shift(iArr[(r33 >> 24) & 255], 8)) ^ KW[r4][2];
            r32 = (((iArr[r33 & 255] ^ shift(iArr[(r22 >> 8) & 255], 24)) ^ shift(iArr[(r12 >> 16) & 255], 16)) ^ shift(iArr[(r0 >> 24) & 255], 8)) ^ KW[r4][3];
            r3 = r4 - 1;
            r1 = 0;
            r2 = 1;
            r = 2;
        }
        int[] iArr2 = Tinv0;
        int r02 = (((iArr2[t0 & 255] ^ shift(iArr2[(r32 >> 8) & 255], 24)) ^ shift(iArr2[(t2 >> 16) & 255], 16)) ^ shift(iArr2[(t1 >> 24) & 255], 8)) ^ KW[r3][0];
        int r13 = (((iArr2[t1 & 255] ^ shift(iArr2[(t0 >> 8) & 255], 24)) ^ shift(iArr2[(r32 >> 16) & 255], 16)) ^ shift(iArr2[(t2 >> 24) & 255], 8)) ^ KW[r3][1];
        int r23 = (((iArr2[t2 & 255] ^ shift(iArr2[(t1 >> 8) & 255], 24)) ^ shift(iArr2[(t0 >> 16) & 255], 16)) ^ shift(iArr2[(r32 >> 24) & 255], 8)) ^ KW[r3][2];
        int r34 = (shift(iArr2[(t0 >> 24) & 255], 8) ^ ((iArr2[r32 & 255] ^ shift(iArr2[(t2 >> 8) & 255], 24)) ^ shift(iArr2[(t1 >> 16) & 255], 16))) ^ KW[r3][3];
        byte[] bArr = f717Si;
        byte[] bArr2 = this.f728s;
        this.f724C0 = (((((bArr2[(r34 >> 8) & 255] & 255) << 8) ^ (bArr[r02 & 255] & 255)) ^ ((bArr2[(r23 >> 16) & 255] & 255) << 16)) ^ (bArr[(r13 >> 24) & 255] << 24)) ^ KW[0][0];
        this.f725C1 = ((((bArr2[r13 & 255] & 255) ^ ((bArr2[(r02 >> 8) & 255] & 255) << 8)) ^ ((bArr[(r34 >> 16) & 255] & 255) << 16)) ^ (bArr2[(r23 >> 24) & 255] << 24)) ^ KW[0][1];
        this.f726C2 = ((((bArr2[r23 & 255] & 255) ^ ((bArr[(r13 >> 8) & 255] & 255) << 8)) ^ ((bArr[(r02 >> 16) & 255] & 255) << 16)) ^ (bArr2[(r34 >> 24) & 255] << 24)) ^ KW[0][2];
        this.f727C3 = ((((bArr[r34 & 255] & 255) ^ ((bArr2[(r23 >> 8) & 255] & 255) << 8)) ^ ((bArr2[(r13 >> 16) & 255] & 255) << 16)) ^ (bArr2[(r02 >> 24) & 255] << 24)) ^ KW[0][3];
    }
}
