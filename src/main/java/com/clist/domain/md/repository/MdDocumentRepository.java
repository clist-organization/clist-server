package com.clist.domain.md.repository;

import com.clist.domain.md.entity.MdDocument;
import com.clist.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MdDocumentRepository extends JpaRepository<MdDocument, UUID> {
    List<MdDocument> findAllByUser(User user);
    Optional<MdDocument> findByUserAndTitle(User user, String title);
    boolean existsByUserAndTitle(User user, String title);
    void deleteByUserAndTitle(User user, String title);
}