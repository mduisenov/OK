package ru.ok.android.ui.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BannerAppRatingDrawer {
    private Canvas canvas;
    private Rect clipRect;
    private Bitmap orangeStar;
    private Bitmap partialStarBitmap;
    private float rating;
    private ViewGroup starsHolder;

    public BannerAppRatingDrawer(@NonNull ViewGroup starsHolder) {
        this.rating = -1.0f;
        this.canvas = new Canvas();
        this.clipRect = new Rect();
        this.starsHolder = starsHolder;
        this.orangeStar = BitmapFactory.decodeResource(this.starsHolder.getResources(), 2130838042);
        this.partialStarBitmap = Bitmap.createBitmap(this.orangeStar.getWidth(), this.orangeStar.getHeight(), this.orangeStar.getConfig());
        this.canvas.setBitmap(this.partialStarBitmap);
    }

    public void setRating(float rating) {
        if (this.rating != rating) {
            this.rating = rating;
            applyRating(rating);
        }
    }

    private void applyRating(float rating) {
        int count = this.starsHolder.getChildCount();
        for (int i = 1; i <= count; i++) {
            ImageView imageView = (ImageView) this.starsHolder.getChildAt(i - 1);
            if (((float) i) < rating) {
                imageView.setImageResource(2130838042);
            } else if (((float) i) - rating < 1.0f) {
                drawPartialStar(imageView, ((float) i) - rating);
            } else {
                imageView.setImageResource(2130838041);
            }
        }
    }

    private void drawPartialStar(ImageView imageView, float ratio) {
        this.partialStarBitmap.eraseColor(0);
        fillClipRect(ratio);
        this.canvas.drawBitmap(this.orangeStar, this.clipRect, this.clipRect, null);
        imageView.setImageBitmap(this.partialStarBitmap);
    }

    private void fillClipRect(float ratio) {
        int rectRight = (int) (((float) this.orangeStar.getWidth()) * ratio);
        Rect rect = this.clipRect;
        this.clipRect.top = 0;
        rect.left = 0;
        this.clipRect.bottom = this.orangeStar.getHeight();
        this.clipRect.right = rectRight;
    }
}
