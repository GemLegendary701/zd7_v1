package com.example.zd7_v1.ui.login

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.zd7_v1.R
import com.example.zd7_v1.utils.PreferencesManager

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим элементы UI
        val emailEditText = view.findViewById<android.widget.EditText>(R.id.emailEditText)
        val passwordEditText = view.findViewById<android.widget.EditText>(R.id.passwordEditText)
        val roleRadioGroup = view.findViewById<android.widget.RadioGroup>(R.id.roleRadioGroup)
        val errorTextView = view.findViewById<android.widget.TextView>(R.id.errorTextView)
        val loginButton = view.findViewById<android.widget.Button>(R.id.loginButton)

        // Демо-данные для удобства тестирования
        emailEditText.setText("agent@mail.com")
        passwordEditText.setText("123456")
        roleRadioGroup.check(R.id.agentRadioButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val selectedRole = when (roleRadioGroup.checkedRadioButtonId) {
                R.id.agentRadioButton -> "agent"
                R.id.clientRadioButton -> "client"
                else -> ""
            }

            // Скрываем предыдущую ошибку
            errorTextView.visibility = View.GONE

            // Валидация email
            if (!isValidEmail(email)) {
                showError("Введите корректный email адрес", errorTextView)
                return@setOnClickListener
            }

            // Валидация пароля
            if (!isValidPassword(password)) {
                showError("Пароль должен содержать минимум 6 символов", errorTextView)
                return@setOnClickListener
            }

            // Проверка выбора роли
            if (selectedRole.isEmpty()) {
                showError("Выберите роль (Турагент или Клиент)", errorTextView)
                return@setOnClickListener
            }

            // Проверка учетных данных
            val isValidCredentials = checkCredentials(email, password, selectedRole)

            if (isValidCredentials) {
                // Сохраняем данные авторизации
                val prefs = PreferencesManager(requireContext())
                prefs.saveLoginData(email, selectedRole)

                // Переход на главный экран
                findNavController().navigate(R.id.action_loginFragment_to_tourListFragment)

            } else {
                showError("Неверный email, пароль или роль не соответствует", errorTextView)
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    private fun checkCredentials(email: String, password: String, role: String): Boolean {
        // Демо-данные для тестирования
        // В реальном приложении здесь должна быть проверка из базы данных

        return when {
            // Турагент
            email == "agent@mail.com" && password == "123456" && role == "agent" -> true

            // Клиент
            email == "client@mail.com" && password == "123456" && role == "client" -> true

            // Любой другой email с паролем "password" для тестирования
            password == "password" && email.isNotEmpty() -> true

            else -> false
        }
    }

    private fun showError(message: String, errorView: android.widget.TextView) {
        errorView.text = message
        errorView.visibility = View.VISIBLE
    }
}