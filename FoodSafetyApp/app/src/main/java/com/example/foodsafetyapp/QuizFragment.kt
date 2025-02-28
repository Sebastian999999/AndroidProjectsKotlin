package com.example.foodsafetyapp

import com.example.foodsafetyapp.models.QuizQuestion
import com.example.foodsafetyapp.models.QuizResponse


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodsafetyapp.databinding.FragmentQuizBinding
import com.example.foodsafetyapp.databinding.ItemQuizQuestionBinding
import com.google.gson.Gson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.Random

class QuizFragment : Fragment() {
    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    private var quizQuestions: List<QuizQuestion> = emptyList()
    private var selectedQuestions: List<QuizQuestion> = emptyList()
    private val userAnswers = mutableMapOf<Int, Int>()  // questionIndex -> selectedOptionIndex
    private val questionViews = mutableListOf<View>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadQuizQuestions()
        setupQuizSubmitButton()
    }

    private fun loadQuizQuestions() {
        try {
            val jsonString = context?.assets
                ?.open("food_safety_quiz.json")
                ?.bufferedReader()
                ?.use { it.readText() }

            val quizResponse = Gson().fromJson(jsonString, QuizResponse::class.java)
            quizQuestions = quizResponse.questions

            // Select 5 random questions
            selectedQuestions = quizQuestions.shuffled().take(5)

            generateQuizUI()
        } catch (e: Exception) {
            Log.e("QuizFragment", "Error loading quiz questions", e)
        }
    }

    private fun generateQuizUI() {
        binding.quizContainer.removeAllViews()
        questionViews.clear()
        userAnswers.clear()

        selectedQuestions.forEachIndexed { index, question ->
            val questionBinding = ItemQuizQuestionBinding.inflate(
                layoutInflater,
                binding.quizContainer,
                true
            )

            questionBinding.tvQuestionNumber.text = "Question ${index + 1}"
            questionBinding.tvQuestion.text = question.question

            // Set up radio buttons
            questionBinding.rbOption1.text = question.options[0]
            questionBinding.rbOption2.text = question.options[1]
            questionBinding.rbOption3.text = question.options[2]
            questionBinding.rbOption4.text = question.options[3]

            // Add listener to radio group
            questionBinding.rgOptions.setOnCheckedChangeListener { _, checkedId ->
                val selectedOptionIndex = when (checkedId) {
                    R.id.rbOption1 -> 0
                    R.id.rbOption2 -> 1
                    R.id.rbOption3 -> 2
                    R.id.rbOption4 -> 3
                    else -> -1
                }

                if (selectedOptionIndex != -1) {
                    userAnswers[index] = selectedOptionIndex
                    updateSubmitButtonState()
                }
            }

            questionViews.add(questionBinding.root)
        }
    }

    private fun updateSubmitButtonState() {
        // Enable submit button only if all questions are answered
        binding.btnSubmitQuiz.isEnabled = userAnswers.size == selectedQuestions.size
    }

    private fun setupQuizSubmitButton() {
        binding.btnSubmitQuiz.setOnClickListener {
            evaluateQuiz()
        }

        binding.btnTryAnotherQuiz.setOnClickListener {
            binding.resultsContainer.visibility = View.GONE
            loadQuizQuestions()  // This will select new random questions and reset the UI
            binding.btnSubmitQuiz.isEnabled = false
        }
    }

    private fun evaluateQuiz() {
        var correctAnswers = 0

        userAnswers.forEach { (questionIndex, selectedOption) ->
            val question = selectedQuestions[questionIndex]
            if (selectedOption == question.correctAnswer) {
                correctAnswers++
            }
        }

        // Show results
        binding.tvScore.text = "$correctAnswers/${selectedQuestions.size}"

        // Set feedback message based on score
        val feedbackMessage = when {
            correctAnswers == selectedQuestions.size -> "Perfect! You're a food safety expert!"
            correctAnswers >= selectedQuestions.size * 0.8 -> "Great job! You're food safety smart!"
            correctAnswers >= selectedQuestions.size * 0.6 -> "Good effort! Keep learning about food safety."
            else -> "Keep studying food safety guidelines to protect yourself and others."
        }

        binding.tvFeedback.text = feedbackMessage
        binding.resultsContainer.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}