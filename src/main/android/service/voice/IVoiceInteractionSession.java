package android.service.voice;

import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
import android.content.Intent;
import android.graphics.Bitmap;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.ThreadedRenderer;
import com.android.internal.app.IVoiceInteractionSessionShowCallback;
/* loaded from: classes3.dex */
public interface IVoiceInteractionSession extends IInterface {
    void closeSystemDialogs() throws RemoteException;

    void destroy() throws RemoteException;

    void handleAssist(int i, IBinder iBinder, Bundle bundle, AssistStructure assistStructure, AssistContent assistContent, int i2, int i3) throws RemoteException;

    void handleScreenshot(Bitmap bitmap) throws RemoteException;

    void hide() throws RemoteException;

    void notifyVisibleActivityInfoChanged(VisibleActivityInfo visibleActivityInfo, int i) throws RemoteException;

    void onLockscreenShown() throws RemoteException;

    void show(Bundle bundle, int i, IVoiceInteractionSessionShowCallback iVoiceInteractionSessionShowCallback) throws RemoteException;

    void taskFinished(Intent intent, int i) throws RemoteException;

    void taskStarted(Intent intent, int i) throws RemoteException;

    /* loaded from: classes3.dex */
    public static class Default implements IVoiceInteractionSession {
        @Override // android.service.voice.IVoiceInteractionSession
        public void show(Bundle sessionArgs, int flags, IVoiceInteractionSessionShowCallback showCallback) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void hide() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void handleAssist(int taskId, IBinder activityId, Bundle assistData, AssistStructure structure, AssistContent content, int index, int count) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void handleScreenshot(Bitmap screenshot) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void taskStarted(Intent intent, int taskId) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void taskFinished(Intent intent, int taskId) throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void closeSystemDialogs() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void onLockscreenShown() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void destroy() throws RemoteException {
        }

        @Override // android.service.voice.IVoiceInteractionSession
        public void notifyVisibleActivityInfoChanged(VisibleActivityInfo visibleActivityInfo, int type) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IVoiceInteractionSession {
        public static final String DESCRIPTOR = "android.service.voice.IVoiceInteractionSession";
        static final int TRANSACTION_closeSystemDialogs = 7;
        static final int TRANSACTION_destroy = 9;
        static final int TRANSACTION_handleAssist = 3;
        static final int TRANSACTION_handleScreenshot = 4;
        static final int TRANSACTION_hide = 2;
        static final int TRANSACTION_notifyVisibleActivityInfoChanged = 10;
        static final int TRANSACTION_onLockscreenShown = 8;
        static final int TRANSACTION_show = 1;
        static final int TRANSACTION_taskFinished = 6;
        static final int TRANSACTION_taskStarted = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVoiceInteractionSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IVoiceInteractionSession)) {
                return (IVoiceInteractionSession) iin;
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
                    return ThreadedRenderer.OVERDRAW_PROPERTY_SHOW;
                case 2:
                    return "hide";
                case 3:
                    return "handleAssist";
                case 4:
                    return "handleScreenshot";
                case 5:
                    return "taskStarted";
                case 6:
                    return "taskFinished";
                case 7:
                    return "closeSystemDialogs";
                case 8:
                    return "onLockscreenShown";
                case 9:
                    return "destroy";
                case 10:
                    return "notifyVisibleActivityInfoChanged";
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
                            Bundle _arg0 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            int _arg1 = data.readInt();
                            IVoiceInteractionSessionShowCallback _arg2 = IVoiceInteractionSessionShowCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            show(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            hide();
                            break;
                        case 3:
                            int _arg02 = data.readInt();
                            IBinder _arg12 = data.readStrongBinder();
                            Bundle _arg22 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            AssistStructure _arg3 = (AssistStructure) data.readTypedObject(AssistStructure.CREATOR);
                            AssistContent _arg4 = (AssistContent) data.readTypedObject(AssistContent.CREATOR);
                            int _arg5 = data.readInt();
                            int _arg6 = data.readInt();
                            data.enforceNoDataAvail();
                            handleAssist(_arg02, _arg12, _arg22, _arg3, _arg4, _arg5, _arg6);
                            break;
                        case 4:
                            Bitmap _arg03 = (Bitmap) data.readTypedObject(Bitmap.CREATOR);
                            data.enforceNoDataAvail();
                            handleScreenshot(_arg03);
                            break;
                        case 5:
                            Intent _arg04 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            taskStarted(_arg04, _arg13);
                            break;
                        case 6:
                            Intent _arg05 = (Intent) data.readTypedObject(Intent.CREATOR);
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            taskFinished(_arg05, _arg14);
                            break;
                        case 7:
                            closeSystemDialogs();
                            break;
                        case 8:
                            onLockscreenShown();
                            break;
                        case 9:
                            destroy();
                            break;
                        case 10:
                            VisibleActivityInfo _arg06 = (VisibleActivityInfo) data.readTypedObject(VisibleActivityInfo.CREATOR);
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            notifyVisibleActivityInfoChanged(_arg06, _arg15);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes3.dex */
        public static class Proxy implements IVoiceInteractionSession {
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

            @Override // android.service.voice.IVoiceInteractionSession
            public void show(Bundle sessionArgs, int flags, IVoiceInteractionSessionShowCallback showCallback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionArgs, 0);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(showCallback);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void hide() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleAssist(int taskId, IBinder activityId, Bundle assistData, AssistStructure structure, AssistContent content, int index, int count) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(taskId);
                    _data.writeStrongBinder(activityId);
                    _data.writeTypedObject(assistData, 0);
                    _data.writeTypedObject(structure, 0);
                    _data.writeTypedObject(content, 0);
                    _data.writeInt(index);
                    _data.writeInt(count);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void handleScreenshot(Bitmap screenshot) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(screenshot, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskStarted(Intent intent, int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(taskId);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void taskFinished(Intent intent, int taskId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(intent, 0);
                    _data.writeInt(taskId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void closeSystemDialogs() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void onLockscreenShown() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void destroy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.service.voice.IVoiceInteractionSession
            public void notifyVisibleActivityInfoChanged(VisibleActivityInfo visibleActivityInfo, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(visibleActivityInfo, 0);
                    _data.writeInt(type);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 9;
        }
    }
}
