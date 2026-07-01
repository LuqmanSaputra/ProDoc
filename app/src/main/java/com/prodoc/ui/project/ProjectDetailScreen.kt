package com.prodoc.ui.project

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.prodoc.data.local.entity.DiagramEntity
import com.prodoc.data.local.entity.LogicEntity
import com.prodoc.data.local.entity.MaterialEntity
import com.prodoc.data.local.entity.ProjectEntity
import com.prodoc.domain.hierarchy.HierarchySummary
import com.prodoc.model.QAStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: ProjectDetailViewModel,
    onBackClick: () -> Unit,
    onMaterialClick: (String) -> Unit,
    onLogicClick: (String) -> Unit,
    onDiagramClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val coroutineScope = rememberCoroutineScope()

    var reviewingMaterial by remember { mutableStateOf<MaterialEntity?>(null) }
    var reviewingLogic by remember { mutableStateOf<LogicEntity?>(null) }
    var reviewingDiagram by remember { mutableStateOf<DiagramEntity?>(null) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showEditProjectDialog by remember { mutableStateOf(false) }

    var pendingDeleteSubProject by remember { mutableStateOf<ProjectEntity?>(null) }
    var pendingEditSubProject by remember { mutableStateOf<SubProjectEditPayload?>(null) }

    var pendingDeleteMaterial by remember { mutableStateOf<MaterialEntity?>(null) }
    var pendingDeleteLogic by remember { mutableStateOf<LogicEntity?>(null) }
    var pendingDeleteDiagram by remember { mutableStateOf<DiagramEntity?>(null) }

    var pendingEditMaterial by remember { mutableStateOf<MaterialEditPayload?>(null) }
    var pendingEditLogic by remember { mutableStateOf<LogicEditPayload?>(null) }
    var pendingEditDiagram by remember { mutableStateOf<DiagramEditPayload?>(null) }

    val tabsToDisplay = remember(uiState.project?.parentProjectId) {
        if (uiState.project?.parentProjectId != null) {
            ProjectTab.entries.filter { it != ProjectTab.SUB_PROJECT }
        } else {
            ProjectTab.entries
        }
    }

    val pagerState = rememberPagerState(pageCount = { tabsToDisplay.size })

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { settledPage ->
            if (settledPage < tabsToDisplay.size) {
                val targetTab = tabsToDisplay[settledPage]
                if (uiState.currentTab != targetTab) {
                    viewModel.onTabSelected(targetTab)
                }
            }
        }
    }

    LaunchedEffect(uiState.currentTab) {
        val targetPage = tabsToDisplay.indexOf(uiState.currentTab)
        if (targetPage != -1 && pagerState.currentPage != targetPage && !pagerState.isScrollInProgress) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.project?.name ?: "Detail Project", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.project == null && !uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Gagal memuat detail project.", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                uiState.projectSummary?.let { summary ->
                    ProjectSummaryCard(summary = summary)
                }

                ScrollableTabRow(selectedTabIndex = pagerState.currentPage.coerceAtMost(tabsToDisplay.size - 1)) {
                    tabsToDisplay.forEachIndexed { index, tab ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(tab.title) }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val currentProject = uiState.project
                    if (currentProject != null) {
                        HorizontalPager(
                            state = pagerState,
                            key = { page -> tabsToDisplay.getOrNull(page)?.name ?: page.toString() },
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Top
                        ) { page ->
                            val currentTabType = tabsToDisplay.getOrNull(page) ?: ProjectTab.DETAIL
                            androidx.compose.runtime.key(currentTabType) {
                                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                    when (currentTabType) {
                                        ProjectTab.DETAIL -> {
                                            Column(
                                                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                                                verticalArrangement = Arrangement.spacedBy(16.dp)
                                            ) {
                                                Card(modifier = Modifier.fillMaxWidth()) {
                                                    Column(modifier = Modifier.padding(16.dp)) {
                                                        Text(text = currentProject.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                                        Spacer(modifier = Modifier.height(4.dp))
                                                        Text(text = "Kategori: ${currentProject.category}", modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                                                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                                                        Text(text = "Deskripsi Lapangan:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                                        Text(text = currentProject.description, style = MaterialTheme.typography.bodyLarge)
                                                    }
                                                }

                                                Card(modifier = Modifier.fillMaxWidth()) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column {
                                                            Text(text = "Status Audit Project", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                                            Text(text = currentProject.status.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                        }
                                                        SuggestionChip(onClick = {}, label = { Text(if (currentProject.parentProjectId != null) "Sub-Project" else "Main Project") })
                                                    }
                                                }

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                                ) {
                                                    Button(
                                                        onClick = { showDeleteConfirmDialog = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Delete, contentDescription = "Hapus Project")
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Hapus")
                                                    }

                                                    Button(
                                                        onClick = { showEditProjectDialog = true },
                                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                        modifier = Modifier.weight(1f)
                                                    ) {
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Project")
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Edit")
                                                    }
                                                }
                                            }
                                        }
                                        ProjectTab.SUB_PROJECT -> SubProjectTabContent(
                                            subProjects = uiState.subProjects,
                                            onAddSubProjectClick = { name, cat, desc -> viewModel.addSubProject(name, cat, desc) },
                                            onSubProjectClick = onProjectClick
                                        )
                                        ProjectTab.MATERIAL -> MaterialTabContent(
                                            materials = uiState.materials,
                                            onAddMaterialClick = { name, desc, qty, unit, uPrice -> viewModel.addMaterialToProject(name, desc, qty, unit, uPrice) },
                                            onEditMaterialClick = { entity, name, desc, qty, unit, uPrice -> pendingEditMaterial = MaterialEditPayload(entity, name, desc, qty, unit, uPrice) },
                                            onDeleteMaterialClick = { entity -> pendingDeleteMaterial = entity },
                                            onMaterialClick = onMaterialClick
                                        )
                                        ProjectTab.LOGIC -> LogicTabContent(
                                            logics = uiState.logics,
                                            onAddLogicClick = { name, desc, config -> viewModel.addLogicToProject(name, desc, config) },
                                            onEditLogicClick = { entity, name, desc, config -> pendingEditLogic = LogicEditPayload(entity, name, desc, config) },
                                            onDeleteLogicClick = { entity -> pendingDeleteLogic = entity },
                                            onLogicClick = onLogicClick
                                        )
                                        ProjectTab.DIAGRAM -> DiagramTabContent(
                                            diagrams = uiState.diagrams,
                                            onAddDiagramClick = { name, desc, photo, pdf, drawio -> viewModel.addDiagramToProject(name, desc, photo, pdf, drawio) },
                                            onEditDiagramClick = { entity, name, desc, photo, pdf, drawio -> pendingEditDiagram = DiagramEditPayload(entity, name, desc, photo, pdf, drawio) },
                                            onDeleteDiagramClick = { entity -> pendingDeleteDiagram = entity },
                                            onDiagramClick = onDiagramClick
                                        )
                                        ProjectTab.QA -> {
                                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                                item { Text("Pilih Dokumen Untuk Melakukan Validasi QA:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
                                                items(uiState.materials, key = { "qa_mat_${it.materialId}" }) { mat ->
                                                    ItemRowQA(name = "📦 Material: ${mat.name}", status = mat.qaStatus.name, modifier = Modifier.clickable { reviewingMaterial = mat })
                                                }
                                                items(uiState.logics, key = { "qa_log_${it.logicId}" }) { log ->
                                                    ItemRowQA(name = "⚙️ Logic Script: ${log.name}", status = log.qaStatus.name, modifier = Modifier.clickable { reviewingLogic = log })
                                                }
                                                items(uiState.diagrams, key = { "qa_diag_${it.diagramId}" }) { diag ->
                                                    ItemRowQA(name = "📊 Diagram Topologi: ${diag.name}", status = diag.qaStatus.name, modifier = Modifier.clickable { reviewingDiagram = diag })
                                                }
                                            }
                                        }
                                        ProjectTab.HISTORY -> {
                                            if (uiState.historyLogs.isEmpty()) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    Text("Belum ada histori yang tercatat.", color = MaterialTheme.colorScheme.outline)
                                                }
                                            } else {
                                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxSize()) {
                                                    items(uiState.historyLogs, key = { it.historyId }) { log ->
                                                        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                                                            Column(modifier = Modifier.padding(12.dp)) {
                                                                Text(text = log.actionDescription, style = MaterialTheme.typography.bodyMedium)
                                                                Spacer(modifier = Modifier.height(4.dp))
                                                                Text(text = dateFormat.format(Date(log.timestamp)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(enabled = false) {},
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }

    if (pendingDeleteSubProject != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteSubProject = null },
            title = { Text("Konfirmasi Hapus Sub-Project") },
            text = { Text("Apakah Anda yakin ingin menghapus dokumen sub-project '${pendingDeleteSubProject!!.name}'? Seluruh data komponen di dalamnya akan ikut dihapus secara permanen.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteSubProject(pendingDeleteSubProject!!); pendingDeleteSubProject = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteSubProject = null }) { Text("Batal") } }
        )
    }
    if (pendingEditSubProject != null) {
        AlertDialog(
            onDismissRequest = { pendingEditSubProject = null },
            title = { Text("Konfirmasi Perubahan Sub-Project") },
            text = { Text("Apakah Anda yakin ingin menyimpan perubahan data pada sub-project '${pendingEditSubProject!!.entity.name}'?") },
            confirmButton = {
                TextButton(onClick = { val p = pendingEditSubProject!!; viewModel.editSubProject(p.entity, p.name, p.category, p.description); pendingEditSubProject = null }) { Text("Ya, Ubah", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingEditSubProject = null }) { Text("Lanjut Ubah") } }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Konfirmasi Penghapusan Project") },
            text = { Text("Apakah Anda yakin ingin menghapus dokumen project ini? Seluruh data komponen di dalamnya akan ikut dibersihkan permanen.") },
            confirmButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false; viewModel.deleteCurrentProject { onBackClick() } }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Batal") } }
        )
    }

    if (pendingDeleteMaterial != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteMaterial = null },
            title = { Text("Konfirmasi Hapus Material") },
            text = { Text("Apakah Anda yakin ingin menghapus komponen material '${pendingDeleteMaterial!!.name}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteMaterial(pendingDeleteMaterial!!); pendingDeleteMaterial = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteMaterial = null }) { Text("Batal") } }
        )
    }

    if (showEditProjectDialog && uiState.project != null) {
        var name by remember { mutableStateOf(uiState.project!!.name) }
        var category by remember { mutableStateOf(uiState.project!!.category) }
        var desc by remember { mutableStateOf(uiState.project!!.description) }
        var showConfirmDialog by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showEditProjectDialog = false },
            title = { Text("Edit Project") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (name.isNotBlank() && category.isNotBlank()) {
                        showConfirmDialog = true
                    }
                }) { Text("Simpan", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditProjectDialog = false }) { Text("Batal") }
            }
        )

        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Konfirmasi Perubahan") },
                text = { Text("Apakah Anda yakin ingin menyimpan perubahan Project ini?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.editCurrentProject(name, category, desc)
                        showConfirmDialog = false
                        showEditProjectDialog = false
                    }) { Text("Ya, Simpan", fontWeight = FontWeight.Bold) }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) { Text("Batal") }
                }
            )
        }
    }

    if (pendingEditMaterial != null) {
        AlertDialog(
            onDismissRequest = { pendingEditMaterial = null },
            title = { Text("Konfirmasi Perubahan Material") },
            text = { Text("Apakah Anda yakin ingin menyimpan perubahan data pada material '${pendingEditMaterial!!.entity.name}'?") },
            confirmButton = {
                TextButton(onClick = { val p = pendingEditMaterial!!; viewModel.editMaterial(p.entity, p.name, p.desc, p.quantity, p.unit, p.unitPrice); pendingEditMaterial = null }) { Text("Ya, Ubah", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingEditMaterial = null }) { Text("Lanjut Ubah") } }
        )
    }

    if (pendingDeleteLogic != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteLogic = null },
            title = { Text("Konfirmasi Hapus Logic Script") },
            text = { Text("Apakah Anda yakin ingin menghapus dokumen logic '${pendingDeleteLogic!!.name}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteLogic(pendingDeleteLogic!!); pendingDeleteLogic = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteLogic = null }) { Text("Batal") } }
        )
    }
    if (pendingEditLogic != null) {
        AlertDialog(
            onDismissRequest = { pendingEditLogic = null },
            title = { Text("Konfirmasi Perubahan Logic Script") },
            text = { Text("Apakah Anda yakin ingin memperbarui berkas konfigurasi '${pendingEditLogic!!.entity.name}'?") },
            confirmButton = {
                TextButton(onClick = { val p = pendingEditLogic!!; viewModel.editLogic(p.entity, p.name, p.desc, p.config); pendingEditLogic = null }) { Text("Ya, Ubah", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingEditLogic = null }) { Text("Lanjut Ubah") } }
        )
    }

    if (pendingDeleteDiagram != null) {
        AlertDialog(
            onDismissRequest = { pendingDeleteDiagram = null },
            title = { Text("Konfirmasi Hapus Diagram") },
            text = { Text("Apakah Anda yakin ingin menghapus berkas diagram topologi '${pendingDeleteDiagram!!.name}'?") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteDiagram(pendingDeleteDiagram!!); pendingDeleteDiagram = null }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Hapus", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingDeleteDiagram = null }) { Text("Batal") } }
        )
    }
    if (pendingEditDiagram != null) {
        AlertDialog(
            onDismissRequest = { pendingEditDiagram = null },
            title = { Text("Konfirmasi Perubahan Diagram") },
            text = { Text("Apakah Anda yakin ingin memperbarui lampiran visual diagram '${pendingEditDiagram!!.entity.name}'?") },
            confirmButton = {
                TextButton(onClick = { val p = pendingEditDiagram!!; viewModel.editDiagram(p.entity, p.name, p.desc, p.photo, p.pdf, p.drawio); pendingEditDiagram = null }) { Text("Ya, Ubah", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { pendingEditDiagram = null }) { Text("Lanjut Ubah") } }
        )
    }

    if (reviewingMaterial != null) {
        ReviewQADialog(title = "Review QA: ${reviewingMaterial!!.name}", description = "Validasi aset perangkat material ini:", onDismiss = { reviewingMaterial = null },
            onApprove = { viewModel.updateMaterialQaStatus(reviewingMaterial!!, QAStatus.APPROVED); reviewingMaterial = null },
            onReject = { r -> viewModel.updateMaterialQaStatus(reviewingMaterial!!, QAStatus.REJECTED, r.ifBlank { "Ditolak oleh QA" }); reviewingMaterial = null }
        )
    }
    if (reviewingLogic != null) {
        ReviewQADialog(title = "Review QA: ${reviewingLogic!!.name}", description = "Validasi  berkas skrip konfigurasi jaringan ini:", onDismiss = { reviewingLogic = null },
            onApprove = { viewModel.updateLogicQaStatus(reviewingLogic!!, QAStatus.APPROVED); reviewingLogic = null },
            onReject = { r -> viewModel.updateLogicQaStatus(reviewingLogic!!, QAStatus.REJECTED, r.ifBlank { "Ditolak oleh QA" }); reviewingLogic = null }
        )
    }
    if (reviewingDiagram != null) {
        ReviewQADialog(title = "Review QA: ${reviewingDiagram!!.name}", description = "Validasi dokumen gambar topologi ini:", onDismiss = { reviewingDiagram = null },
            onApprove = { viewModel.updateDiagramQaStatus(reviewingDiagram!!, QAStatus.APPROVED); reviewingDiagram = null },
            onReject = { r -> viewModel.updateDiagramQaStatus(reviewingDiagram!!, QAStatus.REJECTED, r.ifBlank { "Ditolak oleh QA" }); reviewingDiagram = null }
        )
    }
}

@Composable
fun ItemRowQA(name: String, status: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            SuggestionChip(onClick = {}, label = { Text(status) })
        }
    }
}

