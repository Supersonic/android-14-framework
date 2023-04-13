package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.telephony.CbGeoUtils;
import android.text.TextUtils;
import android.util.NtpTrustedTime;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
@SystemApi
/* loaded from: classes3.dex */
public class CbGeoUtils {
    private static final String CIRCLE_SYMBOL = "circle";
    public static final int EARTH_RADIUS_METER = 6371000;
    public static final double EPS = 1.0E-7d;
    public static final int GEOMETRY_TYPE_CIRCLE = 3;
    public static final int GEOMETRY_TYPE_POLYGON = 2;
    public static final int GEO_FENCING_MAXIMUM_WAIT_TIME = 1;
    private static final String POLYGON_SYMBOL = "polygon";
    private static final String TAG = "CbGeoUtils";

    /* loaded from: classes3.dex */
    public interface Geometry {
        boolean contains(LatLng latLng);
    }

    private CbGeoUtils() {
    }

    /* loaded from: classes3.dex */
    public static class LatLng {
        public final double lat;
        public final double lng;

        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public LatLng subtract(LatLng p) {
            return new LatLng(this.lat - p.lat, this.lng - p.lng);
        }

        public double distance(LatLng p) {
            double dlat = Math.sin(Math.toRadians(this.lat - p.lat) * 0.5d);
            double dlng = Math.sin(Math.toRadians(this.lng - p.lng) * 0.5d);
            double x = (dlat * dlat) + (dlng * dlng * Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(p.lat)));
            return Math.atan2(Math.sqrt(x), Math.sqrt(1.0d - x)) * 2.0d * 6371000.0d;
        }

        public String toString() {
            return NavigationBarInflaterView.KEY_CODE_START + this.lat + "," + this.lng + NavigationBarInflaterView.KEY_CODE_END;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof LatLng) {
                LatLng l = (LatLng) o;
                return this.lat == l.lat && this.lng == l.lng;
            }
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static class Polygon implements Geometry {
        private static final double SCALE = 1000.0d;
        private final LatLng mOrigin;
        private final List<Point> mScaledVertices;
        private final List<LatLng> mVertices;

        public Polygon(List<LatLng> vertices) {
            this.mVertices = vertices;
            int idx = 0;
            for (int i = 1; i < vertices.size(); i++) {
                if (vertices.get(i).lng < vertices.get(idx).lng) {
                    idx = i;
                }
            }
            this.mOrigin = vertices.get(idx);
            this.mScaledVertices = (List) vertices.stream().map(new Function() { // from class: android.telephony.CbGeoUtils$Polygon$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    CbGeoUtils.Polygon.Point lambda$new$0;
                    lambda$new$0 = CbGeoUtils.Polygon.this.lambda$new$0((CbGeoUtils.LatLng) obj);
                    return lambda$new$0;
                }
            }).collect(Collectors.toList());
        }

        public List<LatLng> getVertices() {
            return this.mVertices;
        }

