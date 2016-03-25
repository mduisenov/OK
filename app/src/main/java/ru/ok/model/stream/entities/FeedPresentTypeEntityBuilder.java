package ru.ok.model.stream.entities;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import ru.ok.model.photo.PhotoSize;
import ru.ok.model.presents.AnimationProperties;
import ru.ok.model.stream.RecoverableUnParcelException;

public class FeedPresentTypeEntityBuilder extends BaseEntityBuilder<FeedPresentTypeEntityBuilder, FeedPresentTypeEntity> {
    public static final Creator<FeedPresentTypeEntityBuilder> CREATOR;
    @Nullable
    AnimationProperties animationProperties;
    boolean isAnimated;
    boolean isLive;
    final TreeSet<PhotoSize> pics;
    final TreeSet<PhotoSize> sprites;

    /* renamed from: ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder.1 */
    static class C16281 implements Creator<FeedPresentTypeEntityBuilder> {
        C16281() {
        }

        public FeedPresentTypeEntityBuilder createFromParcel(Parcel source) {
            try {
                return new FeedPresentTypeEntityBuilder().readFromParcel(source);
            } catch (RecoverableUnParcelException e) {
                return null;
            }
        }

        public FeedPresentTypeEntityBuilder[] newArray(int size) {
            return new FeedPresentTypeEntityBuilder[size];
        }
    }

    protected ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder readFromParcel(@android.support.annotation.NonNull android.os.Parcel r13) throws ru.ok.model.stream.RecoverableUnParcelException {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.JadxRuntimeException: Incorrect nodes count for selectOther: B:48:0x007d in [B:26:0x0070, B:48:0x007d, B:46:0x0021]
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
        r12 = this;
        r10 = 0;
        r9 = 1;
        super.readFromParcel(r13);	 Catch:{ all -> 0x0060 }
        r8 = ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder.class;
        r0 = r8.getClassLoader();
        r3 = r13.readInt();
        r4 = r3;
    L_0x0010:
        r3 = r4 + -1;
        if (r4 <= 0) goto L_0x0021;
    L_0x0014:
        r2 = r13.readParcelable(r0);
        r2 = (ru.ok.model.photo.PhotoSize) r2;
        r8 = r12.pics;
        r8.add(r2);
        r4 = r3;
        goto L_0x0010;
    L_0x0021:
        r6 = r13.readInt();
        r7 = r6;
    L_0x0026:
        r6 = r7 + -1;
        if (r7 <= 0) goto L_0x0037;
    L_0x002a:
        r5 = r13.readParcelable(r0);
        r5 = (ru.ok.model.photo.PhotoSize) r5;
        r8 = r12.sprites;
        r8.add(r5);
        r7 = r6;
        goto L_0x0026;
    L_0x0037:
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x005a;
    L_0x003d:
        r1 = r9;
    L_0x003e:
        if (r1 == 0) goto L_0x0048;
    L_0x0040:
        r8 = r13.readParcelable(r0);
        r8 = (ru.ok.model.presents.AnimationProperties) r8;
        r12.animationProperties = r8;
    L_0x0048:
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x005c;
    L_0x004e:
        r8 = r9;
    L_0x004f:
        r12.isAnimated = r8;
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x005e;
    L_0x0057:
        r12.isLive = r9;
        return r12;
    L_0x005a:
        r1 = r10;
        goto L_0x003e;
    L_0x005c:
        r8 = r10;
        goto L_0x004f;
    L_0x005e:
        r9 = r10;
        goto L_0x0057;
    L_0x0060:
        r11 = move-exception;
        r8 = ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder.class;
        r0 = r8.getClassLoader();
        r3 = r13.readInt();
        r4 = r3;
    L_0x006c:
        r3 = r4 + -1;
        if (r4 <= 0) goto L_0x007d;
    L_0x0070:
        r2 = r13.readParcelable(r0);
        r2 = (ru.ok.model.photo.PhotoSize) r2;
        r8 = r12.pics;
        r8.add(r2);
        r4 = r3;
        goto L_0x006c;
    L_0x007d:
        r6 = r13.readInt();
        r7 = r6;
    L_0x0082:
        r6 = r7 + -1;
        if (r7 <= 0) goto L_0x0093;
    L_0x0086:
        r5 = r13.readParcelable(r0);
        r5 = (ru.ok.model.photo.PhotoSize) r5;
        r8 = r12.sprites;
        r8.add(r5);
        r7 = r6;
        goto L_0x0082;
    L_0x0093:
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x00b6;
    L_0x0099:
        r1 = r9;
    L_0x009a:
        if (r1 == 0) goto L_0x00a4;
    L_0x009c:
        r8 = r13.readParcelable(r0);
        r8 = (ru.ok.model.presents.AnimationProperties) r8;
        r12.animationProperties = r8;
    L_0x00a4:
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x00b8;
    L_0x00aa:
        r8 = r9;
    L_0x00ab:
        r12.isAnimated = r8;
        r8 = r13.readInt();
        if (r8 != r9) goto L_0x00ba;
    L_0x00b3:
        r12.isLive = r9;
        throw r11;
    L_0x00b6:
        r1 = r10;
        goto L_0x009a;
    L_0x00b8:
        r8 = r10;
        goto L_0x00ab;
    L_0x00ba:
        r9 = r10;
        goto L_0x00b3;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder.readFromParcel(android.os.Parcel):ru.ok.model.stream.entities.FeedPresentTypeEntityBuilder");
    }

