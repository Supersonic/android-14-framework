package android.hardware.radio;

import android.annotation.SystemApi;
import android.hardware.radio.ProgramList;
import android.hardware.radio.ProgramSelector;
import android.hardware.radio.RadioManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.ArrayMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Collectors;
@SystemApi
/* loaded from: classes2.dex */
public final class ProgramList implements AutoCloseable {
    private boolean mIsClosed;
    private boolean mIsComplete;
    private OnCloseListener mOnCloseListener;
    private final Object mLock = new Object();
    private final Map<ProgramSelector.Identifier, RadioManager.ProgramInfo> mPrograms = new ArrayMap();
    private final List<ListCallback> mListCallbacks = new ArrayList();
    private final List<OnCompleteListener> mOnCompleteListeners = new ArrayList();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public interface OnCloseListener {
        void onClose();
    }

    /* loaded from: classes2.dex */
    public interface OnCompleteListener {
        void onComplete();
    }

    /* loaded from: classes2.dex */
    public static abstract class ListCallback {
        public void onItemChanged(ProgramSelector.Identifier id) {
        }

        public void onItemRemoved(ProgramSelector.Identifier id) {
        }
    }

    /* renamed from: android.hardware.radio.ProgramList$1 */
    /* loaded from: classes2.dex */
    class C12201 extends ListCallback {
        final /* synthetic */ ListCallback val$callback;
        final /* synthetic */ Executor val$executor;

        C12201(Executor executor, ListCallback listCallback) {
            this.val$executor = executor;
            this.val$callback = listCallback;
        }

        @Override // android.hardware.radio.ProgramList.ListCallback
        public void onItemChanged(final ProgramSelector.Identifier id) {
            Executor executor = this.val$executor;
            final ListCallback listCallback = this.val$callback;
            executor.execute(new Runnable() { // from class: android.hardware.radio.ProgramList$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ProgramList.ListCallback.this.onItemChanged(id);
                }
            });
        }

