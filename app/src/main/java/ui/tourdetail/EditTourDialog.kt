package com.example.zd7_v1.ui.tourdetail

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.zd7_v1.R
import com.example.zd7_v1.data.db.TourEntity

class EditTourDialog(
    private val currentTour: TourEntity,
    private val onTourUpdated: (TourEntity) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_edit_tour, null)

        val tourNameEditText = view.findViewById<android.widget.EditText>(R.id.editTourNameEditText)
        val tourCountryEditText = view.findViewById<android.widget.EditText>(R.id.editTourCountryEditText)
        val tourDescriptionEditText = view.findViewById<android.widget.EditText>(R.id.editTourDescriptionEditText)
        val tourPriceEditText = view.findViewById<android.widget.EditText>(R.id.editTourPriceEditText)

        // Заполняем поля текущими значениями тура
        tourNameEditText.setText(currentTour.name)
        tourCountryEditText.setText(currentTour.countryCode)
        tourDescriptionEditText.setText(currentTour.description)
        tourPriceEditText.setText(currentTour.price.toString())

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Редактировать тур")
            .setPositiveButton("Сохранить") { dialog, _ ->
                try {
                    val name = tourNameEditText.text.toString().trim()
                    val country = tourCountryEditText.text.toString().trim().uppercase()
                    val description = tourDescriptionEditText.text.toString().trim()
                    val priceText = tourPriceEditText.text.toString().trim()

                    if (name.isEmpty() || country.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Заполните все поля",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    val price = priceText.toDouble()

                    // Создаем обновленный тур с сохранением старых данных
                    val updatedTour = currentTour.copy(
                        countryCode = country,
                        name = name,
                        description = description,
                        price = price
                    )

                    onTourUpdated(updatedTour)
                    dialog.dismiss()

                } catch (e: NumberFormatException) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Некорректная цена",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    android.widget.Toast.makeText(
                        requireContext(),
                        "Ошибка при сохранении",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}