package com.android.internal.appwidget;

import android.app.IApplicationThread;
import android.app.IServiceConnection;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.widget.RemoteViews;
import com.android.internal.appwidget.IAppWidgetHost;
/* loaded from: classes4.dex */
public interface IAppWidgetService extends IInterface {
    int allocateAppWidgetId(String str, int i) throws RemoteException;

    boolean bindAppWidgetId(String str, int i, int i2, ComponentName componentName, Bundle bundle) throws RemoteException;

    boolean bindRemoteViewsService(String str, int i, Intent intent, IApplicationThread iApplicationThread, IBinder iBinder, IServiceConnection iServiceConnection, long j) throws RemoteException;

    IntentSender createAppWidgetConfigIntentSender(String str, int i, int i2) throws RemoteException;

    void deleteAllHosts() throws RemoteException;

    void deleteAppWidgetId(String str, int i) throws RemoteException;

    void deleteHost(String str, int i) throws RemoteException;

    int[] getAppWidgetIds(ComponentName componentName) throws RemoteException;

    int[] getAppWidgetIdsForHost(String str, int i) throws RemoteException;

    AppWidgetProviderInfo getAppWidgetInfo(String str, int i) throws RemoteException;

    Bundle getAppWidgetOptions(String str, int i) throws RemoteException;

    RemoteViews getAppWidgetViews(String str, int i) throws RemoteException;

    ParceledListSlice getInstalledProvidersForProfile(int i, int i2, String str) throws RemoteException;

    boolean hasBindAppWidgetPermission(String str, int i) throws RemoteException;

    boolean isBoundWidgetPackage(String str, int i) throws RemoteException;

    boolean isRequestPinAppWidgetSupported() throws RemoteException;

    void noteAppWidgetTapped(String str, int i) throws RemoteException;

    void notifyAppWidgetViewDataChanged(String str, int[] iArr, int i) throws RemoteException;

    void notifyProviderInheritance(ComponentName[] componentNameArr) throws RemoteException;

    void partiallyUpdateAppWidgetIds(String str, int[] iArr, RemoteViews remoteViews) throws RemoteException;

    boolean requestPinAppWidget(String str, ComponentName componentName, Bundle bundle, IntentSender intentSender) throws RemoteException;

    void setAppWidgetHidden(String str, int i) throws RemoteException;

    void setBindAppWidgetPermission(String str, int i, boolean z) throws RemoteException;

    ParceledListSlice startListening(IAppWidgetHost iAppWidgetHost, String str, int i, int[] iArr) throws RemoteException;

    void stopListening(String str, int i) throws RemoteException;

    void updateAppWidgetIds(String str, int[] iArr, RemoteViews remoteViews) throws RemoteException;

    void updateAppWidgetOptions(String str, int i, Bundle bundle) throws RemoteException;

    void updateAppWidgetProvider(ComponentName componentName, RemoteViews remoteViews) throws RemoteException;

