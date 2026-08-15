package com.mdtalalwasim.takenote.api;

import com.mdtalalwasim.takenote.dto.NoteDTO;
import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.service.NotesService;
import com.mdtalalwasim.takenote.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("null")
public class NoteRestControllerTest {

    @Mock
    private NotesService notesService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NoteRestController noteRestController;

    private Principal mockPrincipal;
    private User mockUser;
    private Notes mockNote;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockPrincipal = mock(Principal.class);
        when(mockPrincipal.getName()).thenReturn("test@test.com");

        mockUser = new User();
        mockUser.setEmail("test@test.com");

        mockNote = new Notes();
        mockNote.setId(1);
        mockNote.setTitle("Mock Title");
        mockNote.setDescription("Mock Description");
        mockNote.setUser(mockUser);
    }

    @Test
    void testGetNoteById_Success() {
        when(notesService.noteById(1)).thenReturn(mockNote);

        ResponseEntity<NoteDTO> response = noteRestController.getNoteById(1, mockPrincipal);

        assertEquals(200, response.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody());
        assertEquals("Mock Title", response.getBody().getTitle());
    }

    @Test
    void testGetAllNotesWithPagination_Success() {
        // Prepare mock page
        Page<Notes> page = new PageImpl<>(Collections.singletonList(mockNote));
        
        when(userService.getUserByEmail(any())).thenReturn(mockUser);
        when(notesService.getNotesByUserPaginated(eq(mockUser), any(Pageable.class))).thenReturn(page);
        
        ResponseEntity<Page<NoteDTO>> response = noteRestController.getAllNotes(0, 10, null, false, null, mockPrincipal);
        
        assertEquals(200, response.getStatusCode().value());
        org.junit.jupiter.api.Assertions.assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
    }
}
