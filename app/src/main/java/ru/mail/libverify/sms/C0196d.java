package ru.mail.libverify.sms;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.address.packet.MultipleAddresses.Address;
import ru.mail.libverify.api.C0181o;
import ru.mail.libverify.sms.d.1;
import ru.mail.libverify.sms.d.2;
import ru.mail.libverify.sms.d.a;
import ru.mail.libverify.sms.d.b;
import ru.mail.libverify.sms.d.c;
import ru.mail.libverify.sms.d.i;
import ru.mail.libverify.sms.d.k;
import ru.mail.libverify.sms.d.l;
import ru.mail.libverify.sms.d.m;
import ru.mail.libverify.utils.C0203c;
import ru.mail.libverify.utils.C0204d;

/* renamed from: ru.mail.libverify.sms.d */
public final class C0196d {
    public static final Pattern f34a;
    private static final Pattern f35b;
    private Map<i, List<m>> f36c;
    private final ContentResolver f37d;
    private final C0181o f38e;
    private final Context f39f;
    private long f40g;
    private String[] f41h;
    private String f42i;
    private LongSparseArray<c> f43j;

    static {
        f34a = Pattern.compile(".*", 32);
        f35b = Pattern.compile("content://sms/[0-9]+");
    }

    public C0196d(@NonNull Context context, @NonNull C0181o c0181o) {
        int i = 1;
        this.f36c = new LinkedHashMap();
        this.f41h = new String[]{"_id", "type", Address.ELEMENT, Message.BODY};
        this.f42i = "_id ASC";
        this.f43j = new a();
        this.f38e = c0181o;
        this.f39f = context;
        this.f37d = context.getContentResolver();
        if (ContextCompat.checkSelfPermission(this.f39f, "android.permission.READ_SMS") != 0) {
            String str = "Sms";
            C0204d.m131a(str, "can't init SmsManager without %s", "android.permission.READ_SMS");
        } else {
            i = 0;
        }
        if (i == 0) {
            m105d();
            m99b();
            try {
                this.f37d.registerContentObserver(a.a, true, new l(this, new Handler()));
            } catch (Throwable e) {
                C0196d.m88a("start error", e, new Object[0]);
            }
        }
    }

    private List<k> m82a(long j) {
        Cursor query;
        if (this.f43j.size() > 0) {
            j = this.f43j.keyAt(0) - 1;
        }
        String str = "_id > " + j;
        try {
            ContentResolver contentResolver = this.f37d;
            List<k> list = a.a;
            query = contentResolver.query(list, this.f41h, str, null, this.f42i);
        } catch (Throwable e) {
            String str2 = "getLastMessages error";
            C0196d.m88a(str2, e, new Object[0]);
            query = null;
        }
        if (query == null) {
            return Collections.emptyList();
        }
        try {
            list = new ArrayList();
            b bVar = new b(query);
            while (bVar.a()) {
                k b = bVar.b();
                if (((c) this.f43j.get(b.a)) != b.b) {
                    list.add(b);
                    m90a(b);
                }
            }
            return list;
        } finally {
            query.close();
        }
    }

    private b m85a(Pattern pattern, Pattern pattern2, m mVar) {
        i iVar = new i(pattern, pattern2, (byte) 0);
        synchronized (this) {
            List list = (List) this.f36c.get(iVar);
            if (list == null) {
                list = new ArrayList();
                this.f36c.put(iVar, list);
            }
            list.add(mVar);
        }
        return new 1(this, iVar, mVar);
    }

    private void m87a(Uri uri) {
        String uri2 = uri.toString();
        try {
            this.f43j.remove(Long.parseLong(uri2.substring(uri2.lastIndexOf(47) + 1)));
        } catch (Throwable e) {
            C0203c.m127a("SmsManager", "untrackMessage", new Exception(uri.toString(), e));
        } catch (Throwable e2) {
            C0203c.m127a("SmsManager", "untrackMessage", e2);
        }
    }

    private static void m88a(String str, Throwable th, Object... objArr) {
        C0204d.m132a("Sms", th, str, objArr);
    }

    public static void m89a(String str, Object... objArr) {
        C0204d.m141c("Sms", str, objArr);
    }

    private void m90a(k kVar) {
        this.f43j.put(kVar.a, kVar.b);
    }

    static /* synthetic */ void m91a(C0196d c0196d, long j) {
        while (c0196d.f43j.size() > 0 && c0196d.f43j.keyAt(c0196d.f43j.size() - 1) > j) {
            c0196d.f43j.removeAt(c0196d.f43j.size() - 1);
        }
    }

