package com.vise.snowdemo.mode;

import com.vise.xsnow.event.IEvent;

/**
 * @Description: 自定义事件，只需实现IEvent接口就行，内容自行定义
 * @author: <a href="http://www.xiaoyaoyou1212.com">DAWI</a>
 * @date: 2017-01-20 10:11
 */
public class GithubEvent implements IEvent {
    private GithubModel githubModel;

    public GithubModel getGithubModel() {
        return githubModel;
    }

    public GithubEvent setGithubModel(GithubModel githubModel) {
        this.githubModel = githubModel;
        return this;
    }
}
