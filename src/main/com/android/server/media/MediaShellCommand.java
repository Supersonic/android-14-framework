package com.android.server.media;

import android.app.ActivityThread;
import android.media.MediaMetadata;
import android.media.session.ISessionManager;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.android.internal.util.FrameworkStatsLog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes2.dex */
public class MediaShellCommand extends ShellCommand {
    public static MediaSessionManager sMediaSessionManager;
    public static ActivityThread sThread;
    public PrintWriter mErrorWriter;
    public InputStream mInput;
    public final String mPackageName;
    public ISessionManager mSessionService;
    public PrintWriter mWriter;

    public MediaShellCommand(String str) {
        this.mPackageName = str;
    }

    public int onCommand(String str) {
        this.mWriter = getOutPrintWriter();
        this.mErrorWriter = getErrPrintWriter();
        this.mInput = getRawInputStream();
        if (TextUtils.isEmpty(str)) {
            return handleDefaultCommands(str);
        }
        if (sThread == null) {
            Looper.prepare();
            ActivityThread currentActivityThread = ActivityThread.currentActivityThread();
            sThread = currentActivityThread;
            sMediaSessionManager = (MediaSessionManager) currentActivityThread.getSystemContext().getSystemService("media_session");
        }
        ISessionManager asInterface = ISessionManager.Stub.asInterface(ServiceManager.checkService("media_session"));
        this.mSessionService = asInterface;
        if (asInterface == null) {
            throw new IllegalStateException("Can't connect to media session service; is the system running?");
        }
        try {
            if (str.equals("dispatch")) {
                runDispatch();
                return 0;
            } else if (str.equals("list-sessions")) {
                runListSessions();
                return 0;
            } else if (str.equals("monitor")) {
                runMonitor();
                return 0;
            } else if (str.equals("volume")) {
                runVolume();
                return 0;
            } else {
                showError("Error: unknown command '" + str + "'");
                return -1;
            }
        } catch (Exception e) {
            showError(e.toString());
            return -1;
        }
    }

    public void onHelp() {
        this.mWriter.println("usage: media_session [subcommand] [options]");
        this.mWriter.println("       media_session dispatch KEY");
        this.mWriter.println("       media_session list-sessions");
        this.mWriter.println("       media_session monitor <tag>");
        this.mWriter.println("       media_session volume [options]");
        this.mWriter.println();
        this.mWriter.println("media_session dispatch: dispatch a media key to the system.");
        this.mWriter.println("                KEY may be: play, pause, play-pause, mute, headsethook,");
        this.mWriter.println("                stop, next, previous, rewind, record, fast-forward.");
        this.mWriter.println("media_session list-sessions: print a list of the current sessions.");
        this.mWriter.println("media_session monitor: monitor updates to the specified session.");
        this.mWriter.println("                       Use the tag from list-sessions.");
        PrintWriter printWriter = this.mWriter;
        printWriter.println("media_session volume:  " + VolumeCtrl.USAGE);
        this.mWriter.println();
    }

    public final void sendMediaKey(KeyEvent keyEvent) {
        try {
            this.mSessionService.dispatchMediaKeyEvent(this.mPackageName, false, keyEvent, false);
        } catch (RemoteException unused) {
        }
    }

