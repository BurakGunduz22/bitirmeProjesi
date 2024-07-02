package com.android.burakgunduz.bitirmeprojesi.ui.screens.userProfileScreen.subScreens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.FakeTopBar
import com.android.burakgunduz.bitirmeprojesi.ui.screens.components.SnackBarFile
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.archivoFonts
import com.android.burakgunduz.bitirmeprojesi.ui.theme.fonts.robotoFonts
import com.android.burakgunduz.bitirmeprojesi.viewModels.Report
import com.android.burakgunduz.bitirmeprojesi.viewModels.ReportViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

@Composable
fun ReportScreen(viewModel: ReportViewModel, userID: String, navController: NavController) {
    var showReportDialog by remember { mutableStateOf(false) }
    val showReportDetail = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.fetchReports(userID)
    }
    val reports by viewModel.reports.collectAsState()
    val reportList = remember {
        MutableList(3) { "" }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    Log.e("Reports", reports.toString())
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FakeTopBar(
                    navController = navController,
                    screenName = "Reports",
                    isItReport = true,
                    onClick = { showReportDialog = true })
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Send Report")
            }
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(reports) { report ->
                    ReportItem(report, reportList, showReportDetail)
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }

    if (showReportDialog) {
        ReportDialog(
            userID = userID,
            snackbarHostState = snackbarHostState,
            onDismiss = { showReportDialog = false },
            onSend = { newReport ->
                viewModel.addReport(newReport)
                showReportDialog = false
            }
        )
    }
    if (showReportDetail.value) {
        ReportDetailsDialog(report = reportList, onDismiss = { showReportDetail.value = false })
    }

}

@Composable
fun ReportItem(
    report: Report,
    reportList: MutableList<String>,
    showReportDetail: MutableState<Boolean>
) {
    val subjects = listOf(
        "Category Request",
        "Reporting a User",
        "Reporting a bug",
        "Technical Support",
        "General Feedback"
    )
    val statuses = listOf("Created", "Pending", "Completed")
    val date = report.date.toDate()
    val formattedDate = SimpleDateFormat("dd/MM/yyyy").format(date)
    val finalDate = formattedDate.format(date)
    Card(modifier = Modifier
        .fillMaxWidth()
        .clip(AbsoluteRoundedCornerShape(12))
        .clickable {
            showReportDetail.value = true
            reportList[0] = report.title
            reportList[1] = subjects.getOrElse(report.subject) { "Unknown" }
            reportList[2] = report.message
        }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                report.title,
                fontFamily = robotoFonts,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "Date: $finalDate",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.Normal,
            )
            Text(
                "Status: ${statuses.getOrElse(report.status) { "Unknown" }}",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.Normal,
            )
            Text(
                "Subject: ${subjects.getOrElse(report.subject) { "Unknown" }}",
                fontFamily = archivoFonts,
                fontWeight = FontWeight.Normal,
            )
        }
    }
}

@Composable
fun ReportDetailsDialog(report: MutableList<String>, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .width(300.dp)
                    .height(300.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    report[0],
                    fontFamily = robotoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subject: ${report[1]}",
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Report: ${report[2]}",
                    fontFamily = archivoFonts,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    minLines = 5,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDialog(
    userID: String,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit,
    onSend: (Report) -> Unit
) {
    val title = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val subject = remember { mutableStateOf(0) }
    var lineCharCount by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    val selected = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Send Report",
                    fontFamily = robotoFonts,
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = title.value,
                    onValueChange = {
                        lineCharCount = it.length
                        if (lineCharCount <= 35) {
                            title.value = it
                        }
                    },
                    label = {
                        Text(
                            "Title",
                            fontFamily = archivoFonts,
                            fontWeight = FontWeight.Normal,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    },
                    maxLines = 1,
                    modifier = Modifier.width(280.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = message.value,
                    onValueChange = {
                        lineCharCount = it.length
                        if (lineCharCount <= 250) {
                            message.value = it
                        }
                    },
                    label = { Text("Message",
                        fontFamily = archivoFonts,
                        fontWeight = FontWeight.Normal,
                        style = MaterialTheme.typography.titleMedium,) },
                    minLines = 5,
                    maxLines = 5,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    modifier = Modifier.width(280.dp)
                ) {
                    TextField(
                        value = selected.value,
                        onValueChange = {
                            selected.value = it
                        },
                        shape = AbsoluteRoundedCornerShape(16),
                        trailingIcon = {
                            val rotation by animateFloatAsState(
                                if (expanded) 180F else 0F,
                                label = ""
                            )
                            Icon(
                                rememberVectorPainter(Icons.Default.ArrowDropDown),
                                contentDescription = "Dropdown Arrow",
                                Modifier.rotate(rotation),
                            )
                        },
                        label = {
                            Text(text = "Subject",
                                fontFamily = archivoFonts,
                                fontWeight = FontWeight.Normal,
                                style = MaterialTheme.typography.titleMedium,)
                        },
                        modifier = Modifier.menuAnchor(),
                        maxLines = 1,
                        readOnly = true,
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        },
                        modifier = Modifier.size(
                            280.dp,
                            250.dp
                        )
                    ) {
                        val subjects = listOf(
                            "Category Request",
                            "Reporting a User",
                            "Reporting a bug",
                            "Technical Support",
                            "General Feedback"
                        )
                        subjects.forEachIndexed { index, subjectText ->
                            DropdownMenuItem(onClick = {
                                expanded = false
                                subject.value = index
                                selected.value = subjectText
                            }, text = {
                                Text(subjectText)
                            }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        if (title.value.isEmpty() || message.value.isEmpty() || selected.value.isEmpty()) {
                            SnackBarFile(
                                coroutineScope = coroutineScope,
                                snackbarHostState = snackbarHostState,
                                message = "All fields are mandatory",
                                duration = "Short"
                            )
                        } else {
                            val newReport = Report(
                                date = Timestamp.now(), // replace with actual current date
                                message = message.value,
                                status = 0,
                                subject = subject.value,
                                title = title.value,
                                userID = userID // replace with actual userID
                            )
                            onSend(newReport)
                        }
                    }) {
                        Text("Send")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}