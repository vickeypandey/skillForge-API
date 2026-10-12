package com.vickey.skillforge;

import com.vickey.skillforge.entity.SkillExchange;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.SkillExchangeStatus;
import com.vickey.skillforge.exception.ForbiddenOperationException;
import com.vickey.skillforge.repository.SkillExchangeRepository;
import com.vickey.skillforge.repository.SkillRepository;
import com.vickey.skillforge.repository.UserRepository;
import com.vickey.skillforge.service.NotificationService;
import com.vickey.skillforge.service.SkillExchangeService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SkillExchangeAuthorizationTest {
    @Test
    void requesterCannotAcceptTheirOwnExchangeRequest() {
        SkillExchangeRepository exchanges = mock(SkillExchangeRepository.class);
        UserRepository users = mock(UserRepository.class);
        SkillExchange exchange = new SkillExchange();
        User requester = user(1L, "requester@example.test");
        User owner = user(2L, "owner@example.test");
        exchange.setRequester(requester);
        exchange.setSkillOwner(owner);
        exchange.setStatus(SkillExchangeStatus.PENDING);
        when(users.findByEmail(requester.getEmail())).thenReturn(Optional.of(requester));
        when(exchanges.findById(9L)).thenReturn(Optional.of(exchange));
        SkillExchangeService service = new SkillExchangeService(exchanges, mock(SkillRepository.class),
                users, mock(NotificationService.class));

        assertThrows(ForbiddenOperationException.class,
                () -> service.accept(requester.getEmail(), 9L));
        verify(exchanges, never()).save(any());
    }

    private static User user(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        return user;
    }
}
