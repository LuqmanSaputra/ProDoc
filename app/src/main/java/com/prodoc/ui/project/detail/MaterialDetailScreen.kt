package com.prodoc.ui.project.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialDetailScreen(
    viewModel: MaterialDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val material by viewModel.material.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val rupiahFormatter = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")) }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rincian Lengkap Material", fontWeight = FontWeight.Bold) },
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
            } else if (material == null) {
                Text(
                    text = "Data material tidak ditemukan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val currentMaterial = material!!

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
                                text = currentMaterial.name,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            Text(text = "Harga Perangkat", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Text(
                                text = rupiahFormatter.format(currentMaterial.price),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Deskripsi / Spesifikasi Lapangan", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            Text(text = currentMaterial.description, style = MaterialTheme.typography.bodyLarge)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(text = "Status Validasi QA", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
                            SuggestionChip(
                                onClick = {},
                                label = { Text("QA: ${currentMaterial.qaStatus.name}", fontWeight = FontWeight.SemiBold) }
                            )

                            if (!currentMaterial.rejectionReason.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Alasan Penolakan QA:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                                Text(text = currentMaterial.rejectionReason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
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