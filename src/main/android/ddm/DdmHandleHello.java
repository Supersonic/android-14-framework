package android.ddm;

import android.ddm.DdmHandleAppName;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Debug;
import android.p008os.Process;
import android.p008os.UserHandle;
import dalvik.system.VMRuntime;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleHello extends DdmHandle {
    private static final int CLIENT_PROTOCOL_VERSION = 1;
    public static final int CHUNK_HELO = ChunkHandler.type("HELO");
    public static final int CHUNK_WAIT = ChunkHandler.type("WAIT");
    public static final int CHUNK_FEAT = ChunkHandler.type("FEAT");
    private static DdmHandleHello mInstance = new DdmHandleHello();
    private static final String[] FRAMEWORK_FEATURES = {"opengl-tracing", "view-hierarchy"};

    private DdmHandleHello() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_HELO, mInstance);
        DdmServer.registerHandler(CHUNK_FEAT, mInstance);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_HELO) {
            return handleHELO(request);
        }
        if (type == CHUNK_FEAT) {
            return handleFEAT(request);
        }
        throw new RuntimeException("Unknown packet " + name(type));
    }

    private Chunk handleHELO(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        in.getInt();
        String vmName = System.getProperty("java.vm.name", "?");
        String vmVersion = System.getProperty("java.vm.version", "?");
        String vmIdent = vmName + " v" + vmVersion;
        DdmHandleAppName.Names names = DdmHandleAppName.getNames();
        String appName = names.getAppName();
        String pkgName = names.getPkgName();
        VMRuntime vmRuntime = VMRuntime.getRuntime();
        String instructionSetDescription = vmRuntime.is64Bit() ? "64-bit" : "32-bit";
        String vmInstructionSet = vmRuntime.vmInstructionSet();
        if (vmInstructionSet != null && vmInstructionSet.length() > 0) {
            instructionSetDescription = instructionSetDescription + " (" + vmInstructionSet + NavigationBarInflaterView.KEY_CODE_END;
        }
        String vmFlags = "CheckJNI=" + (vmRuntime.isCheckJniEnabled() ? "true" : "false");
        boolean isNativeDebuggable = vmRuntime.isNativeDebuggable();
        ByteBuffer out = ByteBuffer.allocate((vmIdent.length() * 2) + 32 + (appName.length() * 2) + (instructionSetDescription.length() * 2) + (vmFlags.length() * 2) + 1 + (pkgName.length() * 2));
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(1);
        out.putInt(Process.myPid());
        out.putInt(vmIdent.length());
        out.putInt(appName.length());
        putString(out, vmIdent);
        putString(out, appName);
        out.putInt(UserHandle.myUserId());
        out.putInt(instructionSetDescription.length());
        putString(out, instructionSetDescription);
        out.putInt(vmFlags.length());
        putString(out, vmFlags);
        out.put(isNativeDebuggable ? (byte) 1 : (byte) 0);
        out.putInt(pkgName.length());
        putString(out, pkgName);
        Chunk reply = new Chunk(CHUNK_HELO, out);
        if (Debug.waitingForDebugger()) {
            sendWAIT(0);
        }
        return reply;
    }

    private Chunk handleFEAT(Chunk request) {
        String[] vmFeatures = Debug.getVmFeatureList();
        int size = ((vmFeatures.length + FRAMEWORK_FEATURES.length) * 4) + 4;
        for (int i = vmFeatures.length - 1; i >= 0; i--) {
            size += vmFeatures[i].length() * 2;
        }
        for (int i2 = FRAMEWORK_FEATURES.length - 1; i2 >= 0; i2--) {
            size += FRAMEWORK_FEATURES[i2].length() * 2;
        }
        ByteBuffer out = ByteBuffer.allocate(size);
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(vmFeatures.length + FRAMEWORK_FEATURES.length);
        for (int i3 = vmFeatures.length - 1; i3 >= 0; i3--) {
            out.putInt(vmFeatures[i3].length());
            putString(out, vmFeatures[i3]);
        }
        for (int i4 = FRAMEWORK_FEATURES.length - 1; i4 >= 0; i4--) {
            String[] strArr = FRAMEWORK_FEATURES;
            out.putInt(strArr[i4].length());
            putString(out, strArr[i4]);
        }
        return new Chunk(CHUNK_FEAT, out);
    }

    public static void sendWAIT(int reason) {
        byte[] data = {(byte) reason};
        Chunk waitChunk = new Chunk(CHUNK_WAIT, data, 0, 1);
        DdmServer.sendChunk(waitChunk);
    }
}
