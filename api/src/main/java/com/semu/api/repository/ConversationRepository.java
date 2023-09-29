package com.semu.api.repository;

import com.semu.api.model.Conversation;
import com.semu.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Conversation findByIdAndUser(Long id, User user);
}
