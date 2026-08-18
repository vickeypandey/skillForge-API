package com.vickey.skillforge.repository;
import com.vickey.skillforge.entity.RefreshToken;
import com.vickey.skillforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long>
{
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}