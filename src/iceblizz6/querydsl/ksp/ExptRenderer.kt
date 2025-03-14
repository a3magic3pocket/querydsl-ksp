package iceblizz6.querydsl.ksp

class ExptRenderer {
}
/*
import com.squareup.kotlinpoet.*
import com.querydsl.core.types.Expression
import com.querydsl.core.types.dsl.ConstructorExpression
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

fun generateQUserDtoClass(): TypeSpec {
    return TypeSpec.classBuilder("QUserDto")
        .addAnnotation(
            AnnotationSpec.builder(Generated::class)
                .addMember("\"com.querydsl.codegen.DefaultProjectionSerializer\"")
                .build()
        )
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("id", Expression::class.asClassName().parameterizedBy(Long::class.asClassName()))
                .addParameter("name", Expression::class.asClassName().parameterizedBy(String::class.asClassName()))
                .build()
        )
        .superclass(ConstructorExpression::class.asClassName().parameterizedBy(UserDto::class))
        .addSuperclassConstructorParameter(
            "UserDto::class.java, arrayOf(Long::class.java, String::class.java), id, name"
        )
        .addCompanionObject(
            TypeSpec.companionObjectBuilder()
                .addProperty(
                    PropertySpec.builder("serialVersionUID", Long::class)
                        .initializer("-368342898L")
                        .build()
                )
                .build()
        )
        .build()
}

 */