# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#############################################
#
# 基本指令区域（没什么别的需求不需要动）
#
#############################################
#忽略警告
-ignorewarnings

#不要压缩(这个必须，因为开启混淆的时候 默认 会把没有被调用的代码 全都排除掉)
-dontshrink

#保护泛型 如果混淆报错建议关掉
-keepattributes Signature

#保持BuildConfig不被混淆(因为混淆之后就无法在导出jar时排除该类)
-keep class com.chenbabys.dingdingtimestatistics.BuildConfig{
public *;
}
# 代码混淆压缩比，在0~7之间，默认为5，一般不做修改
-optimizationpasses 5
#指定不去忽略非公共库的类成员
-dontskipnonpubliclibraryclassmembers

#混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames

#指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses

#这句话能够使我们的项目混淆后产生映射文件
#包含有类名->混淆后类名的映射关系
-verbose

#不做预校验，preverify是proguard的四个步骤之一，Android不需要preverify，去掉这一步能够加快混淆速度。
-dontpreverify

-printmapping proguardMapping.txt

#保留Annotation不混淆
-keepattributes Annotation,InnerClasses

#避免混淆泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod

#抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable

#指定混淆是采用的算法，后面的参数是一个过滤器
#这个过滤器是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/cast,!field/,!class/merging/

##保留我们使用的四大组件，自定义的Application等等这些类不被混淆
#因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class *.ILicensingService
-keep class androidx.core.app.CoreComponentFactory { *; }
#
#AndroidX混淆
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

#保留我们自定义控件（继承自View）不被混淆
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#保留在Activity中的方法参数是view的方法，
#这样以来我们在layout中写的onClick就不会被影响
-keepclassmembers class * extends android.app.Activity{
public void *(android.view.View);
}

# 保留反射中用到的类和方法，到时根据具体情况再改
# 反编译测试的时候有效，运行时也与未混淆情况一样（这个混淆范围太大，可以不要）
#-keepclassmembers class com.chenbabys.dingdingtimestatistics.** {
#
#   public *;
#
#   protected *;
#
#   private *;
#
#}
#避免混淆binding相关的内容（dataBinding和ViewBinding）（这种方式范围也大了，用下面的好点）
#-keep class **.*Binding {*;}
#-keep class **.*BindingImpl {*;}

# 防止viewbinding反射方法被混淆（这个就是本次混淆的主要关键代码，没有这段就会闪退，因为用到了viewBinding，没哟这段会导致LayoutInflater找不到）
#其他的混淆只是锦上添花，其他全都都是有没有都可以。目前为止（2021.10.18）
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static * inflate(android.view.LayoutInflater);
  public static * inflate(android.view.LayoutInflater, android.view.ViewGroup, boolean);
  public static * bind(android.view.View);
}

#保留R下面的资源
-keep class **.R$ {*;}

#保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留本地native方法不被混淆
-keepclassmembers class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    native <methods>;
}

#注解不能混淆
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}

# 保留Serializable序列化的类不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


#保留Parcelable序列化类不被混淆
-keepnames class * implements android.os.Parcelable
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
 }

##==================gson && protobuf==========================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}
#使用GSON、fastjson等框架时，所写的JSON对象类不混淆，否则无法将JSON解析成对应的对象
-keepclassmembers class * {
    public <init>(org.json.JSONObject);
}

# Prevent R8 from leaving Data object members always null：防止R8让Data对象成员始终为空(前提是实体data class中要添加SerializedName给每个字段),至关重要。
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}

#
##---------------------------------开源项目以及第三方SDK---------------------------------
# okhttp

-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn com.squareup.okio.**
-keep class com.squareup.okio.** { *;}
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-dontwarn okio.**
-keep class okio.** { *;}

#********PersistentCookieJar这个库的***********#
-dontwarn com.franmontiel.persistentcookiejar.**
-keep class com.franmontiel.persistentcookiejar.**

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#重复了
###---------------Begin: proguard configuration for Gson  ----------
## Gson uses generic type information stored in a class file when working with fields. Proguard
## removes such information by default, so configure it to keep all of it.
#-keepattributes Signature
#
## For using GSON @Expose annotation
#-keepattributes *Annotation*
#
## Gson specific classes
#-dontwarn sun.misc.**
##-keep class com.google.gson.stream.** { *; }
#
## Application classes that will be serialized/deserialized over Gson
#-keep class com.google.gson.examples.android.model.** { <fields>; }
#
## Prevent proguard from stripping interface information from TypeAdapterFactory,
## JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
#-keep class * implements com.google.gson.TypeAdapterFactory
#-keep class * implements com.google.gson.JsonSerializer
#-keep class * implements com.google.gson.JsonDeserializer
#
## Prevent R8 from leaving Data object members always null
#-keepclassmembers,allowobfuscation class * {
#  @com.google.gson.annotations.SerializedName <fields>;
#}
#
###---------------End: proguard configuration for Gson  ----------


