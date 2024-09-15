package com.github.frtu.sample.ai.os.tool

import com.github.frtu.sample.ai.os.builder.BuilderMarker
import kotlin.reflect.KFunction2

class FunctionRegistryBuilder(
    private val functionRegistry: FunctionRegistry = FunctionRegistry()
) {
    fun function(
        name: String,
        description: String,
        kFunction2: KFunction2<String, String, String>,
        parameterClass: Class<*>,
        returnClass: Class<*>,
    ) = functionRegistry.registerFunction(
        name = name,
        description = description,
        kFunction2 = kFunction2,
        parameterClass = parameterClass,
        returnClass = returnClass,
    )

    fun function(
        name: String,
        description: String,
        kFunction2: KFunction2<String, String, String>,
        parameterJsonSchema: String,
        returnJsonSchema: String,
    ) = functionRegistry.registerFunction(
        name = name,
        description = description,
        kFunction2 = kFunction2,
        parameterJsonSchema = parameterJsonSchema,
        returnJsonSchema = returnJsonSchema,
    )

    fun register(function: Function) = functionRegistry.registerFunction(function)

    fun build(): FunctionRegistry = functionRegistry
}


@BuilderMarker
fun registry(actions: FunctionRegistryBuilder.() -> Unit): FunctionRegistry =
    FunctionRegistryBuilder().apply(actions).build()
