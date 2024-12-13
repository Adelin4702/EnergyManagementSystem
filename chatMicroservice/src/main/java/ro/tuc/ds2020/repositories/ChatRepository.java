package ro.tuc.ds2020.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ro.tuc.ds2020.entities.Chat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
//    List<Chat> findByName(String name);
//
//    /**
//     * Example: Write Custom Query
//     */
//    @Query(value = "SELECT p " +
//            "FROM Chat p " +
//            "WHERE p.name = :name ")
//    Optional<Chat> findSeniorsByName(@Param("name") String name);
//    Optional<Chat> findById(UUID id);
//
//    Chat findByEmail(String email);
}
