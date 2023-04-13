package android.mtp;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.util.Random;
import libcore.util.HexEncoding;
/* loaded from: classes2.dex */
public class MtpServer implements Runnable {
    private static final int sID_LEN_BYTES = 16;
    private static final int sID_LEN_STR = 32;
    private final Context mContext;
    private final MtpDatabase mDatabase;
    private long mNativeContext;
    private final Runnable mOnTerminate;

    private final native void native_add_storage(MtpStorage mtpStorage);

    private final native void native_cleanup();

    private final native void native_remove_storage(int i);

    private final native void native_run();

    private final native void native_send_device_property_changed(int i);

    private final native void native_send_object_added(int i);

    private final native void native_send_object_info_changed(int i);

    private final native void native_send_object_removed(int i);

    private final native void native_setup(MtpDatabase mtpDatabase, FileDescriptor fileDescriptor, boolean z, String str, String str2, String str3, String str4);

    static {
        System.loadLibrary("media_jni");
    }

    public MtpServer(MtpDatabase database, FileDescriptor controlFd, boolean usePtp, Runnable onTerminate, String deviceInfoManufacturer, String deviceInfoModel, String deviceInfoDeviceVersion) {
        String strRandomId;
        MtpDatabase mtpDatabase = (MtpDatabase) Preconditions.checkNotNull(database);
        this.mDatabase = mtpDatabase;
        this.mOnTerminate = (Runnable) Preconditions.checkNotNull(onTerminate);
        Context context = mtpDatabase.getContext();
        this.mContext = context;
        String strRandomId2 = null;
        SharedPreferences sharedPref = context.getSharedPreferences("mtp-cfg", 0);
        if (sharedPref.contains("mtp-id")) {
            strRandomId2 = sharedPref.getString("mtp-id", null);
            if (strRandomId2.length() != 32) {
                strRandomId2 = null;
            } else {
                int ii = 0;
                while (true) {
                    if (ii >= strRandomId2.length()) {
                        break;
                    } else if (Character.digit(strRandomId2.charAt(ii), 16) != -1) {
                        ii++;
                    } else {
                        strRandomId2 = null;
                        break;
                    }
                }
            }
        }
        if (strRandomId2 != null) {
            strRandomId = strRandomId2;
        } else {
            String strRandomId3 = getRandId();
            sharedPref.edit().putString("mtp-id", strRandomId3).apply();
            strRandomId = strRandomId3;
        }
        String deviceInfoSerialNumber = strRandomId;
        native_setup(database, controlFd, usePtp, deviceInfoManufacturer, deviceInfoModel, deviceInfoDeviceVersion, deviceInfoSerialNumber);
        database.setServer(this);
    }

    private String getRandId() {
        Random randomVal = new Random();
        byte[] randomBytes = new byte[16];
        randomVal.nextBytes(randomBytes);
        return HexEncoding.encodeToString(randomBytes);
    }

    public void start() {
        Thread thread = new Thread(this, "MtpServer");
        thread.start();
    }

    @Override // java.lang.Runnable
    public void run() {
        native_run();
        native_cleanup();
        this.mDatabase.close();
        this.mOnTerminate.run();
    }

    public void sendObjectAdded(int handle) {
        native_send_object_added(handle);
    }

    public void sendObjectRemoved(int handle) {
        native_send_object_removed(handle);
    }

    public void sendObjectInfoChanged(int handle) {
        native_send_object_info_changed(handle);
    }

    public void sendDevicePropertyChanged(int property) {
        native_send_device_property_changed(property);
    }

    public void addStorage(MtpStorage storage) {
        native_add_storage(storage);
    }

    public void removeStorage(MtpStorage storage) {
        native_remove_storage(storage.getStorageId());
    }
}
