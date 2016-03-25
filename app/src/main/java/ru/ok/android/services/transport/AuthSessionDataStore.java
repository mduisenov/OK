package ru.ok.android.services.transport;

import android.content.Context;
import android.net.Uri;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import ru.ok.android.graylog.GrayLog;
import ru.ok.android.onelog.OneLog;
import ru.ok.android.utils.ConfigurationPreferences;
import ru.ok.android.utils.IOUtils;
import ru.ok.android.utils.Logger;
import ru.ok.java.api.ServiceStateHolder;
import ru.ok.model.events.EventsGsonUtils;

public class AuthSessionDataStore {
    private static Gson gson;

    /* renamed from: ru.ok.android.services.transport.AuthSessionDataStore.1 */
    static class C05191 implements InstanceCreator<ServiceStateHolder> {
        C05191() {
        }

        public ServiceStateHolder createInstance(Type type) {
            return AuthSessionDataStore.createDefaultServiceStateHolder();
        }
    }

    public static synchronized ServiceStateHolder getDefault(Context context) {
        ServiceStateHolder data;
        synchronized (AuthSessionDataStore.class) {
            data = readFromFile(getDefaultFile(context));
            if (data == null) {
                Logger.m172d("Try to migrate data from legacy store...");
                data = tryReadFromLegacyStore(context);
            }
            if (data == null) {
                Logger.m172d("No data available, create default");
                data = createDefaultServiceStateHolder();
            }
        }
        return data;
    }

    public static synchronized boolean saveDefault(Context context, ServiceStateHolder data) {
        boolean saveToFile;
        synchronized (AuthSessionDataStore.class) {
            OneLog.attachBaseUrl(Uri.parse(data.getBaseUrl()));
            OneLog.attachApplicationKey(data.getAppKey(), data.getSecretAppKey());
            OneLog.attachSessionKey(data.getSessionKey(), data.getSecretSessionKey());
            GrayLog.attachBaseUrl(Uri.parse(data.getBaseUrl()));
            GrayLog.attachApplicationKey(data.getAppKey(), data.getSecretAppKey());
            GrayLog.attachSessionKey(data.getSessionKey(), data.getSecretSessionKey());
            saveToFile = saveToFile(data, getDefaultFile(context));
        }
        return saveToFile;
    }

    public static synchronized void clearDefault(Context context) {
        synchronized (AuthSessionDataStore.class) {
            delete(getDefaultFile(context));
        }
    }

    private static ServiceStateHolder readFromFile(File file) {
        Exception e;
        Throwable th;
        if (file.exists()) {
            Logger.m173d("Reading from file %s...", file);
            Closeable in = null;
            try {
                Closeable in2 = new JsonReader(new FileReader(file));
                try {
                    Logger.m173d("Read from file: %s", (ServiceStateHolder) gson.fromJson((JsonReader) in2, (Type) ServiceStateHolder.class));
                    IOUtils.closeSilently(in2);
                    return data;
                } catch (Exception e2) {
                    e = e2;
                    in = in2;
                    try {
                        Logger.m180e(e, "Error reading session file %s", file);
                        delete(file);
                        IOUtils.closeSilently(in);
                        Logger.m185w("Failed to read from file %s", file);
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        IOUtils.closeSilently(in);
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    in = in2;
                    IOUtils.closeSilently(in);
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
                Logger.m180e(e, "Error reading session file %s", file);
                delete(file);
                IOUtils.closeSilently(in);
                Logger.m185w("Failed to read from file %s", file);
                return null;
            }
        }
        Logger.m185w("File %s doesn't exist", file);
        return null;
    }

    private static boolean saveToFile(ServiceStateHolder data, File file) {
        Exception e;
        Throwable th;
        Logger.m173d("Saving service state to file: %s", file);
        Closeable out = null;
        try {
            Closeable out2 = new JsonWriter(new FileWriter(file));
            try {
                out2.setIndent("  ");
                EventsGsonUtils.gson.toJson((Object) data, (Type) ServiceStateHolder.class, (JsonWriter) out2);
                Logger.m173d("Saved service state: %s", data);
                IOUtils.closeSilently(out2);
                out = out2;
                return true;
            } catch (Exception e2) {
                e = e2;
                out = out2;
                try {
                    Logger.m180e(e, "Error writing to file: %s", file);
                    IOUtils.closeSilently(out);
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    IOUtils.closeSilently(out);
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                IOUtils.closeSilently(out);
                throw th;
            }
        } catch (Exception e3) {
            e = e3;
            Logger.m180e(e, "Error writing to file: %s", file);
            IOUtils.closeSilently(out);
            return false;
        }
    }

    private static ServiceStateHolder tryReadFromLegacyStore(Context context) {
        ServiceStateHolder data = createDefaultServiceStateHolder();
        if (LegacyAuthSessionDataStore.legacyReadFromPreference(context, data)) {
            data.clearSession();
            if (saveDefault(context, data)) {
                LegacyAuthSessionDataStore.clearLegacyStorage(context);
                return data;
            }
        }
        return null;
    }

    private static void delete(File file) {
        try {
            file.delete();
        } catch (Throwable e) {
            Logger.m179e(e, "Failed to delete file: " + file);
        }
    }

    private static File getDefaultFile(Context context) {
        return new File(context.getFilesDir(), "session_data.json");
    }

    static {
        gson = createGson();
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ServiceStateHolder.class, new C05191());
        return builder.create();
    }

    private static ServiceStateHolder createDefaultServiceStateHolder() {
        ConfigurationPreferences cp = ConfigurationPreferences.getInstance();
        Logger.m173d("%s", cp);
        return new ServiceStateHolder(cp.getAppKey(), cp.getAppSecretKey(), cp.getApiAddress());
    }
}
