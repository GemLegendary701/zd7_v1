package com.example.zd7_v1.data.db

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*

object DatabaseInitializer {
    private const val TAG = "DatabaseInitializer"

    fun initialize(database: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "🚀 Начало инициализации базы данных...")

            try {
                // 1. Проверяем и добавляем демо-страны
                addDemoCountries(database)

                // 2. Проверяем и добавляем демо-туры
                addDemoTours(database)

                // 3. Проверяем и добавляем демо-клиентов
                addDemoClients(database)

                // 4. Проверяем и добавляем демо-заказы
                addDemoOrders(database)

                Log.d(TAG, "✅ База данных успешно инициализирована")

            } catch (e: Exception) {
                Log.e(TAG, "❌ Ошибка инициализации базы данных", e)
            }
        }
    }

    private suspend fun addDemoCountries(database: AppDatabase) {
        // Проверяем, есть ли уже страны
        val existingCount = database.countryDao().getCountriesCount()

        if (existingCount > 0) {
            Log.d(TAG, "⚠️ Страны уже существуют ($existingCount шт.), пропускаем")
            return
        }

        val countries = listOf(
            CountryEntity(
                countryCode = "TR", name = "Турция", flagUrl = "https://flagcdn.com/w320/tr.png",
                capital = "Анкара", population = 84339067, region = "Азия", subregion = "Западная Азия"
            ),
            CountryEntity(
                countryCode = "IT", name = "Италия", flagUrl = "https://flagcdn.com/w320/it.png",
                capital = "Рим", population = 59554023, region = "Европа", subregion = "Южная Европа"
            ),
            CountryEntity(
                countryCode = "JP", name = "Япония", flagUrl = "https://flagcdn.com/w320/jp.png",
                capital = "Токио", population = 125836021, region = "Азия", subregion = "Восточная Азия"
            ),
            CountryEntity(
                countryCode = "GR", name = "Греция", flagUrl = "https://flagcdn.com/w320/gr.png",
                capital = "Афины", population = 10715549, region = "Европа", subregion = "Южная Европа"
            ),
            CountryEntity(
                countryCode = "FR", name = "Франция", flagUrl = "https://flagcdn.com/w320/fr.png",
                capital = "Париж", population = 67391582, region = "Европа", subregion = "Западная Европа"
            ),
            CountryEntity(
                countryCode = "ES", name = "Испания", flagUrl = "https://flagcdn.com/w320/es.png",
                capital = "Мадрид", population = 47351567, region = "Европа", subregion = "Южная Европа"
            ),
            CountryEntity(
                countryCode = "TH", name = "Таиланд", flagUrl = "https://flagcdn.com/w320/th.png",
                capital = "Бангкок", population = 69799978, region = "Азия", subregion = "Юго-Восточная Азия"
            ),
            CountryEntity(
                countryCode = "EG", name = "Египет", flagUrl = "https://flagcdn.com/w320/eg.png",
                capital = "Каир", population = 102334404, region = "Африка", subregion = "Северная Африка"
            )
        )

        database.countryDao().insertAllCountries(countries)
        Log.d(TAG, "✅ Добавлено ${countries.size} демо-стран")
    }

    private suspend fun addDemoTours(database: AppDatabase) {
        // Проверяем, есть ли уже туры
        val existingTours = database.tourDao().getAllTours().firstOrNull()

        if (!existingTours.isNullOrEmpty()) {
            Log.d(TAG, "⚠️ Туры уже существуют (${existingTours.size} шт.), пропускаем")
            return
        }

        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val tours = listOf(
            TourEntity(
                countryCode = "TR", name = "Отдых в Турции: Анталия",
                description = "Отдых на море в отеле 5* с питанием все включено.",
                price = 45000.0, startDate = currentTime + 2592000000L, // +30 дней
                endDate = currentTime + 3888000000L, // +45 дней
                isAvailable = true, imageUrl = "https://example.com/turkey.jpg",
                maxParticipants = 50, currentParticipants = 12
            ),
            TourEntity(
                countryCode = "IT", name = "Экскурсия по Италии",
                description = "Тур по историческим местам Италии с гидом.",
                price = 78000.0, startDate = currentTime + 3888000000L,
                endDate = currentTime + 5184000000L, // +60 дней
                isAvailable = true, imageUrl = "https://example.com/italy.jpg",
                maxParticipants = 30, currentParticipants = 8
            ),
            TourEntity(
                countryCode = "JP", name = "Культурный тур по Японии",
                description = "Знакомство с культурой Японии.",
                price = 120000.0, startDate = currentTime + 7776000000L, // +90 дней
                endDate = currentTime + 9072000000L, // +105 дней
                isAvailable = true, imageUrl = "https://example.com/japan.jpg",
                maxParticipants = 20, currentParticipants = 5
            )
        )

        tours.forEach { database.tourDao().insertTour(it) }
        Log.d(TAG, "✅ Добавлено ${tours.size} демо-туров")
    }

    private suspend fun addDemoClients(database: AppDatabase) {
        // Проверяем, есть ли уже клиенты
        val existingClients = database.clientDao().getAllClients().firstOrNull()

        if (!existingClients.isNullOrEmpty()) {
            Log.d(TAG, "⚠️ Клиенты уже существуют (${existingClients.size} шт.), пропускаем")
            return
        }

        val clients = listOf(
            ClientEntity(
                name = "Иванов Иван Иванович",
                email = "ivanov@mail.com",
                phone = "+7 (999) 123-45-67",
                discountRate = 10,
                notes = "Постоянный клиент"
            ),
            ClientEntity(
                name = "Петрова Анна Сергеевна",
                email = "petrova@mail.com",
                phone = "+7 (999) 234-56-78",
                discountRate = 5,
                notes = "Предпочитает экскурсионные туры"
            ),
            ClientEntity(
                name = "Сидоров Алексей Петрович",
                email = "sidorov@mail.com",
                phone = "+7 (999) 345-67-89",
                discountRate = 15,
                notes = "VIP клиент"
            ),
            ClientEntity(
                name = "Кузнецова Мария Владимировна",
                email = "kuznetsova@mail.com",
                phone = "+7 (999) 456-78-90",
                discountRate = 0,
                notes = "Новый клиент"
            ),
            ClientEntity(
                name = "Васильев Дмитрий Олегович",
                email = "vasiliev@mail.com",
                phone = "+7 (999) 567-89-01",
                discountRate = 8,
                notes = "Бизнес-клиент"
            )
        )

        clients.forEach { database.clientDao().insertClient(it) }
        Log.d(TAG, "✅ Добавлено ${clients.size} демо-клиентов")
    }

    private suspend fun addDemoOrders(database: AppDatabase) {
        // Проверяем, есть ли уже заказы
        val existingOrders = database.orderDao().getAllOrders().firstOrNull()

        if (!existingOrders.isNullOrEmpty()) {
            Log.d(TAG, "⚠️ Заказы уже существуют (${existingOrders.size} шт.), пропускаем")
            return
        }

        // Получаем всех клиентов
        val allClients = database.clientDao().getAllClients().firstOrNull() ?: emptyList()

        // Получаем все туры
        val tourTurkey = database.tourDao().getToursByCountry("TR").firstOrNull()?.firstOrNull()
        val tourItaly = database.tourDao().getToursByCountry("IT").firstOrNull()?.firstOrNull()
        val tourJapan = database.tourDao().getToursByCountry("JP").firstOrNull()?.firstOrNull()

        val orders = mutableListOf<OrderEntity>()

        // Распределяем заказы между разными клиентами
        allClients.forEachIndexed { index, client ->
            val tour = when (index % 3) {
                0 -> tourTurkey
                1 -> tourItaly
                else -> tourJapan
            }

            tour?.let {
                orders.add(
                    OrderEntity(
                        clientId = client.id, // У каждого клиента свой ID!
                        tourId = it.id,
                        totalPrice = it.price * (100 - client.discountRate) / 100,
                        discountApplied = it.price * client.discountRate / 100,
                        status = when (index % 3) {
                            0 -> "NEW"
                            1 -> "CONFIRMED"
                            else -> "COMPLETED"
                        },
                        notes = "Заказ клиента: ${client.name}"
                    )
                )
            }
        }

        orders.forEach { database.orderDao().insertOrder(it) }

        if (orders.isNotEmpty()) {
            Log.d(TAG, "✅ Добавлено ${orders.size} демо-заказов")
        } else {
            Log.d(TAG, "⚠️ Не удалось добавить демо-заказы")
        }
    }
}