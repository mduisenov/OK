package ru.ok.model.stream;

import java.security.MessageDigest;
import java.util.ArrayList;
import ru.ok.java.api.utils.DigestUtils;

public class FeedStringRefs extends FeedRefs<String> {
    public void digest(MessageDigest digest, byte[] buffer) {
        for (int role = 0; role < 10; role++) {
            ArrayList<String> refs = this.refs[role];
            digest.update(refs == null ? (byte) 0 : (byte) 1);
            if (refs != null) {
                int size = refs.size();
                DigestUtils.addInt(digest, size, buffer);
                for (int i = 0; i < size; i++) {
                    DigestUtils.addString(digest, (String) refs.get(i));
                }
            }
        }
    }
}
