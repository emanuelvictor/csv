package io.github.emanuelvictor.csv.example.domain.entity;

import io.github.emanuelvictor.csv.aspect.Index;

public class Costumer {

    @Index(1)
    private int id;

    @Index(2)
    private String document;

    @Index(3)
    private String name;

    @Index(3)
    private String surname;

    @Index(5)
    private boolean active;

}
