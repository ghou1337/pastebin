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
public class DataResponse {
    private String text;
    private Timestamp createdAt;
    private Timestamp expressionDate;
}
