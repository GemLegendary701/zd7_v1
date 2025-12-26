package com.example.zd7_v1.data.api

import com.google.gson.annotations.SerializedName

data class CountryApiResponse(
    @SerializedName("name")
    val name: CountryName,

    @SerializedName("cca2")
    val countryCode: String,

    @SerializedName("flags")
    val flags: CountryFlags,

    @SerializedName("capital")
    val capital: List<String>? = null,

    @SerializedName("population")
    val population: Long? = null,

    @SerializedName("region")
    val region: String? = null,

    @SerializedName("subregion")
    val subregion: String? = null,

    @SerializedName("languages")
    val languages: Map<String, String>? = null,

    @SerializedName("currencies")
    val currencies: Map<String, Currency>? = null,

    @SerializedName("timezones")
    val timezones: List<String>? = null,

    @SerializedName("maps")
    val maps: Maps? = null
    // УБРАЛ дублирование: @SerializedName("flags") val flagUrls: CountryFlags? = null
)

data class CountryName(
    @SerializedName("common")
    val common: String,

    @SerializedName("official")
    val official: String,

    @SerializedName("nativeName")
    val nativeName: Map<String, NativeName>? = null
)

data class NativeName(
    @SerializedName("official")
    val official: String,

    @SerializedName("common")
    val common: String
)

data class CountryFlags(
    @SerializedName("png")
    val png: String,

    @SerializedName("svg")
    val svg: String,

    @SerializedName("alt")
    val alt: String? = null
)

data class Currency(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("symbol")
    val symbol: String? = null
)

data class Maps(
    @SerializedName("googleMaps")
    val googleMaps: String? = null,

    @SerializedName("openStreetMaps")
    val openStreetMaps: String? = null
)

// Упрощенная версия для списка стран
data class SimpleCountry(
    val name: String,
    val code: String,
    val flagUrl: String
)

// Функция для преобразования API ответа в упрощенный формат
fun CountryApiResponse.toSimpleCountry(): SimpleCountry {
    return SimpleCountry(
        name = this.name.common,
        code = this.countryCode,
        flagUrl = this.flags.png
    )
}

// Функция для получения первого названия столицы
fun CountryApiResponse.getCapital(): String {
    return this.capital?.firstOrNull() ?: "Не указана"
}

// Функция для получения формата населения
fun CountryApiResponse.getFormattedPopulation(): String {
    return this.population?.let {
        String.format("%,d", it).replace(",", " ")
    } ?: "Не указано"
}

// Функция для получения основных языков
fun CountryApiResponse.getLanguages(): String {
    return this.languages?.values?.joinToString(", ") ?: "Не указаны"
}

// Функция для получения основных валют
fun CountryApiResponse.getCurrencies(): String {
    return this.currencies?.values?.joinToString(", ") {
        "${it.name ?: "Неизвестно"} (${it.symbol ?: "?"})"
    } ?: "Не указаны"
}