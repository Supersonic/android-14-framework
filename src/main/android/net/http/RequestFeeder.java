package android.net.http;

import org.apache.http.HttpHost;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public interface RequestFeeder {
    Request getRequest();

    Request getRequest(HttpHost httpHost);

    boolean haveRequest(HttpHost httpHost);

    void requeueRequest(Request request);
}
