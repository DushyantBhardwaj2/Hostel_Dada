# Add project specific ProGuard rules here.

# Keep Firebase classes
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Keep Koin
-keep class org.koin.** { *; }

# Keep data classes for Firestore serialization
-keep class com.hosteldada.android.data.firebase.** { *; }
-keepclassmembers class com.hosteldada.android.data.firebase.** { *; }

# Keep Kotlin metadata
-keepattributes RuntimeVisibleAnnotations
-keep class kotlin.Metadata { *; }
