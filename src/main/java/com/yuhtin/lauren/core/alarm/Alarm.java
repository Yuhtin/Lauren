package com.yuhtin.lauren.core.alarm;

import io.github.eikefs.sql.provider.orm.ORM;
import io.github.eikefs.sql.provider.orm.annotations.Field;
import io.github.eikefs.sql.provider.orm.annotations.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@AllArgsConstructor
@Getter
@Table(name = "lauren_alarms", primary = "name")
public class Alarm extends ORM {

    @Field
    private final String name;

    @Field(type = "text")
    @Setter
    private String time;

    public static String create() { return create(Alarm.class); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarm alarm = (Alarm) o;
        return Objects.equals(name, alarm.name) &&
                Objects.equals(time, alarm.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time);
    }
}
