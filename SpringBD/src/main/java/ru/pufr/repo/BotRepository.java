package ru.pufr.repo;

import org.hibernate.criterion.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.pufr.models.BotTable;

import java.util.Optional;

public interface BotRepository extends JpaRepository<BotTable, Long> {
    Optional<BotTable> findByName(String name);

}
