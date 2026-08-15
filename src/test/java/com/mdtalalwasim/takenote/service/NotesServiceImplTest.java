package com.mdtalalwasim.takenote.service;

import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.repository.NotesRepository;
import com.mdtalalwasim.takenote.service.impl.NotesServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
public class NotesServiceImplTest {

    @Mock
    private NotesRepository notesRepository;

    @InjectMocks
    private NotesServiceImpl notesService;

    private Notes sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Notes();
        sampleNote.setId(1);
        sampleNote.setTitle("Test Note");
        sampleNote.setDescription("Valid Content Here");
        sampleNote.setFavorite(false);
    }

    @Test
    void testSaveNote_Success() {
        when(notesRepository.save(any(Notes.class))).thenReturn(sampleNote);
        
        Notes saved = notesService.saveNotes(new Notes());
        
        assertNotNull(saved);
        assertEquals("Test Note", saved.getTitle());
        verify(notesRepository, times(1)).save(any(Notes.class));
    }

    @Test
    void testGetNoteById_Found() {
        when(notesRepository.findById(1)).thenReturn(Optional.of(sampleNote));
        
        Optional<Notes> result = notesService.getNotesById(1);
        
        assertTrue(result.isPresent());
        assertEquals("Valid Content Here", result.get().getDescription());
    }

    @Test
    void testSoftDelete_Success() {
        when(notesRepository.findById(1)).thenReturn(Optional.of(sampleNote));
        
        boolean deleted = notesService.deleteNotes(1);
        
        assertTrue(deleted);
        assertTrue(sampleNote.isDeleted()); // Verify soft delete flag was set
        verify(notesRepository, times(1)).save(sampleNote);
    }
}