    void updateAppWidgetProviderInfo(ComponentName componentName, String str) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAppWidgetService {
        @Override // com.android.internal.appwidget.IAppWidgetService
        public ParceledListSlice startListening(IAppWidgetHost host, String callingPackage, int hostId, int[] appWidgetIds) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void stopListening(String callingPackage, int hostId) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public int allocateAppWidgetId(String callingPackage, int hostId) throws RemoteException {
            return 0;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void deleteAppWidgetId(String callingPackage, int appWidgetId) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void deleteHost(String packageName, int hostId) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void deleteAllHosts() throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public RemoteViews getAppWidgetViews(String callingPackage, int appWidgetId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public int[] getAppWidgetIdsForHost(String callingPackage, int hostId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void setAppWidgetHidden(String callingPackage, int hostId) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public IntentSender createAppWidgetConfigIntentSender(String callingPackage, int appWidgetId, int intentFlags) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void updateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void updateAppWidgetOptions(String callingPackage, int appWidgetId, Bundle extras) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public Bundle getAppWidgetOptions(String callingPackage, int appWidgetId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void partiallyUpdateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void updateAppWidgetProvider(ComponentName provider, RemoteViews views) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void updateAppWidgetProviderInfo(ComponentName provider, String metadataKey) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void notifyAppWidgetViewDataChanged(String packageName, int[] appWidgetIds, int viewId) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public ParceledListSlice getInstalledProvidersForProfile(int categoryFilter, int profileId, String packageName) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public AppWidgetProviderInfo getAppWidgetInfo(String callingPackage, int appWidgetId) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean hasBindAppWidgetPermission(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void setBindAppWidgetPermission(String packageName, int userId, boolean permission) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean bindAppWidgetId(String callingPackage, int appWidgetId, int providerProfileId, ComponentName providerComponent, Bundle options) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean bindRemoteViewsService(String callingPackage, int appWidgetId, Intent intent, IApplicationThread caller, IBinder token, IServiceConnection connection, long flags) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void notifyProviderInheritance(ComponentName[] componentNames) throws RemoteException {
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public int[] getAppWidgetIds(ComponentName providerComponent) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean isBoundWidgetPackage(String packageName, int userId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean requestPinAppWidget(String packageName, ComponentName providerComponent, Bundle extras, IntentSender resultIntent) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public boolean isRequestPinAppWidgetSupported() throws RemoteException {
            return false;
        }

        @Override // com.android.internal.appwidget.IAppWidgetService
        public void noteAppWidgetTapped(String callingPackage, int appWidgetId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAppWidgetService {
        public static final String DESCRIPTOR = "com.android.internal.appwidget.IAppWidgetService";
        static final int TRANSACTION_allocateAppWidgetId = 3;
        static final int TRANSACTION_bindAppWidgetId = 22;
        static final int TRANSACTION_bindRemoteViewsService = 23;
        static final int TRANSACTION_createAppWidgetConfigIntentSender = 10;
        static final int TRANSACTION_deleteAllHosts = 6;
        static final int TRANSACTION_deleteAppWidgetId = 4;
        static final int TRANSACTION_deleteHost = 5;
        static final int TRANSACTION_getAppWidgetIds = 25;
        static final int TRANSACTION_getAppWidgetIdsForHost = 8;
        static final int TRANSACTION_getAppWidgetInfo = 19;
        static final int TRANSACTION_getAppWidgetOptions = 13;
        static final int TRANSACTION_getAppWidgetViews = 7;
        static final int TRANSACTION_getInstalledProvidersForProfile = 18;
        static final int TRANSACTION_hasBindAppWidgetPermission = 20;
        static final int TRANSACTION_isBoundWidgetPackage = 26;
        static final int TRANSACTION_isRequestPinAppWidgetSupported = 28;
        static final int TRANSACTION_noteAppWidgetTapped = 29;
        static final int TRANSACTION_notifyAppWidgetViewDataChanged = 17;
        static final int TRANSACTION_notifyProviderInheritance = 24;
        static final int TRANSACTION_partiallyUpdateAppWidgetIds = 14;
        static final int TRANSACTION_requestPinAppWidget = 27;
        static final int TRANSACTION_setAppWidgetHidden = 9;
        static final int TRANSACTION_setBindAppWidgetPermission = 21;
        static final int TRANSACTION_startListening = 1;
        static final int TRANSACTION_stopListening = 2;
        static final int TRANSACTION_updateAppWidgetIds = 11;
        static final int TRANSACTION_updateAppWidgetOptions = 12;
        static final int TRANSACTION_updateAppWidgetProvider = 15;
        static final int TRANSACTION_updateAppWidgetProviderInfo = 16;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IAppWidgetService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAppWidgetService)) {
                return (IAppWidgetService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "startListening";
                case 2:
                    return "stopListening";
                case 3:
                    return "allocateAppWidgetId";
                case 4:
                    return "deleteAppWidgetId";
                case 5:
                    return "deleteHost";
                case 6:
                    return "deleteAllHosts";
                case 7:
                    return "getAppWidgetViews";
                case 8:
                    return "getAppWidgetIdsForHost";
                case 9:
                    return "setAppWidgetHidden";
                case 10:
                    return "createAppWidgetConfigIntentSender";
                case 11:
                    return "updateAppWidgetIds";
                case 12:
                    return "updateAppWidgetOptions";
                case 13:
                    return "getAppWidgetOptions";
                case 14:
                    return "partiallyUpdateAppWidgetIds";
                case 15:
                    return "updateAppWidgetProvider";
                case 16:
                    return "updateAppWidgetProviderInfo";
                case 17:
                    return "notifyAppWidgetViewDataChanged";
                case 18:
                    return "getInstalledProvidersForProfile";
                case 19:
                    return "getAppWidgetInfo";
                case 20:
                    return "hasBindAppWidgetPermission";
                case 21:
                    return "setBindAppWidgetPermission";
                case 22:
                    return "bindAppWidgetId";
                case 23:
                    return "bindRemoteViewsService";
                case 24:
                    return "notifyProviderInheritance";
                case 25:
                    return "getAppWidgetIds";
                case 26:
                    return "isBoundWidgetPackage";
                case 27:
                    return "requestPinAppWidget";
                case 28:
                    return "isRequestPinAppWidgetSupported";
                case 29:
                    return "noteAppWidgetTapped";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IAppWidgetHost _arg0 = IAppWidgetHost.Stub.asInterface(data.readStrongBinder());
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            int[] _arg3 = data.createIntArray();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result = startListening(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            stopListening(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result2 = allocateAppWidgetId(_arg03, _arg13);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteAppWidgetId(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteHost(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            deleteAllHosts();
                            reply.writeNoException();
                            break;
                        case 7:
                            String _arg06 = data.readString();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            RemoteViews _result3 = getAppWidgetViews(_arg06, _arg16);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 8:
                            String _arg07 = data.readString();
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            int[] _result4 = getAppWidgetIdsForHost(_arg07, _arg17);
                            reply.writeNoException();
                            reply.writeIntArray(_result4);
                            break;
                        case 9:
                            String _arg08 = data.readString();
                            int _arg18 = data.readInt();
                            data.enforceNoDataAvail();
                            setAppWidgetHidden(_arg08, _arg18);
                            reply.writeNoException();
                            break;
                        case 10:
                            String _arg09 = data.readString();
                            int _arg19 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            IntentSender _result5 = createAppWidgetConfigIntentSender(_arg09, _arg19, _arg22);
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        case 11:
                            String _arg010 = data.readString();
                            int[] _arg110 = data.createIntArray();
                            RemoteViews _arg23 = (RemoteViews) data.readTypedObject(RemoteViews.CREATOR);
                            data.enforceNoDataAvail();
                            updateAppWidgetIds(_arg010, _arg110, _arg23);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg011 = data.readString();
                            int _arg111 = data.readInt();
                            Bundle _arg24 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            updateAppWidgetOptions(_arg011, _arg111, _arg24);
                            reply.writeNoException();
                            break;
                        case 13:
                            String _arg012 = data.readString();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            Bundle _result6 = getAppWidgetOptions(_arg012, _arg112);
                            reply.writeNoException();
                            reply.writeTypedObject(_result6, 1);
                            break;
                        case 14:
                            String _arg013 = data.readString();
                            int[] _arg113 = data.createIntArray();
                            RemoteViews _arg25 = (RemoteViews) data.readTypedObject(RemoteViews.CREATOR);
                            data.enforceNoDataAvail();
                            partiallyUpdateAppWidgetIds(_arg013, _arg113, _arg25);
                            reply.writeNoException();
                            break;
                        case 15:
                            ComponentName _arg014 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            RemoteViews _arg114 = (RemoteViews) data.readTypedObject(RemoteViews.CREATOR);
                            data.enforceNoDataAvail();
                            updateAppWidgetProvider(_arg014, _arg114);
                            reply.writeNoException();
                            break;
                        case 16:
                            ComponentName _arg015 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            String _arg115 = data.readString();
                            data.enforceNoDataAvail();
                            updateAppWidgetProviderInfo(_arg015, _arg115);
                            reply.writeNoException();
                            break;
                        case 17:
                            String _arg016 = data.readString();
                            int[] _arg116 = data.createIntArray();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyAppWidgetViewDataChanged(_arg016, _arg116, _arg26);
                            reply.writeNoException();
                            break;
                        case 18:
                            int _arg017 = data.readInt();
                            int _arg117 = data.readInt();
                            String _arg27 = data.readString();
                            data.enforceNoDataAvail();
                            ParceledListSlice _result7 = getInstalledProvidersForProfile(_arg017, _arg117, _arg27);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 19:
                            String _arg018 = data.readString();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            AppWidgetProviderInfo _result8 = getAppWidgetInfo(_arg018, _arg118);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 20:
                            String _arg019 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result9 = hasBindAppWidgetPermission(_arg019, _arg119);
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        case 21:
                            String _arg020 = data.readString();
                            int _arg120 = data.readInt();
                            boolean _arg28 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBindAppWidgetPermission(_arg020, _arg120, _arg28);
                            reply.writeNoException();
                            break;
                        case 22:
                            String _arg021 = data.readString();
                            int _arg121 = data.readInt();
                            int _arg29 = data.readInt();
                            ComponentName _arg32 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result10 = bindAppWidgetId(_arg021, _arg121, _arg29, _arg32, _arg4);
                            reply.writeNoException();
                            reply.writeBoolean(_result10);
                            break;
                        case 23:
                            String _arg022 = data.readString();
                            int _arg122 = data.readInt();
                            Intent _arg210 = (Intent) data.readTypedObject(Intent.CREATOR);
                            IApplicationThread _arg33 = IApplicationThread.Stub.asInterface(data.readStrongBinder());
                            IBinder _arg42 = data.readStrongBinder();
                            IServiceConnection _arg5 = IServiceConnection.Stub.asInterface(data.readStrongBinder());
                            long _arg6 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result11 = bindRemoteViewsService(_arg022, _arg122, _arg210, _arg33, _arg42, _arg5, _arg6);
                            reply.writeNoException();
                            reply.writeBoolean(_result11);
                            break;
                        case 24:
                            ComponentName[] _arg023 = (ComponentName[]) data.createTypedArray(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            notifyProviderInheritance(_arg023);
                            reply.writeNoException();
                            break;
                        case 25:
                            ComponentName _arg024 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            data.enforceNoDataAvail();
                            int[] _result12 = getAppWidgetIds(_arg024);
                            reply.writeNoException();
                            reply.writeIntArray(_result12);
                            break;
                        case 26:
                            String _arg025 = data.readString();
                            int _arg123 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result13 = isBoundWidgetPackage(_arg025, _arg123);
                            reply.writeNoException();
                            reply.writeBoolean(_result13);
                            break;
                        case 27:
                            String _arg026 = data.readString();
                            ComponentName _arg124 = (ComponentName) data.readTypedObject(ComponentName.CREATOR);
                            Bundle _arg211 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            IntentSender _arg34 = (IntentSender) data.readTypedObject(IntentSender.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result14 = requestPinAppWidget(_arg026, _arg124, _arg211, _arg34);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            break;
                        case 28:
                            boolean _result15 = isRequestPinAppWidgetSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result15);
                            break;
                        case 29:
                            String _arg027 = data.readString();
                            int _arg125 = data.readInt();
                            data.enforceNoDataAvail();
                            noteAppWidgetTapped(_arg027, _arg125);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAppWidgetService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public ParceledListSlice startListening(IAppWidgetHost host, String callingPackage, int hostId, int[] appWidgetIds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(host);
                    _data.writeString(callingPackage);
                    _data.writeInt(hostId);
                    _data.writeIntArray(appWidgetIds);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void stopListening(String callingPackage, int hostId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(hostId);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int allocateAppWidgetId(String callingPackage, int hostId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(hostId);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteAppWidgetId(String callingPackage, int appWidgetId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteHost(String packageName, int hostId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(hostId);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void deleteAllHosts() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public RemoteViews getAppWidgetViews(String callingPackage, int appWidgetId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    RemoteViews _result = (RemoteViews) _reply.readTypedObject(RemoteViews.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int[] getAppWidgetIdsForHost(String callingPackage, int hostId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(hostId);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void setAppWidgetHidden(String callingPackage, int hostId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(hostId);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public IntentSender createAppWidgetConfigIntentSender(String callingPackage, int appWidgetId, int intentFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(intentFlags);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    IntentSender _result = (IntentSender) _reply.readTypedObject(IntentSender.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeIntArray(appWidgetIds);
                    _data.writeTypedObject(views, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetOptions(String callingPackage, int appWidgetId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public Bundle getAppWidgetOptions(String callingPackage, int appWidgetId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    Bundle _result = (Bundle) _reply.readTypedObject(Bundle.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void partiallyUpdateAppWidgetIds(String callingPackage, int[] appWidgetIds, RemoteViews views) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeIntArray(appWidgetIds);
                    _data.writeTypedObject(views, 0);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetProvider(ComponentName provider, RemoteViews views) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(provider, 0);
                    _data.writeTypedObject(views, 0);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void updateAppWidgetProviderInfo(ComponentName provider, String metadataKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(provider, 0);
                    _data.writeString(metadataKey);
                    this.mRemote.transact(16, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void notifyAppWidgetViewDataChanged(String packageName, int[] appWidgetIds, int viewId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeIntArray(appWidgetIds);
                    _data.writeInt(viewId);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public ParceledListSlice getInstalledProvidersForProfile(int categoryFilter, int profileId, String packageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(categoryFilter);
                    _data.writeInt(profileId);
                    _data.writeString(packageName);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                    ParceledListSlice _result = (ParceledListSlice) _reply.readTypedObject(ParceledListSlice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public AppWidgetProviderInfo getAppWidgetInfo(String callingPackage, int appWidgetId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    AppWidgetProviderInfo _result = (AppWidgetProviderInfo) _reply.readTypedObject(AppWidgetProviderInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean hasBindAppWidgetPermission(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void setBindAppWidgetPermission(String packageName, int userId, boolean permission) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    _data.writeBoolean(permission);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean bindAppWidgetId(String callingPackage, int appWidgetId, int providerProfileId, ComponentName providerComponent, Bundle options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    _data.writeInt(providerProfileId);
                    _data.writeTypedObject(providerComponent, 0);
                    _data.writeTypedObject(options, 0);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean bindRemoteViewsService(String callingPackage, int appWidgetId, Intent intent, IApplicationThread caller, IBinder token, IServiceConnection connection, long flags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    _data.writeTypedObject(intent, 0);
                    _data.writeStrongInterface(caller);
                    _data.writeStrongBinder(token);
                    _data.writeStrongInterface(connection);
                    _data.writeLong(flags);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void notifyProviderInheritance(ComponentName[] componentNames) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedArray(componentNames, 0);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public int[] getAppWidgetIds(ComponentName providerComponent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(providerComponent, 0);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean isBoundWidgetPackage(String packageName, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeInt(userId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean requestPinAppWidget(String packageName, ComponentName providerComponent, Bundle extras, IntentSender resultIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(packageName);
                    _data.writeTypedObject(providerComponent, 0);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(resultIntent, 0);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public boolean isRequestPinAppWidgetSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.appwidget.IAppWidgetService
            public void noteAppWidgetTapped(String callingPackage, int appWidgetId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    _data.writeInt(appWidgetId);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 28;
        }
    }
}
