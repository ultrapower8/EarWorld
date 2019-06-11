package com.nickole.earworld.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @author shuzijie
 * @date 2019-05-15.
 */
public class News implements Serializable {
    private String id;
    private String title;
    private List<String> imageUrls;
    private String publishDateStr;
    private String posterScreenName;
    private int commentCount;
    private String content;

    private boolean isPlaying;

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getPublishDate() {
        return publishDateStr;
    }

    public void setPublishDate(String publishDate) {
        this.publishDateStr = publishDate;
    }

    public String getPosterScreenName() {
        return posterScreenName;
    }

    public void setPosterScreenName(String posterScreenName) {
        this.posterScreenName = posterScreenName;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
