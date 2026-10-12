package com.vickey.skillforge.service;

import com.vickey.skillforge.dto.NotificationResponseDto;
import com.vickey.skillforge.entity.Notification;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.exception.ForbiddenOperationException;
import com.vickey.skillforge.exception.UserNotFoundException;
import com.vickey.skillforge.repository.NotificationRepository;
import com.vickey.skillforge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void notify(User recipient, String type, String message) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(type);
        notification.setMessage(message);
        repository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> mine(String email) {
        User user = user(email);
        return repository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toDto).toList();
    }

    @Transactional
    public NotificationResponseDto markRead(String email, Long id) {
        User user = user(email);
        Notification notification = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Notification not found"));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You cannot update this notification");
        }
        notification.setRead(true);
        return toDto(notification);
    }

    private User user(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    private NotificationResponseDto toDto(Notification n) {
        return new NotificationResponseDto(n.getId(), n.getType(), n.getMessage(), n.isRead(), n.getCreatedAt());
    }
}
