package com.android.internal.telephony.data;

import android.net.LinkProperties;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.SubscriptionManager;
import android.telephony.data.DataCallResponse;
import android.telephony.data.DataProfile;
import android.telephony.data.DataService;
import android.telephony.data.DataServiceCallback;
import android.telephony.data.NetworkSliceInfo;
import android.telephony.data.TrafficDescriptor;
import com.android.internal.telephony.CommandException;
import com.android.internal.telephony.Phone;
import com.android.internal.telephony.PhoneFactory;
import com.android.telephony.Rlog;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class CellularDataService extends DataService {
    private static final String TAG = CellularDataService.class.getSimpleName();

    /* loaded from: classes.dex */
    private class CellularDataServiceProvider extends DataService.DataServiceProvider {
        private final Map<Message, DataServiceCallback> mCallbackMap;
        private final Handler mHandler;
        private final Phone mPhone;

        private CellularDataServiceProvider(int i) {
            super(CellularDataService.this, i);
            this.mCallbackMap = new HashMap();
            Phone phone = PhoneFactory.getPhone(getSlotIndex());
            this.mPhone = phone;
            Handler handler = new Handler(Looper.myLooper()) { // from class: com.android.internal.telephony.data.CellularDataService.CellularDataServiceProvider.1
                @Override // android.os.Handler
                public void handleMessage(Message message) {
                    DataServiceCallback dataServiceCallback = (DataServiceCallback) CellularDataServiceProvider.this.mCallbackMap.remove(message);
                    AsyncResult asyncResult = (AsyncResult) message.obj;
                    switch (message.what) {
                        case 1:
                            dataServiceCallback.onSetupDataCallComplete(asyncResult.exception == null ? 0 : 4, (DataCallResponse) asyncResult.result);
                            return;
                        case 2:
                            dataServiceCallback.onDeactivateDataCallComplete(asyncResult.exception == null ? 0 : 4);
                            return;
                        case 3:
                            dataServiceCallback.onSetInitialAttachApnComplete(asyncResult.exception == null ? 0 : 4);
                            return;
                        case 4:
                            dataServiceCallback.onSetDataProfileComplete(asyncResult.exception == null ? 0 : 4);
                            return;
                        case 5:
                            int i2 = asyncResult.exception == null ? 0 : 4;
                            Object obj = asyncResult.result;
                            dataServiceCallback.onRequestDataCallListComplete(i2, obj != null ? (List) obj : Collections.EMPTY_LIST);
                            return;
                        case 6:
                            CellularDataServiceProvider.this.notifyDataCallListChanged((List) asyncResult.result);
                            return;
                        case 7:
                            dataServiceCallback.onHandoverStarted(CellularDataServiceProvider.this.toResultCode(asyncResult.exception));
                            return;
                        case 8:
                            dataServiceCallback.onHandoverCancelled(CellularDataServiceProvider.this.toResultCode(asyncResult.exception));
                            return;
                        case 9:
                            Object obj2 = asyncResult.result;
                            if (obj2 instanceof DataProfile) {
                                CellularDataServiceProvider.this.notifyDataProfileUnthrottled((DataProfile) obj2);
                                return;
                            } else {
                                CellularDataServiceProvider.this.notifyApnUnthrottled((String) obj2);
                                return;
                            }
                        default:
                            CellularDataService cellularDataService = CellularDataService.this;
                            cellularDataService.loge("Unexpected event: " + message.what);
                            return;
                    }
                }
            };
            this.mHandler = handler;
            phone.mCi.registerForDataCallListChanged(handler, 6, null);
            phone.mCi.registerForApnUnthrottled(handler, 9, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int toResultCode(Throwable th) {
            if (th == null) {
                return 0;
            }
            if (th instanceof CommandException) {
                return ((CommandException) th).getCommandError() == CommandException.Error.REQUEST_NOT_SUPPORTED ? 1 : 4;
            }
            CellularDataService cellularDataService = CellularDataService.this;
            cellularDataService.loge("Throwable is of type " + th.getClass().getSimpleName() + " but should be CommandException");
            return 4;
        }

        public void setupDataCall(int i, DataProfile dataProfile, boolean z, boolean z2, int i2, LinkProperties linkProperties, int i3, NetworkSliceInfo networkSliceInfo, TrafficDescriptor trafficDescriptor, boolean z3, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 1);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.setupDataCall(i, dataProfile, z, z2, i2, linkProperties, i3, networkSliceInfo, trafficDescriptor, z3, message);
        }

        public void deactivateDataCall(int i, int i2, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 2);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.deactivateDataCall(i, i2, message);
        }

        public void setInitialAttachApn(DataProfile dataProfile, boolean z, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 3);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.setInitialAttachApn(dataProfile, z, message);
        }

        public void setDataProfile(List<DataProfile> list, boolean z, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 4);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.setDataProfile((DataProfile[]) list.toArray(new DataProfile[list.size()]), z, message);
        }

        public void requestDataCallList(DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 5);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.getDataCallList(message);
        }

        public void startHandover(int i, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 7);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.startHandover(message, i);
        }

        public void cancelHandover(int i, DataServiceCallback dataServiceCallback) {
            Message message;
            if (dataServiceCallback != null) {
                message = Message.obtain(this.mHandler, 8);
                this.mCallbackMap.put(message, dataServiceCallback);
            } else {
                message = null;
            }
            this.mPhone.mCi.cancelHandover(message, i);
        }

        public void close() {
            this.mPhone.mCi.unregisterForDataCallListChanged(this.mHandler);
        }
    }

    public DataService.DataServiceProvider onCreateDataServiceProvider(int i) {
        log("Cellular data service created for slot " + i);
        if (!SubscriptionManager.isValidSlotIndex(i)) {
            loge("Tried to cellular data service with invalid slotId " + i);
            return null;
        }
        return new CellularDataServiceProvider(i);
    }

    private void log(String str) {
        Rlog.d(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String str) {
        Rlog.e(TAG, str);
    }
}