        @Override // android.telephony.CbGeoUtils.Geometry
        public boolean contains(LatLng latLng) {
            Point p = lambda$new$0(latLng);
            int n = this.mScaledVertices.size();
            int windingNumber = 0;
            for (int i = 0; i < n; i++) {
                Point a = this.mScaledVertices.get(i);
                Point b = this.mScaledVertices.get((i + 1) % n);
                int ccw = CbGeoUtils.sign(crossProduct(b.subtract(a), p.subtract(a)));
                if (ccw == 0) {
                    if (Math.min(a.f455x, b.f455x) <= p.f455x && p.f455x <= Math.max(a.f455x, b.f455x) && Math.min(a.f456y, b.f456y) <= p.f456y && p.f456y <= Math.max(a.f456y, b.f456y)) {
                        return true;
                    }
                } else if (CbGeoUtils.sign(a.f456y - p.f456y) <= 0) {
                    if (ccw > 0 && CbGeoUtils.sign(b.f456y - p.f456y) > 0) {
                        windingNumber++;
                    }
                } else if (ccw < 0 && CbGeoUtils.sign(b.f456y - p.f456y) <= 0) {
                    windingNumber--;
                }
            }
            return windingNumber != 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* renamed from: convertAndScaleLatLng */
        public Point lambda$new$0(LatLng latLng) {
            double x = latLng.lat - this.mOrigin.lat;
            double y = latLng.lng - this.mOrigin.lng;
            if (CbGeoUtils.sign(this.mOrigin.lng) != 0 && CbGeoUtils.sign(this.mOrigin.lng) != CbGeoUtils.sign(latLng.lng)) {
                double distCross0thMeridian = Math.abs(this.mOrigin.lng) + Math.abs(latLng.lng);
                if (CbGeoUtils.sign((2.0d * distCross0thMeridian) - 360.0d) > 0) {
                    y = CbGeoUtils.sign(this.mOrigin.lng) * (360.0d - distCross0thMeridian);
                }
            }
            return new Point(x * SCALE, SCALE * y);
        }

        private static double crossProduct(Point a, Point b) {
            return (a.f455x * b.f456y) - (a.f456y * b.f455x);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes3.dex */
        public static final class Point {

            /* renamed from: x */
            public final double f455x;

            /* renamed from: y */
            public final double f456y;

            Point(double x, double y) {
                this.f455x = x;
                this.f456y = y;
            }

            public Point subtract(Point p) {
                return new Point(this.f455x - p.f455x, this.f456y - p.f456y);
            }
        }

        public String toString() {
            if (TelephonyUtils.IS_DEBUGGABLE) {
                String str = "Polygon: " + this.mVertices;
                return str;
            }
            return "Polygon: ";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Polygon) {
                Polygon p = (Polygon) o;
                if (this.mVertices.size() != p.mVertices.size()) {
                    return false;
                }
                for (int i = 0; i < this.mVertices.size(); i++) {
                    if (!this.mVertices.get(i).equals(p.mVertices.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

    /* loaded from: classes3.dex */
    public static class Circle implements Geometry {
        private final LatLng mCenter;
        private final double mRadiusMeter;

        public Circle(LatLng center, double radiusInMeters) {
            this.mCenter = center;
            this.mRadiusMeter = radiusInMeters;
        }

        public LatLng getCenter() {
            return this.mCenter;
        }

        public double getRadius() {
            return this.mRadiusMeter;
        }

        @Override // android.telephony.CbGeoUtils.Geometry
        public boolean contains(LatLng latLng) {
            return this.mCenter.distance(latLng) <= this.mRadiusMeter;
        }

        public String toString() {
            if (TelephonyUtils.IS_DEBUGGABLE) {
                String str = "Circle: " + this.mCenter + ", radius = " + this.mRadiusMeter;
                return str;
            }
            return "Circle: ";
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Circle) {
                Circle c = (Circle) o;
                return this.mCenter.equals(c.mCenter) && Double.compare(this.mRadiusMeter, c.mRadiusMeter) == 0;
            }
            return false;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public static List<Geometry> parseGeometriesFromString(String str) {
        String[] split;
        char c;
        List<Geometry> geometries = new ArrayList<>();
        for (String geometryStr : str.split("\\s*;\\s*")) {
            String[] geoParameters = geometryStr.split("\\s*\\|\\s*");
            String str2 = geoParameters[0];
            switch (str2.hashCode()) {
                case -1360216880:
                    if (str2.equals(CIRCLE_SYMBOL)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -397519558:
                    if (str2.equals(POLYGON_SYMBOL)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    geometries.add(new Circle(parseLatLngFromString(geoParameters[1]), Double.parseDouble(geoParameters[2])));
                    break;
                case 1:
                    List<LatLng> vertices = new ArrayList<>(geoParameters.length - 1);
                    for (int i = 1; i < geoParameters.length; i++) {
                        vertices.add(parseLatLngFromString(geoParameters[i]));
                    }
                    geometries.add(new Polygon(vertices));
                    break;
                default:
                    com.android.telephony.Rlog.m8e(TAG, "Invalid geometry format " + geometryStr);
                    break;
            }
        }
        return geometries;
    }

    public static String encodeGeometriesToString(List<Geometry> geometries) {
        if (geometries == null || geometries.isEmpty()) {
            return "";
        }
        return (String) geometries.stream().map(new Function() { // from class: android.telephony.CbGeoUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                String encodeGeometryToString;
                encodeGeometryToString = CbGeoUtils.encodeGeometryToString((CbGeoUtils.Geometry) obj);
                return encodeGeometryToString;
            }
        }).filter(new Predicate() { // from class: android.telephony.CbGeoUtils$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CbGeoUtils.lambda$encodeGeometriesToString$1((String) obj);
            }
        }).collect(Collectors.joining(NavigationBarInflaterView.GRAVITY_SEPARATOR));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$encodeGeometriesToString$1(String encodedStr) {
        return !TextUtils.isEmpty(encodedStr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String encodeGeometryToString(Geometry geometry) {
        StringBuilder sb = new StringBuilder();
        if (geometry instanceof Polygon) {
            sb.append(POLYGON_SYMBOL);
            for (LatLng latLng : ((Polygon) geometry).getVertices()) {
                sb.append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
                sb.append(latLng.lat);
                sb.append(",");
                sb.append(latLng.lng);
            }
        } else if (geometry instanceof Circle) {
            sb.append(CIRCLE_SYMBOL);
            Circle circle = (Circle) geometry;
            sb.append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
            sb.append(circle.getCenter().lat);
            sb.append(",");
            sb.append(circle.getCenter().lng);
            sb.append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
            sb.append(circle.getRadius());
        } else {
            com.android.telephony.Rlog.m8e(TAG, "Unsupported geometry object " + geometry);
            return null;
        }
        return sb.toString();
    }

    public static LatLng parseLatLngFromString(String str) {
        String[] latLng = str.split("\\s*,\\s*");
        return new LatLng(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
    }

    public static int sign(double value) {
        if (value > 1.0E-7d) {
            return 1;
        }
        return value < -1.0E-7d ? -1 : 0;
    }
}
