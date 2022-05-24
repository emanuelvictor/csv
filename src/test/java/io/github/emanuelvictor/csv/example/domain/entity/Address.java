package io.github.emanuelvictor.csv.example.domain.entity;

import io.github.emanuelvictor.csv.aspect.Label;
import lombok.Getter;

public class Address {

    @Getter
    @Label("nome")
    private final String name;

    @Getter
    @Label("codigo")
    private final long code;

    @Getter
    @Label("rua")
    private final String street;

    @Getter
    @Label("numero")
    private final int number;

    public Address(final String name, final long code, final String street, final int number) {
        this.name = name;
        this.code = code;
        this.street = street;
        this.number = number;
    }
}