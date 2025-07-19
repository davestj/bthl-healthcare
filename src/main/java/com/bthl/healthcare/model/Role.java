package com.bthl.healthcare.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private List<String> permissions = new ArrayList<>();

    @Column(name = "is_system_role")
    private Boolean isSystemRole = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    public Role() {
        this.permissions = new ArrayList<>();
        this.users = new HashSet<>();
        this.isSystemRole = false;
    }

    public Role(String name, String description, List<String> permissions) {
        this();
        this.name = name;
        this.description = description;
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>();
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getPermissions() { return permissions; }
    public void setPermissions(List<String> permissions) { 
        this.permissions = permissions != null ? new ArrayList<>(permissions) : new ArrayList<>(); 
    }

    public Boolean getIsSystemRole() { return isSystemRole; }
    public void setIsSystemRole(Boolean isSystemRole) { this.isSystemRole = isSystemRole; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    public boolean hasPermission(String permission) {
        return permissions != null && permissions.contains(permission);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Role role = (Role) obj;
        return Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
