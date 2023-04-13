package android.ddm;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.util.Log;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowManagerGlobal;
import com.android.internal.util.Preconditions;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleViewDebug extends DdmHandle {
    private static final int ERR_EXCEPTION = -3;
    private static final int ERR_INVALID_OP = -1;
    private static final int ERR_INVALID_PARAM = -2;
    private static final char SIG_ARRAY = '[';
    private static final char SIG_BOOLEAN = 'Z';
    private static final char SIG_BYTE = 'B';
    private static final char SIG_CHAR = 'C';
    private static final char SIG_DOUBLE = 'D';
    private static final char SIG_FLOAT = 'F';
    private static final char SIG_INT = 'I';
    private static final char SIG_LONG = 'J';
    private static final char SIG_SHORT = 'S';
    private static final char SIG_STRING = 'R';
    private static final char SIG_VOID = 'V';
    private static final String TAG = "DdmViewDebug";
    private static final int VUOP_CAPTURE_VIEW = 1;
    private static final int VUOP_DUMP_DISPLAYLIST = 2;
    private static final int VUOP_INVOKE_VIEW_METHOD = 4;
    private static final int VUOP_PROFILE_VIEW = 3;
    private static final int VUOP_SET_LAYOUT_PARAMETER = 5;
    private static final int VURT_CAPTURE_LAYERS = 2;
    private static final int VURT_DUMP_HIERARCHY = 1;
    private static final int VURT_DUMP_THEME = 3;
    private static final int CHUNK_VULW = ChunkHandler.type("VULW");
    private static final int CHUNK_VURT = ChunkHandler.type("VURT");
    private static final int CHUNK_VUOP = ChunkHandler.type("VUOP");
    private static final DdmHandleViewDebug sInstance = new DdmHandleViewDebug();

    private DdmHandleViewDebug() {
    }

    public static void register() {
        int i = CHUNK_VULW;
        DdmHandleViewDebug ddmHandleViewDebug = sInstance;
        DdmServer.registerHandler(i, ddmHandleViewDebug);
        DdmServer.registerHandler(CHUNK_VURT, ddmHandleViewDebug);
        DdmServer.registerHandler(CHUNK_VUOP, ddmHandleViewDebug);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_VULW) {
            return listWindows();
        }
        ByteBuffer in = wrapChunk(request);
        int op = in.getInt();
        View rootView = getRootView(in);
        if (rootView == null) {
            return createFailChunk(-2, "Invalid View Root");
        }
        if (type == CHUNK_VURT) {
            if (op == 1) {
                return dumpHierarchy(rootView, in);
            }
            if (op == 2) {
                return captureLayers(rootView);
            }
            if (op == 3) {
                return dumpTheme(rootView);
            }
            return createFailChunk(-1, "Unknown view root operation: " + op);
        }
        View targetView = getTargetView(rootView, in);
        if (targetView == null) {
            return createFailChunk(-2, "Invalid target view");
        }
        if (type == CHUNK_VUOP) {
            switch (op) {
                case 1:
                    return captureView(rootView, targetView);
                case 2:
                    return dumpDisplayLists(rootView, targetView);
                case 3:
                    return profileView(rootView, targetView);
                case 4:
                    return invokeViewMethod(rootView, targetView, in);
                case 5:
                    return setLayoutParameter(rootView, targetView, in);
                default:
                    return createFailChunk(-1, "Unknown view operation: " + op);
            }
        }
        throw new RuntimeException("Unknown packet " + name(type));
    }

    private Chunk listWindows() {
        String[] windowNames = WindowManagerGlobal.getInstance().getViewRootNames();
        int responseLength = 4;
        for (String name : windowNames) {
            responseLength = responseLength + 4 + (name.length() * 2);
        }
        ByteBuffer out = ByteBuffer.allocate(responseLength);
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(windowNames.length);
        for (String name2 : windowNames) {
            out.putInt(name2.length());
            putString(out, name2);
        }
        return new Chunk(CHUNK_VULW, out);
    }

    private View getRootView(ByteBuffer in) {
        try {
            int viewRootNameLength = in.getInt();
            String viewRootName = getString(in, viewRootNameLength);
            return WindowManagerGlobal.getInstance().getRootView(viewRootName);
        } catch (BufferUnderflowException e) {
            return null;
        }
    }

    private View getTargetView(View root, ByteBuffer in) {
        try {
            int viewLength = in.getInt();
            String viewName = getString(in, viewLength);
            return ViewDebug.findView(root, viewName);
        } catch (BufferUnderflowException e) {
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v0 */
    /* JADX WARN: Type inference failed for: r1v7, types: [byte[]] */
    private Chunk dumpHierarchy(View rootView, ByteBuffer in) {
        int i = 1;
        boolean skipChildren = in.getInt() > 0;
        boolean includeProperties = in.getInt() > 0;
        boolean v2 = in.hasRemaining() && in.getInt() > 0;
        long start = System.currentTimeMillis();
        ByteArrayOutputStream b = new ByteArrayOutputStream(2097152);
        try {
            if (v2) {
                ViewDebug.dumpv2(rootView, b);
            } else {
                ViewDebug.dump(rootView, skipChildren, includeProperties, b);
            }
            long end = System.currentTimeMillis();
            Log.m112d(TAG, "Time to obtain view hierarchy (ms): " + (end - start));
            i = b.toByteArray();
            return new Chunk(CHUNK_VURT, (byte[]) i, 0, i.length);
        } catch (IOException | InterruptedException e) {
            return createFailChunk(i, "Unexpected error while obtaining view hierarchy: " + e.getMessage());
        }
    }

    private Chunk captureLayers(View rootView) {
        ByteArrayOutputStream b = new ByteArrayOutputStream(1024);
        DataOutputStream dos = new DataOutputStream(b);
        try {
            try {
                ViewDebug.captureLayers(rootView, dos);
                try {
                    dos.close();
                } catch (IOException e) {
                }
                byte[] data = b.toByteArray();
                return new Chunk(CHUNK_VURT, data, 0, data.length);
            } catch (IOException e2) {
                Chunk createFailChunk = createFailChunk(1, "Unexpected error while obtaining view hierarchy: " + e2.getMessage());
                try {
                    dos.close();
                } catch (IOException e3) {
                }
                return createFailChunk;
            }
        } catch (Throwable th) {
            try {
                dos.close();
            } catch (IOException e4) {
            }
            throw th;
        }
    }

    private Chunk dumpTheme(View rootView) {
        ByteArrayOutputStream b = new ByteArrayOutputStream(1024);
        try {
            ViewDebug.dumpTheme(rootView, b);
            byte[] data = b.toByteArray();
            return new Chunk(CHUNK_VURT, data, 0, data.length);
        } catch (IOException e) {
            return createFailChunk(1, "Unexpected error while dumping the theme: " + e.getMessage());
        }
    }

    private Chunk captureView(View rootView, View targetView) {
        ByteArrayOutputStream b = new ByteArrayOutputStream(1024);
        try {
            ViewDebug.capture(rootView, b, targetView);
            byte[] data = b.toByteArray();
            return new Chunk(CHUNK_VUOP, data, 0, data.length);
        } catch (IOException e) {
            return createFailChunk(1, "Unexpected error while capturing view: " + e.getMessage());
        }
    }

    private Chunk dumpDisplayLists(final View rootView, final View targetView) {
        rootView.post(new Runnable() { // from class: android.ddm.DdmHandleViewDebug.1
            @Override // java.lang.Runnable
            public void run() {
                ViewDebug.outputDisplayList(rootView, targetView);
            }
        });
        return null;
    }

    private Chunk invokeViewMethod(View rootView, View targetView, ByteBuffer in) {
        Class<?>[] argTypes;
        Object[] args;
        int l = in.getInt();
        String methodName = getString(in, l);
        if (!in.hasRemaining()) {
            args = new Object[0];
            argTypes = new Class[0];
        } else {
            int nArgs = in.getInt();
            argTypes = new Class[nArgs];
            Object[] args2 = new Object[nArgs];
            try {
                deserializeMethodParameters(args2, argTypes, in);
                args = args2;
            } catch (ViewMethodInvocationSerializationException e) {
                return createFailChunk(-2, e.getMessage());
            }
        }
        try {
            Method method = targetView.getClass().getMethod(methodName, argTypes);
            try {
                Object result = ViewDebug.invokeViewMethod(targetView, method, args);
                Class<?> returnType = method.getReturnType();
                byte[] returnValue = serializeReturnValue(returnType, returnType.cast(result));
                return new Chunk(CHUNK_VUOP, returnValue, 0, returnValue.length);
            } catch (Exception e2) {
                Log.m110e(TAG, "Exception while invoking method: " + e2.getCause().getMessage());
                String msg = e2.getCause().getMessage();
                if (msg == null) {
                    msg = e2.getCause().toString();
                }
                return createFailChunk(-3, msg);
            }
        } catch (NoSuchMethodException e3) {
            Log.m110e(TAG, "No such method: " + e3.getMessage());
            return createFailChunk(-2, "No such method: " + e3.getMessage());
        }
    }

    private Chunk setLayoutParameter(View rootView, View targetView, ByteBuffer in) {
        int l = in.getInt();
        String param = getString(in, l);
        int value = in.getInt();
        try {
            ViewDebug.setLayoutParameter(targetView, param, value);
            return null;
        } catch (Exception e) {
            Log.m110e(TAG, "Exception setting layout parameter: " + e);
            return createFailChunk(-3, "Error accessing field " + param + ":" + e.getMessage());
        }
    }

    private Chunk profileView(View rootView, View targetView) {
        ByteArrayOutputStream b = new ByteArrayOutputStream(32768);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(b), 32768);
        try {
            try {
                ViewDebug.profileViewAndChildren(targetView, bw);
                try {
                    bw.close();
                } catch (IOException e) {
                }
                byte[] data = b.toByteArray();
                return new Chunk(CHUNK_VUOP, data, 0, data.length);
            } catch (Throwable th) {
                try {
                    bw.close();
                } catch (IOException e2) {
                }
                throw th;
            }
        } catch (IOException e3) {
            Chunk createFailChunk = createFailChunk(1, "Unexpected error while profiling view: " + e3.getMessage());
            try {
                bw.close();
            } catch (IOException e4) {
            }
            return createFailChunk;
        }
    }

    public static void deserializeMethodParameters(Object[] args, Class<?>[] argTypes, ByteBuffer in) throws ViewMethodInvocationSerializationException {
        Preconditions.checkArgument(args.length == argTypes.length);
        for (int i = 0; i < args.length; i++) {
            char typeSignature = in.getChar();
            boolean isArray = typeSignature == '[';
            if (isArray) {
                char arrayType = in.getChar();
                if (arrayType != 'B') {
                    throw new ViewMethodInvocationSerializationException("Unsupported array parameter type (" + typeSignature + ") to invoke view method @argument " + i);
                }
                int arrayLength = in.getInt();
                if (arrayLength > in.remaining()) {
                    throw new BufferUnderflowException();
                }
                byte[] byteArray = new byte[arrayLength];
                in.get(byteArray);
                argTypes[i] = byte[].class;
                args[i] = byteArray;
            } else {
                switch (typeSignature) {
                    case 'B':
                        argTypes[i] = Byte.TYPE;
                        args[i] = Byte.valueOf(in.get());
                        continue;
                    case 'C':
                        argTypes[i] = Character.TYPE;
                        args[i] = Character.valueOf(in.getChar());
                        continue;
                    case 'D':
                        argTypes[i] = Double.TYPE;
                        args[i] = Double.valueOf(in.getDouble());
                        continue;
                    case 'F':
                        argTypes[i] = Float.TYPE;
                        args[i] = Float.valueOf(in.getFloat());
                        continue;
                    case 'I':
                        argTypes[i] = Integer.TYPE;
                        args[i] = Integer.valueOf(in.getInt());
                        continue;
                    case 'J':
                        argTypes[i] = Long.TYPE;
                        args[i] = Long.valueOf(in.getLong());
                        continue;
                    case 'R':
                        argTypes[i] = String.class;
                        int stringUtf8ByteCount = Short.toUnsignedInt(in.getShort());
                        byte[] rawStringBuffer = new byte[stringUtf8ByteCount];
                        in.get(rawStringBuffer);
                        args[i] = new String(rawStringBuffer, StandardCharsets.UTF_8);
                        continue;
                    case 'S':
                        argTypes[i] = Short.TYPE;
                        args[i] = Short.valueOf(in.getShort());
                        continue;
                    case 'Z':
                        argTypes[i] = Boolean.TYPE;
                        args[i] = Boolean.valueOf(in.get() != 0);
                        continue;
                    default:
                        Log.m110e(TAG, "arg " + i + ", unrecognized type: " + typeSignature);
                        throw new ViewMethodInvocationSerializationException("Unsupported parameter type (" + typeSignature + ") to invoke view method.");
                }
            }
        }
    }

    public static byte[] serializeReturnValue(Class<?> type, Object value) throws ViewMethodInvocationSerializationException, IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream(1024);
        DataOutputStream dos = new DataOutputStream(byteOutStream);
        if (type.isArray()) {
            if (!type.equals(byte[].class)) {
                throw new ViewMethodInvocationSerializationException("Unsupported array return type (" + type + NavigationBarInflaterView.KEY_CODE_END);
            }
            byte[] byteArray = (byte[]) value;
            dos.writeChar(91);
            dos.writeChar(66);
            dos.writeInt(byteArray.length);
            dos.write(byteArray);
        } else if (Boolean.TYPE.equals(type)) {
            dos.writeChar(90);
            dos.write(((Boolean) value).booleanValue() ? 1 : 0);
        } else if (Byte.TYPE.equals(type)) {
            dos.writeChar(66);
            dos.writeByte(((Byte) value).byteValue());
        } else if (Character.TYPE.equals(type)) {
            dos.writeChar(67);
            dos.writeChar(((Character) value).charValue());
        } else if (Short.TYPE.equals(type)) {
            dos.writeChar(83);
            dos.writeShort(((Short) value).shortValue());
        } else if (Integer.TYPE.equals(type)) {
            dos.writeChar(73);
            dos.writeInt(((Integer) value).intValue());
        } else if (Long.TYPE.equals(type)) {
            dos.writeChar(74);
            dos.writeLong(((Long) value).longValue());
        } else if (Double.TYPE.equals(type)) {
            dos.writeChar(68);
            dos.writeDouble(((Double) value).doubleValue());
        } else if (Float.TYPE.equals(type)) {
            dos.writeChar(70);
            dos.writeFloat(((Float) value).floatValue());
        } else if (String.class.equals(type)) {
            dos.writeChar(82);
            dos.writeUTF(value != null ? (String) value : "");
        } else {
            dos.writeChar(86);
        }
        return byteOutStream.toByteArray();
    }

    /* loaded from: classes.dex */
    public static class ViewMethodInvocationSerializationException extends Exception {
        ViewMethodInvocationSerializationException(String message) {
            super(message);
        }
    }
}
