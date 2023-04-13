package android.companion.virtual.camera;

import android.hardware.camera2.params.InputConfiguration;
import java.io.InputStream;
/* loaded from: classes.dex */
public interface VirtualCameraInput {
    void closeStream();

    InputStream openStream(InputConfiguration inputConfiguration);
}
