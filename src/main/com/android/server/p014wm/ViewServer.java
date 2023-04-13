package com.android.server.p014wm;

import android.p005os.IInstalld;
import android.util.Slog;
import com.android.server.p014wm.WindowManagerService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* renamed from: com.android.server.wm.ViewServer */
/* loaded from: classes2.dex */
public class ViewServer implements Runnable {
    public final int mPort;
    public ServerSocket mServer;
    public Thread mThread;
    public ExecutorService mThreadPool;
    public final WindowManagerService mWindowManager;

    public ViewServer(WindowManagerService windowManagerService, int i) {
        this.mWindowManager = windowManagerService;
        this.mPort = i;
    }

    public boolean start() throws IOException {
        if (this.mThread != null) {
            return false;
        }
        this.mServer = new ServerSocket(this.mPort, 10, InetAddress.getLocalHost());
        this.mThread = new Thread(this, "Remote View Server [port=" + this.mPort + "]");
        this.mThreadPool = Executors.newFixedThreadPool(10);
        this.mThread.start();
        return true;
    }

    public boolean stop() {
        Thread thread = this.mThread;
        if (thread != null) {
            thread.interrupt();
            ExecutorService executorService = this.mThreadPool;
            if (executorService != null) {
                try {
                    executorService.shutdownNow();
                } catch (SecurityException unused) {
                    Slog.w(StartingSurfaceController.TAG, "Could not stop all view server threads");
                }
            }
            this.mThreadPool = null;
            this.mThread = null;
            try {
                this.mServer.close();
                this.mServer = null;
                return true;
            } catch (IOException unused2) {
                Slog.w(StartingSurfaceController.TAG, "Could not close the view server");
                return false;
            }
        }
        return false;
    }

    public boolean isRunning() {
        Thread thread = this.mThread;
        return thread != null && thread.isAlive();
    }

