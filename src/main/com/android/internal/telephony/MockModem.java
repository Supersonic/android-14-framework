package com.android.internal.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.android.internal.telephony.util.NotificationChannelController;
import com.android.telephony.Rlog;
/* loaded from: classes.dex */
public class MockModem {
    private IBinder mConfigBinder;
    private ServiceConnection mConfigServiceConnection;
    private Context mContext;
    private IBinder mDataBinder;
    private ServiceConnection mDataServiceConnection;
    private IBinder mImsBinder;
    private ServiceConnection mImsServiceConnection;
    private IBinder mMessagingBinder;
    private ServiceConnection mMessagingServiceConnection;
    private IBinder mModemBinder;
    private ServiceConnection mModemServiceConnection;
    private IBinder mNetworkBinder;
    private ServiceConnection mNetworkServiceConnection;
    private String mPackageName;
    private byte mPhoneId;
    private IBinder mSatelliteBinder;
    private ServiceConnection mSatelliteServiceConnection;
    private String mServiceName;
    private IBinder mSimBinder;
    private ServiceConnection mSimServiceConnection;
    private String mTag;
    private IBinder mVoiceBinder;
    private ServiceConnection mVoiceServiceConnection;

    /* JADX INFO: Access modifiers changed from: private */
    public String getModuleName(int i) {
        switch (i) {
            case 1:
                return "data";
            case 2:
                return "messaging";
            case 3:
                return "modem";
            case 4:
                return "network";
            case 5:
                return NotificationChannelController.CHANNEL_ID_SIM;
            case 6:
                return "voice";
            case 7:
                return "ims";
            case 8:
                return "satellite";
            case 9:
                return "config";
            default:
                return "none";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MockModem(Context context, String str) {
        this(context, str, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MockModem(Context context, String str, int i) {
        this.mPhoneId = (byte) i;
        this.mTag = "MockModem-" + ((int) this.mPhoneId);
        this.mContext = context;
        String[] split = str.split("/", 2);
        this.mPackageName = split[0];
        this.mServiceName = split[1];
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MockModemConnection implements ServiceConnection {
        private int mService;

        MockModemConnection(int i) {
            this.mService = i;
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            String str = MockModem.this.mTag;
            Rlog.d(str, "IRadio " + MockModem.this.getModuleName(this.mService) + "  - onServiceConnected");
            int i = this.mService;
            if (i == 3) {
                MockModem.this.mModemBinder = iBinder;
            } else if (i == 5) {
                MockModem.this.mSimBinder = iBinder;
            } else if (i == 2) {
                MockModem.this.mMessagingBinder = iBinder;
            } else if (i == 1) {
                MockModem.this.mDataBinder = iBinder;
            } else if (i == 4) {
                MockModem.this.mNetworkBinder = iBinder;
            } else if (i == 6) {
                MockModem.this.mVoiceBinder = iBinder;
            } else if (i == 7) {
                MockModem.this.mImsBinder = iBinder;
            } else if (i == 8) {
                MockModem.this.mSatelliteBinder = iBinder;
            } else if (i == 9) {
                MockModem.this.mConfigBinder = iBinder;
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            String str = MockModem.this.mTag;
            Rlog.d(str, "IRadio " + MockModem.this.getModuleName(this.mService) + "  - onServiceDisconnected");
            int i = this.mService;
            if (i == 3) {
                MockModem.this.mModemBinder = null;
            } else if (i == 5) {
                MockModem.this.mSimBinder = null;
            } else if (i == 2) {
                MockModem.this.mMessagingBinder = null;
            } else if (i == 1) {
                MockModem.this.mDataBinder = null;
            } else if (i == 4) {
                MockModem.this.mNetworkBinder = null;
            } else if (i == 6) {
                MockModem.this.mVoiceBinder = null;
            } else if (i == 7) {
                MockModem.this.mImsBinder = null;
            } else if (i == 8) {
                MockModem.this.mSatelliteBinder = null;
            } else if (i == 9) {
                MockModem.this.mConfigBinder = null;
            }
        }
    }

    private boolean bindModuleToMockModemService(String str, ServiceConnection serviceConnection) {
        return bindModuleToMockModemService((byte) 0, str, serviceConnection);
    }

    private boolean bindModuleToMockModemService(byte b, String str, ServiceConnection serviceConnection) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this.mPackageName, this.mServiceName));
        intent.setAction(str + ((int) b));
        intent.putExtra("phone_id", b);
        return this.mContext.bindService(intent, serviceConnection, 1);
    }

    public IBinder getServiceBinder(int i) {
        switch (i) {
            case 1:
                return this.mDataBinder;
            case 2:
                return this.mMessagingBinder;
            case 3:
                return this.mModemBinder;
            case 4:
                return this.mNetworkBinder;
            case 5:
                return this.mSimBinder;
            case 6:
                return this.mVoiceBinder;
            case 7:
                return this.mImsBinder;
            case 8:
                return this.mSatelliteBinder;
            case 9:
                return this.mConfigBinder;
            default:
                return null;
        }
    }

    public void bindAllMockModemService() {
        for (int i = 0; i <= 8; i++) {
            bindToMockModemService(i);
        }
    }

    public void bindToMockModemService(int i) {
        if (i == 9) {
            if (this.mConfigBinder == null) {
                MockModemConnection mockModemConnection = new MockModemConnection(9);
                this.mConfigServiceConnection = mockModemConnection;
                if (bindModuleToMockModemService("android.telephony.mockmodem.iradioconfig", mockModemConnection)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Config bind fail");
                this.mConfigServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Config is bound");
        } else if (i == 3) {
            if (this.mModemBinder == null) {
                MockModemConnection mockModemConnection2 = new MockModemConnection(3);
                this.mModemServiceConnection = mockModemConnection2;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiomodem", mockModemConnection2)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Modem bind fail");
                this.mModemServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Modem is bound");
        } else if (i == 5) {
            if (this.mSimBinder == null) {
                MockModemConnection mockModemConnection3 = new MockModemConnection(5);
                this.mSimServiceConnection = mockModemConnection3;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiosim", mockModemConnection3)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Sim bind fail");
                this.mSimServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Sim is bound");
        } else if (i == 2) {
            if (this.mMessagingBinder == null) {
                MockModemConnection mockModemConnection4 = new MockModemConnection(2);
                this.mMessagingServiceConnection = mockModemConnection4;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiomessaging", mockModemConnection4)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Messaging bind fail");
                this.mMessagingServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Messaging is bound");
        } else if (i == 1) {
            if (this.mDataBinder == null) {
                MockModemConnection mockModemConnection5 = new MockModemConnection(1);
                this.mDataServiceConnection = mockModemConnection5;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiodata", mockModemConnection5)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Data bind fail");
                this.mDataServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Data is bound");
        } else if (i == 4) {
            if (this.mNetworkBinder == null) {
                MockModemConnection mockModemConnection6 = new MockModemConnection(4);
                this.mNetworkServiceConnection = mockModemConnection6;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradionetwork", mockModemConnection6)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Network bind fail");
                this.mNetworkServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Network is bound");
        } else if (i == 6) {
            if (this.mVoiceBinder == null) {
                MockModemConnection mockModemConnection7 = new MockModemConnection(6);
                this.mVoiceServiceConnection = mockModemConnection7;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiovoice", mockModemConnection7)) {
                    return;
                }
                Rlog.d(this.mTag, "IRadio Voice bind fail");
                this.mVoiceServiceConnection = null;
                return;
            }
            Rlog.d(this.mTag, "IRadio Voice is bound");
        } else if (i == 7) {
            if (this.mImsBinder == null) {
                MockModemConnection mockModemConnection8 = new MockModemConnection(7);
                this.mImsServiceConnection = mockModemConnection8;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradioims", mockModemConnection8)) {
                    return;
                }
                Rlog.d("MockModem", "IRadio Ims bind fail");
                this.mImsServiceConnection = null;
                return;
            }
            Rlog.d("MockModem", "IRadio Ims is bound");
        } else if (i == 8) {
            if (this.mSatelliteBinder == null) {
                MockModemConnection mockModemConnection9 = new MockModemConnection(8);
                this.mSatelliteServiceConnection = mockModemConnection9;
                if (bindModuleToMockModemService(this.mPhoneId, "android.telephony.mockmodem.iradiosatellite", mockModemConnection9)) {
                    return;
                }
                Rlog.d("MockModem", "IRadio Satellite bind fail");
                this.mSatelliteServiceConnection = null;
                return;
            }
            Rlog.d("MockModem", "IRadio Satellite is bound");
        }
    }

    public void unbindMockModemService(int i) {
        ServiceConnection serviceConnection;
        if (i == 9) {
            ServiceConnection serviceConnection2 = this.mConfigServiceConnection;
            if (serviceConnection2 != null) {
                this.mContext.unbindService(serviceConnection2);
                this.mConfigServiceConnection = null;
                this.mConfigBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Config");
            }
        } else if (i == 3) {
            ServiceConnection serviceConnection3 = this.mModemServiceConnection;
            if (serviceConnection3 != null) {
                this.mContext.unbindService(serviceConnection3);
                this.mModemServiceConnection = null;
                this.mModemBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Modem");
            }
        } else if (i == 5) {
            ServiceConnection serviceConnection4 = this.mSimServiceConnection;
            if (serviceConnection4 != null) {
                this.mContext.unbindService(serviceConnection4);
                this.mSimServiceConnection = null;
                this.mSimBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Sim");
            }
        } else if (i == 2) {
            ServiceConnection serviceConnection5 = this.mMessagingServiceConnection;
            if (serviceConnection5 != null) {
                this.mContext.unbindService(serviceConnection5);
                this.mMessagingServiceConnection = null;
                this.mMessagingBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Messaging");
            }
        } else if (i == 1) {
            ServiceConnection serviceConnection6 = this.mDataServiceConnection;
            if (serviceConnection6 != null) {
                this.mContext.unbindService(serviceConnection6);
                this.mDataServiceConnection = null;
                this.mDataBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Data");
            }
        } else if (i == 4) {
            ServiceConnection serviceConnection7 = this.mNetworkServiceConnection;
            if (serviceConnection7 != null) {
                this.mContext.unbindService(serviceConnection7);
                this.mNetworkServiceConnection = null;
                this.mNetworkBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Network");
            }
        } else if (i == 6) {
            ServiceConnection serviceConnection8 = this.mVoiceServiceConnection;
            if (serviceConnection8 != null) {
                this.mContext.unbindService(serviceConnection8);
                this.mVoiceServiceConnection = null;
                this.mVoiceBinder = null;
                Rlog.d(this.mTag, "unbind IRadio Voice");
            }
        } else if (i == 7) {
            ServiceConnection serviceConnection9 = this.mImsServiceConnection;
            if (serviceConnection9 != null) {
                this.mContext.unbindService(serviceConnection9);
                this.mImsServiceConnection = null;
                this.mImsBinder = null;
                Rlog.d("MockModem", "unbind IRadio Ims");
            }
        } else if (i != 8 || (serviceConnection = this.mSatelliteServiceConnection) == null) {
        } else {
            this.mContext.unbindService(serviceConnection);
            this.mSatelliteServiceConnection = null;
            this.mSatelliteBinder = null;
            Rlog.d("MockModem", "unbind IRadio Satellite");
        }
    }

    public String getServiceName() {
        return this.mServiceName;
    }
}
