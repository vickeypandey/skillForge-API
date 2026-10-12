package com.vickey.skillforge.repository;

import com.vickey.skillforge.entity.SkillExchange;
import com.vickey.skillforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkillExchangeRepository extends JpaRepository<SkillExchange, Long> {
    List<SkillExchange> findByRequesterOrSkillOwnerOrderByCreatedAtDesc(User requester, User skillOwner);
}
