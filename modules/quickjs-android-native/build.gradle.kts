plugins {
    alias(libs.plugins.android.library)
}

// 与 :app splits 对齐; -Parm64Only=true 时只编 arm64-v8a
val arm64Only = providers.gradleProperty("arm64Only").orNull == "true"

android {
    namespace = "com.script.quickjs.nativebridge"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        ndk {
            // 与 app splits 对齐: 只编 arm 系 ABI, x86 系不编译
            abiFilters += if (arm64Only) listOf("arm64-v8a") else listOf("arm64-v8a", "armeabi-v7a")
        }
        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
                cppFlags += listOf("-std=c++17", "-fvisibility=hidden")
                cFlags += "-fvisibility=hidden"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    lint {
        checkDependencies = true
        // x86 系 ABI 恒不编 (与 :app splits 对齐: 默认 arm64-v8a + armeabi-v7a,
        // -Parm64Only=true 时仅 arm64-v8a)
        disable += "ChromeOsAbiSupport"
    }
}
