package android.filterfw.core;

import android.app.slice.SliceItem;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import java.util.Arrays;
import java.util.Map;
/* loaded from: classes.dex */
public class FrameFormat {
    public static final int BYTES_PER_SAMPLE_UNSPECIFIED = 1;
    protected static final int SIZE_UNKNOWN = -1;
    public static final int SIZE_UNSPECIFIED = 0;
    public static final int TARGET_GPU = 3;
    public static final int TARGET_NATIVE = 2;
    public static final int TARGET_RS = 5;
    public static final int TARGET_SIMPLE = 1;
    public static final int TARGET_UNSPECIFIED = 0;
    public static final int TARGET_VERTEXBUFFER = 4;
    public static final int TYPE_BIT = 1;
    public static final int TYPE_BYTE = 2;
    public static final int TYPE_DOUBLE = 6;
    public static final int TYPE_FLOAT = 5;
    public static final int TYPE_INT16 = 3;
    public static final int TYPE_INT32 = 4;
    public static final int TYPE_OBJECT = 8;
    public static final int TYPE_POINTER = 7;
    public static final int TYPE_UNSPECIFIED = 0;
    protected int mBaseType;
    protected int mBytesPerSample;
    protected int[] mDimensions;
    protected KeyValueMap mMetaData;
    protected Class mObjectClass;
    protected int mSize;
    protected int mTarget;

    /* JADX INFO: Access modifiers changed from: protected */
    public FrameFormat() {
        this.mBaseType = 0;
        this.mBytesPerSample = 1;
        this.mSize = -1;
        this.mTarget = 0;
    }

    public FrameFormat(int baseType, int target) {
        this.mBaseType = 0;
        this.mBytesPerSample = 1;
        this.mSize = -1;
        this.mTarget = 0;
        this.mBaseType = baseType;
        this.mTarget = target;
        initDefaults();
    }

    public static FrameFormat unspecified() {
        return new FrameFormat(0, 0);
    }

    public int getBaseType() {
        return this.mBaseType;
    }

    public boolean isBinaryDataType() {
        int i = this.mBaseType;
        return i >= 1 && i <= 6;
    }

    public int getBytesPerSample() {
        return this.mBytesPerSample;
    }

    public int getValuesPerSample() {
        return this.mBytesPerSample / bytesPerSampleOf(this.mBaseType);
    }

    public int getTarget() {
        return this.mTarget;
    }

    public int[] getDimensions() {
        return this.mDimensions;
    }

    public int getDimension(int i) {
        return this.mDimensions[i];
    }

    public int getDimensionCount() {
        int[] iArr = this.mDimensions;
        if (iArr == null) {
            return 0;
        }
        return iArr.length;
    }

    public boolean hasMetaKey(String key) {
        KeyValueMap keyValueMap = this.mMetaData;
        if (keyValueMap != null) {
            return keyValueMap.containsKey(key);
        }
        return false;
    }

    public boolean hasMetaKey(String key, Class expectedClass) {
        KeyValueMap keyValueMap = this.mMetaData;
        if (keyValueMap != null && keyValueMap.containsKey(key)) {
            if (!expectedClass.isAssignableFrom(this.mMetaData.get(key).getClass())) {
                throw new RuntimeException("FrameFormat meta-key '" + key + "' is of type " + this.mMetaData.get(key).getClass() + " but expected to be of type " + expectedClass + "!");
            }
            return true;
        }
        return false;
    }

    public Object getMetaValue(String key) {
        KeyValueMap keyValueMap = this.mMetaData;
        if (keyValueMap != null) {
            return keyValueMap.get(key);
        }
        return null;
    }

    public int getNumberOfDimensions() {
        int[] iArr = this.mDimensions;
        if (iArr != null) {
            return iArr.length;
        }
        return 0;
    }

    public int getLength() {
        int[] iArr = this.mDimensions;
        if (iArr == null || iArr.length < 1) {
            return -1;
        }
        return iArr[0];
    }

    public int getWidth() {
        return getLength();
    }

