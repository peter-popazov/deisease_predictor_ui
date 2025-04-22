package com.peter.rgr.data

data class CognitiveSymptoms(
    val memoryProblems: Boolean = false,
    val languageProblems: Boolean = false,
    val attentionProblems: Boolean = false,
    val executiveFunctionProblems: Boolean = false,
    val visuospatialProblems: Boolean = false,
    val socialCognitionProblems: Boolean = false,
    val otherSymptoms: String = ""
) 