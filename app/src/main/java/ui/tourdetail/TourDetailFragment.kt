package com.example.zd7_v1.ui.tourdetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.zd7_v1.R
import com.example.zd7_v1.TourApplication
import com.example.zd7_v1.data.db.OrderEntity
import com.example.zd7_v1.utils.PreferencesManager
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TourDetailFragment : Fragment() {

    private var currentTourId: Long = 0
    private lateinit var tourNameTextView: android.widget.TextView
    private lateinit var tourCountryTextView: android.widget.TextView
    private lateinit var tourDescriptionTextView: android.widget.TextView
    private lateinit var tourDatesTextView: android.widget.TextView
    private lateinit var tourPriceTextView: android.widget.TextView
    private lateinit var tourStatusTextView: android.widget.TextView
    private lateinit var tourImageView: android.widget.ImageView
    private var currentTour: com.example.zd7_v1.data.db.TourEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tour_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация View элементов
        tourNameTextView = view.findViewById(R.id.tourDetailNameTextView)
        tourCountryTextView = view.findViewById(R.id.tourDetailCountryTextView)
        tourDescriptionTextView = view.findViewById(R.id.tourDetailDescriptionTextView)
        tourDatesTextView = view.findViewById(R.id.tourDetailDatesTextView)
        tourPriceTextView = view.findViewById(R.id.tourDetailPriceTextView)
        tourStatusTextView = view.findViewById(R.id.tourDetailStatusTextView)
        tourImageView = view.findViewById(R.id.tourDetailImageView)

        // Проверка роли пользователя
        val prefs = PreferencesManager(requireContext())
        val isAgent = prefs.isAgent()

        // Находим кнопки
        val editButton = view.findViewById<android.widget.Button>(R.id.editTourButton)
        val bookButton = view.findViewById<android.widget.Button>(R.id.bookTourButton)

        // Показываем кнопки в зависимости от роли
        editButton.visibility = if (isAgent) View.VISIBLE else View.GONE
        bookButton.visibility = if (!isAgent) View.VISIBLE else View.GONE

        // Настройка кнопок
        editButton.setOnClickListener {
            showTourActionsDialog()
        }

        bookButton.setOnClickListener {
            bookTour()
        }

        // Загрузка деталей тура
        loadTourDetails()
    }

    private fun loadTourDetails() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем ID тура из SharedPreferences
            currentTourId = requireActivity()
                .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                .getLong("selected_tour_id", 1L)

            // Получаем тур по ID
            val tour = repository.getTourById(currentTourId)
            currentTour = tour

            if (tour != null) {
                // Получаем информацию о стране
                val country = repository.getCountryByCode(tour.countryCode)

                // Обновляем UI на главном потоке
                requireActivity().runOnUiThread {
                    updateTourUI(tour, country)
                }

            } else {
                // Тур не найден
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Тур не найден", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateTourUI(
        tour: com.example.zd7_v1.data.db.TourEntity,
        country: com.example.zd7_v1.data.db.CountryEntity?
    ) {

        // Заголовок тура
        tourNameTextView.text = tour.name

        // Страна
        val countryName = country?.name ?: tour.countryCode
        tourCountryTextView.text = "Страна: $countryName"

        // Описание
        tourDescriptionTextView.text = "Описание: ${tour.description}"

        // Даты
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val startDate = dateFormat.format(Date(tour.startDate))
        val endDate = dateFormat.format(Date(tour.endDate))
        tourDatesTextView.text = "Даты: $startDate - $endDate"

        // Цена
        tourPriceTextView.text = "Цена: ${tour.price.toInt()} ₽"

        // Статус
        if (tour.isAvailable) {
            tourStatusTextView.text = "✅ Доступен"
            tourStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
        } else {
            tourStatusTextView.text = "❌ Не доступен"
            tourStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null))
        }

        // Загрузка изображения (флаг страны или изображение тура)
        val imageUrl = if (country?.flagUrl != null) {
            country.flagUrl
        } else {
            "https://flagcdn.com/w320/${tour.countryCode.lowercase()}.png"
        }

        Picasso.get()
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(tourImageView)

        // Сохраняем информацию о туре для возможного редактирования
        saveTourInfoForEditing(tour)
    }

    private fun saveTourInfoForEditing(tour: com.example.zd7_v1.data.db.TourEntity) {
        requireActivity()
            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("current_tour_name", tour.name)
            .putString("current_tour_country", tour.countryCode)
            .putString("current_tour_description", tour.description)
            .putString("current_tour_price", tour.price.toString())
            .putLong("current_tour_id", tour.id)
            .apply()
    }

    private fun showTourActionsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Действия с туром")
            .setItems(arrayOf("Редактировать", "Удалить", "Изменить доступность", "Отмена")) { dialog, which ->
                when (which) {
                    0 -> editTour()           // Редактировать
                    1 -> deleteTour()         // Удалить
                    2 -> toggleTourAvailability() // Изменить доступность
                    3 -> dialog.dismiss()     // Отмена
                }
            }
            .show()
    }

    private fun editTour() {
        currentTour?.let { tour ->
            // Создаем и показываем диалог редактирования
            val dialog = EditTourDialog(tour) { updatedTour ->
                // Сохраняем изменения в базу данных
                lifecycleScope.launch {
                    val app = requireActivity().application as TourApplication
                    app.repository.updateTour(updatedTour)

                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Тур успешно обновлен",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Обновляем отображение
                        currentTour = updatedTour
                        loadTourDetails() // Перезагружаем данные тура
                    }
                }
            }
            dialog.show(parentFragmentManager, "EditTourDialog")
        } ?: run {
            Toast.makeText(requireContext(), "Тур не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTour() {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление тура")
            .setMessage("Вы уверены, что хотите удалить этот тур?")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    val app = requireActivity().application as TourApplication
                    app.repository.deleteTour(currentTourId)

                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Тур удален", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun toggleTourAvailability() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val tour = app.repository.getTourById(currentTourId)

            tour?.let {
                val newAvailability = !it.isAvailable
                app.repository.updateTourAvailability(currentTourId, newAvailability)

                requireActivity().runOnUiThread {
                    val message = if (newAvailability) "Тур теперь доступен" else "Тур недоступен"
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                    // Обновляем отображение
                    loadTourDetails()
                }
            }
        }
    }

    private fun bookTour() {
        // Проверяем, есть ли сохраненный ID клиента для быстрого заказа
        val savedClientId = requireActivity()
            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
            .getLong("order_for_client_id", 0L)

        if (savedClientId > 0) {
            // Если есть сохраненный ID клиента (из ClientDetailFragment)
            createOrderForClient(savedClientId)

            // Очищаем сохраненный ID после использования
            requireActivity()
                .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                .edit()
                .remove("order_for_client_id")
                .apply()
        } else {
            // Иначе показываем диалог выбора клиента
            showClientSelectionDialog()
        }
    }

    private fun showClientSelectionDialog() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем список клиентов
            val clientsFlow = repository.getAllClients()
            val clients = clientsFlow.firstOrNull() ?: emptyList()

            if (clients.isEmpty()) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Нет клиентов в базе", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Создаем список имен клиентов для диалога
            val clientNames = Array(clients.size) { index ->
                val client = clients[index]
                "${client.name} (${client.email})"
            }

            requireActivity().runOnUiThread {
                AlertDialog.Builder(requireContext())
                    .setTitle("Выберите клиента для бронирования")
                    .setItems(clientNames) { dialog, which ->
                        val selectedClient = clients[which]
                        // Сохраняем ID выбранного клиента
                        requireActivity()
                            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putLong("selected_client_id", selectedClient.id)
                            .apply()

                        // Создаем заказ для выбранного клиента
                        createOrderForClient(selectedClient.id)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Отмена") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    private fun createOrderForClient(clientId: Long) {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем текущий тур
            val tour = repository.getTourById(currentTourId)

            if (tour == null || !tour.isAvailable) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Тур недоступен для бронирования", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Получаем клиента по ID
            val client = repository.getClientById(clientId)

            if (client == null) {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Клиент не найден", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            // Рассчитываем скидку для ЭТОГО клиента
            val discountRate = repository.calculateClientDiscount(clientId)
            val discountedPrice = tour.price * (100 - discountRate) / 100
            val discountAmount = tour.price * discountRate / 100

            // Создаем заказ для ЭТОГО клиента
            val order = OrderEntity(
                clientId = clientId, // Важно: используем ID выбранного клиента
                tourId = currentTourId,
                totalPrice = discountedPrice,
                discountApplied = discountAmount,
                status = "NEW",
                notes = "Бронирование для: ${client.name}"
            )

            val orderId = repository.insertOrder(order)

            requireActivity().runOnUiThread {
                // Показываем имя клиента, для которого создан заказ
                Toast.makeText(
                    requireContext(),
                    "✅ Тур забронирован для: ${client.name}!\nНомер заказа: $orderId\nСкидка: $discountRate%\nЦена со скидкой: ${discountedPrice.toInt()} ₽",
                    Toast.LENGTH_LONG
                ).show()

                // Предлагаем перейти к заказам клиента
                AlertDialog.Builder(requireContext())
                    .setTitle("Бронирование успешно")
                    .setMessage("Заказ #$orderId создан для ${client.name}. Хотите перейти к списку заказов клиента?")
                    .setPositiveButton("Да") { _, _ ->
                        // Сохраняем ID клиента для ClientDetailFragment
                        requireActivity()
                            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                            .edit()
                            .putLong("selected_client_id", clientId)
                            .apply()

                        // Переходим к деталям клиента
                        try {
                            findNavController().navigate(R.id.action_tourDetailFragment_to_clientDetailFragment)
                        } catch (e: Exception) {
                            findNavController().navigate(R.id.clientListFragment)
                        }
                    }
                    .setNegativeButton("Нет") { dialog, _ ->
                        dialog.dismiss()
                        findNavController().popBackStack()
                    }
                    .show()
            }
        }
    }
}