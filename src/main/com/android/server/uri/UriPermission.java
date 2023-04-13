package com.android.server.uri;

import android.app.GrantedUriPermission;
import android.os.Binder;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.google.android.collect.Sets;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
/* loaded from: classes2.dex */
public final class UriPermission {
    public ArraySet<UriPermissionOwner> mReadOwners;
    public ArraySet<UriPermissionOwner> mWriteOwners;
    public final String sourcePkg;
    public String stringName;
    public final String targetPkg;
    public final int targetUid;
    public final int targetUserId;
    public final GrantUri uri;
    public int modeFlags = 0;
    public int ownedModeFlags = 0;
    public int globalModeFlags = 0;
    public int persistableModeFlags = 0;
    public int persistedModeFlags = 0;
    public long persistedCreateTime = Long.MIN_VALUE;

    public UriPermission(String str, String str2, int i, GrantUri grantUri) {
        this.targetUserId = UserHandle.getUserId(i);
        this.sourcePkg = str;
        this.targetPkg = str2;
        this.targetUid = i;
        this.uri = grantUri;
    }

    public final void updateModeFlags() {
        int i = this.modeFlags;
        this.modeFlags = this.ownedModeFlags | this.globalModeFlags | this.persistedModeFlags;
        if (!Log.isLoggable("UriPermission", 2) || this.modeFlags == i) {
            return;
        }
        Slog.d("UriPermission", "Permission for " + this.targetPkg + " to " + this.uri + " is changing from 0x" + Integer.toHexString(i) + " to 0x" + Integer.toHexString(this.modeFlags) + " via calling UID " + Binder.getCallingUid() + " PID " + Binder.getCallingPid(), new Throwable());
    }

    public void initPersistedModes(int i, long j) {
        int i2 = i & 3;
        this.persistableModeFlags = i2;
        this.persistedModeFlags = i2;
        this.persistedCreateTime = j;
        updateModeFlags();
    }

    public boolean grantModes(int i, UriPermissionOwner uriPermissionOwner) {
        boolean z = (i & 64) != 0;
        int i2 = i & 3;
        if (z) {
            this.persistableModeFlags |= i2;
        }
        if (uriPermissionOwner == null) {
            this.globalModeFlags = i2 | this.globalModeFlags;
        } else {
            if ((i2 & 1) != 0) {
                addReadOwner(uriPermissionOwner);
            }
            if ((i2 & 2) != 0) {
                addWriteOwner(uriPermissionOwner);
            }
        }
        updateModeFlags();
        return false;
    }

    public boolean takePersistableModes(int i) {
        int i2 = i & 3;
        int i3 = this.persistableModeFlags;
        if ((i2 & i3) != i2) {
            Slog.w("UriPermission", "Requested flags 0x" + Integer.toHexString(i2) + ", but only 0x" + Integer.toHexString(this.persistableModeFlags) + " are allowed");
            return false;
        }
        int i4 = this.persistedModeFlags;
        int i5 = (i2 & i3) | i4;
        this.persistedModeFlags = i5;
        if (i5 != 0) {
            this.persistedCreateTime = System.currentTimeMillis();
        }
        updateModeFlags();
        return this.persistedModeFlags != i4;
    }

    public boolean releasePersistableModes(int i) {
        int i2 = this.persistedModeFlags;
        int i3 = (~(i & 3)) & i2;
        this.persistedModeFlags = i3;
        if (i3 == 0) {
            this.persistedCreateTime = Long.MIN_VALUE;
        }
        updateModeFlags();
        return this.persistedModeFlags != i2;
    }

    public boolean revokeModes(int i, boolean z) {
        boolean z2 = (i & 64) != 0;
        int i2 = i & 3;
        int i3 = this.persistedModeFlags;
        if ((i2 & 1) != 0) {
            if (z2) {
                this.persistableModeFlags &= -2;
                this.persistedModeFlags = i3 & (-2);
            }
            this.globalModeFlags &= -2;
            ArraySet<UriPermissionOwner> arraySet = this.mReadOwners;
            if (arraySet != null && z) {
                this.ownedModeFlags &= -2;
                Iterator<UriPermissionOwner> it = arraySet.iterator();
                while (it.hasNext()) {
                    it.next().removeReadPermission(this);
                }
                this.mReadOwners = null;
            }
        }
        if ((i2 & 2) != 0) {
            if (z2) {
                this.persistableModeFlags &= -3;
                this.persistedModeFlags &= -3;
            }
            this.globalModeFlags &= -3;
            ArraySet<UriPermissionOwner> arraySet2 = this.mWriteOwners;
            if (arraySet2 != null && z) {
                this.ownedModeFlags &= -3;
                Iterator<UriPermissionOwner> it2 = arraySet2.iterator();
                while (it2.hasNext()) {
                    it2.next().removeWritePermission(this);
                }
                this.mWriteOwners = null;
            }
        }
        if (this.persistedModeFlags == 0) {
            this.persistedCreateTime = Long.MIN_VALUE;
        }
        updateModeFlags();
        return this.persistedModeFlags != i3;
    }

