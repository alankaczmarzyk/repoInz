package inzynierka.backend.Core.Entities;


import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findTokenByUserId(String id);

    void deleteByUserId(String id);
}