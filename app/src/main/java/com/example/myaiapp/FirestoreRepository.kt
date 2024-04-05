// FirestoreRepository.kt
package com.example.myaiapp

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date


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

    suspend fun getKanjiDocuments(homeKanjiName: String , collectionName: String): List<DocumentSnapshot> {
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

    suspend fun getTestDocuments(homeTestName: String , collectionName: String): List<String> {
        return try {
            val querySnapshot = db.collection("test").document(homeTestName)
                .collection(collectionName).get().await()

            querySnapshot.documents.mapNotNull { it.id }
        } catch (e: Exception) {
            emptyList()
        }
    }
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

    suspend fun getAddCollection(): List<Note> {
        return try {
            val querySnapshot = db.collection("add").get().await()
            querySnapshot.documents.mapNotNull { documentSnapshot ->
                val title = documentSnapshot.getString("title") ?: ""
                val content = documentSnapshot.getString("content") ?: ""
                val timestamp = documentSnapshot.getTimestamp("timestamp")

                Note(title, content, Date())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addNoteToCollection(collectionName: String, note: Note) {
        try {
            db.collection(collectionName).document()
                .set(note).await()
        } catch (e: Exception) {
            // Handle exception, e.g., log or notify user
        }
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
            val querySnapshot = db.collection("grammar").document(grammarName).collection("b1").get().await()
            querySnapshot.documents
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun getVocabularyDocuments(): List<VocabItem> {
        return try {
            val querySnapshot = db.collection("vocabulary").get().await()
            val items = querySnapshot.documents.mapNotNull { document ->
                val go = document.getString("go") ?: ""
                val kanji = document.getString("kanji") ?: ""
                val kotoba = document.getString("kotoba") ?: ""
                val romaji = document.getString("romaji") ?: ""
                if (go.isNotEmpty() && kanji.isNotEmpty() && kotoba.isNotEmpty() && romaji.isNotEmpty()) {
                    VocabItem(go, kanji, kotoba, romaji)
                } else {
                    null
                }
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }

}