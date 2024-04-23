package com.example.myaiapp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.min

@Composable
fun DetailTestScreen(navController: NavController, homeTestName: String?) {
    val firestoreRepository = FirestoreRepository()

    var quizDocuments by remember { mutableStateOf<List<QuizItem>?>(null) }

    // Use a Map to store the selected option, showAnswer, and result for each quiz
    var quizStates by remember { mutableStateOf<Map<Int, QuizState>>(emptyMap()) }

    var selectedTabIndex by remember { mutableStateOf(0) }
    var answerShownCount by remember { mutableStateOf(0) }
    var completedQuizCount by remember { mutableStateOf(0) }

    LaunchedEffect(homeTestName) {
        if (!homeTestName.isNullOrEmpty()) {
            quizDocuments = firestoreRepository.getAllQuizDocuments(homeTestName, "quiz_1").shuffled()
            // Initialize quizStates with keys for each quiz and default values
            quizStates = quizDocuments?.indices?.associateWith { QuizState() } ?: emptyMap()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // TabRow with dynamically created tabs based on the number of quizzes
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            quizDocuments?.forEachIndexed { index, quizItem ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                    },
                    text = { Text("Quiz ${index + 1}") }
                )
            }
        }

        // Display content based on selected tab
        if (quizDocuments != null && selectedTabIndex in 0 until quizDocuments!!.size) {
            val currentQuizIndex = selectedTabIndex
            val currentQuizItem = quizDocuments!![currentQuizIndex]

            // Check if we need to show quiz details or result
            if (answerShownCount >= 3) {
                // Show result
                Column {
                    // Show result
                    QuizResult(
                        correctCount = quizStates.values.sumBy { if (it.isCorrect) 1 else 0 },
                        incorrectCount = quizStates.values.sumBy { if (!it.isCorrect) 1 else 0 },
                        onReviewClick = {
                            navController.navigate("review") // Chuyển đến màn hình xem lại kết quả khi nút "Review Results" được nhấn
                        }
                    )

                    Button(onClick = {
                        navController.navigate("review") // Navigate to review screen
                    }) {
                        Text("Review Results")
                    }
                }
            } else {
                // Show quiz details
                QuizDetails(
                    quizItem = currentQuizItem,
                    quizState = quizStates[currentQuizIndex] ?: QuizState(),
                    onOptionSelected = { option ->
                        val isCorrect = option == currentQuizItem.ans
                        val updatedState = quizStates[currentQuizIndex]?.copy(
                            optionSelected = option,
                            isCorrect = isCorrect
                        ) ?: QuizState(optionSelected = option, isCorrect = isCorrect)
                        quizStates = quizStates + (currentQuizIndex to updatedState)

                        // Move to the next question if not showing the answer
                        if (!updatedState.showAnswer) {
                            val nextIndex = currentQuizIndex + 1
                            if (nextIndex < quizDocuments!!.size) {
                                selectedTabIndex = nextIndex
                            } else {
                                completedQuizCount++
                                answerShownCount++
                            }
                        }
                    },
                    onShowAnswer = {
                        // Update the QuizState for the current quiz to show the answer
                        quizStates = quizStates + (currentQuizIndex to QuizState(showAnswer = true))
                    }
                )
            }
        }
    }
}

@Composable
fun QuizDetails(
    quizItem: QuizItem,
    quizState: QuizState,
    onOptionSelected: (String?) -> Unit,
    onShowAnswer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Display the question title
        Text(text = "${quizItem.question}", modifier = Modifier.padding(bottom = 8.dp))
        Text(
            text = quizItem.quiz,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )

        // Display the options
        val options = listOf(quizItem.op1, quizItem.op2, quizItem.op3, quizItem.op4)

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(options.size / 2) { rowIndex ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val startIndex = rowIndex * 2
                    val endIndex = min(startIndex + 2, options.size)
                    for (i in startIndex until endIndex) {
                        val option = options[i]
                        val textColor = when {
                            quizState.showAnswer && option == quizItem.ans -> Color.Green // Right answer
                            quizState.optionSelected == option -> Color.Red // User selected this option
                            else -> Color.Gray // Default color
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .clickable {
                                    if (!quizState.showAnswer) {
                                        onOptionSelected(option)
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    text = option,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(48.dp)
                .clickable {
                    if (!quizState.showAnswer) {
                        onShowAnswer()
                    }
                }
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center,
            content = {
                Text(
                    text = "Show Answer",
                    color = if (quizState.optionSelected == quizItem.ans) Color.Green else Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        )

        // Display the selected answer
        if (quizState.showAnswer) {
            Text(
                text = "Answer: ${quizItem.ans}",
                color = Color.Green
            )
        }
    }
}

@Composable
fun QuizResult(
    correctCount: Int,
    incorrectCount: Int,
    onReviewClick: () -> Unit // Thêm một tham số là một lambda để xử lý sự kiện khi người dùng nhấn nút "Review Results"
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Result", fontWeight = FontWeight.Bold)
        Text("Correct Answers: $correctCount")
        Text("Incorrect Answers: $incorrectCount")
        Button(
            onClick = onReviewClick // Gọi hàm xử lý khi nút "Review Results" được nhấn
        ) {
            Text("Review Results")
        }
    }
}


@Composable
fun ReviewScreen(quizDocuments: List<QuizItem>, quizStates: Map<Int, QuizState>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        quizDocuments.forEachIndexed { index, quizItem ->
            val currentQuizState = quizStates[index] ?: QuizState()
            val textColor = if (currentQuizState.isCorrect) Color.Green else Color.Red

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Quiz ${index + 1}: ${quizItem.question}",
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(text = quizItem.quiz)
                Text(
                    text = "Selected Answer: ${currentQuizState.optionSelected ?: "Not answered"}",
                    color = textColor
                )
                Text(
                    text = "Correct Answer: ${quizItem.ans}",
                    color = textColor
                )
            }
        }
    }
}


data class QuizState(
    val optionSelected: String? = null,
    val showAnswer: Boolean = false,
    val isCorrect: Boolean = false,
    val incorrectCount: Int = 0
)