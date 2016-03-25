package ru.ok.android.services.app.remote;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import java.lang.reflect.Method;

public class RemoteControlClientCompat {
    private static boolean sHasRemoteControlAPIs;
    private static Method sRCCEditMetadataMethod;
    private static Method sRCCSetPlayStateMethod;
    private static Method sRCCSetTransportControlFlags;
    private static Class sRemoteControlClientClass;
    private Object mActualRemoteControlClient;

    public class MetadataEditorCompat {
        private Object mActualMetadataEditor;
        private Method mApplyMethod;
        private Method mClearMethod;
        private Method mPutBitmapMethod;
        private Method mPutLongMethod;
        private Method mPutStringMethod;

        private MetadataEditorCompat(Object actualMetadataEditor) {
            if (RemoteControlClientCompat.sHasRemoteControlAPIs && actualMetadataEditor == null) {
                throw new IllegalArgumentException("Remote Control API's exist, should not be given a null MetadataEditor");
            }
            if (RemoteControlClientCompat.sHasRemoteControlAPIs) {
                Class metadataEditorClass = actualMetadataEditor.getClass();
                try {
                    this.mPutStringMethod = metadataEditorClass.getMethod("putString", new Class[]{Integer.TYPE, String.class});
                    this.mPutBitmapMethod = metadataEditorClass.getMethod("putBitmap", new Class[]{Integer.TYPE, Bitmap.class});
                    this.mPutLongMethod = metadataEditorClass.getMethod("putLong", new Class[]{Integer.TYPE, Long.TYPE});
                    this.mClearMethod = metadataEditorClass.getMethod("clear", new Class[0]);
                    this.mApplyMethod = metadataEditorClass.getMethod("apply", new Class[0]);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            this.mActualMetadataEditor = actualMetadataEditor;
        }

        public MetadataEditorCompat putString(int key, String value) {
            if (RemoteControlClientCompat.sHasRemoteControlAPIs) {
                try {
                    this.mPutStringMethod.invoke(this.mActualMetadataEditor, new Object[]{Integer.valueOf(key), value});
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return this;
        }

        public MetadataEditorCompat putBitmap(int key, Bitmap bitmap) {
            if (RemoteControlClientCompat.sHasRemoteControlAPIs) {
                try {
                    this.mPutBitmapMethod.invoke(this.mActualMetadataEditor, new Object[]{Integer.valueOf(key), bitmap});
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return this;
        }

        public MetadataEditorCompat putLong(int key, long value) {
            if (RemoteControlClientCompat.sHasRemoteControlAPIs) {
                try {
                    this.mPutLongMethod.invoke(this.mActualMetadataEditor, new Object[]{Integer.valueOf(key), Long.valueOf(value)});
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
            return this;
        }

        public void apply() {
            if (RemoteControlClientCompat.sHasRemoteControlAPIs) {
                try {
                    this.mApplyMethod.invoke(this.mActualMetadataEditor, (Object[]) null);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static {
        /*
        r8 = 0;
        sHasRemoteControlAPIs = r8;
        r8 = ru.ok.android.services.app.remote.RemoteControlClientCompat.class;
        r1 = r8.getClassLoader();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = getActualRemoteControlClientClass(r1);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        sRemoteControlClientClass = r8;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = ru.ok.android.services.app.remote.RemoteControlClientCompat.class;
        r0 = r8.getFields();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r5 = r0.length;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r4 = 0;
    L_0x0017:
        if (r4 >= r5) goto L_0x00b6;
    L_0x0019:
        r3 = r0[r4];	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = sRemoteControlClientClass;	 Catch:{ NoSuchFieldException -> 0x0031, IllegalArgumentException -> 0x0053, IllegalAccessException -> 0x0084, ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, SecurityException -> 0x00f4 }
        r9 = r3.getName();	 Catch:{ NoSuchFieldException -> 0x0031, IllegalArgumentException -> 0x0053, IllegalAccessException -> 0x0084, ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, SecurityException -> 0x00f4 }
        r6 = r8.getField(r9);	 Catch:{ NoSuchFieldException -> 0x0031, IllegalArgumentException -> 0x0053, IllegalAccessException -> 0x0084, ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, SecurityException -> 0x00f4 }
        r8 = 0;
        r7 = r6.get(r8);	 Catch:{ NoSuchFieldException -> 0x0031, IllegalArgumentException -> 0x0053, IllegalAccessException -> 0x0084, ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, SecurityException -> 0x00f4 }
        r8 = 0;
        r3.set(r8, r7);	 Catch:{ NoSuchFieldException -> 0x0031, IllegalArgumentException -> 0x0053, IllegalAccessException -> 0x0084, ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, SecurityException -> 0x00f4 }
    L_0x002e:
        r4 = r4 + 1;
        goto L_0x0017;
    L_0x0031:
        r2 = move-exception;
        r8 = "RemoteControlCompat";
        r9 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9.<init>();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = "Could not get real field: ";
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = r3.getName();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.toString();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        android.util.Log.w(r8, r9);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        goto L_0x002e;
    L_0x0051:
        r8 = move-exception;
    L_0x0052:
        return;
    L_0x0053:
        r2 = move-exception;
        r8 = "RemoteControlCompat";
        r9 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9.<init>();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = "Error trying to pull field value for: ";
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = r3.getName();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = " ";
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = r2.getMessage();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.toString();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        android.util.Log.w(r8, r9);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        goto L_0x002e;
    L_0x0082:
        r8 = move-exception;
        goto L_0x0052;
    L_0x0084:
        r2 = move-exception;
        r8 = "RemoteControlCompat";
        r9 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9.<init>();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = "Error trying to pull field value for: ";
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = r3.getName();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = " ";
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10 = r2.getMessage();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.append(r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = r9.toString();	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        android.util.Log.w(r8, r9);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        goto L_0x002e;
    L_0x00b4:
        r8 = move-exception;
        goto L_0x0052;
    L_0x00b6:
        r8 = sRemoteControlClientClass;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = "editMetadata";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r11 = 0;
        r12 = java.lang.Boolean.TYPE;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10[r11] = r12;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = r8.getMethod(r9, r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        sRCCEditMetadataMethod = r8;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = sRemoteControlClientClass;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = "setPlaybackState";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r11 = 0;
        r12 = java.lang.Integer.TYPE;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10[r11] = r12;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = r8.getMethod(r9, r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        sRCCSetPlayStateMethod = r8;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = sRemoteControlClientClass;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r9 = "setTransportControlFlags";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r11 = 0;
        r12 = java.lang.Integer.TYPE;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r10[r11] = r12;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = r8.getMethod(r9, r10);	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        sRCCSetTransportControlFlags = r8;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        r8 = 1;
        sHasRemoteControlAPIs = r8;	 Catch:{ ClassNotFoundException -> 0x0051, NoSuchMethodException -> 0x0082, IllegalArgumentException -> 0x00b4, SecurityException -> 0x00f4 }
        goto L_0x0052;
    L_0x00f4:
        r8 = move-exception;
        goto L_0x0052;
        */
        throw new UnsupportedOperationException("Method not decompiled: ru.ok.android.services.app.remote.RemoteControlClientCompat.<clinit>():void");
    }

    public static Class getActualRemoteControlClientClass(ClassLoader classLoader) throws ClassNotFoundException {
        return classLoader.loadClass("android.media.RemoteControlClient");
    }

    public RemoteControlClientCompat(PendingIntent pendingIntent) {
        if (sHasRemoteControlAPIs) {
            try {
                this.mActualRemoteControlClient = sRemoteControlClientClass.getConstructor(new Class[]{PendingIntent.class}).newInstance(new Object[]{pendingIntent});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public MetadataEditorCompat editMetadata(boolean startEmpty) {
        Object invoke;
        if (sHasRemoteControlAPIs) {
            try {
                invoke = sRCCEditMetadataMethod.invoke(this.mActualRemoteControlClient, new Object[]{Boolean.valueOf(startEmpty)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        invoke = null;
        return new MetadataEditorCompat(invoke, null);
    }

    public void setPlaybackState(int state) {
        if (sHasRemoteControlAPIs) {
            try {
                sRCCSetPlayStateMethod.invoke(this.mActualRemoteControlClient, new Object[]{Integer.valueOf(state)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setTransportControlFlags(int transportControlFlags) {
        if (sHasRemoteControlAPIs) {
            try {
                sRCCSetTransportControlFlags.invoke(this.mActualRemoteControlClient, new Object[]{Integer.valueOf(transportControlFlags)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public final Object getActualRemoteControlClientObject() {
        return this.mActualRemoteControlClient;
    }
}
