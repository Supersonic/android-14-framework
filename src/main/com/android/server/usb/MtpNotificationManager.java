package com.android.server.usb;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.os.UserHandle;
import com.android.internal.notification.SystemNotificationChannels;
/* loaded from: classes2.dex */
public class MtpNotificationManager {
    public final Context mContext;
    public final OnOpenInAppListener mListener;
    public final Receiver mReceiver;

    /* loaded from: classes2.dex */
    public interface OnOpenInAppListener {
        void onOpenInApp(UsbDevice usbDevice);
    }

    public MtpNotificationManager(Context context, OnOpenInAppListener onOpenInAppListener) {
        this.mContext = context;
        this.mListener = onOpenInAppListener;
        Receiver receiver = new Receiver();
        this.mReceiver = receiver;
        context.registerReceiver(receiver, new IntentFilter("com.android.server.usb.ACTION_OPEN_IN_APPS"));
    }

    public void showNotification(UsbDevice usbDevice) {
        Resources resources = this.mContext.getResources();
        Notification.Builder category = new Notification.Builder(this.mContext, SystemNotificationChannels.USB).setContentTitle(resources.getString(17041690, usbDevice.getProductName())).setContentText(resources.getString(17041689)).setSmallIcon(17303627).setCategory("sys");
        Intent intent = new Intent("com.android.server.usb.ACTION_OPEN_IN_APPS");
        intent.putExtra("device", usbDevice);
        intent.addFlags(1342177280);
        category.setContentIntent(PendingIntent.getBroadcastAsUser(this.mContext, usbDevice.getDeviceId(), intent, 201326592, UserHandle.SYSTEM));
        Notification build = category.build();
        build.flags |= 256;
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).notify(Integer.toString(usbDevice.getDeviceId()), 25, build);
    }

    public void hideNotification(int i) {
        ((NotificationManager) this.mContext.getSystemService(NotificationManager.class)).cancel(Integer.toString(i), 25);
    }

    /* loaded from: classes2.dex */
    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            UsbDevice usbDevice = (UsbDevice) intent.getExtras().getParcelable("device", UsbDevice.class);
            if (usbDevice == null) {
                return;
            }
            String action = intent.getAction();
            action.hashCode();
            if (action.equals("com.android.server.usb.ACTION_OPEN_IN_APPS")) {
                MtpNotificationManager.this.mListener.onOpenInApp(usbDevice);
            }
        }
    }

    public static boolean shouldShowNotification(PackageManager packageManager, UsbDevice usbDevice) {
        return !packageManager.hasSystemFeature("android.hardware.type.automotive") && isMtpDevice(usbDevice);
    }

    public static boolean isMtpDevice(UsbDevice usbDevice) {
        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            UsbInterface usbInterface = usbDevice.getInterface(i);
            if (usbInterface.getInterfaceClass() == 6 && usbInterface.getInterfaceSubclass() == 1 && usbInterface.getInterfaceProtocol() == 1) {
                return true;
            }
            if (usbInterface.getInterfaceClass() == 255 && usbInterface.getInterfaceSubclass() == 255 && usbInterface.getInterfaceProtocol() == 0 && "MTP".equals(usbInterface.getName())) {
                return true;
            }
        }
        return false;
    }

    public void unregister() {
        this.mContext.unregisterReceiver(this.mReceiver);
    }
}
