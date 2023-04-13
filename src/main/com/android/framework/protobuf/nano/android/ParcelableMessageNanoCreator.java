package com.android.framework.protobuf.nano.android;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
import com.android.framework.protobuf.nano.InvalidProtocolBufferNanoException;
import com.android.framework.protobuf.nano.MessageNano;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
/* loaded from: classes4.dex */
public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Parcelable.Creator<T> {
    private static final String TAG = "PMNCreator";
    private final Class<T> mClazz;

    public ParcelableMessageNanoCreator(Class<T> clazz) {
        this.mClazz = clazz;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v4, types: [com.android.framework.protobuf.nano.MessageNano] */
    @Override // android.p008os.Parcelable.Creator
    public T createFromParcel(Parcel in) {
        String className = in.readString();
        byte[] data = in.createByteArray();
        T proto = null;
        try {
            Class<?> clazz = Class.forName(className, false, getClass().getClassLoader()).asSubclass(MessageNano.class);
            Object instance = clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            proto = (MessageNano) instance;
            MessageNano.mergeFrom(proto, data);
            return proto;
        } catch (InvalidProtocolBufferNanoException e) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e);
            return proto;
        } catch (ClassNotFoundException e2) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e2);
            return proto;
        } catch (IllegalAccessException e3) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e3);
            return proto;
        } catch (InstantiationException e4) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e4);
            return proto;
        } catch (NoSuchMethodException e5) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e5);
            return proto;
        } catch (InvocationTargetException e6) {
            Log.m109e(TAG, "Exception trying to create proto from parcel", e6);
            return proto;
        }
    }

    @Override // android.p008os.Parcelable.Creator
    public T[] newArray(int i) {
        return (T[]) ((MessageNano[]) Array.newInstance((Class<?>) this.mClazz, i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T extends MessageNano> void writeToParcel(Class<T> clazz, MessageNano message, Parcel out) {
        out.writeString(clazz.getName());
        out.writeByteArray(MessageNano.toByteArray(message));
    }
}
