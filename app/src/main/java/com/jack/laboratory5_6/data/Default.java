package com.jack.laboratory5_6.data;
import com.thoughtworks.xstream.annotations.XStreamAlias;
@XStreamAlias("Default")
public class Default {
    @XStreamAlias("DefPattern")
    private String defPattern;
    @XStreamAlias("Name")
    private String name = "Me";
    @XStreamAlias("Status")
    private String status = "Tap here to set the status";
    @XStreamAlias("Avatar")
    private String avatar;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    public String getDefPattern() {
        return defPattern;
    }

    public void setDefPattern(String defPattern) {
        this.defPattern = defPattern;
    }
}
