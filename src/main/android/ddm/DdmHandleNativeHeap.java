package android.ddm;

import android.util.Log;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleNativeHeap extends DdmHandle {
    public static final int CHUNK_NHGT = ChunkHandler.type("NHGT");
    private static DdmHandleNativeHeap mInstance = new DdmHandleNativeHeap();

    private native byte[] getLeakInfo();

    private DdmHandleNativeHeap() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_NHGT, mInstance);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        Log.m108i("ddm-nativeheap", "Handling " + name(request.type) + " chunk");
        int type = request.type;
        if (type == CHUNK_NHGT) {
            return handleNHGT(request);
        }
        throw new RuntimeException("Unknown packet " + name(type));
    }

    private Chunk handleNHGT(Chunk request) {
        byte[] data = getLeakInfo();
        if (data != null) {
            Log.m108i("ddm-nativeheap", "Sending " + data.length + " bytes");
            return new Chunk(ChunkHandler.type("NHGT"), data, 0, data.length);
        }
        return createFailChunk(1, "Something went wrong");
    }
}
