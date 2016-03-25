package ru.ok.android.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import ru.ok.android.utils.FileUtils;
import ru.ok.android.utils.FileUtils.IOProgressCallbalck;
import ru.ok.android.utils.InputStreamHolder;
import ru.ok.android.utils.Logger;
import ru.ok.android.utils.MimeTypeFromFileSignatureResolver;

public class SaveToFileFragment extends Fragment {
    private File[] destFiles;
    private InputStreamHolder[] inputStreamHolders;
    private boolean isFinished;
    private boolean isResultDelivered;
    private boolean isSuccessful;
    private SaveToFileFragmentListener listener;
    private SaveToFileAsyncTask taskInProgress;

    public interface SaveToFileFragmentListener {
        void onSaveToFileFinished(SaveToFileFragment saveToFileFragment, boolean z, Bundle bundle);
    }

    class SaveToFileAsyncTask extends AsyncTask<Void, Long, Boolean> implements IOProgressCallbalck {
        private final Context context;
        private long currentFileBytes;
        private long totalBytes;

        /* renamed from: ru.ok.android.ui.fragments.SaveToFileFragment.SaveToFileAsyncTask.1 */
        class C08121 implements Runnable {
            C08121() {
            }

            public void run() {
                Logger.m172d(">>> cleaning files...");
                for (File destFile : SaveToFileFragment.this.destFiles) {
                    if (destFile.exists()) {
                        boolean deletedOk;
                        Logger.m173d("deleting file: %s", destFile);
                        try {
                            deletedOk = destFile.delete();
                        } catch (Throwable e) {
                            Logger.m179e(e, "Error while deleting file: " + destFile);
                            deletedOk = false;
                        }
                        if (!deletedOk) {
                            Logger.m185w("Failed to delete file: %s", destFile);
                        }
                    }
                }
                Logger.m172d("<<< finished cleaning files");
            }
        }

        protected java.lang.Boolean doInBackground(java.lang.Void... r15) {
            /* JADX: method processing error */
/*
            Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:? in {7, 12, 14, 17, 18, 19, 20, 21, 22, 23, 24, 26, 27} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
*/
            /*
            r14 = this;
            r8 = 1;
            r9 = 0;
            r10 = ru.ok.android.ui.fragments.SaveToFileFragment.this;
            r10 = r10.inputStreamHolders;
            r0 = r10.length;
            r3 = 0;
            r4 = 0;
        L_0x000b:
            if (r4 >= r0) goto L_0x007f;
        L_0x000d:
            r10 = r14.isCancelled();
            if (r10 != 0) goto L_0x007f;
        L_0x0013:
            r10 = ru.ok.android.ui.fragments.SaveToFileFragment.this;
            r10 = r10.inputStreamHolders;
            r6 = r10[r4];
            r10 = ru.ok.android.ui.fragments.SaveToFileFragment.this;
            r10 = r10.destFiles;
            r1 = r10[r4];
            r10 = "Start copying file #%d from %s to %s";
            r11 = 3;
            r11 = new java.lang.Object[r11];
            r12 = java.lang.Integer.valueOf(r4);
            r11[r9] = r12;
            r11[r8] = r6;
            r12 = 2;
            r11[r12] = r1;
            ru.ok.android.utils.Logger.m173d(r10, r11);
            r7 = 0;
            r10 = r14.context;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = r10.getContentResolver();	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r5 = r6.open(r10);	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r7 = r14.toMarkSupportedStream(r5);	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r1 = r14.changeFileExtensionFromMimeTypeIfNecessary(r7, r1);	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = ru.ok.android.ui.fragments.SaveToFileFragment.this;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = r10.destFiles;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10[r4] = r1;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = 102400; // 0x19000 float:1.43493E-40 double:5.05923E-319;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            ru.ok.android.utils.FileUtils.copyToFile(r7, r1, r14, r10);	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = r14.totalBytes;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r12 = r14.currentFileBytes;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r10 = r10 + r12;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r14.totalBytes = r10;	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            ru.ok.android.utils.IOUtils.closeSilently(r6);
            ru.ok.android.utils.IOUtils.closeSilently(r7);
        L_0x0065:
            r4 = r4 + 1;
            goto L_0x000b;
        L_0x0068:
            r2 = move-exception;
            r10 = "Failed to save URI to file";	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            ru.ok.android.utils.Logger.m179e(r2, r10);	 Catch:{ Exception -> 0x0068, all -> 0x0077 }
            r3 = 1;
            ru.ok.android.utils.IOUtils.closeSilently(r6);
            ru.ok.android.utils.IOUtils.closeSilently(r7);
            goto L_0x0065;
        L_0x0077:
            r8 = move-exception;
            ru.ok.android.utils.IOUtils.closeSilently(r6);
            ru.ok.android.utils.IOUtils.closeSilently(r7);
            throw r8;
        L_0x007f:
            if (r3 != 0) goto L_0x0086;
        L_0x0081:
            r8 = java.lang.Boolean.valueOf(r8);
            return r8;
        L_0x0086:
            r8 = r9;
            goto L_0x0081;
            */
            throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.ui.fragments.SaveToFileFragment.SaveToFileAsyncTask.doInBackground(java.lang.Void[]):java.lang.Boolean");
        }

        SaveToFileAsyncTask(Context context) {
            this.context = context;
        }

        @NonNull
        private InputStream toMarkSupportedStream(@NonNull InputStream is) {
            return is.markSupported() ? is : new BufferedInputStream(is);
        }

