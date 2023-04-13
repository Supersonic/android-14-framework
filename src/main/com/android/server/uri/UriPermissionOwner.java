package com.android.server.uri;

import android.os.Binder;
import android.os.IBinder;
import android.util.ArraySet;
import android.util.proto.ProtoOutputStream;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
/* loaded from: classes2.dex */
public class UriPermissionOwner {
    public Binder externalToken;
    public final Object mOwner;
    public ArraySet<UriPermission> mReadPerms;
    public final UriGrantsManagerInternal mService;
    public ArraySet<UriPermission> mWritePerms;

    /* loaded from: classes2.dex */
    public class ExternalToken extends Binder {
        public ExternalToken() {
        }

        public UriPermissionOwner getOwner() {
            return UriPermissionOwner.this;
        }
    }

    public UriPermissionOwner(UriGrantsManagerInternal uriGrantsManagerInternal, Object obj) {
        this.mService = uriGrantsManagerInternal;
        this.mOwner = obj;
    }

    public Binder getExternalToken() {
        if (this.externalToken == null) {
            this.externalToken = new ExternalToken();
        }
        return this.externalToken;
    }

    public static UriPermissionOwner fromExternalToken(IBinder iBinder) {
        if (iBinder instanceof ExternalToken) {
            return ((ExternalToken) iBinder).getOwner();
        }
        return null;
    }

    public void removeUriPermissions() {
        removeUriPermissions(3);
    }

    public void removeUriPermissions(int i) {
        removeUriPermission(null, i);
    }

    public void removeUriPermission(GrantUri grantUri, int i) {
        removeUriPermission(grantUri, i, null, -1);
    }

    public void removeUriPermission(GrantUri grantUri, int i, String str, int i2) {
        ArraySet<UriPermission> arraySet;
        ArrayList arrayList = new ArrayList();
        synchronized (this) {
            if ((i & 1) != 0) {
                try {
                    ArraySet<UriPermission> arraySet2 = this.mReadPerms;
                    if (arraySet2 != null) {
                        Iterator<UriPermission> it = arraySet2.iterator();
                        while (it.hasNext()) {
                            UriPermission next = it.next();
                            if (grantUri == null || grantUri.equals(next.uri)) {
                                if (str == null || str.equals(next.targetPkg)) {
                                    if (i2 == -1 || i2 == next.targetUserId) {
                                        arrayList.add(next);
                                        next.removeReadOwner(this);
                                        it.remove();
                                    }
                                }
                            }
                        }
                        if (this.mReadPerms.isEmpty()) {
                            this.mReadPerms = null;
                        }
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            if ((i & 2) != 0 && (arraySet = this.mWritePerms) != null) {
                Iterator<UriPermission> it2 = arraySet.iterator();
                while (it2.hasNext()) {
                    UriPermission next2 = it2.next();
                    if (grantUri == null || grantUri.equals(next2.uri)) {
                        if (str == null || str.equals(next2.targetPkg)) {
                            if (i2 == -1 || i2 == next2.targetUserId) {
                                arrayList.add(next2);
                                next2.removeWriteOwner(this);
                                it2.remove();
                            }
                        }
                    }
                }
                if (this.mWritePerms.isEmpty()) {
                    this.mWritePerms = null;
                }
            }
        }
        int size = arrayList.size();
        for (int i3 = 0; i3 < size; i3++) {
            this.mService.removeUriPermissionIfNeeded((UriPermission) arrayList.get(i3));
        }
    }

    public void addReadPermission(UriPermission uriPermission) {
        synchronized (this) {
            if (this.mReadPerms == null) {
                this.mReadPerms = Sets.newArraySet();
            }
            this.mReadPerms.add(uriPermission);
        }
    }

    public void addWritePermission(UriPermission uriPermission) {
        synchronized (this) {
            if (this.mWritePerms == null) {
                this.mWritePerms = Sets.newArraySet();
            }
            this.mWritePerms.add(uriPermission);
        }
    }

    public void removeReadPermission(UriPermission uriPermission) {
        synchronized (this) {
            this.mReadPerms.remove(uriPermission);
            if (this.mReadPerms.isEmpty()) {
                this.mReadPerms = null;
            }
        }
    }

    public void removeWritePermission(UriPermission uriPermission) {
        synchronized (this) {
            this.mWritePerms.remove(uriPermission);
            if (this.mWritePerms.isEmpty()) {
                this.mWritePerms = null;
            }
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        synchronized (this) {
            if (this.mReadPerms != null) {
                printWriter.print(str);
                printWriter.print("readUriPermissions=");
                printWriter.println(this.mReadPerms);
            }
            if (this.mWritePerms != null) {
                printWriter.print(str);
                printWriter.print("writeUriPermissions=");
                printWriter.println(this.mWritePerms);
            }
        }
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333441L, this.mOwner.toString());
        synchronized (this) {
            ArraySet<UriPermission> arraySet = this.mReadPerms;
            if (arraySet != null) {
                Iterator<UriPermission> it = arraySet.iterator();
                while (it.hasNext()) {
                    it.next().uri.dumpDebug(protoOutputStream, 2246267895810L);
                }
            }
            ArraySet<UriPermission> arraySet2 = this.mWritePerms;
            if (arraySet2 != null) {
                Iterator<UriPermission> it2 = arraySet2.iterator();
                while (it2.hasNext()) {
                    it2.next().uri.dumpDebug(protoOutputStream, 2246267895811L);
                }
            }
        }
        protoOutputStream.end(start);
    }

    public String toString() {
        return this.mOwner.toString();
    }
}
