package com.hotel.hotel_management.repository;

import com.hotel.hotel_management.model.OwnerApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OwnerApprovalRepository extends JpaRepository<OwnerApproval, Long> {
    List<OwnerApproval> findByStatus(OwnerApproval.Status status);
}
