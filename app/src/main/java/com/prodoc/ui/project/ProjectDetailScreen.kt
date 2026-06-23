package com.prodoc.ui.project

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddSubDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.project?.name ?: "Detail Proyek",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
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
        floatingActionButton = {
            if (uiState.currentTab == ProjectTab.DETAIL) {
                FloatingActionButton(
                    onClick = { showAddSubDialog = true },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Tambah Sub Project")
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            SecondaryScrollableTabRow(
                selectedTabIndex = uiState.currentTab.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                ProjectTab.entries.forEach { tab ->
                    Tab(
                        selected = uiState.currentTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = { Text(tab.displayName) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    when (uiState.currentTab) {
                        ProjectTab.DETAIL -> TabDetailContent(
                            project = uiState.project,
                            subProjects = uiState.subProjects,
                            onDeleteProjectClick = {
                                viewModel.deleteCurrentProject(onSuccess = onBackClick)
                            }
                        )

                        ProjectTab.MATERIAL -> MaterialTabContent(
                            materials = uiState.materials,
                            onAddMaterialClick = { name, desc, price ->
                                viewModel.addMaterialToProject(name, desc, price)
                            },
                            onEditMaterialClick = { entity, name, desc, price ->
                                viewModel.editMaterial(entity, name, desc, price)
                            },
                            onDeleteMaterialClick = { entity ->
                                viewModel.deleteMaterial(entity)
                            }
                        )

                        ProjectTab.LOGIC -> LogicTabContent(
                            logics = uiState.logics,
                            onAddLogicClick = { name, desc, config ->
                                viewModel.addLogicToProject(name, desc, config)
                            },
                            onEditLogicClick = { entity, name, desc, config ->
                                viewModel.editLogic(entity, name, desc, config)
                            },
                            onDeleteLogicClick = { entity ->
                                viewModel.deleteLogic(entity)
                            }
                        )

                        ProjectTab.DIAGRAM -> DiagramTabContent(
                            diagrams = uiState.diagrams,
                            onAddDiagramClick = { name, desc, photo, pdf, drawio ->
                                viewModel.addDiagramToProject(name, desc, photo, pdf, drawio)
                            },
                            onEditDiagramClick = { entity, name, desc, photo, pdf, drawio ->
                                viewModel.editDiagram(entity, name, desc, photo, pdf, drawio)
                            },
                            onDeleteDiagramClick = { entity ->
                                viewModel.deleteDiagram(entity)
                            }
                        )

                        ProjectTab.QA -> QaTabContent(
                            materials = uiState.materials,
                            logics = uiState.logics,
                            diagrams = uiState.diagrams,
                            onStatusChange = { rawObj, status, reason ->
                                when (rawObj) {
                                    is MaterialEntity -> viewModel.updateMaterialQaStatus(rawObj, status, reason)
                                    is LogicEntity -> viewModel.updateLogicQaStatus(rawObj, status, reason)
                                    is DiagramEntity -> viewModel.updateDiagramQaStatus(rawObj, status, reason)
                                }
                            }
                        )
                        ProjectTab.HISTORY -> TabHistoryContent(historyLogs = uiState.historyLogs)
                    }
                }
            }
        }
    }

    if (showAddSubDialog) {
        var subName by remember { mutableStateOf("") }
        var subCategory by remember { mutableStateOf("") }
        var subDesc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddSubDialog = false },
            title = { Text("Tambah Sub Project Baru") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = subName, onValueChange = { subName = it }, label = { Text("Nama Sub Project") })
                    OutlinedTextField(value = subCategory, onValueChange = { subCategory = it }, label = { Text("Kategori") })
                    OutlinedTextField(value = subDesc, onValueChange = { subDesc = it }, label = { Text("Deskripsi") })
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (subName.isNotBlank() && subCategory.isNotBlank()) {
                            viewModel.addSubProject(subName, subCategory, subDesc)
                            showAddSubDialog = false
                        }
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showAddSubDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun TabDetailContent(
    project: ProjectEntity?,
    subProjects: List<ProjectEntity>,
    onDeleteProjectClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (project != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Informasi Utama", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Kategori: ${project.category}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Status: ${project.status.name}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(project.description, style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Dibuat: ${dateFormat.format(Date(project.createdAt))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                        Text("Diubah: ${dateFormat.format(Date(project.updatedAt))}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Button(
                            onClick = { showDeleteConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hapus Semua Proyek", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Sub Project di Dalamnya (${subProjects.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (subProjects.isEmpty()) {
            item {
                Text("Belum ada sub-project teknis.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            items(subProjects, key = { it.projectId }) { sub ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(sub.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(sub.description, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Proyek Teknis?") },
            text = { Text("Tindakan ini akan menghapus proyek '${project?.name}' beserta seluruh data di dalamnya secara permanen dari perangkat dan cloud.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteProjectClick()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus Semua", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun TabHistoryContent(historyLogs: List<HistoryEntity>) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss (dd/MM)", Locale.getDefault()) }

    if (historyLogs.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada riwayat aktivitas.")
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
            items(historyLogs, key = { it.historyId }) { log ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.actionDescription, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(dateFormat.format(Date(log.timestamp)), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                HorizontalDivider(
                    modifier = Modifier,
                    thickness = DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}