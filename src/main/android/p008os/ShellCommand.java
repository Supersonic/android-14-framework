package android.p008os;

import com.android.modules.utils.BasicShellCommandHandler;
import java.io.FileDescriptor;
/* renamed from: android.os.ShellCommand */
/* loaded from: classes3.dex */
public abstract class ShellCommand extends BasicShellCommandHandler {
    private ResultReceiver mResultReceiver;
    private ShellCallback mShellCallback;

    public int exec(Binder target, FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
        this.mShellCallback = callback;
        this.mResultReceiver = resultReceiver;
        int result = super.exec(target, in, out, err, args);
        ResultReceiver resultReceiver2 = this.mResultReceiver;
        if (resultReceiver2 != null) {
            resultReceiver2.send(result, null);
        }
        return result;
    }

    public ResultReceiver adoptResultReceiver() {
        ResultReceiver rr = this.mResultReceiver;
        this.mResultReceiver = null;
        return rr;
    }

    public ParcelFileDescriptor openFileForSystem(String path, String mode) {
        try {
            ParcelFileDescriptor pfd = getShellCallback().openFile(path, "u:r:system_server:s0", mode);
            if (pfd != null) {
                return pfd;
            }
        } catch (RuntimeException e) {
            getErrPrintWriter().println("Failure opening file: " + e.getMessage());
        }
        getErrPrintWriter().println("Error: Unable to open file: " + path);
        if (path == null || !path.startsWith("/data/local/tmp/")) {
            getErrPrintWriter().println("Consider using a file under /data/local/tmp/");
            return null;
        }
        return null;
    }

    @Override // com.android.modules.utils.BasicShellCommandHandler
    public int handleDefaultCommands(String cmd) {
        if ("dump".equals(cmd)) {
            String[] newArgs = new String[getAllArgs().length - 1];
            System.arraycopy(getAllArgs(), 1, newArgs, 0, getAllArgs().length - 1);
            getTarget().doDump(getOutFileDescriptor(), getOutPrintWriter(), newArgs);
            return 0;
        }
        return super.handleDefaultCommands(cmd);
    }

    @Override // com.android.modules.utils.BasicShellCommandHandler
    public String peekNextArg() {
        return super.peekNextArg();
    }

    public ShellCallback getShellCallback() {
        return this.mShellCallback;
    }
}
