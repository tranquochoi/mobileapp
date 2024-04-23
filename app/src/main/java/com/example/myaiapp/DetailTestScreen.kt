package com.example.myaiapp

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun DetailTestScreen(navController: NavController, homeTestName: String?) {
    val firestoreRepository = FirestoreRepository()

    var quizDocuments by remember { mutableStateOf<List<QuizItem>?>(null) }
    var quizStates by remember { mutableStateOf<Map<Int, QuizState>>(emptyMap()) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var completedQuizCount by remember { mutableStateOf(0) }
    var isDelayPassed by remember { mutableStateOf(false) }
    var isAnswerSubmitted by remember { mutableStateOf(false) }

    // Hàm reset để thiết lập lại trạng thái của bài kiểm tra
    val resetQuiz: () -> Unit = {
        // Thiết lập lại trạng thái của quizStates
        quizStates = quizStates.mapValues { (_, state) -> state.copy(optionSelected = null, showAnswer = false, isCorrect = false) }
        // Chuyển về câu hỏi đầu tiên
        selectedTabIndex = 0
        // Đặt lại cờ để đảm bảo không có chậm trễ nữa
        isDelayPassed = false
    }

    LaunchedEffect(homeTestName) {
        if (!homeTestName.isNullOrEmpty()) {
            quizDocuments = firestoreRepository.getAllQuizDocuments(homeTestName, "quiz_1").shuffled()
            quizStates = quizDocuments?.indices?.associateWith { QuizState() } ?: emptyMap()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz",
                    color = Color.White,
                ) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = Color.White // Set icon color to white
                        )
                    }
                },
                backgroundColor = Color.Black, // Set background color to black
                modifier = Modifier.fillMaxWidth()
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)

            ) {
                HorizontalScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    quizDocuments = quizDocuments ?: emptyList(),
                    onTabSelected = { selectedTabIndex = it }
                )

                // Calculate the number of completed quizzes
                completedQuizCount = quizStates.values.count { it.optionSelected != null }

                if (quizDocuments != null && selectedTabIndex in 0 until quizDocuments!!.size) {
                    val currentQuizIndex = selectedTabIndex
                    val currentQuizItem = quizDocuments!![currentQuizIndex]

                    // Check if all quizzes in document "quiz_1" are completed
                    if (completedQuizCount >= quizDocuments!!.size) {
                        // Show result if all quizzes are completed
                        QuizResult(
                            correctCount = quizStates.values.sumBy { if (it.isCorrect) 1 else 0 },
                            incorrectCount = quizStates.values.sumBy { if (!it.isCorrect) 1 else 0 },
                            onResetClick = resetQuiz // Truyền hàm reset để xử lý sự kiện khi người dùng nhấn nút reset
                        )
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

                                // Perform the delay only if the delay hasn't already passed
                                if (!isDelayPassed) {
                                    isDelayPassed = true
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        // Move to the next question
                                        val nextIndex = currentQuizIndex + 1
                                        if (nextIndex < quizDocuments!!.size) {
                                            selectedTabIndex = nextIndex
                                            isDelayPassed = false // Reset the flag for the next question
                                        }
                                    }, 1000)
                                }
                            }
                        )

                    }
                }
            }
        }
    )
}
@Composable
fun HorizontalScrollableTabRow(
    selectedTabIndex: Int,
    quizDocuments: List<QuizItem>,
    onTabSelected: (Int) -> Unit
) {
    val scrollState = rememberScrollState()

    Column {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .fillMaxWidth()
        ) {
            quizDocuments.forEachIndexed { index, quizItem ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = { Text("Quiz ${index + 1}") }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp)) // Khoảng trống để đẩy nội dung xuống dưới
    }
}


@Composable
fun QuizDetails(
    quizItem: QuizItem,
    quizState: QuizState,
    onOptionSelected: (String?) -> Unit
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
                        val textColor = if (quizState.optionSelected != null) {
                            if (option == quizState.optionSelected) {
                                if (option == quizItem.ans) {
                                    Color.Green // Correct answer selected
                                } else {
                                    Color.Red // Incorrect answer selected
                                }
                            } else {
                                if (option == quizItem.ans) {
                                    Color.Green // Correct answer unselected
                                } else {
                                    Color.Gray // Default color for unselected options
                                }
                            }
                        } else {
                            Color.Gray // Default color for unselected options
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
    }
}



@Composable
fun QuizResult(
    correctCount: Int,
    incorrectCount: Int,
    onResetClick: () -> Unit // Thêm một tham số mới để xử lý sự kiện khi người dùng nhấn nút "Reset"
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kết quả", fontWeight = FontWeight.Bold)
        Text("Số câu đúng: $correctCount")
        Text("Số câu sai: $incorrectCount")
        IconButton(
            onClick = onResetClick // Gọi hàm xử lý khi nút "Reset" được nhấn
        ) {
            Icon(Icons.Filled.Refresh, contentDescription = "Reset")
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