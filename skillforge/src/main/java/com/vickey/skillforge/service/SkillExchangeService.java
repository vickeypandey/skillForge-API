package com.vickey.skillforge.service;

import com.vickey.skillforge.dto.CreateExchangeRequestDto;
import com.vickey.skillforge.dto.SkillExchangeResponseDto;
import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.SkillExchange;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.SkillExchangeStatus;
import com.vickey.skillforge.exception.*;
import com.vickey.skillforge.repository.SkillExchangeRepository;
import com.vickey.skillforge.repository.SkillRepository;
import com.vickey.skillforge.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillExchangeService {
    private final SkillExchangeRepository exchangeRepository;
    private final SkillRepository skillRepository;
    private final UserRepository userRepository;
    private final NotificationService notifications;

    public SkillExchangeService(SkillExchangeRepository exchangeRepository,
                                SkillRepository skillRepository,
                                UserRepository userRepository,
                                NotificationService notifications) {
        this.exchangeRepository = exchangeRepository;
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
        this.notifications = notifications;
    }

    @Transactional
    public SkillExchangeResponseDto create(String email, CreateExchangeRequestDto request) {
        User requester = user(email);
        Skill offered = skill(request.getOfferedSkillId());
        Skill requested = skill(request.getRequestedSkillId());
        if (!offered.getUser().getId().equals(requester.getId())) {
            throw new ForbiddenOperationException("The offered skill must belong to you");
        }
        if (requested.getUser().getId().equals(requester.getId())) {
            throw new InvalidExchangeTransitionException("You cannot request an exchange with yourself");
        }
        SkillExchange exchange = new SkillExchange();
        exchange.setRequester(requester);
        exchange.setSkillOwner(requested.getUser());
        exchange.setOfferedSkill(offered);
        exchange.setRequestedSkill(requested);
        SkillExchange saved = exchangeRepository.save(exchange);
        notifications.notify(saved.getSkillOwner(), "EXCHANGE_REQUESTED", "You received a new skill exchange request");
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<SkillExchangeResponseDto> mine(String email) {
        User participant = user(email);
        return exchangeRepository.findByRequesterOrSkillOwnerOrderByCreatedAtDesc(participant, participant)
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public SkillExchangeResponseDto get(String email, Long id) {
        User participant = user(email);
        SkillExchange exchange = exchange(id);
        requireParticipant(exchange, participant);
        return toDto(exchange);
    }

    @Transactional
    public SkillExchangeResponseDto accept(String email, Long id) {
        SkillExchange exchange = participantExchange(email, id);
        if (!exchange.getSkillOwner().getEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenOperationException("Only the skill owner can accept this request");
        }
        transition(exchange, SkillExchangeStatus.PENDING, SkillExchangeStatus.ACCEPTED);
        notifications.notify(exchange.getRequester(), "EXCHANGE_ACCEPTED", "Your skill exchange request was accepted");
        return toDto(exchange);
    }

    @Transactional
    public SkillExchangeResponseDto reject(String email, Long id) {
        SkillExchange exchange = participantExchange(email, id);
        if (!exchange.getSkillOwner().getEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenOperationException("Only the skill owner can reject this request");
        }
        transition(exchange, SkillExchangeStatus.PENDING, SkillExchangeStatus.REJECTED);
        notifications.notify(exchange.getRequester(), "EXCHANGE_REJECTED", "Your skill exchange request was declined");
        return toDto(exchange);
    }

    @Transactional
    public SkillExchangeResponseDto cancel(String email, Long id) {
        SkillExchange exchange = participantExchange(email, id);
        if (!exchange.getRequester().getEmail().equalsIgnoreCase(email)) {
            throw new ForbiddenOperationException("Only the requester can cancel this request");
        }
        transition(exchange, SkillExchangeStatus.PENDING, SkillExchangeStatus.CANCELLED);
        notifications.notify(exchange.getSkillOwner(), "EXCHANGE_CANCELLED", "A skill exchange request was cancelled");
        return toDto(exchange);
    }

    @Transactional
    public SkillExchangeResponseDto complete(String email, Long id) {
        SkillExchange exchange = participantExchange(email, id);
        transition(exchange, SkillExchangeStatus.ACCEPTED, SkillExchangeStatus.COMPLETED);
        notifications.notify(exchange.getRequester(), "EXCHANGE_COMPLETED", "A skill exchange was marked complete");
        notifications.notify(exchange.getSkillOwner(), "EXCHANGE_COMPLETED", "A skill exchange was marked complete");
        return toDto(exchange);
    }

    private void transition(SkillExchange exchange, SkillExchangeStatus from, SkillExchangeStatus to) {
        if (exchange.getStatus() != from) {
            throw new InvalidExchangeTransitionException(
                    "Cannot change exchange from " + exchange.getStatus() + " to " + to);
        }
        exchange.setStatus(to);
    }

    private SkillExchange participantExchange(String email, Long id) {
        User participant = user(email);
        SkillExchange exchange = exchange(id);
        requireParticipant(exchange, participant);
        return exchange;
    }

    private void requireParticipant(SkillExchange exchange, User user) {
        if (!exchange.getRequester().getId().equals(user.getId())
                && !exchange.getSkillOwner().getId().equals(user.getId())) {
            throw new ForbiddenOperationException("You are not a participant in this exchange");
        }
    }

    private SkillExchange exchange(Long id) {
        return exchangeRepository.findById(id)
                .orElseThrow(() -> new ExchangeNotFoundException("Exchange not found"));
    }

    private User user(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Skill skill(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new SkillNotFoundException("Skill not found"));
    }

    private SkillExchangeResponseDto toDto(SkillExchange e) {
        return new SkillExchangeResponseDto(e.getId(),
                e.getRequester().getId(), e.getRequester().getName(),
                e.getSkillOwner().getId(), e.getSkillOwner().getName(),
                e.getOfferedSkill().getId(), e.getOfferedSkill().getSkillName(),
                e.getRequestedSkill().getId(), e.getRequestedSkill().getSkillName(),
                e.getStatus(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
