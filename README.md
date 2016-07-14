# ImageGridLayout
This is an android library that contains ImageGridLayout: a GridLayout designed specifically for images. It automatically arranges the images and set's the size of the images in the layout.

## Usage
Include the jitpack repository to your project. See [jitpack.io](https://jitpack.io/) for help.

Include the library in your project via Gradle:
```
dependencies {
    compile 'com.github.0lumide:ImageGridLayout:v0.0.2-alpha'
}
```

or Maven, if you're into that sort of thing
```
<dependency>
    <groupId>com.github.0lumide</groupId>
    <artifactId>ImageGridLayout</artifactId>
    <version>v0.0.2-alpha</version>
</dependency>
```

### Sample
You can instantiate an `ImageGridLayout` instance via xml
```
<co.mide.imagegridlayout.ImageGridLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:maxImageCount="6"
    android:id="@+id/image_grid_layout"
    android:animateLayoutChanges="true"
    app:moreColor="#4e4e4e"
    android:orientation="horizontal">
</co.mide.imagegridlayout.ImageGridLayout>
```
views can be added programmaticaly this way

```
imageGridLayout.addView(new ImageView(Context));
```

This is what the layout looks like

![ImageGridLayout](/img/screen2.gif)

### Development
Feel free to submit a PR, and open new issues if you find a bug.

### License
Released under MIT license.
