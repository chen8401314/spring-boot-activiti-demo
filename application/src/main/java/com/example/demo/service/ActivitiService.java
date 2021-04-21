package com.example.demo.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ActivitiService {
	//注入为我们自动配置好的服务
	@Autowired
	private RuntimeService runtimeService;
	@Autowired
	private TaskService taskService;

	//开始流程，传入申请者的id以及公司的id
	public void startProcess(String processId, String key) {
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("personId", "test1");
		variables.put("compId", "test2");
		variables.put("assignee", "张三");
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processId,key, variables);
		System.out.println("流程变量：" + processInstance.getProcessVariables());
	}

	//获得某个人的任务别表
	public List<Task> getTasks(String assignee) {
		return taskService.createTaskQuery().taskAssignee(assignee).list();
	}

	//完成任务
	public void completeTasks(int flag, String taskId) {
		Map<String, Object> taskVariables = new HashMap<String, Object>();
		taskVariables.put("flag", flag);
		taskService.complete(taskId, taskVariables);
	}
	public void setVariable(String taskId) {
		taskService.setVariable(taskId, "你好", "真的好");
		taskService.setVariableLocal(taskId, "你不好", "真的不好");
	}
}
