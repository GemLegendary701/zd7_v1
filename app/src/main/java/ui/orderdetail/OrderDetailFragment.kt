package com.example.zd7_v1.ui.orderdetail

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
import com.example.zd7_v1.utils.PreferencesManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailFragment : Fragment() {

    private lateinit var orderNumberTextView: android.widget.TextView
    private lateinit var orderStatusTextView: android.widget.TextView
    private lateinit var orderDateTextView: android.widget.TextView
    private lateinit var clientNameTextView: android.widget.TextView
    private lateinit var tourNameTextView: android.widget.TextView
    private lateinit var orderPriceTextView: android.widget.TextView
    private lateinit var discountTextView: android.widget.TextView
    private lateinit var finalPriceTextView: android.widget.TextView
    private lateinit var notesContentTextView: android.widget.TextView
    private lateinit var changeStatusButton: android.widget.Button

    private var currentOrderId: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_order_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация View элементов
        orderNumberTextView = view.findViewById(R.id.orderNumberTextView)
        orderStatusTextView = view.findViewById(R.id.orderStatusTextView)
        orderDateTextView = view.findViewById(R.id.orderDateTextView)
        clientNameTextView = view.findViewById(R.id.clientNameTextView)
        tourNameTextView = view.findViewById(R.id.tourNameTextView)
        orderPriceTextView = view.findViewById(R.id.orderPriceTextView)
        discountTextView = view.findViewById(R.id.discountTextView)
        finalPriceTextView = view.findViewById(R.id.finalPriceTextView)
        notesContentTextView = view.findViewById(R.id.notesContentTextView)
        changeStatusButton = view.findViewById(R.id.changeStatusButton)

        // Проверка роли пользователя
        val prefs = PreferencesManager(requireContext())
        val isAgent = prefs.isAgent()

        // Показываем кнопку изменения статуса только для турагента
        changeStatusButton.visibility = if (isAgent) View.VISIBLE else View.GONE
        changeStatusButton.setOnClickListener {
            showStatusChangeDialog()
        }

        // Кнопка назад
        view.findViewById<android.widget.Button>(R.id.backButton).setOnClickListener {
            findNavController().popBackStack()
        }

        // Загрузка деталей заказа
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем ID заказа из SharedPreferences
            currentOrderId = requireActivity()
                .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                .getLong("selected_order_id", 0L)

            if (currentOrderId == 0L) {
                Toast.makeText(requireContext(), "Заказ не найден", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
                return@launch
            }

            // Получаем заказ с деталями
            val orderWithDetails = repository.getOrderWithDetails(currentOrderId)

            if (orderWithDetails != null) {
                // Обновляем UI на главном потоке
                requireActivity().runOnUiThread {
                    updateOrderUI(orderWithDetails)
                }
            } else {
                // Заказ не найден
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Заказ не найден", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateOrderUI(orderWithDetails: com.example.zd7_v1.data.repository.TourRepository.OrderWithDetails) {
        val order = orderWithDetails.order
        val client = orderWithDetails.client
        val tour = orderWithDetails.tour

        // Номер заказа
        orderNumberTextView.text = "Заказ #${order.id}"

        // Статус заказа
        orderStatusTextView.text = getStatusText(order.status)
        updateStatusColor(order.status)

        // Дата заказа
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        orderDateTextView.text = "Дата заказа: ${dateFormat.format(Date(order.orderDate))}"

        // Клиент
        clientNameTextView.text = "Клиент: ${client?.name ?: "Неизвестен"}"

        // Тур
        tourNameTextView.text = "Тур: ${tour?.name ?: "Неизвестен"}"

        // Цена и скидка
        val originalPrice = (order.totalPrice + order.discountApplied).toInt()
        orderPriceTextView.text = "Стоимость: $originalPrice ₽"
        discountTextView.text = "Скидка: ${order.discountApplied.toInt()} ₽"
        finalPriceTextView.text = "Итоговая сумма: ${order.totalPrice.toInt()} ₽"

        // Примечания
        notesContentTextView.text = order.notes ?: "Нет примечаний"
    }

    private fun getStatusText(status: String): String {
        return when (status.uppercase()) {
            "NEW" -> "НОВЫЙ"
            "CONFIRMED" -> "ПОДТВЕРЖДЕН"
            "CANCELLED" -> "ОТМЕНЕН"
            "COMPLETED" -> "ЗАВЕРШЕН"
            else -> status
        }
    }

    private fun updateStatusColor(status: String) {
        when (status.uppercase()) {
            "NEW" -> orderStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_blue_dark, null))
            "CONFIRMED" -> orderStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
            "CANCELLED" -> orderStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_red_dark, null))
            "COMPLETED" -> orderStatusTextView.setBackgroundColor(resources.getColor(android.R.color.holo_orange_dark, null))
        }
    }

    private fun showStatusChangeDialog() {
        val statusOptions = arrayOf("НОВЫЙ", "ПОДТВЕРЖДЕН", "ОТМЕНЕН", "ЗАВЕРШЕН")
        val statusValues = arrayOf("NEW", "CONFIRMED", "CANCELLED", "COMPLETED")

        AlertDialog.Builder(requireContext())
            .setTitle("Изменить статус заказа")
            .setItems(statusOptions) { dialog, which ->
                val newStatus = statusValues[which]
                updateOrderStatus(newStatus)
                dialog.dismiss()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateOrderStatus(newStatus: String) {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            repository.updateOrderStatus(currentOrderId, newStatus)

            requireActivity().runOnUiThread {
                Toast.makeText(requireContext(), "Статус заказа обновлен", Toast.LENGTH_SHORT).show()

                // Обновляем отображение
                loadOrderDetails()
            }
        }
    }
}