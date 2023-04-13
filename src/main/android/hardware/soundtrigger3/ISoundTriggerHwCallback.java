package android.hardware.soundtrigger3;

import android.media.soundtrigger.PhraseRecognitionEvent;
import android.media.soundtrigger.RecognitionEvent;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface ISoundTriggerHwCallback extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$soundtrigger3$ISoundTriggerHwCallback".replace('$', '.');
    public static final String HASH = "7d8d63478cd50e766d2072140c8aa3457f9fb585";
    public static final int VERSION = 1;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void modelUnloaded(int i) throws RemoteException;

    void phraseRecognitionCallback(int i, PhraseRecognitionEvent phraseRecognitionEvent) throws RemoteException;

    void recognitionCallback(int i, RecognitionEvent recognitionEvent) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISoundTriggerHwCallback {
        @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
        public void modelUnloaded(int model) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
        public void phraseRecognitionCallback(int model, PhraseRecognitionEvent event) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
        public void recognitionCallback(int model, RecognitionEvent event) throws RemoteException {
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISoundTriggerHwCallback {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_modelUnloaded = 1;
        static final int TRANSACTION_phraseRecognitionCallback = 2;
        static final int TRANSACTION_recognitionCallback = 3;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static ISoundTriggerHwCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof ISoundTriggerHwCallback)) {
                return (ISoundTriggerHwCallback) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            modelUnloaded(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            PhraseRecognitionEvent _arg1 = (PhraseRecognitionEvent) data.readTypedObject(PhraseRecognitionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            phraseRecognitionCallback(_arg02, _arg1);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            RecognitionEvent _arg12 = (RecognitionEvent) data.readTypedObject(RecognitionEvent.CREATOR);
                            data.enforceNoDataAvail();
                            recognitionCallback(_arg03, _arg12);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISoundTriggerHwCallback {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
            public void modelUnloaded(int model) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(model);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method modelUnloaded is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
            public void phraseRecognitionCallback(int model, PhraseRecognitionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(model);
                    _data.writeTypedObject(event, 0);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method phraseRecognitionCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
            public void recognitionCallback(int model, RecognitionEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(model);
                    _data.writeTypedObject(event, 0);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method recognitionCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.soundtrigger3.ISoundTriggerHwCallback
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }
    }
}
