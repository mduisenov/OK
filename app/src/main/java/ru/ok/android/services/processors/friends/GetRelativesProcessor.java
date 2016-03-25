package ru.ok.android.services.processors.friends;

import java.util.List;
import ru.ok.android.services.transport.JsonSessionTransportProvider;
import ru.ok.java.api.exceptions.BaseApiException;
import ru.ok.java.api.json.users.JsonGetRelativesParser;
import ru.ok.java.api.request.param.RequestCollectionParam;
import ru.ok.java.api.request.relatives.FriendsRelationsRequest;
import ru.ok.model.Relative;

public class GetRelativesProcessor {
    public static List<Relative> getRelativesAll(List<String> uids, String uid) throws BaseApiException {
        if (uids.size() <= 100) {
            return getRelatives(uids, uid);
        }
        List<Relative> users = getRelatives(uids.subList(0, 100), uid);
        users.addAll(getRelativesAll(uids.subList(100, uids.size()), uid));
        return users;
    }

    private static List<Relative> getRelatives(List<String> uids, String ids) throws BaseApiException {
        return new JsonGetRelativesParser(JsonSessionTransportProvider.getInstance().execJsonHttpMethod(new FriendsRelationsRequest(new RequestCollectionParam(uids), ids))).parse();
    }
}
