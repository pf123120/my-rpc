package cn.nju.peter;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    public HelloObject() {}

    private Integer id;
    private String message;
}
