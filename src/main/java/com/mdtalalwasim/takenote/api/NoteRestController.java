package com.mdtalalwasim.takenote.api;

import com.mdtalalwasim.takenote.dto.NoteDTO;
import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.service.NotesService;
import com.mdtalalwasim.takenote.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.mdtalalwasim.takenote.entity.Tag;

@RestController
@RequestMapping("/api/notes")
public class NoteRestController {

    @Autowired
    private NotesService notesService;

    @Autowired
    private UserService userService;

    @Autowired
    private com.mdtalalwasim.takenote.repository.TagRepository tagRepository;

    private NoteDTO mapToDTO(Notes note) {
        NoteDTO dto = new NoteDTO();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setDescription(note.getDescription());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setFavorite(note.isFavorite());
        if (note.getTags() != null) {
            dto.setTags(note.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        }
        return dto;
    }

    @GetMapping
    public ResponseEntity<Page<NoteDTO>> getAllNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") boolean favoriteOnly,
            @RequestParam(required = false) String tag,
            Principal principal) {
        
        User user = userService.getUserByEmail(principal.getName());
        // Sort by favorite first (true > false, meaning DESC), then by createdAt DESC
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favorite", "createdAt"));
        
        Page<Notes> notesPage;
        if (search != null && !search.trim().isEmpty()) {
            notesPage = notesService.searchNotesByUser(user, search, pageable);
        } else if (favoriteOnly) {
            notesPage = notesService.getFavoriteNotesByUserPaginated(user, pageable);
        } else if (tag != null && !tag.trim().isEmpty()) {
            notesPage = notesService.getNotesByUserAndTagPaginated(user, tag, pageable);
        } else {
            notesPage = notesService.getNotesByUserPaginated(user, pageable);
        }
        
        Page<NoteDTO> dtoPage = notesPage.map(this::mapToDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @GetMapping("/all-tags")
    public ResponseEntity<List<String>> getTags(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(tagRepository.findAllTagNamesByUser(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDTO> getNoteById(@PathVariable Integer id, Principal principal) {
        Notes note = notesService.noteById(id);
        if (note == null) return ResponseEntity.notFound().build();

        // Verify ownership — prevent cross-user note access
        if (!note.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(mapToDTO(note));
    }

    @PostMapping
    public ResponseEntity<NoteDTO> createNote(@Valid @RequestBody NoteDTO noteDTO, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Notes note = new Notes();
        note.setTitle(noteDTO.getTitle());
        note.setDescription(noteDTO.getDescription());
        note.setFavorite(noteDTO.isFavorite());
        note.setUser(user);
        
        if (noteDTO.getTags() != null) {
            // Bug #15 fix: tags are now scoped per-user using findByNameAndUser
            Set<Tag> tags = noteDTO.getTags().stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(tagName -> {
                    return tagRepository.findByNameAndUser(tagName.trim(), user).orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName.trim());
                        newTag.setUser(user);
                        return tagRepository.save(newTag);
                    });
                }).collect(Collectors.toSet());
            note.setTags(tags);
        }
        
        Notes savedNote = notesService.saveNotes(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedNote));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Integer id, @Valid @RequestBody NoteDTO noteDetails, Principal principal) {
        Notes note = notesService.noteById(id);
        if (note == null) return ResponseEntity.notFound().build();

        // Verify ownership
        if (!note.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = note.getUser();
        note.setTitle(noteDetails.getTitle());
        note.setDescription(noteDetails.getDescription());
        note.setFavorite(noteDetails.isFavorite());

        if (noteDetails.getTags() != null) {
            // Bug #15 fix: tags are now scoped per-user using findByNameAndUser
            Set<Tag> tags = noteDetails.getTags().stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(tagName -> {
                    return tagRepository.findByNameAndUser(tagName.trim(), user).orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName.trim());
                        newTag.setUser(user);
                        return tagRepository.save(newTag);
                    });
                }).collect(Collectors.toSet());
            note.setTags(tags);
        } else {
            note.getTags().clear();
        }

        Notes updatedNote = notesService.updateNotes(note);
        return ResponseEntity.ok(mapToDTO(updatedNote));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Integer id, Principal principal) {
        Notes note = notesService.noteById(id);
        if (note == null) return ResponseEntity.notFound().build();
        
        // Verify ownership
        if (!note.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        notesService.deleteNotes(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        long total = notesService.countByUser(user);
        long favorites = notesService.countFavoriteByUser(user);
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("favorites", favorites);
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<NoteDTO> toggleFavorite(@PathVariable Integer id, Principal principal) {
        Notes note = notesService.noteById(id);
        if (note == null) return ResponseEntity.notFound().build();
        
        if (!note.getUser().getEmail().equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        note.setFavorite(!note.isFavorite());
        return ResponseEntity.ok(mapToDTO(notesService.updateNotes(note)));
    }
}
