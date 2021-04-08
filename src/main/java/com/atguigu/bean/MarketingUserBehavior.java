package com.atguigu.bean;

/**
 * @author chujian
 * @create 2021-03-21 17:58
 */
public class MarketingUserBehavior {
    /**
     * 用户的id
     */
    private Long userId;
    /**
     * 用户的行为 、下载、安装、更新、卸载
     */
    private String behavior;
    /**
     * 渠道小米 华为 oppo vivo
     */
    private String channel;
    /**
     * 时间戳
     */
    private Long timestamp;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBehavior() {
        return behavior;
    }

    public void setBehavior(String behavior) {
        this.behavior = behavior;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public MarketingUserBehavior() {
    }

    public MarketingUserBehavior(Long userId, String behavior, String channel, Long timestamp) {
        this.userId = userId;
        this.behavior = behavior;
        this.channel = channel;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MarketingUserBehavior{" +
                "userId=" + userId +
                ", behavior='" + behavior + '\'' +
                ", channel='" + channel + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

