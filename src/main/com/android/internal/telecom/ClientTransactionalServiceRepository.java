package com.android.internal.telecom;

import android.telecom.PhoneAccountHandle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/* loaded from: classes2.dex */
public class ClientTransactionalServiceRepository {
    private static final Map<PhoneAccountHandle, ClientTransactionalServiceWrapper> LOOKUP_TABLE = new ConcurrentHashMap();

    public ClientTransactionalServiceWrapper addNewCallForTransactionalServiceWrapper(PhoneAccountHandle phoneAccountHandle) {
        ClientTransactionalServiceWrapper service;
        if (!hasExistingServiceWrapper(phoneAccountHandle)) {
            service = new ClientTransactionalServiceWrapper(phoneAccountHandle, this);
        } else {
            service = getTransactionalServiceWrapper(phoneAccountHandle);
        }
        LOOKUP_TABLE.put(phoneAccountHandle, service);
        return service;
    }

    private ClientTransactionalServiceWrapper getTransactionalServiceWrapper(PhoneAccountHandle pah) {
        return LOOKUP_TABLE.get(pah);
    }

    private boolean hasExistingServiceWrapper(PhoneAccountHandle pah) {
        return LOOKUP_TABLE.containsKey(pah);
    }

    public boolean removeServiceWrapper(PhoneAccountHandle pah) {
        if (!hasExistingServiceWrapper(pah)) {
            return false;
        }
        LOOKUP_TABLE.remove(pah);
        return true;
    }

    public boolean removeCallFromServiceWrapper(PhoneAccountHandle pah, String callId) {
        if (!hasExistingServiceWrapper(pah)) {
            return false;
        }
        ClientTransactionalServiceWrapper service = LOOKUP_TABLE.get(pah);
        service.untrackCall(callId);
        return true;
    }
}
