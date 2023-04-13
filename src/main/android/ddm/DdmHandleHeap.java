package android.ddm;

import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleHeap extends DdmHandle {
    public static final int CHUNK_HPGC = ChunkHandler.type("HPGC");
    private static DdmHandleHeap mInstance = new DdmHandleHeap();

    private DdmHandleHeap() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_HPGC, mInstance);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_HPGC) {
            return handleHPGC(request);
        }
        throw new RuntimeException("Unknown packet " + name(type));
    }

    private Chunk handleHPGC(Chunk request) {
        Runtime.getRuntime().gc();
        return null;
    }
}