    @Override // java.lang.Runnable
    public void run() {
        while (Thread.currentThread() == this.mThread) {
            try {
                Socket accept = this.mServer.accept();
                ExecutorService executorService = this.mThreadPool;
                if (executorService != null) {
                    executorService.submit(new ViewServerWorker(accept));
                } else {
                    try {
                        accept.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e2) {
                Slog.w(StartingSurfaceController.TAG, "Connection error: ", e2);
            }
        }
    }

    public static boolean writeValue(Socket socket, String str) {
        BufferedWriter bufferedWriter;
        boolean z = false;
        BufferedWriter bufferedWriter2 = null;
        try {
            try {
                bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()), IInstalld.FLAG_FORCE);
            } catch (IOException unused) {
            }
            try {
                bufferedWriter.write(str);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
                bufferedWriter.close();
                z = true;
            } catch (Exception unused2) {
                bufferedWriter2 = bufferedWriter;
                if (bufferedWriter2 != null) {
                    bufferedWriter2.close();
                }
                return z;
            } catch (Throwable th) {
                th = th;
                bufferedWriter2 = bufferedWriter;
                if (bufferedWriter2 != null) {
                    try {
                        bufferedWriter2.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
        } catch (Exception unused4) {
        } catch (Throwable th2) {
            th = th2;
        }
        return z;
    }

    /* renamed from: com.android.server.wm.ViewServer$ViewServerWorker */
    /* loaded from: classes2.dex */
    public class ViewServerWorker implements Runnable, WindowManagerService.WindowChangeListener {
        public Socket mClient;
        public boolean mNeedWindowListUpdate = false;
        public boolean mNeedFocusedWindowUpdate = false;

        public ViewServerWorker(Socket socket) {
            this.mClient = socket;
        }

        /* JADX WARN: Removed duplicated region for block: B:73:0x00e2 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Removed duplicated region for block: B:75:0x00ee A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void run() {
            BufferedReader bufferedReader;
            IOException e;
            Socket socket;
            String substring;
            boolean viewServerWindowCommand;
            Socket socket2;
            BufferedReader bufferedReader2 = null;
            try {
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(this.mClient.getInputStream()), 1024);
                    try {
                        try {
                            String readLine = bufferedReader.readLine();
                            int indexOf = readLine.indexOf(32);
                            if (indexOf == -1) {
                                substring = "";
                            } else {
                                String substring2 = readLine.substring(0, indexOf);
                                substring = readLine.substring(indexOf + 1);
                                readLine = substring2;
                            }
                            if ("PROTOCOL".equalsIgnoreCase(readLine)) {
                                viewServerWindowCommand = ViewServer.writeValue(this.mClient, "4");
                            } else if ("SERVER".equalsIgnoreCase(readLine)) {
                                viewServerWindowCommand = ViewServer.writeValue(this.mClient, "4");
                            } else if ("LIST".equalsIgnoreCase(readLine)) {
                                viewServerWindowCommand = ViewServer.this.mWindowManager.viewServerListWindows(this.mClient);
                            } else if ("GET_FOCUS".equalsIgnoreCase(readLine)) {
                                viewServerWindowCommand = ViewServer.this.mWindowManager.viewServerGetFocusedWindow(this.mClient);
                            } else if ("AUTOLIST".equalsIgnoreCase(readLine)) {
                                viewServerWindowCommand = windowManagerAutolistLoop();
                            } else {
                                viewServerWindowCommand = ViewServer.this.mWindowManager.viewServerWindowCommand(this.mClient, readLine, substring);
                            }
                            if (!viewServerWindowCommand) {
                                Slog.w(StartingSurfaceController.TAG, "An error occurred with the command: " + readLine);
                            }
                            try {
                                bufferedReader.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                            socket2 = this.mClient;
                        } catch (IOException e3) {
                            e = e3;
                            Slog.w(StartingSurfaceController.TAG, "Connection error: ", e);
                            if (bufferedReader != null) {
                                try {
                                    bufferedReader.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                }
                            }
                            Socket socket3 = this.mClient;
                            if (socket3 != null) {
                                socket3.close();
                            }
                            return;
                        }
                    } catch (Throwable th) {
                        th = th;
                        bufferedReader2 = bufferedReader;
                        if (bufferedReader2 != null) {
                            try {
                                bufferedReader2.close();
                            } catch (IOException e5) {
                                e5.printStackTrace();
                            }
                        }
                        socket = this.mClient;
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e6) {
                                e6.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e7) {
                    e7.printStackTrace();
                    return;
                }
            } catch (IOException e8) {
                bufferedReader = null;
                e = e8;
            } catch (Throwable th2) {
                th = th2;
                if (bufferedReader2 != null) {
                }
                socket = this.mClient;
                if (socket != null) {
                }
                throw th;
            }
            if (socket2 != null) {
                socket2.close();
            }
        }

        @Override // com.android.server.p014wm.WindowManagerService.WindowChangeListener
        public void windowsChanged() {
            synchronized (this) {
                this.mNeedWindowListUpdate = true;
                notifyAll();
            }
        }

        @Override // com.android.server.p014wm.WindowManagerService.WindowChangeListener
        public void focusChanged() {
            synchronized (this) {
                this.mNeedFocusedWindowUpdate = true;
                notifyAll();
            }
        }

        public final boolean windowManagerAutolistLoop() {
            boolean z;
            boolean z2;
            boolean z3;
            ViewServer.this.mWindowManager.addWindowChangeListener(this);
            BufferedWriter bufferedWriter = null;
            try {
                try {
                    BufferedWriter bufferedWriter2 = new BufferedWriter(new OutputStreamWriter(this.mClient.getOutputStream()));
                    while (!Thread.interrupted()) {
                        try {
                            synchronized (this) {
                                while (true) {
                                    z = this.mNeedWindowListUpdate;
                                    if (z || this.mNeedFocusedWindowUpdate) {
                                        break;
                                    }
                                    wait();
                                }
                                z2 = false;
                                if (z) {
                                    this.mNeedWindowListUpdate = false;
                                    z3 = true;
                                } else {
                                    z3 = false;
                                }
                                if (this.mNeedFocusedWindowUpdate) {
                                    this.mNeedFocusedWindowUpdate = false;
                                    z2 = true;
                                }
                            }
                            if (z3) {
                                bufferedWriter2.write("LIST UPDATE\n");
                                bufferedWriter2.flush();
                            }
                            if (z2) {
                                bufferedWriter2.write("ACTION_FOCUS UPDATE\n");
                                bufferedWriter2.flush();
                            }
                        } catch (Exception unused) {
                            bufferedWriter = bufferedWriter2;
                            if (bufferedWriter != null) {
                                bufferedWriter.close();
                            }
                            ViewServer.this.mWindowManager.removeWindowChangeListener(this);
                            return true;
                        } catch (Throwable th) {
                            th = th;
                            bufferedWriter = bufferedWriter2;
                            if (bufferedWriter != null) {
                                try {
                                    bufferedWriter.close();
                                } catch (IOException unused2) {
                                }
                            }
                            ViewServer.this.mWindowManager.removeWindowChangeListener(this);
                            throw th;
                        }
                    }
                    bufferedWriter2.close();
                } catch (IOException unused3) {
                }
            } catch (Exception unused4) {
            } catch (Throwable th2) {
                th = th2;
            }
            ViewServer.this.mWindowManager.removeWindowChangeListener(this);
            return true;
        }
    }
}
