package moon.odyssey.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import moon.odyssey.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
