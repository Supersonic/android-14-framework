package android.opengl;
/* loaded from: classes2.dex */
public class Matrix {
    private static final ThreadLocal<float[]> ThreadTmp = new ThreadLocal() { // from class: android.opengl.Matrix.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // java.lang.ThreadLocal
        public float[] initialValue() {
            return new float[32];
        }
    };

    private static boolean overlap(float[] a, int aStart, int aLength, float[] b, int bStart, int bLength) {
        int aEnd;
        int bEnd;
        if (a != b) {
            return false;
        }
        if (aStart == bStart || (aEnd = aStart + aLength) == (bEnd = bStart + bLength)) {
            return true;
        }
        if (aStart < bStart && bStart < aEnd) {
            return true;
        }
        if (aStart < bEnd && bEnd < aEnd) {
            return true;
        }
        if (bStart < aStart && aStart < bEnd) {
            return true;
        }
        if (bStart >= aEnd || aEnd >= bEnd) {
            return false;
        }
        return true;
    }

    public static void multiplyMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset) {
        if (result == null) {
            throw new IllegalArgumentException("result == null");
        }
        if (lhs == null) {
            throw new IllegalArgumentException("lhs == null");
        }
        if (rhs == null) {
            throw new IllegalArgumentException("rhs == null");
        }
        if (resultOffset < 0) {
            throw new IllegalArgumentException("resultOffset < 0");
        }
        if (lhsOffset < 0) {
            throw new IllegalArgumentException("lhsOffset < 0");
        }
        if (rhsOffset < 0) {
            throw new IllegalArgumentException("rhsOffset < 0");
        }
        if (result.length < resultOffset + 16) {
            throw new IllegalArgumentException("result.length < resultOffset + 16");
        }
        if (lhs.length < lhsOffset + 16) {
            throw new IllegalArgumentException("lhs.length < lhsOffset + 16");
        }
        if (rhs.length < rhsOffset + 16) {
            throw new IllegalArgumentException("rhs.length < rhsOffset + 16");
        }
        if (overlap(result, resultOffset, 16, lhs, lhsOffset, 16) || overlap(result, resultOffset, 16, rhs, rhsOffset, 16)) {
            float[] tmp = ThreadTmp.get();
            for (int i = 0; i < 4; i++) {
                float rhs_i0 = rhs[(i * 4) + 0 + rhsOffset];
                float ri0 = lhs[lhsOffset + 0] * rhs_i0;
                float ri1 = lhs[lhsOffset + 1] * rhs_i0;
                float ri2 = lhs[lhsOffset + 2] * rhs_i0;
                float ri3 = lhs[lhsOffset + 3] * rhs_i0;
                for (int j = 1; j < 4; j++) {
                    float rhs_ij = rhs[(i * 4) + j + rhsOffset];
                    ri0 += lhs[(j * 4) + 0 + lhsOffset] * rhs_ij;
                    ri1 += lhs[(j * 4) + 1 + lhsOffset] * rhs_ij;
                    ri2 += lhs[(j * 4) + 2 + lhsOffset] * rhs_ij;
                    ri3 += lhs[(j * 4) + 3 + lhsOffset] * rhs_ij;
                }
                int j2 = i * 4;
                tmp[j2 + 0] = ri0;
                tmp[(i * 4) + 1] = ri1;
                tmp[(i * 4) + 2] = ri2;
                tmp[(i * 4) + 3] = ri3;
            }
            for (int i2 = 0; i2 < 16; i2++) {
                result[i2 + resultOffset] = tmp[i2];
            }
            return;
        }
        for (int i3 = 0; i3 < 4; i3++) {
            float rhs_i02 = rhs[(i3 * 4) + 0 + rhsOffset];
            float ri02 = lhs[lhsOffset + 0] * rhs_i02;
            float ri12 = lhs[lhsOffset + 1] * rhs_i02;
            float ri22 = lhs[lhsOffset + 2] * rhs_i02;
            float ri32 = lhs[lhsOffset + 3] * rhs_i02;
            for (int j3 = 1; j3 < 4; j3++) {
                float rhs_ij2 = rhs[(i3 * 4) + j3 + rhsOffset];
                ri02 += lhs[(j3 * 4) + 0 + lhsOffset] * rhs_ij2;
                ri12 += lhs[(j3 * 4) + 1 + lhsOffset] * rhs_ij2;
                ri22 += lhs[(j3 * 4) + 2 + lhsOffset] * rhs_ij2;
                ri32 += lhs[(j3 * 4) + 3 + lhsOffset] * rhs_ij2;
            }
            int j4 = i3 * 4;
            result[j4 + 0 + resultOffset] = ri02;
            result[(i3 * 4) + 1 + resultOffset] = ri12;
            result[(i3 * 4) + 2 + resultOffset] = ri22;
            result[(i3 * 4) + 3 + resultOffset] = ri32;
        }
    }

    public static void multiplyMV(float[] resultVec, int resultVecOffset, float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset) {
        if (resultVec == null) {
            throw new IllegalArgumentException("resultVec == null");
        }
        if (lhsMat == null) {
            throw new IllegalArgumentException("lhsMat == null");
        }
        if (rhsVec == null) {
            throw new IllegalArgumentException("rhsVec == null");
        }
        if (resultVecOffset < 0) {
            throw new IllegalArgumentException("resultVecOffset < 0");
        }
        if (lhsMatOffset < 0) {
            throw new IllegalArgumentException("lhsMatOffset < 0");
        }
        if (rhsVecOffset < 0) {
            throw new IllegalArgumentException("rhsVecOffset < 0");
        }
        if (resultVec.length < resultVecOffset + 4) {
            throw new IllegalArgumentException("resultVec.length < resultVecOffset + 4");
        }
        if (lhsMat.length < lhsMatOffset + 16) {
            throw new IllegalArgumentException("lhsMat.length < lhsMatOffset + 16");
        }
        if (rhsVec.length < rhsVecOffset + 4) {
            throw new IllegalArgumentException("rhsVec.length < rhsVecOffset + 4");
        }
        float tmp0 = (lhsMat[lhsMatOffset + 0] * rhsVec[rhsVecOffset + 0]) + (lhsMat[lhsMatOffset + 4] * rhsVec[rhsVecOffset + 1]) + (lhsMat[lhsMatOffset + 8] * rhsVec[rhsVecOffset + 2]) + (lhsMat[lhsMatOffset + 12] * rhsVec[rhsVecOffset + 3]);
        float tmp1 = (lhsMat[lhsMatOffset + 1] * rhsVec[rhsVecOffset + 0]) + (lhsMat[lhsMatOffset + 5] * rhsVec[rhsVecOffset + 1]) + (lhsMat[lhsMatOffset + 9] * rhsVec[rhsVecOffset + 2]) + (lhsMat[lhsMatOffset + 13] * rhsVec[rhsVecOffset + 3]);
        float tmp2 = (lhsMat[lhsMatOffset + 2] * rhsVec[rhsVecOffset + 0]) + (lhsMat[lhsMatOffset + 6] * rhsVec[rhsVecOffset + 1]) + (lhsMat[lhsMatOffset + 10] * rhsVec[rhsVecOffset + 2]) + (lhsMat[lhsMatOffset + 14] * rhsVec[rhsVecOffset + 3]);
        float tmp3 = (lhsMat[lhsMatOffset + 3] * rhsVec[rhsVecOffset + 0]) + (lhsMat[lhsMatOffset + 7] * rhsVec[rhsVecOffset + 1]) + (lhsMat[lhsMatOffset + 11] * rhsVec[rhsVecOffset + 2]) + (lhsMat[lhsMatOffset + 15] * rhsVec[rhsVecOffset + 3]);
        resultVec[resultVecOffset + 0] = tmp0;
        resultVec[resultVecOffset + 1] = tmp1;
        resultVec[resultVecOffset + 2] = tmp2;
        resultVec[resultVecOffset + 3] = tmp3;
    }

    public static void transposeM(float[] mTrans, int mTransOffset, float[] m, int mOffset) {
        for (int i = 0; i < 4; i++) {
            int mBase = (i * 4) + mOffset;
            mTrans[i + mTransOffset] = m[mBase];
            mTrans[i + 4 + mTransOffset] = m[mBase + 1];
            mTrans[i + 8 + mTransOffset] = m[mBase + 2];
            mTrans[i + 12 + mTransOffset] = m[mBase + 3];
        }
    }

    public static boolean invertM(float[] mInv, int mInvOffset, float[] m, int mOffset) {
        float src0 = m[mOffset + 0];
        float src4 = m[mOffset + 1];
        float src8 = m[mOffset + 2];
        float src12 = m[mOffset + 3];
        float src1 = m[mOffset + 4];
        float src5 = m[mOffset + 5];
        float src9 = m[mOffset + 6];
        float src13 = m[mOffset + 7];
        float src2 = m[mOffset + 8];
        float src6 = m[mOffset + 9];
        float src10 = m[mOffset + 10];
        float src14 = m[mOffset + 11];
        float src3 = m[mOffset + 12];
        float src7 = m[mOffset + 13];
        float src11 = m[mOffset + 14];
        float src15 = m[mOffset + 15];
        float atmp0 = src10 * src15;
        float atmp1 = src11 * src14;
        float atmp2 = src9 * src15;
        float atmp3 = src11 * src13;
        float atmp4 = src9 * src14;
        float atmp5 = src10 * src13;
        float atmp6 = src8 * src15;
        float atmp7 = src11 * src12;
        float atmp8 = src8 * src14;
        float atmp9 = src10 * src12;
        float atmp10 = src8 * src13;
        float atmp11 = src9 * src12;
        float dst0 = (((atmp0 * src5) + (atmp3 * src6)) + (atmp4 * src7)) - (((atmp1 * src5) + (atmp2 * src6)) + (atmp5 * src7));
        float dst1 = (((atmp1 * src4) + (atmp6 * src6)) + (atmp9 * src7)) - (((atmp0 * src4) + (atmp7 * src6)) + (atmp8 * src7));
        float dst2 = (((atmp2 * src4) + (atmp7 * src5)) + (atmp10 * src7)) - (((atmp3 * src4) + (atmp6 * src5)) + (atmp11 * src7));
        float dst3 = (((atmp5 * src4) + (atmp8 * src5)) + (atmp11 * src6)) - (((atmp4 * src4) + (atmp9 * src5)) + (atmp10 * src6));
        float dst4 = (((atmp1 * src1) + (atmp2 * src2)) + (atmp5 * src3)) - (((atmp0 * src1) + (atmp3 * src2)) + (atmp4 * src3));
        float dst5 = (((atmp0 * src0) + (atmp7 * src2)) + (atmp8 * src3)) - (((atmp1 * src0) + (atmp6 * src2)) + (atmp9 * src3));
        float dst6 = (((atmp3 * src0) + (atmp6 * src1)) + (atmp11 * src3)) - (((atmp2 * src0) + (atmp7 * src1)) + (atmp10 * src3));
        float dst7 = (((atmp4 * src0) + (atmp9 * src1)) + (atmp10 * src2)) - (((atmp5 * src0) + (atmp8 * src1)) + (atmp11 * src2));
        float btmp0 = src2 * src7;
        float btmp1 = src3 * src6;
        float btmp2 = src1 * src7;
        float btmp3 = src3 * src5;
        float btmp4 = src1 * src6;
        float btmp5 = src2 * src5;
        float btmp6 = src0 * src7;
        float btmp7 = src3 * src4;
        float btmp8 = src0 * src6;
        float btmp9 = src2 * src4;
        float btmp10 = src0 * src5;
        float btmp11 = src1 * src4;
        float dst8 = (((btmp0 * src13) + (btmp3 * src14)) + (btmp4 * src15)) - (((btmp1 * src13) + (btmp2 * src14)) + (btmp5 * src15));
        float dst9 = (((btmp1 * src12) + (btmp6 * src14)) + (btmp9 * src15)) - (((btmp0 * src12) + (btmp7 * src14)) + (btmp8 * src15));
        float dst10 = (((btmp2 * src12) + (btmp7 * src13)) + (btmp10 * src15)) - (((btmp3 * src12) + (btmp6 * src13)) + (btmp11 * src15));
        float dst11 = (((btmp5 * src12) + (btmp8 * src13)) + (btmp11 * src14)) - (((btmp4 * src12) + (btmp9 * src13)) + (btmp10 * src14));
        float dst12 = (((btmp2 * src10) + (btmp5 * src11)) + (btmp1 * src9)) - (((btmp4 * src11) + (btmp0 * src9)) + (btmp3 * src10));
        float dst13 = (((btmp8 * src11) + (btmp0 * src8)) + (btmp7 * src10)) - (((btmp6 * src10) + (btmp9 * src11)) + (btmp1 * src8));
        float dst14 = (((btmp6 * src9) + (btmp11 * src11)) + (btmp3 * src8)) - (((btmp10 * src11) + (btmp2 * src8)) + (btmp7 * src9));
        float dst15 = (((btmp10 * src10) + (btmp4 * src8)) + (btmp9 * src9)) - (((btmp8 * src9) + (btmp11 * src10)) + (btmp5 * src8));
        float det = (src0 * dst0) + (src1 * dst1) + (src2 * dst2) + (src3 * dst3);
        if (det == 0.0f) {
            return false;
        }
        float invdet = 1.0f / det;
        mInv[mInvOffset] = dst0 * invdet;
        mInv[mInvOffset + 1] = dst1 * invdet;
        mInv[mInvOffset + 2] = dst2 * invdet;
        mInv[mInvOffset + 3] = dst3 * invdet;
        mInv[mInvOffset + 4] = dst4 * invdet;
        mInv[mInvOffset + 5] = dst5 * invdet;
        mInv[mInvOffset + 6] = dst6 * invdet;
        mInv[mInvOffset + 7] = dst7 * invdet;
        mInv[mInvOffset + 8] = dst8 * invdet;
        mInv[mInvOffset + 9] = dst9 * invdet;
        mInv[mInvOffset + 10] = dst10 * invdet;
        mInv[mInvOffset + 11] = dst11 * invdet;
        mInv[mInvOffset + 12] = dst12 * invdet;
        mInv[mInvOffset + 13] = dst13 * invdet;
        mInv[mInvOffset + 14] = dst14 * invdet;
        mInv[mInvOffset + 15] = dst15 * invdet;
        return true;
    }

    public static void orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (bottom == top) {
            throw new IllegalArgumentException("bottom == top");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }
        float r_width = 1.0f / (right - left);
        float r_height = 1.0f / (top - bottom);
        float r_depth = 1.0f / (far - near);
        float x = r_width * 2.0f;
        float y = 2.0f * r_height;
        float z = (-2.0f) * r_depth;
        float tx = (-(right + left)) * r_width;
        float ty = (-(top + bottom)) * r_height;
        float tz = (-(far + near)) * r_depth;
        m[mOffset + 0] = x;
        m[mOffset + 5] = y;
        m[mOffset + 10] = z;
        m[mOffset + 12] = tx;
        m[mOffset + 13] = ty;
        m[mOffset + 14] = tz;
        m[mOffset + 15] = 1.0f;
        m[mOffset + 1] = 0.0f;
        m[mOffset + 2] = 0.0f;
        m[mOffset + 3] = 0.0f;
        m[mOffset + 4] = 0.0f;
        m[mOffset + 6] = 0.0f;
        m[mOffset + 7] = 0.0f;
        m[mOffset + 8] = 0.0f;
        m[mOffset + 9] = 0.0f;
        m[mOffset + 11] = 0.0f;
    }

    public static void frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near, float far) {
        if (left == right) {
            throw new IllegalArgumentException("left == right");
        }
        if (top == bottom) {
            throw new IllegalArgumentException("top == bottom");
        }
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }
        if (near <= 0.0f) {
            throw new IllegalArgumentException("near <= 0.0f");
        }
        if (far <= 0.0f) {
            throw new IllegalArgumentException("far <= 0.0f");
        }
        float r_width = 1.0f / (right - left);
        float r_height = 1.0f / (top - bottom);
        float r_depth = 1.0f / (near - far);
        float x = near * r_width * 2.0f;
        float y = near * r_height * 2.0f;
        float A = (right + left) * r_width;
        float B = (top + bottom) * r_height;
        float C = (far + near) * r_depth;
        float D = far * near * r_depth * 2.0f;
        m[offset + 0] = x;
        m[offset + 5] = y;
        m[offset + 8] = A;
        m[offset + 9] = B;
        m[offset + 10] = C;
        m[offset + 14] = D;
        m[offset + 11] = -1.0f;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;
        m[offset + 4] = 0.0f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;
        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 15] = 0.0f;
    }

    public static void perspectiveM(float[] m, int offset, float fovy, float aspect, float zNear, float zFar) {
        float f = 1.0f / ((float) Math.tan(fovy * 0.008726646259971648d));
        float rangeReciprocal = 1.0f / (zNear - zFar);
        m[offset + 0] = f / aspect;
        m[offset + 1] = 0.0f;
        m[offset + 2] = 0.0f;
        m[offset + 3] = 0.0f;
        m[offset + 4] = 0.0f;
        m[offset + 5] = f;
        m[offset + 6] = 0.0f;
        m[offset + 7] = 0.0f;
        m[offset + 8] = 0.0f;
        m[offset + 9] = 0.0f;
        m[offset + 10] = (zFar + zNear) * rangeReciprocal;
        m[offset + 11] = -1.0f;
        m[offset + 12] = 0.0f;
        m[offset + 13] = 0.0f;
        m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
        m[offset + 15] = 0.0f;
    }

    public static float length(float x, float y, float z) {
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public static void setIdentityM(float[] sm, int smOffset) {
        for (int i = 0; i < 16; i++) {
            sm[smOffset + i] = 0.0f;
        }
        for (int i2 = 0; i2 < 16; i2 += 5) {
            sm[smOffset + i2] = 1.0f;
        }
    }

    public static void scaleM(float[] sm, int smOffset, float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            int smi = smOffset + i;
            int mi = mOffset + i;
            sm[smi] = m[mi] * x;
            sm[smi + 4] = m[mi + 4] * y;
            sm[smi + 8] = m[mi + 8] * z;
            sm[smi + 12] = m[mi + 12];
        }
    }

    public static void scaleM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            int mi = mOffset + i;
            m[mi] = m[mi] * x;
            int i2 = mi + 4;
            m[i2] = m[i2] * y;
            int i3 = mi + 8;
            m[i3] = m[i3] * z;
        }
    }

    public static void translateM(float[] tm, int tmOffset, float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 12; i++) {
            tm[tmOffset + i] = m[mOffset + i];
        }
        for (int i2 = 0; i2 < 4; i2++) {
            int tmi = tmOffset + i2;
            int mi = mOffset + i2;
            tm[tmi + 12] = (m[mi] * x) + (m[mi + 4] * y) + (m[mi + 8] * z) + m[mi + 12];
        }
    }

    public static void translateM(float[] m, int mOffset, float x, float y, float z) {
        for (int i = 0; i < 4; i++) {
            int mi = mOffset + i;
            int i2 = mi + 12;
            m[i2] = m[i2] + (m[mi] * x) + (m[mi + 4] * y) + (m[mi + 8] * z);
        }
    }

    public static void rotateM(float[] rm, int rmOffset, float[] m, int mOffset, float a, float x, float y, float z) {
        float[] tmp = ThreadTmp.get();
        setRotateM(tmp, 16, a, x, y, z);
        multiplyMM(rm, rmOffset, m, mOffset, tmp, 16);
    }

    public static void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
        rotateM(m, mOffset, m, mOffset, a, x, y, z);
    }

    public static void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z) {
        float x2;
        float y2;
        float z2;
        rm[rmOffset + 3] = 0.0f;
        rm[rmOffset + 7] = 0.0f;
        rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
        float a2 = 0.017453292f * a;
        float s = (float) Math.sin(a2);
        float c = (float) Math.cos(a2);
        if (1.0f == x && 0.0f == y && 0.0f == z) {
            rm[rmOffset + 5] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 6] = s;
            rm[rmOffset + 9] = -s;
            rm[rmOffset + 1] = 0.0f;
            rm[rmOffset + 2] = 0.0f;
            rm[rmOffset + 4] = 0.0f;
            rm[rmOffset + 8] = 0.0f;
            rm[rmOffset + 0] = 1.0f;
        } else if (0.0f == x && 1.0f == y && 0.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 8] = s;
            rm[rmOffset + 2] = -s;
            rm[rmOffset + 1] = 0.0f;
            rm[rmOffset + 4] = 0.0f;
            rm[rmOffset + 6] = 0.0f;
            rm[rmOffset + 9] = 0.0f;
            rm[rmOffset + 5] = 1.0f;
        } else if (0.0f == x && 0.0f == y && 1.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 5] = c;
            rm[rmOffset + 1] = s;
            rm[rmOffset + 4] = -s;
            rm[rmOffset + 2] = 0.0f;
            rm[rmOffset + 6] = 0.0f;
            rm[rmOffset + 8] = 0.0f;
            rm[rmOffset + 9] = 0.0f;
            rm[rmOffset + 10] = 1.0f;
        } else {
            float len = length(x, y, z);
            if (1.0f == len) {
                x2 = x;
                y2 = y;
                z2 = z;
            } else {
                float recipLen = 1.0f / len;
                x2 = x * recipLen;
                y2 = y * recipLen;
                z2 = z * recipLen;
            }
            float nc = 1.0f - c;
            float xy = x2 * y2;
            float yz = y2 * z2;
            float zx = z2 * x2;
            float xs = x2 * s;
            float ys = y2 * s;
            float zs = z2 * s;
            rm[rmOffset + 0] = (x2 * x2 * nc) + c;
            rm[rmOffset + 4] = (xy * nc) - zs;
            rm[rmOffset + 8] = (zx * nc) + ys;
            rm[rmOffset + 1] = (xy * nc) + zs;
            rm[rmOffset + 5] = (y2 * y2 * nc) + c;
            rm[rmOffset + 9] = (yz * nc) - xs;
            rm[rmOffset + 2] = (zx * nc) - ys;
            rm[rmOffset + 6] = (yz * nc) + xs;
            rm[rmOffset + 10] = (z2 * z2 * nc) + c;
        }
    }

    @Deprecated
    public static void setRotateEulerM(float[] rm, int rmOffset, float x, float y, float z) {
        float x2 = x * 0.017453292f;
        float y2 = y * 0.017453292f;
        float z2 = 0.017453292f * z;
        float cx = (float) Math.cos(x2);
        float sx = (float) Math.sin(x2);
        float cy = (float) Math.cos(y2);
        float sy = (float) Math.sin(y2);
        float cz = (float) Math.cos(z2);
        float sz = (float) Math.sin(z2);
        float cxsy = cx * sy;
        float sxsy = sx * sy;
        rm[rmOffset + 0] = cy * cz;
        rm[rmOffset + 1] = (-cy) * sz;
        rm[rmOffset + 2] = sy;
        rm[rmOffset + 3] = 0.0f;
        rm[rmOffset + 4] = (cxsy * cz) + (cx * sz);
        rm[rmOffset + 5] = ((-cxsy) * sz) + (cx * cz);
        rm[rmOffset + 6] = (-sx) * cy;
        rm[rmOffset + 7] = 0.0f;
        rm[rmOffset + 8] = ((-sxsy) * cz) + (sx * sz);
        rm[rmOffset + 9] = (sxsy * sz) + (sx * cz);
        rm[rmOffset + 10] = cx * cy;
        rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
    }

    public static void setRotateEulerM2(float[] rm, int rmOffset, float x, float y, float z) {
        if (rm == null) {
            throw new IllegalArgumentException("rm == null");
        }
        if (rmOffset < 0) {
            throw new IllegalArgumentException("rmOffset < 0");
        }
        if (rm.length < rmOffset + 16) {
            throw new IllegalArgumentException("rm.length < rmOffset + 16");
        }
        float x2 = x * 0.017453292f;
        float y2 = y * 0.017453292f;
        float z2 = 0.017453292f * z;
        float cx = (float) Math.cos(x2);
        float sx = (float) Math.sin(x2);
        float cy = (float) Math.cos(y2);
        float sy = (float) Math.sin(y2);
        float cz = (float) Math.cos(z2);
        float sz = (float) Math.sin(z2);
        float cxsy = cx * sy;
        float sxsy = sx * sy;
        rm[rmOffset + 0] = cy * cz;
        rm[rmOffset + 1] = (-cy) * sz;
        rm[rmOffset + 2] = sy;
        rm[rmOffset + 3] = 0.0f;
        rm[rmOffset + 4] = (sxsy * cz) + (cx * sz);
        rm[rmOffset + 5] = ((-sxsy) * sz) + (cx * cz);
        rm[rmOffset + 6] = (-sx) * cy;
        rm[rmOffset + 7] = 0.0f;
        rm[rmOffset + 8] = ((-cxsy) * cz) + (sx * sz);
        rm[rmOffset + 9] = (cxsy * sz) + (sx * cz);
        rm[rmOffset + 10] = cx * cy;
        rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
    }

    public static void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        float fx = centerX - eyeX;
        float fy = centerY - eyeY;
        float fz = centerZ - eyeZ;
        float rlf = 1.0f / length(fx, fy, fz);
        float fx2 = fx * rlf;
        float fy2 = fy * rlf;
        float fz2 = fz * rlf;
        float sx = (fy2 * upZ) - (fz2 * upY);
        float sy = (fz2 * upX) - (fx2 * upZ);
        float sz = (fx2 * upY) - (fy2 * upX);
        float rls = 1.0f / length(sx, sy, sz);
        float sx2 = sx * rls;
        float sy2 = sy * rls;
        float sz2 = sz * rls;
        float ux = (sy2 * fz2) - (sz2 * fy2);
        float uy = (sz2 * fx2) - (sx2 * fz2);
        float uz = (sx2 * fy2) - (sy2 * fx2);
        rm[rmOffset + 0] = sx2;
        rm[rmOffset + 1] = ux;
        rm[rmOffset + 2] = -fx2;
        rm[rmOffset + 3] = 0.0f;
        rm[rmOffset + 4] = sy2;
        rm[rmOffset + 5] = uy;
        rm[rmOffset + 6] = -fy2;
        rm[rmOffset + 7] = 0.0f;
        rm[rmOffset + 8] = sz2;
        rm[rmOffset + 9] = uz;
        rm[rmOffset + 10] = -fz2;
        rm[rmOffset + 11] = 0.0f;
        rm[rmOffset + 12] = 0.0f;
        rm[rmOffset + 13] = 0.0f;
        rm[rmOffset + 14] = 0.0f;
        rm[rmOffset + 15] = 1.0f;
        translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
    }
}
