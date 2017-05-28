package com.vise.snowdemo.mode;

import java.io.Serializable;

/**
 * @Description:
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/5/28 16:23.
 */
public class ZhiHuModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private String thumbnail;

    public int getId() {
        return id;
    }

    public ZhiHuModel setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public ZhiHuModel setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ZhiHuModel setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public ZhiHuModel setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }

    @Override
    public String toString() {
        return "ZhiHuModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                '}';
    }
}
