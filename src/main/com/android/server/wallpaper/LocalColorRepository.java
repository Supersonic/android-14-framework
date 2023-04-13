package com.android.server.wallpaper;

import android.app.ILocalWallpaperColorConsumer;
import android.graphics.RectF;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class LocalColorRepository {
    public ArrayMap<IBinder, SparseArray<ArraySet<RectF>>> mLocalColorAreas = new ArrayMap<>();
    public RemoteCallbackList<ILocalWallpaperColorConsumer> mCallbacks = new RemoteCallbackList<>();

    public void addAreas(final ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i) {
        ArraySet<RectF> arraySet;
        IBinder asBinder = iLocalWallpaperColorConsumer.asBinder();
        SparseArray<ArraySet<RectF>> sparseArray = this.mLocalColorAreas.get(asBinder);
        if (sparseArray == null) {
            try {
                iLocalWallpaperColorConsumer.asBinder().linkToDeath(new IBinder.DeathRecipient() { // from class: com.android.server.wallpaper.LocalColorRepository$$ExternalSyntheticLambda0
                    @Override // android.os.IBinder.DeathRecipient
                    public final void binderDied() {
                        LocalColorRepository.this.lambda$addAreas$0(iLocalWallpaperColorConsumer);
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            sparseArray = new SparseArray<>();
            this.mLocalColorAreas.put(asBinder, sparseArray);
            arraySet = null;
        } else {
            arraySet = sparseArray.get(i);
        }
        if (arraySet == null) {
            arraySet = new ArraySet<>(list);
            sparseArray.put(i, arraySet);
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            arraySet.add(list.get(i2));
        }
        this.mCallbacks.register(iLocalWallpaperColorConsumer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addAreas$0(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer) {
        this.mLocalColorAreas.remove(iLocalWallpaperColorConsumer.asBinder());
    }

    public List<RectF> removeAreas(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer, List<RectF> list, int i) {
        IBinder asBinder = iLocalWallpaperColorConsumer.asBinder();
        SparseArray<ArraySet<RectF>> sparseArray = this.mLocalColorAreas.get(asBinder);
        if (sparseArray != null) {
            ArraySet<RectF> arraySet = sparseArray.get(i);
            if (arraySet == null) {
                this.mCallbacks.unregister(iLocalWallpaperColorConsumer);
            } else {
                for (int i2 = 0; i2 < list.size(); i2++) {
                    arraySet.remove(list.get(i2));
                }
                if (arraySet.size() == 0) {
                    sparseArray.remove(i);
                }
            }
            if (sparseArray.size() == 0) {
                this.mLocalColorAreas.remove(asBinder);
                this.mCallbacks.unregister(iLocalWallpaperColorConsumer);
            }
        } else {
            this.mCallbacks.unregister(iLocalWallpaperColorConsumer);
        }
        ArraySet arraySet2 = new ArraySet(list);
        for (int i3 = 0; i3 < this.mLocalColorAreas.size(); i3++) {
            for (int i4 = 0; i4 < this.mLocalColorAreas.valueAt(i3).size(); i4++) {
                for (int i5 = 0; i5 < this.mLocalColorAreas.valueAt(i3).valueAt(i4).size(); i5++) {
                    arraySet2.remove(this.mLocalColorAreas.valueAt(i3).valueAt(i4).valueAt(i5));
                }
            }
        }
        return new ArrayList(arraySet2);
    }

    public List<RectF> getAreasByDisplayId(int i) {
        ArraySet<RectF> arraySet;
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.mLocalColorAreas.size(); i2++) {
            SparseArray<ArraySet<RectF>> valueAt = this.mLocalColorAreas.valueAt(i2);
            if (valueAt != null && (arraySet = valueAt.get(i)) != null) {
                for (int i3 = 0; i3 < arraySet.size(); i3++) {
                    arrayList.add(arraySet.valueAt(i3));
                }
            }
        }
        return arrayList;
    }

    public void forEachCallback(final Consumer<ILocalWallpaperColorConsumer> consumer, final RectF rectF, final int i) {
        this.mCallbacks.broadcast(new Consumer() { // from class: com.android.server.wallpaper.LocalColorRepository$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                LocalColorRepository.this.lambda$forEachCallback$1(i, rectF, consumer, (ILocalWallpaperColorConsumer) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$forEachCallback$1(int i, RectF rectF, Consumer consumer, ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer) {
        ArraySet<RectF> arraySet;
        SparseArray<ArraySet<RectF>> sparseArray = this.mLocalColorAreas.get(iLocalWallpaperColorConsumer.asBinder());
        if (sparseArray == null || (arraySet = sparseArray.get(i)) == null || !arraySet.contains(rectF)) {
            return;
        }
        consumer.accept(iLocalWallpaperColorConsumer);
    }

    @VisibleForTesting
    public boolean isCallbackAvailable(ILocalWallpaperColorConsumer iLocalWallpaperColorConsumer) {
        return this.mLocalColorAreas.get(iLocalWallpaperColorConsumer.asBinder()) != null;
    }
}
