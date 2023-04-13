package com.android.ims.internal.uce;

import com.android.ims.internal.uce.common.UceLong;
import com.android.ims.internal.uce.options.IOptionsListener;
import com.android.ims.internal.uce.options.IOptionsService;
import com.android.ims.internal.uce.presence.IPresenceListener;
import com.android.ims.internal.uce.presence.IPresenceService;
import com.android.ims.internal.uce.uceservice.IUceListener;
import com.android.ims.internal.uce.uceservice.IUceService;
/* loaded from: classes4.dex */
public abstract class UceServiceBase {
    private UceServiceBinder mBinder;

    /* loaded from: classes4.dex */
    private final class UceServiceBinder extends IUceService.Stub {
        private UceServiceBinder() {
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean startService(IUceListener uceListener) {
            return UceServiceBase.this.onServiceStart(uceListener);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean stopService() {
            return UceServiceBase.this.onStopService();
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean isServiceStarted() {
            return UceServiceBase.this.onIsServiceStarted();
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createOptionsService(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) {
            return UceServiceBase.this.onCreateOptionsService(optionsListener, optionsServiceListenerHdl);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createOptionsServiceForSubscription(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl, String iccId) {
            return UceServiceBase.this.onCreateOptionsService(optionsListener, optionsServiceListenerHdl, iccId);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public void destroyOptionsService(int optionsServiceHandle) {
            UceServiceBase.this.onDestroyOptionsService(optionsServiceHandle);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createPresenceService(IPresenceListener presServiceListener, UceLong presServiceListenerHdl) {
            return UceServiceBase.this.onCreatePresService(presServiceListener, presServiceListenerHdl);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public int createPresenceServiceForSubscription(IPresenceListener presServiceListener, UceLong presServiceListenerHdl, String iccId) {
            return UceServiceBase.this.onCreatePresService(presServiceListener, presServiceListenerHdl, iccId);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public void destroyPresenceService(int presServiceHdl) {
            UceServiceBase.this.onDestroyPresService(presServiceHdl);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public boolean getServiceStatus() {
            return UceServiceBase.this.onGetServiceStatus();
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IPresenceService getPresenceService() {
            return UceServiceBase.this.onGetPresenceService();
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IPresenceService getPresenceServiceForSubscription(String iccId) {
            return UceServiceBase.this.onGetPresenceService(iccId);
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IOptionsService getOptionsService() {
            return UceServiceBase.this.onGetOptionsService();
        }

        @Override // com.android.ims.internal.uce.uceservice.IUceService
        public IOptionsService getOptionsServiceForSubscription(String iccId) {
            return UceServiceBase.this.onGetOptionsService(iccId);
        }
    }

    public UceServiceBinder getBinder() {
        if (this.mBinder == null) {
            this.mBinder = new UceServiceBinder();
        }
        return this.mBinder;
    }

    protected boolean onServiceStart(IUceListener uceListener) {
        return false;
    }

    protected boolean onStopService() {
        return false;
    }

    protected boolean onIsServiceStarted() {
        return false;
    }

    protected int onCreateOptionsService(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl) {
        return 0;
    }

    protected int onCreateOptionsService(IOptionsListener optionsListener, UceLong optionsServiceListenerHdl, String iccId) {
        return 0;
    }

    protected void onDestroyOptionsService(int cdServiceHandle) {
    }

    protected int onCreatePresService(IPresenceListener presServiceListener, UceLong presServiceListenerHdl) {
        return 0;
    }

    protected int onCreatePresService(IPresenceListener presServiceListener, UceLong presServiceListenerHdl, String iccId) {
        return 0;
    }

    protected void onDestroyPresService(int presServiceHdl) {
    }

    protected boolean onGetServiceStatus() {
        return false;
    }

    protected IPresenceService onGetPresenceService() {
        return null;
    }

    protected IPresenceService onGetPresenceService(String iccId) {
        return null;
    }

    protected IOptionsService onGetOptionsService() {
        return null;
    }

    protected IOptionsService onGetOptionsService(String iccId) {
        return null;
    }
}
