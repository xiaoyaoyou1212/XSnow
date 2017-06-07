package com.vise.snowdemo.mode;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * @Description: 个人信息实体类
 * @author: <a href="http://xiaoyaoyou1212.360doc.com">DAWI</a>
 * @date: 2017/6/5 20:29
 */
@Entity
public class AuthorModel implements Serializable{
    @Id(autoincrement = true)
    private Long id;
    private int author_id;
    private String author_name;
    private String author_nickname;
    private String author_account;
    private String author_github;
    private String author_csdn;
    private String author_websit;
    private String author_introduction;

    @Generated(hash = 1688768647)
    public AuthorModel(Long id, int author_id, String author_name,
            String author_nickname, String author_account, String author_github,
            String author_csdn, String author_websit, String author_introduction) {
        this.id = id;
        this.author_id = author_id;
        this.author_name = author_name;
        this.author_nickname = author_nickname;
        this.author_account = author_account;
        this.author_github = author_github;
        this.author_csdn = author_csdn;
        this.author_websit = author_websit;
        this.author_introduction = author_introduction;
    }

    @Generated(hash = 1129386739)
    public AuthorModel() {
    }

    public String getAuthor_account() {
        return author_account;
    }

    public AuthorModel setAuthor_account(String author_account) {
        this.author_account = author_account;
        return this;
    }

    public String getAuthor_csdn() {
        return author_csdn;
    }

    public AuthorModel setAuthor_csdn(String author_csdn) {
        this.author_csdn = author_csdn;
        return this;
    }

    public String getAuthor_github() {
        return author_github;
    }

    public AuthorModel setAuthor_github(String author_github) {
        this.author_github = author_github;
        return this;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public AuthorModel setAuthor_id(int author_id) {
        this.author_id = author_id;
        return this;
    }

    public String getAuthor_introduction() {
        return author_introduction;
    }

    public AuthorModel setAuthor_introduction(String author_introduction) {
        this.author_introduction = author_introduction;
        return this;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public AuthorModel setAuthor_name(String author_name) {
        this.author_name = author_name;
        return this;
    }

    public String getAuthor_nickname() {
        return author_nickname;
    }

    public AuthorModel setAuthor_nickname(String author_nickname) {
        this.author_nickname = author_nickname;
        return this;
    }

    public String getAuthor_websit() {
        return author_websit;
    }

    public AuthorModel setAuthor_websit(String author_websit) {
        this.author_websit = author_websit;
        return this;
    }

    @Override
    public String toString() {
        return "AuthorModel{" +
                "author_account='" + author_account + '\'' +
                ", author_id=" + author_id +
                ", author_name='" + author_name + '\'' +
                ", author_nickname='" + author_nickname + '\'' +
                ", author_github='" + author_github + '\'' +
                ", author_csdn='" + author_csdn + '\'' +
                ", author_websit='" + author_websit + '\'' +
                ", author_introduction='" + author_introduction + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
