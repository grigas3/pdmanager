# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Applications/Android Studio.app/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn com.google.appengine.**
-dontwarn javax.servlet.**

# Support classes for compatibility with older API versions

-dontwarn android.support.**
-dontnote android.support.**
# Needed to keep generic types and @Key annotations accessed via reflection
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault
-keepclassmembers class * {
  @com.google.api.client.util.Key <fields>;
}

# Needed by google-http-client-android when linking against an older platform version
-dontwarn com.google.api.client.extensions.android.**

# Needed by google-api-client-android when linking against an older platform version
-dontwarn com.google.api.client.googleapis.extensions.android.**

# Needed by google-play-services when linking against an older platform version
-dontwarn com.google.android.gms.**
-dontnote com.google.android.gms.**

# com.google.client.util.IOUtils references java.nio.file.Files when on Java 7+
-dontnote java.nio.file.Files, java.nio.file.Path

# Suppress notes on LicensingServices
-dontnote **.ILicensingService

# Suppress warnings on sun.misc.Unsafe
-dontnote sun.misc.Unsafe
-dontwarn sun.misc.Unsafe