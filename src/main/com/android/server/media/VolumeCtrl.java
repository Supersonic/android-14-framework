package com.android.server.media;

import android.media.AudioSystem;
import android.media.IAudioService;
import android.os.ServiceManager;
import android.util.AndroidException;
/* loaded from: classes2.dex */
public class VolumeCtrl {
    public static final String USAGE = new String("the options are as follows: \n\t\t--stream STREAM selects the stream to control, see AudioManager.STREAM_*\n\t\t                controls AudioManager.STREAM_MUSIC if no stream is specified\n\t\t--set INDEX     sets the volume index value\n\t\t--adj DIRECTION adjusts the volume, use raise|same|lower for the direction\n\t\t--get           outputs the current volume\n\t\t--show          shows the UI during the volume change\n\texamples:\n\t\tadb shell media volume --show --stream 3 --set 11\n\t\tadb shell media volume --stream 0 --adj lower\n\t\tadb shell media volume --stream 3 --get\n");

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0154  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01d8  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void run(MediaShellCommand mediaShellCommand) throws Exception {
        IAudioService asInterface;
        boolean z;
        int i = 5;
        int i2 = 0;
        String str = null;
        int i3 = 3;
        boolean z2 = false;
        int i4 = 0;
        boolean z3 = false;
        while (true) {
            String nextOption = mediaShellCommand.getNextOption();
            char c = 65535;
            if (nextOption != null) {
                switch (nextOption.hashCode()) {
                    case 42995463:
                        if (nextOption.equals("--adj")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 43001270:
                        if (nextOption.equals("--get")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 43012802:
                        if (nextOption.equals("--set")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1333399709:
                        if (nextOption.equals("--show")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 1508023584:
                        if (nextOption.equals("--stream")) {
                            c = 4;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        str = mediaShellCommand.getNextArgRequired();
                        mediaShellCommand.log("[V]", "will adjust volume");
                        z2 = true;
                        break;
                    case 1:
                        mediaShellCommand.log("[V]", "will get volume");
                        z3 = true;
                        break;
                    case 2:
                        i = Integer.decode(mediaShellCommand.getNextArgRequired()).intValue();
                        mediaShellCommand.log("[V]", "will set volume to index=" + i);
                        z2 = true;
                        break;
                    case 3:
                        i4 = 1;
                        break;
                    case 4:
                        i3 = Integer.decode(mediaShellCommand.getNextArgRequired()).intValue();
                        mediaShellCommand.log("[V]", "will control stream=" + i3 + " (" + streamName(i3) + ")");
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown argument " + nextOption);
                }
            } else {
                if (z2) {
                    if (str == null) {
                        mediaShellCommand.showError("Error: no valid volume adjustment (null)");
                        return;
                    }
                    switch (str.hashCode()) {
                        case 3522662:
                            if (str.equals("same")) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case 103164673:
                            if (str.equals("lower")) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        case 108275692:
                            if (str.equals("raise")) {
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
                            i2 = -1;
                            break;
                        case true:
                            break;
                        default:
                            mediaShellCommand.showError("Error: no valid volume adjustment, was " + str + ", expected lower|same|raise");
                            return;
                    }
                    mediaShellCommand.log("[V]", "Connecting to AudioService");
                    asInterface = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
                    if (asInterface != null) {
                        mediaShellCommand.log("[E]", "Error type 2");
                        throw new AndroidException("Can't connect to audio service; is the system running?");
                    } else if (z2 && (i > asInterface.getStreamMaxVolume(i3) || i < asInterface.getStreamMinVolume(i3))) {
                        mediaShellCommand.showError(String.format("Error: invalid volume index %d for stream %d (should be in [%d..%d])", Integer.valueOf(i), Integer.valueOf(i3), Integer.valueOf(asInterface.getStreamMinVolume(i3)), Integer.valueOf(asInterface.getStreamMaxVolume(i3))));
                        return;
                    } else {
                        String name = mediaShellCommand.getClass().getPackage().getName();
                        if (z2) {
                            asInterface.setStreamVolume(i3, i, i4, name);
                        } else if (z2) {
                            asInterface.adjustStreamVolume(i3, i2, i4, name);
                        }
                        if (z3) {
                            mediaShellCommand.log("[V]", "volume is " + asInterface.getStreamVolume(i3) + " in range [" + asInterface.getStreamMinVolume(i3) + ".." + asInterface.getStreamMaxVolume(i3) + "]");
                            return;
                        }
                        return;
                    }
                }
                i2 = 1;
                mediaShellCommand.log("[V]", "Connecting to AudioService");
                asInterface = IAudioService.Stub.asInterface(ServiceManager.checkService("audio"));
                if (asInterface != null) {
                }
            }
        }
    }

    public static String streamName(int i) {
        try {
            return AudioSystem.STREAM_NAMES[i];
        } catch (ArrayIndexOutOfBoundsException unused) {
            return "invalid stream";
        }
    }
}
