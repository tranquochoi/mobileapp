package com.example.myaiapp

data class JishoApiResponse(val data: List<JishoWord>)

data class JishoWord(
    val japanese: List<JishoJapanese>,
    val senses: List<JishoSenses>,

)

data class JishoJapanese(val word: String?, val reading: String?)

data class JishoSenses(val english_definitions: List<String>)

