package com.android.server.p014wm;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ShellCommand;
import android.provider.Settings;
import android.util.Pair;
import android.view.CrossWindowBlurListeners;
import android.view.IWindowManager;
import com.android.internal.os.ByteTransferPipe;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.IoThread;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
/* renamed from: com.android.server.wm.WindowManagerShellCommand */
/* loaded from: classes2.dex */
public class WindowManagerShellCommand extends ShellCommand {
    public final IWindowManager mInterface;
    public final WindowManagerService mInternal;
    public final LetterboxConfiguration mLetterboxConfiguration;

    public WindowManagerShellCommand(WindowManagerService windowManagerService) {
        this.mInterface = windowManagerService;
        this.mInternal = windowManagerService;
        this.mLetterboxConfiguration = windowManagerService.mLetterboxConfiguration;
    }

    public int onCommand(String str) {
        char c;
        if (str == null) {
            return handleDefaultCommands(str);
        }
        PrintWriter outPrintWriter = getOutPrintWriter();
        try {
            switch (str.hashCode()) {
                case -2001980078:
                    if (str.equals("get-letterbox-style")) {
                        c = '\r';
                        break;
                    }
                    c = 65535;
                    break;
                case -1959253708:
                    if (str.equals("get-multi-window-config")) {
                        c = 17;
                        break;
                    }
                    c = 65535;
                    break;
                case -1829173266:
                    if (str.equals("get-ignore-orientation-request")) {
                        c = '\n';
                        break;
                    }
                    c = 65535;
                    break;
                case -1693379742:
                    if (str.equals("set-ignore-orientation-request")) {
                        c = '\t';
                        break;
                    }
                    c = 65535;
                    break;
                case -1067396926:
                    if (str.equals("tracing")) {
                        c = 5;
                        break;
                    }
                    c = 65535;
                    break;
                case -1032601556:
                    if (str.equals("disable-blur")) {
                        c = 20;
                        break;
                    }
                    c = 65535;
                    break;
                case -1014709755:
                    if (str.equals("dump-visible-window-views")) {
                        c = 11;
                        break;
                    }
                    c = 65535;
                    break;
                case -336752166:
                    if (str.equals("folded-area")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case -229462135:
                    if (str.equals("dismiss-keyguard")) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 3530753:
                    if (str.equals("size")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case 93018176:
                    if (str.equals("set-multi-window-config")) {
                        c = 16;
                        break;
                    }
                    c = 65535;
                    break;
                case 108404047:
                    if (str.equals("reset")) {
                        c = 19;
                        break;
                    }
                    c = 65535;
                    break;
                case 109403696:
                    if (str.equals("shell")) {
                        c = 21;
                        break;
                    }
                    c = 65535;
                    break;
                case 188660544:
                    if (str.equals("user-rotation")) {
                        c = 7;
                        break;
                    }
                    c = 65535;
                    break;
                case 342281055:
                    if (str.equals("logging")) {
                        c = 6;
                        break;
                    }
                    c = 65535;
                    break;
                case 344144277:
                    if (str.equals("set-sandbox-display-apis")) {
                        c = 15;
                        break;
                    }
                    c = 65535;
                    break;
                case 731885899:
                    if (str.equals("reset-letterbox-style")) {
                        c = 14;
                        break;
                    }
                    c = 65535;
                    break;
                case 749259358:
                    if (str.equals("set-letterbox-style")) {
                        c = '\f';
                        break;
                    }
                    c = 65535;
                    break;
                case 1336606893:
                    if (str.equals("reset-multi-window-config")) {
                        c = 18;
                        break;
                    }
                    c = 65535;
                    break;
                case 1552717032:
                    if (str.equals("density")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1583955111:
                    if (str.equals("fixed-to-user-rotation")) {
                        c = '\b';
                        break;
                    }
                    c = 65535;
                    break;
                case 1910897543:
                    if (str.equals("scaling")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    return runDisplaySize(outPrintWriter);
                case 1:
                    return runDisplayDensity(outPrintWriter);
                case 2:
                    return runDisplayFoldedArea(outPrintWriter);
                case 3:
                    return runDisplayScaling(outPrintWriter);
                case 4:
                    return runDismissKeyguard(outPrintWriter);
                case 5:
                    return this.mInternal.mWindowTracing.onShellCommand(this);
                case 6:
                    int onShellCommand = ProtoLogImpl.getSingleInstance().onShellCommand(this);
                    if (onShellCommand != 0) {
                        outPrintWriter.println("Not handled, please use `adb shell dumpsys activity service SystemUIService WMShell` if you are looking for ProtoLog in WMShell");
                    }
                    return onShellCommand;
                case 7:
                    return runDisplayUserRotation(outPrintWriter);
                case '\b':
                    return runFixedToUserRotation(outPrintWriter);
                case '\t':
                    return runSetIgnoreOrientationRequest(outPrintWriter);
                case '\n':
                    return runGetIgnoreOrientationRequest(outPrintWriter);
                case 11:
                    return runDumpVisibleWindowViews(outPrintWriter);
                case '\f':
                    return runSetLetterboxStyle(outPrintWriter);
                case '\r':
                    return runGetLetterboxStyle(outPrintWriter);
                case 14:
                    return runResetLetterboxStyle(outPrintWriter);
                case 15:
                    return runSandboxDisplayApis(outPrintWriter);
                case 16:
                    return runSetMultiWindowConfig();
                case 17:
                    return runGetMultiWindowConfig(outPrintWriter);
                case 18:
                    return runResetMultiWindowConfig();
                case 19:
                    return runReset(outPrintWriter);
                case 20:
                    return runSetBlurDisabled(outPrintWriter);
                case 21:
                    return runWmShellCommand(outPrintWriter);
                default:
                    return handleDefaultCommands(str);
            }
        } catch (RemoteException e) {
            outPrintWriter.println("Remote exception: " + e);
            return -1;
        }
    }

    public final int getDisplayId(String str) {
        if (!"-d".equals(str)) {
            str = getNextOption();
        }
        if (str != null && "-d".equals(str)) {
            try {
                return Integer.parseInt(getNextArgRequired());
            } catch (NumberFormatException e) {
                PrintWriter errPrintWriter = this.getErrPrintWriter();
                errPrintWriter.println("Error: bad number " + e);
            } catch (IllegalArgumentException e2) {
                PrintWriter errPrintWriter2 = this.getErrPrintWriter();
                errPrintWriter2.println("Error: " + e2);
            }
        }
        return 0;
    }

    public final void printInitialDisplaySize(PrintWriter printWriter, int i) {
        Point point = new Point();
        Point point2 = new Point();
        try {
            this.mInterface.getInitialDisplaySize(i, point);
            this.mInterface.getBaseDisplaySize(i, point2);
            printWriter.println("Physical size: " + point.x + "x" + point.y);
            if (point.equals(point2)) {
                return;
            }
            printWriter.println("Override size: " + point2.x + "x" + point2.y);
        } catch (RemoteException e) {
            printWriter.println("Remote exception: " + e);
        }
    }

    public final int runDisplaySize(PrintWriter printWriter) throws RemoteException {
        int parseDimension;
        String nextArg = getNextArg();
        int displayId = getDisplayId(nextArg);
        if (nextArg == null) {
            printInitialDisplaySize(printWriter, displayId);
            return 0;
        } else if ("-d".equals(nextArg)) {
            printInitialDisplaySize(printWriter, displayId);
            return 0;
        } else {
            int i = -1;
            if ("reset".equals(nextArg)) {
                parseDimension = -1;
            } else {
                int indexOf = nextArg.indexOf(120);
                if (indexOf <= 0 || indexOf >= nextArg.length() - 1) {
                    getErrPrintWriter().println("Error: bad size " + nextArg);
                    return -1;
                }
                String substring = nextArg.substring(0, indexOf);
                String substring2 = nextArg.substring(indexOf + 1);
                try {
                    int parseDimension2 = parseDimension(substring, displayId);
                    parseDimension = parseDimension(substring2, displayId);
                    i = parseDimension2;
                } catch (NumberFormatException e) {
                    getErrPrintWriter().println("Error: bad number " + e);
                    return -1;
                }
            }
            if (i >= 0 && parseDimension >= 0) {
                this.mInterface.setForcedDisplaySize(displayId, i, parseDimension);
            } else {
                this.mInterface.clearForcedDisplaySize(displayId);
            }
            return 0;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSetBlurDisabled(PrintWriter printWriter) throws RemoteException {
        char c;
        String nextArg = getNextArg();
        if (nextArg == null) {
            printWriter.println("Blur supported on device: " + CrossWindowBlurListeners.CROSS_WINDOW_BLUR_SUPPORTED);
            printWriter.println("Blur enabled: " + this.mInternal.mBlurController.getBlurEnabled());
            return 0;
        }
        int i = 1;
        switch (nextArg.hashCode()) {
            case 48:
                if (nextArg.equals("0")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (nextArg.equals("1")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (nextArg.equals("true")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (nextArg.equals("false")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 3:
                i = 0;
                break;
            case 1:
            case 2:
                break;
            default:
                getErrPrintWriter().println("Error: expected true, 1, false, 0, but got " + nextArg);
                return -1;
        }
        Settings.Global.putInt(this.mInternal.mContext.getContentResolver(), "disable_window_blurs", i);
        return 0;
    }

    public final void printInitialDisplayDensity(PrintWriter printWriter, int i) {
        try {
            int initialDisplayDensity = this.mInterface.getInitialDisplayDensity(i);
            int baseDisplayDensity = this.mInterface.getBaseDisplayDensity(i);
            printWriter.println("Physical density: " + initialDisplayDensity);
            if (initialDisplayDensity != baseDisplayDensity) {
                printWriter.println("Override density: " + baseDisplayDensity);
            }
        } catch (RemoteException e) {
            printWriter.println("Remote exception: " + e);
        }
    }

    public final int runDisplayDensity(PrintWriter printWriter) throws RemoteException {
        int displayIdByUniqueId;
        String nextArg = getNextArg();
        String nextOption = getNextOption();
        String nextArg2 = getNextArg();
        int i = -1;
        if ("-d".equals(nextOption) && nextArg2 != null) {
            try {
                displayIdByUniqueId = Integer.parseInt(nextArg2);
            } catch (NumberFormatException e) {
                getErrPrintWriter().println("Error: bad number " + e);
            }
        } else {
            if ("-u".equals(nextOption) && nextArg2 != null) {
                displayIdByUniqueId = this.mInterface.getDisplayIdByUniqueId(nextArg2);
                if (displayIdByUniqueId == -1) {
                    getErrPrintWriter().println("Error: the uniqueId is invalid ");
                    return -1;
                }
            }
            displayIdByUniqueId = 0;
        }
        if (nextArg == null) {
            printInitialDisplayDensity(printWriter, displayIdByUniqueId);
            return 0;
        } else if ("-d".equals(nextArg)) {
            printInitialDisplayDensity(printWriter, displayIdByUniqueId);
            return 0;
        } else {
            if (!"reset".equals(nextArg)) {
                try {
                    int parseInt = Integer.parseInt(nextArg);
                    if (parseInt < 72) {
                        getErrPrintWriter().println("Error: density must be >= 72");
                        return -1;
                    }
                    i = parseInt;
                } catch (NumberFormatException e2) {
                    getErrPrintWriter().println("Error: bad number " + e2);
                    return -1;
                }
            }
            if (i > 0) {
                this.mInterface.setForcedDisplayDensityForUser(displayIdByUniqueId, i, -2);
            } else {
                this.mInterface.clearForcedDisplayDensityForUser(displayIdByUniqueId, -2);
            }
            return 0;
        }
    }

    public final void printFoldedArea(PrintWriter printWriter) {
        Rect foldedArea = this.mInternal.getFoldedArea();
        if (foldedArea.isEmpty()) {
            printWriter.println("Folded area: none");
            return;
        }
        printWriter.println("Folded area: " + foldedArea.left + "," + foldedArea.top + "," + foldedArea.right + "," + foldedArea.bottom);
    }

    public final int runDisplayFoldedArea(PrintWriter printWriter) {
        String nextArg = getNextArg();
        Rect rect = new Rect();
        if (nextArg == null) {
            printFoldedArea(printWriter);
            return 0;
        }
        if ("reset".equals(nextArg)) {
            rect.setEmpty();
        } else {
            Matcher matcher = Pattern.compile("(-?\\d+),(-?\\d+),(-?\\d+),(-?\\d+)").matcher(nextArg);
            if (!matcher.matches()) {
                getErrPrintWriter().println("Error: area should be LEFT,TOP,RIGHT,BOTTOM");
                return -1;
            }
            rect.set(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(4)));
        }
        this.mInternal.setOverrideFoldedArea(rect);
        return 0;
    }

    public final int runDisplayScaling(PrintWriter printWriter) throws RemoteException {
        String nextArgRequired = getNextArgRequired();
        if ("auto".equals(nextArgRequired)) {
            this.mInterface.setForcedDisplayScalingMode(getDisplayId(nextArgRequired), 0);
        } else if ("off".equals(nextArgRequired)) {
            this.mInterface.setForcedDisplayScalingMode(getDisplayId(nextArgRequired), 1);
        } else {
            getErrPrintWriter().println("Error: scaling must be 'auto' or 'off'");
            return -1;
        }
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSandboxDisplayApis(PrintWriter printWriter) throws RemoteException {
        int i;
        char c;
        String nextArgRequired = getNextArgRequired();
        if ("-d".equals(nextArgRequired)) {
            i = Integer.parseInt(getNextArgRequired());
            nextArgRequired = getNextArgRequired();
        } else {
            i = 0;
        }
        nextArgRequired.hashCode();
        boolean z = true;
        switch (nextArgRequired.hashCode()) {
            case 48:
                if (nextArgRequired.equals("0")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (nextArgRequired.equals("1")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (nextArgRequired.equals("true")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (nextArgRequired.equals("false")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 3:
                z = false;
                break;
            case 1:
            case 2:
                break;
            default:
                getErrPrintWriter().println("Error: expecting true, 1, false, 0, but we get " + nextArgRequired);
                return -1;
        }
        this.mInternal.setSandboxDisplayApis(i, z);
        return 0;
    }

    public final int runDismissKeyguard(PrintWriter printWriter) throws RemoteException {
        this.mInterface.dismissKeyguard((IKeyguardDismissCallback) null, (CharSequence) null);
        return 0;
    }

    public final int parseDimension(String str, int i) throws NumberFormatException {
        int i2;
        if (str.endsWith("px")) {
            return Integer.parseInt(str.substring(0, str.length() - 2));
        }
        if (str.endsWith("dp")) {
            try {
                i2 = this.mInterface.getBaseDisplayDensity(i);
            } catch (RemoteException unused) {
                i2 = 160;
            }
            return (Integer.parseInt(str.substring(0, str.length() - 2)) * i2) / FrameworkStatsLog.f418x97ec91aa;
        }
        return Integer.parseInt(str);
    }

    public final int runDisplayUserRotation(PrintWriter printWriter) {
        int i;
        int parseInt;
        String nextArg = getNextArg();
        if (nextArg == null) {
            return printDisplayUserRotation(printWriter, 0);
        }
        if ("-d".equals(nextArg)) {
            i = Integer.parseInt(getNextArgRequired());
            nextArg = getNextArg();
        } else {
            i = 0;
        }
        if (nextArg == null) {
            return printDisplayUserRotation(printWriter, i);
        }
        if ("free".equals(nextArg)) {
            this.mInternal.thawDisplayRotation(i);
            return 0;
        } else if (!"lock".equals(nextArg)) {
            getErrPrintWriter().println("Error: argument needs to be either -d, free or lock.");
            return -1;
        } else {
            String nextArg2 = getNextArg();
            if (nextArg2 != null) {
                try {
                    parseInt = Integer.parseInt(nextArg2);
                } catch (IllegalArgumentException e) {
                    getErrPrintWriter().println("Error: " + e.getMessage());
                    return -1;
                }
            } else {
                parseInt = -1;
            }
            this.mInternal.freezeDisplayRotation(i, parseInt);
            return 0;
        }
    }

    public final int printDisplayUserRotation(PrintWriter printWriter, int i) {
        int displayUserRotation = this.mInternal.getDisplayUserRotation(i);
        if (displayUserRotation < 0) {
            getErrPrintWriter().println("Error: check logcat for more details.");
            return -1;
        } else if (!this.mInternal.isDisplayRotationFrozen(i)) {
            printWriter.println("free");
            return 0;
        } else {
            printWriter.print("lock ");
            printWriter.println(displayUserRotation);
            return 0;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runFixedToUserRotation(PrintWriter printWriter) throws RemoteException {
        int i;
        boolean z;
        String nextArg = getNextArg();
        if (nextArg == null) {
            printFixedToUserRotation(printWriter, 0);
            return 0;
        }
        if ("-d".equals(nextArg)) {
            i = Integer.parseInt(getNextArgRequired());
            nextArg = getNextArg();
        } else {
            i = 0;
        }
        if (nextArg == null) {
            return printFixedToUserRotation(printWriter, i);
        }
        int i2 = 2;
        switch (nextArg.hashCode()) {
            case -1609594047:
                if (nextArg.equals("enabled")) {
                    z = false;
                    break;
                }
                z = true;
                break;
            case 270940796:
                if (nextArg.equals("disabled")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            case 1544803905:
                if (nextArg.equals("default")) {
                    z = true;
                    break;
                }
                z = true;
                break;
            default:
                z = true;
                break;
        }
        switch (z) {
            case false:
                break;
            case true:
                i2 = 1;
                break;
            case true:
                i2 = 0;
                break;
            default:
                getErrPrintWriter().println("Error: expecting enabled, disabled or default, but we get " + nextArg);
                return -1;
        }
        this.mInterface.setFixedToUserRotation(i, i2);
        return 0;
    }

    public final int printFixedToUserRotation(PrintWriter printWriter, int i) {
        int fixedToUserRotation = this.mInternal.getFixedToUserRotation(i);
        if (fixedToUserRotation == 0) {
            printWriter.println("default");
            return 0;
        } else if (fixedToUserRotation == 1) {
            printWriter.println("disabled");
            return 0;
        } else if (fixedToUserRotation == 2) {
            printWriter.println("enabled");
            return 0;
        } else {
            getErrPrintWriter().println("Error: check logcat for more details.");
            return -1;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSetIgnoreOrientationRequest(PrintWriter printWriter) throws RemoteException {
        int i;
        char c;
        String nextArgRequired = getNextArgRequired();
        if ("-d".equals(nextArgRequired)) {
            i = Integer.parseInt(getNextArgRequired());
            nextArgRequired = getNextArgRequired();
        } else {
            i = 0;
        }
        nextArgRequired.hashCode();
        boolean z = true;
        switch (nextArgRequired.hashCode()) {
            case 48:
                if (nextArgRequired.equals("0")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (nextArgRequired.equals("1")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (nextArgRequired.equals("true")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (nextArgRequired.equals("false")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 3:
                z = false;
                break;
            case 1:
            case 2:
                break;
            default:
                getErrPrintWriter().println("Error: expecting true, 1, false, 0, but we get " + nextArgRequired);
                return -1;
        }
        this.mInterface.setIgnoreOrientationRequest(i, z);
        return 0;
    }

    public final int runGetIgnoreOrientationRequest(PrintWriter printWriter) throws RemoteException {
        int parseInt = "-d".equals(getNextArg()) ? Integer.parseInt(getNextArgRequired()) : 0;
        boolean ignoreOrientationRequest = this.mInternal.getIgnoreOrientationRequest(parseInt);
        printWriter.println("ignoreOrientationRequest " + ignoreOrientationRequest + " for displayId=" + parseInt);
        return 0;
    }

    public final int runDumpVisibleWindowViews(PrintWriter printWriter) {
        if (!this.mInternal.checkCallingPermission("android.permission.DUMP", "runDumpVisibleWindowViews()")) {
            throw new SecurityException("Requires DUMP permission");
        }
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(getRawOutputStream());
            final ArrayList arrayList = new ArrayList();
            synchronized (this.mInternal.mGlobalLock) {
                WindowManagerService.boostPriorityForLockedSection();
                RecentTasks recentTasks = this.mInternal.mAtmService.getRecentTasks();
                final int recentsComponentUid = recentTasks != null ? recentTasks.getRecentsComponentUid() : -1;
                this.mInternal.mRoot.forAllWindows(new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        WindowManagerShellCommand.lambda$runDumpVisibleWindowViews$1(recentsComponentUid, arrayList, (WindowState) obj);
                    }
                }, false);
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Pair pair = (Pair) it.next();
                try {
                    byte[] bArr = ((ByteTransferPipe) pair.second).get();
                    zipOutputStream.putNextEntry(new ZipEntry((String) pair.first));
                    zipOutputStream.write(bArr);
                } catch (IOException unused) {
                }
            }
            zipOutputStream.close();
        } catch (IOException e) {
            printWriter.println("Error fetching dump " + e.getMessage());
        }
        return 0;
    }

    public static /* synthetic */ void lambda$runDumpVisibleWindowViews$1(int i, ArrayList arrayList, final WindowState windowState) {
        boolean z = windowState.getUid() == i;
        if (!windowState.isVisible() && !z) {
            return;
        }
        ByteTransferPipe byteTransferPipe = null;
        try {
            ByteTransferPipe byteTransferPipe2 = new ByteTransferPipe();
            try {
                final ParcelFileDescriptor writeFd = byteTransferPipe2.getWriteFd();
                if (windowState.isClientLocal()) {
                    IoThread.getExecutor().execute(new Runnable() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda9
                        @Override // java.lang.Runnable
                        public final void run() {
                            WindowManagerShellCommand.lambda$runDumpVisibleWindowViews$0(WindowState.this, writeFd);
                        }
                    });
                } else {
                    windowState.mClient.executeCommand("DUMP_ENCODED", (String) null, writeFd);
                }
                arrayList.add(Pair.create(windowState.getName(), byteTransferPipe2));
            } catch (RemoteException | IOException unused) {
                byteTransferPipe = byteTransferPipe2;
                if (byteTransferPipe != null) {
                    byteTransferPipe.kill();
                }
            }
        } catch (RemoteException | IOException unused2) {
        }
    }

    public static /* synthetic */ void lambda$runDumpVisibleWindowViews$0(WindowState windowState, ParcelFileDescriptor parcelFileDescriptor) {
        try {
            windowState.mClient.executeCommand("DUMP_ENCODED", (String) null, parcelFileDescriptor);
        } catch (RemoteException unused) {
        }
    }

    public final int runSetFixedOrientationLetterboxAspectRatio(PrintWriter printWriter) throws RemoteException {
        try {
            float parseFloat = Float.parseFloat(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setFixedOrientationLetterboxAspectRatio(parseFloat);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad aspect ratio format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: aspect ratio should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetDefaultMinAspectRatioForUnresizableApps(PrintWriter printWriter) throws RemoteException {
        try {
            float parseFloat = Float.parseFloat(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setDefaultMinAspectRatioForUnresizableApps(parseFloat);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad aspect ratio format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: aspect ratio should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetLetterboxActivityCornersRadius(PrintWriter printWriter) throws RemoteException {
        try {
            int parseInt = Integer.parseInt(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxActivityCornersRadius(parseInt);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad corners radius format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: corners radius should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetLetterboxBackgroundType(PrintWriter printWriter) throws RemoteException {
        boolean z;
        try {
            String nextArgRequired = getNextArgRequired();
            int i = 3;
            switch (nextArgRequired.hashCode()) {
                case -1700528003:
                    if (nextArgRequired.equals("app_color_background_floating")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case -231186968:
                    if (nextArgRequired.equals("app_color_background")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                case 1216433359:
                    if (nextArgRequired.equals("solid_color")) {
                        z = false;
                        break;
                    }
                    z = true;
                    break;
                case 1474694658:
                    if (nextArgRequired.equals("wallpaper")) {
                        z = true;
                        break;
                    }
                    z = true;
                    break;
                default:
                    z = true;
                    break;
            }
            if (!z) {
                i = 0;
            } else if (z) {
                i = 1;
            } else if (z) {
                i = 2;
            } else if (!z) {
                getErrPrintWriter().println("Error: 'solid_color', 'app_color_background' or 'wallpaper' should be provided as an argument");
                return -1;
            }
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxBackgroundType(i);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (IllegalArgumentException e) {
            getErrPrintWriter().println("Error: 'solid_color', 'app_color_background' or 'wallpaper' should be provided as an argument" + e);
            return -1;
        }
    }

    public final int runSetLetterboxBackgroundColorResource(PrintWriter printWriter) throws RemoteException {
        try {
            int identifier = this.mInternal.mContext.getResources().getIdentifier(getNextArgRequired(), "color", "com.android.internal");
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxBackgroundColorResourceId(identifier);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (Resources.NotFoundException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: color in '@android:color/resource_name' format should be provided as an argument " + e);
            return -1;
        }
    }

    public final int runSetLetterboxBackgroundColor(PrintWriter printWriter) throws RemoteException {
        try {
            Color valueOf = Color.valueOf(Color.parseColor(getNextArgRequired()));
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxBackgroundColor(valueOf);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (IllegalArgumentException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: color in #RRGGBB format should be provided as an argument " + e);
            return -1;
        }
    }

    public final int runSetLetterboxBackgroundWallpaperBlurRadius(PrintWriter printWriter) throws RemoteException {
        try {
            int parseInt = Integer.parseInt(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxBackgroundWallpaperBlurRadius(parseInt);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: blur radius format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: blur radius should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetLetterboxBackgroundWallpaperDarkScrimAlpha(PrintWriter printWriter) throws RemoteException {
        try {
            float parseFloat = Float.parseFloat(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxBackgroundWallpaperDarkScrimAlpha(parseFloat);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad alpha format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: alpha should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetLetterboxHorizontalPositionMultiplier(PrintWriter printWriter) throws RemoteException {
        try {
            float parseFloat = Float.parseFloat(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxHorizontalPositionMultiplier(parseFloat);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad multiplier format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: multiplier should be provided as an argument " + e2);
            return -1;
        }
    }

    public final int runSetLetterboxVerticalPositionMultiplier(PrintWriter printWriter) throws RemoteException {
        try {
            float parseFloat = Float.parseFloat(getNextArgRequired());
            synchronized (this.mInternal.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    this.mLetterboxConfiguration.setLetterboxVerticalPositionMultiplier(parseFloat);
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return 0;
        } catch (NumberFormatException e) {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: bad multiplier format " + e);
            return -1;
        } catch (IllegalArgumentException e2) {
            PrintWriter errPrintWriter2 = getErrPrintWriter();
            errPrintWriter2.println("Error: multiplier should be provided as an argument " + e2);
            return -1;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0054 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runSetLetterboxDefaultPositionForHorizontalReachability(PrintWriter printWriter) throws RemoteException {
        boolean z;
        try {
            String nextArgRequired = getNextArgRequired();
            int hashCode = nextArgRequired.hashCode();
            int i = 2;
            if (hashCode == -1364013995) {
                if (nextArgRequired.equals("center")) {
                    z = true;
                    if (z) {
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                    }
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            } else if (hashCode != 3317767) {
                if (hashCode == 108511772 && nextArgRequired.equals("right")) {
                    z = true;
                    if (z) {
                        i = 0;
                    } else if (z) {
                        i = 1;
                    } else if (!z) {
                        getErrPrintWriter().println("Error: 'left', 'center' or 'right' are expected as an argument");
                        return -1;
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            this.mLetterboxConfiguration.setDefaultPositionForHorizontalReachability(i);
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return 0;
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            } else {
                if (nextArgRequired.equals("left")) {
                    z = false;
                    if (z) {
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                    }
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            }
        } catch (IllegalArgumentException e) {
            getErrPrintWriter().println("Error: 'left', 'center' or 'right' are expected as an argument" + e);
            return -1;
        }
        getErrPrintWriter().println("Error: 'left', 'center' or 'right' are expected as an argument" + e);
        return -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003e  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x004e  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x0054 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runSetLetterboxDefaultPositionForVerticalReachability(PrintWriter printWriter) throws RemoteException {
        boolean z;
        try {
            String nextArgRequired = getNextArgRequired();
            int hashCode = nextArgRequired.hashCode();
            int i = 2;
            if (hashCode == -1383228885) {
                if (nextArgRequired.equals("bottom")) {
                    z = true;
                    if (z) {
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                    }
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            } else if (hashCode != -1364013995) {
                if (hashCode == 115029 && nextArgRequired.equals("top")) {
                    z = false;
                    if (z) {
                        i = 0;
                    } else if (z) {
                        i = 1;
                    } else if (!z) {
                        getErrPrintWriter().println("Error: 'top', 'center' or 'bottom' are expected as an argument");
                        return -1;
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            this.mLetterboxConfiguration.setDefaultPositionForVerticalReachability(i);
                        } catch (Throwable th) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return 0;
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            } else {
                if (nextArgRequired.equals("center")) {
                    z = true;
                    if (z) {
                    }
                    synchronized (this.mInternal.mGlobalLock) {
                    }
                }
                z = true;
                if (z) {
                }
                synchronized (this.mInternal.mGlobalLock) {
                }
            }
        } catch (IllegalArgumentException e) {
            getErrPrintWriter().println("Error: 'top', 'center' or 'bottom' are expected as an argument" + e);
            return -1;
        }
        getErrPrintWriter().println("Error: 'top', 'center' or 'bottom' are expected as an argument" + e);
        return -1;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public final int runSetBooleanFlag(PrintWriter printWriter, Consumer<Boolean> consumer) throws RemoteException {
        char c;
        String nextArg = getNextArg();
        if (nextArg == null) {
            getErrPrintWriter().println("Error: expected true, 1, false, 0, but got empty input.");
            return -1;
        }
        boolean z = true;
        switch (nextArg.hashCode()) {
            case 48:
                if (nextArg.equals("0")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 49:
                if (nextArg.equals("1")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3569038:
                if (nextArg.equals("true")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 97196323:
                if (nextArg.equals("false")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 3:
                z = false;
                break;
            case 1:
            case 2:
                break;
            default:
                getErrPrintWriter().println("Error: expected true, 1, false, 0, but got " + nextArg);
                return -1;
        }
        synchronized (this.mInternal.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                consumer.accept(Boolean.valueOf(z));
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:87:0x0120, code lost:
        if (r0.equals("--aspectRatio") == false) goto L9;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final int runSetLetterboxStyle(PrintWriter printWriter) throws RemoteException {
        if (peekNextArg() == null) {
            getErrPrintWriter().println("Error: No arguments provided.");
        }
        while (true) {
            char c = 0;
            if (peekNextArg() != null) {
                String nextArg = getNextArg();
                nextArg.hashCode();
                switch (nextArg.hashCode()) {
                    case -2007271181:
                        break;
                    case -1688278685:
                        if (nextArg.equals("--isEducationEnabled")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1440939136:
                        if (nextArg.equals("--verticalPositionMultiplier")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1310848756:
                        if (nextArg.equals("--defaultPositionForVerticalReachability")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1294369338:
                        if (nextArg.equals("--isDisplayAspectRatioEnabledForFixedOrientationLetterbox")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1264068297:
                        if (nextArg.equals("--isCameraCompatRefreshEnabled")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1052930822:
                        if (nextArg.equals("--defaultPositionForHorizontalReachability")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1009939225:
                        if (nextArg.equals("--cornerRadius")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    case -951337176:
                        if (nextArg.equals("--backgroundType")) {
                            c = '\b';
                            break;
                        }
                        c = 65535;
                        break;
                    case -911250737:
                        if (nextArg.equals("--isSplitScreenAspectRatioForUnresizableAppsEnabled")) {
                            c = '\t';
                            break;
                        }
                        c = 65535;
                        break;
                    case -335739429:
                        if (nextArg.equals("--wallpaperBlurRadius")) {
                            c = '\n';
                            break;
                        }
                        c = 65535;
                        break;
                    case -301215364:
                        if (nextArg.equals("--isHorizontalReachabilityEnabled")) {
                            c = 11;
                            break;
                        }
                        c = 65535;
                        break;
                    case -69722518:
                        if (nextArg.equals("--isVerticalReachabilityEnabled")) {
                            c = '\f';
                            break;
                        }
                        c = 65535;
                        break;
                    case 229853520:
                        if (nextArg.equals("--wallpaperDarkScrimAlpha")) {
                            c = '\r';
                            break;
                        }
                        c = 65535;
                        break;
                    case 304986101:
                        if (nextArg.equals("--isTranslucentLetterboxingEnabled")) {
                            c = 14;
                            break;
                        }
                        c = 65535;
                        break;
                    case 557317429:
                        if (nextArg.equals("--backgroundColor")) {
                            c = 15;
                            break;
                        }
                        c = 65535;
                        break;
                    case 935353942:
                        if (nextArg.equals("--isCameraCompatRefreshCycleThroughStopEnabled")) {
                            c = 16;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1033642083:
                        if (nextArg.equals("--backgroundColorResource")) {
                            c = 17;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1066804362:
                        if (nextArg.equals("--minAspectRatioForUnresizable")) {
                            c = 18;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1070248110:
                        if (nextArg.equals("--horizontalPositionMultiplier")) {
                            c = 19;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        runSetFixedOrientationLetterboxAspectRatio(printWriter);
                        break;
                    case 1:
                        final LetterboxConfiguration letterboxConfiguration = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda3
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setIsEducationEnabled(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case 2:
                        runSetLetterboxVerticalPositionMultiplier(printWriter);
                        break;
                    case 3:
                        runSetLetterboxDefaultPositionForVerticalReachability(printWriter);
                        break;
                    case 4:
                        final LetterboxConfiguration letterboxConfiguration2 = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration2);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda5
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setIsDisplayAspectRatioEnabledForFixedOrientationLetterbox(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case 5:
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda7
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                WindowManagerShellCommand.this.lambda$runSetLetterboxStyle$2((Boolean) obj);
                            }
                        });
                        break;
                    case 6:
                        runSetLetterboxDefaultPositionForHorizontalReachability(printWriter);
                        break;
                    case 7:
                        runSetLetterboxActivityCornersRadius(printWriter);
                        break;
                    case '\b':
                        runSetLetterboxBackgroundType(printWriter);
                        break;
                    case '\t':
                        final LetterboxConfiguration letterboxConfiguration3 = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration3);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda4
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setIsSplitScreenAspectRatioForUnresizableAppsEnabled(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case '\n':
                        runSetLetterboxBackgroundWallpaperBlurRadius(printWriter);
                        break;
                    case 11:
                        final LetterboxConfiguration letterboxConfiguration4 = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration4);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda1
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setIsHorizontalReachabilityEnabled(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case '\f':
                        final LetterboxConfiguration letterboxConfiguration5 = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration5);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda2
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setIsVerticalReachabilityEnabled(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case '\r':
                        runSetLetterboxBackgroundWallpaperDarkScrimAlpha(printWriter);
                        break;
                    case 14:
                        final LetterboxConfiguration letterboxConfiguration6 = this.mLetterboxConfiguration;
                        Objects.requireNonNull(letterboxConfiguration6);
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda6
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                LetterboxConfiguration.this.setTranslucentLetterboxingOverrideEnabled(((Boolean) obj).booleanValue());
                            }
                        });
                        break;
                    case 15:
                        runSetLetterboxBackgroundColor(printWriter);
                        break;
                    case 16:
                        runSetBooleanFlag(printWriter, new Consumer() { // from class: com.android.server.wm.WindowManagerShellCommand$$ExternalSyntheticLambda8
                            @Override // java.util.function.Consumer
                            public final void accept(Object obj) {
                                WindowManagerShellCommand.this.lambda$runSetLetterboxStyle$3((Boolean) obj);
                            }
                        });
                        break;
                    case 17:
                        runSetLetterboxBackgroundColorResource(printWriter);
                        break;
                    case 18:
                        runSetDefaultMinAspectRatioForUnresizableApps(printWriter);
                        break;
                    case 19:
                        runSetLetterboxHorizontalPositionMultiplier(printWriter);
                        break;
                    default:
                        getErrPrintWriter().println("Error: Unrecognized letterbox style option: " + nextArg);
                        return -1;
                }
            } else {
                return 0;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runSetLetterboxStyle$2(Boolean bool) {
        this.mLetterboxConfiguration.setCameraCompatRefreshEnabled(bool.booleanValue());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runSetLetterboxStyle$3(Boolean bool) {
        this.mLetterboxConfiguration.setCameraCompatRefreshCycleThroughStopEnabled(bool.booleanValue());
    }

    public final int runResetLetterboxStyle(PrintWriter printWriter) throws RemoteException {
        if (peekNextArg() == null) {
            resetLetterboxStyle();
        }
        synchronized (this.mInternal.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                while (true) {
                    char c = 0;
                    if (peekNextArg() == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return 0;
                    }
                    String nextArg = getNextArg();
                    switch (nextArg.hashCode()) {
                        case -2064669968:
                            if (nextArg.equals("wallpaperDarkScrimAlpha")) {
                                c = 6;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1229148346:
                            if (nextArg.equals("IsDisplayAspectRatioEnabledForFixedOrientationLetterbox")) {
                                c = 15;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1135892646:
                            if (nextArg.equals("defaultPositionForHorizontalReachability")) {
                                c = 11;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1124529213:
                            if (nextArg.equals("isEducationEnabled")) {
                                c = '\r';
                                break;
                            }
                            c = 65535;
                            break;
                        case -567226646:
                            if (nextArg.equals("minAspectRatioForUnresizable")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case -563782898:
                            if (nextArg.equals("horizontalPositionMultiplier")) {
                                c = 7;
                                break;
                            }
                            c = 65535;
                            break;
                        case -559641828:
                            if (nextArg.equals("isHorizontalReachabilityEnabled")) {
                                c = '\t';
                                break;
                            }
                            c = 65535;
                            break;
                        case -39374981:
                            if (nextArg.equals("wallpaperBlurRadius")) {
                                c = 5;
                                break;
                            }
                            c = 65535;
                            break;
                        case 208616300:
                            if (nextArg.equals("defaultPositionForVerticalReachability")) {
                                c = '\f';
                                break;
                            }
                            c = 65535;
                            break;
                        case 583595847:
                            if (nextArg.equals("cornerRadius")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case 814923786:
                            if (nextArg.equals("isVerticalReachabilityEnabled")) {
                                c = '\n';
                                break;
                            }
                            c = 65535;
                            break;
                        case 883700309:
                            if (nextArg.equals("isTranslucentLetterboxingEnabled")) {
                                c = 16;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1092174483:
                            if (nextArg.equals("aspectRatio")) {
                                break;
                            }
                            c = 65535;
                            break;
                        case 1109312992:
                            if (nextArg.equals("verticalPositionMultiplier")) {
                                c = '\b';
                                break;
                            }
                            c = 65535;
                            break;
                        case 1287124693:
                            if (nextArg.equals("backgroundColor")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1396867991:
                            if (nextArg.equals("isCameraCompatRefreshEnabled")) {
                                c = 17;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1427509640:
                            if (nextArg.equals("backgroundType")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1869151343:
                            if (nextArg.equals("isSplitScreenAspectRatioForUnresizableAppsEnabled")) {
                                c = 14;
                                break;
                            }
                            c = 65535;
                            break;
                        case 1870284982:
                            if (nextArg.equals("isCameraCompatRefreshCycleThroughStopEnabled")) {
                                c = 18;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            this.mLetterboxConfiguration.resetFixedOrientationLetterboxAspectRatio();
                            break;
                        case 1:
                            this.mLetterboxConfiguration.resetDefaultMinAspectRatioForUnresizableApps();
                            break;
                        case 2:
                            this.mLetterboxConfiguration.resetLetterboxActivityCornersRadius();
                            break;
                        case 3:
                            this.mLetterboxConfiguration.resetLetterboxBackgroundType();
                            break;
                        case 4:
                            this.mLetterboxConfiguration.resetLetterboxBackgroundColor();
                            break;
                        case 5:
                            this.mLetterboxConfiguration.resetLetterboxBackgroundWallpaperBlurRadius();
                            break;
                        case 6:
                            this.mLetterboxConfiguration.resetLetterboxBackgroundWallpaperDarkScrimAlpha();
                            break;
                        case 7:
                            this.mLetterboxConfiguration.resetLetterboxHorizontalPositionMultiplier();
                            break;
                        case '\b':
                            this.mLetterboxConfiguration.resetLetterboxVerticalPositionMultiplier();
                            break;
                        case '\t':
                            this.mLetterboxConfiguration.resetIsHorizontalReachabilityEnabled();
                            break;
                        case '\n':
                            this.mLetterboxConfiguration.resetIsVerticalReachabilityEnabled();
                            break;
                        case 11:
                            this.mLetterboxConfiguration.resetDefaultPositionForHorizontalReachability();
                            break;
                        case '\f':
                            this.mLetterboxConfiguration.resetDefaultPositionForVerticalReachability();
                            break;
                        case '\r':
                            this.mLetterboxConfiguration.resetIsEducationEnabled();
                            break;
                        case 14:
                            this.mLetterboxConfiguration.resetIsSplitScreenAspectRatioForUnresizableAppsEnabled();
                            break;
                        case 15:
                            this.mLetterboxConfiguration.resetIsDisplayAspectRatioEnabledForFixedOrientationLetterbox();
                            break;
                        case 16:
                            this.mLetterboxConfiguration.resetTranslucentLetterboxingEnabled();
                            break;
                        case 17:
                            this.mLetterboxConfiguration.resetCameraCompatRefreshEnabled();
                            break;
                        case 18:
                            this.mLetterboxConfiguration.resetCameraCompatRefreshCycleThroughStopEnabled();
                            break;
                        default:
                            getErrPrintWriter().println("Error: Unrecognized letterbox style option: " + nextArg);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return -1;
                    }
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public final int runSetMultiWindowConfig() {
        int runSetSupportsNonResizableMultiWindow;
        if (peekNextArg() == null) {
            getErrPrintWriter().println("Error: No arguments provided.");
        }
        int i = 0;
        while (peekNextArg() != null) {
            String nextArg = getNextArg();
            nextArg.hashCode();
            if (nextArg.equals("--supportsNonResizable")) {
                runSetSupportsNonResizableMultiWindow = runSetSupportsNonResizableMultiWindow();
            } else if (nextArg.equals("--respectsActivityMinWidthHeight")) {
                runSetSupportsNonResizableMultiWindow = runSetRespectsActivityMinWidthHeightMultiWindow();
            } else {
                getErrPrintWriter().println("Error: Unrecognized multi window option: " + nextArg);
                return -1;
            }
            i += runSetSupportsNonResizableMultiWindow;
        }
        return i == 0 ? 0 : -1;
    }

    public final int runSetSupportsNonResizableMultiWindow() {
        String nextArg = getNextArg();
        if (!nextArg.equals("-1") && !nextArg.equals("0") && !nextArg.equals("1")) {
            getErrPrintWriter().println("Error: a config value of [-1, 0, 1] must be provided as an argument for supportsNonResizableMultiWindow");
            return -1;
        }
        int parseInt = Integer.parseInt(nextArg);
        synchronized (this.mInternal.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mInternal.mAtmService.mSupportsNonResizableMultiWindow = parseInt;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    public final int runSetRespectsActivityMinWidthHeightMultiWindow() {
        String nextArg = getNextArg();
        if (!nextArg.equals("-1") && !nextArg.equals("0") && !nextArg.equals("1")) {
            getErrPrintWriter().println("Error: a config value of [-1, 0, 1] must be provided as an argument for respectsActivityMinWidthHeightMultiWindow");
            return -1;
        }
        int parseInt = Integer.parseInt(nextArg);
        synchronized (this.mInternal.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mInternal.mAtmService.mRespectsActivityMinWidthHeightMultiWindow = parseInt;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    public final int runGetMultiWindowConfig(PrintWriter printWriter) {
        synchronized (this.mInternal.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                printWriter.println("Supports non-resizable in multi window: " + this.mInternal.mAtmService.mSupportsNonResizableMultiWindow);
                printWriter.println("Respects activity min width/height in multi window: " + this.mInternal.mAtmService.mRespectsActivityMinWidthHeightMultiWindow);
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    public final int runResetMultiWindowConfig() {
        int integer = this.mInternal.mContext.getResources().getInteger(17694972);
        int integer2 = this.mInternal.mContext.getResources().getInteger(17694937);
        synchronized (this.mInternal.mAtmService.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                ActivityTaskManagerService activityTaskManagerService = this.mInternal.mAtmService;
                activityTaskManagerService.mSupportsNonResizableMultiWindow = integer;
                activityTaskManagerService.mRespectsActivityMinWidthHeightMultiWindow = integer2;
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    public final void resetLetterboxStyle() {
        synchronized (this.mInternal.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                this.mLetterboxConfiguration.resetFixedOrientationLetterboxAspectRatio();
                this.mLetterboxConfiguration.resetDefaultMinAspectRatioForUnresizableApps();
                this.mLetterboxConfiguration.resetLetterboxActivityCornersRadius();
                this.mLetterboxConfiguration.resetLetterboxBackgroundType();
                this.mLetterboxConfiguration.resetLetterboxBackgroundColor();
                this.mLetterboxConfiguration.resetLetterboxBackgroundWallpaperBlurRadius();
                this.mLetterboxConfiguration.resetLetterboxBackgroundWallpaperDarkScrimAlpha();
                this.mLetterboxConfiguration.resetLetterboxHorizontalPositionMultiplier();
                this.mLetterboxConfiguration.resetIsHorizontalReachabilityEnabled();
                this.mLetterboxConfiguration.resetIsVerticalReachabilityEnabled();
                this.mLetterboxConfiguration.resetDefaultPositionForHorizontalReachability();
                this.mLetterboxConfiguration.resetDefaultPositionForVerticalReachability();
                this.mLetterboxConfiguration.resetIsEducationEnabled();
                this.mLetterboxConfiguration.resetIsSplitScreenAspectRatioForUnresizableAppsEnabled();
                this.mLetterboxConfiguration.resetIsDisplayAspectRatioEnabledForFixedOrientationLetterbox();
                this.mLetterboxConfiguration.resetTranslucentLetterboxingEnabled();
                this.mLetterboxConfiguration.resetCameraCompatRefreshEnabled();
                this.mLetterboxConfiguration.resetCameraCompatRefreshCycleThroughStopEnabled();
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
    }

    public final int runGetLetterboxStyle(PrintWriter printWriter) throws RemoteException {
        synchronized (this.mInternal.mGlobalLock) {
            try {
                WindowManagerService.boostPriorityForLockedSection();
                printWriter.println("Corner radius: " + this.mLetterboxConfiguration.getLetterboxActivityCornersRadius());
                printWriter.println("Horizontal position multiplier: " + this.mLetterboxConfiguration.getLetterboxHorizontalPositionMultiplier(false));
                printWriter.println("Vertical position multiplier: " + this.mLetterboxConfiguration.getLetterboxVerticalPositionMultiplier(false));
                printWriter.println("Horizontal position multiplier (book mode): " + this.mLetterboxConfiguration.getLetterboxHorizontalPositionMultiplier(true));
                printWriter.println("Vertical position multiplier (tabletop mode): " + this.mLetterboxConfiguration.getLetterboxVerticalPositionMultiplier(true));
                printWriter.println("Aspect ratio: " + this.mLetterboxConfiguration.getFixedOrientationLetterboxAspectRatio());
                printWriter.println("Default min aspect ratio for unresizable apps: " + this.mLetterboxConfiguration.getDefaultMinAspectRatioForUnresizableApps());
                printWriter.println("Is horizontal reachability enabled: " + this.mLetterboxConfiguration.getIsHorizontalReachabilityEnabled());
                printWriter.println("Is vertical reachability enabled: " + this.mLetterboxConfiguration.getIsVerticalReachabilityEnabled());
                printWriter.println("Default position for horizontal reachability: " + LetterboxConfiguration.letterboxHorizontalReachabilityPositionToString(this.mLetterboxConfiguration.getDefaultPositionForHorizontalReachability()));
                printWriter.println("Default position for vertical reachability: " + LetterboxConfiguration.letterboxVerticalReachabilityPositionToString(this.mLetterboxConfiguration.getDefaultPositionForVerticalReachability()));
                printWriter.println("Current position for horizontal reachability:" + LetterboxConfiguration.letterboxHorizontalReachabilityPositionToString(this.mLetterboxConfiguration.getLetterboxPositionForHorizontalReachability(false)));
                printWriter.println("Current position for vertical reachability:" + LetterboxConfiguration.letterboxVerticalReachabilityPositionToString(this.mLetterboxConfiguration.getLetterboxPositionForVerticalReachability(false)));
                printWriter.println("Is education enabled: " + this.mLetterboxConfiguration.getIsEducationEnabled());
                printWriter.println("Is using split screen aspect ratio as aspect ratio for unresizable apps: " + this.mLetterboxConfiguration.getIsSplitScreenAspectRatioForUnresizableAppsEnabled());
                printWriter.println("Is using display aspect ratio as aspect ratio for all letterboxed apps: " + this.mLetterboxConfiguration.getIsDisplayAspectRatioEnabledForFixedOrientationLetterbox());
                printWriter.println("    Is activity \"refresh\" in camera compatibility treatment enabled: " + this.mLetterboxConfiguration.isCameraCompatRefreshEnabled());
                printWriter.println("    Refresh using \"stopped -> resumed\" cycle: " + this.mLetterboxConfiguration.isCameraCompatRefreshCycleThroughStopEnabled());
                printWriter.println("Background type: " + LetterboxConfiguration.letterboxBackgroundTypeToString(this.mLetterboxConfiguration.getLetterboxBackgroundType()));
                printWriter.println("    Background color: " + Integer.toHexString(this.mLetterboxConfiguration.getLetterboxBackgroundColor().toArgb()));
                printWriter.println("    Wallpaper blur radius: " + this.mLetterboxConfiguration.getLetterboxBackgroundWallpaperBlurRadius());
                printWriter.println("    Wallpaper dark scrim alpha: " + this.mLetterboxConfiguration.getLetterboxBackgroundWallpaperDarkScrimAlpha());
                if (this.mLetterboxConfiguration.isTranslucentLetterboxingEnabled()) {
                    printWriter.println("Letterboxing for translucent activities: enabled");
                } else {
                    printWriter.println("Letterboxing for translucent activities: disabled");
                }
            } catch (Throwable th) {
                WindowManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
        WindowManagerService.resetPriorityAfterLockedSection();
        return 0;
    }

    public final int runWmShellCommand(PrintWriter printWriter) {
        char c;
        String nextArg = getNextArg();
        int hashCode = nextArg.hashCode();
        if (hashCode != -1067396926) {
            if (hashCode == 3198785 && nextArg.equals("help")) {
                c = 1;
            }
            c = 65535;
        } else {
            if (nextArg.equals("tracing")) {
                c = 0;
            }
            c = 65535;
        }
        if (c == 0) {
            return runWmShellTracing(printWriter);
        }
        return runHelp(printWriter);
    }

    public final int runHelp(PrintWriter printWriter) {
        printWriter.println("Window Manager Shell commands:");
        printWriter.println("  help");
        printWriter.println("    Print this help text.");
        printWriter.println("  tracing <start/stop>");
        printWriter.println("    Start/stop shell transition tracing.");
        return 0;
    }

    public final int runWmShellTracing(PrintWriter printWriter) {
        String nextArg = getNextArg();
        nextArg.hashCode();
        if (nextArg.equals("stop")) {
            this.mInternal.mTransitionTracer.stopTrace(printWriter);
            return 0;
        } else if (nextArg.equals("start")) {
            this.mInternal.mTransitionTracer.startTrace(printWriter);
            return 0;
        } else {
            PrintWriter errPrintWriter = getErrPrintWriter();
            errPrintWriter.println("Error: expected 'start' or 'stop', but got '" + nextArg + "'");
            return -1;
        }
    }

    public final int runReset(PrintWriter printWriter) throws RemoteException {
        int displayId = getDisplayId(getNextArg());
        this.mInterface.clearForcedDisplaySize(displayId);
        this.mInterface.clearForcedDisplayDensityForUser(displayId, -2);
        this.mInternal.setOverrideFoldedArea(new Rect());
        this.mInterface.setForcedDisplayScalingMode(displayId, 0);
        this.mInternal.thawDisplayRotation(displayId);
        this.mInterface.setFixedToUserRotation(displayId, 0);
        this.mInterface.setIgnoreOrientationRequest(displayId, false);
        resetLetterboxStyle();
        this.mInternal.setSandboxDisplayApis(displayId, true);
        runResetMultiWindowConfig();
        printWriter.println("Reset all settings for displayId=" + displayId);
        return 0;
    }

    public void onHelp() {
        PrintWriter outPrintWriter = getOutPrintWriter();
        outPrintWriter.println("Window manager (window) commands:");
        outPrintWriter.println("  help");
        outPrintWriter.println("      Print this help text.");
        outPrintWriter.println("  size [reset|WxH|WdpxHdp] [-d DISPLAY_ID]");
        outPrintWriter.println("    Return or override display size.");
        outPrintWriter.println("    width and height in pixels unless suffixed with 'dp'.");
        outPrintWriter.println("  density [reset|DENSITY] [-d DISPLAY_ID] [-u UNIQUE_ID]");
        outPrintWriter.println("    Return or override display density.");
        outPrintWriter.println("  folded-area [reset|LEFT,TOP,RIGHT,BOTTOM]");
        outPrintWriter.println("    Return or override folded area.");
        outPrintWriter.println("  scaling [off|auto] [-d DISPLAY_ID]");
        outPrintWriter.println("    Set display scaling mode.");
        outPrintWriter.println("  dismiss-keyguard");
        outPrintWriter.println("    Dismiss the keyguard, prompting user for auth if necessary.");
        outPrintWriter.println("  disable-blur [true|1|false|0]");
        outPrintWriter.println("  user-rotation [-d DISPLAY_ID] [free|lock] [rotation]");
        outPrintWriter.println("    Print or set user rotation mode and user rotation.");
        outPrintWriter.println("  dump-visible-window-views");
        outPrintWriter.println("    Dumps the encoded view hierarchies of visible windows");
        outPrintWriter.println("  fixed-to-user-rotation [-d DISPLAY_ID] [enabled|disabled|default]");
        outPrintWriter.println("    Print or set rotating display for app requested orientation.");
        outPrintWriter.println("  set-ignore-orientation-request [-d DISPLAY_ID] [true|1|false|0]");
        outPrintWriter.println("  get-ignore-orientation-request [-d DISPLAY_ID] ");
        outPrintWriter.println("    If app requested orientation should be ignored.");
        outPrintWriter.println("  set-sandbox-display-apis [true|1|false|0]");
        outPrintWriter.println("    Sets override of Display APIs getRealSize / getRealMetrics to reflect ");
        outPrintWriter.println("    DisplayArea of the activity, or the window bounds if in letterbox or");
        outPrintWriter.println("    Size Compat Mode.");
        printLetterboxHelp(outPrintWriter);
        printMultiWindowConfigHelp(outPrintWriter);
        outPrintWriter.println("  reset [-d DISPLAY_ID]");
        outPrintWriter.println("    Reset all override settings.");
        if (Build.IS_USER) {
            return;
        }
        outPrintWriter.println("  tracing (start | stop)");
        outPrintWriter.println("    Start or stop window tracing.");
        outPrintWriter.println("  logging (start | stop | enable | disable | enable-text | disable-text)");
        outPrintWriter.println("    Logging settings.");
    }

    public final void printLetterboxHelp(PrintWriter printWriter) {
        printWriter.println("  set-letterbox-style");
        printWriter.println("    Sets letterbox style using the following options:");
        printWriter.println("      --aspectRatio aspectRatio");
        printWriter.println("        Aspect ratio of letterbox for fixed orientation. If aspectRatio <= 1.0");
        printWriter.println("        both it and R.dimen.config_fixedOrientationLetterboxAspectRatio will");
        printWriter.println("        be ignored and framework implementation will determine aspect ratio.");
        printWriter.println("      --minAspectRatioForUnresizable aspectRatio");
        printWriter.println("        Default min aspect ratio for unresizable apps which is used when an");
        printWriter.println("        app is eligible for the size compat mode.  If aspectRatio <= 1.0");
        printWriter.println("        both it and R.dimen.config_fixedOrientationLetterboxAspectRatio will");
        printWriter.println("        be ignored and framework implementation will determine aspect ratio.");
        printWriter.println("      --cornerRadius radius");
        printWriter.println("        Corners radius for activities in the letterbox mode. If radius < 0,");
        printWriter.println("        both it and R.integer.config_letterboxActivityCornersRadius will be");
        printWriter.println("        ignored and corners of the activity won't be rounded.");
        printWriter.println("      --backgroundType [reset|solid_color|app_color_background");
        printWriter.println("          |app_color_background_floating|wallpaper]");
        printWriter.println("        Type of background used in the letterbox mode.");
        printWriter.println("      --backgroundColor color");
        printWriter.println("        Color of letterbox which is be used when letterbox background type");
        printWriter.println("        is 'solid-color'. Use (set)get-letterbox-style to check and control");
        printWriter.println("        letterbox background type. See Color#parseColor for allowed color");
        printWriter.println("        formats (#RRGGBB and some colors by name, e.g. magenta or olive).");
        printWriter.println("      --backgroundColorResource resource_name");
        printWriter.println("        Color resource name of letterbox background which is used when");
        printWriter.println("        background type is 'solid-color'. Use (set)get-letterbox-style to");
        printWriter.println("        check and control background type. Parameter is a color resource");
        printWriter.println("        name, for example, @android:color/system_accent2_50.");
        printWriter.println("      --wallpaperBlurRadius radius");
        printWriter.println("        Blur radius for 'wallpaper' letterbox background. If radius <= 0");
        printWriter.println("        both it and R.dimen.config_letterboxBackgroundWallpaperBlurRadius");
        printWriter.println("        are ignored and 0 is used.");
        printWriter.println("      --wallpaperDarkScrimAlpha alpha");
        printWriter.println("        Alpha of a black translucent scrim shown over 'wallpaper'");
        printWriter.println("        letterbox background. If alpha < 0 or >= 1 both it and");
        printWriter.println("        R.dimen.config_letterboxBackgroundWallaperDarkScrimAlpha are ignored");
        printWriter.println("        and 0.0 (transparent) is used instead.");
        printWriter.println("      --horizontalPositionMultiplier multiplier");
        printWriter.println("        Horizontal position of app window center. If multiplier < 0 or > 1,");
        printWriter.println("        both it and R.dimen.config_letterboxHorizontalPositionMultiplier");
        printWriter.println("        are ignored and central position (0.5) is used.");
        printWriter.println("      --verticalPositionMultiplier multiplier");
        printWriter.println("        Vertical position of app window center. If multiplier < 0 or > 1,");
        printWriter.println("        both it and R.dimen.config_letterboxVerticalPositionMultiplier");
        printWriter.println("        are ignored and central position (0.5) is used.");
        printWriter.println("      --isHorizontalReachabilityEnabled [true|1|false|0]");
        printWriter.println("        Whether horizontal reachability repositioning is allowed for ");
        printWriter.println("        letterboxed fullscreen apps in landscape device orientation.");
        printWriter.println("      --isVerticalReachabilityEnabled [true|1|false|0]");
        printWriter.println("        Whether vertical reachability repositioning is allowed for ");
        printWriter.println("        letterboxed fullscreen apps in portrait device orientation.");
        printWriter.println("      --defaultPositionForHorizontalReachability [left|center|right]");
        printWriter.println("        Default position of app window when horizontal reachability is.");
        printWriter.println("        enabled.");
        printWriter.println("      --defaultPositionForVerticalReachability [top|center|bottom]");
        printWriter.println("        Default position of app window when vertical reachability is.");
        printWriter.println("        enabled.");
        printWriter.println("      --isEducationEnabled [true|1|false|0]");
        printWriter.println("        Whether education is allowed for letterboxed fullscreen apps.");
        printWriter.println("      --isSplitScreenAspectRatioForUnresizableAppsEnabled [true|1|false|0]");
        printWriter.println("        Whether using split screen aspect ratio as a default aspect ratio for");
        printWriter.println("        unresizable apps.");
        printWriter.println("      --isTranslucentLetterboxingEnabled [true|1|false|0]");
        printWriter.println("        Whether letterboxing for translucent activities is enabled.");
        printWriter.println("      --isCameraCompatRefreshEnabled [true|1|false|0]");
        printWriter.println("        Whether camera compatibility refresh is enabled.");
        printWriter.println("      --isCameraCompatRefreshCycleThroughStopEnabled [true|1|false|0]");
        printWriter.println("        Whether activity \"refresh\" in camera compatibility treatment should");
        printWriter.println("        happen using the \"stopped -> resumed\" cycle rather than");
        printWriter.println("        \"paused -> resumed\" cycle.");
        printWriter.println("  reset-letterbox-style [aspectRatio|cornerRadius|backgroundType");
        printWriter.println("      |backgroundColor|wallpaperBlurRadius|wallpaperDarkScrimAlpha");
        printWriter.println("      |horizontalPositionMultiplier|verticalPositionMultiplier");
        printWriter.println("      |isHorizontalReachabilityEnabled|isVerticalReachabilityEnabled");
        printWriter.println("      |isEducationEnabled||defaultPositionMultiplierForHorizontalReachability");
        printWriter.println("      |isTranslucentLetterboxingEnabled");
        printWriter.println("      |defaultPositionMultiplierForVerticalReachability]");
        printWriter.println("    Resets overrides to default values for specified properties separated");
        printWriter.println("    by space, e.g. 'reset-letterbox-style aspectRatio cornerRadius'.");
        printWriter.println("    If no arguments provided, all values will be reset.");
        printWriter.println("  get-letterbox-style");
        printWriter.println("    Prints letterbox style configuration.");
    }

    public final void printMultiWindowConfigHelp(PrintWriter printWriter) {
        printWriter.println("  set-multi-window-config");
        printWriter.println("    Sets options to determine if activity should be shown in multi window:");
        printWriter.println("      --supportsNonResizable [configValue]");
        printWriter.println("        Whether the device supports non-resizable activity in multi window.");
        printWriter.println("        -1: The device doesn't support non-resizable in multi window.");
        printWriter.println("         0: The device supports non-resizable in multi window only if");
        printWriter.println("            this is a large screen device.");
        printWriter.println("         1: The device always supports non-resizable in multi window.");
        printWriter.println("      --respectsActivityMinWidthHeight [configValue]");
        printWriter.println("        Whether the device checks the activity min width/height to determine ");
        printWriter.println("        if it can be shown in multi window.");
        printWriter.println("        -1: The device ignores the activity min width/height when determining");
        printWriter.println("            if it can be shown in multi window.");
        printWriter.println("         0: If this is a small screen, the device compares the activity min");
        printWriter.println("            width/height with the min multi window modes dimensions");
        printWriter.println("            the device supports to determine if the activity can be shown in");
        printWriter.println("            multi window.");
        printWriter.println("         1: The device always compare the activity min width/height with the");
        printWriter.println("            min multi window dimensions the device supports to determine if");
        printWriter.println("            the activity can be shown in multi window.");
        printWriter.println("  get-multi-window-config");
        printWriter.println("    Prints values of the multi window config options.");
        printWriter.println("  reset-multi-window-config");
        printWriter.println("    Resets overrides to default values of the multi window config options.");
    }
}
