package jaci.openrio.gradle.wpi.dependencies

import groovy.transform.CompileStatic
import jaci.gradle.nativedeps.CombinedNativeLib
import jaci.gradle.nativedeps.NativeDepsSpec
import jaci.gradle.nativedeps.NativeLib
import jaci.openrio.gradle.wpi.WPIExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.language.base.plugins.ComponentModelBasePlugin
import org.gradle.model.Mutate
import org.gradle.model.RuleSource

@CompileStatic
class WPINativeDeps implements Plugin<Project> {
    void apply(Project project) {
        project.pluginManager.apply(WPICommonDeps)
        project.pluginManager.apply(ComponentModelBasePlugin)
    }

    static class WPIDepRules extends RuleSource {
        @Mutate
        void addWPILibraries(NativeDepsSpec libs, final ExtensionContainer extensionContainer) {
            def wpi = extensionContainer.getByType(WPIExtension)
            def common = { NativeLib lib ->
                lib.targetPlatform = 'roborio'
                lib.headerDirs = ['include']
                lib.staticMatchers = ['**/*.a']
            }

            libs.create('wpilibc', NativeLib) { NativeLib lib ->
                common(lib)
                lib.libraryNames = ['wpi']
                lib.sharedMatchers = ["**/libwpilibc.so"]
                lib.maven = "edu.wpi.first.wpilibc:athena:${wpi.wpilibVersion}"
            }

            libs.create('hal', NativeLib) { NativeLib lib ->
                common(lib)
                lib.libraryMatchers = ['**/libHALAthena.so']
                lib.sharedMatchers = ["**/libHALAthena.so"]
                lib.maven = "edu.wpi.first.wpilib:hal:${wpi.wpilibVersion}"
            }

            libs.create('ntcore', NativeLib) { NativeLib lib ->
                common(lib)
                lib.libraryMatchers = ['**/libntcore.so']
                lib.sharedMatchers = ["**/libntcore.so"]
                lib.maven = "edu.wpi.first.wpilib.networktables.cpp:NetworkTables:${wpi.ntcoreVersion}:arm@zip"
            }

            libs.create('wpiutil', NativeLib) { NativeLib lib ->
                common(lib)
                lib.libraryMatchers = ['**/libwpiutil.so']
                lib.sharedMatchers = ["**/libwpiutil.so"]
                lib.maven = "edu.wpi.first.wpilib:wpiutil:${wpi.wpiutilVersion}:arm@zip"
            }

            libs.create('cscore', NativeLib) { NativeLib lib ->
                common(lib)
                lib.libraryMatchers = ['**/libopencv*.so.3.1', '**/libcscore.so']
                lib.sharedMatchers = ["**/libopencv*.so.3.1", "**/libcscore.so"]
                lib.maven = "edu.wpi.cscore.java:cscore:${wpi.cscoreVersion}:athena-uberzip@zip"
            }

            libs.create('wpilib', CombinedNativeLib) { CombinedNativeLib clib ->
                clib.libs << "wpilibc" << "hal" << "ntcore" << "wpiutil" << "cscore"
                clib.targetPlatform = 'roborio'
            }

            // CTRE
            libs.create('ctre', NativeLib) { NativeLib lib ->
                common(lib)
                lib.headerDirs = ['cpp/include']
                lib.libraryMatchers = ['cpp/**/*.a']
                lib.maven = "thirdparty.frc.ctre:Toolsuite-Zip:${wpi.ctreVersion}@zip"
            }

            // NavX
            libs.create('navx', NativeLib) { NativeLib lib ->
                common(lib)
                lib.headerDirs = ['roborio/cpp/include']
                lib.libraryMatchers = ['roborio/**/*.a']
                lib.maven = "thirdparty.frc.kauai:Navx-Zip:${wpi.navxVersion}@zip"
            }
        }
    }
}
