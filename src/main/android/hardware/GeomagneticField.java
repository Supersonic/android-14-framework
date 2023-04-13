package android.hardware;

import android.text.format.Time;
import java.util.Calendar;
import java.util.TimeZone;
/* loaded from: classes.dex */
public class GeomagneticField {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final float EARTH_REFERENCE_RADIUS_KM = 6371.2f;
    private static final float EARTH_SEMI_MAJOR_AXIS_KM = 6378.137f;
    private static final float EARTH_SEMI_MINOR_AXIS_KM = 6356.7524f;
    private static final float[][] G_COEFF;
    private static final float[][] SCHMIDT_QUASI_NORM_FACTORS;
    private float mGcLatitudeRad;
    private float mGcLongitudeRad;
    private float mGcRadiusKm;

    /* renamed from: mX */
    private float f92mX;

    /* renamed from: mY */
    private float f93mY;

    /* renamed from: mZ */
    private float f94mZ;
    private static final float[][] H_COEFF = {new float[]{0.0f}, new float[]{0.0f, 4652.9f}, new float[]{0.0f, -2991.6f, -734.8f}, new float[]{0.0f, -82.2f, 241.8f, -542.9f}, new float[]{0.0f, 282.0f, -158.4f, 199.8f, -350.1f}, new float[]{0.0f, 47.7f, 208.4f, -121.3f, 32.2f, 99.1f}, new float[]{0.0f, -19.1f, 25.0f, 52.7f, -64.4f, 9.0f, 68.1f}, new float[]{0.0f, -51.4f, -16.8f, 2.3f, 23.5f, -2.2f, -27.2f, -1.9f}, new float[]{0.0f, 8.4f, -15.3f, 12.8f, -11.8f, 14.9f, 3.6f, -6.9f, 2.8f}, new float[]{0.0f, -23.3f, 11.1f, 9.8f, -5.1f, -6.2f, 7.8f, 0.4f, -1.5f, 9.7f}, new float[]{0.0f, 3.4f, -0.2f, 3.5f, 4.8f, -8.6f, -0.1f, -4.2f, -3.4f, -0.1f, -8.8f}, new float[]{0.0f, 0.0f, 2.6f, -0.5f, -0.4f, 0.6f, -0.2f, -1.7f, -1.6f, -3.0f, -2.0f, -2.6f}, new float[]{0.0f, -1.2f, 0.5f, 1.3f, -1.8f, 0.1f, 0.7f, -0.1f, 0.6f, 0.2f, -0.9f, 0.0f, 0.5f}};
    private static final float[][] DELTA_G = {new float[]{0.0f}, new float[]{6.7f, 7.7f}, new float[]{-11.5f, -7.1f, -2.2f}, new float[]{2.8f, -6.2f, 3.4f, -12.2f}, new float[]{-1.1f, -1.6f, -6.0f, 5.4f, -5.5f}, new float[]{-0.3f, 0.6f, -0.7f, 0.1f, 1.2f, 1.0f}, new float[]{-0.6f, -0.4f, 0.5f, 1.4f, -1.4f, 0.0f, 0.8f}, new float[]{-0.1f, -0.3f, -0.1f, 0.7f, 0.2f, -0.5f, -0.8f, 1.0f}, new float[]{-0.1f, 0.1f, -0.1f, 0.5f, -0.1f, 0.4f, 0.5f, 0.0f, 0.4f}, new float[]{-0.1f, -0.2f, 0.0f, 0.4f, -0.3f, 0.0f, 0.3f, 0.0f, 0.0f, -0.4f}, new float[]{0.0f, 0.0f, 0.0f, 0.2f, -0.1f, -0.2f, 0.0f, -0.1f, -0.2f, -0.1f, 0.0f}, new float[]{0.0f, -0.1f, 0.0f, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, -0.1f, -0.1f, -0.1f, -0.1f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -0.1f}};
    private static final float[][] DELTA_H = {new float[]{0.0f}, new float[]{0.0f, -25.1f}, new float[]{0.0f, -30.2f, -23.9f}, new float[]{0.0f, 5.7f, -1.0f, 1.1f}, new float[]{0.0f, 0.2f, 6.9f, 3.7f, -5.6f}, new float[]{0.0f, 0.1f, 2.5f, -0.9f, 3.0f, 0.5f}, new float[]{0.0f, 0.1f, -1.8f, -1.4f, 0.9f, 0.1f, 1.0f}, new float[]{0.0f, 0.5f, 0.6f, -0.7f, -0.2f, -1.2f, 0.2f, 0.3f}, new float[]{0.0f, -0.3f, 0.7f, -0.2f, 0.5f, -0.3f, -0.5f, 0.4f, 0.1f}, new float[]{0.0f, -0.3f, 0.2f, -0.4f, 0.4f, 0.1f, 0.0f, -0.2f, 0.5f, 0.2f}, new float[]{0.0f, 0.0f, 0.1f, -0.3f, 0.1f, -0.2f, 0.1f, 0.0f, -0.1f, 0.2f, 0.0f}, new float[]{0.0f, 0.0f, 0.1f, 0.0f, 0.2f, 0.0f, 0.0f, 0.1f, 0.0f, -0.1f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, -0.1f, 0.1f, 0.0f, 0.0f, 0.0f, 0.1f, 0.0f, 0.0f, 0.0f, -0.1f}};
    private static final long BASE_TIME = new Calendar.Builder().setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC)).setDate(2020, 0, 1).build().getTimeInMillis();

    static {
        float[][] fArr = {new float[]{0.0f}, new float[]{-29404.5f, -1450.7f}, new float[]{-2500.0f, 2982.0f, 1676.8f}, new float[]{1363.9f, -2381.0f, 1236.2f, 525.7f}, new float[]{903.1f, 809.4f, 86.2f, -309.4f, 47.9f}, new float[]{-234.4f, 363.1f, 187.8f, -140.7f, -151.2f, 13.7f}, new float[]{65.9f, 65.6f, 73.0f, -121.5f, -36.2f, 13.5f, -64.7f}, new float[]{80.6f, -76.8f, -8.3f, 56.5f, 15.8f, 6.4f, -7.2f, 9.8f}, new float[]{23.6f, 9.8f, -17.5f, -0.4f, -21.1f, 15.3f, 13.7f, -16.5f, -0.3f}, new float[]{5.0f, 8.2f, 2.9f, -1.4f, -1.1f, -13.3f, 1.1f, 8.9f, -9.3f, -11.9f}, new float[]{-1.9f, -6.2f, -0.1f, 1.7f, -0.9f, 0.6f, -0.9f, 1.9f, 1.4f, -2.4f, -3.9f}, new float[]{3.0f, -1.4f, -2.5f, 2.4f, -0.9f, 0.3f, -0.7f, -0.1f, 1.4f, -0.6f, 0.2f, 3.1f}, new float[]{-2.0f, -0.1f, 0.5f, 1.3f, -1.2f, 0.7f, 0.3f, 0.5f, -0.2f, -0.5f, 0.1f, -1.1f, -0.3f}};
        G_COEFF = fArr;
        SCHMIDT_QUASI_NORM_FACTORS = computeSchmidtQuasiNormFactors(fArr.length);
    }

    public GeomagneticField(float gdLatitudeDeg, float gdLongitudeDeg, float altitudeMeters, long timeMillis) {
        int MAX_N = G_COEFF.length;
        float gdLatitudeDeg2 = Math.min(89.99999f, Math.max(-89.99999f, gdLatitudeDeg));
        computeGeocentricCoordinates(gdLatitudeDeg2, gdLongitudeDeg, altitudeMeters);
        LegendreTable legendre = new LegendreTable(MAX_N - 1, (float) (1.5707963267948966d - this.mGcLatitudeRad));
        float[] relativeRadiusPower = new float[MAX_N + 2];
        relativeRadiusPower[0] = 1.0f;
        relativeRadiusPower[1] = EARTH_REFERENCE_RADIUS_KM / this.mGcRadiusKm;
        for (int i = 2; i < relativeRadiusPower.length; i++) {
            relativeRadiusPower[i] = relativeRadiusPower[i - 1] * relativeRadiusPower[1];
        }
        float[] sinMLon = new float[MAX_N];
        float[] cosMLon = new float[MAX_N];
        sinMLon[0] = 0.0f;
        cosMLon[0] = 1.0f;
        sinMLon[1] = (float) Math.sin(this.mGcLongitudeRad);
        cosMLon[1] = (float) Math.cos(this.mGcLongitudeRad);
        for (int m = 2; m < MAX_N; m++) {
            int x = m >> 1;
            sinMLon[m] = (sinMLon[m - x] * cosMLon[x]) + (cosMLon[m - x] * sinMLon[x]);
            cosMLon[m] = (cosMLon[m - x] * cosMLon[x]) - (sinMLon[m - x] * sinMLon[x]);
        }
        float inverseCosLatitude = 1.0f / ((float) Math.cos(this.mGcLatitudeRad));
        float yearsSinceBase = ((float) (timeMillis - BASE_TIME)) / 3.1536001E10f;
        float gcX = 0.0f;
        float gcY = 0.0f;
        float gcZ = 0.0f;
        for (int n = 1; n < MAX_N; n++) {
            int m2 = 0;
            while (m2 <= n) {
                float g = G_COEFF[n][m2] + (DELTA_G[n][m2] * yearsSinceBase);
                float h = H_COEFF[n][m2] + (DELTA_H[n][m2] * yearsSinceBase);
                int MAX_N2 = MAX_N;
                float f = relativeRadiusPower[n + 2] * ((cosMLon[m2] * g) + (sinMLon[m2] * h)) * legendre.mPDeriv[n][m2];
                float[][] fArr = SCHMIDT_QUASI_NORM_FACTORS;
                gcX += f * fArr[n][m2];
                gcY += relativeRadiusPower[n + 2] * m2 * ((sinMLon[m2] * g) - (cosMLon[m2] * h)) * legendre.f95mP[n][m2] * fArr[n][m2] * inverseCosLatitude;
                gcZ -= ((((n + 1) * relativeRadiusPower[n + 2]) * ((cosMLon[m2] * g) + (sinMLon[m2] * h))) * legendre.f95mP[n][m2]) * fArr[n][m2];
                m2++;
                MAX_N = MAX_N2;
            }
        }
        double latDiffRad = Math.toRadians(gdLatitudeDeg2) - this.mGcLatitudeRad;
        this.f92mX = (float) ((gcX * Math.cos(latDiffRad)) + (gcZ * Math.sin(latDiffRad)));
        this.f93mY = gcY;
        this.f94mZ = (float) (((-gcX) * Math.sin(latDiffRad)) + (gcZ * Math.cos(latDiffRad)));
    }

    public float getX() {
        return this.f92mX;
    }

    public float getY() {
        return this.f93mY;
    }

    public float getZ() {
        return this.f94mZ;
    }

    public float getDeclination() {
        return (float) Math.toDegrees(Math.atan2(this.f93mY, this.f92mX));
    }

    public float getInclination() {
        return (float) Math.toDegrees(Math.atan2(this.f94mZ, getHorizontalStrength()));
    }

    public float getHorizontalStrength() {
        return (float) Math.hypot(this.f92mX, this.f93mY);
    }

    public float getFieldStrength() {
        float f = this.f92mX;
        float f2 = this.f93mY;
        float f3 = (f * f) + (f2 * f2);
        float f4 = this.f94mZ;
        return (float) Math.sqrt(f3 + (f4 * f4));
    }

    private void computeGeocentricCoordinates(float gdLatitudeDeg, float gdLongitudeDeg, float altitudeMeters) {
        float altitudeKm = altitudeMeters / 1000.0f;
        double gdLatRad = Math.toRadians(gdLatitudeDeg);
        float clat = (float) Math.cos(gdLatRad);
        float slat = (float) Math.sin(gdLatRad);
        float tlat = slat / clat;
        float latRad = (float) Math.sqrt((4.0680636E7f * clat * clat) + (4.04083E7f * slat * slat));
        this.mGcLatitudeRad = (float) Math.atan((((latRad * altitudeKm) + 4.04083E7f) * tlat) / ((latRad * altitudeKm) + 4.0680636E7f));
        this.mGcLongitudeRad = (float) Math.toRadians(gdLongitudeDeg);
        float radSq = (altitudeKm * altitudeKm) + (2.0f * altitudeKm * ((float) Math.sqrt((4.0680636E7f * clat * clat) + (4.04083E7f * slat * slat)))) + (((((4.0680636E7f * 4.0680636E7f) * clat) * clat) + (((4.04083E7f * 4.04083E7f) * slat) * slat)) / (((4.0680636E7f * clat) * clat) + ((4.04083E7f * slat) * slat)));
        this.mGcRadiusKm = (float) Math.sqrt(radSq);
    }

    /* loaded from: classes.dex */
    private static class LegendreTable {
        static final /* synthetic */ boolean $assertionsDisabled = false;

        /* renamed from: mP */
        public final float[][] f95mP;
        public final float[][] mPDeriv;

        public LegendreTable(int maxN, float thetaRad) {
            float cos = (float) Math.cos(thetaRad);
            float sin = (float) Math.sin(thetaRad);
            float[][] fArr = new float[maxN + 1];
            this.f95mP = fArr;
            float[][] fArr2 = new float[maxN + 1];
            this.mPDeriv = fArr2;
            float[] fArr3 = new float[1];
            fArr3[0] = 1.0f;
            fArr[0] = fArr3;
            float[] fArr4 = new float[1];
            fArr4[0] = 0.0f;
            fArr2[0] = fArr4;
            for (int n = 1; n <= maxN; n++) {
                this.f95mP[n] = new float[n + 1];
                this.mPDeriv[n] = new float[n + 1];
                for (int m = 0; m <= n; m++) {
                    if (n == m) {
                        float[][] fArr5 = this.f95mP;
                        fArr5[n][m] = fArr5[n - 1][m - 1] * sin;
                        float[][] fArr6 = this.mPDeriv;
                        fArr6[n][m] = (fArr5[n - 1][m - 1] * cos) + (fArr6[n - 1][m - 1] * sin);
                    } else if (n != 1 && m != n - 1) {
                        float k = (((n - 1) * (n - 1)) - (m * m)) / (((n * 2) - 1) * ((n * 2) - 3));
                        float[][] fArr7 = this.f95mP;
                        fArr7[n][m] = (fArr7[n - 1][m] * cos) - (fArr7[n - 2][m] * k);
                        float[][] fArr8 = this.mPDeriv;
                        fArr8[n][m] = (((-sin) * fArr7[n - 1][m]) + (fArr8[n - 1][m] * cos)) - (fArr8[n - 2][m] * k);
                    } else {
                        float[][] fArr9 = this.f95mP;
                        fArr9[n][m] = fArr9[n - 1][m] * cos;
                        float[][] fArr10 = this.mPDeriv;
                        fArr10[n][m] = ((-sin) * fArr9[n - 1][m]) + (fArr10[n - 1][m] * cos);
                    }
                }
            }
        }
    }

    private static float[][] computeSchmidtQuasiNormFactors(int maxN) {
        float[][] schmidtQuasiNorm = new float[maxN + 1];
        float[] fArr = new float[1];
        fArr[0] = 1.0f;
        schmidtQuasiNorm[0] = fArr;
        for (int n = 1; n <= maxN; n++) {
            schmidtQuasiNorm[n] = new float[n + 1];
            schmidtQuasiNorm[n][0] = (schmidtQuasiNorm[n - 1][0] * ((n * 2) - 1)) / n;
            int m = 1;
            while (m <= n) {
                schmidtQuasiNorm[n][m] = schmidtQuasiNorm[n][m - 1] * ((float) Math.sqrt((((n - m) + 1) * (m == 1 ? 2 : 1)) / (n + m)));
                m++;
            }
        }
        return schmidtQuasiNorm;
    }
}
