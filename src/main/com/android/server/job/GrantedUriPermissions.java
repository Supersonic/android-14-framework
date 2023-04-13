package com.android.server.job;

import android.app.UriGrantsManager;
import android.content.ClipData;
import android.content.ContentProvider;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.server.LocalServices;
import com.android.server.uri.UriGrantsManagerInternal;
import java.io.PrintWriter;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class GrantedUriPermissions {
    public final int mGrantFlags;
    public final IBinder mPermissionOwner;
    public final int mSourceUserId;
    public final String mTag;
    public final ArrayList<Uri> mUris = new ArrayList<>();

    public static boolean checkGrantFlags(int i) {
        return (i & 3) != 0;
    }

    public GrantedUriPermissions(int i, int i2, String str) throws RemoteException {
        this.mGrantFlags = i;
        this.mSourceUserId = UserHandle.getUserId(i2);
        this.mTag = str;
        this.mPermissionOwner = ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).newUriPermissionOwner("job: " + str);
    }

    public void revoke() {
        for (int size = this.mUris.size() - 1; size >= 0; size--) {
            ((UriGrantsManagerInternal) LocalServices.getService(UriGrantsManagerInternal.class)).revokeUriPermissionFromOwner(this.mPermissionOwner, this.mUris.get(size), this.mGrantFlags, this.mSourceUserId);
        }
        this.mUris.clear();
    }

    public static GrantedUriPermissions createFromIntent(Intent intent, int i, String str, int i2, String str2) {
        int flags = intent.getFlags();
        if (checkGrantFlags(flags)) {
            Uri data = intent.getData();
            GrantedUriPermissions grantUri = data != null ? grantUri(data, i, str, i2, flags, str2, null) : null;
            ClipData clipData = intent.getClipData();
            return clipData != null ? grantClip(clipData, i, str, i2, flags, str2, grantUri) : grantUri;
        }
        return null;
    }

    public static GrantedUriPermissions createFromClip(ClipData clipData, int i, String str, int i2, int i3, String str2) {
        if (checkGrantFlags(i3) && clipData != null) {
            return grantClip(clipData, i, str, i2, i3, str2, null);
        }
        return null;
    }

    public static GrantedUriPermissions grantClip(ClipData clipData, int i, String str, int i2, int i3, String str2, GrantedUriPermissions grantedUriPermissions) {
        int itemCount = clipData.getItemCount();
        GrantedUriPermissions grantedUriPermissions2 = grantedUriPermissions;
        for (int i4 = 0; i4 < itemCount; i4++) {
            grantedUriPermissions2 = grantItem(clipData.getItemAt(i4), i, str, i2, i3, str2, grantedUriPermissions2);
        }
        return grantedUriPermissions2;
    }

    public static GrantedUriPermissions grantUri(Uri uri, int i, String str, int i2, int i3, String str2, GrantedUriPermissions grantedUriPermissions) {
        try {
            int userIdFromUri = ContentProvider.getUserIdFromUri(uri, UserHandle.getUserId(i));
            Uri uriWithoutUserId = ContentProvider.getUriWithoutUserId(uri);
            if (grantedUriPermissions == null) {
                grantedUriPermissions = new GrantedUriPermissions(i3, i, str2);
            }
            UriGrantsManager.getService().grantUriPermissionFromOwner(grantedUriPermissions.mPermissionOwner, i, str, uriWithoutUserId, i3, userIdFromUri, i2);
            grantedUriPermissions.mUris.add(uriWithoutUserId);
        } catch (RemoteException unused) {
            Slog.e("JobScheduler", "AM dead");
        }
        return grantedUriPermissions;
    }

    public static GrantedUriPermissions grantItem(ClipData.Item item, int i, String str, int i2, int i3, String str2, GrantedUriPermissions grantedUriPermissions) {
        if (item.getUri() != null) {
            grantedUriPermissions = grantUri(item.getUri(), i, str, i2, i3, str2, grantedUriPermissions);
        }
        GrantedUriPermissions grantedUriPermissions2 = grantedUriPermissions;
        Intent intent = item.getIntent();
        return (intent == null || intent.getData() == null) ? grantedUriPermissions2 : grantUri(intent.getData(), i, str, i2, i3, str2, grantedUriPermissions2);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.print("mGrantFlags=0x");
        printWriter.print(Integer.toHexString(this.mGrantFlags));
        printWriter.print(" mSourceUserId=");
        printWriter.println(this.mSourceUserId);
        printWriter.print("mTag=");
        printWriter.println(this.mTag);
        printWriter.print("mPermissionOwner=");
        printWriter.println(this.mPermissionOwner);
        for (int i = 0; i < this.mUris.size(); i++) {
            printWriter.print("#");
            printWriter.print(i);
            printWriter.print(": ");
            printWriter.println(this.mUris.get(i));
        }
    }

    public void dump(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.mGrantFlags);
        protoOutputStream.write(1120986464258L, this.mSourceUserId);
        protoOutputStream.write(1138166333443L, this.mTag);
        protoOutputStream.write(1138166333444L, this.mPermissionOwner.toString());
        for (int i = 0; i < this.mUris.size(); i++) {
            Uri uri = this.mUris.get(i);
            if (uri != null) {
                protoOutputStream.write(2237677961221L, uri.toString());
            }
        }
        protoOutputStream.end(start);
    }
}
