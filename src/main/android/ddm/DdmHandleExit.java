package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleExit extends DdmHandle {
    public static final int CHUNK_EXIT = ChunkHandler.type("EXIT");
    private static DdmHandleExit mInstance = new DdmHandleExit();

    private DdmHandleExit() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_EXIT, mInstance);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int statusCode = in.getInt();
        Runtime.getRuntime().halt(statusCode);
        return null;
    }
}
