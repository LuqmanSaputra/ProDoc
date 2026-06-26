package com.prodoc.ui.project

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.DiagramEntity
import com.prodoc.model.QAStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiagramTabContent(
    diagrams: List<DiagramEntity>,
    onAddDiagramClick: (String, String, String, String?, String?) -> Unit,
    onEditDiagramClick: (DiagramEntity, String, String, String, String?, String?) -> Unit,
    onDeleteDiagramClick: (DiagramEntity) -> Unit,
    onDiagramClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingDiagram by remember { mutableStateOf<DiagramEntity?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        if (diagrams.isEmpty()) {
            Text(
                text = "Tambahkan dulu dokumen diagram.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
            ) {
                items(
                    items = diagrams,
                    key = { it.diagramId },
                    contentType = { "DiagramItemCard" }
                ) { diagram ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDiagramClick(diagram.diagramId) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = diagram.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                SuggestionChip(onClick = {}, label = { Text(diagram.qaStatus.name) })
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = diagram.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Last Update: ${dateFormat.format(Date())}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                if (diagram.qaStatus != QAStatus.APPROVED) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { editingDiagram = diagram }) {
                                            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { onDeleteDiagramClick(diagram) }) {
                                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                } else {
                                    Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, null) },
            text = { Text("Diagram") },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var photoUrl by remember { mutableStateOf("") }
        var pdfPath by remember { mutableStateOf("") }
        var drawioPath by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Diagram Baru") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Diagram") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL Foto Topologi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pdfPath, onValueChange = { pdfPath = it }, label = { Text("Path File PDF (Opsional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = drawioPath, onValueChange = { drawioPath = it }, label = { Text("Path File Draw.io (Opsional)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && photoUrl.isNotBlank()) {
                        onAddDiagramClick(
                            name,
                            desc,
                            photoUrl,
                            pdfPath.ifBlank { null },
                            drawioPath.ifBlank { null }
                        )
                        showAddDialog = false
                    }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    if (editingDiagram != null) {
        var name by remember { mutableStateOf(editingDiagram!!.name) }
        var desc by remember { mutableStateOf(editingDiagram!!.description) }
        var photoUrl by remember { mutableStateOf(editingDiagram!!.photoUrl) }
        var pdfPath by remember { mutableStateOf(editingDiagram!!.pdfFilePath ?: "") }
        var drawioPath by remember { mutableStateOf(editingDiagram!!.drawioFilePath ?: "") }

        AlertDialog(
            onDismissRequest = { editingDiagram = null },
            title = { Text("Ubah Dokumen Diagram") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Diagram") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL Foto Topologi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = pdfPath, onValueChange = { pdfPath = it }, label = { Text("Path File PDF (Opsional)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = drawioPath, onValueChange = { drawioPath = it }, label = { Text("Path File Draw.io (Opsional)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && photoUrl.isNotBlank()) {
                        onEditDiagramClick(
                            editingDiagram!!,
                            name,
                            desc,
                            photoUrl,
                            pdfPath.ifBlank { null },
                            drawioPath.ifBlank { null }
                        )
                        editingDiagram = null
                    }
                }) { Text("Ubah") }
            },
            dismissButton = { TextButton(onClick = { editingDiagram = null }) { Text("Batal") } }
        )
    }
}