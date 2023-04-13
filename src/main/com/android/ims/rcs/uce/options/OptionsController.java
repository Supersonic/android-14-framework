package com.android.ims.rcs.uce.options;

import android.net.Uri;
import android.os.RemoteException;
import android.telephony.ims.aidl.IOptionsResponseCallback;
import com.android.ims.rcs.uce.ControllerBase;
import java.util.Set;
/* loaded from: classes.dex */
public interface OptionsController extends ControllerBase {
    void sendCapabilitiesRequest(Uri uri, Set<String> set, IOptionsResponseCallback iOptionsResponseCallback) throws RemoteException;
}
