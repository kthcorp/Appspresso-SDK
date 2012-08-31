package com.appspresso.screw.ui.orientation;

public interface AxOrientationController {
    /**
     * 아래 속성값들 외의 값
     */
    public static final int UNKNOWN = -1;

    /**
     * 기본적으로 단말의 방향에 맡김
     */
    public static final int DEFAULT = 0;

    /**
     * 위아래로 길며 홈버튼이 아래, 카메라가 위에 위치
     */
    public static final int PORTRAIT = 1;

    /**
     * 위아래로 길며 홈버튼이 위, 카메라가 아래에 위치
     */
    public static final int REVERSE_PORTRAIT = 3;

    /**
     * 양옆으로 길며 홈버튼이 왼쪽, 카메라가 오른쪽에 위치
     */
    public static final int LANDSCAPE = 2;

    /**
     * 양옆으로 길며 홈버튼이 오른쪽, 카메라가 오른쪽에 위치
     */
    public static final int REVERSE_LANDSCAPE = 4;

    /**
     * 위의 속성값과 상관없이 이 객체가 생성될 때의 방향으로 복구시킨다.
     */
    public void resetOrientation();

    /**
     * 설정되어 있는 방향값을 가져온다. 어플리케이션의 방향과 다를 수도 있다. 예를 들어 1로 설정했을 때와 0으로 설정하여 1과 같은 방향으로 단말기를 들었을때의
     * 어플리케이션의 방향은 Portrait로 같지만 각각 1, 0을 가져오게 된다.
     * 
     * @return 설정된 방향값
     * @see PORTRAIT
     * @see REVERSE_PORTRAIT
     * @see LANDSCAPE
     * @see REVERSE_LANDSCAPE
     */
    public int getOrientation();

    /**
     * 어플리케이션의 방향을 설정한다.
     * 
     * @param orientation 설정할 방향값
     * @see PORTRAIT
     * @see REVERSE_PORTRAIT
     * @see LANDSCAPE
     * @see REVERSE_LANDSCAPE
     */
    public void setOrientation(int orientation);
}
