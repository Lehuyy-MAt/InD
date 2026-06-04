package fu.se.beind.Repo;


import fu.se.beind.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByIsActiveTrueOrderBySortOrderAsc();
    Optional<Category> findBySlug(String slug);
}
