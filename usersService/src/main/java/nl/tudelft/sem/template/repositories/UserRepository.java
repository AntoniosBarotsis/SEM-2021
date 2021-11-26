package nl.tudelft.sem.template.repositories;

import nl.tudelft.sem.template.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface UserRepository extends JpaRepository<User, String> {

    User findByIdEquals(String id);

    /**
     * Change User password.
     * @param userId     User's ID.
     * @param password   New password.
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 WHERE u.id = ?1")
    void updateUserPassword(String userId, String password);

    /**
     * Change User's name.
     * @param userId     User's ID.
     * @param name   New name.
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = ?2 WHERE u.id = ?1")
    void updateUser(String userId, String name);
}
