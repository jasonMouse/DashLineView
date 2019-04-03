# DashLineView
两头两个可以调节的半圆，中间用可控虚线连接，横竖方向可控的自定义View
![图片](https://github.com/jasonMouse/DashLineView/blob/master/app/src/main/assets/DashLinePicture.png)
```
<!--两边两个半圆，中间一条虚线-->
<declare-styleable name="DashLineView">
    <!--半圆的半径-->
    <attr name="dlv_semicircle_radius" format="dimension|reference" />
    <!--半圆的颜色 默认白色-->
    <attr name="dlv_semicircle_color" format="color|reference" />
    <!--虚线的长度-->
    <attr name="dlv_dash_line_width" format="dimension|reference" />
    <!--虚线的间距-->
    <attr name="dlv_dash_line_gap" format="dimension|reference" />
    <!--虚线的高度-->
    <attr name="dlv_dash_line_height" format="dimension|reference" />
    <!--虚线的颜色 默认白色-->
    <attr name="dlv_dash_line_color" format="color|reference" />
    <!--整体的方向 默认水平-->
    <attr name="dlv_dash_line_orientation">
        <enum name="horizontal" value="0" />
        <enum name="vertical" value="1" />
    </attr>
    <!--第一个半圆是否展示 默认展示-->
    <attr name="dlv_semicircle_first_show" format="boolean" />
    <!--第二个半圆是否展示 默认展示-->
    <attr name="dlv_semicircle_last_show" format="boolean" />
    <!--第一个半圆距离虚线的margin 默认是0-->
    <attr name="dlv_dash_line_first_margin" format="dimension|reference" />
    <!--第二个半圆距离虚线的margin 默认是0-->
    <attr name="dlv_dash_line_last_margin" format="dimension|reference" />
</declare-styleable>
```
