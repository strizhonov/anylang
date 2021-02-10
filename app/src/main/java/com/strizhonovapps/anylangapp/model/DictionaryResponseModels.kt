package com.strizhonovapps.anylangapp.model

data class DictionaryResponse(val head: Any?,
                              val def: List<DictionaryDefinition>)

data class DictionaryDefinition(var text: String?,
                                var pos: String?,
                                var tr: List<DictionaryTranslation>?)

data class DictionaryTranslation(var text: String?,
                                 var pos: String?,
                                 var syn: List<DictionaryTextItem>?,
                                 var mean: List<DictionaryTextItem>?,
                                 var ex: List<DictionaryExample>?)

data class DictionaryExample(var text: String?,
                             var tr: List<DictionaryTextItem>?)

data class DictionaryTextItem(var text: String?)