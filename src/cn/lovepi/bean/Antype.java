package cn.lovepi.bean;

import java.io.Serializable;

/**
 * Created by icarus on 2016/5/29.
 */
public class Antype implements Serializable{
    private int anid;
    private String anname;

    public int getAnid() {
        return anid;
    }

    public void setAnid(int anid) {
        this.anid = anid;
    }

    public String getAnname() {
        return anname;
    }

    public void setAnname(String anname) {
        this.anname = anname;
    }

    @Override
    public String toString() {
        return "Antype{" +
                "anid=" + anid +
                ", anname='" + anname + '\'' +
                '}';
    }
}
