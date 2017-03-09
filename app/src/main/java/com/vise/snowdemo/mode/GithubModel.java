package com.vise.snowdemo.mode;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * @Description: GitHub信息
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 17/1/18 23:15.
 */
@Entity
public class GithubModel implements Serializable {
    @Id(autoincrement = true)
    private Long id;
    private String current_user_url;
    private String current_user_authorizations_html_url;
    private String authorizations_url;
    private String code_search_url;
    private String commit_search_url;
    private String emails_url;
    private String emojis_url;
    private String events_url;
    private String feeds_url;
    private String followers_url;
    private String following_url;
    private String gists_url;
    private String hub_url;
    private String issue_search_url;
    private String issues_url;
    private String keys_url;
    private String notifications_url;
    private String organization_repositories_url;
    private String organization_url;
    private String public_gists_url;
    private String rate_limit_url;
    private String repository_url;
    private String repository_search_url;
    private String current_user_repositories_url;
    private String starred_url;
    private String starred_gists_url;
    private String team_url;
    private String user_url;
    private String user_organizations_url;
    private String user_repositories_url;
    private String user_search_url;

    @Generated(hash = 1630623119)
    public GithubModel(Long id, String current_user_url, String current_user_authorizations_html_url,
            String authorizations_url, String code_search_url, String commit_search_url, String emails_url,
            String emojis_url, String events_url, String feeds_url, String followers_url, String following_url,
            String gists_url, String hub_url, String issue_search_url, String issues_url, String keys_url,
            String notifications_url, String organization_repositories_url, String organization_url,
            String public_gists_url, String rate_limit_url, String repository_url, String repository_search_url,
            String current_user_repositories_url, String starred_url, String starred_gists_url, String team_url,
            String user_url, String user_organizations_url, String user_repositories_url, String user_search_url) {
        this.id = id;
        this.current_user_url = current_user_url;
        this.current_user_authorizations_html_url = current_user_authorizations_html_url;
        this.authorizations_url = authorizations_url;
        this.code_search_url = code_search_url;
        this.commit_search_url = commit_search_url;
        this.emails_url = emails_url;
        this.emojis_url = emojis_url;
        this.events_url = events_url;
        this.feeds_url = feeds_url;
        this.followers_url = followers_url;
        this.following_url = following_url;
        this.gists_url = gists_url;
        this.hub_url = hub_url;
        this.issue_search_url = issue_search_url;
        this.issues_url = issues_url;
        this.keys_url = keys_url;
        this.notifications_url = notifications_url;
        this.organization_repositories_url = organization_repositories_url;
        this.organization_url = organization_url;
        this.public_gists_url = public_gists_url;
        this.rate_limit_url = rate_limit_url;
        this.repository_url = repository_url;
        this.repository_search_url = repository_search_url;
        this.current_user_repositories_url = current_user_repositories_url;
        this.starred_url = starred_url;
        this.starred_gists_url = starred_gists_url;
        this.team_url = team_url;
        this.user_url = user_url;
        this.user_organizations_url = user_organizations_url;
        this.user_repositories_url = user_repositories_url;
        this.user_search_url = user_search_url;
    }

    @Generated(hash = 1283633266)
    public GithubModel() {
    }

    public Long getId() {
        return id;
    }

    public GithubModel setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCurrent_user_url() {
        return current_user_url;
    }

    public GithubModel setCurrent_user_url(String current_user_url) {
        this.current_user_url = current_user_url;
        return this;
    }

    public String getCurrent_user_authorizations_html_url() {
        return current_user_authorizations_html_url;
    }

    public GithubModel setCurrent_user_authorizations_html_url(String current_user_authorizations_html_url) {
        this.current_user_authorizations_html_url = current_user_authorizations_html_url;
        return this;
    }

    public String getAuthorizations_url() {
        return authorizations_url;
    }

    public GithubModel setAuthorizations_url(String authorizations_url) {
        this.authorizations_url = authorizations_url;
        return this;
    }

    public String getCode_search_url() {
        return code_search_url;
    }

    public GithubModel setCode_search_url(String code_search_url) {
        this.code_search_url = code_search_url;
        return this;
    }

    public String getCommit_search_url() {
        return commit_search_url;
    }

    public GithubModel setCommit_search_url(String commit_search_url) {
        this.commit_search_url = commit_search_url;
        return this;
    }

    public String getEmails_url() {
        return emails_url;
    }

    public GithubModel setEmails_url(String emails_url) {
        this.emails_url = emails_url;
        return this;
    }

    public String getEmojis_url() {
        return emojis_url;
    }

    public GithubModel setEmojis_url(String emojis_url) {
        this.emojis_url = emojis_url;
        return this;
    }

    public String getEvents_url() {
        return events_url;
    }

    public GithubModel setEvents_url(String events_url) {
        this.events_url = events_url;
        return this;
    }

    public String getFeeds_url() {
        return feeds_url;
    }

