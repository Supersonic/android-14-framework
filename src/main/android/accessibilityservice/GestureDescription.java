package android.accessibilityservice;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class GestureDescription {
    private static final long MAX_GESTURE_DURATION_MS = 60000;
    private static final int MAX_STROKE_COUNT = 20;
    private final int mDisplayId;
    private final List<StrokeDescription> mStrokes;
    private final float[] mTempPos;

    public static int getMaxStrokeCount() {
        return 20;
    }

    public static long getMaxGestureDuration() {
        return 60000L;
    }

    private GestureDescription() {
        this(new ArrayList());
    }

    private GestureDescription(List<StrokeDescription> strokes) {
        this(strokes, 0);
    }

    private GestureDescription(List<StrokeDescription> strokes, int displayId) {
        ArrayList arrayList = new ArrayList();
        this.mStrokes = arrayList;
        this.mTempPos = new float[2];
        arrayList.addAll(strokes);
        this.mDisplayId = displayId;
    }

    public int getStrokeCount() {
        return this.mStrokes.size();
    }

    public StrokeDescription getStroke(int index) {
        return this.mStrokes.get(index);
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getNextKeyPointAtLeast(long offset) {
        long nextKeyPoint = Long.MAX_VALUE;
        for (int i = 0; i < this.mStrokes.size(); i++) {
            long thisStartTime = this.mStrokes.get(i).mStartTime;
            if (thisStartTime < nextKeyPoint && thisStartTime >= offset) {
                nextKeyPoint = thisStartTime;
            }
            long thisEndTime = this.mStrokes.get(i).mEndTime;
            if (thisEndTime < nextKeyPoint && thisEndTime >= offset) {
                nextKeyPoint = thisEndTime;
            }
        }
        if (nextKeyPoint == Long.MAX_VALUE) {
            return -1L;
        }
        return nextKeyPoint;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPointsForTime(long time, TouchPoint[] touchPoints) {
        int numPointsFound = 0;
        for (int i = 0; i < this.mStrokes.size(); i++) {
            StrokeDescription strokeDescription = this.mStrokes.get(i);
            if (strokeDescription.hasPointForTime(time)) {
                touchPoints[numPointsFound].mStrokeId = strokeDescription.getId();
                touchPoints[numPointsFound].mContinuedStrokeId = strokeDescription.getContinuedStrokeId();
                touchPoints[numPointsFound].mIsStartOfPath = strokeDescription.getContinuedStrokeId() < 0 && time == strokeDescription.mStartTime;
                touchPoints[numPointsFound].mIsEndOfPath = !strokeDescription.willContinue() && time == strokeDescription.mEndTime;
                strokeDescription.getPosForTime(time, this.mTempPos);
                touchPoints[numPointsFound].f5mX = Math.round(this.mTempPos[0]);
                touchPoints[numPointsFound].f6mY = Math.round(this.mTempPos[1]);
                numPointsFound++;
            }
        }
        return numPointsFound;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getTotalDuration(List<StrokeDescription> paths) {
        long latestEnd = Long.MIN_VALUE;
        for (int i = 0; i < paths.size(); i++) {
            StrokeDescription path = paths.get(i);
            latestEnd = Math.max(latestEnd, path.mEndTime);
        }
        return Math.max(latestEnd, 0L);
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private final List<StrokeDescription> mStrokes = new ArrayList();
        private int mDisplayId = 0;

        public Builder addStroke(StrokeDescription strokeDescription) {
            if (this.mStrokes.size() >= 20) {
                throw new IllegalStateException("Attempting to add too many strokes to a gesture. Maximum is 20, got " + this.mStrokes.size());
            }
            this.mStrokes.add(strokeDescription);
            if (GestureDescription.getTotalDuration(this.mStrokes) > 60000) {
                this.mStrokes.remove(strokeDescription);
                throw new IllegalStateException("Gesture would exceed maximum duration with new stroke");
            }
            return this;
        }

        public Builder setDisplayId(int displayId) {
            this.mDisplayId = displayId;
            return this;
        }

        public GestureDescription build() {
            if (this.mStrokes.size() == 0) {
                throw new IllegalStateException("Gestures must have at least one stroke");
            }
            return new GestureDescription(this.mStrokes, this.mDisplayId);
        }
    }

    /* loaded from: classes.dex */
    public static class StrokeDescription {
        private static final int INVALID_STROKE_ID = -1;
        static int sIdCounter;
        boolean mContinued;
        int mContinuedStrokeId;
        long mEndTime;
        int mId;
        Path mPath;
        private PathMeasure mPathMeasure;
        long mStartTime;
        float[] mTapLocation;
        private float mTimeToLengthConversion;

        public StrokeDescription(Path path, long startTime, long duration) {
            this(path, startTime, duration, false);
        }

        public StrokeDescription(Path path, long startTime, long duration, boolean willContinue) {
            this.mContinuedStrokeId = -1;
            this.mContinued = willContinue;
            boolean z = true;
            Preconditions.checkArgument(duration > 0, "Duration must be positive");
            Preconditions.checkArgument(startTime >= 0, "Start time must not be negative");
            Preconditions.checkArgument(!path.isEmpty(), "Path is empty");
            RectF bounds = new RectF();
            path.computeBounds(bounds, false);
            Preconditions.checkArgument((bounds.bottom < 0.0f || bounds.top < 0.0f || bounds.right < 0.0f || bounds.left < 0.0f) ? false : z, "Path bounds must not be negative");
            this.mPath = new Path(path);
            PathMeasure pathMeasure = new PathMeasure(path, false);
            this.mPathMeasure = pathMeasure;
            if (pathMeasure.getLength() == 0.0f) {
                Path tempPath = new Path(path);
                tempPath.lineTo(-1.0f, -1.0f);
                this.mTapLocation = new float[2];
                PathMeasure pathMeasure2 = new PathMeasure(tempPath, false);
                pathMeasure2.getPosTan(0.0f, this.mTapLocation, null);
            }
            if (this.mPathMeasure.nextContour()) {
                throw new IllegalArgumentException("Path has more than one contour");
            }
            this.mPathMeasure.setPath(this.mPath, false);
            this.mStartTime = startTime;
            this.mEndTime = startTime + duration;
            this.mTimeToLengthConversion = getLength() / ((float) duration);
            int i = sIdCounter;
            sIdCounter = i + 1;
            this.mId = i;
        }

        public Path getPath() {
            return new Path(this.mPath);
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        public long getDuration() {
            return this.mEndTime - this.mStartTime;
        }

        public int getId() {
            return this.mId;
        }

        public StrokeDescription continueStroke(Path path, long startTime, long duration, boolean willContinue) {
            if (!this.mContinued) {
                throw new IllegalStateException("Only strokes marked willContinue can be continued");
            }
            StrokeDescription strokeDescription = new StrokeDescription(path, startTime, duration, willContinue);
            strokeDescription.mContinuedStrokeId = this.mId;
            return strokeDescription;
        }

        public boolean willContinue() {
            return this.mContinued;
        }

        public int getContinuedStrokeId() {
            return this.mContinuedStrokeId;
        }

        float getLength() {
            return this.mPathMeasure.getLength();
        }

        boolean getPosForTime(long time, float[] pos) {
            float[] fArr = this.mTapLocation;
            if (fArr != null) {
                pos[0] = fArr[0];
                pos[1] = fArr[1];
                return true;
            } else if (time == this.mEndTime) {
                return this.mPathMeasure.getPosTan(getLength(), pos, null);
            } else {
                float length = this.mTimeToLengthConversion * ((float) (time - this.mStartTime));
                return this.mPathMeasure.getPosTan(length, pos, null);
            }
        }

        boolean hasPointForTime(long time) {
            return time >= this.mStartTime && time <= this.mEndTime;
        }
    }

    /* loaded from: classes.dex */
    public static class TouchPoint implements Parcelable {
        public static final Parcelable.Creator<TouchPoint> CREATOR = new Parcelable.Creator<TouchPoint>() { // from class: android.accessibilityservice.GestureDescription.TouchPoint.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public TouchPoint createFromParcel(Parcel in) {
                return new TouchPoint(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public TouchPoint[] newArray(int size) {
                return new TouchPoint[size];
            }
        };
        private static final int FLAG_IS_END_OF_PATH = 2;
        private static final int FLAG_IS_START_OF_PATH = 1;
        public int mContinuedStrokeId;
        public boolean mIsEndOfPath;
        public boolean mIsStartOfPath;
        public int mStrokeId;

        /* renamed from: mX */
        public float f5mX;

        /* renamed from: mY */
        public float f6mY;

        public TouchPoint() {
        }

        public TouchPoint(TouchPoint pointToCopy) {
            copyFrom(pointToCopy);
        }

        public TouchPoint(Parcel parcel) {
            this.mStrokeId = parcel.readInt();
            this.mContinuedStrokeId = parcel.readInt();
            int startEnd = parcel.readInt();
            this.mIsStartOfPath = (startEnd & 1) != 0;
            this.mIsEndOfPath = (startEnd & 2) != 0;
            this.f5mX = parcel.readFloat();
            this.f6mY = parcel.readFloat();
        }

        public void copyFrom(TouchPoint other) {
            this.mStrokeId = other.mStrokeId;
            this.mContinuedStrokeId = other.mContinuedStrokeId;
            this.mIsStartOfPath = other.mIsStartOfPath;
            this.mIsEndOfPath = other.mIsEndOfPath;
            this.f5mX = other.f5mX;
            this.f6mY = other.f6mY;
        }

        public String toString() {
            return "TouchPoint{mStrokeId=" + this.mStrokeId + ", mContinuedStrokeId=" + this.mContinuedStrokeId + ", mIsStartOfPath=" + this.mIsStartOfPath + ", mIsEndOfPath=" + this.mIsEndOfPath + ", mX=" + this.f5mX + ", mY=" + this.f6mY + '}';
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mStrokeId);
            dest.writeInt(this.mContinuedStrokeId);
            boolean z = this.mIsStartOfPath;
            int i = this.mIsEndOfPath ? 2 : 0;
            int startEnd = z ? 1 : 0;
            dest.writeInt(startEnd | i);
            dest.writeFloat(this.f5mX);
            dest.writeFloat(this.f6mY);
        }
    }

    /* loaded from: classes.dex */
    public static class GestureStep implements Parcelable {
        public static final Parcelable.Creator<GestureStep> CREATOR = new Parcelable.Creator<GestureStep>() { // from class: android.accessibilityservice.GestureDescription.GestureStep.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GestureStep createFromParcel(Parcel in) {
                return new GestureStep(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GestureStep[] newArray(int size) {
                return new GestureStep[size];
            }
        };
        public int numTouchPoints;
        public long timeSinceGestureStart;
        public TouchPoint[] touchPoints;

        public GestureStep(long timeSinceGestureStart, int numTouchPoints, TouchPoint[] touchPointsToCopy) {
            this.timeSinceGestureStart = timeSinceGestureStart;
            this.numTouchPoints = numTouchPoints;
            this.touchPoints = new TouchPoint[numTouchPoints];
            for (int i = 0; i < numTouchPoints; i++) {
                this.touchPoints[i] = new TouchPoint(touchPointsToCopy[i]);
            }
        }

        public GestureStep(Parcel parcel) {
            this.timeSinceGestureStart = parcel.readLong();
            Parcelable[] parcelables = (Parcelable[]) parcel.readParcelableArray(TouchPoint.class.getClassLoader(), TouchPoint.class);
            int length = parcelables == null ? 0 : parcelables.length;
            this.numTouchPoints = length;
            this.touchPoints = new TouchPoint[length];
            for (int i = 0; i < this.numTouchPoints; i++) {
                this.touchPoints[i] = (TouchPoint) parcelables[i];
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.timeSinceGestureStart);
            dest.writeParcelableArray(this.touchPoints, flags);
        }
    }

    /* loaded from: classes.dex */
    public static class MotionEventGenerator {
        private static TouchPoint[] sCurrentTouchPoints;

        public static List<GestureStep> getGestureStepsFromGestureDescription(GestureDescription description, int sampleTimeMs) {
            List<GestureStep> gestureSteps = new ArrayList<>();
            TouchPoint[] currentTouchPoints = getCurrentTouchPoints(description.getStrokeCount());
            int currentTouchPointSize = 0;
            long timeSinceGestureStart = 0;
            long nextKeyPointTime = description.getNextKeyPointAtLeast(0L);
            while (nextKeyPointTime >= 0) {
                timeSinceGestureStart = currentTouchPointSize == 0 ? nextKeyPointTime : Math.min(nextKeyPointTime, sampleTimeMs + timeSinceGestureStart);
                currentTouchPointSize = description.getPointsForTime(timeSinceGestureStart, currentTouchPoints);
                gestureSteps.add(new GestureStep(timeSinceGestureStart, currentTouchPointSize, currentTouchPoints));
                nextKeyPointTime = description.getNextKeyPointAtLeast(1 + timeSinceGestureStart);
            }
            return gestureSteps;
        }

        private static TouchPoint[] getCurrentTouchPoints(int requiredCapacity) {
            TouchPoint[] touchPointArr = sCurrentTouchPoints;
            if (touchPointArr == null || touchPointArr.length < requiredCapacity) {
                sCurrentTouchPoints = new TouchPoint[requiredCapacity];
                for (int i = 0; i < requiredCapacity; i++) {
                    sCurrentTouchPoints[i] = new TouchPoint();
                }
            }
            return sCurrentTouchPoints;
        }
    }
}
