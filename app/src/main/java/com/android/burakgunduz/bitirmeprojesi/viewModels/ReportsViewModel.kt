package com.android.burakgunduz.bitirmeprojesi.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Report(
    val date: Timestamp = Timestamp.now(),
    val message: String = "",
    val status: Int = 0,
    val subject: Int = 0,
    val title: String = "",
    val userID: String = ""
)

class ReportViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _reports = MutableStateFlow<List<Report>>(emptyList())
    val reports: StateFlow<List<Report>> = _reports

     fun fetchReports(userID: String) {
        firestore.collection("reportsRequests")
            .whereEqualTo("userID", userID)
            .get()
            .addOnSuccessListener { result ->
                val reportList = result.map { document ->
                    document.toObject(Report::class.java)
                }
                _reports.value = reportList
            }
            .addOnFailureListener { exception ->
                // Handle error
            }
    }

    fun addReport(report: Report) {
        viewModelScope.launch {
            firestore.collection("reportsRequests")
                .add(report)
                .addOnSuccessListener {
                    fetchReports(report.userID) // Refresh the list after adding a new report
                }
                .addOnFailureListener { exception ->
                    // Handle error
                }
        }
    }
}
