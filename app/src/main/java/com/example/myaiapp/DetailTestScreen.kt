package com.example.myaiapp

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timelapse
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
    // Biến để theo dõi số tim còn lại
    var remainingLives by remember { mutableStateOf(3) }

    // Hàm reset để thiết lập lại trạng thái của bài kiểm tra
    val resetQuiz: () -> Unit = {
        // Thiết lập lại trạng thái của quizStates
        quizStates = quizStates.mapValues { (_, state) -> state.copy(optionSelected = null, showAnswer = false, isCorrect = false) }
        // Đặt lại số tim còn lại
        remainingLives = 3
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
                title = { Text("Quiz", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    // Hiển thị icon trái tim và số lần trả lời sai
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(remainingLives) {
                            Icon(Icons.Default.Favorite, contentDescription = "Life", tint = Color.Red)
                        }
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
                // Calculate the number of completed quizzes
                completedQuizCount = quizStates.values.count { it.optionSelected != null }

                if (quizDocuments != null && selectedTabIndex in 0 until quizDocuments!!.size) {
                    val currentQuizIndex = selectedTabIndex
                    val currentQuizItem = quizDocuments!![currentQuizIndex]

                    // Check if all quizzes in document "quiz_1" are completed
                    if (completedQuizCount >= quizDocuments!!.size || remainingLives <= 0) {
                        // Show result if all quizzes are completed or no remaining lives
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

                                // Giảm số tim nếu đáp án không chính xác
                                if (!isCorrect) {
                                    remainingLives--
                                }

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
        Spacer(modifier = Modifier.height(18.dp)) // Khoảng trống để đẩy nội dung xuống dưới
    }
}

@Composable
fun QuizDetails(
    quizItem: QuizItem,
    quizState: QuizState,
    onOptionSelected: (String?) -> Unit
) {
    var score by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(5) } // Thời gian còn lại tính bằng giây
    var isTimerRunning by remember { mutableStateOf(false) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }
    var timerFinished by remember { mutableStateOf(false) } // Biến để kiểm tra xem bộ đếm thời gian đã hoàn thành chưa

    // Hàm để bắt đầu bộ đếm thời gian
    fun startTimer() {
        timer = object : CountDownTimer((timeLeft * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft--
            }

            override fun onFinish() {
                timerFinished = true // Đặt cờ báo hiệu rằng bộ đếm thời gian đã hoàn thành
                if (quizState.optionSelected == quizItem.ans) {
                    score += 10 // Cộng 10 điểm nếu chọn đúng
                }
                onOptionSelected(quizState.optionSelected)
            }
        }.start()

        isTimerRunning = true
    }

    // Bắt đầu bộ đếm thời gian khi component được tạo
    LaunchedEffect(Unit) {
        startTimer()
    }

    // Reset thời gian còn lại khi chuyển qua câu hỏi mới
    LaunchedEffect(quizItem) {
        timeLeft = 5
        isTimerRunning = false
        timer?.cancel() // Hủy bộ đếm thời gian trước đó nếu có
        timerFinished = false // Đặt lại cờ báo hiệu
        if (!timerFinished) { // Chỉ bắt đầu bộ đếm thời gian mới nếu bộ đếm thời gian trước đó đã hoàn thành
            startTimer()
        }
    }

    // Hiển thị thời gian còn lại cho câu hỏi
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Timelapse,
            contentDescription = "Time Left Icon",
            tint = if (timeLeft < 3) Color.Red else Color(android.graphics.Color.parseColor("#FFE55B")),
            modifier = Modifier
                .size(48.dp)
                .padding(end = 4.dp)
        )
        Text(
            text = " $timeLeft",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = if (timeLeft < 3) Color.Red else Color(android.graphics.Color.parseColor("#FFE55B"))
        )
    }
    Spacer(modifier = Modifier.height(18.dp)) // Khoảng trống để đẩy nội dung xuống dưới
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "${quizItem.question}",
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = quizItem.quiz,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 8.dp),
            fontWeight = FontWeight.Bold
        )
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
                                    Color.White
                                } else {
                                    Color.White
                                }
                            } else {
                                Color.Black
                            }
                        } else {
                            Color.Black
                        }

                        val backgroundColor = if (quizState.optionSelected != null) {
                            if (option == quizState.optionSelected) {
                                if (option == quizItem.ans) {
                                    Color(0xFFC1FF72)
                                } else {
                                    Color(0xFFFF5757)
                                }
                            } else {
                                if (option == quizItem.ans) {
                                    Color(0xFFC1FF72)
                                } else {
                                    Color.White
                                }
                            }
                        } else {
                            Color.White
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    if (!quizState.showAnswer) {
                                        onOptionSelected(option)
                                    }
                                }
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(backgroundColor),
                            contentAlignment = Alignment.Center,
                            content = {
                                Text(
                                    text = option,
                                    color = textColor,
                                    textAlign = TextAlign.Center,
                                    fontSize = 18.sp,
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
    // Tính số điểm cho từng câu
    val totalScore = correctCount * 10

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hình ảnh nền
        Image(
            painter = painterResource(id = R.drawable.img), // Thay "background_image" bằng đường dẫn tới tệp ảnh của bạn
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Đảm bảo hình ảnh nền đầy đủ kích thước và không bị giãn ra
        )

        // Nội dung của QuizResult
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Tổng số điểm: $totalScore",
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic // Làm nghiêng chữ
            ) // Kích thước chữ lớn hơn
            IconButton(
                onClick = onResetClick // Gọi hàm xử lý khi nút "Reset" được nhấn
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reset")
            }
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp // Kích thước chữ lớn hơn
                )
                Text(text = quizItem.quiz, fontSize = 18.sp) // Kích thước chữ lớn hơn
                Text(
                    text = "Selected Answer: ${currentQuizState.optionSelected ?: "Not answered"}",
                    color = textColor,
                    fontSize = 18.sp // Kích thước chữ lớn hơn
                )
                Text(
                    text = "Correct Answer: ${quizItem.ans}",
                    color = textColor,
                    fontSize = 18.sp // Kích thước chữ lớn hơn
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