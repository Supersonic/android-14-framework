package com.android.internal.graphics.cam;

import com.android.internal.graphics.ColorUtils;
/* loaded from: classes4.dex */
public class Cam {
    private static final float CHROMA_SEARCH_ENDPOINT = 0.4f;
    private static final float DE_MAX = 1.0f;
    private static final float DL_MAX = 0.2f;
    private static final float LIGHTNESS_SEARCH_ENDPOINT = 0.01f;
    private final float mAstar;
    private final float mBstar;
    private final float mChroma;
    private final float mHue;

    /* renamed from: mJ */
    private final float f576mJ;
    private final float mJstar;

    /* renamed from: mM */
    private final float f577mM;

    /* renamed from: mQ */
    private final float f578mQ;

    /* renamed from: mS */
    private final float f579mS;

    public float getHue() {
        return this.mHue;
    }

    public float getChroma() {
        return this.mChroma;
    }

    public float getJ() {
        return this.f576mJ;
    }

    public float getQ() {
        return this.f578mQ;
    }

    public float getM() {
        return this.f577mM;
    }

    public float getS() {
        return this.f579mS;
    }

    public float getJstar() {
        return this.mJstar;
    }

    public float getAstar() {
        return this.mAstar;
    }

    public float getBstar() {
        return this.mBstar;
    }

    Cam(float hue, float chroma, float j, float q, float m, float s, float jstar, float astar, float bstar) {
        this.mHue = hue;
        this.mChroma = chroma;
        this.f576mJ = j;
        this.f578mQ = q;
        this.f577mM = m;
        this.f579mS = s;
        this.mJstar = jstar;
        this.mAstar = astar;
        this.mBstar = bstar;
    }

    public static int getInt(float hue, float chroma, float lstar) {
        return getInt(hue, chroma, lstar, Frame.DEFAULT);
    }

    public static Cam fromInt(int argb) {
        return fromIntInFrame(argb, Frame.DEFAULT);
    }

    public static Cam fromIntInFrame(int argb, Frame frame) {
        float f;
        float[] xyz = CamUtils.xyzFromInt(argb);
        float[][] matrix = CamUtils.XYZ_TO_CAM16RGB;
        float rT = (xyz[0] * matrix[0][0]) + (xyz[1] * matrix[0][1]) + (xyz[2] * matrix[0][2]);
        float gT = (xyz[0] * matrix[1][0]) + (xyz[1] * matrix[1][1]) + (xyz[2] * matrix[1][2]);
        float bT = (xyz[0] * matrix[2][0]) + (xyz[1] * matrix[2][1]) + (xyz[2] * matrix[2][2]);
        float rD = frame.getRgbD()[0] * rT;
        float gD = frame.getRgbD()[1] * gT;
        float bD = frame.getRgbD()[2] * bT;
        float rAF = (float) Math.pow((frame.getFl() * Math.abs(rD)) / 100.0d, 0.42d);
        float gAF = (float) Math.pow((frame.getFl() * Math.abs(gD)) / 100.0d, 0.42d);
        float bAF = (float) Math.pow((frame.getFl() * Math.abs(bD)) / 100.0d, 0.42d);
        float rA = ((Math.signum(rD) * 400.0f) * rAF) / (rAF + 27.13f);
        float gA = ((Math.signum(gD) * 400.0f) * gAF) / (gAF + 27.13f);
        float bA = ((Math.signum(bD) * 400.0f) * bAF) / (27.13f + bAF);
        float a = ((float) (((rA * 11.0d) + (gA * (-12.0d))) + bA)) / 11.0f;
        float b = ((float) ((rA + gA) - (bA * 2.0d))) / 9.0f;
        float u = (((rA * 20.0f) + (gA * 20.0f)) + (21.0f * bA)) / 20.0f;
        float p2 = (((40.0f * rA) + (gA * 20.0f)) + bA) / 20.0f;
        float atan2 = (float) Math.atan2(b, a);
        float atanDegrees = (atan2 * 180.0f) / 3.1415927f;
        if (atanDegrees < 0.0f) {
            f = atanDegrees + 360.0f;
        } else {
            f = atanDegrees >= 360.0f ? atanDegrees - 360.0f : atanDegrees;
        }
        float hue = f;
        float hueRadians = (hue * 3.1415927f) / 180.0f;
        float ac = frame.getNbb() * p2;
        float atan22 = ac / frame.getAw();
        float j = ((float) Math.pow(atan22, frame.getC() * frame.getZ())) * 100.0f;
        float q = (4.0f / frame.getC()) * ((float) Math.sqrt(j / 100.0f)) * (frame.getAw() + 4.0f) * frame.getFlRoot();
        float huePrime = ((double) hue) < 20.14d ? hue + 360.0f : hue;
        float eHue = ((float) (Math.cos(((huePrime * 3.141592653589793d) / 180.0d) + 2.0d) + 3.8d)) * 0.25f;
        float p1 = 3846.1538f * eHue * frame.getNc() * frame.getNcb();
        float t = (((float) Math.sqrt((a * a) + (b * b))) * p1) / (0.305f + u);
        float alpha = ((float) Math.pow(t, 0.9d)) * ((float) Math.pow(1.64d - Math.pow(0.29d, frame.getN()), 0.73d));
        float c = ((float) Math.sqrt(j / 100.0d)) * alpha;
        float m = frame.getFlRoot() * c;
        float s = ((float) Math.sqrt((frame.getC() * alpha) / (frame.getAw() + 4.0f))) * 50.0f;
        float jstar = (1.7f * j) / ((0.007f * j) + 1.0f);
        float mstar = ((float) Math.log((0.0228f * m) + 1.0f)) * 43.85965f;
        float astar = ((float) Math.cos(hueRadians)) * mstar;
        float bstar = ((float) Math.sin(hueRadians)) * mstar;
        return new Cam(hue, c, j, q, m, s, jstar, astar, bstar);
    }

