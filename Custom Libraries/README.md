This project demonstrates how to create custom Java and Android library with Android Studio.

For step-by-step guide, see this [blog post](http://dradest.com/blog/2019/02/27/create-custom-java-and-android-libraries-in-android-studio/)

#### answerlibrary

contains Java class AnswerLUE with a method _getMeAnAnswer()_ which returns a String

in app's MainActivity, we populate a textview with this String when LUE button is pressed

#### aarapplication

contains Android library with an Activity that displays and imageview and a textview

in app's MainActivity, we navigate to this activity via intent when AAR button is pressed 

### installation

to run this, simply use Android Studio's 'Open an existing Android Studio project' and select this folder,<br>
or use 'Import project (Gradle, Eclipse ADT, etc.)' and select build.gradle in this folder