package com.prodoc.ui.project

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.prodoc.data.local.entity.DiagramEntity
import com.prodoc.model.QAStatus

@Composable
fun DiagramTabContent(
    diagrams: List<DiagramEntity>,
    onAddDiagramClick: (String, String, String, String?, String?) -> Unit,
    onEditDiagramClick: (DiagramEntity, String, String, String, String?, String?) -> Unit,
    onDeleteDiagramClick: (DiagramEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingDiagram by remember { mutableStateOf<DiagramEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (diagrams.isEmpty()) {
            Text(
                text = "Tambahkan dulu dokumentasi Diagram.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
            ) {
                items(diagrams, key = { it.diagramId }) { diagram ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = diagram.photoUrl,
                                contentDescription = "Foto Diagram ${diagram.name}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )

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
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text(diagram.qaStatus.name) }
                                    )
                                }

                                Text(
                                    text = diagram.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                if (diagram.qaStatus == QAStatus.REJECTED && !diagram.rejectionReason.isNullOrBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Alasan: ${diagram.rejectionReason}",
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (!diagram.pdfFilePath.isNullOrBlank()) {
                                    Text("📄 PDF: ${diagram.pdfFilePath}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                if (!diagram.drawioFilePath.isNullOrBlank()) {
                                    Text("📐 Draw.io: ${diagram.drawioFilePath}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (diagram.qaStatus != QAStatus.APPROVED) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            IconButton(onClick = { editingDiagram = diagram }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(onClick = { onDeleteDiagramClick(diagram) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                                            }
                                        }
                                    } else {
                                        Icon(Icons.Default.Lock, contentDescription = "Terkunci", tint = MaterialTheme.colorScheme.outline)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Diagram") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    // DIALOG TAMBAH DIAGRAM
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var photoUrl by remember { mutableStateOf("") }
        var pdfPath by remember { mutableStateOf("") }
        var drawioPath by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Dokumentasi Visual") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Diagram") }) }
                    item { OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Diagram") }) }
                    item { OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL / Path Foto Diagram") }) }
                    item { OutlinedTextField(value = pdfPath, onValueChange = { pdfPath = it }, label = { Text("Path File PDF (Opsional)") }) }
                    item { OutlinedTextField(value = drawioPath, onValueChange = { drawioPath = it }, label = { Text("Path File Drawio (Opsional)") }) }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && photoUrl.isNotBlank()) {
                            onAddDiagramClick(name, desc, photoUrl, pdfPath.ifBlank { null }, drawioPath.ifBlank { null })
                            showAddDialog = false
                        }
                    }
                ) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    // DIALOG EDIT DIAGRAM
    if (editingDiagram != null) {
        var name by remember { mutableStateOf(editingDiagram!!.name) }
        var desc by remember { mutableStateOf(editingDiagram!!.description) }
        var photoUrl by remember { mutableStateOf(editingDiagram!!.photoUrl) }
        var pdfPath by remember { mutableStateOf(editingDiagram!!.pdfFilePath ?: "") }
        var drawioPath by remember { mutableStateOf(editingDiagram!!.drawioFilePath ?: "") }

        AlertDialog(
            onDismissRequest = { editingDiagram = null },
            title = { Text("Edit Dokumentasi Visual") },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Diagram") }) }
                    item { OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Diagram") }) }
                    item { OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("URL / Path Foto Diagram") }) }
                    item { OutlinedTextField(value = pdfPath, onValueChange = { pdfPath = it }, label = { Text("Path File PDF (Opsional)") }) }
                    item { OutlinedTextField(value = drawioPath, onValueChange = { drawioPath = it }, label = { Text("Path File Drawio (Opsional)") }) }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && photoUrl.isNotBlank()) {
                            onEditDiagramClick(editingDiagram!!, name, desc, photoUrl, pdfPath.ifBlank { null }, drawioPath.ifBlank { null })
                            editingDiagram = null
                        }
                    }
                ) { Text("Ubah") }
            },
            dismissButton = { TextButton(onClick = { editingDiagram = null }) { Text("Batal") } }
        )
    }
}