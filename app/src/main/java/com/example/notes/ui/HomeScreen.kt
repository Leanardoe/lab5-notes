package com.example.notes.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.room.Update
import com.example.notes.data.Note
import kotlinx.coroutines.selects.select
import java.time.Instant
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalLayoutDirection

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

    // Step 1: Track dialog visibility
    var showDialog by remember { mutableStateOf(false) }
    var editDialog by remember { mutableStateOf(false) }
    var selectedNote by remember { mutableStateOf<Note?>(null) }

    // Step 2: Conditionally show the dialog
    if (showDialog) {
        AddDialog(
            onDismissRequest = { showDialog = false },
            onConfirmation = {
                NewNote()
                showDialog = false
            }
        )
    }
    else if (editDialog && selectedNote != null) {
        EditDialog(
            note = selectedNote!!,
            onDismissRequest = { editDialog = false },
            onConfirmation = { updatedNote ->
                EditNote(updatedNote) // ⬅️ pass it in
                editDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(
            onClick = { NewNote() }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Note"
            )
        }
    }) { innerPadding ->
        NoteList(
            //noteList = homeUiState.noteList,
            noteList = listOf(
                Note(1, "Test Note 1", "This is the content of note 1.", System.currentTimeMillis()),
                Note(2, "Test Note 2", "This is the content of note 2.", System.currentTimeMillis()),
                Note(3, "Test Note 3", "This is the content of note 3.", System.currentTimeMillis())
            ),
            modifier = Modifier.padding(innerPadding),
            onNoteClick = { note ->
                selectedNote = note
                editDialog = true
            }
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
    modifier: Modifier,
    onNoteClick: (Note) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(items = noteList, key = { it.id }) { item ->
            NoteCard(note = item, onClick = { onNoteClick(item) })
fun NoteList(noteList: List<Note>, onNoteClick: (Note) -> Unit, modifier: Modifier) {
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
fun NoteCard(note: Note, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(modifier = modifier.fillMaxWidth().clickable { onClick() }) {
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
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
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
fun EditDialog(
    note: Note,
    onDismissRequest: () -> Unit,
    onConfirmation: (Note) -> Unit // ⬅️ pass the updated note back
) {
    var title by remember { mutableStateOf(note.title) }
    var content by remember { mutableStateOf(note.content) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Edit Note", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Content") }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        val updatedNote = note.copy(title = title, content = content)
                        onConfirmation(updatedNote)
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
