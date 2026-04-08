package nguyennhatquan.springbootreview.repository;

import nguyennhatquan.springbootreview.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByMomoOrderId(String momoOrderId);
    Optional<Payment> findByOrderId(Long orderId);
}
