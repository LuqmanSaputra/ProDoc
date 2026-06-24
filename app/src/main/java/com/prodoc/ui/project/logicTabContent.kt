package com.prodoc.ui.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.model.QAStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LogicTabContent(
    logics: List<LogicEntity>,
    onAddLogicClick: (String, String, String) -> Unit,
    onEditLogicClick: (LogicEntity, String, String, String) -> Unit,
    onDeleteLogicClick: (LogicEntity) -> Unit,
    onLogicClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingLogic by remember { mutableStateOf<LogicEntity?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        if (logics.isEmpty()) {
            Text(
                text = "Tambahkan dulu konfigurasi (Logic).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
            ) {
                items(logics, key = { it.logicId }) { logic ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLogicClick(logic.logicId) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = logic.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                SuggestionChip(onClick = {}, label = { Text(logic.qaStatus.name) })
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = logic.description,
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

                                if (logic.qaStatus != QAStatus.APPROVED) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { editingLogic = logic }) {
                                            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { onDeleteLogicClick(logic) }) {
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
            text = { Text("Logic") },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var config by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Konfigurasi Teknis Baru") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Konfigurasi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = config,
                        onValueChange = { config = it },
                        label = { Text("Isi Script") },
                        minLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.fillMaxWidth()
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
            title = { Text("Edit Konfigurasi Teknis") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Konfigurasi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(
                        value = config,
                        onValueChange = { config = it },
                        label = { Text("Isi Script") },
                        minLines = 4,
                        textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.fillMaxWidth()
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