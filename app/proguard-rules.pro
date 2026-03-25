# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the Android SDK tools proguard/proguard-android.txt

# Keep the ExposureCalculator class and its inner classes intact
-keep class com.photography.luxexposimeter.ExposureCalculator { *; }
-keep class com.photography.luxexposimeter.ExposureCalculator$ExposureResult { *; }
