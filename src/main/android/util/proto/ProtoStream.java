package android.util.proto;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class ProtoStream {
    public static final long FIELD_COUNT_MASK = 16492674416640L;
    public static final long FIELD_COUNT_PACKED = 5497558138880L;
    public static final long FIELD_COUNT_REPEATED = 2199023255552L;
    public static final int FIELD_COUNT_SHIFT = 40;
    public static final long FIELD_COUNT_SINGLE = 1099511627776L;
    public static final long FIELD_COUNT_UNKNOWN = 0;
    public static final int FIELD_ID_MASK = -8;
    public static final int FIELD_ID_SHIFT = 3;
    public static final long FIELD_TYPE_BOOL = 34359738368L;
    public static final long FIELD_TYPE_BYTES = 51539607552L;
    public static final long FIELD_TYPE_DOUBLE = 4294967296L;
    public static final long FIELD_TYPE_ENUM = 60129542144L;
    public static final long FIELD_TYPE_FIXED32 = 30064771072L;
    public static final long FIELD_TYPE_FIXED64 = 25769803776L;
    public static final long FIELD_TYPE_FLOAT = 8589934592L;
    public static final long FIELD_TYPE_INT32 = 21474836480L;
    public static final long FIELD_TYPE_INT64 = 12884901888L;
    public static final long FIELD_TYPE_MASK = 1095216660480L;
    public static final long FIELD_TYPE_MESSAGE = 47244640256L;
    private static final String[] FIELD_TYPE_NAMES = {"Double", "Float", "Int64", "UInt64", "Int32", "Fixed64", "Fixed32", "Bool", "String", "Group", "Message", "Bytes", "UInt32", "Enum", "SFixed32", "SFixed64", "SInt32", "SInt64"};
    public static final long FIELD_TYPE_SFIXED32 = 64424509440L;
    public static final long FIELD_TYPE_SFIXED64 = 68719476736L;
    public static final int FIELD_TYPE_SHIFT = 32;
    public static final long FIELD_TYPE_SINT32 = 73014444032L;
    public static final long FIELD_TYPE_SINT64 = 77309411328L;
    public static final long FIELD_TYPE_STRING = 38654705664L;
    public static final long FIELD_TYPE_UINT32 = 55834574848L;
    public static final long FIELD_TYPE_UINT64 = 17179869184L;
    public static final long FIELD_TYPE_UNKNOWN = 0;
    public static final int WIRE_TYPE_END_GROUP = 4;
    public static final int WIRE_TYPE_FIXED32 = 5;
    public static final int WIRE_TYPE_FIXED64 = 1;
    public static final int WIRE_TYPE_LENGTH_DELIMITED = 2;
    public static final int WIRE_TYPE_MASK = 7;
    public static final int WIRE_TYPE_START_GROUP = 3;
    public static final int WIRE_TYPE_VARINT = 0;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface FieldCount {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface FieldType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface WireType {
    }

    public static String getFieldTypeString(long fieldType) {
        int index = ((int) ((FIELD_TYPE_MASK & fieldType) >>> 32)) - 1;
        if (index >= 0) {
            String[] strArr = FIELD_TYPE_NAMES;
            if (index < strArr.length) {
                return strArr[index];
            }
            return null;
        }
        return null;
    }

    public static String getFieldCountString(long fieldCount) {
        if (fieldCount == 1099511627776L) {
            return "";
        }
        if (fieldCount == 2199023255552L) {
            return "Repeated";
        }
        if (fieldCount == FIELD_COUNT_PACKED) {
            return "Packed";
        }
        return null;
    }

    public static String getWireTypeString(int wireType) {
        switch (wireType) {
            case 0:
                return "Varint";
            case 1:
                return "Fixed64";
            case 2:
                return "Length Delimited";
            case 3:
                return "Start Group";
            case 4:
                return "End Group";
            case 5:
                return "Fixed32";
            default:
                return null;
        }
    }

    public static String getFieldIdString(long fieldId) {
        long fieldCount = FIELD_COUNT_MASK & fieldId;
        String countString = getFieldCountString(fieldCount);
        if (countString == null) {
            countString = "fieldCount=" + fieldCount;
        }
        if (countString.length() > 0) {
            countString = countString + " ";
        }
        long fieldType = FIELD_TYPE_MASK & fieldId;
        String typeString = getFieldTypeString(fieldType);
        if (typeString == null) {
            typeString = "fieldType=" + fieldType;
        }
        return countString + typeString + " tag=" + ((int) fieldId) + " fieldId=0x" + Long.toHexString(fieldId);
    }

    public static long makeFieldId(int id, long fieldFlags) {
        return (id & 4294967295L) | fieldFlags;
    }

    public static long makeToken(int tagSize, boolean repeated, int depth, int objectId, int offset) {
        return ((7 & tagSize) << 61) | (repeated ? 1152921504606846976L : 0L) | ((511 & depth) << 51) | ((524287 & objectId) << 32) | (4294967295L & offset);
    }

    public static int getTagSizeFromToken(long token) {
        return (int) ((token >> 61) & 7);
    }

    public static boolean getRepeatedFromToken(long token) {
        return ((token >> 60) & 1) != 0;
    }

    public static int getDepthFromToken(long token) {
        return (int) ((token >> 51) & 511);
    }

    public static int getObjectIdFromToken(long token) {
        return (int) ((token >> 32) & 524287);
    }

    public static int getOffsetFromToken(long token) {
        return (int) token;
    }

    public static int convertObjectIdToOrdinal(int objectId) {
        return 524287 - objectId;
    }

    public static String token2String(long token) {
        if (token == 0) {
            return "Token(0)";
        }
        return "Token(val=0x" + Long.toHexString(token) + " depth=" + getDepthFromToken(token) + " object=" + convertObjectIdToOrdinal(getObjectIdFromToken(token)) + " tagSize=" + getTagSizeFromToken(token) + " offset=" + getOffsetFromToken(token) + ')';
    }
}
