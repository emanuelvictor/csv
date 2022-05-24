package io.github.emanuelvictor.csv.example.domain.entity;

import io.github.emanuelvictor.commons.reflection.aspect.Ignore;
import io.github.emanuelvictor.csv.aspect.Index;
import io.github.emanuelvictor.csv.aspect.Label;

import java.time.LocalDateTime;

public class Student {

    @Ignore
    @Label("crossLabel")
    private String fieldToIgnore;

    @Ignore
    @Index(12)
    private String fieldToIgnore2;

    @Label("name")
    private String name;

    @Label("id")
    @Index(1)
    private int id;

    @Index(4)
    @Label("cpf")
    private String document;

    @Index(3)
    @Label("surname")
    private String surname;

    @Index(6)
    private Gender gender;

    @Index(5)
    private boolean active;

    private long registry;

    @Index(0)
    @Label("updatedOn")
    private LocalDateTime lastUpdate;

    @Index(9)
    private LocalDateTime createdOn;

    private String fieldToNotIgnore;

    @Label("fieldToNotIgnoreWithLabel")
    private String fieldToNotIgnore2;

    private String repetedField;

    @Label("repetedField")
    private String copyOfRepetedField;

    @Index(10)
    @Label("crossLabel")
    private String otherLabel;

    @Index(11)
    private String crossLabel;

    @Ignore
    private String fieldToIgnore3;
}