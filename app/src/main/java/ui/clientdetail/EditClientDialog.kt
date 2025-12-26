package com.example.zd7_v1.ui.clientdetail

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.zd7_v1.R
import com.example.zd7_v1.data.db.ClientEntity

class EditClientDialog(
    private val currentClient: ClientEntity,
    private val onClientUpdated: (ClientEntity) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.dialog_edit_client, null)

        val nameEditText = view.findViewById<android.widget.EditText>(R.id.editClientNameText)
        val emailEditText = view.findViewById<android.widget.EditText>(R.id.editClientEmailText)
        val phoneEditText = view.findViewById<android.widget.EditText>(R.id.editClientPhoneText)
        val discountEditText = view.findViewById<android.widget.EditText>(R.id.editClientDiscountText)

        // Заполняем поля текущими значениями клиента
        nameEditText.setText(currentClient.name)
        emailEditText.setText(currentClient.email)
        phoneEditText.setText(currentClient.phone)
        discountEditText.setText(currentClient.discountRate.toString())

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Редактировать клиента")
            .setPositiveButton("Сохранить") { dialog, _ ->
                try {
                    val name = nameEditText.text.toString().trim()
                    val email = emailEditText.text.toString().trim()
                    val phone = phoneEditText.text.toString().trim()
                    val discountText = discountEditText.text.toString().trim()

                    // Валидация
                    if (name.isEmpty()) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Введите ФИО клиента",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    if (email.isEmpty()) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Введите email клиента",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    if (phone.isEmpty()) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Введите телефон клиента",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    val discount = if (discountText.isEmpty()) 0 else discountText.toIntOrNull() ?: 0

                    // Проверяем, что скидка в допустимых пределах
                    if (discount < 0 || discount > 30) {
                        android.widget.Toast.makeText(
                            requireContext(),
                            "Скидка должна быть от 0 до 30%",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                        return@setPositiveButton
                    }

                    // Создаем обновленного клиента с сохранением старых данных
                    val updatedClient = currentClient.copy(
                        name = name,
                        email = email,
                        phone = phone,
                        discountRate = discount
                    )

                    onClientUpdated(updatedClient)
                    dialog.dismiss()

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