# statusview
[![Download](https://api.bintray.com/packages/jiangyychn/maven/dialog/images/download.svg)](https://bintray.com/jiangyychn/maven/dialog) ![API](https://img.shields.io/badge/api-14%2B-brightgreen.svg)

[English document](https://github.com/jyygithub/dialog/blob/master/README.md)

# 下载
```
dependencies {
    // ... other dependencies here
    implementation 'com.jiangyy:statusview:1.0.0'
}
```

# 使用

```
<FrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.jiangyy.statussample.MainActivity">

    <android.support.v7.widget.AppCompatTextView
        android:id="@+id/tvContent"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:onClick="testClick"
        android:text="显示的数据"/>

    <com.jiangyy.statusview.StatusView
        android:id="@+id/statusView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</FrameLayout>
```

```
切换状态：（默认状态为isLoading）

 mStatusView.isLoading();
 mStatusView.isEmpty();
 mStatusView.isNoNetwork();
 mStatusView.isError();
 mStatusView.isFinished();
```

# 自定义参数

```
app:textSize
app:textColor
app:emptyIcon
app:errorIcon
app:noNetWorkIcon
```