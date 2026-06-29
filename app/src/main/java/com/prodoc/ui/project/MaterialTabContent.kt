package com.prodoc.ui.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.prodoc.data.local.entity.MaterialUnit
import com.prodoc.model.QAStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaterialTabContent(
    materials: List<MaterialEntity>,
    onAddMaterialClick: (String, String, Double, MaterialUnit, Double) -> Unit,
    onEditMaterialClick: (MaterialEntity, String, String, Double, MaterialUnit, Double) -> Unit,
    onDeleteMaterialClick: (MaterialEntity) -> Unit,
    onMaterialClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMaterial by remember { mutableStateOf<MaterialEntity?>(null) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Box(modifier = modifier.fillMaxSize()) {
        if (materials.isEmpty()) {
            Text(
                text = "Belum ada material perangkat yang dicatat.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize().padding(bottom = 72.dp)
            ) {
                items(
                    items = materials,
                    key = { it.materialId },
                    contentType = { "MaterialItemCard" }
                ) { material ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMaterialClick(material.materialId) },
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
                                SuggestionChip(onClick = {}, label = { Text("QA: ${material.qaStatus.name}") })
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = material.description,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (material.qaStatus == QAStatus.REJECTED && !material.rejectionReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Alasan Ditolak: ${material.rejectionReason}",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

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
        var qtyStr by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf(MaterialUnit.PCS) }
        var unitPriceStr by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Material Baru") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi / Spesifikasi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = { Text("Kuantitas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Text(text = "Satuan: ${unit.name}", modifier = Modifier.clickable {
                        val values = MaterialUnit.entries.toTypedArray()
                        unit = values[(unit.ordinal + 1) % values.size]
                    }.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = unitPriceStr, onValueChange = { unitPriceStr = it }, label = { Text("Harga Satuan (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val qty = qtyStr.toDoubleOrNull() ?: 0.0
                    val uPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && qty > 0 && uPrice >= 0) { onAddMaterialClick(name, desc, qty, unit, uPrice); showAddDialog = false }
                }) { Text("Simpan") }
            },
            dismissButton = { TextButton(onClick = { showAddDialog = false }) { Text("Batal") } }
        )
    }

    if (editingMaterial != null) {
        var name by remember { mutableStateOf(editingMaterial!!.name) }
        var desc by remember { mutableStateOf(editingMaterial!!.description) }
        var qtyStr by remember { mutableStateOf(editingMaterial!!.quantity.toString()) }
        var unit by remember { mutableStateOf(editingMaterial!!.unit) }
        var unitPriceStr by remember { mutableStateOf(editingMaterial!!.unitPrice.toString()) }

        AlertDialog(
            onDismissRequest = { editingMaterial = null },
            title = { Text("Edit Data Material") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = qtyStr, onValueChange = { qtyStr = it }, label = { Text("Kuantitas") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    Text(text = "Satuan: ${unit.name}", modifier = Modifier.clickable {
                        val values = MaterialUnit.entries.toTypedArray()
                        unit = values[(unit.ordinal + 1) % values.size]
                    }.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    OutlinedTextField(value = unitPriceStr, onValueChange = { unitPriceStr = it }, label = { Text("Harga Satuan (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val qty = qtyStr.toDoubleOrNull() ?: 0.0
                    val uPrice = unitPriceStr.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && qty > 0 && uPrice >= 0) { onEditMaterialClick(editingMaterial!!, name, desc, qty, unit, uPrice); editingMaterial = null }
                }) { Text("Perbarui") }
            },
            dismissButton = { TextButton(onClick = { editingMaterial = null }) { Text("Batal") } }
        )
    }
}