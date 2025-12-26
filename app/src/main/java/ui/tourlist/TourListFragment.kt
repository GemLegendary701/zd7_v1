package com.example.zd7_v1.ui.tourlist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.zd7_v1.R
import com.example.zd7_v1.TourApplication
import com.example.zd7_v1.ui.TourViewModelFactory
import com.example.zd7_v1.ui.shared.TourAdapter
import com.example.zd7_v1.ui.shared.TourItem
import com.example.zd7_v1.utils.PreferencesManager
import kotlinx.coroutines.*

class TourListFragment : Fragment() {

    private lateinit var toursRecyclerView: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewModel: TourListViewModel
    private lateinit var searchEditText: android.widget.EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_tour_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val app = requireActivity().application as TourApplication
        viewModel = ViewModelProvider(this, TourViewModelFactory(app)).get(TourListViewModel::class.java)

        // Инициализация UI
        toursRecyclerView = view.findViewById(R.id.toursRecyclerView)
        toursRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchEditText = view.findViewById(R.id.searchEditText)

        // Проверка роли пользователя
        val prefs = PreferencesManager(requireContext())
        val isAgent = prefs.isAgent()

        // Кнопка добавления тура только для турагента
        val addTourButton = view.findViewById<android.widget.Button>(R.id.addTourButton)
        addTourButton.visibility = if (isAgent) View.VISIBLE else View.GONE
        addTourButton.setOnClickListener {
            showAddTourDialog()
        }

        // Кнопка выхода
        val logoutButton = view.findViewById<android.widget.Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        // Навигация к клиентам
        val clientsButton = view.findViewById<android.widget.Button>(R.id.clientsButton)
        clientsButton.setOnClickListener {
            findNavController().navigate(R.id.action_tourListFragment_to_clientListFragment)
        }

        // Настройка поиска
        setupSearch()

        // Загрузка туров
        loadTours()
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
            findNavController().navigate(R.id.action_tourListFragment_to_loginFragment)
        } catch (e: Exception) {
            // Если action не найден, переходим напрямую
            findNavController().navigate(R.id.loginFragment)
        }

        Toast.makeText(requireContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show()
    }

    private fun setupSearch() {
        var searchJob: Job? = null

        searchEditText.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                searchJob?.cancel()
                searchJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    loadTours()
                }
            }
        })
    }

    private fun loadTours() {
        val query = searchEditText.text.toString().trim()

        if (query.isNotEmpty()) {
            viewModel.searchTours(query).observe(viewLifecycleOwner) { tours ->
                displayTours(tours)
            }
        } else {
            viewModel.allTours.observe(viewLifecycleOwner) { tours ->
                displayTours(tours)
            }
        }
    }

    private fun displayTours(tours: List<com.example.zd7_v1.data.db.TourEntity>) {
        if (tours.isEmpty()) {
            toursRecyclerView.adapter = null
            return
        }

        val tourItems = tours.map { tour ->
            TourItem(
                id = tour.id.toInt(),
                name = tour.name,
                country = tour.countryCode,
                dates = formatTourDates(tour.startDate, tour.endDate),
                price = "${tour.price.toInt()} ₽",
                isAvailable = tour.isAvailable
            )
        }

        val adapter = TourAdapter(tourItems) { clickedTour: TourItem ->
            requireActivity().getSharedPreferences("temp_data", android.content.Context.MODE_PRIVATE)
                .edit()
                .putLong("selected_tour_id", clickedTour.id.toLong())
                .apply()
            findNavController().navigate(R.id.action_tourListFragment_to_tourDetailFragment)
        }

        toursRecyclerView.adapter = adapter
    }

    private fun formatTourDates(startDate: Long, endDate: Long): String {
        val formatter = java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        return "${formatter.format(java.util.Date(startDate))} - ${formatter.format(java.util.Date(endDate))}"
    }

    private fun showAddTourDialog() {
        val dialog = AddTourDialog { tour ->
            lifecycleScope.launch(Dispatchers.IO) {
                val app = requireActivity().application as TourApplication
                val tourId = app.repository.insertTour(tour)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Тур добавлен (ID: $tourId)",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadTours()
                }
            }
        }
        dialog.show(parentFragmentManager, "AddTourDialog")
    }
}