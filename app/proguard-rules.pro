-keep class com.google.mediapipe.** { *; }
-keep class com.example.nsl_mini.** { *; }
-keep class * extends com.google.mediapipe.tasks.** { *; }
-keepclassmembers class * {
    @com.google.mediapipe.framework.** <fields>;
}
-keepattributes *Annotation*, Signature, InnerClasses, EnclosingMethod
