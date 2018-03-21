package com.wilbur.clingdemo.bean;

/**
 * Created by wilbur on 2018/1/19.
 */

public class AudioInfo {
    private String audioName;
    private long audioId;
    private String audioType;
    private String uri;
    private String filePath;
    private long duration;
    private String bulbulName;
    private String theAlbumName;
    private int index;

    public AudioInfo(String audioName, long audioId, String audioType, String uri, String filePath, long duration, String bulbulName, String theAlbumName, int index) {
        this.audioName = audioName;
        this.audioId = audioId;
        this.audioType = audioType;
        this.uri = uri;
        this.filePath = filePath;
        this.duration = duration;
        this.bulbulName = bulbulName;
        this.theAlbumName = theAlbumName;
        this.index = index;
    }

    public AudioInfo() {
    }

    public String getAudioName() {
        return this.audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public long getAudioId() {
        return this.audioId;
    }

    public void setAudioId(long audioId) {
        this.audioId = audioId;
    }

    public String getAudioType() {
        return this.audioType;
    }

    public void setAudioType(String audioType) {
        this.audioType = audioType;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getBulbulName() {
        return this.bulbulName;
    }

    public void setBulbulName(String bulbulName) {
        this.bulbulName = bulbulName;
    }

    public String getTheAlbumName() {
        return this.theAlbumName;
    }

    public void setTheAlbumName(String theAlbumName) {
        this.theAlbumName = theAlbumName;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
