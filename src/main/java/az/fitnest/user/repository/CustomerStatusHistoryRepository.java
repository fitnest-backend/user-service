package az.fitnest.user.repository;

import az.fitnest.user.model.entity.CustomerStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerStatusHistoryRepository extends JpaRepository<CustomerStatusHistory, Long> {
    List<CustomerStatusHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}