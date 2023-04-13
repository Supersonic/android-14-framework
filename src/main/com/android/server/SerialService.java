package com.android.server;

import android.annotation.EnforcePermission;
import android.content.Context;
import android.hardware.ISerialManager;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SerialService extends ISerialManager.Stub {
    public final Context mContext;
    public final String[] mSerialPorts;

    private native ParcelFileDescriptor native_open(String str);

    public SerialService(Context context) {
        this.mContext = context;
        this.mSerialPorts = context.getResources().getStringArray(17236136);
    }

    @EnforcePermission("android.permission.SERIAL_PORT")
    public String[] getSerialPorts() {
        super.getSerialPorts_enforcePermission();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            String[] strArr = this.mSerialPorts;
            if (i < strArr.length) {
                String str = strArr[i];
                if (new File(str).exists()) {
                    arrayList.add(str);
                }
                i++;
            } else {
                String[] strArr2 = new String[arrayList.size()];
                arrayList.toArray(strArr2);
                return strArr2;
            }
        }
    }

    @EnforcePermission("android.permission.SERIAL_PORT")
    public ParcelFileDescriptor openSerialPort(String str) {
        super.openSerialPort_enforcePermission();
        int i = 0;
        while (true) {
            String[] strArr = this.mSerialPorts;
            if (i < strArr.length) {
                if (strArr[i].equals(str)) {
                    return native_open(str);
                }
                i++;
            } else {
                throw new IllegalArgumentException("Invalid serial port " + str);
            }
        }
    }
}