    private static Cam fromJch(float j, float c, float h) {
        return fromJchInFrame(j, c, h, Frame.DEFAULT);
    }

    private static Cam fromJchInFrame(float j, float c, float h, Frame frame) {
        float q = (4.0f / frame.getC()) * ((float) Math.sqrt(j / 100.0d)) * (frame.getAw() + 4.0f) * frame.getFlRoot();
        float m = c * frame.getFlRoot();
        float alpha = c / ((float) Math.sqrt(j / 100.0d));
        float s = ((float) Math.sqrt((frame.getC() * alpha) / (frame.getAw() + 4.0f))) * 50.0f;
        float hueRadians = (3.1415927f * h) / 180.0f;
        float jstar = (1.7f * j) / ((0.007f * j) + 1.0f);
        float mstar = ((float) Math.log((m * 0.0228d) + 1.0d)) * 43.85965f;
        float astar = mstar * ((float) Math.cos(hueRadians));
        float bstar = mstar * ((float) Math.sin(hueRadians));
        return new Cam(h, c, j, q, m, s, jstar, astar, bstar);
    }

    public float distance(Cam other) {
        float dJ = getJstar() - other.getJstar();
        float dA = getAstar() - other.getAstar();
        float dB = getBstar() - other.getBstar();
        double dEPrime = Math.sqrt((dJ * dJ) + (dA * dA) + (dB * dB));
        double dE = Math.pow(dEPrime, 0.63d) * 1.41d;
        return (float) dE;
    }

    public int viewedInSrgb() {
        return viewed(Frame.DEFAULT);
    }

