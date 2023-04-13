package android.view;

import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.display.DeviceProductInfo;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import android.util.ArraySet;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.SurfaceControl;
import com.android.internal.display.BrightnessSynchronizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class DisplayInfo implements Parcelable {
    public static final Parcelable.Creator<DisplayInfo> CREATOR = new Parcelable.Creator<DisplayInfo>() { // from class: android.view.DisplayInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayInfo createFromParcel(Parcel source) {
            return new DisplayInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayInfo[] newArray(int size) {
            return new DisplayInfo[size];
        }
    };
    public DisplayAddress address;
    public int appHeight;
    public long appVsyncOffsetNanos;
    public int appWidth;
    public float brightnessDefault;
    public float brightnessMaximum;
    public float brightnessMinimum;
    public int colorMode;
    public int committedState;
    public int defaultModeId;
    public DeviceProductInfo deviceProductInfo;
    public DisplayCutout displayCutout;
    public int displayGroupId;
    public int displayId;
    public DisplayShape displayShape;
    public int flags;
    public Display.HdrCapabilities hdrCapabilities;
    public float hdrSdrRatio;
    public int installOrientation;
    public int largestNominalAppHeight;
    public int largestNominalAppWidth;
    public int layerStack;
    public SurfaceControl.RefreshRateRange layoutLimitedRefreshRate;
    public int logicalDensityDpi;
    public int logicalHeight;
    public int logicalWidth;
    public boolean minimalPostProcessingSupported;
    public int modeId;
    public String name;
    public String ownerPackageName;
    public int ownerUid;
    public float physicalXDpi;
    public float physicalYDpi;
    public long presentationDeadlineNanos;
    public float refreshRateOverride;
    public SparseArray<SurfaceControl.RefreshRateRange> refreshRateThermalThrottling;
    public int removeMode;
    public float renderFrameRate;
    public int rotation;
    public RoundedCorners roundedCorners;
    public int smallestNominalAppHeight;
    public int smallestNominalAppWidth;
    public int state;
    public int[] supportedColorModes;
    public Display.Mode[] supportedModes;
    public int type;
    public String uniqueId;
    public int[] userDisabledHdrTypes;

    public DisplayInfo() {
        this.supportedModes = Display.Mode.EMPTY_ARRAY;
        this.supportedColorModes = new int[]{0};
        this.userDisabledHdrTypes = new int[0];
        this.removeMode = 0;
        this.hdrSdrRatio = Float.NaN;
        this.refreshRateThermalThrottling = new SparseArray<>();
    }

    public DisplayInfo(DisplayInfo other) {
        this.supportedModes = Display.Mode.EMPTY_ARRAY;
        this.supportedColorModes = new int[]{0};
        this.userDisabledHdrTypes = new int[0];
        this.removeMode = 0;
        this.hdrSdrRatio = Float.NaN;
        this.refreshRateThermalThrottling = new SparseArray<>();
        copyFrom(other);
    }

    private DisplayInfo(Parcel source) {
        this.supportedModes = Display.Mode.EMPTY_ARRAY;
        this.supportedColorModes = new int[]{0};
        this.userDisabledHdrTypes = new int[0];
        this.removeMode = 0;
        this.hdrSdrRatio = Float.NaN;
        this.refreshRateThermalThrottling = new SparseArray<>();
        readFromParcel(source);
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayInfo) && equals((DisplayInfo) o);
    }

    public boolean equals(DisplayInfo other) {
        return other != null && this.layerStack == other.layerStack && this.flags == other.flags && this.type == other.type && this.displayId == other.displayId && this.displayGroupId == other.displayGroupId && Objects.equals(this.address, other.address) && Objects.equals(this.deviceProductInfo, other.deviceProductInfo) && Objects.equals(this.uniqueId, other.uniqueId) && this.appWidth == other.appWidth && this.appHeight == other.appHeight && this.smallestNominalAppWidth == other.smallestNominalAppWidth && this.smallestNominalAppHeight == other.smallestNominalAppHeight && this.largestNominalAppWidth == other.largestNominalAppWidth && this.largestNominalAppHeight == other.largestNominalAppHeight && this.logicalWidth == other.logicalWidth && this.logicalHeight == other.logicalHeight && Objects.equals(this.displayCutout, other.displayCutout) && this.rotation == other.rotation && this.modeId == other.modeId && this.renderFrameRate == other.renderFrameRate && this.defaultModeId == other.defaultModeId && Arrays.equals(this.supportedModes, other.supportedModes) && this.colorMode == other.colorMode && Arrays.equals(this.supportedColorModes, other.supportedColorModes) && Objects.equals(this.hdrCapabilities, other.hdrCapabilities) && Arrays.equals(this.userDisabledHdrTypes, other.userDisabledHdrTypes) && this.minimalPostProcessingSupported == other.minimalPostProcessingSupported && this.logicalDensityDpi == other.logicalDensityDpi && this.physicalXDpi == other.physicalXDpi && this.physicalYDpi == other.physicalYDpi && this.appVsyncOffsetNanos == other.appVsyncOffsetNanos && this.presentationDeadlineNanos == other.presentationDeadlineNanos && this.state == other.state && this.committedState == other.committedState && this.ownerUid == other.ownerUid && Objects.equals(this.ownerPackageName, other.ownerPackageName) && this.removeMode == other.removeMode && getRefreshRate() == other.getRefreshRate() && this.brightnessMinimum == other.brightnessMinimum && this.brightnessMaximum == other.brightnessMaximum && this.brightnessDefault == other.brightnessDefault && Objects.equals(this.roundedCorners, other.roundedCorners) && this.installOrientation == other.installOrientation && Objects.equals(this.displayShape, other.displayShape) && Objects.equals(this.layoutLimitedRefreshRate, other.layoutLimitedRefreshRate) && BrightnessSynchronizer.floatEquals(this.hdrSdrRatio, other.hdrSdrRatio) && this.refreshRateThermalThrottling.contentEquals(other.refreshRateThermalThrottling);
    }

    public int hashCode() {
        return 0;
    }

    public void copyFrom(DisplayInfo other) {
        this.layerStack = other.layerStack;
        this.flags = other.flags;
        this.type = other.type;
        this.displayId = other.displayId;
        this.displayGroupId = other.displayGroupId;
        this.address = other.address;
        this.deviceProductInfo = other.deviceProductInfo;
        this.name = other.name;
        this.uniqueId = other.uniqueId;
        this.appWidth = other.appWidth;
        this.appHeight = other.appHeight;
        this.smallestNominalAppWidth = other.smallestNominalAppWidth;
        this.smallestNominalAppHeight = other.smallestNominalAppHeight;
        this.largestNominalAppWidth = other.largestNominalAppWidth;
        this.largestNominalAppHeight = other.largestNominalAppHeight;
        this.logicalWidth = other.logicalWidth;
        this.logicalHeight = other.logicalHeight;
        this.displayCutout = other.displayCutout;
        this.rotation = other.rotation;
        this.modeId = other.modeId;
        this.renderFrameRate = other.renderFrameRate;
        this.defaultModeId = other.defaultModeId;
        Display.Mode[] modeArr = other.supportedModes;
        this.supportedModes = (Display.Mode[]) Arrays.copyOf(modeArr, modeArr.length);
        this.colorMode = other.colorMode;
        int[] iArr = other.supportedColorModes;
        this.supportedColorModes = Arrays.copyOf(iArr, iArr.length);
        this.hdrCapabilities = other.hdrCapabilities;
        this.userDisabledHdrTypes = other.userDisabledHdrTypes;
        this.minimalPostProcessingSupported = other.minimalPostProcessingSupported;
        this.logicalDensityDpi = other.logicalDensityDpi;
        this.physicalXDpi = other.physicalXDpi;
        this.physicalYDpi = other.physicalYDpi;
        this.appVsyncOffsetNanos = other.appVsyncOffsetNanos;
        this.presentationDeadlineNanos = other.presentationDeadlineNanos;
        this.state = other.state;
        this.committedState = other.committedState;
        this.ownerUid = other.ownerUid;
        this.ownerPackageName = other.ownerPackageName;
        this.removeMode = other.removeMode;
        this.refreshRateOverride = other.refreshRateOverride;
        this.brightnessMinimum = other.brightnessMinimum;
        this.brightnessMaximum = other.brightnessMaximum;
        this.brightnessDefault = other.brightnessDefault;
        this.roundedCorners = other.roundedCorners;
        this.installOrientation = other.installOrientation;
        this.displayShape = other.displayShape;
        this.layoutLimitedRefreshRate = other.layoutLimitedRefreshRate;
        this.hdrSdrRatio = other.hdrSdrRatio;
        this.refreshRateThermalThrottling = other.refreshRateThermalThrottling;
    }

    public void readFromParcel(Parcel source) {
        this.layerStack = source.readInt();
        this.flags = source.readInt();
        this.type = source.readInt();
        this.displayId = source.readInt();
        this.displayGroupId = source.readInt();
        this.address = (DisplayAddress) source.readParcelable(null, DisplayAddress.class);
        this.deviceProductInfo = (DeviceProductInfo) source.readParcelable(null, DeviceProductInfo.class);
        this.name = source.readString8();
        this.appWidth = source.readInt();
        this.appHeight = source.readInt();
        this.smallestNominalAppWidth = source.readInt();
        this.smallestNominalAppHeight = source.readInt();
        this.largestNominalAppWidth = source.readInt();
        this.largestNominalAppHeight = source.readInt();
        this.logicalWidth = source.readInt();
        this.logicalHeight = source.readInt();
        this.displayCutout = DisplayCutout.ParcelableWrapper.readCutoutFromParcel(source);
        this.rotation = source.readInt();
        this.modeId = source.readInt();
        this.renderFrameRate = source.readFloat();
        this.defaultModeId = source.readInt();
        int nModes = source.readInt();
        this.supportedModes = new Display.Mode[nModes];
        for (int i = 0; i < nModes; i++) {
            this.supportedModes[i] = Display.Mode.CREATOR.createFromParcel(source);
        }
        int i2 = source.readInt();
        this.colorMode = i2;
        int nColorModes = source.readInt();
        this.supportedColorModes = new int[nColorModes];
        for (int i3 = 0; i3 < nColorModes; i3++) {
            this.supportedColorModes[i3] = source.readInt();
        }
        this.hdrCapabilities = (Display.HdrCapabilities) source.readParcelable(null, Display.HdrCapabilities.class);
        this.minimalPostProcessingSupported = source.readBoolean();
        this.logicalDensityDpi = source.readInt();
        this.physicalXDpi = source.readFloat();
        this.physicalYDpi = source.readFloat();
        this.appVsyncOffsetNanos = source.readLong();
        this.presentationDeadlineNanos = source.readLong();
        this.state = source.readInt();
        this.committedState = source.readInt();
        this.ownerUid = source.readInt();
        this.ownerPackageName = source.readString8();
        this.uniqueId = source.readString8();
        this.removeMode = source.readInt();
        this.refreshRateOverride = source.readFloat();
        this.brightnessMinimum = source.readFloat();
        this.brightnessMaximum = source.readFloat();
        this.brightnessDefault = source.readFloat();
        this.roundedCorners = (RoundedCorners) source.readTypedObject(RoundedCorners.CREATOR);
        int numUserDisabledFormats = source.readInt();
        this.userDisabledHdrTypes = new int[numUserDisabledFormats];
        for (int i4 = 0; i4 < numUserDisabledFormats; i4++) {
            this.userDisabledHdrTypes[i4] = source.readInt();
        }
        int i5 = source.readInt();
        this.installOrientation = i5;
        this.displayShape = (DisplayShape) source.readTypedObject(DisplayShape.CREATOR);
        this.layoutLimitedRefreshRate = (SurfaceControl.RefreshRateRange) source.readTypedObject(SurfaceControl.RefreshRateRange.CREATOR);
        this.hdrSdrRatio = source.readFloat();
        this.refreshRateThermalThrottling = source.readSparseArray(null, SurfaceControl.RefreshRateRange.class);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.layerStack);
        dest.writeInt(this.flags);
        dest.writeInt(this.type);
        dest.writeInt(this.displayId);
        dest.writeInt(this.displayGroupId);
        dest.writeParcelable(this.address, flags);
        dest.writeParcelable(this.deviceProductInfo, flags);
        dest.writeString8(this.name);
        dest.writeInt(this.appWidth);
        dest.writeInt(this.appHeight);
        dest.writeInt(this.smallestNominalAppWidth);
        dest.writeInt(this.smallestNominalAppHeight);
        dest.writeInt(this.largestNominalAppWidth);
        dest.writeInt(this.largestNominalAppHeight);
        dest.writeInt(this.logicalWidth);
        dest.writeInt(this.logicalHeight);
        DisplayCutout.ParcelableWrapper.writeCutoutToParcel(this.displayCutout, dest, flags);
        dest.writeInt(this.rotation);
        dest.writeInt(this.modeId);
        dest.writeFloat(this.renderFrameRate);
        dest.writeInt(this.defaultModeId);
        dest.writeInt(this.supportedModes.length);
        int i = 0;
        while (true) {
            Display.Mode[] modeArr = this.supportedModes;
            if (i >= modeArr.length) {
                break;
            }
            modeArr[i].writeToParcel(dest, flags);
            i++;
        }
        int i2 = this.colorMode;
        dest.writeInt(i2);
        dest.writeInt(this.supportedColorModes.length);
        int i3 = 0;
        while (true) {
            int[] iArr = this.supportedColorModes;
            if (i3 >= iArr.length) {
                break;
            }
            dest.writeInt(iArr[i3]);
            i3++;
        }
        dest.writeParcelable(this.hdrCapabilities, flags);
        dest.writeBoolean(this.minimalPostProcessingSupported);
        dest.writeInt(this.logicalDensityDpi);
        dest.writeFloat(this.physicalXDpi);
        dest.writeFloat(this.physicalYDpi);
        dest.writeLong(this.appVsyncOffsetNanos);
        dest.writeLong(this.presentationDeadlineNanos);
        dest.writeInt(this.state);
        dest.writeInt(this.committedState);
        dest.writeInt(this.ownerUid);
        dest.writeString8(this.ownerPackageName);
        dest.writeString8(this.uniqueId);
        dest.writeInt(this.removeMode);
        dest.writeFloat(this.refreshRateOverride);
        dest.writeFloat(this.brightnessMinimum);
        dest.writeFloat(this.brightnessMaximum);
        dest.writeFloat(this.brightnessDefault);
        dest.writeTypedObject(this.roundedCorners, flags);
        dest.writeInt(this.userDisabledHdrTypes.length);
        int i4 = 0;
        while (true) {
            int[] iArr2 = this.userDisabledHdrTypes;
            if (i4 < iArr2.length) {
                dest.writeInt(iArr2[i4]);
                i4++;
            } else {
                int i5 = this.installOrientation;
                dest.writeInt(i5);
                dest.writeTypedObject(this.displayShape, flags);
                dest.writeTypedObject(this.layoutLimitedRefreshRate, flags);
                dest.writeFloat(this.hdrSdrRatio);
                dest.writeSparseArray(this.refreshRateThermalThrottling);
                return;
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float getRefreshRate() {
        float f = this.refreshRateOverride;
        if (f > 0.0f) {
            return f;
        }
        return getMode().getRefreshRate();
    }

    public Display.Mode getMode() {
        return findMode(this.modeId);
    }

    public Display.Mode getDefaultMode() {
        return findMode(this.defaultModeId);
    }

    private Display.Mode findMode(int id) {
        int i = 0;
        while (true) {
            Display.Mode[] modeArr = this.supportedModes;
            if (i < modeArr.length) {
                if (modeArr[i].getModeId() != id) {
                    i++;
                } else {
                    return this.supportedModes[i];
                }
            } else {
                throw new IllegalStateException("Unable to locate mode " + id);
            }
        }
    }

    public Display.Mode findDefaultModeByRefreshRate(float refreshRate) {
        Display.Mode[] modes = this.supportedModes;
        Display.Mode defaultMode = getDefaultMode();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].matches(defaultMode.getPhysicalWidth(), defaultMode.getPhysicalHeight(), refreshRate)) {
                return modes[i];
            }
        }
        return null;
    }

    public float[] getDefaultRefreshRates() {
        Display.Mode[] modes = this.supportedModes;
        ArraySet<Float> rates = new ArraySet<>();
        Display.Mode defaultMode = getDefaultMode();
        for (Display.Mode mode : modes) {
            if (mode.getPhysicalWidth() == defaultMode.getPhysicalWidth() && mode.getPhysicalHeight() == defaultMode.getPhysicalHeight()) {
                rates.add(Float.valueOf(mode.getRefreshRate()));
            }
        }
        int i = rates.size();
        float[] result = new float[i];
        int i2 = 0;
        Iterator<Float> it = rates.iterator();
        while (it.hasNext()) {
            Float rate = it.next();
            result[i2] = rate.floatValue();
            i2++;
        }
        return result;
    }

    public void getAppMetrics(DisplayMetrics outMetrics) {
        getAppMetrics(outMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, null);
    }

    public void getAppMetrics(DisplayMetrics outMetrics, DisplayAdjustments displayAdjustments) {
        getMetricsWithSize(outMetrics, displayAdjustments.getCompatibilityInfo(), displayAdjustments.getConfiguration(), this.appWidth, this.appHeight);
    }

    public void getAppMetrics(DisplayMetrics outMetrics, CompatibilityInfo ci, Configuration configuration) {
        getMetricsWithSize(outMetrics, ci, configuration, this.appWidth, this.appHeight);
    }

    public void getLogicalMetrics(DisplayMetrics outMetrics, CompatibilityInfo compatInfo, Configuration configuration) {
        getMetricsWithSize(outMetrics, compatInfo, configuration, this.logicalWidth, this.logicalHeight);
    }

    public void getMaxBoundsMetrics(DisplayMetrics outMetrics, CompatibilityInfo compatInfo, Configuration configuration) {
        Rect bounds = configuration.windowConfiguration.getMaxBounds();
        getMetricsWithSize(outMetrics, compatInfo, null, bounds.width(), bounds.height());
    }

    public int getNaturalWidth() {
        int i = this.rotation;
        return (i == 0 || i == 2) ? this.logicalWidth : this.logicalHeight;
    }

    public int getNaturalHeight() {
        int i = this.rotation;
        return (i == 0 || i == 2) ? this.logicalHeight : this.logicalWidth;
    }

    public boolean isHdr() {
        Display.HdrCapabilities hdrCapabilities = this.hdrCapabilities;
        int[] types = hdrCapabilities != null ? hdrCapabilities.getSupportedHdrTypes() : null;
        return types != null && types.length > 0;
    }

    public boolean isWideColorGamut() {
        int[] iArr;
        for (int colorMode : this.supportedColorModes) {
            if (colorMode == 6 || colorMode > 7) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAccess(int uid) {
        return Display.hasAccess(uid, this.flags, this.ownerUid, this.displayId);
    }

    private void getMetricsWithSize(DisplayMetrics outMetrics, CompatibilityInfo compatInfo, Configuration configuration, int width, int height) {
        int i = this.logicalDensityDpi;
        outMetrics.noncompatDensityDpi = i;
        outMetrics.densityDpi = i;
        float f = this.logicalDensityDpi * 0.00625f;
        outMetrics.noncompatDensity = f;
        outMetrics.density = f;
        float f2 = outMetrics.density;
        outMetrics.noncompatScaledDensity = f2;
        outMetrics.scaledDensity = f2;
        float f3 = this.physicalXDpi;
        outMetrics.noncompatXdpi = f3;
        outMetrics.xdpi = f3;
        float f4 = this.physicalYDpi;
        outMetrics.noncompatYdpi = f4;
        outMetrics.ydpi = f4;
        Rect appBounds = configuration != null ? configuration.windowConfiguration.getAppBounds() : null;
        int width2 = appBounds != null ? appBounds.width() : width;
        int height2 = appBounds != null ? appBounds.height() : height;
        outMetrics.widthPixels = width2;
        outMetrics.noncompatWidthPixels = width2;
        outMetrics.heightPixels = height2;
        outMetrics.noncompatHeightPixels = height2;
        boolean applyToSize = configuration != null && appBounds == null;
        compatInfo.applyDisplayMetricsIfNeeded(outMetrics, applyToSize);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DisplayInfo{\"");
        sb.append(this.name);
        sb.append("\", displayId ");
        sb.append(this.displayId);
        sb.append(", displayGroupId ");
        sb.append(this.displayGroupId);
        sb.append(flagsToString(this.flags));
        sb.append(", real ");
        sb.append(this.logicalWidth);
        sb.append(" x ");
        sb.append(this.logicalHeight);
        sb.append(", largest app ");
        sb.append(this.largestNominalAppWidth);
        sb.append(" x ");
        sb.append(this.largestNominalAppHeight);
        sb.append(", smallest app ");
        sb.append(this.smallestNominalAppWidth);
        sb.append(" x ");
        sb.append(this.smallestNominalAppHeight);
        sb.append(", appVsyncOff ");
        sb.append(this.appVsyncOffsetNanos);
        sb.append(", presDeadline ");
        sb.append(this.presentationDeadlineNanos);
        sb.append(", mode ");
        sb.append(this.modeId);
        sb.append(this.renderFrameRate);
        sb.append(", defaultMode ");
        sb.append(this.defaultModeId);
        sb.append(", modes ");
        sb.append(Arrays.toString(this.supportedModes));
        sb.append(", hdrCapabilities ");
        sb.append(this.hdrCapabilities);
        sb.append(", userDisabledHdrTypes ");
        sb.append(Arrays.toString(this.userDisabledHdrTypes));
        sb.append(", minimalPostProcessingSupported ");
        sb.append(this.minimalPostProcessingSupported);
        sb.append(", rotation ");
        sb.append(this.rotation);
        sb.append(", state ");
        sb.append(Display.stateToString(this.state));
        sb.append(", committedState ");
        sb.append(Display.stateToString(this.committedState));
        if (Process.myUid() != 1000) {
            sb.append("}");
            return sb.toString();
        }
        sb.append(", type ");
        sb.append(Display.typeToString(this.type));
        sb.append(", uniqueId \"");
        sb.append(this.uniqueId);
        sb.append("\", app ");
        sb.append(this.appWidth);
        sb.append(" x ");
        sb.append(this.appHeight);
        sb.append(", density ");
        sb.append(this.logicalDensityDpi);
        sb.append(" (");
        sb.append(this.physicalXDpi);
        sb.append(" x ");
        sb.append(this.physicalYDpi);
        sb.append(") dpi, layerStack ");
        sb.append(this.layerStack);
        sb.append(", colorMode ");
        sb.append(this.colorMode);
        sb.append(", supportedColorModes ");
        sb.append(Arrays.toString(this.supportedColorModes));
        if (this.address != null) {
            sb.append(", address ").append(this.address);
        }
        sb.append(", deviceProductInfo ");
        sb.append(this.deviceProductInfo);
        if (this.ownerUid != 0 || this.ownerPackageName != null) {
            sb.append(", owner ").append(this.ownerPackageName);
            sb.append(" (uid ").append(this.ownerUid).append(NavigationBarInflaterView.KEY_CODE_END);
        }
        sb.append(", removeMode ");
        sb.append(this.removeMode);
        sb.append(", refreshRateOverride ");
        sb.append(this.refreshRateOverride);
        sb.append(", brightnessMinimum ");
        sb.append(this.brightnessMinimum);
        sb.append(", brightnessMaximum ");
        sb.append(this.brightnessMaximum);
        sb.append(", brightnessDefault ");
        sb.append(this.brightnessDefault);
        sb.append(", installOrientation ");
        sb.append(Surface.rotationToString(this.installOrientation));
        sb.append(", layoutLimitedRefreshRate ");
        sb.append(this.layoutLimitedRefreshRate);
        sb.append(", hdrSdrRatio ");
        if (Float.isNaN(this.hdrSdrRatio)) {
            sb.append("not_available");
        } else {
            sb.append(this.hdrSdrRatio);
        }
        sb.append(", refreshRateThermalThrottling ");
        sb.append(this.refreshRateThermalThrottling);
        sb.append("}");
        return sb.toString();
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long fieldId) {
        long token = protoOutputStream.start(fieldId);
        protoOutputStream.write(1120986464257L, this.logicalWidth);
        protoOutputStream.write(1120986464258L, this.logicalHeight);
        protoOutputStream.write(1120986464259L, this.appWidth);
        protoOutputStream.write(1120986464260L, this.appHeight);
        protoOutputStream.write(1138166333445L, this.name);
        protoOutputStream.write(1120986464262L, this.flags);
        DisplayCutout displayCutout = this.displayCutout;
        if (displayCutout != null) {
            displayCutout.dumpDebug(protoOutputStream, 1146756268039L);
        }
        protoOutputStream.end(token);
    }

    private static String flagsToString(int flags) {
        StringBuilder result = new StringBuilder();
        if ((flags & 2) != 0) {
            result.append(", FLAG_SECURE");
        }
        if ((flags & 1) != 0) {
            result.append(", FLAG_SUPPORTS_PROTECTED_BUFFERS");
        }
        if ((flags & 4) != 0) {
            result.append(", FLAG_PRIVATE");
        }
        if ((flags & 8) != 0) {
            result.append(", FLAG_PRESENTATION");
        }
        if ((1073741824 & flags) != 0) {
            result.append(", FLAG_SCALING_DISABLED");
        }
        if ((flags & 16) != 0) {
            result.append(", FLAG_ROUND");
        }
        if ((flags & 32) != 0) {
            result.append(", FLAG_CAN_SHOW_WITH_INSECURE_KEYGUARD");
        }
        if ((flags & 64) != 0) {
            result.append(", FLAG_SHOULD_SHOW_SYSTEM_DECORATIONS");
        }
        if ((flags & 128) != 0) {
            result.append(", FLAG_TRUSTED");
        }
        if ((flags & 256) != 0) {
            result.append(", FLAG_OWN_DISPLAY_GROUP");
        }
        if ((flags & 512) != 0) {
            result.append(", FLAG_ALWAYS_UNLOCKED");
        }
        if ((flags & 1024) != 0) {
            result.append(", FLAG_TOUCH_FEEDBACK_DISABLED");
        }
        if ((flags & 8192) != 0) {
            result.append(", FLAG_REAR_DISPLAY");
        }
        return result.toString();
    }
}
