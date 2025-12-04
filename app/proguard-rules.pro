# MathGenius/app/proguard-rules.pro
# ProGuard Rules for MathGenius

# Keep all classes in the calculator package
-keep class com.mathgenius.calculator.** { *; }

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep Kotlin reflection
-keep class kotlin.reflect.** { *; }

# Keep sealed classes
-keep class * extends com.mathgenius.calculator.core.engine.Expr { *; }

# Keep enum classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep R class
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Suppress warnings
-dontwarn kotlin.**
-dontwarn javax.annotation.**