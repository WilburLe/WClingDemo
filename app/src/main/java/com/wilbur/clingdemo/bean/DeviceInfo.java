package com.wilbur.clingdemo.bean;

import org.fourthline.cling.model.meta.Device;

/**
 * Created by wilbur on 2018/1/19.
 */

public class DeviceInfo {
    private Device device = null;
    private String name = null;
    private boolean isPlay = false;
    private boolean isStop = false;
    private long audioID = -1L;
    private long oldAudioID = -1L;

    public DeviceInfo(Device device, String name) {
        this.device = device;
        this.name = name;
    }

    public DeviceInfo() {
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAudioID(long id) {
        this.audioID = id;
    }

    public void setPlay(boolean flag) {
        this.isPlay = flag;
    }

    public boolean getPlay() {
        return this.isPlay;
    }

    public long getAudioID() {
        return this.audioID;
    }

    public void setStop(boolean flag) {
        this.isStop = flag;
    }

    public boolean getStop() {
        return this.isStop;
    }

    public void setOldAudioID(long audioid) {
        this.oldAudioID = audioid;
    }

    public long getOldAudioID() {
        return this.oldAudioID;
    }
}
