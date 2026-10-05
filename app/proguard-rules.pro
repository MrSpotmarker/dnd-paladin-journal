# ProGuard & R8 Regeln für Paladin App

# Kotlinx Serialization
-keepattributes *Annotation*,InnerClasses
-dontnote kotlinx.serialization.SerializationKt

-keepclassmembers class * {
    *** Companion;
}

-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Model-Klassen behalten
-keep class com.paladin.app.model.** { *; }
