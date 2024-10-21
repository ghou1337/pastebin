package pl.pastebin.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Data {
    private String name;
    private String text;
    private Timestamp createdAt;
    private Integer expressionDate;
    private String UUID;
}
