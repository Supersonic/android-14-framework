package android.gesture;

import android.graphics.RectF;
import android.util.Log;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class GestureUtils {
    private static final float NONUNIFORM_SCALE = (float) Math.sqrt(2.0d);
    private static final float SCALING_THRESHOLD = 0.26f;

    private GestureUtils() {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void closeStream(Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                Log.m109e(GestureConstants.LOG_TAG, "Could not close stream", e);
            }
        }
    }

    public static float[] spatialSampling(Gesture gesture, int bitmapSize) {
        return spatialSampling(gesture, bitmapSize, false);
    }

    public static float[] spatialSampling(Gesture gesture, int bitmapSize, boolean keepAspectRatio) {
        int size;
        float segmentStartX;
        float targetPatchSize;
        float[] pts;
        float preDx;
        float preDy;
        float targetPatchSize2 = bitmapSize - 1;
        float[] sample = new float[bitmapSize * bitmapSize];
        Arrays.fill(sample, 0.0f);
        RectF rect = gesture.getBoundingBox();
        float gestureWidth = rect.width();
        float gestureHeight = rect.height();
        float sx = targetPatchSize2 / gestureWidth;
        float sy = targetPatchSize2 / gestureHeight;
        if (keepAspectRatio) {
            float scale = sx < sy ? sx : sy;
            sx = scale;
            sy = scale;
        } else {
            float aspectRatio = gestureWidth / gestureHeight;
            if (aspectRatio > 1.0f) {
                aspectRatio = 1.0f / aspectRatio;
            }
            if (aspectRatio < SCALING_THRESHOLD) {
                float scale2 = sx < sy ? sx : sy;
                sx = scale2;
                sy = scale2;
            } else if (sx > sy) {
                float scale3 = NONUNIFORM_SCALE * sy;
                if (scale3 < sx) {
                    sx = scale3;
                }
            } else {
                float scale4 = NONUNIFORM_SCALE * sx;
                if (scale4 < sy) {
                    sy = scale4;
                }
            }
        }
        float preDx2 = -rect.centerX();
        float preDy2 = -rect.centerY();
        float postDx = targetPatchSize2 / 2.0f;
        float postDy = targetPatchSize2 / 2.0f;
        ArrayList<GestureStroke> strokes = gesture.getStrokes();
        int count = strokes.size();
        int index = 0;
        while (index < count) {
            GestureStroke stroke = strokes.get(index);
            RectF rect2 = rect;
            float[] strokepoints = stroke.points;
            int size2 = strokepoints.length;
            float gestureWidth2 = gestureWidth;
            float[] pts2 = new float[size2];
            float gestureHeight2 = gestureHeight;
            for (int i = 0; i < size2; i += 2) {
                pts2[i] = ((strokepoints[i] + preDx2) * sx) + postDx;
                pts2[i + 1] = ((strokepoints[i + 1] + preDy2) * sy) + postDy;
            }
            float segmentEndX = -1.0f;
            float segmentEndY = -1.0f;
            float sx2 = sx;
            int i2 = 0;
            while (i2 < size2) {
                float segmentStartX2 = pts2[i2] < 0.0f ? 0.0f : pts2[i2];
                float segmentStartY = pts2[i2 + 1] < 0.0f ? 0.0f : pts2[i2 + 1];
                if (segmentStartX2 <= targetPatchSize2) {
                    size = size2;
                    segmentStartX = segmentStartX2;
                } else {
                    size = size2;
                    segmentStartX = targetPatchSize2;
                }
                if (segmentStartY > targetPatchSize2) {
                    targetPatchSize = targetPatchSize2;
                } else {
                    targetPatchSize = targetPatchSize2;
                    targetPatchSize2 = segmentStartY;
                }
                plot(segmentStartX, targetPatchSize2, sample, bitmapSize);
                if (segmentEndX == -1.0f) {
                    pts = pts2;
                    preDx = preDx2;
                    preDy = preDy2;
                } else {
                    if (segmentEndX > segmentStartX) {
                        preDx = preDx2;
                        preDy = preDy2;
                        float xpos = (float) Math.ceil(segmentStartX);
                        float slope = (segmentEndY - targetPatchSize2) / (segmentEndX - segmentStartX);
                        while (xpos < segmentEndX) {
                            float[] pts3 = pts2;
                            float ypos = ((xpos - segmentStartX) * slope) + targetPatchSize2;
                            plot(xpos, ypos, sample, bitmapSize);
                            xpos += 1.0f;
                            pts2 = pts3;
                        }
                        pts = pts2;
                    } else {
                        pts = pts2;
                        preDx = preDx2;
                        preDy = preDy2;
                        if (segmentEndX < segmentStartX) {
                            float slope2 = (segmentEndY - targetPatchSize2) / (segmentEndX - segmentStartX);
                            for (float xpos2 = (float) Math.ceil(segmentEndX); xpos2 < segmentStartX; xpos2 += 1.0f) {
                                float ypos2 = ((xpos2 - segmentStartX) * slope2) + targetPatchSize2;
                                plot(xpos2, ypos2, sample, bitmapSize);
                            }
                        }
                    }
                    if (segmentEndY > targetPatchSize2) {
                        float invertSlope = (segmentEndX - segmentStartX) / (segmentEndY - targetPatchSize2);
                        for (float ypos3 = (float) Math.ceil(targetPatchSize2); ypos3 < segmentEndY; ypos3 += 1.0f) {
                            float xpos3 = ((ypos3 - targetPatchSize2) * invertSlope) + segmentStartX;
                            plot(xpos3, ypos3, sample, bitmapSize);
                        }
                    } else if (segmentEndY < targetPatchSize2) {
                        float invertSlope2 = (segmentEndX - segmentStartX) / (segmentEndY - targetPatchSize2);
                        for (float ypos4 = (float) Math.ceil(segmentEndY); ypos4 < targetPatchSize2; ypos4 += 1.0f) {
                            float xpos4 = ((ypos4 - targetPatchSize2) * invertSlope2) + segmentStartX;
                            plot(xpos4, ypos4, sample, bitmapSize);
                        }
                    }
                }
                segmentEndX = segmentStartX;
                segmentEndY = targetPatchSize2;
                i2 += 2;
                targetPatchSize2 = targetPatchSize;
                preDx2 = preDx;
                size2 = size;
                preDy2 = preDy;
                pts2 = pts;
            }
            index++;
            rect = rect2;
            gestureWidth = gestureWidth2;
            gestureHeight = gestureHeight2;
            sx = sx2;
        }
        return sample;
    }

    private static void plot(float x, float y, float[] sample, int sampleSize) {
        float x2 = x < 0.0f ? 0.0f : x;
        float y2 = y >= 0.0f ? y : 0.0f;
        int xFloor = (int) Math.floor(x2);
        int xCeiling = (int) Math.ceil(x2);
        int yFloor = (int) Math.floor(y2);
        int yCeiling = (int) Math.ceil(y2);
        if (x2 == xFloor && y2 == yFloor) {
            int index = (yCeiling * sampleSize) + xCeiling;
            if (sample[index] < 1.0f) {
                sample[index] = 1.0f;
            }
            return;
        }
        double xFloorSq = Math.pow(xFloor - x2, 2.0d);
        double yFloorSq = Math.pow(yFloor - y2, 2.0d);
        double xCeilingSq = Math.pow(xCeiling - x2, 2.0d);
        double yCeilingSq = Math.pow(yCeiling - y2, 2.0d);
        float topLeft = (float) Math.sqrt(xFloorSq + yFloorSq);
        float topRight = (float) Math.sqrt(xCeilingSq + yFloorSq);
        float btmLeft = (float) Math.sqrt(xFloorSq + yCeilingSq);
        float btmRight = (float) Math.sqrt(xCeilingSq + yCeilingSq);
        float sum = topLeft + topRight + btmLeft + btmRight;
        float value = topLeft / sum;
        int index2 = (yFloor * sampleSize) + xFloor;
        if (value > sample[index2]) {
            sample[index2] = value;
        }
        float value2 = topRight / sum;
        int index3 = (yFloor * sampleSize) + xCeiling;
        if (value2 > sample[index3]) {
            sample[index3] = value2;
        }
        float value3 = btmLeft / sum;
        int index4 = (yCeiling * sampleSize) + xFloor;
        if (value3 > sample[index4]) {
            sample[index4] = value3;
        }
        float value4 = btmRight / sum;
        int index5 = (yCeiling * sampleSize) + xCeiling;
        if (value4 > sample[index5]) {
            sample[index5] = value4;
        }
    }

    public static float[] temporalSampling(GestureStroke stroke, int numPoints) {
        float lstPointY;
        int i;
        float increment = stroke.length / (numPoints - 1);
        int vectorLength = numPoints * 2;
        float[] vector = new float[vectorLength];
        float distanceSoFar = 0.0f;
        float[] pts = stroke.points;
        float lstPointX = pts[0];
        int i2 = 1;
        float ratio = pts[1];
        float currentPointX = Float.MIN_VALUE;
        float currentPointY = Float.MIN_VALUE;
        vector[0] = lstPointX;
        int index = 0 + 1;
        vector[index] = ratio;
        int index2 = index + 1;
        int i3 = 0;
        int count = pts.length / 2;
        while (true) {
            if (i3 >= count) {
                lstPointY = ratio;
                break;
            }
            if (currentPointX == Float.MIN_VALUE) {
                i3++;
                if (i3 >= count) {
                    lstPointY = ratio;
                    break;
                }
                currentPointX = pts[i3 * 2];
                currentPointY = pts[(i3 * 2) + i2];
            }
            float deltaX = currentPointX - lstPointX;
            float deltaY = currentPointY - ratio;
            float lstPointY2 = ratio;
            int i4 = i3;
            int count2 = count;
            float distance = (float) Math.hypot(deltaX, deltaY);
            if (distanceSoFar + distance >= increment) {
                float ratio2 = (increment - distanceSoFar) / distance;
                float nx = (ratio2 * deltaX) + lstPointX;
                float ny = lstPointY2 + (ratio2 * deltaY);
                vector[index2] = nx;
                int index3 = index2 + 1;
                vector[index3] = ny;
                i = 1;
                index2 = index3 + 1;
                lstPointX = nx;
                distanceSoFar = 0.0f;
                ratio = ny;
            } else {
                i = 1;
                lstPointX = currentPointX;
                ratio = currentPointY;
                currentPointX = Float.MIN_VALUE;
                currentPointY = Float.MIN_VALUE;
                distanceSoFar += distance;
            }
            i2 = i;
            count = count2;
            i3 = i4;
        }
        for (int i5 = index2; i5 < vectorLength; i5 += 2) {
            vector[i5] = lstPointX;
            vector[i5 + 1] = lstPointY;
        }
        return vector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float[] computeCentroid(float[] points) {
        float centerX = 0.0f;
        float centerY = 0.0f;
        int count = points.length;
        int i = 0;
        while (i < count) {
            centerX += points[i];
            int i2 = i + 1;
            centerY += points[i2];
            i = i2 + 1;
        }
        float[] center = {(centerX * 2.0f) / count, (2.0f * centerY) / count};
        return center;
    }

    private static float[][] computeCoVariance(float[] points) {
        float[][] array = (float[][]) Array.newInstance(Float.TYPE, 2, 2);
        array[0][0] = 0.0f;
        array[0][1] = 0.0f;
        array[1][0] = 0.0f;
        array[1][1] = 0.0f;
        int count = points.length;
        int i = 0;
        while (i < count) {
            float x = points[i];
            int i2 = i + 1;
            float y = points[i2];
            float[] fArr = array[0];
            fArr[0] = fArr[0] + (x * x);
            float[] fArr2 = array[0];
            fArr2[1] = fArr2[1] + (x * y);
            array[1][0] = array[0][1];
            float[] fArr3 = array[1];
            fArr3[1] = fArr3[1] + (y * y);
            i = i2 + 1;
        }
        float[] fArr4 = array[0];
        fArr4[0] = fArr4[0] / (count / 2);
        float[] fArr5 = array[0];
        fArr5[1] = fArr5[1] / (count / 2);
        float[] fArr6 = array[1];
        fArr6[0] = fArr6[0] / (count / 2);
        float[] fArr7 = array[1];
        fArr7[1] = fArr7[1] / (count / 2);
        return array;
    }

    static float computeTotalLength(float[] points) {
        float sum = 0.0f;
        int count = points.length - 4;
        for (int i = 0; i < count; i += 2) {
            float dx = points[i + 2] - points[i];
            float dy = points[i + 3] - points[i + 1];
            sum = (float) (sum + Math.hypot(dx, dy));
        }
        return sum;
    }

    static float computeStraightness(float[] points) {
        float totalLen = computeTotalLength(points);
        float dx = points[2] - points[0];
        float dy = points[3] - points[1];
        return ((float) Math.hypot(dx, dy)) / totalLen;
    }

    static float computeStraightness(float[] points, float totalLen) {
        float dx = points[2] - points[0];
        float dy = points[3] - points[1];
        return ((float) Math.hypot(dx, dy)) / totalLen;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float squaredEuclideanDistance(float[] vector1, float[] vector2) {
        float squaredDistance = 0.0f;
        int size = vector1.length;
        for (int i = 0; i < size; i++) {
            float difference = vector1[i] - vector2[i];
            squaredDistance += difference * difference;
        }
        return squaredDistance / size;
    }

    static float cosineDistance(float[] vector1, float[] vector2) {
        float sum = 0.0f;
        int len = vector1.length;
        for (int i = 0; i < len; i++) {
            sum += vector1[i] * vector2[i];
        }
        return (float) Math.acos(sum);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float minimumCosineDistance(float[] vector1, float[] vector2, int numOrientations) {
        int len = vector1.length;
        float a = 0.0f;
        float b = 0.0f;
        for (int i = 0; i < len; i += 2) {
            a += (vector1[i] * vector2[i]) + (vector1[i + 1] * vector2[i + 1]);
            b += (vector1[i] * vector2[i + 1]) - (vector1[i + 1] * vector2[i]);
        }
        if (a != 0.0f) {
            float tan = b / a;
            double angle = Math.atan(tan);
            if (numOrientations > 2 && Math.abs(angle) >= 3.141592653589793d / numOrientations) {
                return (float) Math.acos(a);
            }
            double cosine = Math.cos(angle);
            double sine = tan * cosine;
            return (float) Math.acos((a * cosine) + (b * sine));
        }
        return 1.5707964f;
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(ArrayList<GesturePoint> originalPoints) {
        int count = originalPoints.size();
        float[] points = new float[count * 2];
        for (int i = 0; i < count; i++) {
            GesturePoint point = originalPoints.get(i);
            int index = i * 2;
            points[index] = point.f58x;
            points[index + 1] = point.f59y;
        }
        float[] meanVector = computeCentroid(points);
        return computeOrientedBoundingBox(points, meanVector);
    }

    public static OrientedBoundingBox computeOrientedBoundingBox(float[] originalPoints) {
        int size = originalPoints.length;
        float[] points = new float[size];
        for (int i = 0; i < size; i++) {
            points[i] = originalPoints[i];
        }
        float[] meanVector = computeCentroid(points);
        return computeOrientedBoundingBox(points, meanVector);
    }

    private static OrientedBoundingBox computeOrientedBoundingBox(float[] points, float[] centroid) {
        float angle;
        translate(points, -centroid[0], -centroid[1]);
        float[][] array = computeCoVariance(points);
        float[] targetVector = computeOrientation(array);
        if (targetVector[0] == 0.0f && targetVector[1] == 0.0f) {
            angle = -1.5707964f;
        } else {
            angle = (float) Math.atan2(targetVector[1], targetVector[0]);
            rotate(points, -angle);
        }
        float minx = Float.MAX_VALUE;
        float miny = Float.MAX_VALUE;
        float maxx = Float.MIN_VALUE;
        float maxy = Float.MIN_VALUE;
        int count = points.length;
        int i = 0;
        while (i < count) {
            if (points[i] < minx) {
                minx = points[i];
            }
            if (points[i] > maxx) {
                maxx = points[i];
            }
            int i2 = i + 1;
            if (points[i2] < miny) {
                miny = points[i2];
            }
            if (points[i2] > maxy) {
                maxy = points[i2];
            }
            i = i2 + 1;
        }
        return new OrientedBoundingBox((float) ((180.0f * angle) / 3.141592653589793d), centroid[0], centroid[1], maxx - minx, maxy - miny);
    }

    private static float[] computeOrientation(float[][] covarianceMatrix) {
        float[] targetVector = new float[2];
        if (covarianceMatrix[0][1] == 0.0f || covarianceMatrix[1][0] == 0.0f) {
            targetVector[0] = 1.0f;
            targetVector[1] = 0.0f;
        }
        float a = (-covarianceMatrix[0][0]) - covarianceMatrix[1][1];
        float b = (covarianceMatrix[0][0] * covarianceMatrix[1][1]) - (covarianceMatrix[0][1] * covarianceMatrix[1][0]);
        float value = a / 2.0f;
        float rightside = (float) Math.sqrt(Math.pow(value, 2.0d) - b);
        float lambda1 = (-value) + rightside;
        float lambda2 = (-value) - rightside;
        if (lambda1 == lambda2) {
            targetVector[0] = 0.0f;
            targetVector[1] = 0.0f;
        } else {
            float lambda = lambda1 > lambda2 ? lambda1 : lambda2;
            targetVector[0] = 1.0f;
            targetVector[1] = (lambda - covarianceMatrix[0][0]) / covarianceMatrix[0][1];
        }
        return targetVector;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float[] rotate(float[] points, float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            float x = (points[i] * cos) - (points[i + 1] * sin);
            float y = (points[i] * sin) + (points[i + 1] * cos);
            points[i] = x;
            points[i + 1] = y;
        }
        return points;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float[] translate(float[] points, float dx, float dy) {
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            points[i] = points[i] + dx;
            int i2 = i + 1;
            points[i2] = points[i2] + dy;
        }
        return points;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static float[] scale(float[] points, float sx, float sy) {
        int size = points.length;
        for (int i = 0; i < size; i += 2) {
            points[i] = points[i] * sx;
            int i2 = i + 1;
            points[i2] = points[i2] * sy;
        }
        return points;
    }
}
