package com.android.server.ambientcontext;

import android.app.ambientcontext.AmbientContextEvent;
import android.app.ambientcontext.AmbientContextEventRequest;
import android.os.RemoteCallback;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public interface RemoteAmbientDetectionService {
    void dump(String str, PrintWriter printWriter);

    void queryServiceStatus(@AmbientContextEvent.EventCode int[] iArr, String str, RemoteCallback remoteCallback);

    void startDetection(AmbientContextEventRequest ambientContextEventRequest, String str, RemoteCallback remoteCallback, RemoteCallback remoteCallback2);

    void stopDetection(String str);

    void unbind();
}
