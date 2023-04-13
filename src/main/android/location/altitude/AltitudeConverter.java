package android.location.altitude;

import android.content.Context;
import android.location.Location;
import com.android.internal.location.altitude.GeoidHeightMap;
import com.android.internal.location.altitude.S2CellIdUtils;
import com.android.internal.location.altitude.nano.MapParamsProto;
import com.android.internal.util.Preconditions;
import java.io.IOException;
/* loaded from: classes2.dex */
public final class AltitudeConverter {
    private static final double MAX_ABS_VALID_LATITUDE = 90.0d;
    private static final double MAX_ABS_VALID_LONGITUDE = 180.0d;
    private final GeoidHeightMap mGeoidHeightMap = new GeoidHeightMap();

    private static void validate(Location location) {
        Preconditions.checkArgument(isFiniteAndAtAbsMost(location.getLatitude(), MAX_ABS_VALID_LATITUDE), "Invalid latitude: %f", Double.valueOf(location.getLatitude()));
        Preconditions.checkArgument(isFiniteAndAtAbsMost(location.getLongitude(), MAX_ABS_VALID_LONGITUDE), "Invalid longitude: %f", Double.valueOf(location.getLongitude()));
        Preconditions.checkArgument(location.hasAltitude(), "Missing altitude above WGS84");
        Preconditions.checkArgument(Double.isFinite(location.getAltitude()), "Invalid altitude above WGS84: %f", Double.valueOf(location.getAltitude()));
    }

    private static boolean isFiniteAndAtAbsMost(double value, double rhs) {
        return Double.isFinite(value) && Math.abs(value) <= rhs;
    }

    private static long[] findMapSquare(MapParamsProto params, Location location) {
        long s2CellId = S2CellIdUtils.fromLatLngDegrees(location.getLatitude(), location.getLongitude());
        long s0 = S2CellIdUtils.getParent(s2CellId, params.mapS2Level);
        long[] edgeNeighbors = new long[4];
        S2CellIdUtils.getEdgeNeighbors(s0, edgeNeighbors);
        int i1 = S2CellIdUtils.getI(s2CellId) > S2CellIdUtils.getI(s0) ? -1 : 1;
        long s1 = edgeNeighbors[i1 + 2];
        int i2 = S2CellIdUtils.getJ(s2CellId) > S2CellIdUtils.getJ(s0) ? 1 : -1;
        long s2 = edgeNeighbors[i2 + 1];
        S2CellIdUtils.getEdgeNeighbors(s1, edgeNeighbors);
        long s3 = 0;
        int i = 0;
        while (true) {
            if (i >= edgeNeighbors.length) {
                break;
            } else if (edgeNeighbors[i] == s0) {
                int i3 = (((i1 * i2) + i) + edgeNeighbors.length) % edgeNeighbors.length;
                s3 = edgeNeighbors[i3] == s2 ? 0L : edgeNeighbors[i3];
            } else {
                i++;
            }
        }
        edgeNeighbors[0] = s0;
        edgeNeighbors[1] = s1;
        edgeNeighbors[2] = s2;
        edgeNeighbors[3] = s3;
        return edgeNeighbors;
    }

    private static void addMslAltitude(MapParamsProto params, long[] s2CellIds, double[] geoidHeightsMeters, Location location) {
        long s0 = s2CellIds[0];
        double h0 = geoidHeightsMeters[0];
        double h1 = geoidHeightsMeters[1];
        double h2 = geoidHeightsMeters[2];
        double h3 = s2CellIds[3] == 0 ? h0 : geoidHeightsMeters[3];
        long s2CellId = S2CellIdUtils.fromLatLngDegrees(location.getLatitude(), location.getLongitude());
        double sizeIj = 1 << (30 - params.mapS2Level);
        double wi = Math.abs(S2CellIdUtils.getI(s2CellId) - S2CellIdUtils.getI(s0)) / sizeIj;
        double wj = Math.abs(S2CellIdUtils.getJ(s2CellId) - S2CellIdUtils.getJ(s0)) / sizeIj;
        double offsetMeters = h0 + ((h1 - h0) * wi) + ((h2 - h0) * wj) + ((((h3 - h1) - h2) + h0) * wi * wj);
        location.setMslAltitudeMeters(location.getAltitude() - offsetMeters);
        if (location.hasVerticalAccuracy()) {
            double verticalAccuracyMeters = location.getVerticalAccuracyMeters();
            if (Double.isFinite(verticalAccuracyMeters) && verticalAccuracyMeters >= 0.0d) {
                location.setMslAltitudeAccuracyMeters((float) Math.hypot(verticalAccuracyMeters, params.modelRmseMeters));
            }
        }
    }

    public void addMslAltitudeToLocation(Context context, Location location) throws IOException {
        validate(location);
        MapParamsProto params = GeoidHeightMap.getParams(context);
        long[] s2CellIds = findMapSquare(params, location);
        double[] geoidHeightsMeters = this.mGeoidHeightMap.readGeoidHeights(params, context, s2CellIds);
        addMslAltitude(params, s2CellIds, geoidHeightsMeters, location);
    }

    public boolean addMslAltitudeToLocation(Location location) {
        long[] s2CellIds;
        double[] geoidHeightsMeters;
        validate(location);
        MapParamsProto params = GeoidHeightMap.getParams();
        if (params == null || (geoidHeightsMeters = this.mGeoidHeightMap.readGeoidHeights(params, (s2CellIds = findMapSquare(params, location)))) == null) {
            return false;
        }
        addMslAltitude(params, s2CellIds, geoidHeightsMeters, location);
        return true;
    }
}
