package ru.pufr.repo;

import org.springframework.data.repository.CrudRepository;
import ru.pufr.models.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
