package com.android.modules.utils;

import android.p008os.Binder;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedInputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
/* loaded from: classes5.dex */
public abstract class BasicShellCommandHandler {
    protected static final boolean DEBUG = false;
    protected static final String TAG = "ShellCommand";
    private int mArgPos;
    private String[] mArgs;
    private String mCmd;
    private String mCurArgData;
    private FileDescriptor mErr;
    private PrintWriter mErrPrintWriter;
    private FileOutputStream mFileErr;
    private FileInputStream mFileIn;
    private FileOutputStream mFileOut;
    private FileDescriptor mIn;
    private InputStream mInputStream;
    private FileDescriptor mOut;
    private PrintWriter mOutPrintWriter;
    private Binder mTarget;

    public abstract int onCommand(String str);

    public abstract void onHelp();

    public void init(Binder target, FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, int firstArgPos) {
        this.mTarget = target;
        this.mIn = in;
        this.mOut = out;
        this.mErr = err;
        this.mArgs = args;
        this.mCmd = null;
        this.mArgPos = firstArgPos;
        this.mCurArgData = null;
        this.mFileIn = null;
        this.mFileOut = null;
        this.mFileErr = null;
        this.mOutPrintWriter = null;
        this.mErrPrintWriter = null;
        this.mInputStream = null;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0029, code lost:
        if (r2 != null) goto L13;
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x002b, code lost:
        r2.flush();
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x0061, code lost:
        if (r2 == null) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0064, code lost:
        return r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public int exec(Binder target, FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args) {
        String cmd;
        int start;
        PrintWriter printWriter;
        if (args != null && args.length > 0) {
            cmd = args[0];
            start = 1;
        } else {
            cmd = null;
            start = 0;
        }
        init(target, in, out, err, args, start);
        this.mCmd = cmd;
        int res = -1;
        try {
            res = onCommand(cmd);
            PrintWriter printWriter2 = this.mOutPrintWriter;
            if (printWriter2 != null) {
                printWriter2.flush();
            }
            printWriter = this.mErrPrintWriter;
        } catch (Throwable e) {
            try {
                PrintWriter eout = getErrPrintWriter();
                eout.println();
                eout.println("Exception occurred while executing '" + this.mCmd + "':");
                e.printStackTrace(eout);
                PrintWriter printWriter3 = this.mOutPrintWriter;
                if (printWriter3 != null) {
                    printWriter3.flush();
                }
                printWriter = this.mErrPrintWriter;
            } catch (Throwable th) {
                PrintWriter printWriter4 = this.mOutPrintWriter;
                if (printWriter4 != null) {
                    printWriter4.flush();
                }
                PrintWriter printWriter5 = this.mErrPrintWriter;
                if (printWriter5 != null) {
                    printWriter5.flush();
                }
                throw th;
            }
        }
    }

    public FileDescriptor getOutFileDescriptor() {
        return this.mOut;
    }

    public OutputStream getRawOutputStream() {
        if (this.mFileOut == null) {
            this.mFileOut = new FileOutputStream(this.mOut);
        }
        return this.mFileOut;
    }

    public PrintWriter getOutPrintWriter() {
        if (this.mOutPrintWriter == null) {
            this.mOutPrintWriter = new PrintWriter(getRawOutputStream());
        }
        return this.mOutPrintWriter;
    }

    public FileDescriptor getErrFileDescriptor() {
        return this.mErr;
    }

    public OutputStream getRawErrorStream() {
        if (this.mFileErr == null) {
            this.mFileErr = new FileOutputStream(this.mErr);
        }
        return this.mFileErr;
    }

    public PrintWriter getErrPrintWriter() {
        if (this.mErr == null) {
            return getOutPrintWriter();
        }
        if (this.mErrPrintWriter == null) {
            this.mErrPrintWriter = new PrintWriter(getRawErrorStream());
        }
        return this.mErrPrintWriter;
    }

    public FileDescriptor getInFileDescriptor() {
        return this.mIn;
    }

    public InputStream getRawInputStream() {
        if (this.mFileIn == null) {
            this.mFileIn = new FileInputStream(this.mIn);
        }
        return this.mFileIn;
    }

    public InputStream getBufferedInputStream() {
        if (this.mInputStream == null) {
            this.mInputStream = new BufferedInputStream(getRawInputStream());
        }
        return this.mInputStream;
    }

    public String getNextOption() {
        if (this.mCurArgData != null) {
            String prev = this.mArgs[this.mArgPos - 1];
            throw new IllegalArgumentException("No argument expected after \"" + prev + "\"");
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return null;
        }
        String arg = strArr[i];
        if (arg.startsWith(NativeLibraryHelper.CLEAR_ABI_OVERRIDE)) {
            this.mArgPos++;
            if (arg.equals("--")) {
                return null;
            }
            if (arg.length() > 1 && arg.charAt(1) != '-') {
                if (arg.length() > 2) {
                    this.mCurArgData = arg.substring(2);
                    return arg.substring(0, 2);
                }
                this.mCurArgData = null;
                return arg;
            }
            this.mCurArgData = null;
            return arg;
        }
        return null;
    }

    public String getNextArg() {
        if (this.mCurArgData != null) {
            String arg = this.mCurArgData;
            this.mCurArgData = null;
            return arg;
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i < strArr.length) {
            this.mArgPos = i + 1;
            return strArr[i];
        }
        return null;
    }

    public String peekNextArg() {
        String str = this.mCurArgData;
        if (str != null) {
            return str;
        }
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i < strArr.length) {
            return strArr[i];
        }
        return null;
    }

    public String[] peekRemainingArgs() {
        int remaining = getRemainingArgsCount();
        String[] args = new String[remaining];
        int pos = this.mArgPos;
        while (true) {
            String[] strArr = this.mArgs;
            if (pos < strArr.length) {
                args[pos - this.mArgPos] = strArr[pos];
                pos++;
            } else {
                return args;
            }
        }
    }

    public int getRemainingArgsCount() {
        int i = this.mArgPos;
        String[] strArr = this.mArgs;
        if (i >= strArr.length) {
            return 0;
        }
        return strArr.length - i;
    }

    public String getNextArgRequired() {
        String arg = getNextArg();
        if (arg == null) {
            String prev = this.mArgs[this.mArgPos - 1];
            throw new IllegalArgumentException("Argument expected after \"" + prev + "\"");
        }
        return arg;
    }

    public int handleDefaultCommands(String cmd) {
        if (cmd == null || "help".equals(cmd) || "-h".equals(cmd)) {
            onHelp();
            return -1;
        }
        getOutPrintWriter().println("Unknown command: " + cmd);
        return -1;
    }

    public Binder getTarget() {
        return this.mTarget;
    }

    public String[] getAllArgs() {
        return this.mArgs;
    }
}
