package com.android.ims.rcs.uce.presence.subscribe;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.aidl.ISubscribeResponseCallback;
import com.android.ims.rcs.uce.ControllerBase;
import java.util.List;
/* loaded from: classes.dex */
public interface SubscribeController extends ControllerBase {
    void requestCapabilities(List<Uri> list, ISubscribeResponseCallback iSubscribeResponseCallback) throws RemoteException;
}
