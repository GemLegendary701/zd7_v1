package com.example.zd7_v1.ui.clientlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zd7_v1.R
import com.example.zd7_v1.TourApplication
import com.example.zd7_v1.data.db.ClientEntity
import com.example.zd7_v1.ui.shared.ClientAdapter
import com.example.zd7_v1.ui.shared.ClientItem
import com.example.zd7_v1.utils.PreferencesManager
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ClientListFragment : Fragment() {

    private lateinit var clientsRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var sortSpinner: Spinner
    private var currentSortOption = "По имени"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_client_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализация RecyclerView
        clientsRecyclerView = view.findViewById(R.id.clientsRecyclerView)
        clientsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Проверка роли пользователя
        val prefs = PreferencesManager(requireContext())
        val isAgent = prefs.isAgent()

        // Кнопка добавления клиента только для турагента
        val addClientButton = view.findViewById<android.widget.Button>(R.id.addClientButton)
        addClientButton.visibility = if (isAgent) View.VISIBLE else View.GONE
        addClientButton.setOnClickListener {
            showAddClientDialog()
        }

        // Кнопка выхода
        val logoutButton = view.findViewById<android.widget.Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        // Навигация к турам
        view.findViewById<android.widget.Button>(R.id.toursNavButton).setOnClickListener {
            findNavController().navigate(R.id.action_clientListFragment_to_tourListFragment)
        }

        // Настройка спиннера для сортировки
        sortSpinner = view.findViewById(R.id.sortSpinner)
        setupSortSpinner()

        // Загрузка клиентов
        loadClients()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход из аккаунта")
            .setMessage("Вы действительно хотите выйти из аккаунта?")
            .setPositiveButton("Выйти") { dialog, _ ->
                logoutAndGoToLogin()
                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun logoutAndGoToLogin() {
        val prefs = PreferencesManager(requireContext())
        prefs.logout()

        // Переходим на экран входа
        try {
            findNavController().navigate(R.id.action_clientListFragment_to_loginFragment)
        } catch (e: Exception) {
            // Если action не найден, переходим напрямую
            findNavController().navigate(R.id.loginFragment)
        }

        Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
    }

    private fun setupSortSpinner() {
        val sortOptions = arrayOf("По имени", "По скидке (убыв.)", "По скидке (возр.)", "По дате регистрации")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            sortOptions
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                currentSortOption = sortOptions[position]
                loadClients()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                // Ничего не делаем
            }
        }
    }

    private fun loadClients() {
        lifecycleScope.launch {
            val app = requireActivity().application as TourApplication
            val repository = app.repository

            // Получаем всех клиентов
            val allClientsFlow = repository.getAllClients()
            var clients = allClientsFlow.firstOrNull() ?: emptyList()

            // Применяем сортировку
            clients = when (currentSortOption) {
                "По скидке (убыв.)" -> {
                    clients.sortedByDescending { it.discountRate }
                }
                "По скидке (возр.)" -> {
                    clients.sortedBy { it.discountRate }
                }
                "По дате регистрации" -> {
                    clients.sortedByDescending { it.registrationDate }
                }
                else -> { // "По имени"
                    clients.sortedBy { it.name }
                }
            }

            if (clients.isNotEmpty()) {
                val clientItems = clients.map { client ->
                    ClientItem(
                        id = client.id.toInt(),
                        name = client.name,
                        email = client.email,
                        phone = client.phone,
                        discount = client.discountRate
                    )
                }

                val adapter = ClientAdapter(clientItems) { clickedClient ->
                    requireActivity()
                        .getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                        .edit()
                        .putLong("selected_client_id", clickedClient.id.toLong())
                        .apply()

                    findNavController().navigate(R.id.action_clientListFragment_to_clientDetailFragment)
                }

                requireActivity().runOnUiThread {
                    clientsRecyclerView.adapter = adapter
                }
            } else {
                requireActivity().runOnUiThread {
                    clientsRecyclerView.adapter = null
                }
            }
        }
    }

    private fun showAddClientDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_client, null)

        val nameEditText = dialogView.findViewById<EditText>(R.id.clientNameEditText)
        val emailEditText = dialogView.findViewById<EditText>(R.id.clientEmailEditText)
        val phoneEditText = dialogView.findViewById<EditText>(R.id.clientPhoneEditText)
        val discountEditText = dialogView.findViewById<EditText>(R.id.clientDiscountEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Добавить нового клиента")
            .setView(dialogView)
            .setPositiveButton("Добавить") { dialog, _ ->
                val name = nameEditText.text.toString().trim()
                val email = emailEditText.text.toString().trim()
                val phone = phoneEditText.text.toString().trim()
                val discountText = discountEditText.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(requireContext(), "Введите ФИО клиента", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (email.isEmpty()) {
                    Toast.makeText(requireContext(), "Введите email клиента", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (phone.isEmpty()) {
                    Toast.makeText(requireContext(), "Введите телефон клиента", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val discount = if (discountText.isEmpty()) 0 else discountText.toIntOrNull() ?: 0

                if (discount < 0 || discount > 30) {
                    Toast.makeText(requireContext(), "Скидка должна быть от 0 до 30%", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val client = ClientEntity(
                    name = name,
                    email = email,
                    phone = phone,
                    discountRate = discount
                )

                lifecycleScope.launch {
                    val app = requireActivity().application as TourApplication
                    val clientId = app.repository.insertClient(client)

                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "Клиент добавлен (ID: $clientId)",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadClients()
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}