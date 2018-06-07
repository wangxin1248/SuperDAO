package cn.lovepi.bean;

import java.io.Serializable;

/**
 * Created by icarus on 2016/5/29.
 */
public class Animals implements Serializable{
    private int id;
    private String name;
    private int age;
    private int anid;
    private String anname;

    public String getAnname() {
        return anname;
    }

    public void setAnname(String anname) {
        this.anname = anname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAnid() {
        return anid;
    }

    public void setAnid(int anid) {
        this.anid = anid;
    }

    @Override
    public String toString() {
        return "Animals{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", anid=" + anid +
                ", anname='" + anname + '\'' +
                '}';
    }
}
