package com.android.internal.protolog;

import com.android.internal.protolog.BaseProtoLogImpl;
import com.android.internal.protolog.common.IProtoLogGroup;
import java.io.File;
/* loaded from: classes4.dex */
public class ProtoLogImpl extends BaseProtoLogImpl {
    private static final int BUFFER_CAPACITY = 1048576;
    private static final String LOG_FILENAME = "/data/misc/wmtrace/wm_log.winscope";
    private static final int PER_CHUNK_SIZE = 1024;
    private static final String VIEWER_CONFIG_FILENAME = "/system/etc/protolog.conf.json.gz";
    private static ProtoLogImpl sServiceInstance = null;

    static {
        addLogGroupEnum(ProtoLogGroup.values());
    }

    /* renamed from: d */
    public static void m33d(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.DEBUG, group, messageHash, paramsMask, messageString, args);
    }

    /* renamed from: v */
    public static void m30v(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.VERBOSE, group, messageHash, paramsMask, messageString, args);
    }

    /* renamed from: i */
    public static void m31i(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.INFO, group, messageHash, paramsMask, messageString, args);
    }

    /* renamed from: w */
    public static void m29w(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.WARN, group, messageHash, paramsMask, messageString, args);
    }

    /* renamed from: e */
    public static void m32e(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.ERROR, group, messageHash, paramsMask, messageString, args);
    }

    public static void wtf(IProtoLogGroup group, int messageHash, int paramsMask, String messageString, Object... args) {
        getSingleInstance().log(BaseProtoLogImpl.LogLevel.WTF, group, messageHash, paramsMask, messageString, args);
    }

    public static boolean isEnabled(IProtoLogGroup group) {
        return group.isLogToLogcat() || (group.isLogToProto() && getSingleInstance().isProtoEnabled());
    }

    public static synchronized ProtoLogImpl getSingleInstance() {
        ProtoLogImpl protoLogImpl;
        synchronized (ProtoLogImpl.class) {
            if (sServiceInstance == null) {
                sServiceInstance = new ProtoLogImpl(new File(LOG_FILENAME), 1048576, new ProtoLogViewerConfigReader(), 1024);
            }
            protoLogImpl = sServiceInstance;
        }
        return protoLogImpl;
    }

    public static synchronized void setSingleInstance(ProtoLogImpl instance) {
        synchronized (ProtoLogImpl.class) {
            sServiceInstance = instance;
        }
    }

    public ProtoLogImpl(File logFile, int bufferCapacity, ProtoLogViewerConfigReader viewConfigReader, int perChunkSize) {
        super(logFile, VIEWER_CONFIG_FILENAME, bufferCapacity, viewConfigReader, perChunkSize);
    }
}
