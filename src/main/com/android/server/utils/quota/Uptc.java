package com.android.server.utils.quota;

import android.util.proto.ProtoOutputStream;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class Uptc {
    public final int mHash;
    public final String packageName;
    public final String tag;
    public final int userId;

    public Uptc(int i, String str, String str2) {
        this.userId = i;
        this.packageName = str;
        this.tag = str2;
        StringBuilder sb = new StringBuilder();
        sb.append((i * 31) + (str.hashCode() * 31));
        sb.append(str2);
        this.mHash = sb.toString() == null ? 0 : str2.hashCode() * 31;
    }

    public String toString() {
        return string(this.userId, this.packageName, this.tag);
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.userId);
        protoOutputStream.write(1138166333442L, this.packageName);
        protoOutputStream.write(1138166333443L, this.tag);
        protoOutputStream.end(start);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Uptc) {
            Uptc uptc = (Uptc) obj;
            return this.userId == uptc.userId && Objects.equals(this.packageName, uptc.packageName) && Objects.equals(this.tag, uptc.tag);
        }
        return false;
    }

    public int hashCode() {
        return this.mHash;
    }

    public static String string(int i, String str, String str2) {
        String str3;
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        sb.append(i);
        sb.append(">");
        sb.append(str);
        if (str2 == null) {
            str3 = "";
        } else {
            str3 = "::" + str2;
        }
        sb.append(str3);
        return sb.toString();
    }
}
