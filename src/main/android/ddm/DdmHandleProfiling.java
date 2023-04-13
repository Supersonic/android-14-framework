package android.ddm;

import android.p008os.Debug;
import android.util.Log;
import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleProfiling extends DdmHandle {
    private static final boolean DEBUG = false;
    public static final int CHUNK_MPRS = ChunkHandler.type("MPRS");
    public static final int CHUNK_MPRE = ChunkHandler.type("MPRE");
    public static final int CHUNK_MPSS = ChunkHandler.type("MPSS");
    public static final int CHUNK_MPSE = ChunkHandler.type("MPSE");
    public static final int CHUNK_MPRQ = ChunkHandler.type("MPRQ");
    public static final int CHUNK_SPSS = ChunkHandler.type("SPSS");
    public static final int CHUNK_SPSE = ChunkHandler.type("SPSE");
    private static DdmHandleProfiling mInstance = new DdmHandleProfiling();

    private DdmHandleProfiling() {
    }

    public static void register() {
        DdmServer.registerHandler(CHUNK_MPRS, mInstance);
        DdmServer.registerHandler(CHUNK_MPRE, mInstance);
        DdmServer.registerHandler(CHUNK_MPSS, mInstance);
        DdmServer.registerHandler(CHUNK_MPSE, mInstance);
        DdmServer.registerHandler(CHUNK_MPRQ, mInstance);
        DdmServer.registerHandler(CHUNK_SPSS, mInstance);
        DdmServer.registerHandler(CHUNK_SPSE, mInstance);
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        int type = request.type;
        if (type == CHUNK_MPRS) {
            return handleMPRS(request);
        }
        if (type == CHUNK_MPRE) {
            return handleMPRE(request);
        }
        if (type == CHUNK_MPSS) {
            return handleMPSS(request);
        }
        if (type == CHUNK_MPSE) {
            return handleMPSEOrSPSE(request, "Method");
        }
        if (type == CHUNK_MPRQ) {
            return handleMPRQ(request);
        }
        if (type == CHUNK_SPSS) {
            return handleSPSS(request);
        }
        if (type == CHUNK_SPSE) {
            return handleMPSEOrSPSE(request, "Sample");
        }
        throw new RuntimeException("Unknown packet " + name(type));
    }

    private Chunk handleMPRS(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int bufferSize = in.getInt();
        int flags = in.getInt();
        int len = in.getInt();
        String fileName = getString(in, len);
        try {
            Debug.startMethodTracing(fileName, bufferSize, flags);
            return null;
        } catch (RuntimeException re) {
            return createFailChunk(1, re.getMessage());
        }
    }

    private Chunk handleMPRE(Chunk request) {
        byte result;
        try {
            Debug.stopMethodTracing();
            result = 0;
        } catch (RuntimeException re) {
            Log.m104w("ddm-heap", "Method profiling end failed: " + re.getMessage());
            result = 1;
        }
        byte[] reply = {result};
        return new Chunk(CHUNK_MPRE, reply, 0, reply.length);
    }

    private Chunk handleMPSS(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int bufferSize = in.getInt();
        int flags = in.getInt();
        try {
            Debug.startMethodTracingDdms(bufferSize, flags, false, 0);
            return null;
        } catch (RuntimeException re) {
            return createFailChunk(1, re.getMessage());
        }
    }

    private Chunk handleMPSEOrSPSE(Chunk request, String type) {
        try {
            Debug.stopMethodTracing();
            return null;
        } catch (RuntimeException re) {
            Log.m104w("ddm-heap", type + " prof stream end failed: " + re.getMessage());
            return createFailChunk(1, re.getMessage());
        }
    }

    private Chunk handleMPRQ(Chunk request) {
        int result = Debug.getMethodTracingMode();
        byte[] reply = {(byte) result};
        return new Chunk(CHUNK_MPRQ, reply, 0, reply.length);
    }

    private Chunk handleSPSS(Chunk request) {
        ByteBuffer in = wrapChunk(request);
        int bufferSize = in.getInt();
        int flags = in.getInt();
        int interval = in.getInt();
        try {
            Debug.startMethodTracingDdms(bufferSize, flags, true, interval);
            return null;
        } catch (RuntimeException re) {
            return createFailChunk(1, re.getMessage());
        }
    }
}
