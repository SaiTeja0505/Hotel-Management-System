package com.hotel.hotel_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "owner_approvals")
public class OwnerApproval {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    public OwnerApproval() {}

    public OwnerApproval(Long id, User owner, User approvedBy, Status status) {
        this.id = id;
        this.owner = owner;
        this.approvedBy = approvedBy;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        APPROVED,
        REJECTED,
        PENDING
    }
}
