package android.opengl;

import java.io.Writer;
import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.opengles.InterfaceC4503GL;
/* loaded from: classes2.dex */
public class GLDebugHelper {
    public static final int CONFIG_CHECK_GL_ERROR = 1;
    public static final int CONFIG_CHECK_THREAD = 2;
    public static final int CONFIG_LOG_ARGUMENT_NAMES = 4;
    public static final int ERROR_WRONG_THREAD = 28672;

    public static InterfaceC4503GL wrap(InterfaceC4503GL gl, int configFlags, Writer log) {
        if (configFlags != 0) {
            gl = new GLErrorWrapper(gl, configFlags);
        }
        if (log != null) {
            boolean logArgumentNames = (configFlags & 4) != 0;
            return new GLLogWrapper(gl, log, logArgumentNames);
        }
        return gl;
    }

    public static EGL wrap(EGL egl, int configFlags, Writer log) {
        if (log != null) {
            return new EGLLogWrapper(egl, configFlags, log);
        }
        return egl;
    }
}
