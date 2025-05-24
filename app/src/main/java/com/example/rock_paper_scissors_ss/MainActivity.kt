package com.example.rock_paper_scissors_ss

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    // Объявление переменных для элементов интерфейса
    private lateinit var resultTextView: TextView      // Текстовое поле для отображения результата
    private lateinit var computerChoiceImageView: ImageView  // Изображение выбора компьютера
    private lateinit var playerChoiceImageView: ImageView    // Изображение выбора игрока
    private lateinit var playButton: Button            // Кнопка "Играть"
    private lateinit var handler: Handler              // Обработчик для выполнения отложенных задач

    // Список всех кнопок выбора (камень, ножницы и т.д.)
    private lateinit var choiceButtons: List<Button>

    private var playerChoice: Int = -1  // Текущий выбор игрока (-1 означает отсутствие выбора)
    private var isRoundActive = false   // Флаг, указывающий на активность текущего раунда

    // Список возможных вариантов выбора
    private val choices = listOf("Камень", "Ножницы", "Бумага", "Ящерица", "Спок")

    // Список ресурсов изображений для каждого варианта выбора
    private val choiceImages = listOf(
        R.drawable.rock,     // Камень
        R.drawable.scissors, // Ножницы
        R.drawable.paper,    // Бумага
        R.drawable.lizard,   // Ящерица
        R.drawable.spock     // Спок
    )

    // Правила победы: ключ - индекс выбора, значение - список индексов, которые он побеждает
    private val winRules = mapOf(
        0 to listOf(1, 3), // Камень побеждает Ножницы (1) и Ящерицу (3)
        1 to listOf(2, 3), // Ножницы побеждают Бумагу (2) и Ящерицу (3)
        2 to listOf(0, 4), // Бумага побеждает Камень (0) и Спока (4)
        3 to listOf(2, 4), // Ящерица побеждает Бумагу (2) и Спока (4)
        4 to listOf(0, 1)  // Спок побеждает Камень (0) и Ножницы (1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Установка макета активности

        // Инициализация Handler для работы с UI потоком
        handler = Handler(Looper.getMainLooper())

        // Привязка элементов интерфейса к переменным
        resultTextView = findViewById(R.id.resultTextView)
        computerChoiceImageView = findViewById(R.id.computerChoiceImageView)
        playerChoiceImageView = findViewById(R.id.playerChoiceImageView)
        playButton = findViewById(R.id.playButton)

        // Инициализация списка кнопок выбора
        choiceButtons = listOf(
            findViewById(R.id.rockButton),    // Кнопка "Камень"
            findViewById(R.id.scissorsButton), // Кнопка "Ножницы"
            findViewById(R.id.paperButton),    // Кнопка "Бумага"
            findViewById(R.id.lizardButton),   // Кнопка "Ящерица"
            findViewById(R.id.spockButton)     // Кнопка "Спок"
        )

        resetChoices()  // Сброс состояния игры при запуске
    }

    // Обработчик выбора игрока
    fun onChoiceSelected(view: View) {
        if (isRoundActive) return // Блокируем выбор во время активного раунда

        resetButtonColors()  // Сбрасываем цвета всех кнопок
        view.setBackgroundColor(Color.GREEN)  // Подсвечиваем выбранную кнопку зеленым

        // Определяем выбор игрока по ID нажатой кнопки
        when (view.id) {
            R.id.rockButton -> playerChoice = 0
            R.id.scissorsButton -> playerChoice = 1
            R.id.paperButton -> playerChoice = 2
            R.id.lizardButton -> playerChoice = 3
            R.id.spockButton -> playerChoice = 4
        }

        // Устанавливаем соответствующее изображение для выбора игрока
        playerChoiceImageView.setImageResource(choiceImages[playerChoice])
    }

    // Подсветка выбора компьютера на соответствующих кнопках
    private fun highlightComputerChoice(choice: Int) {
        val buttonId = when (choice) {
            0 -> R.id.rockButton
            1 -> R.id.scissorsButton
            2 -> R.id.paperButton
            3 -> R.id.lizardButton
            4 -> R.id.spockButton
            else -> return
        }
        // Подсвечиваем кнопку красным цветом
        findViewById<Button>(buttonId).setBackgroundColor(Color.RED)
    }

    // Сброс цветов всех кнопок к стандартному виду
    private fun resetButtonColors() {
        choiceButtons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_default))
        }
    }

    // Обработчик нажатия кнопки "Играть"
    fun onPlayButtonClick(view: View) {
        if (isRoundActive) return // Блокируем кнопку во время активного раунда
        if (playerChoice == -1) {
            // Если игрок не сделал выбор, показываем сообщение
            Toast.makeText(this, "Пожалуйста, сделайте выбор!", Toast.LENGTH_SHORT).show()
            return
        }

        isRoundActive = true // Устанавливаем флаг активного раунда
        disableAllButtons()   // Блокируем все кнопки

        // Компьютер делает случайный выбор
        val computerChoice = Random.nextInt(choices.size)
        // Отображаем выбор компьютера
        computerChoiceImageView.setImageResource(choiceImages[computerChoice])
        // Подсвечиваем выбор компьютера
        highlightComputerChoice(computerChoice)

        // Определяем победителя
        determineWinner(playerChoice, computerChoice)

        // Запланировать сброс через 3 секунды
        handler.postDelayed({
            resetChoices()    // Сбрасываем выборы
            isRoundActive = false // Снимаем флаг активного раунда
            enableAllButtons() // Разблокируем все кнопки
        }, 3000)
    }

    // Определение победителя
    private fun determineWinner(playerChoice: Int, computerChoice: Int) {
        if (playerChoice == computerChoice) {
            // Ничья - одинаковые выборы
            resultTextView.text = "Ничья! Играем снова."
        } else if (winRules[playerChoice]?.contains(computerChoice) == true) {
            // Игрок победил
            resultTextView.text = "Вы победили! ${choices[playerChoice]} побеждает ${choices[computerChoice]}"
        } else {
            // Компьютер победил
            resultTextView.text = "Компьютер победил! ${choices[computerChoice]} побеждает ${choices[playerChoice]}"
        }
    }

    // Сброс всех выборов и интерфейса
    private fun resetChoices() {
        playerChoice = -1  // Сбрасываем выбор игрока
        // Устанавливаем изображения "вопросительных знаков"
        playerChoiceImageView.setImageResource(R.drawable.question)
        computerChoiceImageView.setImageResource(R.drawable.question)
        // Устанавливаем стандартное сообщение
        resultTextView.text = "Сделайте Ваш выбор"
        // Сбрасываем цвета кнопок
        resetButtonColors()
    }

    // Метод для блокировки всех кнопок
    private fun disableAllButtons() {
        choiceButtons.forEach { it.isEnabled = false } // Блокируем кнопки выбора
        playButton.isEnabled = false                  // Блокируем кнопку "Играть"
    }

    // Метод для разблокировки всех кнопок
    private fun enableAllButtons() {
        choiceButtons.forEach { it.isEnabled = true } // Разблокируем кнопки выбора
        playButton.isEnabled = true                  // Разблокируем кнопку "Играть"
    }

    // Очистка Handler при уничтожении активности
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Удаляем все отложенные задачи
    }
}