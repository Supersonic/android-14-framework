package com.android.framework.protobuf;

import com.android.framework.protobuf.Internal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@CheckReturnValue
/* loaded from: classes4.dex */
abstract class ListFieldSchema {
    private static final ListFieldSchema FULL_INSTANCE = new ListFieldSchemaFull();
    private static final ListFieldSchema LITE_INSTANCE = new ListFieldSchemaLite();

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract void makeImmutableListAt(Object obj, long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract <L> void mergeListsAt(Object obj, Object obj2, long j);

    /* JADX INFO: Access modifiers changed from: package-private */
    public abstract <L> List<L> mutableListAt(Object obj, long j);

    private ListFieldSchema() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ListFieldSchema full() {
        return FULL_INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ListFieldSchema lite() {
        return LITE_INSTANCE;
    }

    /* loaded from: classes4.dex */
    private static final class ListFieldSchemaFull extends ListFieldSchema {
        private static final Class<?> UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(Collections.emptyList()).getClass();

        private ListFieldSchemaFull() {
            super();
        }

        @Override // com.android.framework.protobuf.ListFieldSchema
        <L> List<L> mutableListAt(Object message, long offset) {
            return mutableListAt(message, offset, 10);
        }

        @Override // com.android.framework.protobuf.ListFieldSchema
        void makeImmutableListAt(Object message, long offset) {
            Object immutable;
            List<?> list = (List) UnsafeUtil.getObject(message, offset);
            if (list instanceof LazyStringList) {
                immutable = ((LazyStringList) list).getUnmodifiableView();
            } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list.getClass())) {
                return;
            } else {
                if ((list instanceof PrimitiveNonBoxingCollection) && (list instanceof Internal.ProtobufList)) {
                    if (((Internal.ProtobufList) list).isModifiable()) {
                        ((Internal.ProtobufList) list).makeImmutable();
                        return;
                    }
                    return;
                }
                immutable = Collections.unmodifiableList(list);
            }
            UnsafeUtil.putObject(message, offset, immutable);
        }

        private static <L> List<L> mutableListAt(Object message, long offset, int additionalCapacity) {
            List<L> list;
            List<L> list2 = getList(message, offset);
            if (list2.isEmpty()) {
                if (list2 instanceof LazyStringList) {
                    list = new LazyStringArrayList(additionalCapacity);
                } else if ((list2 instanceof PrimitiveNonBoxingCollection) && (list2 instanceof Internal.ProtobufList)) {
                    list = ((Internal.ProtobufList) list2).mutableCopyWithCapacity(additionalCapacity);
                } else {
                    list = new ArrayList(additionalCapacity);
                }
                UnsafeUtil.putObject(message, offset, list);
                return list;
            } else if (UNMODIFIABLE_LIST_CLASS.isAssignableFrom(list2.getClass())) {
                ArrayList<L> newList = new ArrayList<>(list2.size() + additionalCapacity);
                newList.addAll(list2);
                UnsafeUtil.putObject(message, offset, newList);
                return newList;
            } else if (list2 instanceof UnmodifiableLazyStringList) {
                LazyStringArrayList newList2 = new LazyStringArrayList(list2.size() + additionalCapacity);
                newList2.addAll((UnmodifiableLazyStringList) list2);
                UnsafeUtil.putObject(message, offset, newList2);
                return newList2;
            } else if ((list2 instanceof PrimitiveNonBoxingCollection) && (list2 instanceof Internal.ProtobufList) && !((Internal.ProtobufList) list2).isModifiable()) {
                List<L> list3 = ((Internal.ProtobufList) list2).mutableCopyWithCapacity(list2.size() + additionalCapacity);
                UnsafeUtil.putObject(message, offset, list3);
                return list3;
            } else {
                return list2;
            }
        }

        @Override // com.android.framework.protobuf.ListFieldSchema
        <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
            List<E> other = getList(otherMsg, offset);
            List<E> mine = mutableListAt(msg, offset, other.size());
            int size = mine.size();
            int otherSize = other.size();
            if (size > 0 && otherSize > 0) {
                mine.addAll(other);
            }
            List<E> merged = size > 0 ? mine : other;
            UnsafeUtil.putObject(msg, offset, merged);
        }

        static <E> List<E> getList(Object message, long offset) {
            return (List) UnsafeUtil.getObject(message, offset);
        }
    }

    /* loaded from: classes4.dex */
    private static final class ListFieldSchemaLite extends ListFieldSchema {
        private ListFieldSchemaLite() {
            super();
        }

        @Override // com.android.framework.protobuf.ListFieldSchema
        <L> List<L> mutableListAt(Object message, long offset) {
            Internal.ProtobufList<L> list = getProtobufList(message, offset);
            if (!list.isModifiable()) {
                int size = list.size();
                Internal.ProtobufList<L> list2 = list.mutableCopyWithCapacity(size == 0 ? 10 : size * 2);
                UnsafeUtil.putObject(message, offset, list2);
                return list2;
            }
            return list;
        }

        @Override // com.android.framework.protobuf.ListFieldSchema
        void makeImmutableListAt(Object message, long offset) {
            Internal.ProtobufList<?> list = getProtobufList(message, offset);
            list.makeImmutable();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // com.android.framework.protobuf.ListFieldSchema
        <E> void mergeListsAt(Object msg, Object otherMsg, long offset) {
            Internal.ProtobufList<E> mine = getProtobufList(msg, offset);
            Internal.ProtobufList<E> other = getProtobufList(otherMsg, offset);
            int size = mine.size();
            int otherSize = other.size();
            Internal.ProtobufList<E> mine2 = mine;
            mine2 = mine;
            if (size > 0 && otherSize > 0) {
                boolean isModifiable = mine.isModifiable();
                Internal.ProtobufList<E> mine3 = mine;
                if (!isModifiable) {
                    mine3 = mine.mutableCopyWithCapacity(size + otherSize);
                }
                mine3.addAll(other);
                mine2 = mine3;
            }
            Internal.ProtobufList<E> merged = size > 0 ? mine2 : other;
            UnsafeUtil.putObject(msg, offset, merged);
        }

        static <E> Internal.ProtobufList<E> getProtobufList(Object message, long offset) {
            return (Internal.ProtobufList) UnsafeUtil.getObject(message, offset);
        }
    }
}
