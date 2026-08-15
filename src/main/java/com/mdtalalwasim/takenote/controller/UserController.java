package com.mdtalalwasim.takenote.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import com.mdtalalwasim.takenote.entity.Notes;
import com.mdtalalwasim.takenote.entity.User;
import com.mdtalalwasim.takenote.repository.UserRepository;
import com.mdtalalwasim.takenote.service.NotesService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/user")//this will only accessible when user is login, as we mention in securityConfig class.
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotesService notesService;
	
	
	//everytime if user is login 
	//@ModelAttribute help to access the below method to every where, for the login user 
	@ModelAttribute
	public User getUser(Principal principal, Model model) {
		String email = principal.getName();
		User user = this.userRepository.findByEmail(email);
		
		model.addAttribute("user",user); //binding to view.
		model.addAttribute("loggedInUser", user);
		
		return user;
	}
	
	
	
	@GetMapping("/add-notes")
	public String addNotes() {
		return "add-notes";
	}
	
	@GetMapping("/view-notes")
	public String viewNotes(Model model, Principal principal) {
		User user = getUser(principal, model);
		List<Notes> listOfNotesByUser = this.notesService.getNotesByUser(user);
		model.addAttribute("listOfNotesByUser",listOfNotesByUser);
		return "view-notes";
	}
	
	@GetMapping("/edit-notes/{id}")
	public String editNotes(@PathVariable("id") Integer id, Model model, Principal principal) {
		Notes notes = this.notesService.getNotesById(id).orElse(null);
		if (notes == null || !notes.getUser().getEmail().equals(principal.getName())) {
			return "redirect:/user/view-notes";
		}
		model.addAttribute("notes",notes);
		
		return "edit-notes";
	}
	
	@PostMapping("/save-notes")
	public String saveNotes(@Valid @ModelAttribute Notes notes, BindingResult bindingResult, HttpSession session, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			session.setAttribute("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
			return "redirect:/user/add-notes";
		}
		notes.setId(0); // Force creation to prevent note hijacking via ID spoofing
		return processNotePersistence(notes, session, principal, model, "Note Save Successfully", "redirect:/user/add-notes");
	}
	
	@PostMapping("/update-notes")
	public String updateNotes(@Valid @ModelAttribute Notes notes, BindingResult bindingResult, HttpSession session, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			session.setAttribute("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
			return "redirect:/user/view-notes";
		}
		
		if (notes.getId() > 0) {
			Notes existingNote = this.notesService.getNotesById(notes.getId()).orElse(null);
			if (existingNote == null || !existingNote.getUser().getEmail().equals(principal.getName())) {
				session.setAttribute("message", "Access denied!");
				return "redirect:/user/view-notes";
			}
		} else {
			session.setAttribute("message", "Invalid note ID!");
			return "redirect:/user/view-notes";
		}
		
		return processNotePersistence(notes, session, principal, model, "Note Updated Successfully", "redirect:/user/view-notes");
	}

	private String processNotePersistence(Notes notes, HttpSession session, Principal principal, Model model, String successMsg, String redirectPath) {
		notes.setUser(getUser(principal, model));
		Notes saveNotes = this.notesService.saveNotes(notes);

		String message = (saveNotes != null) ? successMsg : "Something wrong on server";
		session.setAttribute("message", message);
		return redirectPath;
	}
	
	@GetMapping("/delete-notes/{id}")
	public String deleteNotes(@PathVariable("id") Integer id, HttpSession session, Principal principal) {
		Notes existingNote = this.notesService.getNotesById(id).orElse(null);
		if (existingNote == null || !existingNote.getUser().getEmail().equals(principal.getName())) {
			session.setAttribute("message", "Access denied!");
			return "redirect:/user/view-notes";
		}
		
		boolean deleteNotes = this.notesService.deleteNotes(id);
		if(deleteNotes) {
			session.setAttribute("message", "Note is Deleted Successfully!");
		}else {
			session.setAttribute("message", "Something wrong on server!");
		}
		
		return "redirect:/user/view-notes";
	}
	
	
	
}