    public int viewed(Frame frame) {
        float alpha;
        if (getChroma() == 0.0d || getJ() == 0.0d) {
            alpha = 0.0f;
        } else {
            alpha = getChroma() / ((float) Math.sqrt(getJ() / 100.0d));
        }
        float t = (float) Math.pow(alpha / Math.pow(1.64d - Math.pow(0.29d, frame.getN()), 0.73d), 1.1111111111111112d);
        float hRad = (getHue() * 3.1415927f) / 180.0f;
        float eHue = ((float) (Math.cos(hRad + 2.0d) + 3.8d)) * 0.25f;
        float ac = frame.getAw() * ((float) Math.pow(getJ() / 100.0d, (1.0d / frame.getC()) / frame.getZ()));
        float p1 = 3846.1538f * eHue * frame.getNc() * frame.getNcb();
        float p2 = ac / frame.getNbb();
        float hSin = (float) Math.sin(hRad);
        float hCos = (float) Math.cos(hRad);
        float gamma = (((0.305f + p2) * 23.0f) * t) / (((23.0f * p1) + ((11.0f * t) * hCos)) + ((108.0f * t) * hSin));
        float a = gamma * hCos;
        float b = gamma * hSin;
        float rA = (((p2 * 460.0f) + (451.0f * a)) + (288.0f * b)) / 1403.0f;
        float gA = (((p2 * 460.0f) - (891.0f * a)) - (261.0f * b)) / 1403.0f;
        float bA = (((460.0f * p2) - (220.0f * a)) - (6300.0f * b)) / 1403.0f;
        float alpha2 = Math.abs(rA);
        float rCBase = (float) Math.max(0.0d, (Math.abs(rA) * 27.13d) / (400.0d - alpha2));
        float rC = Math.signum(rA) * (100.0f / frame.getFl()) * ((float) Math.pow(rCBase, 2.380952380952381d));
        float gCBase = (float) Math.max(0.0d, (Math.abs(gA) * 27.13d) / (400.0d - Math.abs(gA)));
        float gC = Math.signum(gA) * (100.0f / frame.getFl()) * ((float) Math.pow(gCBase, 2.380952380952381d));
        float bCBase = (float) Math.max(0.0d, (Math.abs(bA) * 27.13d) / (400.0d - Math.abs(bA)));
        float bC = Math.signum(bA) * (100.0f / frame.getFl()) * ((float) Math.pow(bCBase, 2.380952380952381d));
        float rF = rC / frame.getRgbD()[0];
        float gF = gC / frame.getRgbD()[1];
        float bF = bC / frame.getRgbD()[2];
        float[][] matrix = CamUtils.CAM16RGB_TO_XYZ;
        float x = (matrix[0][0] * rF) + (matrix[0][1] * gF) + (matrix[0][2] * bF);
        float y = (matrix[1][0] * rF) + (matrix[1][1] * gF) + (matrix[1][2] * bF);
        float rCBase2 = (matrix[2][0] * rF) + (matrix[2][1] * gF) + (matrix[2][2] * bF);
        int argb = ColorUtils.XYZToColor(x, y, rCBase2);
        return argb;
    }

    public static int getInt(float hue, float chroma, float lstar, Frame frame) {
        if (frame == Frame.DEFAULT) {
            return HctSolver.solveToInt(hue, chroma, lstar);
        }
        if (chroma < 1.0d || Math.round(lstar) <= 0.0d || Math.round(lstar) >= 100.0d) {
            return CamUtils.intFromLstar(lstar);
        }
        float hue2 = hue >= 0.0f ? Math.min(360.0f, hue) : 0.0f;
        float high = chroma;
        float mid = chroma;
        float low = 0.0f;
        boolean isFirstLoop = true;
        Cam answer = null;
        while (Math.abs(low - high) >= 0.4f) {
            Cam possibleAnswer = findCamByJ(hue2, mid, lstar);
            if (isFirstLoop) {
                if (possibleAnswer != null) {
                    return possibleAnswer.viewed(frame);
                }
                isFirstLoop = false;
                mid = low + ((high - low) / 2.0f);
            } else {
                if (possibleAnswer == null) {
                    high = mid;
                } else {
                    answer = possibleAnswer;
                    low = mid;
                }
                mid = low + ((high - low) / 2.0f);
            }
        }
        if (answer == null) {
            return CamUtils.intFromLstar(lstar);
        }
        return answer.viewed(frame);
    }

    private static Cam findCamByJ(float hue, float chroma, float lstar) {
        float low = 0.0f;
        float high = 100.0f;
        float bestdL = 1000.0f;
        float bestdE = 1000.0f;
        Cam bestCam = null;
        while (Math.abs(low - high) > 0.01f) {
            float mid = low + ((high - low) / 2.0f);
            Cam camBeforeClip = fromJch(mid, chroma, hue);
            int clipped = camBeforeClip.viewedInSrgb();
            float clippedLstar = CamUtils.lstarFromInt(clipped);
            float dL = Math.abs(lstar - clippedLstar);
            if (dL < 0.2f) {
                Cam camClipped = fromInt(clipped);
                float dE = camClipped.distance(fromJch(camClipped.getJ(), camClipped.getChroma(), hue));
                if (dE <= 1.0f) {
                    bestdL = dL;
                    bestdE = dE;
                    bestCam = camClipped;
                }
            }
            if (bestdL == 0.0f && bestdE == 0.0f) {
                break;
            } else if (clippedLstar < lstar) {
                low = mid;
            } else {
                high = mid;
            }
        }
        return bestCam;
    }
}
