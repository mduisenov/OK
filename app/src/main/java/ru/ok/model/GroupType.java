package ru.ok.model;

import ru.ok.android.proto.MessagesProto.Message;

public enum GroupType {
    ARMY(4),
    COLLEGE(2),
    CUSTOM(7),
    FACULTY(-3),
    HOLIDAY(6),
    MOIMIR(-4),
    SCHOOL(1),
    UNIVERSITY(3),
    WORKPLACE(5),
    HAPPENING(8),
    OTHER(0);
    
    public final int categoryId;

    public static GroupType fromCategoryId(int categoryId) {
        switch (categoryId) {
            case -4:
                return MOIMIR;
            case -3:
                return FACULTY;
            case Message.TEXT_FIELD_NUMBER /*1*/:
                return SCHOOL;
            case Message.AUTHORID_FIELD_NUMBER /*2*/:
                return COLLEGE;
            case Message.TYPE_FIELD_NUMBER /*3*/:
                return UNIVERSITY;
            case Message.CAPABILITIES_FIELD_NUMBER /*4*/:
                return ARMY;
            case Message.UUID_FIELD_NUMBER /*5*/:
                return WORKPLACE;
            case Message.REPLYTO_FIELD_NUMBER /*6*/:
                return HOLIDAY;
            case Message.ATTACHES_FIELD_NUMBER /*7*/:
                return CUSTOM;
            case Message.TASKID_FIELD_NUMBER /*8*/:
                return HAPPENING;
            default:
                return OTHER;
        }
    }

    private GroupType(int categoryId) {
        this.categoryId = categoryId;
    }
}
