package com.example.projectcontrol.entities.Enum;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProjectStateEnum {
    ACTIVE("active"),
    PAUSED("paused"),
    MAINTENANCE("maintenance"),
    ARCHIVED("archived"),
    DISCONTINUED("discontinued");

    private final String value;

    ProjectStateEnum(String value) { this.value = value; }

    @JsonValue
    public String getValue() { return value; }
}
