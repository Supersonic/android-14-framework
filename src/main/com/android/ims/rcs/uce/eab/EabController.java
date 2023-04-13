package com.android.ims.rcs.uce.eab;

import android.net.Uri;
import android.telephony.ims.RcsContactUceCapability;
import com.android.ims.rcs.uce.ControllerBase;
import com.android.ims.rcs.uce.UceController;
import java.util.List;
/* loaded from: classes.dex */
public interface EabController extends ControllerBase {
    EabCapabilityResult getAvailability(Uri uri);

    EabCapabilityResult getAvailabilityIncludingExpired(Uri uri);

    List<EabCapabilityResult> getCapabilities(List<Uri> list);

    List<EabCapabilityResult> getCapabilitiesIncludingExpired(List<Uri> list);

    void saveCapabilities(List<RcsContactUceCapability> list);

    void setUceRequestCallback(UceController.UceControllerCallback uceControllerCallback);
}
