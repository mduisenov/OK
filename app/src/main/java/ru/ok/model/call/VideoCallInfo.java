package ru.ok.model.call;

public class VideoCallInfo {
    private String disp;
    private String sid;
    private String userName;
    private String userPic;

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getDisp() {
        return this.disp;
    }

    public void setDisp(String disp) {
        this.disp = disp;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return this.userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }
}
