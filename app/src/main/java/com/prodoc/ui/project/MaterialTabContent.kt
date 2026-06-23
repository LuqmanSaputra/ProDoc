package com.prodoc.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.MaterialEntity
import com.prodoc.model.QAStatus
import java.text.NumberFormat
import java.util.Locale

@Composable
fun MaterialTabContent(
    materials: List<MaterialEntity>,
    onAddMaterialClick: (String, String, Double) -> Unit,
    onEditMaterialClick: (MaterialEntity, String, String, Double) -> Unit,
    onDeleteMaterialClick: (MaterialEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMaterial by remember { mutableStateOf<MaterialEntity?>(null) }
    val rupiahFormatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")) }

    Box(modifier = modifier.fillMaxSize()) {
        if (materials.isEmpty()) {
            Text(
                text = "Tambahkan dulu dokumentasi material.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
            ) {
                items(materials, key = { it.materialId }) { material ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = material.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = rupiahFormatter.format(material.price),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = material.description, style = MaterialTheme.typography.bodyMedium)

                            if (material.qaStatus == QAStatus.REJECTED && !material.rejectionReason.isNullOrBlank()) {
                                Text("Alasan: ${material.rejectionReason}", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                SuggestionChip(onClick = {}, label = { Text("QA: ${material.qaStatus.name}") })

                                if (material.qaStatus != QAStatus.APPROVED) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        IconButton(onClick = { editingMaterial = material }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                                        }
                                        IconButton(onClick = { onDeleteMaterialClick(material) }) {
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

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("Material") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var priceStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Material Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Barang/Material")
                        }
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Deskripsi / Spesifikasi")
                        }
                    )
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Harga (Rp)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && price > 0) { onAddMaterialClick(name, desc, price); showAddDialog = false }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    if (editingMaterial != null) {
        var name by remember { mutableStateOf(editingMaterial!!.name) }
        var desc by remember { mutableStateOf(editingMaterial!!.description) }
        var priceStr by remember { mutableStateOf(editingMaterial!!.price.toString()) }

        AlertDialog(
            onDismissRequest = { editingMaterial = null },
            title = { Text("Edit Data Material") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") })
                    OutlinedTextField(value = priceStr, onValueChange = { priceStr = it }, label = { Text("Harga (Rp)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val price = priceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) { onEditMaterialClick(editingMaterial!!, name, desc, price); editingMaterial = null }
                }) { Text("Ubah") }
            },
            dismissButton = { TextButton(onClick = { editingMaterial = null }) { Text("Batal") } }
        )
    }
}