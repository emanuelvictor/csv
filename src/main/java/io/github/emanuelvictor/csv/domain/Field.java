package io.github.emanuelvictor.csv.domain;

import lombok.*;


@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Field {

    /**
     * Native name of the field
     */
    @Getter
    @Setter
    private String nativeName;

    @Setter
    private String label;

    @Getter
    @Setter
    private Integer index;

    @Getter
    @Setter
    private Object value;

    /**
     * @param label String
     */
    public Field(final String label) {
        this.label = label;
    }

    /**
     * @param label    String
     * @param index Integer
     */
    public Field(final String label, final Integer index) {
        this.label = label;
        this.index = index;
    }

    /**
     * @return String
     */
    public String getLabel() {
        return (label == null || label.length() == 0) ? nativeName : label;
    }
}

