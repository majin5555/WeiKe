package ijkPlayer.bean;

/**
 * ========================================
 * <p>
 * 作 者：majin
 * ========================================
 */
public class VideoijkBean {
    /**
     * id
     */
    int     id;
    /**
     * 分辨率名称
     */
    String  stream;
    /**
     * 视频流对应的网址
     */
    String  url      = "";
    /**
     * 备注备用
     */
    String  remarks;
    /**
     * 当前选中的
     */
    boolean select;
    /**
     * true 有云平台 false 无云平台
     */
    boolean isClound = false;
    /**
     * true 直播 false 历史数据
     */
    boolean isLive   = false;
    /**
     * 开始时间
     */
    String  beginTime;
    /**
     * 结束时间
     */
    String  endTime;

    /**
     * 历史播放暂停时的进度
     */
    long historyDuring;

    private String cameraType;
    /**
     * 摄像头名称
     */
    private String cameraName;

    public String getCameraName() {
        return cameraName == null ? "" : cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public VideoijkBean() {
    }

    public VideoijkBean(String url, boolean isClound) {
        this.url = url;
        this.isClound = isClound;
    }

    public String getCameraType() {
        return cameraType == null ? "" : cameraType;
    }

    public void setCameraType(String cameraType) {
        this.cameraType = cameraType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public void setClound(boolean clound) {
        isClound = clound;
    }

    public boolean isClound() {
        return isClound;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setLive(boolean live) {
        isLive = live;
    }

    public String getBeginTime() {
        return beginTime == null ? "" : beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime == null ? "" : endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getHistoryDuring() {
        return historyDuring;
    }

    public void setHistoryDuring(long historyDuring) {
        this.historyDuring = historyDuring;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VideoijkBean that = (VideoijkBean) o;

        if (id != that.id) return false;
        if (stream != null ? ! stream.equals(that.stream) : that.stream != null) return false;
        if (url != null ? ! url.equals(that.url) : that.url != null) return false;
        return remarks != null ? remarks.equals(that.remarks) : that.remarks == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (stream != null ? stream.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        return result;
    }
}
