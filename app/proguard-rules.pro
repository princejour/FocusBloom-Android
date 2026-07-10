# Keep Google Mobile Ads and UMP metadata used through reflection.
-keepattributes *Annotation*
-dontwarn com.google.android.gms.**

# Keep model constructors and fields used by Android framework state restoration.
-keepclassmembers class com.walhero.focusbloom.data.** { *; }
