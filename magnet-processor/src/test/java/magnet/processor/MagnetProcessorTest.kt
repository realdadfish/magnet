package magnet.processor

import com.google.testing.compile.CompilationSubject.assertThat
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.junit.Test
import javax.tools.JavaFileObject

class MagnetProcessorTest {

    @Test
    fun generateFactory_NoParams() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageNoParams.java"),
                withResource("Page.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageNoParamsMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageNoParamsMagnetFactory.java"))
    }

    @Test
    fun generateFactory_WithScope() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithScope.java"),
                withResource("Page.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageWithScopeMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithScopeMagnetFactory.java"))
    }

    @Test
    fun generateFactory_WithArbitraryParams() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithParams.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java"),
                withResource("UserData.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageWithParamsMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithParamsMagnetFactory.java"))
    }

    @Test
    fun generateFactory_WithArbitraryParamsAndScope() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePage.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java"),
                withResource("UserData.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageMagnetFactory.java"))
    }

    @Test
    fun generateFactory_FailsOnGenericTypeInConstructorParameter() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithGenericParam.java"),
                withResource("Page.java")
            )

        assertThat(compilation).failed()
        assertThat(compilation).hadErrorContaining("is specified using a generic type")
    }

    @Test
    fun generateFactory_TypeNotImplemented() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("Tab.java"),
                withResource("UnimplementedTab.java")
            )

        assertThat(compilation).failed()
        assertThat(compilation).hadErrorContaining("must implement")
    }

    @Test
    fun generateFactory_WithClassifierParams() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithClassifierParams.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java"),
                withResource("UserData.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageWithClassifierParamsMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithClassifierParamsMagnetFactory.java"))

    }

    @Test
    fun generateFactory_WithManyParams() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithManyParams.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageWithManyParamsMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithManyParamsMagnetFactory.java"))

    }

    @Test
    fun generateFactory_WithManyWildcardParams() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithManyWildcardParams.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/HomePageWithManyWildcardParamsMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithManyWildcardParamsMagnetFactory.java"))

    }

    @Test
    fun generateFactory_UsingStaticMethod() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePageWithStaticConstructor.java"),
                withResource("HomePageWithStaticConstructorSingle.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/utils/HomePageWithStaticConstructorSingleCreateRepositoriesMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/HomePageWithStaticConstructorSingleCreateRepositoriesMagnetFactory.java"))

    }

    @Test
    fun generateFactoryIndex_Target() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("UserPageMenuItem.java"),
                withResource("MenuItem.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("magnet/index/app_extension_UserPageMenuItemMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/app_extension_UserPageMenuItemMagnetFactory.java"))
    }

    @Test
    fun generateFactoryIndex_NoTarget() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("HomePage.java"),
                withResource("Page.java"),
                withResource("HomeRepository.java"),
                withResource("UserData.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("magnet/index/app_extension_HomePageMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/app_extension_HomePageMagnetFactory.java"))
    }

    @Test
    fun generateFactoryIndex_UnknownType_SingleImpl() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("Tab.java"), // interface Tab is package private
                withResource("UnknownTypeTab.java"),
                withResource("AppExtensionRegistry.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/UnknownTypeTabMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/UnknownType_UnknownTypeTabMagnetFactory.java"))

        assertThat(compilation)
            .generatedSourceFile("magnet/MagnetIndexer")
            .hasSourceEquivalentTo(withResource("generated/UnknownType_MagnetIndexer.java"))
    }

    @Test
    fun generateFactoryIndex_UnknownType_MultipleImpls() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("Tab.java"), // interface Tab is package private
                withResource("UnknownTypeTab.java"),
                withResource("UnknownTypeTab2.java"),
                withResource("AppExtensionRegistry.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

        assertThat(compilation)
            .generatedSourceFile("app/extension/UnknownTypeTabMagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/UnknownType_UnknownTypeTabMagnetFactory.java"))

        assertThat(compilation)
            .generatedSourceFile("app/extension/UnknownTypeTab2MagnetFactory")
            .hasSourceEquivalentTo(withResource("generated/UnknownType_UnknownTypeTab2MagnetFactory.java"))

        assertThat(compilation)
            .generatedSourceFile("magnet/MagnetIndexer")
            .hasSourceEquivalentTo(withResource("generated/UnknownType_MultipleImpls_MagnetIndexer.java"))
    }

    @Test
    fun generateFactoryIndex_ForInterfaceWithGenericType() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("Executor.java"),
                withResource("ExecutorImpl.java")
            )

        assertThat(compilation).succeededWithoutWarnings()

    }

    @Test
    fun generateMagnetRegistry() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("AppExtensionRegistry.java"),
                withResource("UserPage.java"),
                withResource("HomePageMenuItem.java"),
                withResource("UserPageMenuItem.java"),
                withResource("HomePage.java"),
                withResource("Page.java"),
                withResource("MenuItem.java"),
                withResource("HomeRepository.java"),
                withResource("UserData.java")
            )

        assertThat(compilation).succeeded()

        assertThat(compilation)
            .generatedSourceFile("magnet/MagnetIndexer")
            .hasSourceEquivalentTo(withResource("generated/MagnetIndexer.java"))

    }

    @Test
    fun generateMagnetRegistry_Empty() {

        val compilation = Compiler.javac()
            .withProcessors(MagnetProcessor())
            .compile(
                withResource("AppExtensionRegistry.java"),
                withResource("Page.java")
            )

        assertThat(compilation).succeeded()

        assertThat(compilation)
            .generatedSourceFile("magnet/MagnetIndexer")
            .hasSourceEquivalentTo(withResource("generated/MagnetIndexer_empty.java"))

    }

    private fun withResource(name: String): JavaFileObject {
        return JavaFileObjects.forResource(javaClass.simpleName + '/' + name)
    }

}