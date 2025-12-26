package com.example.zd7_v1.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountryApiService {

    /**
     * Получить все страны
     * @return List<CountryApiResponse> список всех стран
     */
    @GET("all")
    suspend fun getAllCountries(): List<CountryApiResponse>

    /**
     * Получить страну по коду (2 или 3 буквы)
     * @param code Код страны (например: "ru", "us", "de")
     * @return List<CountryApiResponse> список с одной страной
     */
    @GET("alpha/{code}")
    suspend fun getCountryByCode(@Path("code") code: String): List<CountryApiResponse>

    /**
     * Получить несколько стран по кодам
     * @param codes Коды стран через запятую (например: "ru,us,de")
     * @return List<CountryApiResponse> список стран
     */
    @GET("alpha")
    suspend fun getCountriesByCodes(@Query("codes") codes: String): List<CountryApiResponse>

    /**
     * Получить страны по региону
     * @param region Регион (например: "europe", "asia", "africa")
     * @return List<CountryApiResponse> список стран региона
     */
    @GET("region/{region}")
    suspend fun getCountriesByRegion(@Path("region") region: String): List<CountryApiResponse>

    /**
     * Получить страны по субрегиону
     * @param subregion Субрегион (например: "eastern-europe", "western-europe")
     * @return List<CountryApiResponse> список стран субрегиона
     */
    @GET("subregion/{subregion}")
    suspend fun getCountriesBySubregion(@Path("subregion") subregion: String): List<CountryApiResponse>

    /**
     * Поиск стран по имени
     * @param name Название страны или часть названия
     * @param fullText Если true - полное совпадение имени
     * @return List<CountryApiResponse> список найденных стран
     */
    @GET("name/{name}")
    suspend fun searchCountries(
        @Path("name") name: String,
        @Query("fullText") fullText: Boolean = false
    ): List<CountryApiResponse>

    /**
     * Получить страны по языку
     * @param language Код языка (например: "rus", "eng", "spa")
     * @return List<CountryApiResponse> список стран, где говорят на этом языке
     */
    @GET("lang/{lang}")
    suspend fun getCountriesByLanguage(@Path("lang") language: String): List<CountryApiResponse>

    /**
     * Получить страны по валюте
     * @param currency Код валюты (например: "RUB", "USD", "EUR")
     * @return List<CountryApiResponse> список стран, использующих эту валюту
     */
    @GET("currency/{currency}")
    suspend fun getCountriesByCurrency(@Path("currency") currency: String): List<CountryApiResponse>

    /**
     * Получить страны по столице
     * @param capital Название столицы
     * @return List<CountryApiResponse> список стран с этой столицей
     */
    @GET("capital/{capital}")
    suspend fun getCountriesByCapital(@Path("capital") capital: String): List<CountryApiResponse>

    /**
     * Получить страны по коду вызова
     * @param callingCode Код вызова (например: "7" для России, "1" для США)
     * @return List<CountryApiResponse> список стран с этим кодом вызова
     */
    @GET("callingcode/{code}")
    suspend fun getCountriesByCallingCode(@Path("code") callingCode: String): List<CountryApiResponse>
}