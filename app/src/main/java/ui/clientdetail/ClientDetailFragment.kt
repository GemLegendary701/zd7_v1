package com.example.zd7_v1.ui.clientdetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zd7_v1.R
import com.example.zd7_v1.TourApplication
import com.example.zd7_v1.ui.shared.OrderAdapter
import com.example.zd7_v1.ui.shared.OrderItem
import com.example.zd7_v1.utils.PreferencesManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClientDetailFragment : Fragment() {

    private lateinit var clientNameTextView: TextView
    private lateinit var clientEmailTextView: TextView
    private lateinit var clientPhoneTextView: TextView
    private lateinit var clientDiscountTextView: TextView
    private lateinit var ordersRecyclerView: androidx.recyclerview.widget.RecyclerView
    private var currentClientId: Long = 0
    private var currentClient: com.example.zd7_v1.data.db.ClientEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_client_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация View элементов
        clientNameTextView = view.findViewById(R.id.clientNameTextView)
        clientEmailTextView = view.findViewById(R.id.clientEmailTextView)
        clientPhoneTextView = view.findViewById(R.id.clientPhoneTextView)
        clientDiscountTextView = view.findViewById(R.id.clientDiscountTextView)

        // Настройка RecyclerView для заказов
        ordersRecyclerView = view.findViewById(R.id.clientOrdersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Проверка роли пользователя
        val prefs = PreferencesManager(requireContext())
        val isAgent = prefs.isAgent()

        // Кнопка редактирования клиента только для турагента
        val editButton = view.findViewById<android.widget.Button>(R.id.editClientButton)
        editButton.visibility = if (isAgent) View.VISIBLE else View.GONE
        editButton.setOnClickListener {
            editClient()
        }

        // Кнопка нового заказа
        view.findViewById<android.widget.Button>(R.id.addOrderButton).setOnClickListener {
            createNewOrder()
        }

        // Загрузка данных клиента
        loadClientData()
    }

    private fun loadClientData() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            currentClientId = requireActivity()
                .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                .getLong("selected_client_id", 1L)

            val client = repository.getClientById(currentClientId)
            currentClient = client

            if (client != null) {
                // Обновляем UI с данными клиента
                requireActivity().runOnUiThread {
                    updateClientUI(client)
                }

                // Загружаем заказы клиента
                loadClientOrders(currentClientId)

            } else {
                // Клиент не найден
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Клиент не найден", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun updateClientUI(client: com.example.zd7_v1.data.db.ClientEntity) {
        clientNameTextView.text = "ФИО: ${client.name}"
        clientEmailTextView.text = "Email: ${client.email}"
        clientPhoneTextView.text = "Телефон: ${client.phone}"
        clientDiscountTextView.text = "Базовая скидка: ${client.discountRate}%"
    }

    private fun loadClientOrders(clientId: Long) {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем заказы клиента с деталями
            val ordersFlow = repository.getOrdersByClient(clientId)
            val orders = ordersFlow.firstOrNull() ?: emptyList()

            if (orders.isNotEmpty()) {
                // Для каждого заказа получаем информацию о туре
                val orderItems = mutableListOf<OrderItem>()

                for (order in orders) {
                    val tour = repository.getTourById(order.tourId)
                    val tourName = tour?.name ?: "Тур #${order.tourId}"

                    orderItems.add(OrderItem(
                        id = order.id.toInt(),
                        tourName = tourName,
                        orderDate = formatDate(order.orderDate),
                        price = "${order.totalPrice.toInt()} ₽ (скидка: ${order.discountApplied.toInt()} ₽)",
                        status = order.status
                    ))
                }

                // Создаем и устанавливаем адаптер
                val adapter = OrderAdapter(orderItems) { clickedOrder ->
                    showOrderDetails(clickedOrder.id)
                }

                requireActivity().runOnUiThread {
                    ordersRecyclerView.adapter = adapter
                }
            } else {
                // Нет заказов
                requireActivity().runOnUiThread {
                    ordersRecyclerView.adapter = null
                }
            }
        }
    }

    private fun formatDate(timestamp: Long): String {
        val date = Date(timestamp)
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

    private fun editClient() {
        currentClient?.let { client ->
            // Создаем и показываем диалог редактирования
            val dialog = EditClientDialog(client) { updatedClient ->
                // Сохраняем изменения в базу данных
                lifecycleScope.launch {
                    val app = requireActivity().application as TourApplication
                    app.repository.updateClient(updatedClient)

                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Данные клиента обновлены",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Обновляем отображение
                        currentClient = updatedClient
                        updateClientUI(updatedClient)
                    }
                }
            }
            dialog.show(parentFragmentManager, "EditClientDialog")
        } ?: run {
            Toast.makeText(requireContext(), "Клиент не найден", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewOrder() {
        // Сохраняем ID клиента для создания заказа
        val clientId = requireActivity()
            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
            .getLong("selected_client_id", 1L)

        requireActivity()
            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
            .edit()
            .putLong("order_for_client_id", clientId)
            .apply()

        // Переходим к списку туров для выбора
        try {
            findNavController().navigate(R.id.action_clientDetailFragment_to_tourListFragment)
        } catch (e: Exception) {
            findNavController().navigate(R.id.tourListFragment)
        }
    }

    private fun showOrderDetails(orderId: Int) {
        // Сохраняем ID заказа
        requireActivity()
            .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
            .edit()
            .putLong("selected_order_id", orderId.toLong())
            .apply()

        // Переходим к деталям заказа
        try {
            findNavController().navigate(R.id.action_clientDetailFragment_to_orderDetailFragment)
        } catch (e: Exception) {
            // Если action не существует, используем напрямую
            findNavController().navigate(R.id.orderDetailFragment)
        }
    }
}