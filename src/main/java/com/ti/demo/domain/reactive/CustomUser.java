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
public class CustomUser {

    private String userId;
    private String password;
    private Integer age;
    private boolean enabled;
    private List<String> roles;
    
}
