package com.example.demo.mapper;

import com.example.demo.dto.TaskDTO;
import org.activiti.engine.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;


/**
 * @author chenxiang
 */
@Mapper(config = StructConfig.class)
public interface TestMapper {

    TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);


    TaskDTO toTaskDTO(Task task);

    List<TaskDTO> toTaskList(List<Task> tasks);


}
