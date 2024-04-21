// FirestoreRepository.kt
package com.example.myaiapp

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID


class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()


    suspend fun getHomeCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("home").get().await()
            val homeCollections = querySnapshot.documents.mapNotNull { it.id }
            homeCollections
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMojiDocuments(homeName: String, collectionName: String): List<DocumentSnapshot> {
        return try {
            val querySnapshot = db.collection("home").document(homeName)
                .collection(collectionName).get().await()

            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }


    suspend fun getHomeKajiCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("home_kanji").get().await()
            val homeCollections = querySnapshot.documents.mapNotNull { it.id }
            homeCollections
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getKanjiDocuments(
        homeKanjiName: String,
        collectionName: String
    ): List<DocumentSnapshot> {
        return try {
            val querySnapshot = db.collection("home_kanji").document(homeKanjiName)
                .collection(collectionName).get().await()

            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getHomeTestCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("test").get().await()
            val homeCollections = querySnapshot.documents.mapNotNull { it.id }
            homeCollections
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTestDocuments(homeTestName: String, collectionName: String): List<String> {
        return try {
            val querySnapshot = db.collection("test").document(homeTestName)
                .collection(collectionName).get().await()

            querySnapshot.documents.mapNotNull { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
    // Trong lớp FirestoreRepository

    suspend fun getAllQuizDocuments(homeTestName: String, collectionName: String): List<QuizItem> {
        return try {
            val querySnapshot = db.collection("test").document(homeTestName)
                .collection(collectionName).get().await()

            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val ans = documentSnapshot.getString("ans") ?: ""
                val op = documentSnapshot.get("op") as? Map<String, String> ?: emptyMap()
                val ques = documentSnapshot.getString("ques") ?: ""
                val quiz = documentSnapshot.getString("quiz") ?: ""

                QuizItem(ans, op, ques, quiz)
            }.distinctBy { it.quiz } // Filter out duplicates based on the quiz content
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun updateNoteInCollection(collectionName: String, note: Note) {
        try {
            // Update the document with the new data
            val updatedData = mapOf(
                "id" to note.id,
                "title" to note.title,
                "content" to note.content,
                "timestamp" to FieldValue.serverTimestamp() // Use server timestamp
            )
            db.collection(collectionName).document(note.id)
                .update(updatedData).await()
        } catch (e: Exception) {
            // Handle exception, e.g., log or notify user
        }
    }



    suspend fun getAddCollection(): List<Note> {
        return try {
            val querySnapshot = db.collection("add").get().await()
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val id = documentSnapshot.id // Lấy ID của tài liệu
                val title = documentSnapshot.getString("title") ?: ""
                val content = documentSnapshot.getString("content") ?: ""
                val timestamp = documentSnapshot.getTimestamp("timestamp")

                Note(id, title, content, timestamp) // Sử dụng ID khi tạo Note
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteNoteFromCollection(collectionName: String, notes: List<Note>) {
        try {
            // Lặp qua từng ghi chú trong danh sách
            notes.forEach { note ->
                // Xóa tài liệu theo ID
                db.collection(collectionName).document(note.id)
                    .delete().await()
            }
        } catch (e: Exception) {
            // Xử lý ngoại lệ, ví dụ: ghi log hoặc thông báo cho người dùng
        }
    }



    suspend fun addNoteToCollection(collectionName: String, note: Note) {
        try {
            val newDocumentId = generateCustomDocumentId(collectionName) // Tạo ID mới từ hàm generateCustomDocumentId
            val noteWithId = note.copy(id = newDocumentId) // Cập nhật note với id mới
            db.collection(collectionName).document(newDocumentId)
                .set(noteWithId).await()
        } catch (e: Exception) {
            // Xử lý ngoại lệ, ví dụ: ghi log hoặc thông báo cho người dùng
        }
    }

    private suspend fun generateCustomDocumentId(collectionName: String): String {
        val querySnapshot = db.collection(collectionName).get().await()
        val count = querySnapshot.size() + 1 // Tăng count lên 1 để tạo mã ID tiếp theo
        val formattedCount = String.format("%04d", count) // Format count để có 4 chữ số, ví dụ: 0001, 0002, ...
        return formattedCount
    }

    suspend fun getGrammarCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("grammar").get().await()
            val grammarCollections = querySnapshot.documents.mapNotNull { it.id }
            grammarCollections
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getGrammarDocuments(grammarName: String): List<DocumentSnapshot> {
        return try {
            val querySnapshot =
                db.collection("grammar").document(grammarName).collection("b1").get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVocabularyCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("vocabulary").get().await()
            querySnapshot.documents.mapNotNull { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVocabularyDocuments(vocabularyName: String, collectionName: String): List<DocumentSnapshot> {
        return try {
            val querySnapshot = db.collection("vocabulary").document(vocabularyName)
                .collection(collectionName).get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getKaiwaCollections(): List<String> {
        return try {
            val querySnapshot = db.collection("kaiwa").get().await()
            querySnapshot.documents.mapNotNull { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getKaiwaDocuments(kaiwaName: String, collectionName: String): List<DocumentSnapshot> {
        return try {
            val querySnapshot = db.collection("kaiwa").document(kaiwaName)
                .collection(collectionName).get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }
}