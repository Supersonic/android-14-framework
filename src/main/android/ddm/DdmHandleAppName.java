package android.ddm;

import java.nio.ByteBuffer;
import org.apache.harmony.dalvik.ddmc.Chunk;
import org.apache.harmony.dalvik.ddmc.ChunkHandler;
import org.apache.harmony.dalvik.ddmc.DdmServer;
/* loaded from: classes.dex */
public class DdmHandleAppName extends DdmHandle {
    public static final int CHUNK_APNM = ChunkHandler.type("APNM");
    private static volatile Names sNames = new Names("", "");
    private static DdmHandleAppName mInstance = new DdmHandleAppName();

    private DdmHandleAppName() {
    }

    public static void register() {
    }

    public void onConnected() {
    }

    public void onDisconnected() {
    }

    public Chunk handleChunk(Chunk request) {
        return null;
    }

    public static void setAppName(String name, int userId) {
        setAppName(name, name, userId);
    }

    public static void setAppName(String appName, String pkgName, int userId) {
        if (appName == null || appName.isEmpty() || pkgName == null || pkgName.isEmpty()) {
            return;
        }
        sNames = new Names(appName, pkgName);
        sendAPNM(appName, pkgName, userId);
    }

    public static Names getNames() {
        return sNames;
    }

    private static void sendAPNM(String appName, String pkgName, int userId) {
        ByteBuffer out = ByteBuffer.allocate((appName.length() * 2) + 4 + 4 + 4 + (pkgName.length() * 2));
        out.order(ChunkHandler.CHUNK_ORDER);
        out.putInt(appName.length());
        putString(out, appName);
        out.putInt(userId);
        out.putInt(pkgName.length());
        putString(out, pkgName);
        Chunk chunk = new Chunk(CHUNK_APNM, out);
        DdmServer.sendChunk(chunk);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Names {
        private final String mAppName;
        private final String mPkgName;

        private Names(String appName, String pkgName) {
            this.mAppName = appName;
            this.mPkgName = pkgName;
        }

        public String getAppName() {
            return this.mAppName;
        }

        public String getPkgName() {
            return this.mPkgName;
        }
    }
}