    public FeedPresentTypeEntityBuilder withAnimated(boolean isAnimated) {
        this.isAnimated = isAnimated;
        return this;
    }

    public FeedPresentTypeEntityBuilder withAnimationProperties(AnimationProperties animationProperties) {
        this.animationProperties = animationProperties;
        return this;
    }

    public FeedPresentTypeEntityBuilder addPic(PhotoSize pic) {
        if (pic != null) {
            this.pics.add(pic);
        }
        return this;
    }

    public FeedPresentTypeEntityBuilder addSprite(PhotoSize pic) {
        if (this.sprites != null) {
            this.sprites.add(pic);
        }
        return this;
    }

    public boolean isAnimated() {
        return this.isAnimated;
    }

    public boolean hasPics() {
        return (this.pics == null || this.pics.isEmpty()) ? false : true;
    }

    public boolean hasSprites() {
        return (this.sprites == null || this.sprites.isEmpty()) ? false : true;
    }

    public void setLive(boolean isLive) {
        this.isLive = isLive;
    }

    protected FeedPresentTypeEntity doPreBuild() {
        return new FeedPresentTypeEntity(getId(), this.pics, this.sprites, getLikeInfo(), this.animationProperties, this.isAnimated, this.isLive);
    }

    public void getRefs(List<String> list) {
    }

    public void writeToParcel(Parcel dest, int flags) {
        int i;
        int i2 = 1;
        super.writeToParcel(dest, flags);
        dest.writeInt(this.pics.size());
        Iterator i$ = this.pics.iterator();
        while (i$.hasNext()) {
            dest.writeParcelable((PhotoSize) i$.next(), flags);
        }
        dest.writeInt(this.sprites.size());
        i$ = this.sprites.iterator();
        while (i$.hasNext()) {
            dest.writeParcelable((PhotoSize) i$.next(), flags);
        }
        dest.writeInt(this.animationProperties != null ? 1 : 0);
        if (this.animationProperties != null) {
            dest.writeParcelable(this.animationProperties, 0);
        }
        if (this.isAnimated) {
            i = 1;
        } else {
            i = 0;
        }
        dest.writeInt(i);
        if (!this.isLive) {
            i2 = 0;
        }
        dest.writeInt(i2);
    }

    public FeedPresentTypeEntityBuilder() {
        super(21);
        this.pics = new TreeSet();
        this.sprites = new TreeSet();
    }

    static {
        CREATOR = new C16281();
    }
}
