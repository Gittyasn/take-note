package com.mdtalalwasim.takenote.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;

public interface NotesService {
	
	public Notes saveNotes(Notes notes);
	
	public Optional<Notes> getNotesById(Integer noteId);
	
	public Notes noteById(Integer noteId);
	
	public List<Notes> getNotesByUser(User user);

	public Page<Notes> getNotesByUserPaginated(User user, Pageable pageable);
	
	public Page<Notes> getFavoriteNotesByUserPaginated(User user, Pageable pageable);
	
	public Page<Notes> getNotesByUserAndTagPaginated(User user, String tag, Pageable pageable);

	public Page<Notes> searchNotesByUser(User user, String query, Pageable pageable);
	
	public Notes updateNotes(Notes notes);
	
	public boolean deleteNotes(Integer noteId);

	public long countByUser(User user);

	public long countFavoriteByUser(User user);

}
