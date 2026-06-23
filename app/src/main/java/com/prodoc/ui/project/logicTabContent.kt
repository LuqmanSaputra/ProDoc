package com.prodoc.ui.project

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.model.QAStatus

@Composable
fun LogicTabContent(
    logics: List<LogicEntity>,
    onAddLogicClick: (String, String, String) -> Unit,
    onEditLogicClick: (LogicEntity, String, String, String) -> Unit,
    onDeleteLogicClick: (LogicEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLogic by remember { mutableStateOf<LogicEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        if (logics.isEmpty()) {
            Text(text = "Tambahkan dulu dokumentasi konfigurasi (Logic).", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline, modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)) {
                items(logics, key = { it.logicId }) { logic ->
                    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(text = logic.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                SuggestionChip(onClick = {}, label = { Text(logic.qaStatus.name) })
                            }
                            Text(text = logic.description, style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFF1E1E1E), shape = RoundedCornerShape(6.dp)) {
                                Box(modifier = Modifier.padding(12.dp).horizontalScroll(rememberScrollState())) {
                                    Text(text = logic.configText, color = Color(0xFF9CDCFE), fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                                if (logic.qaStatus != QAStatus.APPROVED) {
                                    IconButton(onClick = { editingLogic = logic }) { Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary) }
                                    IconButton(onClick = { onDeleteLogicClick(logic) }) { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) }
                                } else {
                                    Icon(Icons.Default.Lock, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(onClick = { showAddDialog = true }, icon = { Icon(Icons.Default.Add, null) }, text = { Text("Logic") }, containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var config by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Konfigurasi Teknis Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Konfigurasi") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
                    OutlinedTextField(
                        value = config,
                        onValueChange = { config = it },
                        label = { Text("Isi Script") },
                        minLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && config.isNotBlank()) {
                        onAddLogicClick(name, desc, config)
                        showAddDialog = false
                    }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    if (editingLogic != null) {
        var name by remember { mutableStateOf(editingLogic!!.name) }
        var desc by remember { mutableStateOf(editingLogic!!.description) }
        var config by remember { mutableStateOf(editingLogic!!.configText) }

        AlertDialog(
            onDismissRequest = { editingLogic = null },
            title = { Text("Ubah Konfigurasi Teknis") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Konfigurasi") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
                    OutlinedTextField(
                        value = config,
                        onValueChange = { config = it },
                        label = { Text("Isi Script") },
                        minLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace)
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && config.isNotBlank()) {
                        onEditLogicClick(editingLogic!!, name, desc, config)
                        editingLogic = null
                    }
                }) { Text("Ubah") }
            },
            dismissButton = { TextButton(onClick = { editingLogic = null }) { Text("Batal") } }
        )
    }
}