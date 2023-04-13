package android.hardware.camera2.params;

import android.graphics.Point;
import android.graphics.Rect;
/* loaded from: classes.dex */
public final class Face {
    public static final int ID_UNSUPPORTED = -1;
    public static final int SCORE_MAX = 100;
    public static final int SCORE_MIN = 1;
    private Rect mBounds;
    private int mId;
    private Point mLeftEye;
    private Point mMouth;
    private Point mRightEye;
    private int mScore;

    public Face(Rect bounds, int score, int id, Point leftEyePosition, Point rightEyePosition, Point mouthPosition) {
        init(bounds, score, id, leftEyePosition, rightEyePosition, mouthPosition);
    }

    public Face(Rect bounds, int score) {
        init(bounds, score, -1, null, null, null);
    }

    private void init(Rect bounds, int score, int id, Point leftEyePosition, Point rightEyePosition, Point mouthPosition) {
        checkNotNull("bounds", bounds);
        checkScore(score);
        checkId(id);
        if (id == -1) {
            checkNull("leftEyePosition", leftEyePosition);
            checkNull("rightEyePosition", rightEyePosition);
            checkNull("mouthPosition", mouthPosition);
        }
        checkFace(leftEyePosition, rightEyePosition, mouthPosition);
        this.mBounds = bounds;
        this.mScore = score;
        this.mId = id;
        this.mLeftEye = leftEyePosition;
        this.mRightEye = rightEyePosition;
        this.mMouth = mouthPosition;
    }

    public Rect getBounds() {
        return this.mBounds;
    }

    public int getScore() {
        return this.mScore;
    }

    public int getId() {
        return this.mId;
    }

    public Point getLeftEyePosition() {
        return this.mLeftEye;
    }

    public Point getRightEyePosition() {
        return this.mRightEye;
    }

    public Point getMouthPosition() {
        return this.mMouth;
    }

    public String toString() {
        return String.format("{ bounds: %s, score: %s, id: %d, leftEyePosition: %s, rightEyePosition: %s, mouthPosition: %s }", this.mBounds, Integer.valueOf(this.mScore), Integer.valueOf(this.mId), this.mLeftEye, this.mRightEye, this.mMouth);
    }

    private static void checkNotNull(String name, Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " was required, but it was null");
        }
    }

    private static void checkNull(String name, Object obj) {
        if (obj != null) {
            throw new IllegalArgumentException(name + " was required to be null, but it wasn't");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkScore(int score) {
        if (score < 1 || score > 100) {
            throw new IllegalArgumentException("Confidence out of range");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkId(int id) {
        if (id < 0 && id != -1) {
            throw new IllegalArgumentException("Id out of range");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkFace(Point leftEyePosition, Point rightEyePosition, Point mouthPosition) {
        if (leftEyePosition != null || rightEyePosition != null || mouthPosition != null) {
            if (leftEyePosition == null || rightEyePosition == null || mouthPosition == null) {
                throw new IllegalArgumentException("If any of leftEyePosition, rightEyePosition, or mouthPosition are non-null, all three must be non-null.");
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private static final long FIELD_BOUNDS = 2;
        private static final long FIELD_BUILT = 1;
        private static final long FIELD_ID = 8;
        private static final long FIELD_LEFT_EYE = 16;
        private static final long FIELD_MOUTH = 64;
        private static final String FIELD_NAME_BOUNDS = "bounds";
        private static final String FIELD_NAME_LEFT_EYE = "left eye";
        private static final String FIELD_NAME_MOUTH = "mouth";
        private static final String FIELD_NAME_RIGHT_EYE = "right eye";
        private static final String FIELD_NAME_SCORE = "score";
        private static final long FIELD_RIGHT_EYE = 32;
        private static final long FIELD_SCORE = 4;
        private Rect mBounds;
        private long mBuilderFieldsSet;
        private int mId;
        private Point mLeftEye;
        private Point mMouth;
        private Point mRightEye;
        private int mScore;

        public Builder() {
            this.mBuilderFieldsSet = 0L;
            this.mBounds = null;
            this.mScore = 0;
            this.mId = -1;
            this.mLeftEye = null;
            this.mRightEye = null;
            this.mMouth = null;
        }

        public Builder(Face current) {
            this.mBuilderFieldsSet = 0L;
            this.mBounds = null;
            this.mScore = 0;
            this.mId = -1;
            this.mLeftEye = null;
            this.mRightEye = null;
            this.mMouth = null;
            this.mBounds = current.mBounds;
            this.mScore = current.mScore;
            this.mId = current.mId;
            this.mLeftEye = current.mLeftEye;
            this.mRightEye = current.mRightEye;
            this.mMouth = current.mMouth;
        }

        public Builder setBounds(Rect bounds) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mBounds = bounds;
            return this;
        }

        public Builder setScore(int score) {
            checkNotUsed();
            Face.checkScore(score);
            this.mBuilderFieldsSet |= 4;
            this.mScore = score;
            return this;
        }

        public Builder setId(int id) {
            checkNotUsed();
            Face.checkId(id);
            this.mBuilderFieldsSet |= 8;
            this.mId = id;
            return this;
        }

        public Builder setLeftEyePosition(Point leftEyePosition) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mLeftEye = leftEyePosition;
            return this;
        }

        public Builder setRightEyePosition(Point rightEyePosition) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mRightEye = rightEyePosition;
            return this;
        }

        public Builder setMouthPosition(Point mouthPosition) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mMouth = mouthPosition;
            return this;
        }

        public Face build() {
            checkNotUsed();
            checkFieldSet(2L, FIELD_NAME_BOUNDS);
            checkFieldSet(4L, FIELD_NAME_SCORE);
            if (this.mId == -1) {
                checkIdUnsupportedThenNull(this.mLeftEye, FIELD_NAME_LEFT_EYE);
                checkIdUnsupportedThenNull(this.mRightEye, FIELD_NAME_RIGHT_EYE);
                checkIdUnsupportedThenNull(this.mMouth, FIELD_NAME_MOUTH);
            }
            Face.checkFace(this.mLeftEye, this.mRightEye, this.mMouth);
            this.mBuilderFieldsSet |= 1;
            return new Face(this.mBounds, this.mScore, this.mId, this.mLeftEye, this.mRightEye, this.mMouth);
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 1) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }

        private void checkFieldSet(long field, String fieldName) {
            if ((this.mBuilderFieldsSet & field) == 0) {
                throw new IllegalStateException("Field \"" + fieldName + "\" must be set before building.");
            }
        }

        private void checkIdUnsupportedThenNull(Object obj, String fieldName) {
            if (obj != null) {
                throw new IllegalArgumentException("Field \"" + fieldName + "\" must be unset or null if id is ID_UNSUPPORTED.");
            }
        }
    }
}
