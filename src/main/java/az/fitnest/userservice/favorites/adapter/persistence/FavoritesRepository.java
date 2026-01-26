package az.fitnest.userservice.favorites.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.userservice.favorites.domain.model.Favorite;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Long>{

}
