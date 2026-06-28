package com.prodoc.ui.dashboard

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.model.ProjectStatus
import androidx.compose.material.icons.automirrored.filled.ExitToApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onProjectClick: (String) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var showLogoutConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ProDoc Dashboard", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(
                        onClick = {
                            Log.d("ProDoc", "[Dashboard] Tombol Logout di TopAppBar ditekan.")
                            showLogoutConfirmDialog = true
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Keluar Akun")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Project")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(title = "Total Project", count = uiState.projects.size.toString(), modifier = Modifier.weight(1f))
                SummaryCard(title = "Pending QA", count = uiState.projects.count { it.entity.status == ProjectStatus.PENDING_QA }.toString(), modifier = Modifier.weight(1f))
                SummaryCard(title = "Belum Sync", count = uiState.unSyncedCount.toString(), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Project atau kategori...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedStatusFilter == null,
                        onClick = { viewModel.onStatusFilterChanged(null) },
                        label = { Text("Semua") }
                    )
                }
                items(ProjectStatus.entries) { status ->
                    FilterChip(
                        selected = uiState.selectedStatusFilter == status,
                        onClick = { viewModel.onStatusFilterChanged(status) },
                        label = { Text(status.name) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Belum ada project", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.projects, key = { it.entity.projectId }) { projectItem ->
                        ProjectItemRow(
                            projectItem = projectItem,
                            onClick = { onProjectClick(projectItem.entity.projectId) }
                        )
                    }
                }
            }
        }
    }

    if (showLogoutConfirmDialog) {
        LaunchedEffect(Unit) {
            Log.d("ProDoc", "[Dashboard] Dialog Logout tampil (showLogoutConfirmDialog == true).")
        }
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Keluar dari Akun") },
            text = { Text("Apakah Anda yakin ingin keluar dari akun ini?") },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Batal")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Log.d("ProDoc", "[Dashboard] User menekan tombol Logout konfirmasi di dalam Dialog.")
                        showLogoutConfirmDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout", fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Tambah Project Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Project") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (e.g. Network, Elektronik)") })
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi Singkat") })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotBlank() && category.isNotBlank()) {
                            viewModel.addNewTestProject(name, category, desc)
                            showAddDialog = false
                        }
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun SummaryCard(title: String, count: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = modifier.padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProjectItemRow(projectItem: DashboardProjectItem, onClick: () -> Unit) {
    val project = projectItem.entity

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = project.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (project.parentProjectId != null) {
                        Text(text = "↳ Sub-Project", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.SemiBold)
                    }
                }
                SuggestionChip(
                    onClick = {},
                    label = { Text(project.status.name, style = MaterialTheme.typography.bodySmall) }
                )
            }
            Text(text = "Kategori: ${project.category}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = project.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outlineVariant)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "🌿 Sub: ${projectItem.subProjectCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = "📦 Mat: ${projectItem.materialCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = "⚙️ Log: ${projectItem.logicCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                Text(text = "📊 Diag: ${projectItem.diagramCount}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
            }
        }
    }
}