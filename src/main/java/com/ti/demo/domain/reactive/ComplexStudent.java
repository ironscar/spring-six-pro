package com.ti.demo.domain.reactive;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplexStudent {

    private Integer id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Greeting> greetings;
    
}
