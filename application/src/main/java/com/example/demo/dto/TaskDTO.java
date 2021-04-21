package com.example.demo.dto;

import lombok.Data;

@Data
public class TaskDTO {

    private String id;

    private String assignee;

    private String taskDefinitionKey;

    private String processDefinitionId;

    private String name;

}
