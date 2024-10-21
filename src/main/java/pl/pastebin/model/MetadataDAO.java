package pl.pastebin.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class MetadataDAO {
    private String name;
    private String text;
    private Integer createdAt;
    private Integer expressionDate;
}
