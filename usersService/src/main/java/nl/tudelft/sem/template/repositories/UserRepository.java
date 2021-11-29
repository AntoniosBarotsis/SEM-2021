package nl.tudelft.sem.template.repositories;

import javax.transaction.Transactional;

import nl.tudelft.sem.template.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import nl.tudelft.sem.template.entities.User;


public interface UserRepository extends JpaRepository<User, String> {

    User findByIdEquals(String id);

    /**
     * Change User password.
     *
     * @param userId     User's ID.
     * @param password   New password.
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 WHERE u.id = ?1")
    void updateUserPassword(String userId, String password);

    /**
     * Change User's name.
     *
     * @param userId     User's ID.
     * @param name   New name.
     */
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.name = ?2 WHERE u.id = ?1")
    void updateUser(String userId, String name);

    @Transactional
    @Modifying
    @Query("update User u set u.role = ?2, u.name = ?3, u.password = ?4 where u.id = ?1")
    int updateUserById(String id, Role role, String name, String password);
}
