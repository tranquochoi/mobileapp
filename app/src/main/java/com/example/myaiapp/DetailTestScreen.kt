    // DetailTestScreen.kt
    package com.example.myaiapp

    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.padding
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.Tab
    import androidx.compose.material3.TabRow
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.graphics.Color
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.navigation.NavController

    // DetailTestScreen.kt

    @Composable
    fun DetailTestScreen(navController: NavController, homeTestName: String?) {
        val firestoreRepository = FirestoreRepository()

        var quizDocuments by remember { mutableStateOf<List<QuizItem>?>(null) }

        // Use a Map to store the selected option and showAnswer for each quiz
        var quizStates by remember { mutableStateOf<Map<Int, QuizState>>(emptyMap()) }

        LaunchedEffect(homeTestName) {
            if (!homeTestName.isNullOrEmpty()) {
                quizDocuments = firestoreRepository.getAllQuizDocuments(homeTestName, "quiz_1").shuffled()
                // Initialize quizStates with keys for each quiz and default values
                quizStates = quizDocuments?.indices?.associateWith { QuizState() } ?: emptyMap()
            }
        }

        var selectedTabIndex by remember { mutableStateOf(0) }

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
                        text = { Text("Quiz ${index + 1}",    onTextLayout = {}, // hoặc null nếu không cần
                        ) }
                    )
                }
            }

            // Display content based on selected tab
            if (quizDocuments != null && selectedTabIndex in 0 until quizDocuments!!.size) {
                // Pass the QuizState for the current quiz and the onOptionSelected callback
                QuizDetails(
                    quizItem = quizDocuments!![selectedTabIndex],
                    quizState = quizStates[selectedTabIndex] ?: QuizState(),
                    onOptionSelected = { option ->
                        // Update the QuizState for the current quiz
                        quizStates = quizStates + (selectedTabIndex to QuizState(optionSelected = option))
                    },
                    onShowAnswer = {
                        // Update the QuizState for the current quiz to show the answer
                        quizStates = quizStates + (selectedTabIndex to QuizState(showAnswer = true))
                    }
                )
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
            Text(text = "${quizItem.ques}", modifier = Modifier.padding(bottom = 8.dp),    onTextLayout = {}, // hoặc null nếu không cần
            )
            Text(
                text = quizItem.quiz,
                modifier = Modifier.padding(bottom = 8.dp),
                fontWeight = FontWeight.Bold,
                onTextLayout = {}, // hoặc null nếu không cần

            )

            quizItem.op.forEach { (optionKey, optionValue) ->
                val textColor = when {
                    quizState.showAnswer && optionKey == quizItem.ans -> Color.Green // Right answer
                    quizState.optionSelected == optionKey -> Color.Red // User selected this option
                    else -> Color.Gray // Default color
                }

                Button(
                    onClick = {
                        if (!quizState.showAnswer) {
                            onOptionSelected(optionKey)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    enabled = !quizState.showAnswer, // Disable buttons once the answer is revealed
                    colors = ButtonDefaults.buttonColors(
                        contentColor = textColor // Set text color directly
                    )
                ) {
                    Text(text = optionValue,    onTextLayout = {}, // hoặc null nếu không cần
                    )

                }
            }

            Button(
                onClick = {
                    if (!quizState.showAnswer) {
                        onShowAnswer()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (quizState.optionSelected == quizItem.ans) Color.Green else Color.Gray
                ),
                content = {
                    Text(text = "${quizItem.ans}",    onTextLayout = {}, // hoặc null nếu không cần
                    )
                }
            )

            // Display the selected answer
            if (quizState.showAnswer) {
                Text(
                    text = "Answer: ${quizItem.ans}",
                    color = Color.Green,
                    onTextLayout = {}, // hoặc null nếu không cần

                )
            }
        }
    }

    data class QuizState(
        val optionSelected: String? = null,
        val showAnswer: Boolean = false
    )

