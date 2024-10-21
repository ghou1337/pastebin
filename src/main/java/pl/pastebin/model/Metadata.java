package pl.pastebin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "metadata")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Metadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hash")
    private String hash;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "expression_date")
    private Timestamp expressionDate;
}
