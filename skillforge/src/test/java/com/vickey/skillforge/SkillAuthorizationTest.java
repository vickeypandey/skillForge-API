package com.vickey.skillforge;

import com.vickey.skillforge.dto.SkillRequestDto;
import com.vickey.skillforge.entity.Skill;
import com.vickey.skillforge.entity.User;
import com.vickey.skillforge.enums.Role;
import com.vickey.skillforge.exception.ForbiddenOperationException;
import com.vickey.skillforge.repository.SkillRepository;
import com.vickey.skillforge.repository.UserRepository;
import com.vickey.skillforge.service.SkillService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class SkillAuthorizationTest {

    @Test
    void anotherUserCannotUpdateSkill() {
        SkillRepository skills = mock(SkillRepository.class);
        UserRepository users = mock(UserRepository.class);
        User owner = user(1L, Role.USER);
        User attacker = user(2L, Role.USER);
        Skill skill = skill(owner);
        when(skills.findById(7L)).thenReturn(Optional.of(skill));
        when(users.findByEmail("attacker@example.test")).thenReturn(Optional.of(attacker));

        SkillService service = new SkillService(skills, users);
        assertThrows(ForbiddenOperationException.class,
                () -> service.updateSkill(7L, "attacker@example.test", new SkillRequestDto()));
        verify(skills, never()).save(any());
    }

    @Test
    void anotherUserCannotDeleteSkill() {
        SkillRepository skills = mock(SkillRepository.class);
        UserRepository users = mock(UserRepository.class);
        User owner = user(1L, Role.USER);
        User attacker = user(2L, Role.USER);
        when(skills.findById(7L)).thenReturn(Optional.of(skill(owner)));
        when(users.findByEmail("attacker@example.test")).thenReturn(Optional.of(attacker));

        SkillService service = new SkillService(skills, users);
        assertThrows(ForbiddenOperationException.class,
                () -> service.deleteSkill(7L, "attacker@example.test"));
        verify(skills, never()).delete(any(Skill.class));
    }

    private static User user(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setRole(role);
        return user;
    }

    private static Skill skill(User owner) {
        Skill skill = new Skill();
        skill.setId(7L);
        skill.setUser(owner);
        return skill;
    }
}
