package com.nickole.earworld.entity;

/**
 * @author shuzijie
 * @date 2019-06-06.
 */
public class VoiceFile {
    private String filePath;
    private String content;
    private String createDate;
    private String totalTime;
    private String fileSize;

    public VoiceFile(String filePath, String content, String createDate, String totalTime, String fileSize) {
        this.filePath = filePath;
        this.content = content;
        this.createDate = createDate;
        this.totalTime = totalTime;
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
