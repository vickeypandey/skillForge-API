package com.vickey.skillforge.repository;
import com.vickey.skillforge.entity.RefreshToken;
import com.vickey.skillforge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long>
{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select token from RefreshToken token where token.token = :value")
    Optional<RefreshToken> findByToken(@Param("value") String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser(User user);
}
