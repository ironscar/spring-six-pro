package com.ti.demo.domain.reactive;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("greeting")
public class Greeting {

    @Id
    private Integer id;

    private String message;

    public Greeting(String msg) {
        message = msg;
    }

    public static Greeting getGreetingMapping1(Map<String, Object> row) {
        if (row.get("g_id") != null) {
            return Greeting.builder()
                .id(Integer.parseInt(row.get("g_id").toString()))
                .message((String) row.get("g_msg"))
                .build();
        }
        return null;
    }

}
