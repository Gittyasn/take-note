package com.mdtalalwasim.takenote.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.repository.NotesRepository;
import com.mdtalalwasim.takenote.service.NotesService;

@Service
@SuppressWarnings("null")
public class NotesServiceImpl implements NotesService {

	@Autowired
	NotesRepository notesRepository;
	
	@Override
	public Notes saveNotes(Notes notes) {
		
		return this.notesRepository.save(notes);
	}

	@Override
	public Optional<Notes> getNotesById(Integer noteId) {
		
		return this.notesRepository.findById(noteId);
	
	} 

	@Override
	public List<Notes> getNotesByUser(User user) {
		List<Notes> userNotes = this.notesRepository.findByUser(user);
		return userNotes;
	}

	@Override
	public Page<Notes> getNotesByUserPaginated(User user, Pageable pageable) {
		return this.notesRepository.findByUser(user, pageable);
	}

	@Override
	public Page<Notes> getFavoriteNotesByUserPaginated(User user, Pageable pageable) {
		return this.notesRepository.findByUserAndFavoriteTrue(user, pageable);
	}

	@Override
	public Page<Notes> getNotesByUserAndTagPaginated(User user, String tag, Pageable pageable) {
		return this.notesRepository.findByUserAndTagsName(user, tag, pageable);
	}

	@Override
	public Page<Notes> searchNotesByUser(User user, String query, Pageable pageable) {
		return this.notesRepository.searchNotesByUser(user, query, pageable);
	}

	@Override
	public Notes updateNotes(Notes notes) {
		return this.notesRepository.save(notes);
	}

	@Override
	public boolean deleteNotes(Integer noteId) {
		Optional<Notes> opt = this.notesRepository.findById(noteId);
		if(opt.isPresent()) {
			Notes note = opt.get();
			note.setDeleted(true);
			this.notesRepository.save(note);
			return true;
		}
		return false;
	}

	@Override
	public Notes noteById(Integer noteId) {
		return this.notesRepository.findById(noteId).orElse(null);
	}

	@Override
	public long countByUser(User user) {
		return this.notesRepository.countByUser(user);
	}

	@Override
	public long countFavoriteByUser(User user) {
		return this.notesRepository.countByUserAndFavoriteTrue(user);
	}
}
