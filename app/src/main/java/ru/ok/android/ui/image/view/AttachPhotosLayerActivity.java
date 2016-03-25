package ru.ok.android.ui.image.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import ru.ok.android.onelog.PhotoLayerLogger.PageScrollLogListener;
import ru.ok.android.ui.adapters.photo.AttachPhotoLayerAdapter;
import ru.ok.android.ui.adapters.photo.PhotoLayerAdapter;
import ru.ok.android.ui.image.PreviewDataHolder;
import ru.ok.android.utils.localization.LocalizationManager;
import ru.ok.model.Identifiable;
import ru.ok.model.messages.Attachment;

public class AttachPhotosLayerActivity extends PhotoLayerActivity {
    private ArrayList<Attachment> attachments;
    protected AttachPhotoLayerAdapter mImagesPagerAdapter;

    /* renamed from: ru.ok.android.ui.image.view.AttachPhotosLayerActivity.1 */
    class C10011 extends SimpleOnPageChangeListener {
        private final PageScrollLogListener scrollLogger;

        C10011() {
            this.scrollLogger = new PageScrollLogListener(AttachPhotosLayerActivity.this.photoLayerLogger);
        }

        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            this.scrollLogger.onPageScrollStateChanged(state);
        }

        public void onPageSelected(int position) {
            this.scrollLogger.onPageSelected(position);
            AttachPhotosLayerActivity.this.onPhotoSelected(AttachPhotosLayerActivity.this.mImagesPagerAdapter.getRealPosition(position));
        }
    }

    public AttachPhotosLayerActivity() {
        this.attachments = new ArrayList();
    }

    protected void onCreateLocalized(Bundle savedInstanceState) {
        super.onCreateLocalized(savedInstanceState);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        if (savedInstanceState == null) {
            this.attachments = getIntent().getParcelableArrayListExtra("attachments");
        } else {
            this.attachments = savedInstanceState.getParcelableArrayList("attachments");
        }
        startAnimation(savedInstanceState);
    }

    protected PhotoLayerAdapter getViewImagesAdapter() {
        return this.mImagesPagerAdapter;
    }

    protected void doPreparePager() {
        getPagerView().setOnPageChangeListener(new C10011());
        super.doPreparePager();
    }

    protected void onAnimationNotExists(int realPosition) {
        preparePager();
        onPagerDataUpdated(realPosition, false);
    }

    protected void onAnimationEnd(Uri previewUri) {
        ((Attachment) this.attachments.get(getInitialRealPosition())).setPreviewUri(previewUri);
        resetAfterAnimation();
    }

    protected Attachment getIdentifiableFromIntent() {
        return (Attachment) getIntent().getParcelableExtra("selected");
    }

    protected int getInitialRealPosition() {
        Attachment attachment = getIdentifiableFromIntent();
        if (attachment == null) {
            return 0;
        }
        for (int i = 0; i < this.attachments.size(); i++) {
            if (attachment.equals((Attachment) this.attachments.get(i))) {
                return i;
            }
        }
        return 0;
    }

    protected final void resetAfterAnimation() {
        preparePager();
        onPagerDataUpdated(getInitialRealPosition(), false);
        clearAfterAnimation();
    }

    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("attachments", this.attachments);
        super.onSaveInstanceState(outState);
    }

    protected PhotoLayerAdapter createViewImageAdapter(@NonNull DecorHandler decorHandler, @NonNull ProgressSyncHelper syncHelper, @Nullable PreviewDataHolder previewDataHolder) {
        this.mImagesPagerAdapter = new AttachPhotoLayerAdapter(this, decorHandler, this.attachments, syncHelper, previewDataHolder);
        return this.mImagesPagerAdapter;
    }

    protected void onPhotoSelected(int realPosition) {
        setRealPositionBuffer(realPosition);
        supportInvalidateOptionsMenu();
        notifyPhotoSelected((Identifiable) this.attachments.get(realPosition));
    }

    protected final void notifyPhotoSelected(Identifiable identifiable) {
    }

    protected String getCurrentPhotoId() {
        return String.valueOf(((Attachment) this.attachments.get(getCurrentRealPosition()))._id);
    }

    public void finish() {
        super.finish();
        notifyPhotoSelected(null);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        LocalizationManager.inflate((Context) this, getMenuInflater(), 2131689475, menu);
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        setSupportProgressBarIndeterminateVisibility(false);
        int realPosition = getCurrentRealPosition();
        if (realPosition >= 0 && realPosition < this.attachments.size() && ((Attachment) this.attachments.get(realPosition)) != null) {
            MenuItem item = menu.findItem(2131624799);
            if (item != null) {
                item.setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Attachment attachment = (Attachment) this.attachments.get(getCurrentRealPosition());
        if (attachment == null) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case 2131624799:
                saveAttachment(attachment);
                this.photoLayerLogger.logClickSave();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveAttachment(@NonNull Attachment attachment) {
        String url = null;
        String fileToSaveExtension = null;
        if (attachment.hasGif()) {
            url = attachment.gifUrl;
            fileToSaveExtension = "gif";
        } else if (attachment.getLargestSize() != null) {
            url = attachment.getLargestSize().getUrl();
            fileToSaveExtension = "jpg";
        }
        if (url != null) {
            ViewPhotosOptionsMenuHelper.savePhotoToFile(this, url, fileToSaveExtension, getCurrentRealPosition());
        }
    }
}