@Composable
fun ReviewQADialog(title: String, description: String, onDismiss: () -> Unit, onApprove: () -> Unit, onReject: (String) -> Unit) {
    var reason by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(description)
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Alasan (Apabila di REJECTED)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onApprove) { Text("APPROVE", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) }
                TextButton(onClick = { onReject(reason) }) { Text("REJECT", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            }
        }
    )
}

@Composable
fun ProjectSummaryCard(
    summary: HierarchySummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "📊 Ringkasan Kumulatif Project (Utama & Sub)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Text(text = "📂 Sub Project: ${summary.totalSubProjects}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "📦 Material: ${summary.totalMaterials}", style = MaterialTheme.typography.bodyMedium)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
                    Text(text = "⚙️ Logic: ${summary.totalLogics}", style = MaterialTheme.typography.bodyMedium)
                    Text(text = "📐 Diagram: ${summary.totalDiagrams}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💰 Total Material Cost:",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = String.format(Locale.forLanguageTag("id-ID"), "Rp %,.0f", summary.totalMaterialCost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

data class SubProjectEditPayload(val entity: ProjectEntity, val name: String, val category: String, val description: String)
data class MaterialEditPayload(val entity: MaterialEntity, val name: String, val desc: String, val quantity: Double, val unit: com.prodoc.data.local.entity.MaterialUnit, val unitPrice: Double)
data class LogicEditPayload(val entity: LogicEntity, val name: String, val desc: String, val config: String)
data class DiagramEditPayload(val entity: DiagramEntity, val name: String, val desc: String, val photo: String, val pdf: String?, val drawio: String?)