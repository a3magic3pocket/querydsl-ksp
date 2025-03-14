package iceblizz6.querydsl.ksp

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.querydsl.core.annotations.QueryProjection
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import iceblizz6.querydsl.ksp.QueryModelExtractor.Companion.queryClassName
import java.util.logging.Logger

class QueryDslProcessor(
    private val settings: KspSettings,
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    val typeProcessor = QueryModelExtractor(settings, logger)
    var isRun = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (!isRun) {
            val a = resolver.getSymbolsWithAnnotation(QueryProjection::class.qualifiedName!!)
                .filterIsInstance<KSFunctionDeclaration>()
                .forEach { ksFunctionDeclaration ->
                    val constructorAnnotation = ksFunctionDeclaration.annotations
                        .find { it.annotationType.resolve().declaration.qualifiedName?.asString() == QueryProjection::class.qualifiedName }

                    // 생성자가 속한 클래스 정보를 얻기
                    val classDeclaration = ksFunctionDeclaration.parent as? KSClassDeclaration

                    if (classDeclaration != null) {
                        // 클래스의 프로퍼티들 가져오기 (data class의 필드들)
                        val properties = classDeclaration.getAllProperties()

                        // 필드 정보 추출
                        properties.forEach { property ->
                            val propName = property.simpleName.asString()
                            val extractor = TypeExtractor(settings, property)
                            val type = extractor.extract(property.type.resolve())

                        }
                    }
                }


            val b = resolver.getSymbolsWithAnnotation(QueryModelType.ENTITY.associatedAnnotation)
            b.forEach {
                val refined = it as KSClassDeclaration
                val properties = refined.getDeclaredProperties()
                properties.forEach { property ->
                    val propName = property.simpleName.asString()
                    val extractor = TypeExtractor(settings, property)
                    val type = extractor.extract(property.type.resolve())

                }
            }
            isRun = true
        }

        // 초기화
        QueryModelRenderer.initLogger(logger)

        if (settings.enable) {
            QueryModelType.entries.forEach { type ->
                resolver.getSymbolsWithAnnotation(type.associatedAnnotation)
                    .mapNotNull { declaration ->
                        when (declaration) {
                            is KSClassDeclaration -> declaration
                            is KSFunctionDeclaration -> {
                                if (declaration.isConstructor()) {
                                    declaration.parent as? KSClassDeclaration
                                } else {
                                    null
                                }

                            }
                            else -> null
                        }
                    }
                    .filter {
                        val result = isIncluded(it)
                        result
                    }
                    .forEach { declaration ->
                        typeProcessor.add(declaration, type)
                    }
            }
        }

        return emptyList()
    }

    override fun finish() {
        val models = typeProcessor.process()
        models.forEach { model ->
            val typeSpec = QueryModelRenderer.render(model)
            FileSpec.builder(model.className)
                .indent(settings.indent)
                .addType(typeSpec)
                .build()
                .writeTo(
                    codeGenerator = codeGenerator,
                    aggregating = false,
                    originatingKSFiles = listOf(model.originatingFile)
                )
        }
    }

    private fun isIncluded(declaration: KSClassDeclaration): Boolean {
        val className = declaration.qualifiedName!!.asString()
        if (settings.excludedPackages.any { className.startsWith(it) }) {
            return false
        } else if (settings.excludedClasses.any { it == className }) {
            return false
        } else if (settings.includedClasses.isNotEmpty()) {
            return settings.includedClasses.any { it == className }
        } else if (settings.includedPackages.isNotEmpty()) {
            return settings.includedPackages.any { className.startsWith(it) }
        } else {
            return true
        }
    }
}
