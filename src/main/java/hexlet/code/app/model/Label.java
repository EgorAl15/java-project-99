package hexlet.code.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import org.hibernate.Hibernate;

@Entity
@Table(name = "labels")
public class Label {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 1000)
  private String name;

  private LocalDateTime createdAt;

  @PrePersist
  public void prePersist() {
    createdAt = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (object == null || Hibernate.getClass(this) != Hibernate.getClass(object)) {
      return false;
    }

    Label label = (Label) object;

    return id != null && Objects.equals(id, label.id);
  }

  @Override
  public final int hashCode() {
    return Hibernate.getClass(this).hashCode();
  }
}
