package com.ti.demo.domain.reactive;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("student")
public class ComplexStudent {

    @Id
    private Integer id;

    private String firstName;
    private String lastName;
    private String email;
    private List<Greeting> greetings;

    public static Mono<ComplexStudent> getComplexStudentMapping1(List<Map<String, Object>> rows) {
        return Mono.just(ComplexStudent.builder()
            .id(Integer.parseInt(rows.get(0).get("st_id").toString()))
            .firstName((String) rows.get(0).get("first_name"))
            .lastName((String) rows.get(0).get("last_name"))
            .email((String) rows.get(0).get("email"))
            .greetings(rows.stream().map(Greeting::getGreetingMapping1).filter(Objects::nonNull).toList())
            .build());
    }
    
}
