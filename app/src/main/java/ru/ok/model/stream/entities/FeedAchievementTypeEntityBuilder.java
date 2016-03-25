package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedAchievementTypeEntityBuilder extends BaseEntityBuilder<FeedAchievementTypeEntityBuilder, FeedAchievementTypeEntity> {
    public static final Creator<FeedAchievementTypeEntityBuilder> CREATOR;
    final TreeSet<PhotoSize> pics;
    String title;

    /* renamed from: ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder.1 */
    static class C16131 implements Creator<FeedAchievementTypeEntityBuilder> {
        C16131() {
        }

        public FeedAchievementTypeEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedAchievementTypeEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedAchievementTypeEntityBuilder[] newArray(int size) {
            return new FeedAchievementTypeEntityBuilder[size];
        }
    }

    protected ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder readFromParcel(android.os.Parcel r7) throws ru.ok.model.stream.RecoverableUnParcelException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:10:0x0048 in [B:6:0x003b, B:10:0x0048, B:9:0x0049]
	at jadx.core.utils.BlockUtils.selectOther(BlockUtils.java:53)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:62)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
        /*
        r6 = this;
        super.readFromParcel(r7);	 Catch:{ all -> 0x0025 }
        r4 = r7.readString();
        r6.title = r4;
        r4 = ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder.class;
        r0 = r4.getClassLoader();
        r1 = r7.readInt();
        r2 = r1;
    L_0x0014:
        r1 = r2 + -1;
        if (r2 <= 0) goto L_0x0049;
    L_0x0018:
        r3 = r7.readParcelable(r0);
        r3 = (ru.ok.model.photo.PhotoSize) r3;
        r4 = r6.pics;
        r4.add(r3);
        r2 = r1;
        goto L_0x0014;
    L_0x0025:
        r4 = move-exception;
        r5 = r7.readString();
        r6.title = r5;
        r5 = ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder.class;
        r0 = r5.getClassLoader();
        r1 = r7.readInt();
        r2 = r1;
    L_0x0037:
        r1 = r2 + -1;
        if (r2 <= 0) goto L_0x0048;
    L_0x003b:
        r3 = r7.readParcelable(r0);
        r3 = (ru.ok.model.photo.PhotoSize) r3;
        r5 = r6.pics;
        r5.add(r3);
        r2 = r1;
        goto L_0x0037;
    L_0x0048:
        throw r4;
    L_0x0049:
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder.readFromParcel(android.os.Parcel):ru.ok.model.stream.entities.FeedAchievementTypeEntityBuilder");
    }

    public FeedAchievementTypeEntityBuilder addPic(PhotoSize pic) {
        if (pic != null) {
            this.pics.add(pic);
        }
        return this;
    }

    public FeedAchievementTypeEntityBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    protected FeedAchievementTypeEntity doPreBuild() {
        return new FeedAchievementTypeEntity(getId(), this.title, this.pics, getLikeInfo());
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeInt(this.pics.size());
        Iterator i$ = this.pics.iterator();
        while (i$.hasNext()) {
            dest.writeParcelable((PhotoSize) i$.next(), flags);
        }
    }

    public FeedAchievementTypeEntityBuilder() {
        super(19);
        this.pics = new TreeSet();
    }

    static {
        CREATOR = new C16131();
    }
}