        @Override // android.hardware.radio.ProgramList.ListCallback
        public void onItemRemoved(final ProgramSelector.Identifier id) {
            Executor executor = this.val$executor;
            final ListCallback listCallback = this.val$callback;
            executor.execute(new Runnable() { // from class: android.hardware.radio.ProgramList$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ProgramList.ListCallback.this.onItemRemoved(id);
                }
            });
        }
    }

    public void registerListCallback(Executor executor, ListCallback callback) {
        registerListCallback(new C12201(executor, callback));
    }

    public void registerListCallback(ListCallback callback) {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mListCallbacks.add((ListCallback) Objects.requireNonNull(callback));
        }
    }

    public void unregisterListCallback(ListCallback callback) {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mListCallbacks.remove(Objects.requireNonNull(callback));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$addOnCompleteListener$0(Executor executor, final OnCompleteListener listener) {
        Objects.requireNonNull(listener);
        executor.execute(new Runnable() { // from class: android.hardware.radio.ProgramList$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ProgramList.OnCompleteListener.this.onComplete();
            }
        });
    }

    public void addOnCompleteListener(final Executor executor, final OnCompleteListener listener) {
        addOnCompleteListener(new OnCompleteListener() { // from class: android.hardware.radio.ProgramList$$ExternalSyntheticLambda1
            @Override // android.hardware.radio.ProgramList.OnCompleteListener
            public final void onComplete() {
                ProgramList.lambda$addOnCompleteListener$0(executor, listener);
            }
        });
    }

    public void addOnCompleteListener(OnCompleteListener listener) {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mOnCompleteListeners.add((OnCompleteListener) Objects.requireNonNull(listener));
            if (this.mIsComplete) {
                listener.onComplete();
            }
        }
    }

    public void removeOnCompleteListener(OnCompleteListener listener) {
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mOnCompleteListeners.remove(Objects.requireNonNull(listener));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnCloseListener(OnCloseListener listener) {
        synchronized (this.mLock) {
            if (this.mOnCloseListener != null) {
                throw new IllegalStateException("Close callback is already set");
            }
            this.mOnCloseListener = listener;
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        OnCloseListener onCompleteListenersCopied = null;
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mIsClosed = true;
            this.mPrograms.clear();
            this.mListCallbacks.clear();
            this.mOnCompleteListeners.clear();
            OnCloseListener onCloseListener = this.mOnCloseListener;
            if (onCloseListener != null) {
                onCompleteListenersCopied = onCloseListener;
                this.mOnCloseListener = null;
            }
            if (onCompleteListenersCopied != null) {
                onCompleteListenersCopied.onClose();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void apply(Chunk chunk) {
        final List<ProgramSelector.Identifier> removedList = new ArrayList<>();
        final List<ProgramSelector.Identifier> changedList = new ArrayList<>();
        List<OnCompleteListener> onCompleteListenersCopied = new ArrayList<>();
        synchronized (this.mLock) {
            if (this.mIsClosed) {
                return;
            }
            this.mIsComplete = false;
            List<ListCallback> listCallbacksCopied = new ArrayList<>(this.mListCallbacks);
            if (chunk.isPurge()) {
                Iterator<Map.Entry<ProgramSelector.Identifier, RadioManager.ProgramInfo>> programsIterator = this.mPrograms.entrySet().iterator();
                while (programsIterator.hasNext()) {
                    RadioManager.ProgramInfo removed = programsIterator.next().getValue();
                    if (removed != null) {
                        removedList.add(removed.getSelector().getPrimaryId());
                    }
                    programsIterator.remove();
                }
            }
            chunk.getRemoved().stream().forEach(new Consumer() { // from class: android.hardware.radio.ProgramList$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProgramList.this.lambda$apply$1(removedList, (ProgramSelector.Identifier) obj);
                }
            });
            chunk.getModified().stream().forEach(new Consumer() { // from class: android.hardware.radio.ProgramList$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProgramList.this.lambda$apply$2(changedList, (RadioManager.ProgramInfo) obj);
                }
            });
            if (chunk.isComplete()) {
                this.mIsComplete = true;
                onCompleteListenersCopied = new ArrayList<>(this.mOnCompleteListeners);
            }
            for (int i = 0; i < removedList.size(); i++) {
                for (int cbIndex = 0; cbIndex < listCallbacksCopied.size(); cbIndex++) {
                    listCallbacksCopied.get(cbIndex).onItemRemoved(removedList.get(i));
                }
            }
            for (int i2 = 0; i2 < changedList.size(); i2++) {
                for (int cbIndex2 = 0; cbIndex2 < listCallbacksCopied.size(); cbIndex2++) {
                    listCallbacksCopied.get(cbIndex2).onItemChanged(changedList.get(i2));
                }
            }
            if (chunk.isComplete()) {
                for (int cbIndex3 = 0; cbIndex3 < onCompleteListenersCopied.size(); cbIndex3++) {
                    onCompleteListenersCopied.get(cbIndex3).onComplete();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: putLocked */
    public void lambda$apply$2(RadioManager.ProgramInfo value, List<ProgramSelector.Identifier> changedIdentifierList) {
        ProgramSelector.Identifier key = value.getSelector().getPrimaryId();
        this.mPrograms.put((ProgramSelector.Identifier) Objects.requireNonNull(key), value);
        ProgramSelector.Identifier sel = value.getSelector().getPrimaryId();
        changedIdentifierList.add(sel);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: removeLocked */
    public void lambda$apply$1(ProgramSelector.Identifier key, List<ProgramSelector.Identifier> removedIdentifierList) {
        RadioManager.ProgramInfo removed = this.mPrograms.remove(Objects.requireNonNull(key));
        if (removed == null) {
            return;
        }
        ProgramSelector.Identifier sel = removed.getSelector().getPrimaryId();
        removedIdentifierList.add(sel);
    }

    public List<RadioManager.ProgramInfo> toList() {
        List<RadioManager.ProgramInfo> list;
        synchronized (this.mLock) {
            list = (List) this.mPrograms.values().stream().collect(Collectors.toList());
        }
        return list;
    }

    public RadioManager.ProgramInfo get(ProgramSelector.Identifier id) {
        RadioManager.ProgramInfo programInfo;
        synchronized (this.mLock) {
            programInfo = this.mPrograms.get(Objects.requireNonNull(id));
        }
        return programInfo;
    }

    /* loaded from: classes2.dex */
    public static final class Filter implements Parcelable {
        public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() { // from class: android.hardware.radio.ProgramList.Filter.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Filter createFromParcel(Parcel in) {
                return new Filter(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Filter[] newArray(int size) {
                return new Filter[size];
            }
        };
        private final boolean mExcludeModifications;
        private final Set<Integer> mIdentifierTypes;
        private final Set<ProgramSelector.Identifier> mIdentifiers;
        private final boolean mIncludeCategories;
        private final Map<String, String> mVendorFilter;

        public Filter(Set<Integer> identifierTypes, Set<ProgramSelector.Identifier> identifiers, boolean includeCategories, boolean excludeModifications) {
            this.mIdentifierTypes = (Set) Objects.requireNonNull(identifierTypes);
            this.mIdentifiers = (Set) Objects.requireNonNull(identifiers);
            this.mIncludeCategories = includeCategories;
            this.mExcludeModifications = excludeModifications;
            this.mVendorFilter = null;
        }

        public Filter() {
            this.mIdentifierTypes = Collections.emptySet();
            this.mIdentifiers = Collections.emptySet();
            this.mIncludeCategories = false;
            this.mExcludeModifications = false;
            this.mVendorFilter = null;
        }

        public Filter(Map<String, String> vendorFilter) {
            this.mIdentifierTypes = Collections.emptySet();
            this.mIdentifiers = Collections.emptySet();
            this.mIncludeCategories = false;
            this.mExcludeModifications = false;
            this.mVendorFilter = vendorFilter;
        }

        private Filter(Parcel in) {
            this.mIdentifierTypes = Utils.createIntSet(in);
            this.mIdentifiers = Utils.createSet(in, ProgramSelector.Identifier.CREATOR);
            this.mIncludeCategories = in.readByte() != 0;
            this.mExcludeModifications = in.readByte() != 0;
            this.mVendorFilter = Utils.readStringMap(in);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            Utils.writeIntSet(dest, this.mIdentifierTypes);
            Utils.writeSet(dest, this.mIdentifiers);
            dest.writeByte(this.mIncludeCategories ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mExcludeModifications ? (byte) 1 : (byte) 0);
            Utils.writeStringMap(dest, this.mVendorFilter);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public Map<String, String> getVendorFilter() {
            return this.mVendorFilter;
        }

        public Set<Integer> getIdentifierTypes() {
            return this.mIdentifierTypes;
        }

        public Set<ProgramSelector.Identifier> getIdentifiers() {
            return this.mIdentifiers;
        }

        public boolean areCategoriesIncluded() {
            return this.mIncludeCategories;
        }

        public boolean areModificationsExcluded() {
            return this.mExcludeModifications;
        }

        public int hashCode() {
            return Objects.hash(this.mIdentifierTypes, this.mIdentifiers, Boolean.valueOf(this.mIncludeCategories), Boolean.valueOf(this.mExcludeModifications));
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Filter) {
                Filter other = (Filter) obj;
                return this.mIncludeCategories == other.mIncludeCategories && this.mExcludeModifications == other.mExcludeModifications && Objects.equals(this.mIdentifierTypes, other.mIdentifierTypes) && Objects.equals(this.mIdentifiers, other.mIdentifiers);
            }
            return false;
        }

        public String toString() {
            return "Filter [mIdentifierTypes=" + this.mIdentifierTypes + ", mIdentifiers=" + this.mIdentifiers + ", mIncludeCategories=" + this.mIncludeCategories + ", mExcludeModifications=" + this.mExcludeModifications + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes2.dex */
    public static final class Chunk implements Parcelable {
        public static final Parcelable.Creator<Chunk> CREATOR = new Parcelable.Creator<Chunk>() { // from class: android.hardware.radio.ProgramList.Chunk.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Chunk createFromParcel(Parcel in) {
                return new Chunk(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public Chunk[] newArray(int size) {
                return new Chunk[size];
            }
        };
        private final boolean mComplete;
        private final Set<RadioManager.ProgramInfo> mModified;
        private final boolean mPurge;
        private final Set<ProgramSelector.Identifier> mRemoved;

        public Chunk(boolean purge, boolean complete, Set<RadioManager.ProgramInfo> modified, Set<ProgramSelector.Identifier> removed) {
            this.mPurge = purge;
            this.mComplete = complete;
            this.mModified = modified != null ? modified : Collections.emptySet();
            this.mRemoved = removed != null ? removed : Collections.emptySet();
        }

        private Chunk(Parcel in) {
            this.mPurge = in.readByte() != 0;
            this.mComplete = in.readByte() != 0;
            this.mModified = Utils.createSet(in, RadioManager.ProgramInfo.CREATOR);
            this.mRemoved = Utils.createSet(in, ProgramSelector.Identifier.CREATOR);
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.mPurge ? (byte) 1 : (byte) 0);
            dest.writeByte(this.mComplete ? (byte) 1 : (byte) 0);
            Utils.writeSet(dest, this.mModified);
            Utils.writeSet(dest, this.mRemoved);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean isPurge() {
            return this.mPurge;
        }

        public boolean isComplete() {
            return this.mComplete;
        }

        public Set<RadioManager.ProgramInfo> getModified() {
            return this.mModified;
        }

        public Set<ProgramSelector.Identifier> getRemoved() {
            return this.mRemoved;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof Chunk) {
                Chunk other = (Chunk) obj;
                return this.mPurge == other.mPurge && this.mComplete == other.mComplete && Objects.equals(this.mModified, other.mModified) && Objects.equals(this.mRemoved, other.mRemoved);
            }
            return false;
        }

        public String toString() {
            return "Chunk [mPurge=" + this.mPurge + ", mComplete=" + this.mComplete + ", mModified=" + this.mModified + ", mRemoved=" + this.mRemoved + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }
}
