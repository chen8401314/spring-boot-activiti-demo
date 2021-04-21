package com.example.demo.controller;

import com.example.demo.base.Response;
import com.example.demo.dto.TaskDTO;
import com.example.demo.mapper.TestMapper;
import com.example.demo.service.ActivitiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api(value = "MyRestController ")
@RestController
@RequestMapping(value = "/test", produces = {APPLICATION_JSON_UTF8_VALUE})
public class MyRestController {
	@Autowired
	private ActivitiService myService;

	@Autowired
	private TaskService taskService;

	@Autowired
	protected RepositoryService repositoryService;

	@ApiOperation(value = "开启流程实例")
	@RequestMapping(value = "/process", method = RequestMethod.GET)
	public Response startProcessInstance(@RequestParam String processId, @RequestParam String key) {
		myService.startProcess(processId,key);
		return Response.success();
	}

	@ApiOperation(value = "获取流程信息")
	@RequestMapping(value = "/getCurTask", method = RequestMethod.GET)
	public Response<List<TaskDTO>> getCurTask(@RequestParam String key) {
		List<Task> list = taskService.createTaskQuery().processInstanceBusinessKey(key).list();
		System.out.println("办理人"+list.get(0).getAssignee());
		return Response.success(TestMapper.INSTANCE.toTaskList(list));
	}

	@ApiOperation(value = "获取流程详细信息")
	@PostMapping(value = "/getTransition")
	public Response getTransition(@RequestBody TaskDTO taskDTO) {
		// 1.获取流程定义
		ProcessDefinitionEntity pd = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(taskDTO.getProcessDefinitionId());
		// 2.获取流程所有节点信息
		ActivityImpl activity = getActivityImpl(pd, taskDTO.getTaskDefinitionKey());
		if (activity != null) {
			// 3.通过活动对象找当前活动的所有出口
			List<PvmTransition> transitions = activity.getOutgoingTransitions();
			for (PvmTransition trans : transitions) {
				PvmActivity pvmActivity = trans.getDestination();
				//如果是子流程
				if(StringUtils.equals(pvmActivity.getProperty("type").toString(), "subProcess")){
					//遍历子流程所有任务节点
					for(PvmActivity pvmActivity1:pvmActivity.getActivities()){
						//找到开始节点
						if(StringUtils.equals(pvmActivity1.getProperty("type").toString(), "startEvent")){
							PvmActivity startActivity = pvmActivity1;
							//通过开始节点往下接续解析找到任务节点
							List<PvmTransition> test=	startActivity.getOutgoingTransitions();
						}
					}
				}
			}
		}

		return Response.success();
	}
	private ActivityImpl getActivityImpl(ProcessDefinitionEntity pd, String taskKey) {
		ActivityImpl activity = null;
		List<ActivityImpl> activityImplList = pd.getActivities();
		for (ActivityImpl activityImpl : activityImplList) {
			//找到当前节点
			if (StringUtils.equals(activityImpl.getId(), taskKey)) {
				activity = activityImpl;
				break;
			}
		}
		return activity;
	}
	//获取当前人的任务
	@RequestMapping(value = "/tasks", method = RequestMethod.GET)
	public List<TaskRepresentation> getTasks(@RequestParam String assignee) {
		List<Task> tasks = myService.getTasks(assignee);
		List<TaskRepresentation> dtos = new ArrayList<TaskRepresentation>();
		for (Task task : tasks) {
			dtos.add(new TaskRepresentation(task.getId(), task.getName(),task.getProcessVariables()));
		}
		return dtos;
	}

	//获取当前人的任务
	@RequestMapping(value = "/taskById", method = RequestMethod.GET)
	public Response getTaskById(@RequestParam String taskId) {
		Task task =  taskService.createTaskQuery().taskId(taskId).singleResult();
		Map<String, Object> vars = taskService.getVariables(taskId);
        for (String variableName : vars.keySet()) {
            String val = (String) vars.get(variableName);
            System.out.println(variableName + " = " +val);
        }
		return Response.success();
	}

	//开启流程实例
	@RequestMapping(value = "/setVariable/{taskId}", method = RequestMethod.GET)
	public Response setVariable(@PathVariable String taskId) {
		myService.setVariable(taskId);
		return Response.success();
	}


	//完成任务
	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public Response complete(@RequestParam int flag, @RequestParam String taskId) {
		myService.completeTasks(flag, taskId);
		return Response.success();
	}

	//Task的dto
	static class TaskRepresentation

	{
		private String id;
		private String name;
		private Map map;

		public Map getMap() {
			return map;
		}

		public void setMap(Map map) {
			this.map = map;
		}

		public TaskRepresentation(String id, String name, Map map) {
			this.id = id;
			this.name = name;
			this.map = map;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

}
