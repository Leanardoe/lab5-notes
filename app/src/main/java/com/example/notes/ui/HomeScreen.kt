package com.example.notes.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.data.Note
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val homeUiState by viewModel.homeUiState.collectAsState()
    val AddDialog = remember { mutableStateOf(false) }
    val EditDialog = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val newNote : (note: Note) -> Unit = { note ->
        coroutineScope.launch {
            viewModel.addNote(note)
            AddDialog.value = false
        }
    }

    val selectNote : (note: Note) -> Unit = { note ->
        homeUiState.currentNote = note
        EditDialog.value = true
    }

    val editNote : (note: Note) -> Unit = { note ->
        coroutineScope.launch {
            viewModel.editNote(note)
            EditDialog.value = false
        }
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(
            onClick = { AddDialog.value = true }
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "New Note"
            )
        }
    }) { innerPadding ->
        when {
            AddDialog.value -> {
                NoteDialog(
                    onDismissRequest = { AddDialog.value = false },
                    onConfirmation = newNote
                )
            }
        }

        when {
            EditDialog.value -> {
                NoteDialog(
                    onDismissRequest = { EditDialog.value = false },
                    onConfirmation = editNote,
                    note = homeUiState.currentNote
                )
            }
        }

        NoteList(
            noteList = homeUiState.noteList,
            onNoteClick = selectNote,
            Modifier.padding(innerPadding)
        )
    }

}

@Composable
fun NoteList(
    noteList: List<Note>,
    onNoteClick: (Note) -> Unit,
    modifier: Modifier
) {
    LazyColumn (
        modifier = Modifier.padding(16.dp)
    ) {
        items(items = noteList, key = {it.id}) { item ->
            NoteCard(item, onNoteClick)
        }
    }
}

@Composable
fun NoteCard(note: Note, onNoteClick: (Note) -> Unit, modifier: Modifier = Modifier) {
    val selectedNote = note

    Card (
        modifier = Modifier.fillMaxWidth()
            .clickable { onNoteClick(note) }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = selectedNote.title,
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = selectedNote.content,
                textAlign = TextAlign.Left
            )
        }
    }
}

@Composable
fun NoteDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (Note) -> Unit,
    note: Note? = null
) {
    var noteTitle by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var dialogTitle = "Add Note"


    if (note != null) {
        dialogTitle = "Edit Note"
        noteTitle = note.title
        noteText = note.content
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = dialogTitle,
                modifier = Modifier.padding(top = 24.dp, start = 20.dp, end = 20.dp),
                textAlign = TextAlign.Left,
                fontSize = 24.sp
            )

            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    TextButton(
                        onClick = {
                            if (note == null) {
                                val newNote : Note = Note(0, noteTitle, noteText, System.currentTimeMillis())
                                onConfirmation(newNote)
                            }
                            else {
                                val editNote : Note = Note(note.id, noteTitle, noteText, note.timestamp)
                                onConfirmation(editNote)
                            }
                          },
                        modifier = Modifier.padding(8.dp)
                    ) {
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
    val onNoteClick : (Note) -> Unit = {}
    NoteCard(note, onNoteClick)
}

@Composable
@Preview
fun AddDialogPreview() {
    val onDismissRequest = {}
    val onConfirmation : (Note) -> Unit = {}
    NoteDialog(onDismissRequest, onConfirmation)
}