package com.android.server.voiceinteraction;

import android.service.voice.ISandboxedDetectionService;
import com.android.internal.infra.ServiceConnector;
/* compiled from: R8$$SyntheticClass */
/* loaded from: classes2.dex */
public final /* synthetic */ class SoftwareTrustedHotwordDetectorSession$$ExternalSyntheticLambda0 implements ServiceConnector.VoidJob {
    public final void runNoResult(Object obj) {
        ((ISandboxedDetectionService) obj).stopDetection();
    }
}
