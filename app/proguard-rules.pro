# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add this global rule
-keepattributes Signature

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models.
# Modify this rule to fit the structure of your app.
-keepclassmembers class com.burakgunduz.bitirmeprojesi.** {
  *;
}
# Google Maps
-keep class com.google.android.gms.maps.** { *; }
-keep class com.google.android.gms.maps.model.** { *; }
-keep class com.google.maps.android.** { *; }
-keep class com.google.android.gms.common.api.** { *; }
-keep class com.google.android.gms.common.** { *; }
-keep class com.google.android.gms.location.** { *; }
-keep class com.google.android.libraries.maps.** { *; }
-keep interface com.google.android.libraries.maps.** { *; }
-dontwarn com.google.android.gms.**

# Geocoder
-keep class android.location.** { *; }
-dontwarn android.location.**

# Ensure that reflection and dynamic class loading aren't stripped
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Google Play Services

# Support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }

# AppCompat
-keep class androidx.appcompat.widget.** { *; }
# Firebase Auth
-keep class com.google.firebase.auth.** { *; }
-dontwarn com.google.firebase.auth.**

# Firestore
-keep class com.google.firebase.firestore.** { *; }
-dontwarn com.google.firebase.firestore.**

# Firebase Storage
-keep class com.google.firebase.storage.** { *; }
-dontwarn com.google.firebase.storage.**

-keep class com.google.firebase.example.fireeats.java.model.** { *; }
-keep class com.google.firebase.example.fireeats.kotlin.model.** { *; }
# General Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**
-keep class androidx.lifecycle.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep class androidx.compose.** { *; }

# Keep classes used by Firebase Firestore from ProGuard obfuscation
-keep class com.google.firebase.firestore.model.** { *; }
-keep class com.google.firebase.firestore.remote.** { *; }

# Keep the FirestoreAnnotations
-keep @com.google.firebase.firestore.annotation.** class * { *; }

# Keep all the classes related to Firestore for reflection
-keep class com.google.firebase.firestore.util.** { *; }
# Guava library used by Firebase
-dontwarn org.apache.**

-keep class com.android.burakgunduz.bitirmeprojesi.viewModels.** { *; }

# Preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# Hide the original source file name.
-renamesourcefileattribute SourceFile