package android.gesture;
/* loaded from: classes.dex */
class Instance {
    private static final float[] ORIENTATIONS = {0.0f, 0.7853982f, 1.5707964f, 2.3561945f, 3.1415927f, 0.0f, -0.7853982f, -1.5707964f, -2.3561945f, -3.1415927f};
    private static final int PATCH_SAMPLE_SIZE = 16;
    private static final int SEQUENCE_SAMPLE_SIZE = 16;

    /* renamed from: id */
    final long f60id;
    final String label;
    final float[] vector;

    private Instance(long id, float[] sample, String sampleName) {
        this.f60id = id;
        this.vector = sample;
        this.label = sampleName;
    }

    private void normalize() {
        float[] sample = this.vector;
        float sum = 0.0f;
        int size = sample.length;
        for (int i = 0; i < size; i++) {
            sum += sample[i] * sample[i];
        }
        float magnitude = (float) Math.sqrt(sum);
        for (int i2 = 0; i2 < size; i2++) {
            sample[i2] = sample[i2] / magnitude;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Instance createInstance(int sequenceType, int orientationType, Gesture gesture, String label) {
        if (sequenceType == 2) {
            float[] pts = temporalSampler(orientationType, gesture);
            Instance instance = new Instance(gesture.getID(), pts, label);
            instance.normalize();
            return instance;
        }
        float[] pts2 = spatialSampler(gesture);
        return new Instance(gesture.getID(), pts2, label);
    }

    private static float[] spatialSampler(Gesture gesture) {
        return GestureUtils.spatialSampling(gesture, 16, false);
    }

    private static float[] temporalSampler(int orientationType, Gesture gesture) {
        float[] pts = GestureUtils.temporalSampling(gesture.getStrokes().get(0), 16);
        float[] center = GestureUtils.computeCentroid(pts);
        float orientation = (float) Math.atan2(pts[1] - center[1], pts[0] - center[0]);
        float adjustment = -orientation;
        if (orientationType != 1) {
            int count = ORIENTATIONS.length;
            for (int i = 0; i < count; i++) {
                float delta = ORIENTATIONS[i] - orientation;
                if (Math.abs(delta) < Math.abs(adjustment)) {
                    adjustment = delta;
                }
            }
        }
        GestureUtils.translate(pts, -center[0], -center[1]);
        GestureUtils.rotate(pts, adjustment);
        return pts;
    }
}
