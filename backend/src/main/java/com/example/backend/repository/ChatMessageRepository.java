package com.example.backend.repository;

import com.example.backend.model.ChatMessage;
import com.example.backend.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Fetch last 10 messages for a user
    List<ChatMessage> findTop10ByUserOrderByTimestampDesc(User user);
    
    // Optional: Clean up history if needed
    void deleteByUser(User user);
}