    public int getHeight() {
        int[] iArr = this.mDimensions;
        if (iArr == null || iArr.length < 2) {
            return -1;
        }
        return iArr[1];
    }

    public int getDepth() {
        int[] iArr = this.mDimensions;
        if (iArr == null || iArr.length < 3) {
            return -1;
        }
        return iArr[2];
    }

    public int getSize() {
        if (this.mSize == -1) {
            this.mSize = calcSize(this.mDimensions);
        }
        return this.mSize;
    }

    public Class getObjectClass() {
        return this.mObjectClass;
    }

    public MutableFrameFormat mutableCopy() {
        MutableFrameFormat result = new MutableFrameFormat();
        result.setBaseType(getBaseType());
        result.setTarget(getTarget());
        result.setBytesPerSample(getBytesPerSample());
        result.setDimensions(getDimensions());
        result.setObjectClass(getObjectClass());
        KeyValueMap keyValueMap = this.mMetaData;
        result.mMetaData = keyValueMap == null ? null : (KeyValueMap) keyValueMap.clone();
        return result;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof FrameFormat) {
            FrameFormat format = (FrameFormat) object;
            return format.mBaseType == this.mBaseType && format.mTarget == this.mTarget && format.mBytesPerSample == this.mBytesPerSample && Arrays.equals(format.mDimensions, this.mDimensions) && format.mMetaData.equals(this.mMetaData);
        }
        return false;
    }

    public int hashCode() {
        return ((this.mBaseType ^ 4211) ^ this.mBytesPerSample) ^ getSize();
    }

    /* JADX WARN: Removed duplicated region for block: B:43:0x008a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean isCompatibleWith(FrameFormat specification) {
        if (specification.getBaseType() == 0 || getBaseType() == specification.getBaseType()) {
            if (specification.getTarget() == 0 || getTarget() == specification.getTarget()) {
                if (specification.getBytesPerSample() == 1 || getBytesPerSample() == specification.getBytesPerSample()) {
                    if (specification.getDimensionCount() <= 0 || getDimensionCount() == specification.getDimensionCount()) {
                        for (int i = 0; i < specification.getDimensionCount(); i++) {
                            int specDim = specification.getDimension(i);
                            if (specDim != 0 && getDimension(i) != specDim) {
                                return false;
                            }
                        }
                        if (specification.getObjectClass() == null || (getObjectClass() != null && specification.getObjectClass().isAssignableFrom(getObjectClass()))) {
                            KeyValueMap keyValueMap = specification.mMetaData;
                            if (keyValueMap != null) {
                                for (String specKey : keyValueMap.keySet()) {
                                    KeyValueMap keyValueMap2 = this.mMetaData;
                                    if (keyValueMap2 == null || !keyValueMap2.containsKey(specKey) || !this.mMetaData.get(specKey).equals(specification.mMetaData.get(specKey))) {
                                        return false;
                                    }
                                    while (r0.hasNext()) {
                                    }
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public boolean mayBeCompatibleWith(FrameFormat specification) {
        if (specification.getBaseType() == 0 || getBaseType() == 0 || getBaseType() == specification.getBaseType()) {
            if (specification.getTarget() == 0 || getTarget() == 0 || getTarget() == specification.getTarget()) {
                if (specification.getBytesPerSample() == 1 || getBytesPerSample() == 1 || getBytesPerSample() == specification.getBytesPerSample()) {
                    if (specification.getDimensionCount() <= 0 || getDimensionCount() <= 0 || getDimensionCount() == specification.getDimensionCount()) {
                        for (int i = 0; i < specification.getDimensionCount(); i++) {
                            int specDim = specification.getDimension(i);
                            if (specDim != 0 && getDimension(i) != 0 && getDimension(i) != specDim) {
                                return false;
                            }
                        }
                        if (specification.getObjectClass() == null || getObjectClass() == null || specification.getObjectClass().isAssignableFrom(getObjectClass())) {
                            KeyValueMap keyValueMap = specification.mMetaData;
                            if (keyValueMap != null && this.mMetaData != null) {
                                for (String specKey : keyValueMap.keySet()) {
                                    if (this.mMetaData.containsKey(specKey) && !this.mMetaData.get(specKey).equals(specification.mMetaData.get(specKey))) {
                                        return false;
                                    }
                                }
                            }
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public static int bytesPerSampleOf(int baseType) {
        switch (baseType) {
            case 1:
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
            case 5:
            case 7:
                return 4;
            case 6:
                return 8;
            default:
                return 1;
        }
    }

    public static String dimensionsToString(int[] dimensions) {
        StringBuffer buffer = new StringBuffer();
        if (dimensions != null) {
            int n = dimensions.length;
            for (int i = 0; i < n; i++) {
                if (dimensions[i] == 0) {
                    buffer.append("[]");
                } else {
                    buffer.append(NavigationBarInflaterView.SIZE_MOD_START + String.valueOf(dimensions[i]) + NavigationBarInflaterView.SIZE_MOD_END);
                }
            }
        }
        return buffer.toString();
    }

    public static String baseTypeToString(int baseType) {
        switch (baseType) {
            case 0:
                return "unspecified";
            case 1:
                return "bit";
            case 2:
                return "byte";
            case 3:
                return SliceItem.FORMAT_INT;
            case 4:
                return SliceItem.FORMAT_INT;
            case 5:
                return "float";
            case 6:
                return "double";
            case 7:
                return "pointer";
            case 8:
                return "object";
            default:
                return "unknown";
        }
    }

    public static String targetToString(int target) {
        switch (target) {
            case 0:
                return "unspecified";
            case 1:
                return "simple";
            case 2:
                return "native";
            case 3:
                return "gpu";
            case 4:
                return "vbo";
            case 5:
                return "renderscript";
            default:
                return "unknown";
        }
    }

    public static String metaDataToString(KeyValueMap metaData) {
        if (metaData == null) {
            return "";
        }
        StringBuffer buffer = new StringBuffer();
        buffer.append("{ ");
        for (Map.Entry<String, Object> entry : metaData.entrySet()) {
            buffer.append(entry.getKey() + ": " + entry.getValue() + " ");
        }
        buffer.append("}");
        return buffer.toString();
    }

    public static int readTargetString(String targetString) {
        if (targetString.equalsIgnoreCase("CPU") || targetString.equalsIgnoreCase("NATIVE")) {
            return 2;
        }
        if (targetString.equalsIgnoreCase("GPU")) {
            return 3;
        }
        if (targetString.equalsIgnoreCase("SIMPLE")) {
            return 1;
        }
        if (targetString.equalsIgnoreCase("VERTEXBUFFER")) {
            return 4;
        }
        if (targetString.equalsIgnoreCase("UNSPECIFIED")) {
            return 0;
        }
        throw new RuntimeException("Unknown target type '" + targetString + "'!");
    }

    public String toString() {
        int valuesPerSample = getValuesPerSample();
        String sampleCountString = valuesPerSample == 1 ? "" : String.valueOf(valuesPerSample);
        String targetString = this.mTarget == 0 ? "" : targetToString(this.mTarget) + " ";
        String classString = this.mObjectClass != null ? " class(" + this.mObjectClass.getSimpleName() + ") " : "";
        return targetString + baseTypeToString(this.mBaseType) + sampleCountString + dimensionsToString(this.mDimensions) + classString + metaDataToString(this.mMetaData);
    }

    private void initDefaults() {
        this.mBytesPerSample = bytesPerSampleOf(this.mBaseType);
    }

    int calcSize(int[] dimensions) {
        if (dimensions == null || dimensions.length <= 0) {
            return 0;
        }
        int size = getBytesPerSample();
        for (int dim : dimensions) {
            size *= dim;
        }
        return size;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isReplaceableBy(FrameFormat format) {
        return this.mTarget == format.mTarget && getSize() == format.getSize() && Arrays.equals(format.mDimensions, this.mDimensions);
    }
}
