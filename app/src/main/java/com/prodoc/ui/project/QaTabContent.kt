package com.prodoc.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.DiagramEntity
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.data.local.entity.MaterialEntity
import com.prodoc.model.QAStatus

data class QaGenericItem(
    val id: String,
    val name: String,
    val type: String,
    val status: QAStatus,
    val reason: String?,
    val rawObject: Any
)

@Composable
fun QaTabContent(
    materials: List<MaterialEntity>,
    logics: List<LogicEntity>,
    diagrams: List<DiagramEntity>,
    onStatusChange: (Any, QAStatus, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val qaItems = remember(materials, logics, diagrams) {
        val list = mutableListOf<QaGenericItem>()
        materials.forEach { list.add(QaGenericItem(it.materialId, it.name, "MATERIAL", it.qaStatus, it.rejectionReason, it)) }
        logics.forEach { list.add(QaGenericItem(it.logicId, it.name, "LOGIC", it.qaStatus, it.rejectionReason, it)) }
        diagrams.forEach { list.add(QaGenericItem(it.diagramId, it.name, "DIAGRAM", it.qaStatus, it.rejectionReason, it)) }
        list.sortedBy { it.name }
    }

    var selectedItemForDialog by remember { mutableStateOf<QaGenericItem?>(null) }

    if (qaItems.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum tersedia untuk divalidasi QA.", color = MaterialTheme.colorScheme.outline)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = modifier.fillMaxSize()
        ) {
            items(qaItems, key = { "${it.type}_${it.id}" }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = when (item.status) {
                            QAStatus.APPROVED -> Color(0xFFE8F5E9)
                            QAStatus.REJECTED -> Color(0xFFFFEBEE)
                            QAStatus.PENDING -> Color(0xFFFFF8E1)
                            QAStatus.DRAFT -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = item.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text(text = "Tipe: ${item.type}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                when (item.status) {
                                    QAStatus.APPROVED -> {
                                        Icon(imageVector = Icons.Default.Lock, contentDescription = "Terkunci", tint = Color(0xFF2E7D32))
                                        Text("APPROVED", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    }
                                    QAStatus.REJECTED -> {
                                        Icon(imageVector = Icons.Default.Warning, contentDescription = "Ditolak", tint = Color(0xFFC62828))
                                        Text("REJECTED", color = Color(0xFFC62828), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    }
                                    QAStatus.PENDING -> {
                                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Pending", tint = Color(0xFFF57F17))
                                        Text("PENDING", color = Color(0xFFF57F17), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    }
                                    QAStatus.DRAFT -> {
                                        Text("DRAFT", color = MaterialTheme.colorScheme.outline, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        if (item.status == QAStatus.REJECTED && !item.reason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Alasan Penolakan: ${item.reason}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFC62828),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (item.status != QAStatus.APPROVED) {
                                Button(
                                    onClick = { selectedItemForDialog = item },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Ubah Status QA", style = MaterialTheme.typography.bodySmall)
                                }
                            } else {
                                AssistChip(
                                    onClick = {},
                                    label = { Text("Item Terkunci & Valid", color = Color(0xFF2E7D32)) },
                                    leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedItemForDialog != null) {
        val currentItem = selectedItemForDialog!!
        var reasonText by remember { mutableStateOf("") }
        var showReasonInput by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedItemForDialog = null },
            title = { Text("Simulasi Validasi QA") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Verifikasi kualitas item '${currentItem.name}':")

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ElevatedButton(
                            onClick = {
                                onStatusChange(currentItem.rawObject, QAStatus.APPROVED, null)
                                selectedItemForDialog = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                        ) { Text("Approve", color = Color.White) }

                        ElevatedButton(
                            onClick = { showReasonInput = true },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                        ) { Text("Reject", color = Color.White) }
                    }

                    if (showReasonInput) {
                        OutlinedTextField(
                            value = reasonText,
                            onValueChange = { reasonText = it },
                            label = { Text("Alasan (Wajib)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                if (showReasonInput) {
                    TextButton(
                        onClick = {
                            if (reasonText.isNotBlank()) {
                                onStatusChange(currentItem.rawObject, QAStatus.REJECTED, reasonText)
                                selectedItemForDialog = null
                            }
                        }
                    ) { Text("Simpan") }
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedItemForDialog = null }) { Text("Batal") }
            }
        )
    }
}