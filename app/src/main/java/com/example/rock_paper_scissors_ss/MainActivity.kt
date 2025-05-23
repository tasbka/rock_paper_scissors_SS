package com.example.rock_paper_scissors_ss

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    private var playerChoice: Int = -1  // Текущий выбор игрока (-1 означает отсутствие выбора)

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
        2 to listOf(0, 4),     // Бумага побеждает Камень и Спока
        3 to listOf(2, 4),     // Ящерица побеждает Бумагу и Спока
        4 to listOf(0, 1)      // Спок побеждает Камень и Ножницы
    )

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)  // Установка макета активности

        // Инициализация элементов интерфейса
        resultTextView = findViewById(R.id.resultTextView)
        computerChoiceImageView = findViewById(R.id.computerChoiceImageView)
        playerChoiceImageView = findViewById(R.id.playerChoiceImageView)
        playButton = findViewById(R.id.playButton)

        resetChoices()  // Сброс всех выборов при запуске приложения
    }

    // Обработчик выбора игрока
    fun onChoiceSelected(view: View) {
        resetButtonColors()  // Сбрасываем предыдущие выделения

        // Выделяем выбранную кнопку зеленым цветом
        view.setBackgroundColor(Color.GREEN)

        // Определяем, какой вариант выбрал игрок
        when (view.id) {
            R.id.rockButton -> playerChoice = 0
            R.id.scissorsButton -> playerChoice = 1
            R.id.paperButton -> playerChoice = 2
            R.id.lizardButton -> playerChoice = 3
            R.id.spockButton -> playerChoice = 4
        }

        // Отображаем выбранный вариант игрока
        playerChoiceImageView.setImageResource(choiceImages[playerChoice])
    }

    // Подсветка выбора компьютера
    private fun highlightComputerChoice(choice: Int) {
        // Определяем ID кнопки, соответствующей выбору компьютера
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
        // Получаем список всех кнопок выбора
        val buttons = listOf(
            findViewById<Button>(R.id.rockButton),
            findViewById<Button>(R.id.scissorsButton),
            findViewById<Button>(R.id.paperButton),
            findViewById<Button>(R.id.lizardButton),
            findViewById<Button>(R.id.spockButton)
        )

        // Для каждой кнопки устанавливаем стандартный цвет фона
        buttons.forEach { button ->
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.button_default))
        }
    }

    // Обработчик нажатия кнопки "Играть"
    fun onPlayButtonClick(view: View) {
        // Проверяем, сделал ли игрок выбор
        if (playerChoice == -1) {
            Toast.makeText(this, "Пожалуйста, сделайте выбор!", Toast.LENGTH_SHORT).show()
            return
        }

        // Компьютер делает случайный выбор
        val computerChoice = Random.nextInt(choices.size)
        // Отображаем выбор компьютера
        computerChoiceImageView.setImageResource(choiceImages[computerChoice])

        highlightComputerChoice(computerChoice)  // Подсвечиваем выбор компьютера

        determineWinner(playerChoice, computerChoice)  // Определяем победителя
    }

    // Определение победителя
    private fun determineWinner(playerChoice: Int, computerChoice: Int) {
        // Проверка на ничью
        if (playerChoice == computerChoice) {
            Toast.makeText(this, "Ничья! Играем снова.", Toast.LENGTH_SHORT).show()
            resetChoices()  // Сбрасываем выборы для переигровки
            return
        }

        // Проверяем, побеждает ли выбор игрока выбор компьютера
        if (winRules[playerChoice]?.contains(computerChoice) == true) {
            resultTextView.text = "Вы победили! ${choices[playerChoice]} побеждает ${choices[computerChoice]}"
        } else {
            resultTextView.text = "Компьютер победил! ${choices[computerChoice]} побеждает ${choices[playerChoice]}"
        }
    }

    // Полный сброс всех выборов и интерфейса
    private fun resetChoices() {
        playerChoice = -1  // Сбрасываем выбор игрока
        playerChoiceImageView.setImageResource(R.drawable.question)  // Знак вопроса для игрока
        computerChoiceImageView.setImageResource(R.drawable.question)  // Знак вопроса для компьютера
        resultTextView.text = "Сделайте Ваш выбор"  // Стандартное сообщение
        resetButtonColors()  // Сбрасываем цвета кнопок
    }
}