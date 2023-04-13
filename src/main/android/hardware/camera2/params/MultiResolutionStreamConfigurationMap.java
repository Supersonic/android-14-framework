package android.hardware.camera2.params;

import android.hardware.camera2.utils.HashCodeHelpers;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public final class MultiResolutionStreamConfigurationMap {
    private final Map<String, StreamConfiguration[]> mConfigurations;
    private final Map<Integer, List<MultiResolutionStreamInfo>> mMultiResolutionOutputConfigs = new HashMap();
    private final Map<Integer, List<MultiResolutionStreamInfo>> mMultiResolutionInputConfigs = new HashMap();

    public MultiResolutionStreamConfigurationMap(Map<String, StreamConfiguration[]> configurations) {
        Map<Integer, List<MultiResolutionStreamInfo>> destMap;
        Preconditions.checkNotNull(configurations, "multi-resolution configurations must not be null");
        if (configurations.size() == 0) {
            throw new IllegalArgumentException("multi-resolution configurations must not be empty");
        }
        this.mConfigurations = configurations;
        for (Map.Entry<String, StreamConfiguration[]> entry : configurations.entrySet()) {
            String cameraId = entry.getKey();
            StreamConfiguration[] configs = entry.getValue();
            for (StreamConfiguration config : configs) {
                int format = config.getFormat();
                MultiResolutionStreamInfo multiResolutionStreamInfo = new MultiResolutionStreamInfo(config.getWidth(), config.getHeight(), cameraId);
                if (config.isInput()) {
                    destMap = this.mMultiResolutionInputConfigs;
                } else {
                    destMap = this.mMultiResolutionOutputConfigs;
                }
                if (!destMap.containsKey(Integer.valueOf(format))) {
                    List<MultiResolutionStreamInfo> multiResolutionStreamInfoList = new ArrayList<>();
                    destMap.put(Integer.valueOf(format), multiResolutionStreamInfoList);
                }
                destMap.get(Integer.valueOf(format)).add(multiResolutionStreamInfo);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class SizeComparator implements Comparator<MultiResolutionStreamInfo> {
        @Override // java.util.Comparator
        public int compare(MultiResolutionStreamInfo lhs, MultiResolutionStreamInfo rhs) {
            return StreamConfigurationMap.compareSizes(lhs.getWidth(), lhs.getHeight(), rhs.getWidth(), rhs.getHeight());
        }
    }

    public int[] getOutputFormats() {
        return getPublicImageFormats(true);
    }

    public int[] getInputFormats() {
        return getPublicImageFormats(false);
    }

    private int[] getPublicImageFormats(boolean output) {
        Map<Integer, List<MultiResolutionStreamInfo>> multiResolutionConfigs = output ? this.mMultiResolutionOutputConfigs : this.mMultiResolutionInputConfigs;
        int formatCount = multiResolutionConfigs.size();
        int[] formats = new int[formatCount];
        int i = 0;
        for (Integer format : multiResolutionConfigs.keySet()) {
            formats[i] = StreamConfigurationMap.imageFormatToPublic(format.intValue());
            i++;
        }
        return formats;
    }

    public Collection<MultiResolutionStreamInfo> getOutputInfo(int format) {
        return getInfo(format, true);
    }

    public Collection<MultiResolutionStreamInfo> getInputInfo(int format) {
        return getInfo(format, false);
    }

    private Collection<MultiResolutionStreamInfo> getInfo(int format, boolean output) {
        int internalFormat = StreamConfigurationMap.imageFormatToInternal(format);
        Map<Integer, List<MultiResolutionStreamInfo>> multiResolutionConfigs = output ? this.mMultiResolutionOutputConfigs : this.mMultiResolutionInputConfigs;
        if (multiResolutionConfigs.containsKey(Integer.valueOf(internalFormat))) {
            return Collections.unmodifiableCollection(multiResolutionConfigs.get(Integer.valueOf(internalFormat)));
        }
        return Collections.emptyList();
    }

    private void appendConfigurationsString(StringBuilder sb, boolean output) {
        sb.append(output ? "Outputs(" : "Inputs(");
        int[] formats = getPublicImageFormats(output);
        if (formats != null) {
            for (int format : formats) {
                Collection<MultiResolutionStreamInfo> streamInfoList = getInfo(format, output);
                sb.append(NavigationBarInflaterView.SIZE_MOD_START + StreamConfigurationMap.formatToString(format) + ":");
                for (MultiResolutionStreamInfo streamInfo : streamInfoList) {
                    sb.append(String.format("[w:%d, h:%d, id:%s], ", Integer.valueOf(streamInfo.getWidth()), Integer.valueOf(streamInfo.getHeight()), streamInfo.getPhysicalCameraId()));
                }
                if (sb.charAt(sb.length() - 1) == ' ') {
                    sb.delete(sb.length() - 2, sb.length());
                }
                sb.append(NavigationBarInflaterView.SIZE_MOD_END);
            }
        }
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MultiResolutionStreamConfigurationMap)) {
            return false;
        }
        MultiResolutionStreamConfigurationMap other = (MultiResolutionStreamConfigurationMap) obj;
        if (!this.mConfigurations.keySet().equals(other.mConfigurations.keySet())) {
            return false;
        }
        for (String id : this.mConfigurations.keySet()) {
            if (!Arrays.equals(this.mConfigurations.get(id), other.mConfigurations.get(id))) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return HashCodeHelpers.hashCodeGeneric(this.mConfigurations, this.mMultiResolutionOutputConfigs, this.mMultiResolutionInputConfigs);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("MultiResolutionStreamConfigurationMap(");
        appendConfigurationsString(sb, true);
        sb.append(",");
        appendConfigurationsString(sb, false);
        sb.append(NavigationBarInflaterView.KEY_CODE_END);
        return sb.toString();
    }
}