        @NonNull
        private File changeFileExtensionFromMimeTypeIfNecessary(@NonNull InputStream is, @NonNull File destFile) {
            if (!TextUtils.isEmpty(FileUtils.getFileExtension(destFile.getName()))) {
                return destFile;
            }
            try {
                String mimeType = MimeTypeFromFileSignatureResolver.resolveMimeTypeFromStream(is);
                if (mimeType == null) {
                    Logger.m177e("Failed to resolve mime type from input stream for the file (%s)", destFile);
                    return destFile;
                }
                String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (TextUtils.isEmpty(ext)) {
                    Logger.m177e("No file extension mapping exists for the file (%s) and mimeType (%s)", destFile, mimeType);
                    return destFile;
                }
                File newDestFile = new File(destFile.getAbsolutePath() + '.' + ext);
                if (!destFile.exists()) {
                    return newDestFile;
                }
                if (destFile.renameTo(newDestFile)) {
                    return destFile;
                }
                Logger.m177e("Failed to rename file (%s) to to a new one (%s)", destFile, newDestFile);
                return destFile;
            } catch (IOException e) {
                Logger.m180e(e, "Failed to find the file (%s) mime type and change its extension", destFile);
                return destFile;
            }
        }

        public void onIOProgress(long totalBytesProcessed) {
            this.currentFileBytes = totalBytesProcessed;
            publishProgress(new Long[]{Long.valueOf(this.totalBytes + totalBytesProcessed)});
        }

        protected void onProgressUpdate(Long... values) {
            long latestProgress = (values == null || values.length == 0) ? 0 : values[values.length - 1].longValue();
            Logger.m173d("progress: %d bytes", Long.valueOf(latestProgress));
        }

        protected void onPostExecute(Boolean isSuccessful) {
            Logger.m173d("isSuccessful=%s", isSuccessful);
            SaveToFileFragment.this.taskInProgress = null;
            SaveToFileFragment.this.isFinished = true;
            SaveToFileFragment.this.isSuccessful = isSuccessful.booleanValue();
            if (SaveToFileFragment.this.isResumed()) {
                SaveToFileFragment.this.deliverResult();
            }
        }

        protected void onCancelled() {
            Logger.m172d("");
            THREAD_POOL_EXECUTOR.execute(new C08121());
        }
    }

    public SaveToFileFragment() {
        this.isFinished = false;
        this.isSuccessful = false;
        this.isResultDelivered = false;
    }

    public static SaveToFileFragment newInstance(@NonNull InputStreamHolder[] ihs, @NonNull File[] destFiles, @Nullable Bundle additionalArgs) {
        Bundle args = new Bundle();
        args.putParcelableArray("src_ihs", (Parcelable[]) Arrays.copyOf(ihs, ihs.length, Parcelable[].class));
        args.putSerializable("dest_files", destFiles);
        args.putBundle("additional_args", additionalArgs);
        SaveToFileFragment fragment = new SaveToFileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public Bundle getAdditionalArgs() {
        return getArguments().getBundle("additional_args");
    }

    public void setListener(SaveToFileFragmentListener listener) {
        this.listener = listener;
    }

    public boolean isFinished() {
        return this.isFinished;
    }

    public boolean isResultDelivered() {
        return this.isResultDelivered;
    }

    public File getDestFile(int index) {
        if (index < 0 || index >= this.destFiles.length) {
            return null;
        }
        return this.destFiles[index];
    }

    public File[] getDestFiles() {
        return this.destFiles;
    }

    public void deliverResult() {
        if (this.listener != null) {
            this.isResultDelivered = true;
            this.listener.onSaveToFileFinished(this, this.isSuccessful, getAdditionalArgs());
        }
    }

    public void abort() {
        Logger.m172d("");
        if (this.taskInProgress != null) {
            this.taskInProgress.cancel(true);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initState();
        if (isDataValid()) {
            startSaveToFile();
            return;
        }
        String str = "Invalid args: srcUri=%s, destFile=%s";
        Object[] objArr = new Object[2];
        objArr[0] = Logger.isLoggingEnable() ? Arrays.toString(this.inputStreamHolders) : this.inputStreamHolders;
        objArr[1] = Logger.isLoggingEnable() ? Arrays.toString(this.destFiles) : this.destFiles;
        Logger.m177e(str, objArr);
    }

    private void initState() {
        Parcelable[] parcelablesIsh;
        File[] fileArr = null;
        Bundle args = getArguments();
        if (args != null) {
            parcelablesIsh = args.getParcelableArray("src_ihs");
        } else {
            parcelablesIsh = null;
        }
        this.inputStreamHolders = parcelablesIsh == null ? null : (InputStreamHolder[]) Arrays.copyOf(parcelablesIsh, parcelablesIsh.length, InputStreamHolder[].class);
        Object[] filesObjects = args == null ? null : (Object[]) args.getSerializable("dest_files");
        if (filesObjects != null) {
            fileArr = (File[]) Arrays.copyOf(filesObjects, filesObjects.length, File[].class);
        }
        this.destFiles = fileArr;
    }

    private boolean isDataValid() {
        return this.inputStreamHolders != null && this.destFiles != null && this.inputStreamHolders.length > 0 && this.inputStreamHolders.length == this.destFiles.length;
    }

    private void startSaveToFile() {
        Activity activity = getActivity();
        if (activity != null) {
            this.taskInProgress = new SaveToFileAsyncTask(activity);
            this.taskInProgress.execute(new Void[0]);
        }
    }
}
