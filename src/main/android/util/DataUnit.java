package android.util;
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* loaded from: classes3.dex */
public class DataUnit {
    public static final DataUnit KILOBYTES = new C34341("KILOBYTES", 0);
    public static final DataUnit MEGABYTES = new C34352("MEGABYTES", 1);
    public static final DataUnit GIGABYTES = new C34363("GIGABYTES", 2);
    public static final DataUnit KIBIBYTES = new C34374("KIBIBYTES", 3);
    public static final DataUnit MEBIBYTES = new C34385("MEBIBYTES", 4);
    public static final DataUnit GIBIBYTES = new C34396("GIBIBYTES", 5);
    private static final /* synthetic */ DataUnit[] $VALUES = $values();

    /* renamed from: android.util.DataUnit$1 */
    /* loaded from: classes3.dex */
    enum C34341 extends DataUnit {
        private C34341(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1000 * v;
        }
    }

    private static /* synthetic */ DataUnit[] $values() {
        return new DataUnit[]{KILOBYTES, MEGABYTES, GIGABYTES, KIBIBYTES, MEBIBYTES, GIBIBYTES};
    }

    private DataUnit(String str, int i) {
    }

    public static DataUnit valueOf(String name) {
        return (DataUnit) Enum.valueOf(DataUnit.class, name);
    }

    public static DataUnit[] values() {
        return (DataUnit[]) $VALUES.clone();
    }

    /* renamed from: android.util.DataUnit$2 */
    /* loaded from: classes3.dex */
    enum C34352 extends DataUnit {
        private C34352(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1000000 * v;
        }
    }

    /* renamed from: android.util.DataUnit$3 */
    /* loaded from: classes3.dex */
    enum C34363 extends DataUnit {
        private C34363(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1000000000 * v;
        }
    }

    /* renamed from: android.util.DataUnit$4 */
    /* loaded from: classes3.dex */
    enum C34374 extends DataUnit {
        private C34374(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1024 * v;
        }
    }

    /* renamed from: android.util.DataUnit$5 */
    /* loaded from: classes3.dex */
    enum C34385 extends DataUnit {
        private C34385(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1048576 * v;
        }
    }

    /* renamed from: android.util.DataUnit$6 */
    /* loaded from: classes3.dex */
    enum C34396 extends DataUnit {
        private C34396(String str, int i) {
            super(str, i);
        }

        @Override // android.util.DataUnit
        public long toBytes(long v) {
            return 1073741824 * v;
        }
    }

    public long toBytes(long v) {
        throw new AbstractMethodError();
    }
}
