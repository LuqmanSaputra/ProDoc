package com.prodoc.ui.project.detail

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.*
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogicDetailScreen(
    viewModel: LogicDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val logic by viewModel.logic.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Script Logic", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (logic == null) {
                Text(
                    text = "Data konfigurasi tidak ditemukan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val currentLogic = logic!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = currentLogic.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Text(text = "Deskripsi Konfigurasi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Text(text = currentLogic.description, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Status Validasi", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            SuggestionChip(
                                onClick = {},
                                label = { Text("Status: ${currentLogic.qaStatus.name}", fontWeight = FontWeight.SemiBold) }
                            )

                            if (!currentLogic.rejectionReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Alasan:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                Text(text = currentLogic.rejectionReason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Isi Script / Konfigurasi Teknis", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = Color(0xFF1E1E1E),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .horizontalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = currentLogic.configText,
                                        color = Color(0xFF9CDCFE),
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Waktu Pembuatan", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    Text(dateFormat.format(Date()), style = MaterialTheme.typography.bodySmall)
                                }
                                Column {
                                    Text("Terakhir Diubah", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    Text(dateFormat.format(Date()), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}