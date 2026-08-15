package com.mdtalalwasim.takenote.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;

/**
 * Bug #15 fix: Tags are now scoped per-user.
 * Previously tags were global (shared across all users with same name),
 * meaning if User A created tag "Work" and User B also used "Work",
 * they'd share the same Tag row. Now each user has their own tag rows.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    // Each tag now belongs to a specific user — tags are user-scoped.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "tags")
    private Set<Notes> notes;
}
