package kz.spring.demo.model;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Developer {
    private long id;
    private String name;
    private String surname;
}
