package com.clist.domain.md.entity;

import com.clist.domain.feedback.entity.FeedbackSession;
import com.clist.domain.quiz.entity.QuizSession;
import com.clist.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "md_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MdDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "mdDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuizSession> quizSessions = new ArrayList<>();

    @OneToMany(mappedBy = "mdDocument", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<FeedbackSession> feedbackSessions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}