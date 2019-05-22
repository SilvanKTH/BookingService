package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.demo.domain.TestPerson;
import com.example.demo.repository.TestPersonRepo;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping
public class TestController {
	
	@Autowired
	private TestPersonRepo repo;
	
	/*
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String forwardTest() {
		return "redirect:Test/test";
	}
	*/
	
	@RequestMapping(value="/test", method=RequestMethod.GET)
	public String GetTest() {
		return "Test/testForm";
	}
	
	@RequestMapping(value="/test", method=RequestMethod.POST)
	public String PostTest(HttpServletRequest request, Model model) {
		String nameInBrowser = request.getParameter("nameInForm");
		String lastNameInBrowser = request.getParameter("lastNameInForm");
		
		if (nameInBrowser == null || nameInBrowser == "") {
			nameInBrowser = "world";
		}
		
		if (lastNameInBrowser == null || lastNameInBrowser == "") {
			lastNameInBrowser = "world";
		}
		
		TestPerson person = new TestPerson(nameInBrowser, lastNameInBrowser);
		
		repo.save(person);
		
		model.addAttribute("nameInForm", nameInBrowser);
		model.addAttribute("lastNameInForm", lastNameInBrowser);
		return "Test/testReturn";
	}
	
	@RequestMapping(value="/simple", method=RequestMethod.GET)
	public String SimpleGetTest() {
		return "Test/testText";
	}
	
	
	@RequestMapping(value="/simple", method=RequestMethod.POST)
	public String SimplePostTest() {
		return "Test/testText";
	}
	
}
