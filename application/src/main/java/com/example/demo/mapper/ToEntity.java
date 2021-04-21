package com.example.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
@Mapping(target = "name", source = "name1")
@Mapping(target = "age", source = "age1")
@Mapping(target = "birthday", source = "birthday1")
@Mapping(target = "eventStatus", source = "eventStatus1")
public @interface ToEntity {

}