    public int getStrength(int i) {
        int i2 = i & 3;
        if ((this.persistableModeFlags & i2) == i2) {
            return 3;
        }
        if ((this.globalModeFlags & i2) == i2) {
            return 2;
        }
        return (this.ownedModeFlags & i2) == i2 ? 1 : 0;
    }

    public final void addReadOwner(UriPermissionOwner uriPermissionOwner) {
        if (this.mReadOwners == null) {
            this.mReadOwners = Sets.newArraySet();
            this.ownedModeFlags |= 1;
            updateModeFlags();
        }
        if (this.mReadOwners.add(uriPermissionOwner)) {
            uriPermissionOwner.addReadPermission(this);
        }
    }

    public void removeReadOwner(UriPermissionOwner uriPermissionOwner) {
        if (!this.mReadOwners.remove(uriPermissionOwner)) {
            Slog.wtf("UriPermission", "Unknown read owner " + uriPermissionOwner + " in " + this);
        }
        if (this.mReadOwners.size() == 0) {
            this.mReadOwners = null;
            this.ownedModeFlags &= -2;
            updateModeFlags();
        }
    }

    public final void addWriteOwner(UriPermissionOwner uriPermissionOwner) {
        if (this.mWriteOwners == null) {
            this.mWriteOwners = Sets.newArraySet();
            this.ownedModeFlags |= 2;
            updateModeFlags();
        }
        if (this.mWriteOwners.add(uriPermissionOwner)) {
            uriPermissionOwner.addWritePermission(this);
        }
    }

    public void removeWriteOwner(UriPermissionOwner uriPermissionOwner) {
        if (!this.mWriteOwners.remove(uriPermissionOwner)) {
            Slog.wtf("UriPermission", "Unknown write owner " + uriPermissionOwner + " in " + this);
        }
        if (this.mWriteOwners.size() == 0) {
            this.mWriteOwners = null;
            this.ownedModeFlags &= -3;
            updateModeFlags();
        }
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("UriPermission{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        sb.append(this.uri);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.print("targetUserId=" + this.targetUserId);
        printWriter.print(" sourcePkg=" + this.sourcePkg);
        printWriter.println(" targetPkg=" + this.targetPkg);
        printWriter.print(str);
        printWriter.print("mode=0x" + Integer.toHexString(this.modeFlags));
        printWriter.print(" owned=0x" + Integer.toHexString(this.ownedModeFlags));
        printWriter.print(" global=0x" + Integer.toHexString(this.globalModeFlags));
        printWriter.print(" persistable=0x" + Integer.toHexString(this.persistableModeFlags));
        printWriter.print(" persisted=0x" + Integer.toHexString(this.persistedModeFlags));
        if (this.persistedCreateTime != Long.MIN_VALUE) {
            printWriter.print(" persistedCreate=" + this.persistedCreateTime);
        }
        printWriter.println();
        if (this.mReadOwners != null) {
            printWriter.print(str);
            printWriter.println("readOwners:");
            Iterator<UriPermissionOwner> it = this.mReadOwners.iterator();
            while (it.hasNext()) {
                printWriter.print(str);
                printWriter.println("  * " + it.next());
            }
        }
        if (this.mWriteOwners != null) {
            printWriter.print(str);
            printWriter.println("writeOwners:");
            Iterator<UriPermissionOwner> it2 = this.mReadOwners.iterator();
            while (it2.hasNext()) {
                printWriter.print(str);
                printWriter.println("  * " + it2.next());
            }
        }
    }

    /* loaded from: classes2.dex */
    public static class PersistedTimeComparator implements Comparator<UriPermission> {
        @Override // java.util.Comparator
        public int compare(UriPermission uriPermission, UriPermission uriPermission2) {
            return Long.compare(uriPermission.persistedCreateTime, uriPermission2.persistedCreateTime);
        }
    }

    /* loaded from: classes2.dex */
    public static class Snapshot {
        public final long persistedCreateTime;
        public final int persistedModeFlags;
        public final String sourcePkg;
        public final String targetPkg;
        public final int targetUserId;
        public final GrantUri uri;

        public Snapshot(UriPermission uriPermission) {
            this.targetUserId = uriPermission.targetUserId;
            this.sourcePkg = uriPermission.sourcePkg;
            this.targetPkg = uriPermission.targetPkg;
            this.uri = uriPermission.uri;
            this.persistedModeFlags = uriPermission.persistedModeFlags;
            this.persistedCreateTime = uriPermission.persistedCreateTime;
        }
    }

    public Snapshot snapshot() {
        return new Snapshot();
    }

    public android.content.UriPermission buildPersistedPublicApiObject() {
        return new android.content.UriPermission(this.uri.uri, this.persistedModeFlags, this.persistedCreateTime);
    }

    public GrantedUriPermission buildGrantedUriPermission() {
        return new GrantedUriPermission(this.uri.uri, this.targetPkg);
    }
}
