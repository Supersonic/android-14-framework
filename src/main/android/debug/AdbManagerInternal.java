package android.debug;

import java.io.File;
/* loaded from: classes.dex */
public abstract class AdbManagerInternal {
    public abstract File getAdbKeysFile();

    public abstract File getAdbTempKeysFile();

    public abstract boolean isAdbEnabled(byte b);

    public abstract void notifyKeyFilesUpdated();

    public abstract void registerTransport(IAdbTransport iAdbTransport);

    public abstract void startAdbdForTransport(byte b);

    public abstract void stopAdbdForTransport(byte b);

    public abstract void unregisterTransport(IAdbTransport iAdbTransport);
}