    public GithubModel setFeeds_url(String feeds_url) {
        this.feeds_url = feeds_url;
        return this;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public GithubModel setFollowers_url(String followers_url) {
        this.followers_url = followers_url;
        return this;
    }

    public String getFollowing_url() {
        return following_url;
    }

    public GithubModel setFollowing_url(String following_url) {
        this.following_url = following_url;
        return this;
    }

    public String getGists_url() {
        return gists_url;
    }

    public GithubModel setGists_url(String gists_url) {
        this.gists_url = gists_url;
        return this;
    }

    public String getHub_url() {
        return hub_url;
    }

    public GithubModel setHub_url(String hub_url) {
        this.hub_url = hub_url;
        return this;
    }

    public String getIssue_search_url() {
        return issue_search_url;
    }

    public GithubModel setIssue_search_url(String issue_search_url) {
        this.issue_search_url = issue_search_url;
        return this;
    }

    public String getIssues_url() {
        return issues_url;
    }

    public GithubModel setIssues_url(String issues_url) {
        this.issues_url = issues_url;
        return this;
    }

    public String getKeys_url() {
        return keys_url;
    }

    public GithubModel setKeys_url(String keys_url) {
        this.keys_url = keys_url;
        return this;
    }

    public String getNotifications_url() {
        return notifications_url;
    }

    public GithubModel setNotifications_url(String notifications_url) {
        this.notifications_url = notifications_url;
        return this;
    }

    public String getOrganization_repositories_url() {
        return organization_repositories_url;
    }

    public GithubModel setOrganization_repositories_url(String organization_repositories_url) {
        this.organization_repositories_url = organization_repositories_url;
        return this;
    }

    public String getOrganization_url() {
        return organization_url;
    }

    public GithubModel setOrganization_url(String organization_url) {
        this.organization_url = organization_url;
        return this;
    }

    public String getPublic_gists_url() {
        return public_gists_url;
    }

    public GithubModel setPublic_gists_url(String public_gists_url) {
        this.public_gists_url = public_gists_url;
        return this;
    }

    public String getRate_limit_url() {
        return rate_limit_url;
    }

    public GithubModel setRate_limit_url(String rate_limit_url) {
        this.rate_limit_url = rate_limit_url;
        return this;
    }

    public String getRepository_url() {
        return repository_url;
    }

    public GithubModel setRepository_url(String repository_url) {
        this.repository_url = repository_url;
        return this;
    }

    public String getRepository_search_url() {
        return repository_search_url;
    }

    public GithubModel setRepository_search_url(String repository_search_url) {
        this.repository_search_url = repository_search_url;
        return this;
    }

    public String getCurrent_user_repositories_url() {
        return current_user_repositories_url;
    }

    public GithubModel setCurrent_user_repositories_url(String current_user_repositories_url) {
        this.current_user_repositories_url = current_user_repositories_url;
        return this;
    }

    public String getStarred_url() {
        return starred_url;
    }

    public GithubModel setStarred_url(String starred_url) {
        this.starred_url = starred_url;
        return this;
    }

    public String getStarred_gists_url() {
        return starred_gists_url;
    }

    public GithubModel setStarred_gists_url(String starred_gists_url) {
        this.starred_gists_url = starred_gists_url;
        return this;
    }

    public String getTeam_url() {
        return team_url;
    }

    public GithubModel setTeam_url(String team_url) {
        this.team_url = team_url;
        return this;
    }

    public String getUser_url() {
        return user_url;
    }

    public GithubModel setUser_url(String user_url) {
        this.user_url = user_url;
        return this;
    }

    public String getUser_organizations_url() {
        return user_organizations_url;
    }

    public GithubModel setUser_organizations_url(String user_organizations_url) {
        this.user_organizations_url = user_organizations_url;
        return this;
    }

    public String getUser_repositories_url() {
        return user_repositories_url;
    }

    public GithubModel setUser_repositories_url(String user_repositories_url) {
        this.user_repositories_url = user_repositories_url;
        return this;
    }

    public String getUser_search_url() {
        return user_search_url;
    }

    public GithubModel setUser_search_url(String user_search_url) {
        this.user_search_url = user_search_url;
        return this;
    }

    @Override
    public String toString() {
        return "GithubModel{" +
                "id=" + id +
                ", current_user_url='" + current_user_url + '\'' +
                ", current_user_authorizations_html_url='" + current_user_authorizations_html_url + '\'' +
                ", authorizations_url='" + authorizations_url + '\'' +
                ", code_search_url='" + code_search_url + '\'' +
                ", commit_search_url='" + commit_search_url + '\'' +
                ", emails_url='" + emails_url + '\'' +
                ", emojis_url='" + emojis_url + '\'' +
                ", events_url='" + events_url + '\'' +
                ", feeds_url='" + feeds_url + '\'' +
                ", followers_url='" + followers_url + '\'' +
                ", following_url='" + following_url + '\'' +
                ", gists_url='" + gists_url + '\'' +
                ", hub_url='" + hub_url + '\'' +
                ", issue_search_url='" + issue_search_url + '\'' +
                ", issues_url='" + issues_url + '\'' +
                ", keys_url='" + keys_url + '\'' +
                ", notifications_url='" + notifications_url + '\'' +
                ", organization_repositories_url='" + organization_repositories_url + '\'' +
                ", organization_url='" + organization_url + '\'' +
                ", public_gists_url='" + public_gists_url + '\'' +
                ", rate_limit_url='" + rate_limit_url + '\'' +
                ", repository_url='" + repository_url + '\'' +
                ", repository_search_url='" + repository_search_url + '\'' +
                ", current_user_repositories_url='" + current_user_repositories_url + '\'' +
                ", starred_url='" + starred_url + '\'' +
                ", starred_gists_url='" + starred_gists_url + '\'' +
                ", team_url='" + team_url + '\'' +
                ", user_url='" + user_url + '\'' +
                ", user_organizations_url='" + user_organizations_url + '\'' +
                ", user_repositories_url='" + user_repositories_url + '\'' +
                ", user_search_url='" + user_search_url + '\'' +
                '}';
    }
}
