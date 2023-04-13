package com.google.android.gles_jni;

import android.app.AppGlobals;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.IPackageManager;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.Log;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;
/* loaded from: classes5.dex */
public class GLImpl implements GL10, GL10Ext, GL11, GL11Ext, GL11ExtensionPack {
    private boolean haveCheckedExtensions;
    private boolean have_OES_blend_equation_separate;
    private boolean have_OES_blend_subtract;
    private boolean have_OES_framebuffer_object;
    private boolean have_OES_texture_cube_map;
    Buffer _colorPointer = null;
    Buffer _normalPointer = null;
    Buffer _texCoordPointer = null;
    Buffer _vertexPointer = null;
    Buffer _pointSizePointerOES = null;
    Buffer _matrixIndexPointerOES = null;
    Buffer _weightPointerOES = null;

    private static native void _nativeClassInit();

    private native void glColorPointerBounds(int i, int i2, int i3, Buffer buffer, int i4);

    private native void glMatrixIndexPointerOESBounds(int i, int i2, int i3, Buffer buffer, int i4);

    private native void glNormalPointerBounds(int i, int i2, Buffer buffer, int i3);

    private native void glPointSizePointerOESBounds(int i, int i2, Buffer buffer, int i3);

    private native void glTexCoordPointerBounds(int i, int i2, int i3, Buffer buffer, int i4);

    private native void glVertexPointerBounds(int i, int i2, int i3, Buffer buffer, int i4);

    private native void glWeightPointerOESBounds(int i, int i2, int i3, Buffer buffer, int i4);

