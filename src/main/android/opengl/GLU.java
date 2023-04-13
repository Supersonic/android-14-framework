package android.opengl;

import javax.microedition.khronos.opengles.GL10;
/* loaded from: classes2.dex */
public class GLU {
    private static final float[] sScratch = new float[32];

    public static String gluErrorString(int error) {
        switch (error) {
            case 0:
                return "no error";
            case 1280:
                return "invalid enum";
            case 1281:
                return "invalid value";
            case 1282:
                return "invalid operation";
            case 1283:
                return "stack overflow";
            case 1284:
                return "stack underflow";
            case 1285:
                return "out of memory";
            default:
                return null;
        }
    }

    public static void gluLookAt(GL10 gl, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            try {
                try {
                    Matrix.setLookAtM(scratch, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
                    gl.glMultMatrixf(scratch, 0);
                } catch (Throwable th) {
                    th = th;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    public static void gluOrtho2D(GL10 gl, float left, float right, float bottom, float top) {
        gl.glOrthof(left, right, bottom, top, -1.0f, 1.0f);
    }

    public static void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar) {
        float top = ((float) Math.tan(fovy * 0.008726646259971648d)) * zNear;
        float bottom = -top;
        float left = bottom * aspect;
        float right = top * aspect;
        gl.glFrustumf(left, right, bottom, top, zNear, zFar);
    }

    public static int gluProject(float objX, float objY, float objZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] win, int winOffset) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            Matrix.multiplyMM(scratch, 0, project, projectOffset, model, modelOffset);
            scratch[16] = objX;
            scratch[17] = objY;
            scratch[18] = objZ;
            scratch[19] = 1.0f;
            Matrix.multiplyMV(scratch, 20, scratch, 0, scratch, 16);
            float w = scratch[23];
            if (w == 0.0f) {
                return 0;
            }
            float rw = 1.0f / w;
            win[winOffset] = view[viewOffset] + (view[viewOffset + 2] * ((scratch[20] * rw) + 1.0f) * 0.5f);
            win[winOffset + 1] = view[viewOffset + 1] + (view[viewOffset + 3] * ((scratch[21] * rw) + 1.0f) * 0.5f);
            win[winOffset + 2] = ((scratch[22] * rw) + 1.0f) * 0.5f;
            return 1;
        }
    }

    public static int gluUnProject(float winX, float winY, float winZ, float[] model, int modelOffset, float[] project, int projectOffset, int[] view, int viewOffset, float[] obj, int objOffset) {
        float[] scratch = sScratch;
        synchronized (scratch) {
            Matrix.multiplyMM(scratch, 0, project, projectOffset, model, modelOffset);
            if (Matrix.invertM(scratch, 16, scratch, 0)) {
                scratch[0] = (((winX - view[viewOffset + 0]) * 2.0f) / view[viewOffset + 2]) - 1.0f;
                scratch[1] = (((winY - view[viewOffset + 1]) * 2.0f) / view[viewOffset + 3]) - 1.0f;
                scratch[2] = (winZ * 2.0f) - 1.0f;
                scratch[3] = 1.0f;
                Matrix.multiplyMV(obj, objOffset, scratch, 16, scratch, 0);
                return 1;
            }
            return 0;
        }
    }
}
