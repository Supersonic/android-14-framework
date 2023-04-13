package com.android.commands.p000vr;

import android.app.Vr2dDisplayProperties;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.vr.IVrManager;
import com.android.internal.os.BaseCommand;
import java.io.PrintStream;
/* renamed from: com.android.commands.vr.Vr */
/* loaded from: classes.dex */
public final class C0000Vr extends BaseCommand {
    private static final String COMMAND_ENABLE_VD = "enable-virtual-display";
    private static final String COMMAND_SET_PERSISTENT_VR_MODE_ENABLED = "set-persistent-vr-mode-enabled";
    private static final String COMMAND_SET_VR2D_DISPLAY_PROPERTIES = "set-display-props";
    private IVrManager mVrService;

    public static void main(String[] args) {
        new C0000Vr().run(args);
    }

    public void onShowUsage(PrintStream out) {
        out.println("usage: vr [subcommand]\nusage: vr set-persistent-vr-mode-enabled [true|false]\nusage: vr set-display-props [width] [height] [dpi]\nusage: vr enable-virtual-display [true|false]\n");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void onRun() throws Exception {
        char c;
        IVrManager asInterface = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        this.mVrService = asInterface;
        if (asInterface == null) {
            showError("Error: Could not access the Vr Manager. Is the system running?");
            return;
        }
        String command = nextArgRequired();
        switch (command.hashCode()) {
            case -190799946:
                if (command.equals(COMMAND_ENABLE_VD)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -111561094:
                if (command.equals(COMMAND_SET_VR2D_DISPLAY_PROPERTIES)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 2040743325:
                if (command.equals(COMMAND_SET_PERSISTENT_VR_MODE_ENABLED)) {
                    c = 1;
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
                runSetVr2dDisplayProperties();
                return;
            case 1:
                runSetPersistentVrModeEnabled();
                return;
            case 2:
                runEnableVd();
                return;
            default:
                throw new IllegalArgumentException("unknown command '" + command + "'");
        }
    }

    private void runSetVr2dDisplayProperties() throws RemoteException {
        String widthStr = nextArgRequired();
        int width = Integer.parseInt(widthStr);
        String heightStr = nextArgRequired();
        int height = Integer.parseInt(heightStr);
        String dpiStr = nextArgRequired();
        int dpi = Integer.parseInt(dpiStr);
        Vr2dDisplayProperties vr2dDisplayProperties = new Vr2dDisplayProperties(width, height, dpi);
        try {
            this.mVrService.setVr2dDisplayProperties(vr2dDisplayProperties);
        } catch (RemoteException re) {
            System.err.println("Error: Can't set persistent mode " + re);
        }
    }

    private void runEnableVd() throws RemoteException {
        Vr2dDisplayProperties.Builder builder = new Vr2dDisplayProperties.Builder();
        String value = nextArgRequired();
        if ("true".equals(value)) {
            builder.setEnabled(true);
        } else if ("false".equals(value)) {
            builder.setEnabled(false);
        }
        try {
            this.mVrService.setVr2dDisplayProperties(builder.build());
        } catch (RemoteException re) {
            System.err.println("Error: Can't enable (" + value + ") virtual display" + re);
        }
    }

    private void runSetPersistentVrModeEnabled() throws RemoteException {
        String enableStr = nextArg();
        boolean enabled = Boolean.parseBoolean(enableStr);
        try {
            this.mVrService.setPersistentVrModeEnabled(enabled);
        } catch (RemoteException re) {
            System.err.println("Error: Can't set persistent mode " + re);
        }
    }
}
