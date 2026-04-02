package com.example.demo.repository;


import com.example.demo.entity.User;
import com.example.demo.entity.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User user1;

    @BeforeEach
    void setUp() {
        // Создаем обычных пользователей (USER)
        user1 = new User();
        user1.setName("Mihael");
        user1.setEmail("mihael@mail.com");
        user1.setPassword("pass1");
        user1.setUserRole(UserRole.USER);

        User user2 = new User();
        user2.setName("Anna");
        user2.setEmail("anna@mail.com");
        user2.setPassword("pass2");
        user2.setUserRole(UserRole.USER);

        // Создаем администратора (не USER)
        User adminUser = new User();
        adminUser.setName("Admin");
        adminUser.setEmail("admin@mail.com");
        adminUser.setPassword("adminpass");
        adminUser.setUserRole(UserRole.ADMIN);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(adminUser);
        entityManager.flush();
    }

    @Test
    void findByName_shouldReturnUsersWithGivenNameAndUserRole() {
        // WHEN
        List<User> users = userRepository.findByName("Mihael");

        // THEN
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getName()).isEqualTo("Mihael");
        assertThat(users.get(0).getUserRole()).isEqualTo(UserRole.USER);
        assertThat(users.get(0).getEmail()).isEqualTo("mihael@mail.com");
    }

    @Test
    void findByName_shouldReturnEmptyList_whenNameNotFound() {
        // WHEN
        List<User> users = userRepository.findByName("NonExistentName");

        // THEN
        assertThat(users).isEmpty();
    }

    @Test
    void findByName_shouldNotReturnAdminUsers_EvenWithMatchingName() {
        // GIVEN - создаем USER с именем "Admin" и ADMIN с именем "Admin"
        User userWithAdminName = new User();
        userWithAdminName.setName("Admin");
        userWithAdminName.setEmail("useradmin@mail.com");
        userWithAdminName.setPassword("pass");
        userWithAdminName.setUserRole(UserRole.USER);
        entityManager.persist(userWithAdminName);

        // Уже есть adminUser с именем "Admin" и ролью ADMIN

        // WHEN
        List<User> users = userRepository.findByName("Admin");

        // THEN - должен вернуть только USER с именем Admin, но не ADMIN
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getEmail()).isEqualTo("useradmin@mail.com");
        assertThat(users.get(0).getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void findByName_shouldReturnAllUsersWithSameName() {
        User anotherMihael = new User();
        anotherMihael.setName("Mihael");
        anotherMihael.setEmail("mihael2@mail.com");
        anotherMihael.setPassword("pass3");
        anotherMihael.setUserRole(UserRole.USER);
        entityManager.persist(anotherMihael);

        List<User> users = userRepository.findByName("Mihael");

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("mihael@mail.com", "mihael2@mail.com");
    }

    @Test
    void findByEmail_shouldReturnUser_whenEmailExists() {
        Optional<User> found = userRepository.findByEmail("mihael@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Mihael");
        assertThat(found.get().getEmail()).isEqualTo("mihael@mail.com");
        assertThat(found.get().getUserRole()).isEqualTo(UserRole.USER);
    }

    @Test
    void findByEmail_shouldReturnAdminUser_whenAdminEmailExists() {
        Optional<User> found = userRepository.findByEmail("admin@mail.com");

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Admin");
        assertThat(found.get().getUserRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@mail.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findByEmail_shouldBeCaseSensitive() {
        Optional<User> found = userRepository.findByEmail("MIHAEL@MAIL.COM");
        assertThat(found).isEmpty();
    }

    @Test
    void findAllUsers_shouldReturnOnlyUsersWithUserRole() {
        List<User> users = userRepository.findAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(User::getEmail)
                .containsExactlyInAnyOrder("mihael@mail.com", "anna@mail.com");
        assertThat(users).allMatch(user -> user.getUserRole() == UserRole.USER);
        assertThat(users).noneMatch(user -> user.getUserRole() == UserRole.ADMIN);
    }

    @Test
    void findAllUsers_shouldReturnAllUserRoleUsers_RegardlessOfName() {
        User user3 = new User();
        user3.setName("Any Name");
        user3.setEmail("user3@mail.com");
        user3.setUserRole(UserRole.USER);
        entityManager.persist(user3);

        List<User> users = userRepository.findAllUsers();

        assertThat(users).hasSize(3);
        assertThat(users).extracting(User::getEmail)
                .contains("mihael@mail.com", "anna@mail.com", "user3@mail.com");
    }

    @Test
    void save_shouldSetIdAndPersistUser() {
        User newUser = new User();
        newUser.setName("New");
        newUser.setEmail("new@mail.com");
        newUser.setPassword("newpass");
        newUser.setUserRole(UserRole.USER);

        User saved = userRepository.save(newUser);

        assertThat(saved.getId()).isNotNull();

        User found = entityManager.find(User.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("new@mail.com");
    }

    @Test
    void deleteById_shouldRemoveUser() {
        // GIVEN
        Long userId = user1.getId();

        // WHEN
        userRepository.deleteById(userId);
        entityManager.flush();

        // THEN
        User deleted = entityManager.find(User.class, userId);
        assertThat(deleted).isNull();
    }
}