    public native String _glGetString(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glActiveTexture(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glAlphaFunc(int i, float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glAlphaFuncx(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glBindBuffer(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBindFramebufferOES(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBindRenderbufferOES(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBindTexture(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBlendEquation(int i);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBlendEquationSeparate(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glBlendFunc(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glBlendFuncSeparate(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glBufferData(int i, int i2, Buffer buffer, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glBufferSubData(int i, int i2, int i3, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native int glCheckFramebufferStatusOES(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClear(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClearColor(float f, float f2, float f3, float f4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClearColorx(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClearDepthf(float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClearDepthx(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClearStencil(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glClientActiveTexture(int i);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glClipPlanef(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glClipPlanef(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glClipPlanex(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glClipPlanex(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glColor4f(float f, float f2, float f3, float f4);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glColor4ub(byte b, byte b2, byte b3, byte b4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glColor4x(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glColorMask(boolean z, boolean z2, boolean z3, boolean z4);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glColorPointer(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glCompressedTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glCompressedTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glCopyTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glCopyTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glCullFace(int i);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glCurrentPaletteMatrixOES(int i);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glDeleteBuffers(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glDeleteBuffers(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glDeleteFramebuffersOES(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glDeleteFramebuffersOES(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glDeleteRenderbuffersOES(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glDeleteRenderbuffersOES(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDeleteTextures(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDeleteTextures(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDepthFunc(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDepthMask(boolean z);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDepthRangef(float f, float f2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDepthRangex(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDisable(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDisableClientState(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDrawArrays(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glDrawElements(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glDrawElements(int i, int i2, int i3, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexfOES(float f, float f2, float f3, float f4, float f5);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexfvOES(FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexfvOES(float[] fArr, int i);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexiOES(int i, int i2, int i3, int i4, int i5);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexivOES(IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexivOES(int[] iArr, int i);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexsOES(short s, short s2, short s3, short s4, short s5);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexsvOES(ShortBuffer shortBuffer);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexsvOES(short[] sArr, int i);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexxOES(int i, int i2, int i3, int i4, int i5);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexxvOES(IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glDrawTexxvOES(int[] iArr, int i);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11Ext, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glEnable(int i);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11Ext
    public native void glEnableClientState(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFinish();

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFlush();

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogf(int i, float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogfv(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogfv(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogx(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogxv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFogxv(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glFramebufferRenderbufferOES(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glFramebufferTexture2DOES(int i, int i2, int i3, int i4, int i5);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFrontFace(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFrustumf(float f, float f2, float f3, float f4, float f5, float f6);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glFrustumx(int i, int i2, int i3, int i4, int i5, int i6);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGenBuffers(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGenBuffers(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGenFramebuffersOES(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGenFramebuffersOES(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGenRenderbuffersOES(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGenRenderbuffersOES(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glGenTextures(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glGenTextures(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGenerateMipmapOES(int i);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetBooleanv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetBooleanv(int i, boolean[] zArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetBufferParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetBufferParameteriv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetClipPlanef(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetClipPlanef(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetClipPlanex(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetClipPlanex(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native int glGetError();

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetFixedv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetFixedv(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetFloatv(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetFloatv(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetFramebufferAttachmentParameterivOES(int i, int i2, int i3, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetFramebufferAttachmentParameterivOES(int i, int i2, int i3, int[] iArr, int i4);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetIntegerv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetIntegerv(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetLightfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetLightfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetLightxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetLightxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetMaterialfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetMaterialfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetMaterialxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetMaterialxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetRenderbufferParameterivOES(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetRenderbufferParameterivOES(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexEnviv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexEnviv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexEnvxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexEnvxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGenfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGenfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGeniv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGeniv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGenxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glGetTexGenxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameterfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameterfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameteriv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameterxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glGetTexParameterxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glHint(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native boolean glIsBuffer(int i);

    @Override // javax.microedition.khronos.opengles.GL11
    public native boolean glIsEnabled(int i);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native boolean glIsFramebufferOES(int i);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native boolean glIsRenderbufferOES(int i);

    @Override // javax.microedition.khronos.opengles.GL11
    public native boolean glIsTexture(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelf(int i, float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelfv(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelfv(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelx(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelxv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightModelxv(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightf(int i, int i2, float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightx(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLightxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLineWidth(float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLineWidthx(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLoadIdentity();

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLoadMatrixf(FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLoadMatrixf(float[] fArr, int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLoadMatrixx(IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLoadMatrixx(int[] iArr, int i);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glLoadPaletteFromModelViewMatrixOES();

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glLogicOp(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialf(int i, int i2, float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialx(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMaterialxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glMatrixIndexPointerOES(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMatrixMode(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultMatrixf(FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultMatrixf(float[] fArr, int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultMatrixx(IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultMatrixx(int[] iArr, int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultiTexCoord4f(int i, float f, float f2, float f3, float f4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glMultiTexCoord4x(int i, int i2, int i3, int i4, int i5);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glNormal3f(float f, float f2, float f3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glNormal3x(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glNormalPointer(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glOrthof(float f, float f2, float f3, float f4, float f5, float f6);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glOrthox(int i, int i2, int i3, int i4, int i5, int i6);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPixelStorei(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterf(int i, float f);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterfv(int i, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterfv(int i, float[] fArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterx(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterxv(int i, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glPointParameterxv(int i, int[] iArr, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPointSize(float f);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPointSizex(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPolygonOffset(float f, float f2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPolygonOffsetx(int i, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPopMatrix();

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glPushMatrix();

    @Override // javax.microedition.khronos.opengles.GL10Ext
    public native int glQueryMatrixxOES(IntBuffer intBuffer, IntBuffer intBuffer2);

    @Override // javax.microedition.khronos.opengles.GL10Ext
    public native int glQueryMatrixxOES(int[] iArr, int i, int[] iArr2, int i2);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glReadPixels(int i, int i2, int i3, int i4, int i5, int i6, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glRenderbufferStorageOES(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glRotatef(float f, float f2, float f3, float f4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glRotatex(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glSampleCoverage(float f, boolean z);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glSampleCoveragex(int i, boolean z);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glScalef(float f, float f2, float f3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glScalex(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glScissor(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glShadeModel(int i);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glStencilFunc(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glStencilMask(int i);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glStencilOp(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexCoordPointer(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvf(int i, int i2, float f);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexEnvi(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexEnviv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexEnviv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvx(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexEnvxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenf(int i, int i2, float f);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGeni(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGeniv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGeniv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenx(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexGenxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glTexImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.opengles.GL11ExtensionPack
    public native void glTexParameterf(int i, int i2, float f);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameterfv(int i, int i2, FloatBuffer floatBuffer);

    @Override // javax.microedition.khronos.opengles.GL11, javax.microedition.khronos.opengles.GL11Ext
    public native void glTexParameterfv(int i, int i2, float[] fArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameteri(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameteriv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameteriv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glTexParameterx(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameterxv(int i, int i2, IntBuffer intBuffer);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glTexParameterxv(int i, int i2, int[] iArr, int i3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glTexSubImage2D(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, Buffer buffer);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glTranslatef(float f, float f2, float f3);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glTranslatex(int i, int i2, int i3);

    @Override // javax.microedition.khronos.opengles.GL11
    public native void glVertexPointer(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL10
    public native void glViewport(int i, int i2, int i3, int i4);

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public native void glWeightPointerOES(int i, int i2, int i3, int i4);

    static {
        _nativeClassInit();
    }

    @Override // javax.microedition.khronos.opengles.GL11
    public void glGetPointerv(int pname, Buffer[] params) {
        throw new UnsupportedOperationException("glGetPointerv");
    }

    private static boolean allowIndirectBuffers(String appName) {
        int version = 0;
        IPackageManager pm = AppGlobals.getPackageManager();
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(appName, 0L, UserHandle.myUserId());
            if (applicationInfo != null) {
                version = applicationInfo.targetSdkVersion;
            }
        } catch (RemoteException e) {
        }
        Log.m110e("OpenGLES", String.format("Application %s (SDK target %d) called a GL11 Pointer method with an indirect Buffer.", appName, Integer.valueOf(version)));
        if (version > 3) {
            return false;
        }
        return true;
    }

    @Override // javax.microedition.khronos.opengles.GL10
    public void glColorPointer(int size, int type, int stride, Buffer pointer) {
        glColorPointerBounds(size, type, stride, pointer, pointer.remaining());
        if (size == 4) {
            if ((type == 5126 || type == 5121 || type == 5132) && stride >= 0) {
                this._colorPointer = pointer;
            }
        }
    }

    @Override // javax.microedition.khronos.opengles.GL10
    public String glGetString(int name) {
        String returnValue = _glGetString(name);
        return returnValue;
    }

    @Override // javax.microedition.khronos.opengles.GL10
    public void glNormalPointer(int type, int stride, Buffer pointer) {
        glNormalPointerBounds(type, stride, pointer, pointer.remaining());
        if ((type == 5126 || type == 5120 || type == 5122 || type == 5132) && stride >= 0) {
            this._normalPointer = pointer;
        }
    }

    @Override // javax.microedition.khronos.opengles.GL10
    public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
        glTexCoordPointerBounds(size, type, stride, pointer, pointer.remaining());
        if (size == 2 || size == 3 || size == 4) {
            if ((type == 5126 || type == 5120 || type == 5122 || type == 5132) && stride >= 0) {
                this._texCoordPointer = pointer;
            }
        }
    }

    @Override // javax.microedition.khronos.opengles.GL10
    public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
        glVertexPointerBounds(size, type, stride, pointer, pointer.remaining());
        if (size == 2 || size == 3 || size == 4) {
            if ((type == 5126 || type == 5120 || type == 5122 || type == 5132) && stride >= 0) {
                this._vertexPointer = pointer;
            }
        }
    }

    @Override // javax.microedition.khronos.opengles.GL11
    public void glPointSizePointerOES(int type, int stride, Buffer pointer) {
        glPointSizePointerOESBounds(type, stride, pointer, pointer.remaining());
        if ((type == 5126 || type == 5132) && stride >= 0) {
            this._pointSizePointerOES = pointer;
        }
    }

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public void glMatrixIndexPointerOES(int size, int type, int stride, Buffer pointer) {
        glMatrixIndexPointerOESBounds(size, type, stride, pointer, pointer.remaining());
        if (size == 2 || size == 3 || size == 4) {
            if ((type == 5126 || type == 5120 || type == 5122 || type == 5132) && stride >= 0) {
                this._matrixIndexPointerOES = pointer;
            }
        }
    }

    @Override // javax.microedition.khronos.opengles.GL11Ext
    public void glWeightPointerOES(int size, int type, int stride, Buffer pointer) {
        glWeightPointerOESBounds(size, type, stride, pointer, pointer.remaining());
    }
}
