package com.android.internal.telephony;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes3.dex */
public interface IMms extends IInterface {
    Uri addMultimediaMessageDraft(String str, Uri uri) throws RemoteException;

    Uri addTextMessageDraft(String str, String str2, String str3) throws RemoteException;

    boolean archiveStoredConversation(String str, long j, boolean z) throws RemoteException;

    boolean deleteStoredConversation(String str, long j) throws RemoteException;

    boolean deleteStoredMessage(String str, Uri uri) throws RemoteException;

    void downloadMessage(int i, String str, String str2, Uri uri, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException;

    boolean getAutoPersisting() throws RemoteException;

    Uri importMultimediaMessage(String str, Uri uri, String str2, long j, boolean z, boolean z2) throws RemoteException;

    Uri importTextMessage(String str, String str2, int i, String str3, long j, boolean z, boolean z2) throws RemoteException;

    void sendMessage(int i, String str, Uri uri, String str2, Bundle bundle, PendingIntent pendingIntent, long j, String str3) throws RemoteException;

    void sendStoredMessage(int i, String str, Uri uri, Bundle bundle, PendingIntent pendingIntent) throws RemoteException;

    void setAutoPersisting(String str, boolean z) throws RemoteException;

    boolean updateStoredMessageStatus(String str, Uri uri, ContentValues contentValues) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IMms {
        @Override // com.android.internal.telephony.IMms
        public void sendMessage(int subId, String callingPkg, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, long messageId, String attributionTag) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IMms
        public void downloadMessage(int subId, String callingPkg, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, long messageId, String attributionTag) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IMms
        public Uri importTextMessage(String callingPkg, String address, int type, String text, long timestampMillis, boolean seen, boolean read) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.IMms
        public Uri importMultimediaMessage(String callingPkg, Uri contentUri, String messageId, long timestampSecs, boolean seen, boolean read) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.IMms
        public boolean deleteStoredMessage(String callingPkg, Uri messageUri) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IMms
        public boolean deleteStoredConversation(String callingPkg, long conversationId) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IMms
        public boolean updateStoredMessageStatus(String callingPkg, Uri messageUri, ContentValues statusValues) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IMms
        public boolean archiveStoredConversation(String callingPkg, long conversationId, boolean archived) throws RemoteException {
            return false;
        }

        @Override // com.android.internal.telephony.IMms
        public Uri addTextMessageDraft(String callingPkg, String address, String text) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.IMms
        public Uri addMultimediaMessageDraft(String callingPkg, Uri contentUri) throws RemoteException {
            return null;
        }

        @Override // com.android.internal.telephony.IMms
        public void sendStoredMessage(int subId, String callingPkg, Uri messageUri, Bundle configOverrides, PendingIntent sentIntent) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IMms
        public void setAutoPersisting(String callingPkg, boolean enabled) throws RemoteException {
        }

        @Override // com.android.internal.telephony.IMms
        public boolean getAutoPersisting() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IMms {
        public static final String DESCRIPTOR = "com.android.internal.telephony.IMms";
        static final int TRANSACTION_addMultimediaMessageDraft = 10;
        static final int TRANSACTION_addTextMessageDraft = 9;
        static final int TRANSACTION_archiveStoredConversation = 8;
        static final int TRANSACTION_deleteStoredConversation = 6;
        static final int TRANSACTION_deleteStoredMessage = 5;
        static final int TRANSACTION_downloadMessage = 2;
        static final int TRANSACTION_getAutoPersisting = 13;
        static final int TRANSACTION_importMultimediaMessage = 4;
        static final int TRANSACTION_importTextMessage = 3;
        static final int TRANSACTION_sendMessage = 1;
        static final int TRANSACTION_sendStoredMessage = 11;
        static final int TRANSACTION_setAutoPersisting = 12;
        static final int TRANSACTION_updateStoredMessageStatus = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IMms asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IMms)) {
                return (IMms) iin;
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
                    return "sendMessage";
                case 2:
                    return "downloadMessage";
                case 3:
                    return "importTextMessage";
                case 4:
                    return "importMultimediaMessage";
                case 5:
                    return "deleteStoredMessage";
                case 6:
                    return "deleteStoredConversation";
                case 7:
                    return "updateStoredMessageStatus";
                case 8:
                    return "archiveStoredConversation";
                case 9:
                    return "addTextMessageDraft";
                case 10:
                    return "addMultimediaMessageDraft";
                case 11:
                    return "sendStoredMessage";
                case 12:
                    return "setAutoPersisting";
                case 13:
                    return "getAutoPersisting";
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
                            int _arg0 = data.readInt();
                            String _arg1 = data.readString();
                            Uri _arg2 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg3 = data.readString();
                            Bundle _arg4 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            PendingIntent _arg5 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            long _arg6 = data.readLong();
                            String _arg7 = data.readString();
                            data.enforceNoDataAvail();
                            sendMessage(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            String _arg22 = data.readString();
                            Uri _arg32 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg42 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            PendingIntent _arg52 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            long _arg62 = data.readLong();
                            String _arg72 = data.readString();
                            data.enforceNoDataAvail();
                            downloadMessage(_arg02, _arg12, _arg22, _arg32, _arg42, _arg52, _arg62, _arg72);
                            reply.writeNoException();
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            String _arg13 = data.readString();
                            int _arg23 = data.readInt();
                            String _arg33 = data.readString();
                            long _arg43 = data.readLong();
                            boolean _arg53 = data.readBoolean();
                            boolean _arg63 = data.readBoolean();
                            data.enforceNoDataAvail();
                            Uri _result = importTextMessage(_arg03, _arg13, _arg23, _arg33, _arg43, _arg53, _arg63);
                            reply.writeNoException();
                            reply.writeTypedObject(_result, 1);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            Uri _arg14 = (Uri) data.readTypedObject(Uri.CREATOR);
                            String _arg24 = data.readString();
                            long _arg34 = data.readLong();
                            boolean _arg44 = data.readBoolean();
                            boolean _arg54 = data.readBoolean();
                            data.enforceNoDataAvail();
                            Uri _result2 = importMultimediaMessage(_arg04, _arg14, _arg24, _arg34, _arg44, _arg54);
                            reply.writeNoException();
                            reply.writeTypedObject(_result2, 1);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            Uri _arg15 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result3 = deleteStoredMessage(_arg05, _arg15);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            long _arg16 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result4 = deleteStoredConversation(_arg06, _arg16);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            Uri _arg17 = (Uri) data.readTypedObject(Uri.CREATOR);
                            ContentValues _arg25 = (ContentValues) data.readTypedObject(ContentValues.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result5 = updateStoredMessageStatus(_arg07, _arg17, _arg25);
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            long _arg18 = data.readLong();
                            boolean _arg26 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result6 = archiveStoredConversation(_arg08, _arg18, _arg26);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            String _arg19 = data.readString();
                            String _arg27 = data.readString();
                            data.enforceNoDataAvail();
                            Uri _result7 = addTextMessageDraft(_arg09, _arg19, _arg27);
                            reply.writeNoException();
                            reply.writeTypedObject(_result7, 1);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            Uri _arg110 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            Uri _result8 = addMultimediaMessageDraft(_arg010, _arg110);
                            reply.writeNoException();
                            reply.writeTypedObject(_result8, 1);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            String _arg111 = data.readString();
                            Uri _arg28 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Bundle _arg35 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            PendingIntent _arg45 = (PendingIntent) data.readTypedObject(PendingIntent.CREATOR);
                            data.enforceNoDataAvail();
                            sendStoredMessage(_arg011, _arg111, _arg28, _arg35, _arg45);
                            reply.writeNoException();
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setAutoPersisting(_arg012, _arg112);
                            reply.writeNoException();
                            break;
                        case 13:
                            boolean _result9 = getAutoPersisting();
                            reply.writeNoException();
                            reply.writeBoolean(_result9);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes3.dex */
        private static class Proxy implements IMms {
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

            @Override // com.android.internal.telephony.IMms
            public void sendMessage(int subId, String callingPkg, Uri contentUri, String locationUrl, Bundle configOverrides, PendingIntent sentIntent, long messageId, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(contentUri, 0);
                    _data.writeString(locationUrl);
                    _data.writeTypedObject(configOverrides, 0);
                    _data.writeTypedObject(sentIntent, 0);
                    _data.writeLong(messageId);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public void downloadMessage(int subId, String callingPkg, String locationUrl, Uri contentUri, Bundle configOverrides, PendingIntent downloadedIntent, long messageId, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeString(locationUrl);
                    _data.writeTypedObject(contentUri, 0);
                    _data.writeTypedObject(configOverrides, 0);
                    _data.writeTypedObject(downloadedIntent, 0);
                    _data.writeLong(messageId);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public Uri importTextMessage(String callingPkg, String address, int type, String text, long timestampMillis, boolean seen, boolean read) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(address);
                    _data.writeInt(type);
                    _data.writeString(text);
                    _data.writeLong(timestampMillis);
                    _data.writeBoolean(seen);
                    _data.writeBoolean(read);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    Uri _result = (Uri) _reply.readTypedObject(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public Uri importMultimediaMessage(String callingPkg, Uri contentUri, String messageId, long timestampSecs, boolean seen, boolean read) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(contentUri, 0);
                    _data.writeString(messageId);
                    _data.writeLong(timestampSecs);
                    _data.writeBoolean(seen);
                    _data.writeBoolean(read);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    Uri _result = (Uri) _reply.readTypedObject(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public boolean deleteStoredMessage(String callingPkg, Uri messageUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(messageUri, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public boolean deleteStoredConversation(String callingPkg, long conversationId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeLong(conversationId);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public boolean updateStoredMessageStatus(String callingPkg, Uri messageUri, ContentValues statusValues) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(messageUri, 0);
                    _data.writeTypedObject(statusValues, 0);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public boolean archiveStoredConversation(String callingPkg, long conversationId, boolean archived) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeLong(conversationId);
                    _data.writeBoolean(archived);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public Uri addTextMessageDraft(String callingPkg, String address, String text) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeString(address);
                    _data.writeString(text);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    Uri _result = (Uri) _reply.readTypedObject(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public Uri addMultimediaMessageDraft(String callingPkg, Uri contentUri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(contentUri, 0);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    Uri _result = (Uri) _reply.readTypedObject(Uri.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public void sendStoredMessage(int subId, String callingPkg, Uri messageUri, Bundle configOverrides, PendingIntent sentIntent) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(subId);
                    _data.writeString(callingPkg);
                    _data.writeTypedObject(messageUri, 0);
                    _data.writeTypedObject(configOverrides, 0);
                    _data.writeTypedObject(sentIntent, 0);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public void setAutoPersisting(String callingPkg, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPkg);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telephony.IMms
            public boolean getAutoPersisting() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 12;
        }
    }
}
