package com.android.internal.telephony.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import com.android.internal.telephony.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.internal.telephony.protobuf.nano.MessageNano;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
/* loaded from: classes.dex */
public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Parcelable.Creator<T> {
    private final Class<T> mClazz;

    public ParcelableMessageNanoCreator(Class<T> cls) {
        this.mClazz = cls;
    }

    @Override // android.os.Parcelable.Creator
    public T createFromParcel(Parcel parcel) {
        String readString = parcel.readString();
        byte[] createByteArray = parcel.createByteArray();
        T t = null;
        try {
            T t2 = (T) Class.forName(readString, false, ParcelableMessageNanoCreator.class.getClassLoader()).asSubclass(MessageNano.class).getConstructor(new Class[0]).newInstance(new Object[0]);
            try {
                MessageNano.mergeFrom(t2, createByteArray);
                return t2;
            } catch (InvalidProtocolBufferNanoException e) {
                e = e;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            } catch (ClassNotFoundException e2) {
                e = e2;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            } catch (IllegalAccessException e3) {
                e = e3;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            } catch (InstantiationException e4) {
                e = e4;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            } catch (NoSuchMethodException e5) {
                e = e5;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            } catch (InvocationTargetException e6) {
                e = e6;
                t = t2;
                Log.e("PMNCreator", "Exception trying to create proto from parcel", e);
                return t;
            }
        } catch (InvalidProtocolBufferNanoException e7) {
            e = e7;
        } catch (ClassNotFoundException e8) {
            e = e8;
        } catch (IllegalAccessException e9) {
            e = e9;
        } catch (InstantiationException e10) {
            e = e10;
        } catch (NoSuchMethodException e11) {
            e = e11;
        } catch (InvocationTargetException e12) {
            e = e12;
        }
    }

    @Override // android.os.Parcelable.Creator
    public T[] newArray(int i) {
        return (T[]) ((MessageNano[]) Array.newInstance((Class<?>) this.mClazz, i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T extends MessageNano> void writeToParcel(Class<T> cls, MessageNano messageNano, Parcel parcel) {
        parcel.writeString(cls.getName());
        parcel.writeByteArray(MessageNano.toByteArray(messageNano));
    }
}