    static /* synthetic */ void m92a(C0196d c0196d, Uri uri) {
        String str = "Sms";
        C0204d.m141c(str, "Got some message folder change: uri=%s", uri);
        c0196d.f38e.m52d().post(new 2(c0196d, uri));
    }

    static /* synthetic */ void m93a(C0196d c0196d, k kVar) {
        for (m a : c0196d.m95b(kVar)) {
            a.a(kVar);
        }
    }

    private List<m> m95b(k kVar) {
        List<m> arrayList = new ArrayList();
        if (!(TextUtils.isEmpty(kVar.c) || TextUtils.isEmpty(kVar.d))) {
            synchronized (this) {
                for (Entry entry : this.f36c.entrySet()) {
                    i iVar = (i) entry.getKey();
                    if (iVar.b.matcher(kVar.c).matches() && iVar.a.matcher(kVar.d).matches()) {
                        arrayList.addAll((Collection) entry.getValue());
                    }
                }
            }
        }
        return arrayList;
    }

    private k m97b(Uri uri) {
        Cursor query;
        try {
            k kVar = uri;
            query = this.f37d.query(kVar, this.f41h, null, null, this.f42i);
        } catch (Throwable e) {
            String str = "getLastMessages error";
            C0196d.m88a(str, e, new Object[0]);
            query = null;
        }
        if (query == null) {
            m87a(uri);
            return null;
        }
        try {
            b bVar = new b(query);
            kVar = bVar.a.moveToFirst();
            if (kVar != null) {
                kVar = bVar.b();
                if (((c) this.f43j.get(kVar.a)) != kVar.b) {
                    m90a(kVar);
                    return kVar;
                }
            }
            m87a(uri);
            query.close();
        } catch (Throwable e2) {
            kVar = "getMessage error";
            C0196d.m88a((String) kVar, e2, new Object[0]);
        } finally {
            query.close();
        }
        return null;
    }

    private void m99b() {
        Throwable e;
        Cursor query;
        try {
            query = this.f37d.query(a.a, this.f41h, null, null, "_id DESC LIMIT 128");
            if (query != null) {
                try {
                    b bVar = new b(query);
                    while (bVar.a()) {
                        m90a(bVar.b());
                    }
                    if (query != null) {
                        query.close();
                    }
                } catch (Exception e2) {
                    e = e2;
                    try {
                        C0196d.m88a("prefillKnownMessages error", e, new Object[0]);
                        if (query != null) {
                            query.close();
                        }
                    } catch (Throwable th) {
                        e = th;
                        if (query != null) {
                            query.close();
                        }
                        throw e;
                    }
                }
            } else if (query != null) {
                query.close();
            }
        } catch (Exception e3) {
            e = e3;
            query = null;
            C0196d.m88a("prefillKnownMessages error", e, new Object[0]);
            if (query != null) {
                query.close();
            }
        } catch (Throwable th2) {
            e = th2;
            query = null;
            if (query != null) {
                query.close();
            }
            throw e;
        }
    }

    public static void m100b(String str, Object... objArr) {
        C0204d.m131a("Sms", str, objArr);
    }

    static /* synthetic */ void m101b(C0196d c0196d, k kVar) {
        Iterator it = c0196d.m95b(kVar).iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    private long m102c() {
        Cursor query;
        try {
            query = this.f37d.query(a.a, new String[]{"_id"}, null, null, "_id DESC LIMIT 1");
            if (query != null) {
                if (query.moveToFirst()) {
                    long j = query.getLong(query.getColumnIndex("_id"));
                    if (query == null) {
                        return j;
                    }
                    query.close();
                    return j;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Throwable e) {
            C0196d.m88a("obtainLastSmsId error", e, new Object[0]);
        } catch (Throwable th) {
            if (query != null) {
                query.close();
            }
        }
        return -1;
    }

    static /* synthetic */ void m104c(C0196d c0196d, k kVar) {
        Iterator it = c0196d.m95b(kVar).iterator();
        while (it.hasNext()) {
            it.next();
        }
    }

    private void m105d() {
        this.f40g = m102c();
    }

    static /* synthetic */ void m106d(C0196d c0196d, k kVar) {
        Iterator it = c0196d.m95b(kVar).iterator();
        while (it.hasNext()) {
            it.next();
        }
    }
}
