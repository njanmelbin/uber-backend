package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="constant")
public class Constant extends Auditable{

    String name;
    String value;

    public Long getAsLong(){
        return Long.parseLong(value);
    }
}
