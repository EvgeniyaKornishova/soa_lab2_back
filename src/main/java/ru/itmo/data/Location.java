package ru.itmo.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Location {
    private Integer x; //Поле не может быть null
    private Float y;
    private Float z;
}
