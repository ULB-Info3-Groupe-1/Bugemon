package ulb.repositories;

import java.util.List;
import java.util.Optional;

import ulb.models.bugemon.Bugemon;

public interface BugemonRepository {

    List<Bugemon> findAll();

    Optional<Bugemon> findByName(String name);

    Optional<Bugemon> findById(String id);
}
