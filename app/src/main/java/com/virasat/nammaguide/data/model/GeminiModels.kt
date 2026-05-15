package com.virasat.nammaguide.data.model

data class GeminiRequest(
    val contents: List<GContent>,
    val systemInstruction: SystemInstruction? = null,
    val generationConfig: GenerationConfig? = null
)

data class GContent(
    val role: String,
    val parts: List<GPart>
)

data class GPart(val text: String)

data class SystemInstruction(val parts: List<GPart>)

data class GenerationConfig(
    val temperature: Double = 0.7,
    val maxOutputTokens: Int = 1024
)

data class GeminiResponse(
    val candidates: List<GCandidate>?
)

data class GCandidate(val content: GContent)
