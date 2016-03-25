package ru.ok.android.services.processors.stickers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import ru.ok.android.storage.StorageHelper;
import ru.ok.android.utils.Logger;
import ru.ok.model.stickers.Sticker;
import ru.ok.model.stickers.StickerSet;
import ru.ok.model.stickers.StickersResponse;

final class StickersStorage {
    static synchronized void storeStickers(Context context, @NonNull StickersResponse data) {
        synchronized (StickersStorage.class) {
            DataOutputStream os;
            try {
                os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(StorageHelper.getFileByName(context, "stickers"))));
                os.writeInt(data.version);
                os.writeLong(data.expirationDeltaMs);
                os.writeInt(data.sets.size());
                for (StickerSet set : data.sets) {
                    os.writeInt(set.id);
                    os.writeUTF(set.name);
                    os.writeUTF(set.iconUrl);
                    os.writeInt(set.price);
                    os.writeInt(set.sinceVersion);
                    os.writeInt(set.width);
                    os.writeInt(set.height);
                    os.writeInt(set.stickers.size());
                    for (Sticker code : set.stickers) {
                        os.writeUTF(code.code);
                    }
                }
                os.close();
            } catch (Throwable e) {
                Logger.m179e(e, "Failed to write stickers data");
            } catch (Throwable th) {
                os.close();
            }
        }
    }

    @Nullable
    static synchronized StickersResponse readStickers(Context context) {
        StickersResponse stickersResponse;
        synchronized (StickersStorage.class) {
            DataInputStream dataInputStream;
            try {
                dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(StorageHelper.getFileByName(context, "stickers"))));
                int version = dataInputStream.readInt();
                long expirationDeltaMs = dataInputStream.readLong();
                int n = dataInputStream.readInt();
                List<StickerSet> sets = new ArrayList();
                for (int i = 0; i < n; i++) {
                    int id = dataInputStream.readInt();
                    String name = dataInputStream.readUTF();
                    String iconUrl = dataInputStream.readUTF();
                    int price = dataInputStream.readInt();
                    int sinceVersion = dataInputStream.readInt();
                    int width = dataInputStream.readInt();
                    int height = dataInputStream.readInt();
                    int stickersCount = dataInputStream.readInt();
                    List<Sticker> stickers = new ArrayList();
                    for (int j = 0; j < stickersCount; j++) {
                        stickers.add(new Sticker(dataInputStream.readUTF(), price, width, height));
                    }
                    sets.add(new StickerSet(id, name, iconUrl, price, sinceVersion, width, height, stickers));
                }
                stickersResponse = new StickersResponse(version, expirationDeltaMs, sets);
                dataInputStream.close();
            } catch (FileNotFoundException e) {
                stickersResponse = null;
            } catch (Throwable e2) {
                Logger.m179e(e2, "Failed to read stickers data");
                stickersResponse = null;
            } catch (Throwable th) {
                dataInputStream.close();
            }
        }
        return stickersResponse;
    }

    public static void removeStickers(Context context) {
        StorageHelper.removeFile(context, "stickers");
    }
}
