package android.media;
/* loaded from: classes2.dex */
public interface MediaScannerClient {
    void handleStringTag(String str, String str2);

    void scanFile(String str, long j, long j2, boolean z, boolean z2);

    void setMimeType(String str);
}
