package com.mvc.app.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mvc.app.common.MyUtil;
import com.mvc.app.common.PaginateUtil;
import com.mvc.app.common.RequestUtils;
import com.mvc.app.domain.dto.EmployeeDto;
import com.mvc.app.domain.dto.ProjectsDto;
import com.mvc.app.domain.dto.SessionInfo;
import com.mvc.app.security.LoginMemberUtil;
import com.mvc.app.service.EmployeeService;
import com.mvc.app.service.ProjectService;
import com.mvc.app.service.TaskService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/projects/*")
public class ProjectController {
	private final ProjectService service;
	private final EmployeeService employeeService;
	private final TaskService taskService;
	private final PaginateUtil paginateUtil;
	private final MyUtil myUtil;

	@GetMapping("list")
	public String projectlist(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd, Model model) throws Exception {

		try {
			int size = 10;
			int total_page = 0;

			kwd = myUtil.decodeUrl(kwd);

			SessionInfo info = LoginMemberUtil.getSessionInfo();

			Map<String, Object> map = new HashMap<>();
			map.put("empId", info.getEmpId());
			map.put("schType", schType);
			map.put("kwd", kwd);

			int dataCount = service.dataCount(map);

			dataCount = service.dataCount(map);
			if (dataCount != 0) {
				total_page = dataCount / size + (dataCount % size > 0 ? 1 : 0);
			}
			current_page = Math.min(current_page, total_page);
			int offset = (current_page - 1) * size;
			if (offset < 0)
				offset = 0;
			map.put("offset", offset);
			map.put("size", size);

			List<ProjectsDto> list = service.projectslist(map);

			String cp = RequestUtils.getContextPath();
			String query = "";
			String listUrl = cp + "/projects/list";
			String articleUrl = cp + "/projects/article?page=" + current_page;

			if (!kwd.isBlank()) {
				query = "schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);

				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = paginateUtil.paging(current_page, total_page, listUrl);

			model.addAttribute("list", list);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);
			model.addAttribute("page", current_page);

			model.addAttribute("paging", paging);
			model.addAttribute("articleUrl", articleUrl);

			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);

		} catch (Exception e) {
			log.info("projectlist : ", e);
		}

		return "projects/list";
	}

	@GetMapping("create")
	public String projectCreate(Model model) {
		SessionInfo info = LoginMemberUtil.getSessionInfo();
		EmployeeDto emp = employeeService.findByEmpId(info.getEmpId());
		model.addAttribute("empId", emp.getEmpId());
		model.addAttribute("empName", emp.getName());
		model.addAttribute("empDept", emp.getDeptName());
		model.addAttribute("empGrade", emp.getGradeName());

		return "projects/create";
	}

	@PostMapping("create")
	@ResponseBody
	public ResponseEntity<?> createFullProject(@RequestBody ProjectsDto dto, HttpServletRequest req) throws Exception {
		try {
			SessionInfo info = LoginMemberUtil.getSessionInfo();
			dto.setEmpId(info.getEmpId());

			service.createFullProject(dto, dto.getMembers(), dto.getStages());
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.info("createFullProject : ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("article")
	public String projectarticle(@RequestParam(name = "projectId") long projectId, Model model) throws Exception {
		try {
			ProjectsDto dto = service.projectarticle(projectId);

			List<ProjectsDto> members = service.projectMembers(projectId);

			model.addAttribute("dto", dto);
			model.addAttribute("members", members);

		} catch (Exception e) {
			log.info("projectarticle : ", e);
		}
		return "projects/article";
	}

	@GetMapping("gantt")
	public String projectgantt(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd, Model model) throws Exception {

		try {
			int size = 10;
			int total_page = 0;

			kwd = myUtil.decodeUrl(kwd);

			SessionInfo info = LoginMemberUtil.getSessionInfo();

			Map<String, Object> map = new HashMap<>();
			map.put("empId", info.getEmpId());
			map.put("schType", schType);
			map.put("kwd", kwd);

			int dataCount = taskService.myDataCount(map);

			dataCount = taskService.myDataCount(map);
			if (dataCount != 0) {
				total_page = dataCount / size + (dataCount % size > 0 ? 1 : 0);
			}
			current_page = Math.min(current_page, total_page);
			int offset = (current_page - 1) * size;
			if (offset < 0)
				offset = 0;
			map.put("offset", offset);
			map.put("size", size);

			List<ProjectsDto> list = taskService.myProjectslist(map);

			String cp = RequestUtils.getContextPath();
			String query = "";
			String listUrl = cp + "/projects/gantt";
			String articleUrl = cp + "/projects/task?page=" + current_page;

			if (!kwd.isBlank()) {
				query = "schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);

				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = paginateUtil.paging(current_page, total_page, listUrl);

			model.addAttribute("list", list);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);
			model.addAttribute("page", current_page);

			model.addAttribute("paging", paging);
			model.addAttribute("articleUrl", articleUrl);

			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);

		} catch (Exception e) {
			log.info("projectgantt : ", e);
		}
		return "projects/gantt";
	}

	@GetMapping("task")
	public String projectask(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "projectId") long projectId,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd, Model model) {

		try {
			int size = 30;
			int total_page = 0;

			kwd = myUtil.decodeUrl(kwd);

			Map<String, Object> map = new HashMap<>();
			map.put("projectId", projectId);
			map.put("schType", schType);
			map.put("kwd", kwd);

			int taskDataCount = taskService.taskDataCount(map);
			if (taskDataCount != 0) {
				total_page = taskDataCount / size + (taskDataCount % size > 0 ? 1 : 0);
			}

			current_page = Math.min(current_page, total_page);

			int offset = (current_page - 1) * size;
			if (offset < 0)
				offset = 0;
			map.put("offset", offset);
			map.put("size", size);

			List<ProjectsDto> members = service.projectMembers(projectId);
			List<ProjectsDto> stages = taskService.findStagesByProjectId(projectId);

			SessionInfo info = LoginMemberUtil.getSessionInfo();
			String loginEmpId = info.getEmpId();

			boolean isManager = members.stream()
					.anyMatch(m -> m.getEmpId().equals(loginEmpId) && "M".equals(m.getRole()));

			model.addAttribute("isManager", isManager);
			model.addAttribute("loginEmpId", loginEmpId);

			String cp = RequestUtils.getContextPath();
			String query = "";
			String listUrl = cp + "/projects/task?projectId=" + projectId;
			String articleUrl = cp + "/projects/task?projectId=" + projectId + "&page=" + current_page;

			if (!kwd.isBlank()) {
				query = "schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);

				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = paginateUtil.paging(current_page, total_page, listUrl);

			List<ProjectsDto> list = taskService.tasklist(map);
			model.addAttribute("list", list);
			model.addAttribute("taskDataCount", taskDataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);
			model.addAttribute("page", current_page);

			model.addAttribute("paging", paging);
			model.addAttribute("articleUrl", articleUrl);

			model.addAttribute("projectId", projectId);
			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);
			model.addAttribute("stages", stages);
			model.addAttribute("members", members);

			ProjectsDto dto = service.projectarticle(projectId);
			String projectStart = dto.getStartDate() != null ? dto.getStartDate().replace("/", "-") : "";
			String projectEnd = dto.getEndDate() != null ? dto.getEndDate().replace("/", "-") : "";
			model.addAttribute("projectStart", projectStart);
			model.addAttribute("projectEnd", projectEnd);
			model.addAttribute("projectTitle", dto.getTitle());

		} catch (Exception e) {
			log.info("projecttask : ", e);
		}

		return "projects/task";
	}

	@PostMapping("task/insert")
	@ResponseBody
	public ResponseEntity<?> insertTask(@RequestBody ProjectsDto dto, HttpServletRequest req) throws Exception {
		try {
			SessionInfo info = LoginMemberUtil.getSessionInfo();
			dto.setTaskCreator(info.getEmpId());

			taskService.insertProjectTask(dto);
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.info("insertTask : ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("task/update")
	@ResponseBody
	public ResponseEntity<?> updateTask(@RequestBody List<ProjectsDto> list, HttpServletRequest req) throws Exception {
		try {

			for (ProjectsDto dto : list) {
				taskService.updateProjectTask(dto);
			}
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.info("updateTask : ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@PostMapping("task/dailyinsert")
	@ResponseBody
	public ResponseEntity<?> insertTaskDailylog(@RequestBody ProjectsDto dto) throws Exception {
		try {
			SessionInfo info = LoginMemberUtil.getSessionInfo();
			dto.setEmpId(info.getEmpId());

			taskService.insertTaskDailylog(dto);
			return ResponseEntity.ok().build();

		} catch (Exception e) {
			log.info("insertTaskDailylog : ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("task/dailylist")
	@ResponseBody
	public ResponseEntity<?> taskDailylist(@RequestParam("empTaskId") String empTaskId) {
		try {
			List<ProjectsDto> logs = taskService.taskDailylist(empTaskId);
			return ResponseEntity.ok(logs);

		} catch (Exception e) {
			log.info("getDailyLogs : ", e);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
	}

	@GetMapping("ganttarticle")
	public String projectganttarticle() {
		return "projects/ganttarticle";
	}

	@GetMapping("/myProjectList")
	public String myProjectList(@RequestParam(name = "page", defaultValue = "1") int current_page,
			@RequestParam(name = "schType", defaultValue = "all") String schType,
			@RequestParam(name = "kwd", defaultValue = "") String kwd, Model model) throws Exception {

		try {
			int size = 10;
			int total_page = 0;

			kwd = myUtil.decodeUrl(kwd);

			SessionInfo info = LoginMemberUtil.getSessionInfo();

			Map<String, Object> map = new HashMap<>();
			map.put("empId", info.getEmpId());
			map.put("schType", schType);
			map.put("kwd", kwd);

			// ✅ DB에서 총 프로젝트 수
			int dataCount = service.myProjectsCount(map);

			if (dataCount != 0) {
				total_page = dataCount / size + (dataCount % size > 0 ? 1 : 0);
			}

			current_page = Math.min(current_page, total_page);

			int offset = (current_page - 1) * size;
			if (offset < 0)
				offset = 0;

			map.put("offset", offset);
			map.put("size", size);

			// ✅ 프로젝트 리스트 조회
			List<ProjectsDto> list = service.myProjectsList(map);

			// ✅ 통계 계산
			long totalProjects = dataCount;
			long activeProjects = list.stream().filter(p -> "2".equals(p.getStatus())).count();
			long finishedProjects = list.stream().filter(p -> "4".equals(p.getStatus())).count();
			long delayedProjects = list.stream().filter(p -> "5".equals(p.getStatus())).count();

			String cp = RequestUtils.getContextPath();
			String query = "";
			String listUrl = cp + "/projects/myProjectList";
			String articleUrl = cp + "/projects/article?page=" + current_page;

			if (!kwd.isBlank()) {
				query = "schType=" + schType + "&kwd=" + myUtil.encodeUrl(kwd);
				listUrl += "?" + query;
				articleUrl += "&" + query;
			}

			String paging = paginateUtil.paging(current_page, total_page, listUrl);

			// ✅ 모델에 담기
			model.addAttribute("list", list);
			model.addAttribute("dataCount", dataCount);
			model.addAttribute("size", size);
			model.addAttribute("total_page", total_page);
			model.addAttribute("page", current_page);
			model.addAttribute("paging", paging);
			model.addAttribute("articleUrl", articleUrl);

			model.addAttribute("schType", schType);
			model.addAttribute("kwd", kwd);

			// ✅ 통계 모델 추가
			model.addAttribute("totalProjects", totalProjects);
			model.addAttribute("activeProjects", activeProjects);
			model.addAttribute("finishedProjects", finishedProjects);
			model.addAttribute("delayedProjects", delayedProjects);

		} catch (Exception e) {
			log.info("myProjectList : ", e);
		}

		return "projects/myProjectList";
	}

}
