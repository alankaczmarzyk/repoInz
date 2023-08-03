package inzynierka.backend.Core.Entities;

import inzynierka.backend.Role.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role,String> {
    Optional<Role> findByName(ERole name);
}
