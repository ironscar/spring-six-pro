package com.ti.demo.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    
    private static final long serialVersionUID = 1L;

    public Integer id;

    public String firstName;

    public String lastName;

    public String email;

    public Student(String fname, String lname, String email) {
        this.firstName = fname;
        this.lastName = lname;
        this.email = email;
    }

}
