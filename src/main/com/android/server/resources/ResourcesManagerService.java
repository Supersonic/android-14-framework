package com.android.server.resources;

import android.content.Context;
import android.content.res.IResourcesManager;
import android.content.res.ResourceTimer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallback;
import android.os.RemoteException;
import com.android.server.SystemService;
import com.android.server.p006am.ActivityManagerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class ResourcesManagerService extends SystemService {
    public ActivityManagerService mActivityManagerService;
    public final IBinder mService;

    public ResourcesManagerService(Context context) {
        super(context);
        IResourcesManager.Stub stub = new IResourcesManager.Stub() { // from class: com.android.server.resources.ResourcesManagerService.1
            public boolean dumpResources(String str, ParcelFileDescriptor parcelFileDescriptor, RemoteCallback remoteCallback) throws RemoteException {
                int callingUid = Binder.getCallingUid();
                if (callingUid != 0 && callingUid != 2000) {
                    remoteCallback.sendResult((Bundle) null);
                    throw new SecurityException("dump should only be called by shell");
                }
                return ResourcesManagerService.this.mActivityManagerService.dumpResources(str, parcelFileDescriptor, remoteCallback);
            }

            public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
                try {
                    ParcelFileDescriptor dup = ParcelFileDescriptor.dup(fileDescriptor);
                    ResourcesManagerService.this.mActivityManagerService.dumpAllResources(dup, printWriter);
                    if (dup != null) {
                        dup.close();
                    }
                } catch (Exception e) {
                    printWriter.println("Exception while trying to dump all resources: " + e.getMessage());
                    e.printStackTrace(printWriter);
                }
            }

            /* JADX WARN: Multi-variable type inference failed */
            public int handleShellCommand(ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, ParcelFileDescriptor parcelFileDescriptor3, String[] strArr) {
                return new ResourcesManagerShellCommand(this).exec(this, parcelFileDescriptor.getFileDescriptor(), parcelFileDescriptor2.getFileDescriptor(), parcelFileDescriptor3.getFileDescriptor(), strArr);
            }
        };
        this.mService = stub;
        publishBinderService("resources", stub);
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        ResourceTimer.start();
    }

    public void setActivityManagerService(ActivityManagerService activityManagerService) {
        this.mActivityManagerService = activityManagerService;
    }
}