    public final void runMonitor() throws Exception {
        String nextArgRequired = getNextArgRequired();
        if (nextArgRequired == null) {
            showError("Error: must include a session id");
            return;
        }
        boolean z = false;
        try {
            Iterator<MediaController> it = sMediaSessionManager.getActiveSessions(null).iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                MediaController next = it.next();
                if (next != null) {
                    try {
                        if (nextArgRequired.equals(next.getTag())) {
                            new ControllerMonitor(next).run();
                            z = true;
                            break;
                        }
                        continue;
                    } catch (RemoteException unused) {
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            PrintWriter printWriter = this.mErrorWriter;
            printWriter.println("***Error monitoring session*** " + e.getMessage());
        }
        if (z) {
            return;
        }
        PrintWriter printWriter2 = this.mErrorWriter;
        printWriter2.println("No session found with id " + nextArgRequired);
    }

    public final void runDispatch() throws Exception {
        int i;
        String nextArgRequired = getNextArgRequired();
        if ("play".equals(nextArgRequired)) {
            i = 126;
        } else if ("pause".equals(nextArgRequired)) {
            i = 127;
        } else if ("play-pause".equals(nextArgRequired)) {
            i = 85;
        } else if ("mute".equals(nextArgRequired)) {
            i = 91;
        } else if ("headsethook".equals(nextArgRequired)) {
            i = 79;
        } else if ("stop".equals(nextArgRequired)) {
            i = 86;
        } else if ("next".equals(nextArgRequired)) {
            i = 87;
        } else if ("previous".equals(nextArgRequired)) {
            i = 88;
        } else if ("rewind".equals(nextArgRequired)) {
            i = 89;
        } else if ("record".equals(nextArgRequired)) {
            i = 130;
        } else if (!"fast-forward".equals(nextArgRequired)) {
            showError("Error: unknown dispatch code '" + nextArgRequired + "'");
            return;
        } else {
            i = 90;
        }
        long uptimeMillis = SystemClock.uptimeMillis();
        sendMediaKey(new KeyEvent(uptimeMillis, uptimeMillis, 0, i, 0, 0, -1, 0, 0, FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP));
        sendMediaKey(new KeyEvent(uptimeMillis, uptimeMillis, 1, i, 0, 0, -1, 0, 0, FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP));
    }

    public void log(String str, String str2) {
        PrintWriter printWriter = this.mWriter;
        printWriter.println(str + " " + str2);
    }

    public void showError(String str) {
        onHelp();
        this.mErrorWriter.println(str);
    }

    /* loaded from: classes2.dex */
    public class ControllerCallback extends MediaController.Callback {
        public ControllerCallback() {
        }

        @Override // android.media.session.MediaController.Callback
        public void onSessionDestroyed() {
            MediaShellCommand.this.mWriter.println("onSessionDestroyed. Enter q to quit.");
        }

        @Override // android.media.session.MediaController.Callback
        public void onSessionEvent(String str, Bundle bundle) {
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            printWriter.println("onSessionEvent event=" + str + ", extras=" + bundle);
        }

        @Override // android.media.session.MediaController.Callback
        public void onPlaybackStateChanged(PlaybackState playbackState) {
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            printWriter.println("onPlaybackStateChanged " + playbackState);
        }

        @Override // android.media.session.MediaController.Callback
        public void onMetadataChanged(MediaMetadata mediaMetadata) {
            String str;
            if (mediaMetadata == null) {
                str = null;
            } else {
                str = "title=" + mediaMetadata.getDescription();
            }
            MediaShellCommand.this.mWriter.println("onMetadataChanged " + str);
        }

        @Override // android.media.session.MediaController.Callback
        public void onQueueChanged(List<MediaSession.QueueItem> list) {
            String str;
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            StringBuilder sb = new StringBuilder();
            sb.append("onQueueChanged, ");
            if (list == null) {
                str = "null queue";
            } else {
                str = " size=" + list.size();
            }
            sb.append(str);
            printWriter.println(sb.toString());
        }

        @Override // android.media.session.MediaController.Callback
        public void onQueueTitleChanged(CharSequence charSequence) {
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            printWriter.println("onQueueTitleChange " + ((Object) charSequence));
        }

        @Override // android.media.session.MediaController.Callback
        public void onExtrasChanged(Bundle bundle) {
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            printWriter.println("onExtrasChanged " + bundle);
        }

        @Override // android.media.session.MediaController.Callback
        public void onAudioInfoChanged(MediaController.PlaybackInfo playbackInfo) {
            PrintWriter printWriter = MediaShellCommand.this.mWriter;
            printWriter.println("onAudioInfoChanged " + playbackInfo);
        }
    }

    /* loaded from: classes2.dex */
    public class ControllerMonitor {
        public final MediaController mController;
        public final ControllerCallback mControllerCallback;

        public ControllerMonitor(MediaController mediaController) {
            this.mController = mediaController;
            this.mControllerCallback = new ControllerCallback();
        }

        public void printUsageMessage() {
            try {
                PrintWriter printWriter = MediaShellCommand.this.mWriter;
                printWriter.println("V2Monitoring session " + this.mController.getTag() + "...  available commands: play, pause, next, previous");
            } catch (RuntimeException unused) {
                MediaShellCommand.this.mWriter.println("Error trying to monitor session!");
            }
            MediaShellCommand.this.mWriter.println("(q)uit: finish monitoring");
        }

        public void run() throws RemoteException {
            MediaController mediaController;
            boolean z;
            printUsageMessage();
            HandlerThread handlerThread = new HandlerThread("MediaCb") { // from class: com.android.server.media.MediaShellCommand.ControllerMonitor.1
                @Override // android.os.HandlerThread
                public void onLooperPrepared() {
                    try {
                        ControllerMonitor.this.mController.registerCallback(ControllerMonitor.this.mControllerCallback);
                    } catch (RuntimeException unused) {
                        MediaShellCommand.this.mErrorWriter.println("Error registering monitor callback");
                    }
                }
            };
            handlerThread.start();
            try {
                try {
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(MediaShellCommand.this.mInput));
                        while (true) {
                            MediaShellCommand.this.mWriter.flush();
                            MediaShellCommand.this.mErrorWriter.flush();
                            String readLine = bufferedReader.readLine();
                            if (readLine == null) {
                                break;
                            }
                            if (readLine.length() > 0) {
                                if ("q".equals(readLine) || "quit".equals(readLine)) {
                                    break;
                                }
                                if ("play".equals(readLine)) {
                                    dispatchKeyCode(126);
                                } else if ("pause".equals(readLine)) {
                                    dispatchKeyCode(127);
                                } else if ("next".equals(readLine)) {
                                    dispatchKeyCode(87);
                                } else if ("previous".equals(readLine)) {
                                    dispatchKeyCode(88);
                                } else {
                                    PrintWriter printWriter = MediaShellCommand.this.mErrorWriter;
                                    printWriter.println("Invalid command: " + readLine);
                                }
                                z = true;
                            } else {
                                z = false;
                            }
                            synchronized (this) {
                                if (z) {
                                    MediaShellCommand.this.mWriter.println("");
                                }
                                printUsageMessage();
                            }
                        }
                        mediaController = this.mController;
                    } catch (Exception unused) {
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handlerThread.getLooper().quit();
                    mediaController = this.mController;
                }
                mediaController.unregisterCallback(this.mControllerCallback);
            } finally {
                handlerThread.getLooper().quit();
                try {
                    this.mController.unregisterCallback(this.mControllerCallback);
                } catch (Exception unused2) {
                }
            }
        }

        public final void dispatchKeyCode(int i) {
            long uptimeMillis = SystemClock.uptimeMillis();
            KeyEvent keyEvent = new KeyEvent(uptimeMillis, uptimeMillis, 0, i, 0, 0, -1, 0, 0, FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP);
            KeyEvent keyEvent2 = new KeyEvent(uptimeMillis, uptimeMillis, 1, i, 0, 0, -1, 0, 0, FrameworkStatsLog.HDMI_CEC_MESSAGE_REPORTED__USER_CONTROL_PRESSED_COMMAND__UP);
            try {
                this.mController.dispatchMediaButtonEvent(keyEvent);
                this.mController.dispatchMediaButtonEvent(keyEvent2);
            } catch (RuntimeException unused) {
                PrintWriter printWriter = MediaShellCommand.this.mErrorWriter;
                printWriter.println("Failed to dispatch " + i);
            }
        }
    }

    public final void runListSessions() {
        this.mWriter.println("Sessions:");
        try {
            for (MediaController mediaController : sMediaSessionManager.getActiveSessions(null)) {
                if (mediaController != null) {
                    try {
                        PrintWriter printWriter = this.mWriter;
                        printWriter.println("  tag=" + mediaController.getTag() + ", package=" + mediaController.getPackageName());
                    } catch (RuntimeException unused) {
                    }
                }
            }
        } catch (Exception unused2) {
            this.mErrorWriter.println("***Error listing sessions***");
        }
    }

    public final void runVolume() throws Exception {
        VolumeCtrl.run(this);
    }
}
