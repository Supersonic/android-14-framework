package android.media;

import android.Manifest;
import android.app.ActivityThread;
import android.bluetooth.BluetoothDevice;
import android.content.AttributionSource;
import android.media.IAudioDeviceVolumeDispatcher;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioModeDispatcher;
import android.media.IAudioRoutesObserver;
import android.media.IAudioServerStateDispatcher;
import android.media.ICapturePresetDevicesRoleDispatcher;
import android.media.ICommunicationDeviceDispatcher;
import android.media.IDeviceVolumeBehaviorDispatcher;
import android.media.IDevicesForAttributesCallback;
import android.media.IMuteAwaitConnectionCallback;
import android.media.IPlaybackConfigDispatcher;
import android.media.IPreferredMixerAttributesDispatcher;
import android.media.IRecordingConfigDispatcher;
import android.media.IRingtonePlayer;
import android.media.ISpatializerCallback;
import android.media.ISpatializerHeadToSoundStagePoseCallback;
import android.media.ISpatializerHeadTrackerAvailableCallback;
import android.media.ISpatializerHeadTrackingModeCallback;
import android.media.ISpatializerOutputCallback;
import android.media.IStrategyNonDefaultDevicesDispatcher;
import android.media.IStrategyPreferredDevicesDispatcher;
import android.media.IStreamAliasingDispatcher;
import android.media.IVolumeController;
import android.media.PlayerBase;
import android.media.audiopolicy.AudioPolicyConfig;
import android.media.audiopolicy.IAudioPolicyCallback;
import android.media.projection.IMediaProjection;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.PermissionEnforcer;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.view.KeyEvent;
import java.util.List;
import java.util.Map;
/* loaded from: classes2.dex */
public interface IAudioService extends IInterface {
    int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str, AudioAttributes audioAttributes, String str2) throws RemoteException;

    int abandonAudioFocusForTest(IAudioFocusDispatcher iAudioFocusDispatcher, String str, AudioAttributes audioAttributes, String str2) throws RemoteException;

    void addAssistantServicesUids(int[] iArr) throws RemoteException;

    int addMixForPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void addOnDevicesForAttributesChangedListener(AudioAttributes audioAttributes, IDevicesForAttributesCallback iDevicesForAttributesCallback) throws RemoteException;

    void addSpatializerCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    void adjustStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void adjustStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) throws RemoteException;

    void adjustStreamVolumeWithAttribution(int i, int i2, int i3, String str, String str2) throws RemoteException;

    void adjustSuggestedStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) throws RemoteException;

    void adjustVolumeGroupVolume(int i, int i2, int i3, String str) throws RemoteException;

    boolean areNavigationRepeatSoundEffectsEnabled() throws RemoteException;

    boolean canBeSpatialized(AudioAttributes audioAttributes, AudioFormat audioFormat) throws RemoteException;

    void cancelMuteAwaitConnection(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    int clearPreferredDevicesForCapturePreset(int i) throws RemoteException;

    int clearPreferredMixerAttributes(AudioAttributes audioAttributes, int i) throws RemoteException;

    void disableSafeMediaVolume(String str) throws RemoteException;

    int dispatchFocusChange(AudioFocusInfo audioFocusInfo, int i, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void forceComputeCsdOnAllDevices(boolean z) throws RemoteException;

    void forceRemoteSubmixFullVolume(boolean z, IBinder iBinder) throws RemoteException;

    void forceUseFrameworkMel(boolean z) throws RemoteException;

    void forceVolumeControlStream(int i, IBinder iBinder) throws RemoteException;

    int[] getActiveAssistantServiceUids() throws RemoteException;

    List<AudioPlaybackConfiguration> getActivePlaybackConfigurations() throws RemoteException;

    List<AudioRecordingConfiguration> getActiveRecordingConfigurations() throws RemoteException;

    int getActualHeadTrackingMode() throws RemoteException;

    long getAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    int getAllowedCapturePolicy() throws RemoteException;

    int[] getAssistantServicesUids() throws RemoteException;

    List<android.media.audiopolicy.AudioProductStrategy> getAudioProductStrategies() throws RemoteException;

    List<android.media.audiopolicy.AudioVolumeGroup> getAudioVolumeGroups() throws RemoteException;

    int[] getAvailableCommunicationDeviceIds() throws RemoteException;

    int getCommunicationDevice() throws RemoteException;

    float getCsd() throws RemoteException;

    int getCurrentAudioFocus() throws RemoteException;

    VolumeInfo getDefaultVolumeInfo() throws RemoteException;

    int getDesiredHeadTrackingMode() throws RemoteException;

    int getDeviceMaskForStream(int i) throws RemoteException;

    VolumeInfo getDeviceVolume(VolumeInfo volumeInfo, AudioDeviceAttributes audioDeviceAttributes, String str) throws RemoteException;

    int getDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    List<AudioDeviceAttributes> getDevicesForAttributes(AudioAttributes audioAttributes) throws RemoteException;

    List<AudioDeviceAttributes> getDevicesForAttributesUnprotected(AudioAttributes audioAttributes) throws RemoteException;

    int getEncodedSurroundMode(int i) throws RemoteException;

    long getFadeOutDurationOnFocusLossMillis(AudioAttributes audioAttributes) throws RemoteException;

    int getFocusRampTimeMs(int i, AudioAttributes audioAttributes) throws RemoteException;

    List<AudioFocusInfo> getFocusStack() throws RemoteException;

    AudioHalVersionInfo getHalVersion() throws RemoteException;

    List getIndependentStreamTypes() throws RemoteException;

    int getLastAudibleStreamVolume(int i) throws RemoteException;

    int getLastAudibleVolumeForVolumeGroup(int i) throws RemoteException;

    long getMaxAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    int getMode() throws RemoteException;

    AudioDeviceAttributes getMutingExpectedDevice() throws RemoteException;

    List<AudioDeviceAttributes> getNonDefaultDevicesForStrategy(int i) throws RemoteException;

    List<AudioDeviceAttributes> getPreferredDevicesForCapturePreset(int i) throws RemoteException;

    List<AudioDeviceAttributes> getPreferredDevicesForStrategy(int i) throws RemoteException;

    List getReportedSurroundFormats() throws RemoteException;

    int getRingerModeExternal() throws RemoteException;

    int getRingerModeInternal() throws RemoteException;

    IRingtonePlayer getRingtonePlayer() throws RemoteException;

    float getRs2Value() throws RemoteException;

    List<AudioDeviceAttributes> getSpatializerCompatibleAudioDevices() throws RemoteException;

    int getSpatializerImmersiveAudioLevel() throws RemoteException;

    int getSpatializerOutput() throws RemoteException;

    void getSpatializerParameter(int i, byte[] bArr) throws RemoteException;

    int getStreamMaxVolume(int i) throws RemoteException;

    int getStreamMinVolume(int i) throws RemoteException;

    int getStreamTypeAlias(int i) throws RemoteException;

    int getStreamVolume(int i) throws RemoteException;

    int[] getSupportedHeadTrackingModes() throws RemoteException;

    int[] getSupportedSystemUsages() throws RemoteException;

    Map getSurroundFormats() throws RemoteException;

    int getUiSoundsStreamType() throws RemoteException;

    int getVibrateSetting(int i) throws RemoteException;

    IVolumeController getVolumeController() throws RemoteException;

    int getVolumeGroupMaxVolumeIndex(int i) throws RemoteException;

    int getVolumeGroupMinVolumeIndex(int i) throws RemoteException;

    int getVolumeGroupVolumeIndex(int i) throws RemoteException;

    void handleBluetoothActiveDeviceChanged(BluetoothDevice bluetoothDevice, BluetoothDevice bluetoothDevice2, BluetoothProfileConnectionInfo bluetoothProfileConnectionInfo) throws RemoteException;

    void handleVolumeKey(KeyEvent keyEvent, boolean z, String str, String str2) throws RemoteException;

    boolean hasHapticChannels(Uri uri) throws RemoteException;

    boolean hasHeadTracker(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    boolean hasRegisteredDynamicPolicy() throws RemoteException;

    boolean isAudioServerRunning() throws RemoteException;

    boolean isBluetoothA2dpOn() throws RemoteException;

    boolean isBluetoothScoOn() throws RemoteException;

    boolean isBluetoothVariableLatencyEnabled() throws RemoteException;

    boolean isCallScreeningModeSupported() throws RemoteException;

    boolean isCameraSoundForced() throws RemoteException;

    boolean isCsdEnabled() throws RemoteException;

    boolean isHdmiSystemAudioSupported() throws RemoteException;

    boolean isHeadTrackerAvailable() throws RemoteException;

    boolean isHeadTrackerEnabled(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    boolean isHomeSoundEffectEnabled() throws RemoteException;

    boolean isHotwordStreamSupported(boolean z) throws RemoteException;

    boolean isMasterMute() throws RemoteException;

    boolean isMicrophoneMuted() throws RemoteException;

    boolean isMusicActive(boolean z) throws RemoteException;

    boolean isPstnCallAudioInterceptable() throws RemoteException;

    boolean isSpatializerAvailable() throws RemoteException;

    boolean isSpatializerAvailableForDevice(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    boolean isSpatializerEnabled() throws RemoteException;

    boolean isSpeakerphoneOn() throws RemoteException;

    boolean isStreamAffectedByMute(int i) throws RemoteException;

    boolean isStreamAffectedByRingerMode(int i) throws RemoteException;

    boolean isStreamMute(int i) throws RemoteException;

    boolean isSurroundFormatEnabled(int i) throws RemoteException;

    boolean isUltrasoundSupported() throws RemoteException;

    boolean isValidRingerMode(int i) throws RemoteException;

    boolean isVolumeControlUsingVolumeGroups() throws RemoteException;

    boolean isVolumeFixed() throws RemoteException;

    boolean isVolumeGroupMuted(int i) throws RemoteException;

    boolean loadSoundEffects() throws RemoteException;

    void lowerVolumeToRs1(String str) throws RemoteException;

    void muteAwaitConnection(int[] iArr, AudioDeviceAttributes audioDeviceAttributes, long j) throws RemoteException;

    void notifyVolumeControllerVisible(IVolumeController iVolumeController, boolean z) throws RemoteException;

    void playSoundEffect(int i, int i2) throws RemoteException;

    void playSoundEffectVolume(int i, float f) throws RemoteException;

    void playerAttributes(int i, AudioAttributes audioAttributes) throws RemoteException;

    void playerEvent(int i, int i2, int i3) throws RemoteException;

    void playerHasOpPlayAudio(int i, boolean z) throws RemoteException;

    void playerSessionId(int i, int i2) throws RemoteException;

    void portEvent(int i, int i2, PersistableBundle persistableBundle) throws RemoteException;

    void recenterHeadTracker() throws RemoteException;

    void recorderEvent(int i, int i2) throws RemoteException;

    String registerAudioPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback, boolean z, boolean z2, boolean z3, boolean z4, IMediaProjection iMediaProjection) throws RemoteException;

    void registerAudioServerStateDispatcher(IAudioServerStateDispatcher iAudioServerStateDispatcher) throws RemoteException;

    void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) throws RemoteException;

    void registerCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) throws RemoteException;

    void registerDeviceVolumeBehaviorDispatcher(boolean z, IDeviceVolumeBehaviorDispatcher iDeviceVolumeBehaviorDispatcher) throws RemoteException;

    void registerDeviceVolumeDispatcherForAbsoluteVolume(boolean z, IAudioDeviceVolumeDispatcher iAudioDeviceVolumeDispatcher, String str, AudioDeviceAttributes audioDeviceAttributes, List<VolumeInfo> list, boolean z2, int i) throws RemoteException;

    void registerHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) throws RemoteException;

    void registerModeDispatcher(IAudioModeDispatcher iAudioModeDispatcher) throws RemoteException;

    void registerMuteAwaitConnectionDispatcher(IMuteAwaitConnectionCallback iMuteAwaitConnectionCallback, boolean z) throws RemoteException;

    void registerPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) throws RemoteException;

    void registerPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher iPreferredMixerAttributesDispatcher) throws RemoteException;

    void registerRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher) throws RemoteException;

    void registerSpatializerCallback(ISpatializerCallback iSpatializerCallback) throws RemoteException;

    void registerSpatializerHeadTrackerAvailableCallback(ISpatializerHeadTrackerAvailableCallback iSpatializerHeadTrackerAvailableCallback, boolean z) throws RemoteException;

    void registerSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) throws RemoteException;

    void registerSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) throws RemoteException;

    void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) throws RemoteException;

    void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) throws RemoteException;

    void registerStreamAliasingDispatcher(IStreamAliasingDispatcher iStreamAliasingDispatcher, boolean z) throws RemoteException;

    void releasePlayer(int i) throws RemoteException;

    void releaseRecorder(int i) throws RemoteException;

    void reloadAudioSettings() throws RemoteException;

    void removeAssistantServicesUids(int[] iArr) throws RemoteException;

    int removeDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    int removeMixForPolicy(AudioPolicyConfig audioPolicyConfig, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void removeOnDevicesForAttributesChangedListener(IDevicesForAttributesCallback iDevicesForAttributesCallback) throws RemoteException;

    int removePreferredDevicesForStrategy(int i) throws RemoteException;

    void removeSpatializerCompatibleAudioDevice(AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    int removeUidDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i) throws RemoteException;

    int removeUserIdDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i) throws RemoteException;

    int requestAudioFocus(AudioAttributes audioAttributes, int i, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2, String str3, int i2, IAudioPolicyCallback iAudioPolicyCallback, int i3) throws RemoteException;

    int requestAudioFocusForTest(AudioAttributes audioAttributes, int i, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2, int i2, int i3, int i4) throws RemoteException;

    boolean sendFocusLoss(AudioFocusInfo audioFocusInfo, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void setActiveAssistantServiceUids(int[] iArr) throws RemoteException;

    boolean setAdditionalOutputDeviceDelay(AudioDeviceAttributes audioDeviceAttributes, long j) throws RemoteException;

    int setAllowedCapturePolicy(int i) throws RemoteException;

    void setBluetoothA2dpOn(boolean z) throws RemoteException;

    void setBluetoothScoOn(boolean z) throws RemoteException;

    void setBluetoothVariableLatencyEnabled(boolean z) throws RemoteException;

    boolean setCommunicationDevice(IBinder iBinder, int i) throws RemoteException;

    void setCsd(float f) throws RemoteException;

    void setDesiredHeadTrackingMode(int i) throws RemoteException;

    int setDeviceAsNonDefaultForStrategy(int i, AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    void setDeviceVolume(VolumeInfo volumeInfo, AudioDeviceAttributes audioDeviceAttributes, String str) throws RemoteException;

    void setDeviceVolumeBehavior(AudioDeviceAttributes audioDeviceAttributes, int i, String str) throws RemoteException;

    boolean setEncodedSurroundMode(int i) throws RemoteException;

    int setFocusPropertiesForPolicy(int i, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void setFocusRequestResultFromExtPolicy(AudioFocusInfo audioFocusInfo, int i, IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    int setHdmiSystemAudioSupported(boolean z) throws RemoteException;

    void setHeadTrackerEnabled(boolean z, AudioDeviceAttributes audioDeviceAttributes) throws RemoteException;

    void setHomeSoundEffectEnabled(boolean z) throws RemoteException;

    void setMasterMute(boolean z, int i, String str, int i2, String str2) throws RemoteException;

    void setMicrophoneMute(boolean z, String str, int i, String str2) throws RemoteException;

    void setMicrophoneMuteFromSwitch(boolean z) throws RemoteException;

    void setMode(int i, IBinder iBinder, String str) throws RemoteException;

    void setMultiAudioFocusEnabled(boolean z) throws RemoteException;

    void setNavigationRepeatSoundEffectsEnabled(boolean z) throws RemoteException;

    void setNotifAliasRingForTest(boolean z) throws RemoteException;

    int setPreferredDevicesForCapturePreset(int i, List<AudioDeviceAttributes> list) throws RemoteException;

    int setPreferredDevicesForStrategy(int i, List<AudioDeviceAttributes> list) throws RemoteException;

    int setPreferredMixerAttributes(AudioAttributes audioAttributes, int i, AudioMixerAttributes audioMixerAttributes) throws RemoteException;

    void setRingerModeExternal(int i, String str) throws RemoteException;

    void setRingerModeInternal(int i, String str) throws RemoteException;

    void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) throws RemoteException;

    void setRs2Value(float f) throws RemoteException;

    void setRttEnabled(boolean z) throws RemoteException;

    void setSpatializerEnabled(boolean z) throws RemoteException;

    void setSpatializerGlobalTransform(float[] fArr) throws RemoteException;

    void setSpatializerParameter(int i, byte[] bArr) throws RemoteException;

    void setSpeakerphoneOn(IBinder iBinder, boolean z) throws RemoteException;

    void setStreamVolume(int i, int i2, int i3, String str) throws RemoteException;

    void setStreamVolumeForUid(int i, int i2, int i3, String str, int i4, int i5, UserHandle userHandle, int i6) throws RemoteException;

    void setStreamVolumeWithAttribution(int i, int i2, int i3, String str, String str2) throws RemoteException;

    void setSupportedSystemUsages(int[] iArr) throws RemoteException;

    boolean setSurroundFormatEnabled(int i, boolean z) throws RemoteException;

    void setTestDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, boolean z) throws RemoteException;

    int setUidDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i, int[] iArr, String[] strArr) throws RemoteException;

    int setUserIdDeviceAffinity(IAudioPolicyCallback iAudioPolicyCallback, int i, int[] iArr, String[] strArr) throws RemoteException;

    void setVibrateSetting(int i, int i2) throws RemoteException;

    void setVolumeController(IVolumeController iVolumeController) throws RemoteException;

    void setVolumeGroupVolumeIndex(int i, int i2, int i3, String str, String str2) throws RemoteException;

    void setVolumePolicy(VolumePolicy volumePolicy) throws RemoteException;

    void setWiredDeviceConnectionState(AudioDeviceAttributes audioDeviceAttributes, int i, String str) throws RemoteException;

    boolean shouldVibrate(int i) throws RemoteException;

    void startBluetoothSco(IBinder iBinder, int i) throws RemoteException;

    void startBluetoothScoVirtualCall(IBinder iBinder) throws RemoteException;

    AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) throws RemoteException;

    void stopBluetoothSco(IBinder iBinder) throws RemoteException;

    boolean supportsBluetoothVariableLatency() throws RemoteException;

    int trackPlayer(PlayerBase.PlayerIdCard playerIdCard) throws RemoteException;

    int trackRecorder(IBinder iBinder) throws RemoteException;

    void unloadSoundEffects() throws RemoteException;

    void unregisterAudioFocusClient(String str) throws RemoteException;

    void unregisterAudioPolicy(IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void unregisterAudioPolicyAsync(IAudioPolicyCallback iAudioPolicyCallback) throws RemoteException;

    void unregisterAudioServerStateDispatcher(IAudioServerStateDispatcher iAudioServerStateDispatcher) throws RemoteException;

    void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher iCapturePresetDevicesRoleDispatcher) throws RemoteException;

    void unregisterCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher iCommunicationDeviceDispatcher) throws RemoteException;

    void unregisterHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback iSpatializerHeadToSoundStagePoseCallback) throws RemoteException;

    void unregisterModeDispatcher(IAudioModeDispatcher iAudioModeDispatcher) throws RemoteException;

    void unregisterPlaybackCallback(IPlaybackConfigDispatcher iPlaybackConfigDispatcher) throws RemoteException;

    void unregisterPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher iPreferredMixerAttributesDispatcher) throws RemoteException;

    void unregisterRecordingCallback(IRecordingConfigDispatcher iRecordingConfigDispatcher) throws RemoteException;

    void unregisterSpatializerCallback(ISpatializerCallback iSpatializerCallback) throws RemoteException;

    void unregisterSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback iSpatializerHeadTrackingModeCallback) throws RemoteException;

    void unregisterSpatializerOutputCallback(ISpatializerOutputCallback iSpatializerOutputCallback) throws RemoteException;

    void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher iStrategyNonDefaultDevicesDispatcher) throws RemoteException;

    void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher iStrategyPreferredDevicesDispatcher) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IAudioService {
        @Override // android.media.IAudioService
        public int trackPlayer(PlayerBase.PlayerIdCard pic) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void playerAttributes(int piid, AudioAttributes attr) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void playerEvent(int piid, int event, int eventId) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void releasePlayer(int piid) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int trackRecorder(IBinder recorder) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void recorderEvent(int riid, int event) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void releaseRecorder(int riid) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void playerSessionId(int piid, int sessionId) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void portEvent(int portId, int event, PersistableBundle extras) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void adjustStreamVolumeWithAttribution(int streamType, int direction, int flags, String callingPackage, String attributionTag) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setStreamVolume(int streamType, int index, int flags, String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setStreamVolumeWithAttribution(int streamType, int index, int flags, String callingPackage, String attributionTag) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada, String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public VolumeInfo getDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada, String callingPackage) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void handleVolumeKey(KeyEvent event, boolean isOnTv, String callingPackage, String caller) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isStreamMute(int streamType) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void forceRemoteSubmixFullVolume(boolean startForcing, IBinder cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isMasterMute() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setMasterMute(boolean mute, int flags, String callingPackage, int userId, String attributionTag) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getStreamVolume(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getStreamMinVolume(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getStreamMaxVolume(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public List<android.media.audiopolicy.AudioVolumeGroup> getAudioVolumeGroups() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void setVolumeGroupVolumeIndex(int groupId, int index, int flags, String callingPackage, String attributionTag) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getVolumeGroupVolumeIndex(int groupId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getVolumeGroupMaxVolumeIndex(int groupId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getVolumeGroupMinVolumeIndex(int groupId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getLastAudibleVolumeForVolumeGroup(int groupId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean isVolumeGroupMuted(int groupId) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void adjustVolumeGroupVolume(int groupId, int direction, int flags, String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getLastAudibleStreamVolume(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void setSupportedSystemUsages(int[] systemUsages) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int[] getSupportedSystemUsages() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public List<android.media.audiopolicy.AudioProductStrategy> getAudioProductStrategies() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean isMicrophoneMuted() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isUltrasoundSupported() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isHotwordStreamSupported(boolean lookbackAudio) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setMicrophoneMute(boolean on, String callingPackage, int userId, String attributionTag) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setMicrophoneMuteFromSwitch(boolean on) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setRingerModeExternal(int ringerMode, String caller) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setRingerModeInternal(int ringerMode, String caller) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getRingerModeExternal() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getRingerModeInternal() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean isValidRingerMode(int ringerMode) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setVibrateSetting(int vibrateType, int vibrateSetting) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getVibrateSetting(int vibrateType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean shouldVibrate(int vibrateType) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setMode(int mode, IBinder cb, String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getMode() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void playSoundEffect(int effectType, int userId) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void playSoundEffectVolume(int effectType, float volume) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean loadSoundEffects() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void unloadSoundEffects() throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void reloadAudioSettings() throws RemoteException {
        }

        @Override // android.media.IAudioService
        public Map getSurroundFormats() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public List getReportedSurroundFormats() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean setSurroundFormatEnabled(int audioFormat, boolean enabled) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isSurroundFormatEnabled(int audioFormat) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean setEncodedSurroundMode(int mode) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int getEncodedSurroundMode(int targetSdkVersion) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void setSpeakerphoneOn(IBinder cb, boolean on) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isSpeakerphoneOn() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setBluetoothScoOn(boolean on) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isBluetoothScoOn() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setBluetoothA2dpOn(boolean on) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isBluetoothA2dpOn() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int requestAudioFocus(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, String attributionTag, int flags, IAudioPolicyCallback pcb, int sdk) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa, String callingPackageName) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void unregisterAudioFocusClient(String clientId) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getCurrentAudioFocus() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void startBluetoothSco(IBinder cb, int targetSdkVersion) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void startBluetoothScoVirtualCall(IBinder cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void stopBluetoothSco(IBinder cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void forceVolumeControlStream(int streamType, IBinder cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setRingtonePlayer(IRingtonePlayer player) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public IRingtonePlayer getRingtonePlayer() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int getUiSoundsStreamType() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public List getIndependentStreamTypes() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int getStreamTypeAlias(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean isVolumeControlUsingVolumeGroups() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void registerStreamAliasingDispatcher(IStreamAliasingDispatcher isad, boolean register) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setNotifAliasRingForTest(boolean alias) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setWiredDeviceConnectionState(AudioDeviceAttributes aa, int state, String caller) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean isCameraSoundForced() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setVolumeController(IVolumeController controller) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public IVolumeController getVolumeController() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isStreamAffectedByRingerMode(int streamType) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isStreamAffectedByMute(int streamType) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void disableSafeMediaVolume(String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void lowerVolumeToRs1(String callingPackage) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public float getRs2Value() throws RemoteException {
            return 0.0f;
        }

        @Override // android.media.IAudioService
        public void setRs2Value(float rs2Value) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public float getCsd() throws RemoteException {
            return 0.0f;
        }

        @Override // android.media.IAudioService
        public void setCsd(float csd) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void forceUseFrameworkMel(boolean useFrameworkMel) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void forceComputeCsdOnAllDevices(boolean computeCsdOnAllDevices) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isCsdEnabled() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int setHdmiSystemAudioSupported(boolean on) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean isHdmiSystemAudioSupported() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public String registerAudioPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb, boolean hasFocusListener, boolean isFocusPolicy, boolean isTestFocusPolicy, boolean isVolumeController, IMediaProjection projection) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void unregisterAudioPolicyAsync(IAudioPolicyCallback pcb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterAudioPolicy(IAudioPolicyCallback pcb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int addMixForPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int removeMixForPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int setFocusPropertiesForPolicy(int duckingBehavior, IAudioPolicyCallback pcb) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void setVolumePolicy(VolumePolicy policy) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean hasRegisteredDynamicPolicy() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void registerRecordingCallback(IRecordingConfigDispatcher rcdb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterRecordingCallback(IRecordingConfigDispatcher rcdb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public List<AudioRecordingConfiguration> getActiveRecordingConfigurations() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void registerPlaybackCallback(IPlaybackConfigDispatcher pcdb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterPlaybackCallback(IPlaybackConfigDispatcher pcdb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public List<AudioPlaybackConfiguration> getActivePlaybackConfigurations() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int getFocusRampTimeMs(int focusGain, AudioAttributes attr) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int dispatchFocusChange(AudioFocusInfo afi, int focusChange, IAudioPolicyCallback pcb) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void playerHasOpPlayAudio(int piid, boolean hasOpPlayAudio) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void handleBluetoothActiveDeviceChanged(BluetoothDevice newDevice, BluetoothDevice previousDevice, BluetoothProfileConnectionInfo info) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setFocusRequestResultFromExtPolicy(AudioFocusInfo afi, int requestResult, IAudioPolicyCallback pcb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void registerAudioServerStateDispatcher(IAudioServerStateDispatcher asd) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterAudioServerStateDispatcher(IAudioServerStateDispatcher asd) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isAudioServerRunning() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int setUidDeviceAffinity(IAudioPolicyCallback pcb, int uid, int[] deviceTypes, String[] deviceAddresses) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int removeUidDeviceAffinity(IAudioPolicyCallback pcb, int uid) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int setUserIdDeviceAffinity(IAudioPolicyCallback pcb, int userId, int[] deviceTypes, String[] deviceAddresses) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int removeUserIdDeviceAffinity(IAudioPolicyCallback pcb, int userId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean hasHapticChannels(Uri uri) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isCallScreeningModeSupported() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int setPreferredDevicesForStrategy(int strategy, List<AudioDeviceAttributes> devices) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int removePreferredDevicesForStrategy(int strategy) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getPreferredDevicesForStrategy(int strategy) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int setDeviceAsNonDefaultForStrategy(int strategy, AudioDeviceAttributes device) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int removeDeviceAsNonDefaultForStrategy(int strategy, AudioDeviceAttributes device) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getNonDefaultDevicesForStrategy(int strategy) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getDevicesForAttributes(AudioAttributes attributes) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getDevicesForAttributesUnprotected(AudioAttributes attributes) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void addOnDevicesForAttributesChangedListener(AudioAttributes attributes, IDevicesForAttributesCallback callback) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void removeOnDevicesForAttributesChangedListener(IDevicesForAttributesCallback callback) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int setAllowedCapturePolicy(int capturePolicy) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int getAllowedCapturePolicy() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setRttEnabled(boolean rttEnabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setDeviceVolumeBehavior(AudioDeviceAttributes device, int deviceVolumeBehavior, String pkgName) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getDeviceVolumeBehavior(AudioDeviceAttributes device) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void setMultiAudioFocusEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int setPreferredDevicesForCapturePreset(int capturePreset, List<AudioDeviceAttributes> devices) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int clearPreferredDevicesForCapturePreset(int capturePreset) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getPreferredDevicesForCapturePreset(int capturePreset) throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void adjustStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void adjustSuggestedStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isMusicActive(boolean remotely) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int getDeviceMaskForStream(int streamType) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int[] getAvailableCommunicationDeviceIds() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean setCommunicationDevice(IBinder cb, int portId) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public int getCommunicationDevice() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void registerCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean areNavigationRepeatSoundEffectsEnabled() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setNavigationRepeatSoundEffectsEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isHomeSoundEffectEnabled() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setHomeSoundEffectEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean setAdditionalOutputDeviceDelay(AudioDeviceAttributes device, long delayMillis) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public long getAdditionalOutputDeviceDelay(AudioDeviceAttributes device) throws RemoteException {
            return 0L;
        }

        @Override // android.media.IAudioService
        public long getMaxAdditionalOutputDeviceDelay(AudioDeviceAttributes device) throws RemoteException {
            return 0L;
        }

        @Override // android.media.IAudioService
        public int requestAudioFocusForTest(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, int flags, int uid, int sdk) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int abandonAudioFocusForTest(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa, String callingPackageName) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public long getFadeOutDurationOnFocusLossMillis(AudioAttributes aa) throws RemoteException {
            return 0L;
        }

        @Override // android.media.IAudioService
        public void registerModeDispatcher(IAudioModeDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterModeDispatcher(IAudioModeDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getSpatializerImmersiveAudioLevel() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public boolean isSpatializerEnabled() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isSpatializerAvailable() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isSpatializerAvailableForDevice(AudioDeviceAttributes device) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean hasHeadTracker(AudioDeviceAttributes device) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setHeadTrackerEnabled(boolean enabled, AudioDeviceAttributes device) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isHeadTrackerEnabled(AudioDeviceAttributes device) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public boolean isHeadTrackerAvailable() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void registerSpatializerHeadTrackerAvailableCallback(ISpatializerHeadTrackerAvailableCallback cb, boolean register) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setSpatializerEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean canBeSpatialized(AudioAttributes aa, AudioFormat af) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void registerSpatializerCallback(ISpatializerCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterSpatializerCallback(ISpatializerCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void registerSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void registerHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public List<AudioDeviceAttributes> getSpatializerCompatibleAudioDevices() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void addSpatializerCompatibleAudioDevice(AudioDeviceAttributes ada) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void removeSpatializerCompatibleAudioDevice(AudioDeviceAttributes ada) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setDesiredHeadTrackingMode(int mode) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getDesiredHeadTrackingMode() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int[] getSupportedHeadTrackingModes() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int getActualHeadTrackingMode() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void setSpatializerGlobalTransform(float[] transform) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void recenterHeadTracker() throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setSpatializerParameter(int key, byte[] value) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void getSpatializerParameter(int key, byte[] value) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int getSpatializerOutput() throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void registerSpatializerOutputCallback(ISpatializerOutputCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterSpatializerOutputCallback(ISpatializerOutputCallback cb) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isVolumeFixed() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public VolumeInfo getDefaultVolumeInfo() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean isPstnCallAudioInterceptable() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void muteAwaitConnection(int[] usagesToMute, AudioDeviceAttributes dev, long timeOutMs) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void cancelMuteAwaitConnection(AudioDeviceAttributes dev) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public AudioDeviceAttributes getMutingExpectedDevice() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void registerMuteAwaitConnectionDispatcher(IMuteAwaitConnectionCallback cb, boolean register) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setTestDeviceConnectionState(AudioDeviceAttributes device, boolean connected) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void registerDeviceVolumeBehaviorDispatcher(boolean register, IDeviceVolumeBehaviorDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public List<AudioFocusInfo> getFocusStack() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public boolean sendFocusLoss(AudioFocusInfo focusLoser, IAudioPolicyCallback apcb) throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void addAssistantServicesUids(int[] assistantUID) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void removeAssistantServicesUids(int[] assistantUID) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void setActiveAssistantServiceUids(int[] activeUids) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public int[] getAssistantServicesUids() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int[] getActiveAssistantServiceUids() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public void registerDeviceVolumeDispatcherForAbsoluteVolume(boolean register, IAudioDeviceVolumeDispatcher cb, String packageName, AudioDeviceAttributes device, List<VolumeInfo> volumes, boolean handlesvolumeAdjustment, int volumeBehavior) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public AudioHalVersionInfo getHalVersion() throws RemoteException {
            return null;
        }

        @Override // android.media.IAudioService
        public int setPreferredMixerAttributes(AudioAttributes aa, int portId, AudioMixerAttributes mixerAttributes) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public int clearPreferredMixerAttributes(AudioAttributes aa, int portId) throws RemoteException {
            return 0;
        }

        @Override // android.media.IAudioService
        public void registerPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public void unregisterPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher dispatcher) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean supportsBluetoothVariableLatency() throws RemoteException {
            return false;
        }

        @Override // android.media.IAudioService
        public void setBluetoothVariableLatencyEnabled(boolean enabled) throws RemoteException {
        }

        @Override // android.media.IAudioService
        public boolean isBluetoothVariableLatencyEnabled() throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IAudioService {
        public static final String DESCRIPTOR = "android.media.IAudioService";
        static final int TRANSACTION_abandonAudioFocus = 69;
        static final int TRANSACTION_abandonAudioFocusForTest = 174;
        static final int TRANSACTION_addAssistantServicesUids = 220;
        static final int TRANSACTION_addMixForPolicy = 106;
        static final int TRANSACTION_addOnDevicesForAttributesChangedListener = 139;
        static final int TRANSACTION_addSpatializerCompatibleAudioDevice = 196;
        static final int TRANSACTION_adjustStreamVolume = 10;
        static final int TRANSACTION_adjustStreamVolumeForUid = 156;
        static final int TRANSACTION_adjustStreamVolumeWithAttribution = 11;
        static final int TRANSACTION_adjustSuggestedStreamVolumeForUid = 157;
        static final int TRANSACTION_adjustVolumeGroupVolume = 31;
        static final int TRANSACTION_areNavigationRepeatSoundEffectsEnabled = 166;
        static final int TRANSACTION_canBeSpatialized = 188;
        static final int TRANSACTION_cancelMuteAwaitConnection = 213;
        static final int TRANSACTION_clearPreferredDevicesForCapturePreset = 152;
        static final int TRANSACTION_clearPreferredMixerAttributes = 228;
        static final int TRANSACTION_disableSafeMediaVolume = 92;
        static final int TRANSACTION_dispatchFocusChange = 118;
        static final int TRANSACTION_forceComputeCsdOnAllDevices = 99;
        static final int TRANSACTION_forceRemoteSubmixFullVolume = 18;
        static final int TRANSACTION_forceUseFrameworkMel = 98;
        static final int TRANSACTION_forceVolumeControlStream = 75;
        static final int TRANSACTION_getActiveAssistantServiceUids = 224;
        static final int TRANSACTION_getActivePlaybackConfigurations = 116;
        static final int TRANSACTION_getActiveRecordingConfigurations = 113;
        static final int TRANSACTION_getActualHeadTrackingMode = 201;
        static final int TRANSACTION_getAdditionalOutputDeviceDelay = 171;
        static final int TRANSACTION_getAllowedCapturePolicy = 142;
        static final int TRANSACTION_getAssistantServicesUids = 223;
        static final int TRANSACTION_getAudioProductStrategies = 35;
        static final int TRANSACTION_getAudioVolumeGroups = 24;
        static final int TRANSACTION_getAvailableCommunicationDeviceIds = 161;
        static final int TRANSACTION_getCommunicationDevice = 163;
        static final int TRANSACTION_getCsd = 96;
        static final int TRANSACTION_getCurrentAudioFocus = 71;
        static final int TRANSACTION_getDefaultVolumeInfo = 210;
        static final int TRANSACTION_getDesiredHeadTrackingMode = 199;
        static final int TRANSACTION_getDeviceMaskForStream = 160;
        static final int TRANSACTION_getDeviceVolume = 15;
        static final int TRANSACTION_getDeviceVolumeBehavior = 149;
        static final int TRANSACTION_getDevicesForAttributes = 137;
        static final int TRANSACTION_getDevicesForAttributesUnprotected = 138;
        static final int TRANSACTION_getEncodedSurroundMode = 61;
        static final int TRANSACTION_getFadeOutDurationOnFocusLossMillis = 175;
        static final int TRANSACTION_getFocusRampTimeMs = 117;
        static final int TRANSACTION_getFocusStack = 218;
        static final int TRANSACTION_getHalVersion = 226;
        static final int TRANSACTION_getIndependentStreamTypes = 79;
        static final int TRANSACTION_getLastAudibleStreamVolume = 32;
        static final int TRANSACTION_getLastAudibleVolumeForVolumeGroup = 29;
        static final int TRANSACTION_getMaxAdditionalOutputDeviceDelay = 172;
        static final int TRANSACTION_getMode = 50;
        static final int TRANSACTION_getMutingExpectedDevice = 214;
        static final int TRANSACTION_getNonDefaultDevicesForStrategy = 136;
        static final int TRANSACTION_getPreferredDevicesForCapturePreset = 153;
        static final int TRANSACTION_getPreferredDevicesForStrategy = 133;
        static final int TRANSACTION_getReportedSurroundFormats = 57;
        static final int TRANSACTION_getRingerModeExternal = 43;
        static final int TRANSACTION_getRingerModeInternal = 44;
        static final int TRANSACTION_getRingtonePlayer = 77;
        static final int TRANSACTION_getRs2Value = 94;
        static final int TRANSACTION_getSpatializerCompatibleAudioDevices = 195;
        static final int TRANSACTION_getSpatializerImmersiveAudioLevel = 178;
        static final int TRANSACTION_getSpatializerOutput = 206;
        static final int TRANSACTION_getSpatializerParameter = 205;
        static final int TRANSACTION_getStreamMaxVolume = 23;
        static final int TRANSACTION_getStreamMinVolume = 22;
        static final int TRANSACTION_getStreamTypeAlias = 80;
        static final int TRANSACTION_getStreamVolume = 21;
        static final int TRANSACTION_getSupportedHeadTrackingModes = 200;
        static final int TRANSACTION_getSupportedSystemUsages = 34;
        static final int TRANSACTION_getSurroundFormats = 56;
        static final int TRANSACTION_getUiSoundsStreamType = 78;
        static final int TRANSACTION_getVibrateSetting = 47;
        static final int TRANSACTION_getVolumeController = 88;
        static final int TRANSACTION_getVolumeGroupMaxVolumeIndex = 27;
        static final int TRANSACTION_getVolumeGroupMinVolumeIndex = 28;
        static final int TRANSACTION_getVolumeGroupVolumeIndex = 26;
        static final int TRANSACTION_handleBluetoothActiveDeviceChanged = 120;
        static final int TRANSACTION_handleVolumeKey = 16;
        static final int TRANSACTION_hasHapticChannels = 129;
        static final int TRANSACTION_hasHeadTracker = 182;
        static final int TRANSACTION_hasRegisteredDynamicPolicy = 110;
        static final int TRANSACTION_isAudioServerRunning = 124;
        static final int TRANSACTION_isBluetoothA2dpOn = 67;
        static final int TRANSACTION_isBluetoothScoOn = 65;
        static final int TRANSACTION_isBluetoothVariableLatencyEnabled = 233;
        static final int TRANSACTION_isCallScreeningModeSupported = 130;
        static final int TRANSACTION_isCameraSoundForced = 86;
        static final int TRANSACTION_isCsdEnabled = 100;
        static final int TRANSACTION_isHdmiSystemAudioSupported = 102;
        static final int TRANSACTION_isHeadTrackerAvailable = 185;
        static final int TRANSACTION_isHeadTrackerEnabled = 184;
        static final int TRANSACTION_isHomeSoundEffectEnabled = 168;
        static final int TRANSACTION_isHotwordStreamSupported = 38;
        static final int TRANSACTION_isMasterMute = 19;
        static final int TRANSACTION_isMicrophoneMuted = 36;
        static final int TRANSACTION_isMusicActive = 159;
        static final int TRANSACTION_isPstnCallAudioInterceptable = 211;
        static final int TRANSACTION_isSpatializerAvailable = 180;
        static final int TRANSACTION_isSpatializerAvailableForDevice = 181;
        static final int TRANSACTION_isSpatializerEnabled = 179;
        static final int TRANSACTION_isSpeakerphoneOn = 63;
        static final int TRANSACTION_isStreamAffectedByMute = 91;
        static final int TRANSACTION_isStreamAffectedByRingerMode = 90;
        static final int TRANSACTION_isStreamMute = 17;
        static final int TRANSACTION_isSurroundFormatEnabled = 59;
        static final int TRANSACTION_isUltrasoundSupported = 37;
        static final int TRANSACTION_isValidRingerMode = 45;
        static final int TRANSACTION_isVolumeControlUsingVolumeGroups = 81;
        static final int TRANSACTION_isVolumeFixed = 209;
        static final int TRANSACTION_isVolumeGroupMuted = 30;
        static final int TRANSACTION_loadSoundEffects = 53;
        static final int TRANSACTION_lowerVolumeToRs1 = 93;
        static final int TRANSACTION_muteAwaitConnection = 212;
        static final int TRANSACTION_notifyVolumeControllerVisible = 89;
        static final int TRANSACTION_playSoundEffect = 51;
        static final int TRANSACTION_playSoundEffectVolume = 52;
        static final int TRANSACTION_playerAttributes = 2;
        static final int TRANSACTION_playerEvent = 3;
        static final int TRANSACTION_playerHasOpPlayAudio = 119;
        static final int TRANSACTION_playerSessionId = 8;
        static final int TRANSACTION_portEvent = 9;
        static final int TRANSACTION_recenterHeadTracker = 203;
        static final int TRANSACTION_recorderEvent = 6;
        static final int TRANSACTION_registerAudioPolicy = 103;
        static final int TRANSACTION_registerAudioServerStateDispatcher = 122;
        static final int TRANSACTION_registerCapturePresetDevicesRoleDispatcher = 154;
        static final int TRANSACTION_registerCommunicationDeviceDispatcher = 164;
        static final int TRANSACTION_registerDeviceVolumeBehaviorDispatcher = 217;
        static final int TRANSACTION_registerDeviceVolumeDispatcherForAbsoluteVolume = 225;
        static final int TRANSACTION_registerHeadToSoundstagePoseCallback = 193;
        static final int TRANSACTION_registerModeDispatcher = 176;
        static final int TRANSACTION_registerMuteAwaitConnectionDispatcher = 215;
        static final int TRANSACTION_registerPlaybackCallback = 114;
        static final int TRANSACTION_registerPreferredMixerAttributesDispatcher = 229;
        static final int TRANSACTION_registerRecordingCallback = 111;
        static final int TRANSACTION_registerSpatializerCallback = 189;
        static final int TRANSACTION_registerSpatializerHeadTrackerAvailableCallback = 186;
        static final int TRANSACTION_registerSpatializerHeadTrackingCallback = 191;
        static final int TRANSACTION_registerSpatializerOutputCallback = 207;
        static final int TRANSACTION_registerStrategyNonDefaultDevicesDispatcher = 145;
        static final int TRANSACTION_registerStrategyPreferredDevicesDispatcher = 143;
        static final int TRANSACTION_registerStreamAliasingDispatcher = 82;
        static final int TRANSACTION_releasePlayer = 4;
        static final int TRANSACTION_releaseRecorder = 7;
        static final int TRANSACTION_reloadAudioSettings = 55;
        static final int TRANSACTION_removeAssistantServicesUids = 221;
        static final int TRANSACTION_removeDeviceAsNonDefaultForStrategy = 135;
        static final int TRANSACTION_removeMixForPolicy = 107;
        static final int TRANSACTION_removeOnDevicesForAttributesChangedListener = 140;
        static final int TRANSACTION_removePreferredDevicesForStrategy = 132;
        static final int TRANSACTION_removeSpatializerCompatibleAudioDevice = 197;
        static final int TRANSACTION_removeUidDeviceAffinity = 126;
        static final int TRANSACTION_removeUserIdDeviceAffinity = 128;
        static final int TRANSACTION_requestAudioFocus = 68;
        static final int TRANSACTION_requestAudioFocusForTest = 173;
        static final int TRANSACTION_sendFocusLoss = 219;
        static final int TRANSACTION_setActiveAssistantServiceUids = 222;
        static final int TRANSACTION_setAdditionalOutputDeviceDelay = 170;
        static final int TRANSACTION_setAllowedCapturePolicy = 141;
        static final int TRANSACTION_setBluetoothA2dpOn = 66;
        static final int TRANSACTION_setBluetoothScoOn = 64;
        static final int TRANSACTION_setBluetoothVariableLatencyEnabled = 232;
        static final int TRANSACTION_setCommunicationDevice = 162;
        static final int TRANSACTION_setCsd = 97;
        static final int TRANSACTION_setDesiredHeadTrackingMode = 198;
        static final int TRANSACTION_setDeviceAsNonDefaultForStrategy = 134;
        static final int TRANSACTION_setDeviceVolume = 14;
        static final int TRANSACTION_setDeviceVolumeBehavior = 148;
        static final int TRANSACTION_setEncodedSurroundMode = 60;
        static final int TRANSACTION_setFocusPropertiesForPolicy = 108;
        static final int TRANSACTION_setFocusRequestResultFromExtPolicy = 121;
        static final int TRANSACTION_setHdmiSystemAudioSupported = 101;
        static final int TRANSACTION_setHeadTrackerEnabled = 183;
        static final int TRANSACTION_setHomeSoundEffectEnabled = 169;
        static final int TRANSACTION_setMasterMute = 20;
        static final int TRANSACTION_setMicrophoneMute = 39;
        static final int TRANSACTION_setMicrophoneMuteFromSwitch = 40;
        static final int TRANSACTION_setMode = 49;
        static final int TRANSACTION_setMultiAudioFocusEnabled = 150;
        static final int TRANSACTION_setNavigationRepeatSoundEffectsEnabled = 167;
        static final int TRANSACTION_setNotifAliasRingForTest = 83;
        static final int TRANSACTION_setPreferredDevicesForCapturePreset = 151;
        static final int TRANSACTION_setPreferredDevicesForStrategy = 131;
        static final int TRANSACTION_setPreferredMixerAttributes = 227;
        static final int TRANSACTION_setRingerModeExternal = 41;
        static final int TRANSACTION_setRingerModeInternal = 42;
        static final int TRANSACTION_setRingtonePlayer = 76;
        static final int TRANSACTION_setRs2Value = 95;
        static final int TRANSACTION_setRttEnabled = 147;
        static final int TRANSACTION_setSpatializerEnabled = 187;
        static final int TRANSACTION_setSpatializerGlobalTransform = 202;
        static final int TRANSACTION_setSpatializerParameter = 204;
        static final int TRANSACTION_setSpeakerphoneOn = 62;
        static final int TRANSACTION_setStreamVolume = 12;
        static final int TRANSACTION_setStreamVolumeForUid = 158;
        static final int TRANSACTION_setStreamVolumeWithAttribution = 13;
        static final int TRANSACTION_setSupportedSystemUsages = 33;
        static final int TRANSACTION_setSurroundFormatEnabled = 58;
        static final int TRANSACTION_setTestDeviceConnectionState = 216;
        static final int TRANSACTION_setUidDeviceAffinity = 125;
        static final int TRANSACTION_setUserIdDeviceAffinity = 127;
        static final int TRANSACTION_setVibrateSetting = 46;
        static final int TRANSACTION_setVolumeController = 87;
        static final int TRANSACTION_setVolumeGroupVolumeIndex = 25;
        static final int TRANSACTION_setVolumePolicy = 109;
        static final int TRANSACTION_setWiredDeviceConnectionState = 84;
        static final int TRANSACTION_shouldVibrate = 48;
        static final int TRANSACTION_startBluetoothSco = 72;
        static final int TRANSACTION_startBluetoothScoVirtualCall = 73;
        static final int TRANSACTION_startWatchingRoutes = 85;
        static final int TRANSACTION_stopBluetoothSco = 74;
        static final int TRANSACTION_supportsBluetoothVariableLatency = 231;
        static final int TRANSACTION_trackPlayer = 1;
        static final int TRANSACTION_trackRecorder = 5;
        static final int TRANSACTION_unloadSoundEffects = 54;
        static final int TRANSACTION_unregisterAudioFocusClient = 70;
        static final int TRANSACTION_unregisterAudioPolicy = 105;
        static final int TRANSACTION_unregisterAudioPolicyAsync = 104;
        static final int TRANSACTION_unregisterAudioServerStateDispatcher = 123;
        static final int TRANSACTION_unregisterCapturePresetDevicesRoleDispatcher = 155;
        static final int TRANSACTION_unregisterCommunicationDeviceDispatcher = 165;
        static final int TRANSACTION_unregisterHeadToSoundstagePoseCallback = 194;
        static final int TRANSACTION_unregisterModeDispatcher = 177;
        static final int TRANSACTION_unregisterPlaybackCallback = 115;
        static final int TRANSACTION_unregisterPreferredMixerAttributesDispatcher = 230;
        static final int TRANSACTION_unregisterRecordingCallback = 112;
        static final int TRANSACTION_unregisterSpatializerCallback = 190;
        static final int TRANSACTION_unregisterSpatializerHeadTrackingCallback = 192;
        static final int TRANSACTION_unregisterSpatializerOutputCallback = 208;
        static final int TRANSACTION_unregisterStrategyNonDefaultDevicesDispatcher = 146;
        static final int TRANSACTION_unregisterStrategyPreferredDevicesDispatcher = 144;
        private final PermissionEnforcer mEnforcer;

        public Stub(PermissionEnforcer enforcer) {
            attachInterface(this, DESCRIPTOR);
            if (enforcer == null) {
                throw new IllegalArgumentException("enforcer cannot be null");
            }
            this.mEnforcer = enforcer;
        }

        @Deprecated
        public Stub() {
            this(PermissionEnforcer.fromContext(ActivityThread.currentActivityThread().getSystemContext()));
        }

        public static IAudioService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAudioService)) {
                return (IAudioService) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "trackPlayer";
                case 2:
                    return "playerAttributes";
                case 3:
                    return "playerEvent";
                case 4:
                    return "releasePlayer";
                case 5:
                    return "trackRecorder";
                case 6:
                    return "recorderEvent";
                case 7:
                    return "releaseRecorder";
                case 8:
                    return "playerSessionId";
                case 9:
                    return "portEvent";
                case 10:
                    return "adjustStreamVolume";
                case 11:
                    return "adjustStreamVolumeWithAttribution";
                case 12:
                    return "setStreamVolume";
                case 13:
                    return "setStreamVolumeWithAttribution";
                case 14:
                    return "setDeviceVolume";
                case 15:
                    return "getDeviceVolume";
                case 16:
                    return "handleVolumeKey";
                case 17:
                    return "isStreamMute";
                case 18:
                    return "forceRemoteSubmixFullVolume";
                case 19:
                    return "isMasterMute";
                case 20:
                    return "setMasterMute";
                case 21:
                    return "getStreamVolume";
                case 22:
                    return "getStreamMinVolume";
                case 23:
                    return "getStreamMaxVolume";
                case 24:
                    return "getAudioVolumeGroups";
                case 25:
                    return "setVolumeGroupVolumeIndex";
                case 26:
                    return "getVolumeGroupVolumeIndex";
                case 27:
                    return "getVolumeGroupMaxVolumeIndex";
                case 28:
                    return "getVolumeGroupMinVolumeIndex";
                case 29:
                    return "getLastAudibleVolumeForVolumeGroup";
                case 30:
                    return "isVolumeGroupMuted";
                case 31:
                    return "adjustVolumeGroupVolume";
                case 32:
                    return "getLastAudibleStreamVolume";
                case 33:
                    return "setSupportedSystemUsages";
                case 34:
                    return "getSupportedSystemUsages";
                case 35:
                    return "getAudioProductStrategies";
                case 36:
                    return "isMicrophoneMuted";
                case 37:
                    return "isUltrasoundSupported";
                case 38:
                    return "isHotwordStreamSupported";
                case 39:
                    return "setMicrophoneMute";
                case 40:
                    return "setMicrophoneMuteFromSwitch";
                case 41:
                    return "setRingerModeExternal";
                case 42:
                    return "setRingerModeInternal";
                case 43:
                    return "getRingerModeExternal";
                case 44:
                    return "getRingerModeInternal";
                case 45:
                    return "isValidRingerMode";
                case 46:
                    return "setVibrateSetting";
                case 47:
                    return "getVibrateSetting";
                case 48:
                    return "shouldVibrate";
                case 49:
                    return "setMode";
                case 50:
                    return "getMode";
                case 51:
                    return "playSoundEffect";
                case 52:
                    return "playSoundEffectVolume";
                case 53:
                    return "loadSoundEffects";
                case 54:
                    return "unloadSoundEffects";
                case 55:
                    return "reloadAudioSettings";
                case 56:
                    return "getSurroundFormats";
                case 57:
                    return "getReportedSurroundFormats";
                case 58:
                    return "setSurroundFormatEnabled";
                case 59:
                    return "isSurroundFormatEnabled";
                case 60:
                    return "setEncodedSurroundMode";
                case 61:
                    return "getEncodedSurroundMode";
                case 62:
                    return "setSpeakerphoneOn";
                case 63:
                    return "isSpeakerphoneOn";
                case 64:
                    return "setBluetoothScoOn";
                case 65:
                    return "isBluetoothScoOn";
                case 66:
                    return "setBluetoothA2dpOn";
                case 67:
                    return "isBluetoothA2dpOn";
                case 68:
                    return "requestAudioFocus";
                case 69:
                    return "abandonAudioFocus";
                case 70:
                    return "unregisterAudioFocusClient";
                case 71:
                    return "getCurrentAudioFocus";
                case 72:
                    return "startBluetoothSco";
                case 73:
                    return "startBluetoothScoVirtualCall";
                case 74:
                    return "stopBluetoothSco";
                case 75:
                    return "forceVolumeControlStream";
                case 76:
                    return "setRingtonePlayer";
                case 77:
                    return "getRingtonePlayer";
                case 78:
                    return "getUiSoundsStreamType";
                case 79:
                    return "getIndependentStreamTypes";
                case 80:
                    return "getStreamTypeAlias";
                case 81:
                    return "isVolumeControlUsingVolumeGroups";
                case 82:
                    return "registerStreamAliasingDispatcher";
                case 83:
                    return "setNotifAliasRingForTest";
                case 84:
                    return "setWiredDeviceConnectionState";
                case 85:
                    return "startWatchingRoutes";
                case 86:
                    return "isCameraSoundForced";
                case 87:
                    return "setVolumeController";
                case 88:
                    return "getVolumeController";
                case 89:
                    return "notifyVolumeControllerVisible";
                case 90:
                    return "isStreamAffectedByRingerMode";
                case 91:
                    return "isStreamAffectedByMute";
                case 92:
                    return "disableSafeMediaVolume";
                case 93:
                    return "lowerVolumeToRs1";
                case 94:
                    return "getRs2Value";
                case 95:
                    return "setRs2Value";
                case 96:
                    return "getCsd";
                case 97:
                    return "setCsd";
                case 98:
                    return "forceUseFrameworkMel";
                case 99:
                    return "forceComputeCsdOnAllDevices";
                case 100:
                    return "isCsdEnabled";
                case 101:
                    return "setHdmiSystemAudioSupported";
                case 102:
                    return "isHdmiSystemAudioSupported";
                case 103:
                    return "registerAudioPolicy";
                case 104:
                    return "unregisterAudioPolicyAsync";
                case 105:
                    return "unregisterAudioPolicy";
                case 106:
                    return "addMixForPolicy";
                case 107:
                    return "removeMixForPolicy";
                case 108:
                    return "setFocusPropertiesForPolicy";
                case 109:
                    return "setVolumePolicy";
                case 110:
                    return "hasRegisteredDynamicPolicy";
                case 111:
                    return "registerRecordingCallback";
                case 112:
                    return "unregisterRecordingCallback";
                case 113:
                    return "getActiveRecordingConfigurations";
                case 114:
                    return "registerPlaybackCallback";
                case 115:
                    return "unregisterPlaybackCallback";
                case 116:
                    return "getActivePlaybackConfigurations";
                case 117:
                    return "getFocusRampTimeMs";
                case 118:
                    return "dispatchFocusChange";
                case 119:
                    return "playerHasOpPlayAudio";
                case 120:
                    return "handleBluetoothActiveDeviceChanged";
                case 121:
                    return "setFocusRequestResultFromExtPolicy";
                case 122:
                    return "registerAudioServerStateDispatcher";
                case 123:
                    return "unregisterAudioServerStateDispatcher";
                case 124:
                    return "isAudioServerRunning";
                case 125:
                    return "setUidDeviceAffinity";
                case 126:
                    return "removeUidDeviceAffinity";
                case 127:
                    return "setUserIdDeviceAffinity";
                case 128:
                    return "removeUserIdDeviceAffinity";
                case 129:
                    return "hasHapticChannels";
                case 130:
                    return "isCallScreeningModeSupported";
                case 131:
                    return "setPreferredDevicesForStrategy";
                case 132:
                    return "removePreferredDevicesForStrategy";
                case 133:
                    return "getPreferredDevicesForStrategy";
                case 134:
                    return "setDeviceAsNonDefaultForStrategy";
                case 135:
                    return "removeDeviceAsNonDefaultForStrategy";
                case 136:
                    return "getNonDefaultDevicesForStrategy";
                case 137:
                    return "getDevicesForAttributes";
                case 138:
                    return "getDevicesForAttributesUnprotected";
                case 139:
                    return "addOnDevicesForAttributesChangedListener";
                case 140:
                    return "removeOnDevicesForAttributesChangedListener";
                case 141:
                    return "setAllowedCapturePolicy";
                case 142:
                    return "getAllowedCapturePolicy";
                case 143:
                    return "registerStrategyPreferredDevicesDispatcher";
                case 144:
                    return "unregisterStrategyPreferredDevicesDispatcher";
                case 145:
                    return "registerStrategyNonDefaultDevicesDispatcher";
                case 146:
                    return "unregisterStrategyNonDefaultDevicesDispatcher";
                case 147:
                    return "setRttEnabled";
                case 148:
                    return "setDeviceVolumeBehavior";
                case 149:
                    return "getDeviceVolumeBehavior";
                case 150:
                    return "setMultiAudioFocusEnabled";
                case 151:
                    return "setPreferredDevicesForCapturePreset";
                case 152:
                    return "clearPreferredDevicesForCapturePreset";
                case 153:
                    return "getPreferredDevicesForCapturePreset";
                case 154:
                    return "registerCapturePresetDevicesRoleDispatcher";
                case 155:
                    return "unregisterCapturePresetDevicesRoleDispatcher";
                case 156:
                    return "adjustStreamVolumeForUid";
                case 157:
                    return "adjustSuggestedStreamVolumeForUid";
                case 158:
                    return "setStreamVolumeForUid";
                case 159:
                    return "isMusicActive";
                case 160:
                    return "getDeviceMaskForStream";
                case 161:
                    return "getAvailableCommunicationDeviceIds";
                case 162:
                    return "setCommunicationDevice";
                case 163:
                    return "getCommunicationDevice";
                case 164:
                    return "registerCommunicationDeviceDispatcher";
                case 165:
                    return "unregisterCommunicationDeviceDispatcher";
                case 166:
                    return "areNavigationRepeatSoundEffectsEnabled";
                case 167:
                    return "setNavigationRepeatSoundEffectsEnabled";
                case 168:
                    return "isHomeSoundEffectEnabled";
                case 169:
                    return "setHomeSoundEffectEnabled";
                case 170:
                    return "setAdditionalOutputDeviceDelay";
                case 171:
                    return "getAdditionalOutputDeviceDelay";
                case 172:
                    return "getMaxAdditionalOutputDeviceDelay";
                case 173:
                    return "requestAudioFocusForTest";
                case 174:
                    return "abandonAudioFocusForTest";
                case 175:
                    return "getFadeOutDurationOnFocusLossMillis";
                case 176:
                    return "registerModeDispatcher";
                case 177:
                    return "unregisterModeDispatcher";
                case 178:
                    return "getSpatializerImmersiveAudioLevel";
                case 179:
                    return "isSpatializerEnabled";
                case 180:
                    return "isSpatializerAvailable";
                case 181:
                    return "isSpatializerAvailableForDevice";
                case 182:
                    return "hasHeadTracker";
                case 183:
                    return "setHeadTrackerEnabled";
                case 184:
                    return "isHeadTrackerEnabled";
                case 185:
                    return "isHeadTrackerAvailable";
                case 186:
                    return "registerSpatializerHeadTrackerAvailableCallback";
                case 187:
                    return "setSpatializerEnabled";
                case 188:
                    return "canBeSpatialized";
                case 189:
                    return "registerSpatializerCallback";
                case 190:
                    return "unregisterSpatializerCallback";
                case 191:
                    return "registerSpatializerHeadTrackingCallback";
                case 192:
                    return "unregisterSpatializerHeadTrackingCallback";
                case 193:
                    return "registerHeadToSoundstagePoseCallback";
                case 194:
                    return "unregisterHeadToSoundstagePoseCallback";
                case 195:
                    return "getSpatializerCompatibleAudioDevices";
                case 196:
                    return "addSpatializerCompatibleAudioDevice";
                case 197:
                    return "removeSpatializerCompatibleAudioDevice";
                case 198:
                    return "setDesiredHeadTrackingMode";
                case 199:
                    return "getDesiredHeadTrackingMode";
                case 200:
                    return "getSupportedHeadTrackingModes";
                case 201:
                    return "getActualHeadTrackingMode";
                case 202:
                    return "setSpatializerGlobalTransform";
                case 203:
                    return "recenterHeadTracker";
                case 204:
                    return "setSpatializerParameter";
                case 205:
                    return "getSpatializerParameter";
                case 206:
                    return "getSpatializerOutput";
                case 207:
                    return "registerSpatializerOutputCallback";
                case 208:
                    return "unregisterSpatializerOutputCallback";
                case 209:
                    return "isVolumeFixed";
                case 210:
                    return "getDefaultVolumeInfo";
                case 211:
                    return "isPstnCallAudioInterceptable";
                case 212:
                    return "muteAwaitConnection";
                case 213:
                    return "cancelMuteAwaitConnection";
                case 214:
                    return "getMutingExpectedDevice";
                case 215:
                    return "registerMuteAwaitConnectionDispatcher";
                case 216:
                    return "setTestDeviceConnectionState";
                case 217:
                    return "registerDeviceVolumeBehaviorDispatcher";
                case 218:
                    return "getFocusStack";
                case 219:
                    return "sendFocusLoss";
                case 220:
                    return "addAssistantServicesUids";
                case 221:
                    return "removeAssistantServicesUids";
                case 222:
                    return "setActiveAssistantServiceUids";
                case 223:
                    return "getAssistantServicesUids";
                case 224:
                    return "getActiveAssistantServiceUids";
                case 225:
                    return "registerDeviceVolumeDispatcherForAbsoluteVolume";
                case 226:
                    return "getHalVersion";
                case 227:
                    return "setPreferredMixerAttributes";
                case 228:
                    return "clearPreferredMixerAttributes";
                case 229:
                    return "registerPreferredMixerAttributesDispatcher";
                case 230:
                    return "unregisterPreferredMixerAttributesDispatcher";
                case 231:
                    return "supportsBluetoothVariableLatency";
                case 232:
                    return "setBluetoothVariableLatencyEnabled";
                case 233:
                    return "isBluetoothVariableLatencyEnabled";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            PlayerBase.PlayerIdCard _arg0 = (PlayerBase.PlayerIdCard) data.readTypedObject(PlayerBase.PlayerIdCard.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = trackPlayer(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            AudioAttributes _arg1 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            playerAttributes(_arg02, _arg1);
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            playerEvent(_arg03, _arg12, _arg2);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            releasePlayer(_arg04);
                            return true;
                        case 5:
                            IBinder _arg05 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            int _result2 = trackRecorder(_arg05);
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            recorderEvent(_arg06, _arg13);
                            return true;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            releaseRecorder(_arg07);
                            return true;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            playerSessionId(_arg08, _arg14);
                            return true;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg15 = data.readInt();
                            PersistableBundle _arg22 = (PersistableBundle) data.readTypedObject(PersistableBundle.CREATOR);
                            data.enforceNoDataAvail();
                            portEvent(_arg09, _arg15, _arg22);
                            return true;
                        case 10:
                            int _arg010 = data.readInt();
                            int _arg16 = data.readInt();
                            int _arg23 = data.readInt();
                            String _arg3 = data.readString();
                            data.enforceNoDataAvail();
                            adjustStreamVolume(_arg010, _arg16, _arg23, _arg3);
                            reply.writeNoException();
                            return true;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg17 = data.readInt();
                            int _arg24 = data.readInt();
                            String _arg32 = data.readString();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            adjustStreamVolumeWithAttribution(_arg011, _arg17, _arg24, _arg32, _arg4);
                            reply.writeNoException();
                            return true;
                        case 12:
                            int _arg012 = data.readInt();
                            int _arg18 = data.readInt();
                            int _arg25 = data.readInt();
                            String _arg33 = data.readString();
                            data.enforceNoDataAvail();
                            setStreamVolume(_arg012, _arg18, _arg25, _arg33);
                            reply.writeNoException();
                            return true;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg19 = data.readInt();
                            int _arg26 = data.readInt();
                            String _arg34 = data.readString();
                            String _arg42 = data.readString();
                            data.enforceNoDataAvail();
                            setStreamVolumeWithAttribution(_arg013, _arg19, _arg26, _arg34, _arg42);
                            reply.writeNoException();
                            return true;
                        case 14:
                            VolumeInfo _arg014 = (VolumeInfo) data.readTypedObject(VolumeInfo.CREATOR);
                            AudioDeviceAttributes _arg110 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            String _arg27 = data.readString();
                            data.enforceNoDataAvail();
                            setDeviceVolume(_arg014, _arg110, _arg27);
                            reply.writeNoException();
                            return true;
                        case 15:
                            VolumeInfo _arg015 = (VolumeInfo) data.readTypedObject(VolumeInfo.CREATOR);
                            AudioDeviceAttributes _arg111 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            String _arg28 = data.readString();
                            data.enforceNoDataAvail();
                            VolumeInfo _result3 = getDeviceVolume(_arg015, _arg111, _arg28);
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            return true;
                        case 16:
                            KeyEvent _arg016 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            boolean _arg112 = data.readBoolean();
                            String _arg29 = data.readString();
                            String _arg35 = data.readString();
                            data.enforceNoDataAvail();
                            handleVolumeKey(_arg016, _arg112, _arg29, _arg35);
                            return true;
                        case 17:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result4 = isStreamMute(_arg017);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            return true;
                        case 18:
                            boolean _arg018 = data.readBoolean();
                            IBinder _arg113 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            forceRemoteSubmixFullVolume(_arg018, _arg113);
                            reply.writeNoException();
                            return true;
                        case 19:
                            boolean _result5 = isMasterMute();
                            reply.writeNoException();
                            reply.writeBoolean(_result5);
                            return true;
                        case 20:
                            boolean _arg019 = data.readBoolean();
                            int _arg114 = data.readInt();
                            String _arg210 = data.readString();
                            int _arg36 = data.readInt();
                            String _arg43 = data.readString();
                            data.enforceNoDataAvail();
                            setMasterMute(_arg019, _arg114, _arg210, _arg36, _arg43);
                            reply.writeNoException();
                            return true;
                        case 21:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result6 = getStreamVolume(_arg020);
                            reply.writeNoException();
                            reply.writeInt(_result6);
                            return true;
                        case 22:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result7 = getStreamMinVolume(_arg021);
                            reply.writeNoException();
                            reply.writeInt(_result7);
                            return true;
                        case 23:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result8 = getStreamMaxVolume(_arg022);
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            return true;
                        case 24:
                            List<android.media.audiopolicy.AudioVolumeGroup> _result9 = getAudioVolumeGroups();
                            reply.writeNoException();
                            reply.writeTypedList(_result9, 1);
                            return true;
                        case 25:
                            int _arg023 = data.readInt();
                            int _arg115 = data.readInt();
                            int _arg211 = data.readInt();
                            String _arg37 = data.readString();
                            String _arg44 = data.readString();
                            data.enforceNoDataAvail();
                            setVolumeGroupVolumeIndex(_arg023, _arg115, _arg211, _arg37, _arg44);
                            reply.writeNoException();
                            return true;
                        case 26:
                            int _arg024 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result10 = getVolumeGroupVolumeIndex(_arg024);
                            reply.writeNoException();
                            reply.writeInt(_result10);
                            return true;
                        case 27:
                            int _arg025 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result11 = getVolumeGroupMaxVolumeIndex(_arg025);
                            reply.writeNoException();
                            reply.writeInt(_result11);
                            return true;
                        case 28:
                            int _arg026 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result12 = getVolumeGroupMinVolumeIndex(_arg026);
                            reply.writeNoException();
                            reply.writeInt(_result12);
                            return true;
                        case 29:
                            int _arg027 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result13 = getLastAudibleVolumeForVolumeGroup(_arg027);
                            reply.writeNoException();
                            reply.writeInt(_result13);
                            return true;
                        case 30:
                            int _arg028 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result14 = isVolumeGroupMuted(_arg028);
                            reply.writeNoException();
                            reply.writeBoolean(_result14);
                            return true;
                        case 31:
                            int _arg029 = data.readInt();
                            int _arg116 = data.readInt();
                            int _arg212 = data.readInt();
                            String _arg38 = data.readString();
                            data.enforceNoDataAvail();
                            adjustVolumeGroupVolume(_arg029, _arg116, _arg212, _arg38);
                            reply.writeNoException();
                            return true;
                        case 32:
                            int _arg030 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result15 = getLastAudibleStreamVolume(_arg030);
                            reply.writeNoException();
                            reply.writeInt(_result15);
                            return true;
                        case 33:
                            int[] _arg031 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setSupportedSystemUsages(_arg031);
                            reply.writeNoException();
                            return true;
                        case 34:
                            int[] _result16 = getSupportedSystemUsages();
                            reply.writeNoException();
                            reply.writeIntArray(_result16);
                            return true;
                        case 35:
                            List<android.media.audiopolicy.AudioProductStrategy> _result17 = getAudioProductStrategies();
                            reply.writeNoException();
                            reply.writeTypedList(_result17, 1);
                            return true;
                        case 36:
                            boolean _result18 = isMicrophoneMuted();
                            reply.writeNoException();
                            reply.writeBoolean(_result18);
                            return true;
                        case 37:
                            boolean _result19 = isUltrasoundSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result19);
                            return true;
                        case 38:
                            boolean _arg032 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result20 = isHotwordStreamSupported(_arg032);
                            reply.writeNoException();
                            reply.writeBoolean(_result20);
                            return true;
                        case 39:
                            boolean _arg033 = data.readBoolean();
                            String _arg117 = data.readString();
                            int _arg213 = data.readInt();
                            String _arg39 = data.readString();
                            data.enforceNoDataAvail();
                            setMicrophoneMute(_arg033, _arg117, _arg213, _arg39);
                            reply.writeNoException();
                            return true;
                        case 40:
                            boolean _arg034 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMicrophoneMuteFromSwitch(_arg034);
                            return true;
                        case 41:
                            int _arg035 = data.readInt();
                            String _arg118 = data.readString();
                            data.enforceNoDataAvail();
                            setRingerModeExternal(_arg035, _arg118);
                            reply.writeNoException();
                            return true;
                        case 42:
                            int _arg036 = data.readInt();
                            String _arg119 = data.readString();
                            data.enforceNoDataAvail();
                            setRingerModeInternal(_arg036, _arg119);
                            reply.writeNoException();
                            return true;
                        case 43:
                            int _result21 = getRingerModeExternal();
                            reply.writeNoException();
                            reply.writeInt(_result21);
                            return true;
                        case 44:
                            int _result22 = getRingerModeInternal();
                            reply.writeNoException();
                            reply.writeInt(_result22);
                            return true;
                        case 45:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result23 = isValidRingerMode(_arg037);
                            reply.writeNoException();
                            reply.writeBoolean(_result23);
                            return true;
                        case 46:
                            int _arg038 = data.readInt();
                            int _arg120 = data.readInt();
                            data.enforceNoDataAvail();
                            setVibrateSetting(_arg038, _arg120);
                            reply.writeNoException();
                            return true;
                        case 47:
                            int _arg039 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result24 = getVibrateSetting(_arg039);
                            reply.writeNoException();
                            reply.writeInt(_result24);
                            return true;
                        case 48:
                            int _arg040 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result25 = shouldVibrate(_arg040);
                            reply.writeNoException();
                            reply.writeBoolean(_result25);
                            return true;
                        case 49:
                            int _arg041 = data.readInt();
                            IBinder _arg121 = data.readStrongBinder();
                            String _arg214 = data.readString();
                            data.enforceNoDataAvail();
                            setMode(_arg041, _arg121, _arg214);
                            reply.writeNoException();
                            return true;
                        case 50:
                            int _result26 = getMode();
                            reply.writeNoException();
                            reply.writeInt(_result26);
                            return true;
                        case 51:
                            int _arg042 = data.readInt();
                            int _arg122 = data.readInt();
                            data.enforceNoDataAvail();
                            playSoundEffect(_arg042, _arg122);
                            return true;
                        case 52:
                            int _arg043 = data.readInt();
                            float _arg123 = data.readFloat();
                            data.enforceNoDataAvail();
                            playSoundEffectVolume(_arg043, _arg123);
                            return true;
                        case 53:
                            boolean _result27 = loadSoundEffects();
                            reply.writeNoException();
                            reply.writeBoolean(_result27);
                            return true;
                        case 54:
                            unloadSoundEffects();
                            return true;
                        case 55:
                            reloadAudioSettings();
                            return true;
                        case 56:
                            Map _result28 = getSurroundFormats();
                            reply.writeNoException();
                            reply.writeMap(_result28);
                            return true;
                        case 57:
                            List _result29 = getReportedSurroundFormats();
                            reply.writeNoException();
                            reply.writeList(_result29);
                            return true;
                        case 58:
                            int _arg044 = data.readInt();
                            boolean _arg124 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result30 = setSurroundFormatEnabled(_arg044, _arg124);
                            reply.writeNoException();
                            reply.writeBoolean(_result30);
                            return true;
                        case 59:
                            int _arg045 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result31 = isSurroundFormatEnabled(_arg045);
                            reply.writeNoException();
                            reply.writeBoolean(_result31);
                            return true;
                        case 60:
                            int _arg046 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result32 = setEncodedSurroundMode(_arg046);
                            reply.writeNoException();
                            reply.writeBoolean(_result32);
                            return true;
                        case 61:
                            int _arg047 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result33 = getEncodedSurroundMode(_arg047);
                            reply.writeNoException();
                            reply.writeInt(_result33);
                            return true;
                        case 62:
                            IBinder _arg048 = data.readStrongBinder();
                            boolean _arg125 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSpeakerphoneOn(_arg048, _arg125);
                            reply.writeNoException();
                            return true;
                        case 63:
                            boolean _result34 = isSpeakerphoneOn();
                            reply.writeNoException();
                            reply.writeBoolean(_result34);
                            return true;
                        case 64:
                            boolean _arg049 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBluetoothScoOn(_arg049);
                            reply.writeNoException();
                            return true;
                        case 65:
                            boolean _result35 = isBluetoothScoOn();
                            reply.writeNoException();
                            reply.writeBoolean(_result35);
                            return true;
                        case 66:
                            boolean _arg050 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBluetoothA2dpOn(_arg050);
                            reply.writeNoException();
                            return true;
                        case 67:
                            boolean _result36 = isBluetoothA2dpOn();
                            reply.writeNoException();
                            reply.writeBoolean(_result36);
                            return true;
                        case 68:
                            AudioAttributes _arg051 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            int _arg126 = data.readInt();
                            IBinder _arg215 = data.readStrongBinder();
                            IAudioFocusDispatcher _arg310 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                            String _arg45 = data.readString();
                            String _arg5 = data.readString();
                            String _arg6 = data.readString();
                            int _arg7 = data.readInt();
                            IAudioPolicyCallback _arg8 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg9 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result37 = requestAudioFocus(_arg051, _arg126, _arg215, _arg310, _arg45, _arg5, _arg6, _arg7, _arg8, _arg9);
                            reply.writeNoException();
                            reply.writeInt(_result37);
                            return true;
                        case 69:
                            IAudioFocusDispatcher _arg052 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                            String _arg127 = data.readString();
                            AudioAttributes _arg216 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            String _arg311 = data.readString();
                            data.enforceNoDataAvail();
                            int _result38 = abandonAudioFocus(_arg052, _arg127, _arg216, _arg311);
                            reply.writeNoException();
                            reply.writeInt(_result38);
                            return true;
                        case 70:
                            String _arg053 = data.readString();
                            data.enforceNoDataAvail();
                            unregisterAudioFocusClient(_arg053);
                            reply.writeNoException();
                            return true;
                        case 71:
                            int _result39 = getCurrentAudioFocus();
                            reply.writeNoException();
                            reply.writeInt(_result39);
                            return true;
                        case 72:
                            IBinder _arg054 = data.readStrongBinder();
                            int _arg128 = data.readInt();
                            data.enforceNoDataAvail();
                            startBluetoothSco(_arg054, _arg128);
                            reply.writeNoException();
                            return true;
                        case 73:
                            IBinder _arg055 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            startBluetoothScoVirtualCall(_arg055);
                            reply.writeNoException();
                            return true;
                        case 74:
                            IBinder _arg056 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            stopBluetoothSco(_arg056);
                            reply.writeNoException();
                            return true;
                        case 75:
                            int _arg057 = data.readInt();
                            IBinder _arg129 = data.readStrongBinder();
                            data.enforceNoDataAvail();
                            forceVolumeControlStream(_arg057, _arg129);
                            reply.writeNoException();
                            return true;
                        case 76:
                            IRingtonePlayer _arg058 = IRingtonePlayer.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setRingtonePlayer(_arg058);
                            reply.writeNoException();
                            return true;
                        case 77:
                            IRingtonePlayer _result40 = getRingtonePlayer();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result40);
                            return true;
                        case 78:
                            int _result41 = getUiSoundsStreamType();
                            reply.writeNoException();
                            reply.writeInt(_result41);
                            return true;
                        case 79:
                            List _result42 = getIndependentStreamTypes();
                            reply.writeNoException();
                            reply.writeList(_result42);
                            return true;
                        case 80:
                            int _arg059 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result43 = getStreamTypeAlias(_arg059);
                            reply.writeNoException();
                            reply.writeInt(_result43);
                            return true;
                        case 81:
                            boolean _result44 = isVolumeControlUsingVolumeGroups();
                            reply.writeNoException();
                            reply.writeBoolean(_result44);
                            return true;
                        case 82:
                            IStreamAliasingDispatcher _arg060 = IStreamAliasingDispatcher.Stub.asInterface(data.readStrongBinder());
                            boolean _arg130 = data.readBoolean();
                            data.enforceNoDataAvail();
                            registerStreamAliasingDispatcher(_arg060, _arg130);
                            return true;
                        case 83:
                            boolean _arg061 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setNotifAliasRingForTest(_arg061);
                            reply.writeNoException();
                            return true;
                        case 84:
                            AudioDeviceAttributes _arg062 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            int _arg131 = data.readInt();
                            String _arg217 = data.readString();
                            data.enforceNoDataAvail();
                            setWiredDeviceConnectionState(_arg062, _arg131, _arg217);
                            reply.writeNoException();
                            return true;
                        case 85:
                            IAudioRoutesObserver _arg063 = IAudioRoutesObserver.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            AudioRoutesInfo _result45 = startWatchingRoutes(_arg063);
                            reply.writeNoException();
                            reply.writeTypedObject(_result45, 1);
                            return true;
                        case 86:
                            boolean _result46 = isCameraSoundForced();
                            reply.writeNoException();
                            reply.writeBoolean(_result46);
                            return true;
                        case 87:
                            IVolumeController _arg064 = IVolumeController.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setVolumeController(_arg064);
                            reply.writeNoException();
                            return true;
                        case 88:
                            IVolumeController _result47 = getVolumeController();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result47);
                            return true;
                        case 89:
                            IVolumeController _arg065 = IVolumeController.Stub.asInterface(data.readStrongBinder());
                            boolean _arg132 = data.readBoolean();
                            data.enforceNoDataAvail();
                            notifyVolumeControllerVisible(_arg065, _arg132);
                            reply.writeNoException();
                            return true;
                        case 90:
                            int _arg066 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result48 = isStreamAffectedByRingerMode(_arg066);
                            reply.writeNoException();
                            reply.writeBoolean(_result48);
                            return true;
                        case 91:
                            int _arg067 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result49 = isStreamAffectedByMute(_arg067);
                            reply.writeNoException();
                            reply.writeBoolean(_result49);
                            return true;
                        case 92:
                            String _arg068 = data.readString();
                            data.enforceNoDataAvail();
                            disableSafeMediaVolume(_arg068);
                            reply.writeNoException();
                            return true;
                        case 93:
                            String _arg069 = data.readString();
                            data.enforceNoDataAvail();
                            lowerVolumeToRs1(_arg069);
                            reply.writeNoException();
                            return true;
                        case 94:
                            float _result50 = getRs2Value();
                            reply.writeNoException();
                            reply.writeFloat(_result50);
                            return true;
                        case 95:
                            float _arg070 = data.readFloat();
                            data.enforceNoDataAvail();
                            setRs2Value(_arg070);
                            return true;
                        case 96:
                            float _result51 = getCsd();
                            reply.writeNoException();
                            reply.writeFloat(_result51);
                            return true;
                        case 97:
                            float _arg071 = data.readFloat();
                            data.enforceNoDataAvail();
                            setCsd(_arg071);
                            return true;
                        case 98:
                            boolean _arg072 = data.readBoolean();
                            data.enforceNoDataAvail();
                            forceUseFrameworkMel(_arg072);
                            return true;
                        case 99:
                            boolean _arg073 = data.readBoolean();
                            data.enforceNoDataAvail();
                            forceComputeCsdOnAllDevices(_arg073);
                            return true;
                        case 100:
                            boolean _result52 = isCsdEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result52);
                            return true;
                        case 101:
                            boolean _arg074 = data.readBoolean();
                            data.enforceNoDataAvail();
                            int _result53 = setHdmiSystemAudioSupported(_arg074);
                            reply.writeNoException();
                            reply.writeInt(_result53);
                            return true;
                        case 102:
                            boolean _result54 = isHdmiSystemAudioSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result54);
                            return true;
                        case 103:
                            AudioPolicyConfig _arg075 = (AudioPolicyConfig) data.readTypedObject(AudioPolicyConfig.CREATOR);
                            IAudioPolicyCallback _arg133 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg218 = data.readBoolean();
                            boolean _arg312 = data.readBoolean();
                            boolean _arg46 = data.readBoolean();
                            boolean _arg52 = data.readBoolean();
                            IMediaProjection _arg62 = IMediaProjection.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            String _result55 = registerAudioPolicy(_arg075, _arg133, _arg218, _arg312, _arg46, _arg52, _arg62);
                            reply.writeNoException();
                            reply.writeString(_result55);
                            return true;
                        case 104:
                            IAudioPolicyCallback _arg076 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterAudioPolicyAsync(_arg076);
                            return true;
                        case 105:
                            IAudioPolicyCallback _arg077 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterAudioPolicy(_arg077);
                            reply.writeNoException();
                            return true;
                        case 106:
                            AudioPolicyConfig _arg078 = (AudioPolicyConfig) data.readTypedObject(AudioPolicyConfig.CREATOR);
                            IAudioPolicyCallback _arg134 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result56 = addMixForPolicy(_arg078, _arg134);
                            reply.writeNoException();
                            reply.writeInt(_result56);
                            return true;
                        case 107:
                            AudioPolicyConfig _arg079 = (AudioPolicyConfig) data.readTypedObject(AudioPolicyConfig.CREATOR);
                            IAudioPolicyCallback _arg135 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result57 = removeMixForPolicy(_arg079, _arg135);
                            reply.writeNoException();
                            reply.writeInt(_result57);
                            return true;
                        case 108:
                            int _arg080 = data.readInt();
                            IAudioPolicyCallback _arg136 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result58 = setFocusPropertiesForPolicy(_arg080, _arg136);
                            reply.writeNoException();
                            reply.writeInt(_result58);
                            return true;
                        case 109:
                            VolumePolicy _arg081 = (VolumePolicy) data.readTypedObject(VolumePolicy.CREATOR);
                            data.enforceNoDataAvail();
                            setVolumePolicy(_arg081);
                            reply.writeNoException();
                            return true;
                        case 110:
                            boolean _result59 = hasRegisteredDynamicPolicy();
                            reply.writeNoException();
                            reply.writeBoolean(_result59);
                            return true;
                        case 111:
                            IRecordingConfigDispatcher _arg082 = IRecordingConfigDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerRecordingCallback(_arg082);
                            reply.writeNoException();
                            return true;
                        case 112:
                            IRecordingConfigDispatcher _arg083 = IRecordingConfigDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterRecordingCallback(_arg083);
                            return true;
                        case 113:
                            List<AudioRecordingConfiguration> _result60 = getActiveRecordingConfigurations();
                            reply.writeNoException();
                            reply.writeTypedList(_result60, 1);
                            return true;
                        case 114:
                            IPlaybackConfigDispatcher _arg084 = IPlaybackConfigDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerPlaybackCallback(_arg084);
                            reply.writeNoException();
                            return true;
                        case 115:
                            IPlaybackConfigDispatcher _arg085 = IPlaybackConfigDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterPlaybackCallback(_arg085);
                            return true;
                        case 116:
                            List<AudioPlaybackConfiguration> _result61 = getActivePlaybackConfigurations();
                            reply.writeNoException();
                            reply.writeTypedList(_result61, 1);
                            return true;
                        case 117:
                            int _arg086 = data.readInt();
                            AudioAttributes _arg137 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result62 = getFocusRampTimeMs(_arg086, _arg137);
                            reply.writeNoException();
                            reply.writeInt(_result62);
                            return true;
                        case 118:
                            AudioFocusInfo _arg087 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            int _arg138 = data.readInt();
                            IAudioPolicyCallback _arg219 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            int _result63 = dispatchFocusChange(_arg087, _arg138, _arg219);
                            reply.writeNoException();
                            reply.writeInt(_result63);
                            return true;
                        case 119:
                            int _arg088 = data.readInt();
                            boolean _arg139 = data.readBoolean();
                            data.enforceNoDataAvail();
                            playerHasOpPlayAudio(_arg088, _arg139);
                            return true;
                        case 120:
                            BluetoothDevice _arg089 = (BluetoothDevice) data.readTypedObject(BluetoothDevice.CREATOR);
                            BluetoothDevice _arg140 = (BluetoothDevice) data.readTypedObject(BluetoothDevice.CREATOR);
                            BluetoothProfileConnectionInfo _arg220 = (BluetoothProfileConnectionInfo) data.readTypedObject(BluetoothProfileConnectionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            handleBluetoothActiveDeviceChanged(_arg089, _arg140, _arg220);
                            reply.writeNoException();
                            return true;
                        case 121:
                            AudioFocusInfo _arg090 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            int _arg141 = data.readInt();
                            IAudioPolicyCallback _arg221 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setFocusRequestResultFromExtPolicy(_arg090, _arg141, _arg221);
                            return true;
                        case 122:
                            IAudioServerStateDispatcher _arg091 = IAudioServerStateDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerAudioServerStateDispatcher(_arg091);
                            reply.writeNoException();
                            return true;
                        case 123:
                            IAudioServerStateDispatcher _arg092 = IAudioServerStateDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterAudioServerStateDispatcher(_arg092);
                            return true;
                        case 124:
                            boolean _result64 = isAudioServerRunning();
                            reply.writeNoException();
                            reply.writeBoolean(_result64);
                            return true;
                        case 125:
                            IAudioPolicyCallback _arg093 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg142 = data.readInt();
                            int[] _arg222 = data.createIntArray();
                            String[] _arg313 = data.createStringArray();
                            data.enforceNoDataAvail();
                            int _result65 = setUidDeviceAffinity(_arg093, _arg142, _arg222, _arg313);
                            reply.writeNoException();
                            reply.writeInt(_result65);
                            return true;
                        case 126:
                            IAudioPolicyCallback _arg094 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg143 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result66 = removeUidDeviceAffinity(_arg094, _arg143);
                            reply.writeNoException();
                            reply.writeInt(_result66);
                            return true;
                        case 127:
                            IAudioPolicyCallback _arg095 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg144 = data.readInt();
                            int[] _arg223 = data.createIntArray();
                            String[] _arg314 = data.createStringArray();
                            data.enforceNoDataAvail();
                            int _result67 = setUserIdDeviceAffinity(_arg095, _arg144, _arg223, _arg314);
                            reply.writeNoException();
                            reply.writeInt(_result67);
                            return true;
                        case 128:
                            IAudioPolicyCallback _arg096 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg145 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result68 = removeUserIdDeviceAffinity(_arg096, _arg145);
                            reply.writeNoException();
                            reply.writeInt(_result68);
                            return true;
                        case 129:
                            Uri _arg097 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result69 = hasHapticChannels(_arg097);
                            reply.writeNoException();
                            reply.writeBoolean(_result69);
                            return true;
                        case 130:
                            boolean _result70 = isCallScreeningModeSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result70);
                            return true;
                        case 131:
                            int _arg098 = data.readInt();
                            List<AudioDeviceAttributes> _arg146 = data.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result71 = setPreferredDevicesForStrategy(_arg098, _arg146);
                            reply.writeNoException();
                            reply.writeInt(_result71);
                            return true;
                        case 132:
                            int _arg099 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result72 = removePreferredDevicesForStrategy(_arg099);
                            reply.writeNoException();
                            reply.writeInt(_result72);
                            return true;
                        case 133:
                            int _arg0100 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AudioDeviceAttributes> _result73 = getPreferredDevicesForStrategy(_arg0100);
                            reply.writeNoException();
                            reply.writeTypedList(_result73, 1);
                            return true;
                        case 134:
                            int _arg0101 = data.readInt();
                            AudioDeviceAttributes _arg147 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result74 = setDeviceAsNonDefaultForStrategy(_arg0101, _arg147);
                            reply.writeNoException();
                            reply.writeInt(_result74);
                            return true;
                        case 135:
                            int _arg0102 = data.readInt();
                            AudioDeviceAttributes _arg148 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result75 = removeDeviceAsNonDefaultForStrategy(_arg0102, _arg148);
                            reply.writeNoException();
                            reply.writeInt(_result75);
                            return true;
                        case 136:
                            int _arg0103 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AudioDeviceAttributes> _result76 = getNonDefaultDevicesForStrategy(_arg0103);
                            reply.writeNoException();
                            reply.writeTypedList(_result76, 1);
                            return true;
                        case 137:
                            AudioAttributes _arg0104 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            List<AudioDeviceAttributes> _result77 = getDevicesForAttributes(_arg0104);
                            reply.writeNoException();
                            reply.writeTypedList(_result77, 1);
                            return true;
                        case 138:
                            AudioAttributes _arg0105 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            List<AudioDeviceAttributes> _result78 = getDevicesForAttributesUnprotected(_arg0105);
                            reply.writeNoException();
                            reply.writeTypedList(_result78, 1);
                            return true;
                        case 139:
                            AudioAttributes _arg0106 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            IDevicesForAttributesCallback _arg149 = IDevicesForAttributesCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addOnDevicesForAttributesChangedListener(_arg0106, _arg149);
                            reply.writeNoException();
                            return true;
                        case 140:
                            IDevicesForAttributesCallback _arg0107 = IDevicesForAttributesCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeOnDevicesForAttributesChangedListener(_arg0107);
                            return true;
                        case 141:
                            int _arg0108 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result79 = setAllowedCapturePolicy(_arg0108);
                            reply.writeNoException();
                            reply.writeInt(_result79);
                            return true;
                        case 142:
                            int _result80 = getAllowedCapturePolicy();
                            reply.writeNoException();
                            reply.writeInt(_result80);
                            return true;
                        case 143:
                            IStrategyPreferredDevicesDispatcher _arg0109 = IStrategyPreferredDevicesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerStrategyPreferredDevicesDispatcher(_arg0109);
                            reply.writeNoException();
                            return true;
                        case 144:
                            IStrategyPreferredDevicesDispatcher _arg0110 = IStrategyPreferredDevicesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterStrategyPreferredDevicesDispatcher(_arg0110);
                            return true;
                        case 145:
                            IStrategyNonDefaultDevicesDispatcher _arg0111 = IStrategyNonDefaultDevicesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerStrategyNonDefaultDevicesDispatcher(_arg0111);
                            reply.writeNoException();
                            return true;
                        case 146:
                            IStrategyNonDefaultDevicesDispatcher _arg0112 = IStrategyNonDefaultDevicesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterStrategyNonDefaultDevicesDispatcher(_arg0112);
                            return true;
                        case 147:
                            boolean _arg0113 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setRttEnabled(_arg0113);
                            return true;
                        case 148:
                            AudioDeviceAttributes _arg0114 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            int _arg150 = data.readInt();
                            String _arg224 = data.readString();
                            data.enforceNoDataAvail();
                            setDeviceVolumeBehavior(_arg0114, _arg150, _arg224);
                            reply.writeNoException();
                            return true;
                        case 149:
                            AudioDeviceAttributes _arg0115 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result81 = getDeviceVolumeBehavior(_arg0115);
                            reply.writeNoException();
                            reply.writeInt(_result81);
                            return true;
                        case 150:
                            boolean _arg0116 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMultiAudioFocusEnabled(_arg0116);
                            return true;
                        case 151:
                            int _arg0117 = data.readInt();
                            List<AudioDeviceAttributes> _arg151 = data.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result82 = setPreferredDevicesForCapturePreset(_arg0117, _arg151);
                            reply.writeNoException();
                            reply.writeInt(_result82);
                            return true;
                        case 152:
                            int _arg0118 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result83 = clearPreferredDevicesForCapturePreset(_arg0118);
                            reply.writeNoException();
                            reply.writeInt(_result83);
                            return true;
                        case 153:
                            int _arg0119 = data.readInt();
                            data.enforceNoDataAvail();
                            List<AudioDeviceAttributes> _result84 = getPreferredDevicesForCapturePreset(_arg0119);
                            reply.writeNoException();
                            reply.writeTypedList(_result84, 1);
                            return true;
                        case 154:
                            ICapturePresetDevicesRoleDispatcher _arg0120 = ICapturePresetDevicesRoleDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCapturePresetDevicesRoleDispatcher(_arg0120);
                            reply.writeNoException();
                            return true;
                        case 155:
                            ICapturePresetDevicesRoleDispatcher _arg0121 = ICapturePresetDevicesRoleDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCapturePresetDevicesRoleDispatcher(_arg0121);
                            return true;
                        case 156:
                            int _arg0122 = data.readInt();
                            int _arg152 = data.readInt();
                            int _arg225 = data.readInt();
                            String _arg315 = data.readString();
                            int _arg47 = data.readInt();
                            int _arg53 = data.readInt();
                            UserHandle _arg63 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg72 = data.readInt();
                            data.enforceNoDataAvail();
                            adjustStreamVolumeForUid(_arg0122, _arg152, _arg225, _arg315, _arg47, _arg53, _arg63, _arg72);
                            return true;
                        case 157:
                            int _arg0123 = data.readInt();
                            int _arg153 = data.readInt();
                            int _arg226 = data.readInt();
                            String _arg316 = data.readString();
                            int _arg48 = data.readInt();
                            int _arg54 = data.readInt();
                            UserHandle _arg64 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg73 = data.readInt();
                            data.enforceNoDataAvail();
                            adjustSuggestedStreamVolumeForUid(_arg0123, _arg153, _arg226, _arg316, _arg48, _arg54, _arg64, _arg73);
                            return true;
                        case 158:
                            int _arg0124 = data.readInt();
                            int _arg154 = data.readInt();
                            int _arg227 = data.readInt();
                            String _arg317 = data.readString();
                            int _arg49 = data.readInt();
                            int _arg55 = data.readInt();
                            UserHandle _arg65 = (UserHandle) data.readTypedObject(UserHandle.CREATOR);
                            int _arg74 = data.readInt();
                            data.enforceNoDataAvail();
                            setStreamVolumeForUid(_arg0124, _arg154, _arg227, _arg317, _arg49, _arg55, _arg65, _arg74);
                            return true;
                        case 159:
                            boolean _arg0125 = data.readBoolean();
                            data.enforceNoDataAvail();
                            boolean _result85 = isMusicActive(_arg0125);
                            reply.writeNoException();
                            reply.writeBoolean(_result85);
                            return true;
                        case 160:
                            int _arg0126 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result86 = getDeviceMaskForStream(_arg0126);
                            reply.writeNoException();
                            reply.writeInt(_result86);
                            return true;
                        case 161:
                            int[] _result87 = getAvailableCommunicationDeviceIds();
                            reply.writeNoException();
                            reply.writeIntArray(_result87);
                            return true;
                        case 162:
                            IBinder _arg0127 = data.readStrongBinder();
                            int _arg155 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result88 = setCommunicationDevice(_arg0127, _arg155);
                            reply.writeNoException();
                            reply.writeBoolean(_result88);
                            return true;
                        case 163:
                            int _result89 = getCommunicationDevice();
                            reply.writeNoException();
                            reply.writeInt(_result89);
                            return true;
                        case 164:
                            ICommunicationDeviceDispatcher _arg0128 = ICommunicationDeviceDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCommunicationDeviceDispatcher(_arg0128);
                            reply.writeNoException();
                            return true;
                        case 165:
                            ICommunicationDeviceDispatcher _arg0129 = ICommunicationDeviceDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCommunicationDeviceDispatcher(_arg0129);
                            return true;
                        case 166:
                            boolean _result90 = areNavigationRepeatSoundEffectsEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result90);
                            return true;
                        case 167:
                            boolean _arg0130 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setNavigationRepeatSoundEffectsEnabled(_arg0130);
                            return true;
                        case 168:
                            boolean _result91 = isHomeSoundEffectEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result91);
                            return true;
                        case 169:
                            boolean _arg0131 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setHomeSoundEffectEnabled(_arg0131);
                            return true;
                        case 170:
                            AudioDeviceAttributes _arg0132 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            long _arg156 = data.readLong();
                            data.enforceNoDataAvail();
                            boolean _result92 = setAdditionalOutputDeviceDelay(_arg0132, _arg156);
                            reply.writeNoException();
                            reply.writeBoolean(_result92);
                            return true;
                        case 171:
                            AudioDeviceAttributes _arg0133 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            long _result93 = getAdditionalOutputDeviceDelay(_arg0133);
                            reply.writeNoException();
                            reply.writeLong(_result93);
                            return true;
                        case 172:
                            AudioDeviceAttributes _arg0134 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            long _result94 = getMaxAdditionalOutputDeviceDelay(_arg0134);
                            reply.writeNoException();
                            reply.writeLong(_result94);
                            return true;
                        case 173:
                            AudioAttributes _arg0135 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            int _arg157 = data.readInt();
                            IBinder _arg228 = data.readStrongBinder();
                            IAudioFocusDispatcher _arg318 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                            String _arg410 = data.readString();
                            String _arg56 = data.readString();
                            int _arg66 = data.readInt();
                            int _arg75 = data.readInt();
                            int _arg82 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result95 = requestAudioFocusForTest(_arg0135, _arg157, _arg228, _arg318, _arg410, _arg56, _arg66, _arg75, _arg82);
                            reply.writeNoException();
                            reply.writeInt(_result95);
                            return true;
                        case 174:
                            IAudioFocusDispatcher _arg0136 = IAudioFocusDispatcher.Stub.asInterface(data.readStrongBinder());
                            String _arg158 = data.readString();
                            AudioAttributes _arg229 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            String _arg319 = data.readString();
                            data.enforceNoDataAvail();
                            int _result96 = abandonAudioFocusForTest(_arg0136, _arg158, _arg229, _arg319);
                            reply.writeNoException();
                            reply.writeInt(_result96);
                            return true;
                        case 175:
                            AudioAttributes _arg0137 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            long _result97 = getFadeOutDurationOnFocusLossMillis(_arg0137);
                            reply.writeNoException();
                            reply.writeLong(_result97);
                            return true;
                        case 176:
                            IAudioModeDispatcher _arg0138 = IAudioModeDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerModeDispatcher(_arg0138);
                            reply.writeNoException();
                            return true;
                        case 177:
                            IAudioModeDispatcher _arg0139 = IAudioModeDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterModeDispatcher(_arg0139);
                            return true;
                        case 178:
                            int _result98 = getSpatializerImmersiveAudioLevel();
                            reply.writeNoException();
                            reply.writeInt(_result98);
                            return true;
                        case 179:
                            boolean _result99 = isSpatializerEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result99);
                            return true;
                        case 180:
                            boolean _result100 = isSpatializerAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result100);
                            return true;
                        case 181:
                            AudioDeviceAttributes _arg0140 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result101 = isSpatializerAvailableForDevice(_arg0140);
                            reply.writeNoException();
                            reply.writeBoolean(_result101);
                            return true;
                        case 182:
                            AudioDeviceAttributes _arg0141 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result102 = hasHeadTracker(_arg0141);
                            reply.writeNoException();
                            reply.writeBoolean(_result102);
                            return true;
                        case 183:
                            boolean _arg0142 = data.readBoolean();
                            AudioDeviceAttributes _arg159 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            setHeadTrackerEnabled(_arg0142, _arg159);
                            reply.writeNoException();
                            return true;
                        case 184:
                            AudioDeviceAttributes _arg0143 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result103 = isHeadTrackerEnabled(_arg0143);
                            reply.writeNoException();
                            reply.writeBoolean(_result103);
                            return true;
                        case 185:
                            boolean _result104 = isHeadTrackerAvailable();
                            reply.writeNoException();
                            reply.writeBoolean(_result104);
                            return true;
                        case 186:
                            ISpatializerHeadTrackerAvailableCallback _arg0144 = ISpatializerHeadTrackerAvailableCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg160 = data.readBoolean();
                            data.enforceNoDataAvail();
                            registerSpatializerHeadTrackerAvailableCallback(_arg0144, _arg160);
                            reply.writeNoException();
                            return true;
                        case 187:
                            boolean _arg0145 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setSpatializerEnabled(_arg0145);
                            reply.writeNoException();
                            return true;
                        case 188:
                            AudioAttributes _arg0146 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            AudioFormat _arg161 = (AudioFormat) data.readTypedObject(AudioFormat.CREATOR);
                            data.enforceNoDataAvail();
                            boolean _result105 = canBeSpatialized(_arg0146, _arg161);
                            reply.writeNoException();
                            reply.writeBoolean(_result105);
                            return true;
                        case 189:
                            ISpatializerCallback _arg0147 = ISpatializerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSpatializerCallback(_arg0147);
                            reply.writeNoException();
                            return true;
                        case 190:
                            ISpatializerCallback _arg0148 = ISpatializerCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSpatializerCallback(_arg0148);
                            reply.writeNoException();
                            return true;
                        case 191:
                            ISpatializerHeadTrackingModeCallback _arg0149 = ISpatializerHeadTrackingModeCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSpatializerHeadTrackingCallback(_arg0149);
                            reply.writeNoException();
                            return true;
                        case 192:
                            ISpatializerHeadTrackingModeCallback _arg0150 = ISpatializerHeadTrackingModeCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSpatializerHeadTrackingCallback(_arg0150);
                            reply.writeNoException();
                            return true;
                        case 193:
                            ISpatializerHeadToSoundStagePoseCallback _arg0151 = ISpatializerHeadToSoundStagePoseCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerHeadToSoundstagePoseCallback(_arg0151);
                            reply.writeNoException();
                            return true;
                        case 194:
                            ISpatializerHeadToSoundStagePoseCallback _arg0152 = ISpatializerHeadToSoundStagePoseCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterHeadToSoundstagePoseCallback(_arg0152);
                            reply.writeNoException();
                            return true;
                        case 195:
                            List<AudioDeviceAttributes> _result106 = getSpatializerCompatibleAudioDevices();
                            reply.writeNoException();
                            reply.writeTypedList(_result106, 1);
                            return true;
                        case 196:
                            AudioDeviceAttributes _arg0153 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            addSpatializerCompatibleAudioDevice(_arg0153);
                            reply.writeNoException();
                            return true;
                        case 197:
                            AudioDeviceAttributes _arg0154 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            removeSpatializerCompatibleAudioDevice(_arg0154);
                            reply.writeNoException();
                            return true;
                        case 198:
                            int _arg0155 = data.readInt();
                            data.enforceNoDataAvail();
                            setDesiredHeadTrackingMode(_arg0155);
                            reply.writeNoException();
                            return true;
                        case 199:
                            int _result107 = getDesiredHeadTrackingMode();
                            reply.writeNoException();
                            reply.writeInt(_result107);
                            return true;
                        case 200:
                            int[] _result108 = getSupportedHeadTrackingModes();
                            reply.writeNoException();
                            reply.writeIntArray(_result108);
                            return true;
                        case 201:
                            int _result109 = getActualHeadTrackingMode();
                            reply.writeNoException();
                            reply.writeInt(_result109);
                            return true;
                        case 202:
                            float[] _arg0156 = data.createFloatArray();
                            data.enforceNoDataAvail();
                            setSpatializerGlobalTransform(_arg0156);
                            return true;
                        case 203:
                            recenterHeadTracker();
                            return true;
                        case 204:
                            int _arg0157 = data.readInt();
                            byte[] _arg162 = data.createByteArray();
                            data.enforceNoDataAvail();
                            setSpatializerParameter(_arg0157, _arg162);
                            reply.writeNoException();
                            return true;
                        case 205:
                            int _arg0158 = data.readInt();
                            byte[] _arg163 = data.createByteArray();
                            data.enforceNoDataAvail();
                            getSpatializerParameter(_arg0158, _arg163);
                            reply.writeNoException();
                            reply.writeByteArray(_arg163);
                            return true;
                        case 206:
                            int _result110 = getSpatializerOutput();
                            reply.writeNoException();
                            reply.writeInt(_result110);
                            return true;
                        case 207:
                            ISpatializerOutputCallback _arg0159 = ISpatializerOutputCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerSpatializerOutputCallback(_arg0159);
                            reply.writeNoException();
                            return true;
                        case 208:
                            ISpatializerOutputCallback _arg0160 = ISpatializerOutputCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterSpatializerOutputCallback(_arg0160);
                            reply.writeNoException();
                            return true;
                        case 209:
                            boolean _result111 = isVolumeFixed();
                            reply.writeNoException();
                            reply.writeBoolean(_result111);
                            return true;
                        case 210:
                            VolumeInfo _result112 = getDefaultVolumeInfo();
                            reply.writeNoException();
                            reply.writeTypedObject(_result112, 1);
                            return true;
                        case 211:
                            boolean _result113 = isPstnCallAudioInterceptable();
                            reply.writeNoException();
                            reply.writeBoolean(_result113);
                            return true;
                        case 212:
                            int[] _arg0161 = data.createIntArray();
                            AudioDeviceAttributes _arg164 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            long _arg230 = data.readLong();
                            data.enforceNoDataAvail();
                            muteAwaitConnection(_arg0161, _arg164, _arg230);
                            return true;
                        case 213:
                            AudioDeviceAttributes _arg0162 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            cancelMuteAwaitConnection(_arg0162);
                            return true;
                        case 214:
                            AudioDeviceAttributes _result114 = getMutingExpectedDevice();
                            reply.writeNoException();
                            reply.writeTypedObject(_result114, 1);
                            return true;
                        case 215:
                            IMuteAwaitConnectionCallback _arg0163 = IMuteAwaitConnectionCallback.Stub.asInterface(data.readStrongBinder());
                            boolean _arg165 = data.readBoolean();
                            data.enforceNoDataAvail();
                            registerMuteAwaitConnectionDispatcher(_arg0163, _arg165);
                            reply.writeNoException();
                            return true;
                        case 216:
                            AudioDeviceAttributes _arg0164 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            boolean _arg166 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setTestDeviceConnectionState(_arg0164, _arg166);
                            reply.writeNoException();
                            return true;
                        case 217:
                            boolean _arg0165 = data.readBoolean();
                            IDeviceVolumeBehaviorDispatcher _arg167 = IDeviceVolumeBehaviorDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerDeviceVolumeBehaviorDispatcher(_arg0165, _arg167);
                            reply.writeNoException();
                            return true;
                        case 218:
                            List<AudioFocusInfo> _result115 = getFocusStack();
                            reply.writeNoException();
                            reply.writeTypedList(_result115, 1);
                            return true;
                        case 219:
                            AudioFocusInfo _arg0166 = (AudioFocusInfo) data.readTypedObject(AudioFocusInfo.CREATOR);
                            IAudioPolicyCallback _arg168 = IAudioPolicyCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result116 = sendFocusLoss(_arg0166, _arg168);
                            reply.writeNoException();
                            reply.writeBoolean(_result116);
                            return true;
                        case 220:
                            int[] _arg0167 = data.createIntArray();
                            data.enforceNoDataAvail();
                            addAssistantServicesUids(_arg0167);
                            reply.writeNoException();
                            return true;
                        case 221:
                            int[] _arg0168 = data.createIntArray();
                            data.enforceNoDataAvail();
                            removeAssistantServicesUids(_arg0168);
                            reply.writeNoException();
                            return true;
                        case 222:
                            int[] _arg0169 = data.createIntArray();
                            data.enforceNoDataAvail();
                            setActiveAssistantServiceUids(_arg0169);
                            reply.writeNoException();
                            return true;
                        case 223:
                            int[] _result117 = getAssistantServicesUids();
                            reply.writeNoException();
                            reply.writeIntArray(_result117);
                            return true;
                        case 224:
                            int[] _result118 = getActiveAssistantServiceUids();
                            reply.writeNoException();
                            reply.writeIntArray(_result118);
                            return true;
                        case 225:
                            boolean _arg0170 = data.readBoolean();
                            IAudioDeviceVolumeDispatcher _arg169 = IAudioDeviceVolumeDispatcher.Stub.asInterface(data.readStrongBinder());
                            String _arg231 = data.readString();
                            AudioDeviceAttributes _arg320 = (AudioDeviceAttributes) data.readTypedObject(AudioDeviceAttributes.CREATOR);
                            List<VolumeInfo> _arg411 = data.createTypedArrayList(VolumeInfo.CREATOR);
                            boolean _arg57 = data.readBoolean();
                            int _arg67 = data.readInt();
                            data.enforceNoDataAvail();
                            registerDeviceVolumeDispatcherForAbsoluteVolume(_arg0170, _arg169, _arg231, _arg320, _arg411, _arg57, _arg67);
                            reply.writeNoException();
                            return true;
                        case 226:
                            AudioHalVersionInfo _result119 = getHalVersion();
                            reply.writeNoException();
                            reply.writeTypedObject(_result119, 1);
                            return true;
                        case 227:
                            AudioAttributes _arg0171 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            int _arg170 = data.readInt();
                            AudioMixerAttributes _arg232 = (AudioMixerAttributes) data.readTypedObject(AudioMixerAttributes.CREATOR);
                            data.enforceNoDataAvail();
                            int _result120 = setPreferredMixerAttributes(_arg0171, _arg170, _arg232);
                            reply.writeNoException();
                            reply.writeInt(_result120);
                            return true;
                        case 228:
                            AudioAttributes _arg0172 = (AudioAttributes) data.readTypedObject(AudioAttributes.CREATOR);
                            int _arg171 = data.readInt();
                            data.enforceNoDataAvail();
                            int _result121 = clearPreferredMixerAttributes(_arg0172, _arg171);
                            reply.writeNoException();
                            reply.writeInt(_result121);
                            return true;
                        case 229:
                            IPreferredMixerAttributesDispatcher _arg0173 = IPreferredMixerAttributesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerPreferredMixerAttributesDispatcher(_arg0173);
                            reply.writeNoException();
                            return true;
                        case 230:
                            IPreferredMixerAttributesDispatcher _arg0174 = IPreferredMixerAttributesDispatcher.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterPreferredMixerAttributesDispatcher(_arg0174);
                            return true;
                        case 231:
                            boolean _result122 = supportsBluetoothVariableLatency();
                            reply.writeNoException();
                            reply.writeBoolean(_result122);
                            return true;
                        case 232:
                            boolean _arg0175 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setBluetoothVariableLatencyEnabled(_arg0175);
                            reply.writeNoException();
                            return true;
                        case 233:
                            boolean _result123 = isBluetoothVariableLatencyEnabled();
                            reply.writeNoException();
                            reply.writeBoolean(_result123);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IAudioService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.media.IAudioService
            public int trackPlayer(PlayerBase.PlayerIdCard pic) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(pic, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playerAttributes(int piid, AudioAttributes attr) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(piid);
                    _data.writeTypedObject(attr, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playerEvent(int piid, int event, int eventId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(piid);
                    _data.writeInt(event);
                    _data.writeInt(eventId);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void releasePlayer(int piid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(piid);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int trackRecorder(IBinder recorder) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(recorder);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void recorderEvent(int riid, int event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(riid);
                    _data.writeInt(event);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void releaseRecorder(int riid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(riid);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playerSessionId(int piid, int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(piid);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void portEvent(int portId, int event, PersistableBundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(portId);
                    _data.writeInt(event);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustStreamVolume(int streamType, int direction, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustStreamVolumeWithAttribution(int streamType, int direction, int flags, String callingPackage, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamVolume(int streamType, int index, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(index);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamVolumeWithAttribution(int streamType, int index, int flags, String callingPackage, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(index);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(vi, 0);
                    _data.writeTypedObject(ada, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public VolumeInfo getDeviceVolume(VolumeInfo vi, AudioDeviceAttributes ada, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(vi, 0);
                    _data.writeTypedObject(ada, 0);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                    VolumeInfo _result = (VolumeInfo) _reply.readTypedObject(VolumeInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void handleVolumeKey(KeyEvent event, boolean isOnTv, String callingPackage, String caller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    _data.writeBoolean(isOnTv);
                    _data.writeString(callingPackage);
                    _data.writeString(caller);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isStreamMute(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void forceRemoteSubmixFullVolume(boolean startForcing, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(startForcing);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(18, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isMasterMute() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(19, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMasterMute(boolean mute, int flags, String callingPackage, int userId, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(mute);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(20, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(21, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamMinVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(22, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamMaxVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(23, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<android.media.audiopolicy.AudioVolumeGroup> getAudioVolumeGroups() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, _reply, 0);
                    _reply.readException();
                    List<android.media.audiopolicy.AudioVolumeGroup> _result = _reply.createTypedArrayList(android.media.audiopolicy.AudioVolumeGroup.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setVolumeGroupVolumeIndex(int groupId, int index, int flags, String callingPackage, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    _data.writeInt(index);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(25, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getVolumeGroupVolumeIndex(int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    this.mRemote.transact(26, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getVolumeGroupMaxVolumeIndex(int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    this.mRemote.transact(27, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getVolumeGroupMinVolumeIndex(int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    this.mRemote.transact(28, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getLastAudibleVolumeForVolumeGroup(int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    this.mRemote.transact(29, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isVolumeGroupMuted(int groupId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    this.mRemote.transact(30, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustVolumeGroupVolume(int groupId, int direction, int flags, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(groupId);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(31, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getLastAudibleStreamVolume(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(32, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSupportedSystemUsages(int[] systemUsages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(systemUsages);
                    this.mRemote.transact(33, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int[] getSupportedSystemUsages() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(34, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<android.media.audiopolicy.AudioProductStrategy> getAudioProductStrategies() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(35, _data, _reply, 0);
                    _reply.readException();
                    List<android.media.audiopolicy.AudioProductStrategy> _result = _reply.createTypedArrayList(android.media.audiopolicy.AudioProductStrategy.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isMicrophoneMuted() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(36, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isUltrasoundSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(37, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isHotwordStreamSupported(boolean lookbackAudio) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(lookbackAudio);
                    this.mRemote.transact(38, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMicrophoneMute(boolean on, String callingPackage, int userId, String attributionTag) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    _data.writeString(callingPackage);
                    _data.writeInt(userId);
                    _data.writeString(attributionTag);
                    this.mRemote.transact(39, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMicrophoneMuteFromSwitch(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(40, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRingerModeExternal(int ringerMode, String caller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ringerMode);
                    _data.writeString(caller);
                    this.mRemote.transact(41, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRingerModeInternal(int ringerMode, String caller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ringerMode);
                    _data.writeString(caller);
                    this.mRemote.transact(42, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getRingerModeExternal() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(43, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getRingerModeInternal() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(44, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isValidRingerMode(int ringerMode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(ringerMode);
                    this.mRemote.transact(45, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setVibrateSetting(int vibrateType, int vibrateSetting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    _data.writeInt(vibrateSetting);
                    this.mRemote.transact(46, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getVibrateSetting(int vibrateType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    this.mRemote.transact(47, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean shouldVibrate(int vibrateType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(vibrateType);
                    this.mRemote.transact(48, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMode(int mode, IBinder cb, String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    _data.writeStrongBinder(cb);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(49, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(50, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playSoundEffect(int effectType, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(effectType);
                    _data.writeInt(userId);
                    this.mRemote.transact(51, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playSoundEffectVolume(int effectType, float volume) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(effectType);
                    _data.writeFloat(volume);
                    this.mRemote.transact(52, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean loadSoundEffects() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(53, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unloadSoundEffects() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(54, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void reloadAudioSettings() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(55, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public Map getSurroundFormats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(56, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    Map _result = _reply.readHashMap(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List getReportedSurroundFormats() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(57, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    List _result = _reply.readArrayList(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean setSurroundFormatEnabled(int audioFormat, boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(audioFormat);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(58, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSurroundFormatEnabled(int audioFormat) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(audioFormat);
                    this.mRemote.transact(59, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean setEncodedSurroundMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(60, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getEncodedSurroundMode(int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(61, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSpeakerphoneOn(IBinder cb, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    _data.writeBoolean(on);
                    this.mRemote.transact(62, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSpeakerphoneOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(63, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothScoOn(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(64, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isBluetoothScoOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(65, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothA2dpOn(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(66, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isBluetoothA2dpOn() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(67, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int requestAudioFocus(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, String attributionTag, int flags, IAudioPolicyCallback pcb, int sdk) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeInt(durationHint);
                    _data.writeStrongBinder(cb);
                    _data.writeStrongInterface(fd);
                    _data.writeString(clientId);
                    _data.writeString(callingPackageName);
                    _data.writeString(attributionTag);
                    _data.writeInt(flags);
                    _data.writeStrongInterface(pcb);
                    _data.writeInt(sdk);
                    this.mRemote.transact(68, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int abandonAudioFocus(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa, String callingPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(fd);
                    _data.writeString(clientId);
                    _data.writeTypedObject(aa, 0);
                    _data.writeString(callingPackageName);
                    this.mRemote.transact(69, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterAudioFocusClient(String clientId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(clientId);
                    this.mRemote.transact(70, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getCurrentAudioFocus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(71, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void startBluetoothSco(IBinder cb, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(72, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void startBluetoothScoVirtualCall(IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(73, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void stopBluetoothSco(IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(74, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void forceVolumeControlStream(int streamType, IBinder cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeStrongBinder(cb);
                    this.mRemote.transact(75, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRingtonePlayer(IRingtonePlayer player) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(player);
                    this.mRemote.transact(76, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public IRingtonePlayer getRingtonePlayer() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(77, _data, _reply, 0);
                    _reply.readException();
                    IRingtonePlayer _result = IRingtonePlayer.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getUiSoundsStreamType() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(78, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List getIndependentStreamTypes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(79, _data, _reply, 0);
                    _reply.readException();
                    ClassLoader cl = getClass().getClassLoader();
                    List _result = _reply.readArrayList(cl);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getStreamTypeAlias(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(80, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isVolumeControlUsingVolumeGroups() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(81, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerStreamAliasingDispatcher(IStreamAliasingDispatcher isad, boolean register) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(isad);
                    _data.writeBoolean(register);
                    this.mRemote.transact(82, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setNotifAliasRingForTest(boolean alias) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(alias);
                    this.mRemote.transact(83, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setWiredDeviceConnectionState(AudioDeviceAttributes aa, int state, String caller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeInt(state);
                    _data.writeString(caller);
                    this.mRemote.transact(84, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(observer);
                    this.mRemote.transact(85, _data, _reply, 0);
                    _reply.readException();
                    AudioRoutesInfo _result = (AudioRoutesInfo) _reply.readTypedObject(AudioRoutesInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isCameraSoundForced() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(86, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setVolumeController(IVolumeController controller) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    this.mRemote.transact(87, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public IVolumeController getVolumeController() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(88, _data, _reply, 0);
                    _reply.readException();
                    IVolumeController _result = IVolumeController.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void notifyVolumeControllerVisible(IVolumeController controller, boolean visible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(controller);
                    _data.writeBoolean(visible);
                    this.mRemote.transact(89, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isStreamAffectedByRingerMode(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(90, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isStreamAffectedByMute(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(91, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void disableSafeMediaVolume(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(92, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void lowerVolumeToRs1(String callingPackage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callingPackage);
                    this.mRemote.transact(93, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public float getRs2Value() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(94, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRs2Value(float rs2Value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(rs2Value);
                    this.mRemote.transact(95, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public float getCsd() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(96, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setCsd(float csd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(csd);
                    this.mRemote.transact(97, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void forceUseFrameworkMel(boolean useFrameworkMel) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(useFrameworkMel);
                    this.mRemote.transact(98, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void forceComputeCsdOnAllDevices(boolean computeCsdOnAllDevices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(computeCsdOnAllDevices);
                    this.mRemote.transact(99, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isCsdEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(100, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setHdmiSystemAudioSupported(boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(on);
                    this.mRemote.transact(101, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isHdmiSystemAudioSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(102, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public String registerAudioPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb, boolean hasFocusListener, boolean isFocusPolicy, boolean isTestFocusPolicy, boolean isVolumeController, IMediaProjection projection) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyConfig, 0);
                    _data.writeStrongInterface(pcb);
                    _data.writeBoolean(hasFocusListener);
                    _data.writeBoolean(isFocusPolicy);
                    _data.writeBoolean(isTestFocusPolicy);
                    _data.writeBoolean(isVolumeController);
                    _data.writeStrongInterface(projection);
                    this.mRemote.transact(103, _data, _reply, 0);
                    _reply.readException();
                    String _result = _reply.readString();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterAudioPolicyAsync(IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(104, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterAudioPolicy(IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(105, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int addMixForPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyConfig, 0);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(106, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int removeMixForPolicy(AudioPolicyConfig policyConfig, IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policyConfig, 0);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(107, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setFocusPropertiesForPolicy(int duckingBehavior, IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(duckingBehavior);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(108, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setVolumePolicy(VolumePolicy policy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(policy, 0);
                    this.mRemote.transact(109, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean hasRegisteredDynamicPolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(110, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerRecordingCallback(IRecordingConfigDispatcher rcdb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(rcdb);
                    this.mRemote.transact(111, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterRecordingCallback(IRecordingConfigDispatcher rcdb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(rcdb);
                    this.mRemote.transact(112, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioRecordingConfiguration> getActiveRecordingConfigurations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(113, _data, _reply, 0);
                    _reply.readException();
                    List<AudioRecordingConfiguration> _result = _reply.createTypedArrayList(AudioRecordingConfiguration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerPlaybackCallback(IPlaybackConfigDispatcher pcdb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcdb);
                    this.mRemote.transact(114, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterPlaybackCallback(IPlaybackConfigDispatcher pcdb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcdb);
                    this.mRemote.transact(115, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioPlaybackConfiguration> getActivePlaybackConfigurations() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(116, _data, _reply, 0);
                    _reply.readException();
                    List<AudioPlaybackConfiguration> _result = _reply.createTypedArrayList(AudioPlaybackConfiguration.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getFocusRampTimeMs(int focusGain, AudioAttributes attr) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(focusGain);
                    _data.writeTypedObject(attr, 0);
                    this.mRemote.transact(117, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int dispatchFocusChange(AudioFocusInfo afi, int focusChange, IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    _data.writeInt(focusChange);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(118, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void playerHasOpPlayAudio(int piid, boolean hasOpPlayAudio) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(piid);
                    _data.writeBoolean(hasOpPlayAudio);
                    this.mRemote.transact(119, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void handleBluetoothActiveDeviceChanged(BluetoothDevice newDevice, BluetoothDevice previousDevice, BluetoothProfileConnectionInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(newDevice, 0);
                    _data.writeTypedObject(previousDevice, 0);
                    _data.writeTypedObject(info, 0);
                    this.mRemote.transact(120, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setFocusRequestResultFromExtPolicy(AudioFocusInfo afi, int requestResult, IAudioPolicyCallback pcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(afi, 0);
                    _data.writeInt(requestResult);
                    _data.writeStrongInterface(pcb);
                    this.mRemote.transact(121, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerAudioServerStateDispatcher(IAudioServerStateDispatcher asd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(asd);
                    this.mRemote.transact(122, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterAudioServerStateDispatcher(IAudioServerStateDispatcher asd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(asd);
                    this.mRemote.transact(123, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isAudioServerRunning() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(124, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setUidDeviceAffinity(IAudioPolicyCallback pcb, int uid, int[] deviceTypes, String[] deviceAddresses) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    _data.writeInt(uid);
                    _data.writeIntArray(deviceTypes);
                    _data.writeStringArray(deviceAddresses);
                    this.mRemote.transact(125, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int removeUidDeviceAffinity(IAudioPolicyCallback pcb, int uid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    _data.writeInt(uid);
                    this.mRemote.transact(126, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setUserIdDeviceAffinity(IAudioPolicyCallback pcb, int userId, int[] deviceTypes, String[] deviceAddresses) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    _data.writeInt(userId);
                    _data.writeIntArray(deviceTypes);
                    _data.writeStringArray(deviceAddresses);
                    this.mRemote.transact(127, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int removeUserIdDeviceAffinity(IAudioPolicyCallback pcb, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(pcb);
                    _data.writeInt(userId);
                    this.mRemote.transact(128, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean hasHapticChannels(Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(uri, 0);
                    this.mRemote.transact(129, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isCallScreeningModeSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(130, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setPreferredDevicesForStrategy(int strategy, List<AudioDeviceAttributes> devices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    _data.writeTypedList(devices, 0);
                    this.mRemote.transact(131, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int removePreferredDevicesForStrategy(int strategy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    this.mRemote.transact(132, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getPreferredDevicesForStrategy(int strategy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    this.mRemote.transact(133, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setDeviceAsNonDefaultForStrategy(int strategy, AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(134, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int removeDeviceAsNonDefaultForStrategy(int strategy, AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(135, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getNonDefaultDevicesForStrategy(int strategy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strategy);
                    this.mRemote.transact(136, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getDevicesForAttributes(AudioAttributes attributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
                    this.mRemote.transact(137, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getDevicesForAttributesUnprotected(AudioAttributes attributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
                    this.mRemote.transact(138, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void addOnDevicesForAttributesChangedListener(AudioAttributes attributes, IDevicesForAttributesCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(attributes, 0);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(139, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void removeOnDevicesForAttributesChangedListener(IDevicesForAttributesCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(140, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setAllowedCapturePolicy(int capturePolicy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(capturePolicy);
                    this.mRemote.transact(141, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getAllowedCapturePolicy() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(142, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(143, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterStrategyPreferredDevicesDispatcher(IStrategyPreferredDevicesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(144, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(145, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterStrategyNonDefaultDevicesDispatcher(IStrategyNonDefaultDevicesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(146, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setRttEnabled(boolean rttEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(rttEnabled);
                    this.mRemote.transact(147, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setDeviceVolumeBehavior(AudioDeviceAttributes device, int deviceVolumeBehavior, String pkgName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeInt(deviceVolumeBehavior);
                    _data.writeString(pkgName);
                    this.mRemote.transact(148, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getDeviceVolumeBehavior(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(149, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setMultiAudioFocusEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(150, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setPreferredDevicesForCapturePreset(int capturePreset, List<AudioDeviceAttributes> devices) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(capturePreset);
                    _data.writeTypedList(devices, 0);
                    this.mRemote.transact(151, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int clearPreferredDevicesForCapturePreset(int capturePreset) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(capturePreset);
                    this.mRemote.transact(152, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getPreferredDevicesForCapturePreset(int capturePreset) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(capturePreset);
                    this.mRemote.transact(153, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(154, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterCapturePresetDevicesRoleDispatcher(ICapturePresetDevicesRoleDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(155, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeTypedObject(userHandle, 0);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(156, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void adjustSuggestedStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeTypedObject(userHandle, 0);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(157, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setStreamVolumeForUid(int streamType, int direction, int flags, String packageName, int uid, int pid, UserHandle userHandle, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    _data.writeInt(direction);
                    _data.writeInt(flags);
                    _data.writeString(packageName);
                    _data.writeInt(uid);
                    _data.writeInt(pid);
                    _data.writeTypedObject(userHandle, 0);
                    _data.writeInt(targetSdkVersion);
                    this.mRemote.transact(158, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isMusicActive(boolean remotely) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(remotely);
                    this.mRemote.transact(159, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getDeviceMaskForStream(int streamType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(streamType);
                    this.mRemote.transact(160, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int[] getAvailableCommunicationDeviceIds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(161, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean setCommunicationDevice(IBinder cb, int portId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(cb);
                    _data.writeInt(portId);
                    this.mRemote.transact(162, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getCommunicationDevice() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(163, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(164, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterCommunicationDeviceDispatcher(ICommunicationDeviceDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(165, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean areNavigationRepeatSoundEffectsEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(166, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setNavigationRepeatSoundEffectsEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(167, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isHomeSoundEffectEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(168, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setHomeSoundEffectEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(169, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean setAdditionalOutputDeviceDelay(AudioDeviceAttributes device, long delayMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeLong(delayMillis);
                    this.mRemote.transact(170, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public long getAdditionalOutputDeviceDelay(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(171, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public long getMaxAdditionalOutputDeviceDelay(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(172, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int requestAudioFocusForTest(AudioAttributes aa, int durationHint, IBinder cb, IAudioFocusDispatcher fd, String clientId, String callingPackageName, int flags, int uid, int sdk) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeInt(durationHint);
                    _data.writeStrongBinder(cb);
                    _data.writeStrongInterface(fd);
                    _data.writeString(clientId);
                    _data.writeString(callingPackageName);
                    _data.writeInt(flags);
                    _data.writeInt(uid);
                    _data.writeInt(sdk);
                    this.mRemote.transact(173, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int abandonAudioFocusForTest(IAudioFocusDispatcher fd, String clientId, AudioAttributes aa, String callingPackageName) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(fd);
                    _data.writeString(clientId);
                    _data.writeTypedObject(aa, 0);
                    _data.writeString(callingPackageName);
                    this.mRemote.transact(174, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public long getFadeOutDurationOnFocusLossMillis(AudioAttributes aa) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    this.mRemote.transact(175, _data, _reply, 0);
                    _reply.readException();
                    long _result = _reply.readLong();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerModeDispatcher(IAudioModeDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(176, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterModeDispatcher(IAudioModeDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(177, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getSpatializerImmersiveAudioLevel() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(178, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSpatializerEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(179, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSpatializerAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(180, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isSpatializerAvailableForDevice(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(181, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean hasHeadTracker(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(182, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setHeadTrackerEnabled(boolean enabled, AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(183, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isHeadTrackerEnabled(AudioDeviceAttributes device) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    this.mRemote.transact(184, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isHeadTrackerAvailable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(185, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerSpatializerHeadTrackerAvailableCallback(ISpatializerHeadTrackerAvailableCallback cb, boolean register) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    _data.writeBoolean(register);
                    this.mRemote.transact(186, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSpatializerEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(187, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean canBeSpatialized(AudioAttributes aa, AudioFormat af) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeTypedObject(af, 0);
                    this.mRemote.transact(188, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerSpatializerCallback(ISpatializerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(189, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterSpatializerCallback(ISpatializerCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(190, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(191, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterSpatializerHeadTrackingCallback(ISpatializerHeadTrackingModeCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(192, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(193, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterHeadToSoundstagePoseCallback(ISpatializerHeadToSoundStagePoseCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(194, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioDeviceAttributes> getSpatializerCompatibleAudioDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(195, _data, _reply, 0);
                    _reply.readException();
                    List<AudioDeviceAttributes> _result = _reply.createTypedArrayList(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void addSpatializerCompatibleAudioDevice(AudioDeviceAttributes ada) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ada, 0);
                    this.mRemote.transact(196, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void removeSpatializerCompatibleAudioDevice(AudioDeviceAttributes ada) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(ada, 0);
                    this.mRemote.transact(197, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setDesiredHeadTrackingMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(mode);
                    this.mRemote.transact(198, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getDesiredHeadTrackingMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(199, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int[] getSupportedHeadTrackingModes() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(200, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getActualHeadTrackingMode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(201, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSpatializerGlobalTransform(float[] transform) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloatArray(transform);
                    this.mRemote.transact(202, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void recenterHeadTracker() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(203, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setSpatializerParameter(int key, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    _data.writeByteArray(value);
                    this.mRemote.transact(204, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void getSpatializerParameter(int key, byte[] value) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(key);
                    _data.writeByteArray(value);
                    this.mRemote.transact(205, _data, _reply, 0);
                    _reply.readException();
                    _reply.readByteArray(value);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int getSpatializerOutput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(206, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerSpatializerOutputCallback(ISpatializerOutputCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(207, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterSpatializerOutputCallback(ISpatializerOutputCallback cb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    this.mRemote.transact(208, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isVolumeFixed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(209, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public VolumeInfo getDefaultVolumeInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(210, _data, _reply, 0);
                    _reply.readException();
                    VolumeInfo _result = (VolumeInfo) _reply.readTypedObject(VolumeInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isPstnCallAudioInterceptable() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(211, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void muteAwaitConnection(int[] usagesToMute, AudioDeviceAttributes dev, long timeOutMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(usagesToMute);
                    _data.writeTypedObject(dev, 0);
                    _data.writeLong(timeOutMs);
                    this.mRemote.transact(212, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void cancelMuteAwaitConnection(AudioDeviceAttributes dev) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(dev, 0);
                    this.mRemote.transact(213, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public AudioDeviceAttributes getMutingExpectedDevice() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(214, _data, _reply, 0);
                    _reply.readException();
                    AudioDeviceAttributes _result = (AudioDeviceAttributes) _reply.readTypedObject(AudioDeviceAttributes.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerMuteAwaitConnectionDispatcher(IMuteAwaitConnectionCallback cb, boolean register) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(cb);
                    _data.writeBoolean(register);
                    this.mRemote.transact(215, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setTestDeviceConnectionState(AudioDeviceAttributes device, boolean connected) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(device, 0);
                    _data.writeBoolean(connected);
                    this.mRemote.transact(216, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerDeviceVolumeBehaviorDispatcher(boolean register, IDeviceVolumeBehaviorDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(register);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(217, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public List<AudioFocusInfo> getFocusStack() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(218, _data, _reply, 0);
                    _reply.readException();
                    List<AudioFocusInfo> _result = _reply.createTypedArrayList(AudioFocusInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean sendFocusLoss(AudioFocusInfo focusLoser, IAudioPolicyCallback apcb) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(focusLoser, 0);
                    _data.writeStrongInterface(apcb);
                    this.mRemote.transact(219, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void addAssistantServicesUids(int[] assistantUID) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(assistantUID);
                    this.mRemote.transact(220, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void removeAssistantServicesUids(int[] assistantUID) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(assistantUID);
                    this.mRemote.transact(221, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setActiveAssistantServiceUids(int[] activeUids) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeIntArray(activeUids);
                    this.mRemote.transact(222, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int[] getAssistantServicesUids() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(223, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int[] getActiveAssistantServiceUids() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(224, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerDeviceVolumeDispatcherForAbsoluteVolume(boolean register, IAudioDeviceVolumeDispatcher cb, String packageName, AudioDeviceAttributes device, List<VolumeInfo> volumes, boolean handlesvolumeAdjustment, int volumeBehavior) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(register);
                    _data.writeStrongInterface(cb);
                    _data.writeString(packageName);
                    _data.writeTypedObject(device, 0);
                    _data.writeTypedList(volumes, 0);
                    _data.writeBoolean(handlesvolumeAdjustment);
                    _data.writeInt(volumeBehavior);
                    this.mRemote.transact(225, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public AudioHalVersionInfo getHalVersion() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(226, _data, _reply, 0);
                    _reply.readException();
                    AudioHalVersionInfo _result = (AudioHalVersionInfo) _reply.readTypedObject(AudioHalVersionInfo.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int setPreferredMixerAttributes(AudioAttributes aa, int portId, AudioMixerAttributes mixerAttributes) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeInt(portId);
                    _data.writeTypedObject(mixerAttributes, 0);
                    this.mRemote.transact(227, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public int clearPreferredMixerAttributes(AudioAttributes aa, int portId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(aa, 0);
                    _data.writeInt(portId);
                    this.mRemote.transact(228, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void registerPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(229, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void unregisterPreferredMixerAttributesDispatcher(IPreferredMixerAttributesDispatcher dispatcher) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(dispatcher);
                    this.mRemote.transact(230, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean supportsBluetoothVariableLatency() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(231, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public void setBluetoothVariableLatencyEnabled(boolean enabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    this.mRemote.transact(232, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.media.IAudioService
            public boolean isBluetoothVariableLatencyEnabled() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(233, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        protected void setDeviceVolume_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_ROUTING, Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED}, source);
        }

        protected void getDeviceVolume_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_ROUTING, Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED}, source);
        }

        protected void setMasterMute_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getAudioVolumeGroups_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void setVolumeGroupVolumeIndex_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, Manifest.C0000permission.MODIFY_AUDIO_ROUTING}, source);
        }

        protected void getVolumeGroupVolumeIndex_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, Manifest.C0000permission.MODIFY_AUDIO_ROUTING}, source);
        }

        protected void getVolumeGroupMaxVolumeIndex_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, Manifest.C0000permission.MODIFY_AUDIO_ROUTING}, source);
        }

        protected void getVolumeGroupMinVolumeIndex_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, Manifest.C0000permission.MODIFY_AUDIO_ROUTING}, source);
        }

        protected void getLastAudibleVolumeForVolumeGroup_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.QUERY_AUDIO_STATE, source);
        }

        protected void getLastAudibleStreamVolume_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.QUERY_AUDIO_STATE, source);
        }

        protected void setSupportedSystemUsages_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getSupportedSystemUsages_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getAudioProductStrategies_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void isUltrasoundSupported_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.ACCESS_ULTRASOUND, source);
        }

        protected void isHotwordStreamSupported_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CAPTURE_AUDIO_HOTWORD, source);
        }

        protected void getIndependentStreamTypes_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void getStreamTypeAlias_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void isVolumeControlUsingVolumeGroups_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void registerStreamAliasingDispatcher_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void setNotifAliasRingForTest_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void setWiredDeviceConnectionState_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getRs2Value_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void setRs2Value_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void getCsd_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void setCsd_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void forceUseFrameworkMel_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void forceComputeCsdOnAllDevices_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void isCsdEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED, source);
        }

        protected void setPreferredDevicesForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void removePreferredDevicesForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getPreferredDevicesForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void setDeviceAsNonDefaultForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void removeDeviceAsNonDefaultForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getNonDefaultDevicesForStrategy_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void setDeviceVolumeBehavior_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_ROUTING, Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED}, source);
        }

        protected void getDeviceVolumeBehavior_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermissionAnyOf(new String[]{Manifest.C0000permission.MODIFY_AUDIO_ROUTING, Manifest.C0000permission.QUERY_AUDIO_STATE, Manifest.C0000permission.MODIFY_AUDIO_SETTINGS_PRIVILEGED}, source);
        }

        protected void setMultiAudioFocusEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void clearPreferredDevicesForCapturePreset_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getPreferredDevicesForCapturePreset_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void isSpatializerAvailableForDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void hasHeadTracker_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void setHeadTrackerEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void isHeadTrackerEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void setSpatializerEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void registerSpatializerHeadTrackingCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void unregisterSpatializerHeadTrackingCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void registerHeadToSoundstagePoseCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void unregisterHeadToSoundstagePoseCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getSpatializerCompatibleAudioDevices_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void addSpatializerCompatibleAudioDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void removeSpatializerCompatibleAudioDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void setDesiredHeadTrackingMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getDesiredHeadTrackingMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getSupportedHeadTrackingModes_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getActualHeadTrackingMode_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void setSpatializerGlobalTransform_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void recenterHeadTracker_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void setSpatializerParameter_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getSpatializerParameter_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void getSpatializerOutput_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void registerSpatializerOutputCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void unregisterSpatializerOutputCallback_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_DEFAULT_AUDIO_EFFECTS, source);
        }

        protected void isPstnCallAudioInterceptable_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.CALL_AUDIO_INTERCEPTION, source);
        }

        protected void getMutingExpectedDevice_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void registerMuteAwaitConnectionDispatcher_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getFocusStack_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void addAssistantServicesUids_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void removeAssistantServicesUids_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void setActiveAssistantServiceUids_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getAssistantServicesUids_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void getActiveAssistantServiceUids_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void supportsBluetoothVariableLatency_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void setBluetoothVariableLatencyEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        protected void isBluetoothVariableLatencyEnabled_enforcePermission() throws SecurityException {
            AttributionSource source = new AttributionSource(getCallingUid(), null, null);
            this.mEnforcer.enforcePermission(Manifest.C0000permission.MODIFY_AUDIO_ROUTING, source);
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 232;
        }
    }
}
