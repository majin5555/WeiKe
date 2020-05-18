package com.example.ijkplayerlibrary.ijkPlayer.bean;

/**
 * @作者 ZAMAJIN
 * @时间 2018/11/23 15:49
 * @描述
 */
public class VideoPhone {
    public VideoPhone() {
    }

    private String action;
    private Info   info;
    private String sequence;

    public VideoPhone(String action, Info info, String sequence) {
        this.action = action;
        this.info = info;
        this.sequence = sequence;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action == null ? "" : action;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public Info getInfo() {
        return info;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public static class Info {
        private String device;
        private String speed;
        private String WebRTC;

        private String begin_time;
        private String end_time;
        private String privilege;
        private String location;


        /*控制云台*/
        public Info(String device, String speed, String WebRTC) {
            this.device = device;
            this.speed = speed;
            this.WebRTC = WebRTC;
        }

        /*查询历史数据*/
        public Info(String device, String begin_time, String end_time, String privilege, String location) {
            this.device = device;
            this.begin_time = begin_time;
            this.end_time = end_time;
            this.privilege = privilege;
            this.location = location;
        }

        public String getDevice() {
            return device == null ? "" : device;
        }

        public String getSpeed() {
            return speed == null ? "" : speed;
        }

        public String getBegin_time() {
            return begin_time == null ? "" : begin_time;
        }

        public String getEnd_time() {
            return end_time == null ? "" : end_time;
        }

        public String getPrivilege() {
            return privilege == null ? "" : privilege;
        }

        public String getLocation() {
            return location == null ? "" : location;
        }

        public String getWebRTC() {
            return WebRTC == null ? "" : WebRTC;
        }
    }

    @Override
    public String toString() {
        return "VideoPhone{" +
                "action='" + action + '\'' +
                ", info=" + info +
                ", sequence='" + sequence + '\'' +
                '}';
    }
}
