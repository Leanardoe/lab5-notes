package com.example.notes.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.data.Note
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var currentNote by remember { mutableStateOf<Note?>(null) }

    fun newNote() {
        currentNote = null
        showDialog = true
    }

    fun editNote(note: Note) {
        currentNote = note
        showDialog = true
    }

    fun dismissDialog() {
        showDialog = false
    }

    fun saveNote(title: String, content: String) {
        coroutineScope.launch {
            val timestamp = System.currentTimeMillis()
            val note = currentNote?.copy(title = title, content = content, timestamp = timestamp)
                ?: Note(0, title, content, timestamp)

            if (note.id == 0) {
                viewModel.addNote(note)
            } else {
                viewModel.editNote(note)
            }
            showDialog = false
        }
    }

    fun deleteNote() {
        coroutineScope.launch {
            currentNote?.let {
                viewModel.deleteNote(it)
                showDialog = false
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { newNote() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Note")
            }
        }
    ) { innerPadding ->
        NoteList(
            noteList = homeUiState.noteList,
            onNoteClick = { note -> editNote(note) },
            modifier = Modifier.padding(innerPadding)
        )
    }

    if (showDialog) {
        AddDialog(
            note = currentNote,
            onDismissRequest = { dismissDialog() },
            onConfirmation = { title, content -> saveNote(title, content) },
            onDelete = { deleteNote() }
        )
    }
}

@Composable
fun NoteList(
    noteList: List<Note>,
    onNoteClick: (Note) -> Unit,
    modifier: Modifier
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(items = noteList, key = { it.id }) { note ->
            NoteCard(note = note, onClick = { onNoteClick(note) })
        }
    }
}

@Composable
fun NoteCard(note: Note, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = note.title,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = note.content,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun AddDialog(
    note: Note?,
    onDismissRequest: () -> Unit,
    onConfirmation: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    var noteTitle by remember { mutableStateOf(note?.title ?: "") }
    var noteText by remember { mutableStateOf(note?.content ?: "") }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (note == null) "Add Note" else "Edit Note",
                    textAlign = TextAlign.Left,
                    fontSize = 24.sp
                )

                OutlinedTextField(
                    value = noteTitle,
                    onValueChange = { noteTitle = it },
                    label = { Text("Title") }
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Content") }
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (note != null) {
                        TextButton(onClick = onDelete) {
                            Text("Delete")
                        }
                    }
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    TextButton(onClick = { onConfirmation(noteTitle, noteText) }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun NoteCardPreview() {
    val note = Note(1, "Test Note", "This is a test", System.currentTimeMillis())
    NoteCard(note, onClick = {})
}

@Composable
@Preview
fun AddDialogPreview() {
    AddDialog(
        note = null,
        onDismissRequest = {},
        onConfirmation = { _, _ -> },
        onDelete = {}
    )
}
