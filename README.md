# CircleProgressView
<br /> <br />
**How to get a Git project into your build:**

**Step 1.** Add the JitPack repository to your build file, add it in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```  
**Step 2.** Add the dependency
```
dependencies {
  implementation 'com.github.R12rus:CircleProgressView:0.0.1'
}
```

# **How to use:<br />**
```
<com.r12.CircleProgressView
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:text="Infinite"
            android:textSize="30sp" />

</com.r12.CircleProgressView>
```
```
<com.r12.CircleProgressView
        android:layout_width="200dp"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:layout_marginTop="32dp"
        app:circleprogressview_progress="64"
        app:circleprogressview_circle_color="@color/colorAccent">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:text="74%"
            android:textSize="30sp" />

</com.r12.CircleProgressView>
```
# Example
![Alt text](/images/1.gif?raw=true "CircleProgressView example")
