package com.spring.app.employees.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value="/emp/*")
public class EmpController {
	
	
	@GetMapping("empList.action")
	   public String requiredLogin_empList(HttpServletRequest request, HttpServletResponse response) {
	      
	      
	      
	      
	      return "emp/empList.tiles2";
	      ///WEB-INF/views/tiles2/emp/empList.jsp
	   }
	
}
