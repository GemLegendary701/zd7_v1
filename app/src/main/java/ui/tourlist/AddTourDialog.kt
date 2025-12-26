package com.example.zd7_v1.ui.tourlist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.zd7_v1.R
import com.example.zd7_v1.data.db.TourEntity
import java.util.Calendar

class AddTourDialog(
    private val onTourAdded: (TourEntity) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_add_tour, null)

        val tourNameEditText = view.findViewById<android.widget.EditText>(R.id.tourNameEditText)
        val tourCountryEditText = view.findViewById<android.widget.EditText>(R.id.tourCountryEditText)
        val tourDescriptionEditText = view.findViewById<android.widget.EditText>(R.id.tourDescriptionEditText)
        val tourPriceEditText = view.findViewById<android.widget.EditText>(R.id.tourPriceEditText)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Добавить новый тур")
            .setPositiveButton("Добавить") { dialog, _ ->
                try {
                    val name = tourNameEditText.text.toString().trim()
                    val country = tourCountryEditText.text.toString().trim().uppercase()
                    val description = tourDescriptionEditText.text.toString().trim()
                    val priceText = tourPriceEditText.text.toString().trim()

                    if (name.isEmpty() || country.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                        return@setPositiveButton
                    }

                    val price = priceText.toDouble()

                    val tour = TourEntity(
                        countryCode = country,
                        name = name,
                        description = description,
                        price = price,
                        startDate = Calendar.getInstance().timeInMillis + 30L * 24 * 60 * 60 * 1000,
                        endDate = Calendar.getInstance().timeInMillis + 45L * 24 * 60 * 60 * 1000,
                        isAvailable = true
                    )

                    onTourAdded(tour)
                    dialog.dismiss()

                } catch (e: Exception) {
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}