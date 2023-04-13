package com.android.internal.location.altitude;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import com.android.internal.location.altitude.nano.MapParamsProto;
import com.android.internal.location.altitude.nano.S2TileProto;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class GeoidHeightMap {
    private static final Object sLock = new Object();
    private static MapParamsProto sParams;
    private final LruCache<Long, S2TileProto> mCacheTiles = new LruCache<>(4);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface TileFunction {
        S2TileProto getTile(long j);
    }

    public static MapParamsProto getParams(Context context) throws IOException {
        MapParamsProto mapParamsProto;
        synchronized (sLock) {
            if (sParams == null) {
                InputStream is = context.getApplicationContext().getAssets().open("geoid_height_map/map-params.pb");
                sParams = MapParamsProto.parseFrom(is.readAllBytes());
                if (is != null) {
                    is.close();
                }
            }
            mapParamsProto = sParams;
        }
        return mapParamsProto;
    }

    public static MapParamsProto getParams() {
        MapParamsProto mapParamsProto;
        synchronized (sLock) {
            mapParamsProto = sParams;
        }
        return mapParamsProto;
    }

    private static long getCacheKey(MapParamsProto params, long s2CellId) {
        return S2CellIdUtils.getParent(s2CellId, params.cacheTileS2Level);
    }

    private static String getDiskToken(MapParamsProto params, long s2CellId) {
        return S2CellIdUtils.getToken(S2CellIdUtils.getParent(s2CellId, params.diskTileS2Level));
    }

    private static boolean getUnitIntervalValues(MapParamsProto params, TileFunction tileFunction, long[] s2CellIds, double[] values) {
        int len = s2CellIds.length;
        S2TileProto[] tiles = new S2TileProto[len];
        for (int i = 0; i < len; i++) {
            if (s2CellIds[i] != 0) {
                long cacheKey = getCacheKey(params, s2CellIds[i]);
                tiles[i] = tileFunction.getTile(cacheKey);
            }
            values[i] = Double.NaN;
        }
        for (int i2 = 0; i2 < len; i2++) {
            if (tiles[i2] != null && Double.isNaN(values[i2])) {
                mergeByteBufferValues(params, s2CellIds, tiles, i2, values);
                mergeByteJpegValues(params, s2CellIds, tiles, i2, values);
                mergeBytePngValues(params, s2CellIds, tiles, i2, values);
            }
        }
        boolean allFound = true;
        for (int i3 = 0; i3 < len; i3++) {
            if (s2CellIds[i3] != 0) {
                if (Double.isNaN(values[i3])) {
                    allFound = false;
                } else {
                    values[i3] = (((int) values[i3]) & 255) / 255.0d;
                }
            }
        }
        return allFound;
    }

    private static void mergeByteBufferValues(MapParamsProto params, long[] s2CellIds, S2TileProto[] tiles, int tileIndex, double[] values) {
        byte[] bytes = tiles[tileIndex].byteBuffer;
        if (bytes == null || bytes.length == 0) {
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes).asReadOnlyBuffer();
        int tileS2Level = params.mapS2Level - (Integer.numberOfTrailingZeros(byteBuffer.limit()) / 2);
        int numBitsLeftOfTile = (tileS2Level * 2) + 3;
        for (int i = tileIndex; i < tiles.length; i++) {
            if (tiles[i] == tiles[tileIndex]) {
                long maskedS2CellId = s2CellIds[i] & ((-1) >>> numBitsLeftOfTile);
                int numBitsRightOfMap = ((30 - params.mapS2Level) * 2) + 1;
                int bufferIndex = (int) (maskedS2CellId >>> numBitsRightOfMap);
                values[i] = Double.isNaN(values[i]) ? 0.0d : values[i];
                values[i] = values[i] + (byteBuffer.get(bufferIndex) & 255);
            }
        }
    }

    private static void mergeByteJpegValues(MapParamsProto params, long[] s2CellIds, S2TileProto[] tiles, int tileIndex, double[] values) {
        mergeByteImageValues(params, tiles[tileIndex].byteJpeg, s2CellIds, tiles, tileIndex, values);
    }

    private static void mergeBytePngValues(MapParamsProto params, long[] s2CellIds, S2TileProto[] tiles, int tileIndex, double[] values) {
        mergeByteImageValues(params, tiles[tileIndex].bytePng, s2CellIds, tiles, tileIndex, values);
    }

    private static void mergeByteImageValues(MapParamsProto params, byte[] bytes, long[] s2CellIds, S2TileProto[] tiles, int tileIndex, double[] values) {
        Bitmap bitmap;
        if (bytes == null || bytes.length == 0 || (bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length)) == null) {
            return;
        }
        for (int i = tileIndex; i < tiles.length; i++) {
            if (s2CellIds[i] != 0 && tiles[i] == tiles[tileIndex]) {
                values[i] = Double.isNaN(values[i]) ? 0.0d : values[i];
                values[i] = values[i] + (bitmap.getPixel(getIndexX(params, s2CellIds[i], bitmap.getWidth()), getIndexY(params, s2CellIds[i], bitmap.getHeight())) & 255);
            }
        }
    }

    private static int getIndexX(MapParamsProto params, long s2CellId, int width) {
        return getIndexXOrY(params, S2CellIdUtils.getI(s2CellId), width);
    }

    private static int getIndexY(MapParamsProto params, long s2CellId, int height) {
        return getIndexXOrY(params, S2CellIdUtils.getJ(s2CellId), height);
    }

    private static int getIndexXOrY(MapParamsProto params, int iOrJ, int widthOrHeight) {
        return (iOrJ >> (30 - params.mapS2Level)) % widthOrHeight;
    }

    private static void validate(MapParamsProto params, long[] s2CellIds) {
        Preconditions.checkArgument(s2CellIds.length == 4);
        int length = s2CellIds.length;
        for (int i = 0; i < length; i++) {
            long s2CellId = s2CellIds[i];
            Preconditions.checkArgument(s2CellId == 0 || S2CellIdUtils.getLevel(s2CellId) == params.mapS2Level);
        }
    }

    public double[] readGeoidHeights(MapParamsProto params, Context context, long[] s2CellIds) throws IOException {
        validate(params, s2CellIds);
        double[] heightsMeters = new double[s2CellIds.length];
        LruCache<Long, S2TileProto> lruCache = this.mCacheTiles;
        Objects.requireNonNull(lruCache);
        if (getGeoidHeights(params, new GeoidHeightMap$$ExternalSyntheticLambda0(lruCache), s2CellIds, heightsMeters)) {
            return heightsMeters;
        }
        TileFunction loadedTiles = loadFromCacheAndDisk(params, context, s2CellIds);
        if (getGeoidHeights(params, loadedTiles, s2CellIds, heightsMeters)) {
            return heightsMeters;
        }
        throw new IOException("Unable to calculate geoid heights from raw assets.");
    }

    public double[] readGeoidHeights(MapParamsProto params, long[] s2CellIds) {
        validate(params, s2CellIds);
        double[] heightsMeters = new double[s2CellIds.length];
        LruCache<Long, S2TileProto> lruCache = this.mCacheTiles;
        Objects.requireNonNull(lruCache);
        if (getGeoidHeights(params, new GeoidHeightMap$$ExternalSyntheticLambda0(lruCache), s2CellIds, heightsMeters)) {
            return heightsMeters;
        }
        return null;
    }

    private boolean getGeoidHeights(MapParamsProto params, TileFunction tileFunction, long[] s2CellIds, double[] heightsMeters) {
        boolean allFound = getUnitIntervalValues(params, tileFunction, s2CellIds, heightsMeters);
        for (int i = 0; i < heightsMeters.length; i++) {
            heightsMeters[i] = heightsMeters[i] * params.modelAMeters;
            heightsMeters[i] = heightsMeters[i] + params.modelBMeters;
        }
        return allFound;
    }

    private TileFunction loadFromCacheAndDisk(MapParamsProto params, Context context, long[] s2CellIds) throws IOException {
        int i;
        int len = s2CellIds.length;
        final long[] cacheKeys = new long[len];
        for (int i2 = 0; i2 < len; i2++) {
            if (s2CellIds[i2] != 0) {
                cacheKeys[i2] = getCacheKey(params, s2CellIds[i2]);
            }
        }
        final S2TileProto[] loadedTiles = new S2TileProto[len];
        String[] diskTokens = new String[len];
        for (int i3 = 0; i3 < len; i3++) {
            if (s2CellIds[i3] != 0 && diskTokens[i3] == null) {
                loadedTiles[i3] = this.mCacheTiles.get(Long.valueOf(cacheKeys[i3]));
                diskTokens[i3] = getDiskToken(params, cacheKeys[i3]);
                for (int j = i3 + 1; j < len; j++) {
                    if (cacheKeys[j] == cacheKeys[i3]) {
                        loadedTiles[j] = loadedTiles[i3];
                        diskTokens[j] = diskTokens[i3];
                    }
                }
            }
        }
        int i4 = 0;
        while (i4 < len) {
            if (s2CellIds[i4] == 0) {
                i = i4;
            } else if (loadedTiles[i4] != null) {
                i = i4;
            } else {
                InputStream is = context.getApplicationContext().getAssets().open("geoid_height_map/tile-" + diskTokens[i4] + ".pb");
                try {
                    S2TileProto tile = S2TileProto.parseFrom(is.readAllBytes());
                    if (is != null) {
                        is.close();
                    }
                    i = i4;
                    mergeFromDiskTile(params, tile, cacheKeys, diskTokens, i4, loadedTiles);
                } catch (Throwable th) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            i4 = i + 1;
        }
        return new TileFunction() { // from class: com.android.internal.location.altitude.GeoidHeightMap$$ExternalSyntheticLambda1
            @Override // com.android.internal.location.altitude.GeoidHeightMap.TileFunction
            public final S2TileProto getTile(long j2) {
                return GeoidHeightMap.lambda$loadFromCacheAndDisk$0(cacheKeys, loadedTiles, j2);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ S2TileProto lambda$loadFromCacheAndDisk$0(long[] cacheKeys, S2TileProto[] loadedTiles, long cacheKey) {
        for (int i = 0; i < cacheKeys.length; i++) {
            if (cacheKeys[i] == cacheKey) {
                return loadedTiles[i];
            }
        }
        return null;
    }

    private void mergeFromDiskTile(MapParamsProto params, final S2TileProto diskTile, long[] cacheKeys, String[] diskTokens, int diskTokenIndex, S2TileProto[] loadedTiles) throws IOException {
        int len = cacheKeys.length;
        int numMapCellsPerCacheTile = 1 << ((params.mapS2Level - params.cacheTileS2Level) * 2);
        long[] s2CellIds = new long[numMapCellsPerCacheTile];
        double[] values = new double[numMapCellsPerCacheTile];
        TileFunction diskTileFunction = new TileFunction() { // from class: com.android.internal.location.altitude.GeoidHeightMap$$ExternalSyntheticLambda2
            @Override // com.android.internal.location.altitude.GeoidHeightMap.TileFunction
            public final S2TileProto getTile(long j) {
                return GeoidHeightMap.lambda$mergeFromDiskTile$1(S2TileProto.this, j);
            }
        };
        for (int i = diskTokenIndex; i < len; i++) {
            if (Objects.equals(diskTokens[i], diskTokens[diskTokenIndex]) && loadedTiles[i] == null) {
                long s2CellId = S2CellIdUtils.getTraversalStart(cacheKeys[i], params.mapS2Level);
                for (int j = 0; j < numMapCellsPerCacheTile; j++) {
                    s2CellIds[j] = s2CellId;
                    s2CellId = S2CellIdUtils.getTraversalNext(s2CellId);
                }
                if (!getUnitIntervalValues(params, diskTileFunction, s2CellIds, values)) {
                    throw new IOException("Corrupted disk tile of disk token: " + diskTokens[i]);
                }
                loadedTiles[i] = new S2TileProto();
                loadedTiles[i].byteBuffer = new byte[numMapCellsPerCacheTile];
                for (int j2 = 0; j2 < numMapCellsPerCacheTile; j2++) {
                    loadedTiles[i].byteBuffer[j2] = (byte) Math.round(values[j2] * 255.0d);
                }
                for (int j3 = i + 1; j3 < len; j3++) {
                    if (cacheKeys[j3] == cacheKeys[i]) {
                        loadedTiles[j3] = loadedTiles[i];
                    }
                }
                this.mCacheTiles.put(Long.valueOf(cacheKeys[i]), loadedTiles[i]);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ S2TileProto lambda$mergeFromDiskTile$1(S2TileProto diskTile, long cacheKey) {
        return diskTile;
    }
}
