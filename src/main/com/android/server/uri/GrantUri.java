package com.android.server.uri;

import android.content.ContentProvider;
import android.net.Uri;
import android.util.proto.ProtoOutputStream;
/* loaded from: classes2.dex */
public class GrantUri {
    public final boolean prefix;
    public final int sourceUserId;
    public final Uri uri;

    public GrantUri(int i, Uri uri, int i2) {
        this.sourceUserId = i;
        this.uri = uri;
        this.prefix = (i2 & 128) != 0;
    }

    public int hashCode() {
        return ((((this.sourceUserId + 31) * 31) + this.uri.hashCode()) * 31) + (this.prefix ? 1231 : 1237);
    }

    public boolean equals(Object obj) {
        if (obj instanceof GrantUri) {
            GrantUri grantUri = (GrantUri) obj;
            return this.uri.equals(grantUri.uri) && this.sourceUserId == grantUri.sourceUserId && this.prefix == grantUri.prefix;
        }
        return false;
    }

    public String toString() {
        String str = this.uri.toString() + " [user " + this.sourceUserId + "]";
        if (this.prefix) {
            return str + " [prefix]";
        }
        return str;
    }

    public String toSafeString() {
        String str = this.uri.toSafeString() + " [user " + this.sourceUserId + "]";
        if (this.prefix) {
            return str + " [prefix]";
        }
        return str;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1138166333442L, this.uri.toString());
        protoOutputStream.write(1120986464257L, this.sourceUserId);
        protoOutputStream.end(start);
    }

    public static GrantUri resolve(int i, Uri uri, int i2) {
        if ("content".equals(uri.getScheme())) {
            return new GrantUri(ContentProvider.getUserIdFromUri(uri, i), ContentProvider.getUriWithoutUserId(uri), i2);
        }
        return new GrantUri(i, uri, i2);
    }
}
