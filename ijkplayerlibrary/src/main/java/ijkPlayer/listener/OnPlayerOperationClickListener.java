package ijkPlayer.listener;

/**
 * =======================================
 * <p/>
 * 作 者：马晋
 * ========================================
 */
public interface OnPlayerOperationClickListener {

    /**
     * 用户点击控制面板的操作  flag
     * 向上：up
     * 向下：down
     * 向左：left
     * 向右：right
     * 拉近：zoom_in
     * 拉远：zoom_out
     */
    void CloundButtonDown(String flag);//按下

    void CloundButtonUp(String flag);//抬起


    /*放大*/
    void onEnlarge();

    /*缩小*/
    void onNarrow();

    /*左滑*/
    void slideLeft();

    /*右滑*/
    void slideRight();

    /*向上*/
    void slideUp();

    /*向下*/
    void slideDown();

    /*单指抬起*/
    void ACTION_UP();

    /*显示历史的view*/
    void showHistory(boolean isShowHistory);

    /*播放历史视频*/
    void playHistory(String startTime, String stopTime);

    /*恢复播放历史视频*/
    void resumePlayHistory(long time);


    void playSpeed(String startTime, String stopTime,String speed);


}
