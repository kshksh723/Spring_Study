package com.spring.app.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.service.BoardService;
import com.spring.app.common.Sha256;

/*
	사용자 웹브라우저 요청(View)  ==> DispatcherServlet ==> @Controller 클래스 <==>> Service단(핵심업무로직단, business logic단) <==>> Model단[Repository](DAO, DTO) <==>> myBatis <==>> DB(오라클)           
	(http://...  *.action)                                  |                                                                                                                              
	 ↑                                                View Resolver
	 |                                                      ↓
	 |                                                View단(.jsp 또는 Bean명)
	 -------------------------------------------------------| 
	
	사용자(클라이언트)가 웹브라우저에서 http://localhost:9090/board/test/test_insert.action 을 실행하면
	배치서술자인 web.xml 에 기술된 대로  org.springframework.web.servlet.DispatcherServlet 이 작동된다.
	DispatcherServlet 은 bean 으로 등록된 객체중 controller 빈을 찾아서  URL값이 "/test_insert.action" 으로
	매핑된 메소드를 실행시키게 된다.                                               
	Service(서비스)단 객체를 업무 로직단(비지니스 로직단)이라고 부른다.
	Service(서비스)단 객체가 하는 일은 Model단에서 작성된 데이터베이스 관련 여러 메소드들 중 관련있는것들만을 모아 모아서
	하나의 트랜잭션 처리 작업이 이루어지도록 만들어주는 객체이다.
	여기서 업무라는 것은 데이터베이스와 관련된 처리 업무를 말하는 것으로 Model 단에서 작성된 메소드를 말하는 것이다.
	이 서비스 객체는 @Controller 단에서 넘겨받은 어떤 값을 가지고 Model 단에서 작성된 여러 메소드를 호출하여 실행되어지도록 해주는 것이다.
	실행되어진 결과값을 @Controller 단으로 넘겨준다.
*/

// ==== #30. 컨트롤러 선언 ====
// @Component
/* XML에서 빈을 만드는 대신에 클래스명 앞에 @Component 어노테이션을 적어주면 해당 클래스는 bean으로 자동 등록된다. 
      그리고 bean의 이름(첫글자는 소문자)은 해당 클래스명이 된다. 
      즉, 여기서 bean의 이름은 boardController 이 된다. 
      여기서는 @Controller 를 사용하므로 @Component 기능이 이미 있으므로 @Component를 명기하지 않아도 BoardController 는 bean 으로 등록되어 스프링컨테이너가 자동적으로 관리해준다. 
*/
@Controller
public class BoardController {

	// === #35. 의존객체 주입하기(DI: Dependency Injection) ===
    // ※ 의존객체주입(DI : Dependency Injection) 
	//  ==> 스프링 프레임워크는 객체를 관리해주는 컨테이너를 제공해주고 있다.
	//      스프링 컨테이너는 bean으로 등록되어진 BoardController 클래스 객체가 사용되어질때, 
	//      BoardController 클래스의 인스턴스 객체변수(의존객체)인 BoardService service 에 
	//      자동적으로 bean 으로 등록되어 생성되어진 BoardService service 객체를  
	//      BoardController 클래스의 인스턴스 변수 객체로 사용되어지게끔 넣어주는 것을 의존객체주입(DI : Dependency Injection)이라고 부른다. 
	//      이것이 바로 IoC(Inversion of Control == 제어의 역전) 인 것이다.
	//      즉, 개발자가 인스턴스 변수 객체를 필요에 의해 생성해주던 것에서 탈피하여 스프링은 컨테이너에 객체를 담아 두고, 
	//      필요할 때에 컨테이너로부터 객체를 가져와 사용할 수 있도록 하고 있다. 
	//      스프링은 객체의 생성 및 생명주기를 관리할 수 있는 기능을 제공하고 있으므로, 더이상 개발자에 의해 객체를 생성 및 소멸하도록 하지 않고
	//      객체 생성 및 관리를 스프링 프레임워크가 가지고 있는 객체 관리기능을 사용하므로 Inversion of Control == 제어의 역전 이라고 부른다.  
	//      그래서 스프링 컨테이너를 IoC 컨테이너라고도 부른다.
	
	//  IOC(Inversion of Control) 란 ?
	//  ==> 스프링 프레임워크는 사용하고자 하는 객체를 빈형태로 이미 만들어 두고서 컨테이너(Container)에 넣어둔후
	//      필요한 객체사용시 컨테이너(Container)에서 꺼내어 사용하도록 되어있다.
	//      이와 같이 객체 생성 및 소멸에 대한 제어권을 개발자가 하는것이 아니라 스프링 Container 가 하게됨으로써 
	//      객체에 대한 제어역할이 개발자에게서 스프링 Container로 넘어가게 됨을 뜻하는 의미가 제어의 역전 
	//      즉, IOC(Inversion of Control) 이라고 부른다.
	
	
	//  === 느슨한 결합 ===
	//      스프링 컨테이너가 BoardController 클래스 객체에서 BoardService 클래스 객체를 사용할 수 있도록 
	//      만들어주는 것을 "느슨한 결합" 이라고 부른다.
	//      느스한 결합은 BoardController 객체가 메모리에서 삭제되더라도 BoardService service 객체는 메모리에서 동시에 삭제되는 것이 아니라 남아 있다.
	
	// ===> 단단한 결합(개발자가 인스턴스 변수 객체를 필요에 의해서 생성해주던 것)
	// private InterBoardService service = new BoardService(); 
	// ===> BoardController 객체가 메모리에서 삭제 되어지면  BoardService service 객체는 멤버변수(필드)이므로 메모리에서 자동적으로 삭제되어진다.
	
	@Autowired  // Type에 따라 알아서 Bean 을 주입해준다.
	private BoardService service; 
	
	
	// ==== **** spring 기초 시작  **** ===== //
	@RequestMapping(value = "/test/test_insert.action")  // http://localhost:9099/board/test/test_insert.action 
	public String test_insert(HttpServletRequest request) {
		
		int n = service.test_insert();
		
		String message = "";
		
		if(n==1) {
			message = "데이터 입력 성공!!";
		}
		else {
			message = "데이터 입력 실패!!";
		}
		
		request.setAttribute("message", message);
		request.setAttribute("n", n);
		
		return "test/test_insert";  
		//   /WEB-INF/views/test/test_insert.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select.action")  // http://localhost:9099/board/test/test_select.action 
	public String test_select(HttpServletRequest request) {
		
		List<TestVO> testvoList = service.test_select();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select";
		//   /WEB-INF/views/test/test_select.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select_vo2.action")  // http://localhost:9099/board/test/test_select.action 
	public String test_select_vo2(HttpServletRequest request) {
		
		List<TestVO2> testvoList = service.test_select_vo2();
		
		request.setAttribute("testvoList", testvoList);
		
		return "test/test_select_vo2";
		//   /WEB-INF/views/test/test_select_vo2.jsp 페이지를 만들어야 한다.
	}
	
	
	@RequestMapping(value = "/test/test_select_map.action")   
	public String test_select_map(HttpServletRequest request) {
		
		List<Map<String, String>> mapList = service.test_select_map();
		
		request.setAttribute("mapList", mapList);
		
		return "test/test_select_map";
		//   /WEB-INF/views/test/test_select_map.jsp 페이지를 만들어야 한다.
	}
	
	
 //	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.GET})  // 오로지 GET방식만 허락하는 것임.
 //	@RequestMapping(value = "/test/test_form1.action", method= {RequestMethod.POST}) // 오로지 POST방식만 허락하는 것임.
	@RequestMapping(value = "/test/test_form1.action") // GET방식 또는 POST방식 둘 모두 허락하는 것임.
	public String test_form1(HttpServletRequest request) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) { // GET 방식이라면
			return "test/test_form1"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form1.jsp 페이지를 만들어야 한다.
		}
		else { // POST 방식이라면
			String no = request.getParameter("no");
			String name = request.getParameter("name");
			
			TestVO tvo = new TestVO();
			tvo.setNo(no);
			tvo.setName(name);
			
			int n = service.test_insert(tvo);
			
			if(n==1) {
				return "redirect:/test/test_select.action";
				//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
			else {
				return "redirect:/test/test_form1.action";
				//  /test/test_form1.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
		}
	}
	
	
	@RequestMapping(value = "/test/test_form2.action") // GET방식 또는 POST방식 둘 모두 허락하는 것임.
	public String test_form2(HttpServletRequest request, TestVO tvo) {
		
		String method = request.getMethod();
		
		if("GET".equalsIgnoreCase(method)) { // GET 방식이라면
			return "test/test_form2"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form2.jsp 페이지를 만들어야 한다.
		}
		else { // POST 방식이라면
			
			int n = service.test_insert(tvo);
			
			if(n==1) {
				return "redirect:/test/test_select.action";
				//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
			else {
				return "redirect:/test/test_form2.action";
				//  /test/test_form2.action 페이지로 redirect(페이지이동)해라는 말이다.
			}
		}
	}
	
	
	@RequestMapping(value = "/test/test_form3.action", method= {RequestMethod.GET}) // GET방식 만 허락하는 것임.
	public String test_form3() {
		
		return "test/test_form3"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form3.jsp 페이지를 만들어야 한다.
	}
		
	@RequestMapping(value = "/test/test_form3.action", method= {RequestMethod.POST}) // POST방식 만 허락하는 것임.
	public String test_form3(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select.action";
			//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form3.action";
			//  /test/test_form3.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}
	
	
	@GetMapping("/test/test_form4.action") // GET방식 만 허락하는 것임.
	public String test_form4() {
		
		return "test/test_form4"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form4.jsp 페이지를 만들어야 한다.
	}
		
	@PostMapping("/test/test_form4.action") // POST방식 만 허락하는 것임.
	public String test_form4(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select.action";
			//  /test/test_select.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form4.action";
			//  /test/test_form4.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}	
	
	
	@GetMapping("/test/test_form4_vo2.action") 
	public String test_form4_vo2() {
		
		return "test/test_form4_vo2"; 
	}
		
	@PostMapping("/test/test_form4_vo2.action") 
	public String test_form4_vo2(TestVO2 tvo) {
		
		int n = service.test_insert_vo2(tvo);
		
		if(n==1) {
			return "redirect:/test/test_select_vo2.action";
			
		}
		else {
			return "redirect:/test/test_form4_vo2.action";
		}
	}
	
	
	@GetMapping("/test/test_form5.action") // GET방식 만 허락하는 것임.
	public String test_form5() {
		
		return "test/test_form5"; // view 단 페이지를 띄워라
			//  /WEB-INF/views/test/test_form5.jsp 페이지를 만들어야 한다.
	}
		
	@PostMapping("/test/test_form5.action") // POST방식 만 허락하는 것임.
	public String test_form5(HttpServletRequest request) {
		
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("no", no);
		paraMap.put("name", name);
		
		int n = service.test_insert(paraMap);
		
		if(n==1) {
			return "redirect:/test/test_select_map.action";
			//  /test/test_select_map.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			return "redirect:/test/test_form5.action";
			//  /test/test_form5.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
	}
	
	
	// ======= Ajax  연습시작  ======= //
	@GetMapping("/test/test_form6.action") 
	public String test_form6() {
		
		return "test/test_form6"; 
	}
	
	
  /*
    @ResponseBody 란?
	  메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
	 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.
  */ 
	@ResponseBody
	@PostMapping("/test/ajax_insert.action")
	public String ajax_insert(TestVO tvo) {
		
		int n = service.test_insert(tvo);
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("n", n); // {"n":1}
		
		return jsonObj.toString();  // "{"n":1}" 
	}
	
	
	/*
    @ResponseBody 란?
	  메소드에 @ResponseBody Annotation이 되어 있으면 return 되는 값은 View 단 페이지를 통해서 출력되는 것이 아니라 
	 return 되어지는 값 그 자체를 웹브라우저에 바로 직접 쓰여지게 하는 것이다. 일반적으로 JSON 값을 Return 할때 많이 사용된다.
	 
    >>> 스프링에서 json 또는 gson을 사용한 ajax 구현시 데이터를 화면에 출력해 줄때 한글로 된 데이터가 '?'로 출력되어 한글이 깨지는 현상이 있다. 
               이것을 해결하는 방법은 @RequestMapping 어노테이션의 속성 중 produces="text/plain;charset=UTF-8" 를 사용하면 
               응답 페이지에 대한 UTF-8 인코딩이 가능하여 한글 깨짐을 방지 할 수 있다. <<< 
    */	
	@ResponseBody
	@GetMapping(value="/test/ajax_select.action", produces="text/plain;charset=UTF-8")
	public String ajax_select() {
		
		List<TestVO> testvoList = service.test_select();
		
		JSONArray jsonArr = new JSONArray(); //  [] 
		
		if(testvoList != null) {
			for(TestVO vo : testvoList) {
				JSONObject jsonObj = new JSONObject();     // {} 
				jsonObj.put("no", vo.getNo());             // {"no":"101"} 
				jsonObj.put("name", vo.getName());         // {"no":"101", "name":"이순신"}
				jsonObj.put("writeday", vo.getWriteday()); // {"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}
				
				jsonArr.put(jsonObj); // [{"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}]
			}// end of for------------------------
		}
		
		return jsonArr.toString(); // "[{"no":"101", "name":"이순신", "writeday":"2024-06-11 17:27:09"}]" 
		                           // 또는 "[]"
	}
	// ======= Ajax  연습끝  ======= //
	
	
	// === return 타입을  String 대신에 ModelAndView 를 사용해 보겠습니다. 시작  === //
	@GetMapping(value = "/test/modelandview_select.action")   
	public ModelAndView modelandview_select(ModelAndView mav) {
		
		List<TestVO> testvoList = service.test_select();
		
		mav.addObject("testvoList", testvoList);
	//  위의 것은	
	//	request.setAttribute("testvoList", testvoList); 와 같은 것이다. 
		
		mav.setViewName("test/modelandview_select");
	//  view 단 페이지의 파일명 지정하기
	//	/WEB-INF/views/test/modelandview_select.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	// === return 타입을  String 대신에 ModelAndView 를 사용해 보겠습니다. 끝  === //
	
	
	// *******   tiles 연습 시작     ******* //
	@GetMapping("/test/tiles_test1.action")
	public String tiles_test1() {
		
		return "tiles_test1.tiles1";
	    //	/WEB-INF/views/tiles1/tiles_test1.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test2.action")
	public String tiles_test2() {
		
		return "test/tiles_test2.tiles1";
	    //	/WEB-INF/views/tiles1/test/tiles_test2.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test3.action")
	public String tiles_test3() {
		
		return "test/sample/tiles_test3.tiles1";
	    // /WEB-INF/views/tiles1/test/sample/tiles_test3.jsp 페이지를 만들어야 한다.
		
	}
	
	
	@GetMapping("/test/tiles_test4.action")
	public ModelAndView tiles_test4(ModelAndView mav) {
		
		mav.setViewName("tiles_test4.tiles2");
		//  /WEB-INF/views/tiles2/tiles_test4.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	
	@GetMapping("/test/tiles_test5.action")
	public ModelAndView tiles_test5(ModelAndView mav) {
		
		mav.setViewName("test/tiles_test5.tiles2");
		//  /WEB-INF/views/tiles2/test/tiles_test5.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	@GetMapping("/test/tiles_test6.action")
	public ModelAndView tiles_test6(ModelAndView mav) {
		
		mav.setViewName("test/sample/tiles_test6.tiles2");
		//  /WEB-INF/views/tiles2/test/sample/tiles_test6.jsp 페이지를 만들어야 한다.
		
		return mav;
	    
	}
	
	
	@GetMapping("/test/tiles_test7.action")
	public ModelAndView tiles_test7(ModelAndView mav) {
		
		mav.setViewName("test7/tiles_test7.tiles3");
		//  /WEB-INF/views/tiles3/test7/side.jsp 페이지를 만들어야 한다.
		//  /WEB-INF/views/tiles3/test7/content/tiles_test7.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@GetMapping("/test/tiles_test8.action")
	public ModelAndView tiles_test8(ModelAndView mav) {
		
		mav.setViewName("test8/tiles_test8.tiles3");
		//  /WEB-INF/views/tiles3/test8/side.jsp 페이지를 만들어야 한다.
		//  /WEB-INF/views/tiles3/test8/content/tiles_test8.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	@GetMapping("/test/tiles_test9.action")
	public ModelAndView tiles_test9(ModelAndView mav) {
		
		mav.setViewName("test9/tiles_test9.tiles4");
		// /WEB-INF/views/tiles4/test9/content/tiles_test9.jsp 페이지를 만들어야 한다.
		
		return mav;
	}
	// *******   tiles 연습 끝     ******* //
	
	// ==== **** spring 기초 끝  **** ===== //
	
	/////////////////////////////////////////////////////
	
	
	
	
	// ======= ****** 게시판 시작 ****** ======== //
	
	// === #36. 메인 페이지 요청 === //
	// 먼저, com.spring.app.HomeController 클래스에 가서 @Controller 을 주석처리한다.
	@GetMapping("/")
	public ModelAndView home(ModelAndView mav) {
	    mav.setViewName("redirect:/index.action");
		return mav;
	}
	
/*	
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {
		
		List<Map<String, String>> mapList = service.getImgfilenameList();
		
		mav.addObject("mapList", mapList);
		mav.setViewName("main/index.tiles1");
		//  /WEB-INF/views/tiles1/main/index.jsp  페이지를 만들어야 한다.
		
		return mav;
	}
*/	
	// === 또는 === //
	@GetMapping("/index.action")
	public ModelAndView index(ModelAndView mav) {
		mav = service.index(mav);
		return mav;
	}
	
	
	// === #40. 로그인 폼 페이지 요청 === //
	@GetMapping("/login.action")
	public ModelAndView login(ModelAndView mav) {
		mav.setViewName("login/loginform.tiles1");
		//  /WEB-INF/views/tiles1/login/loginform.jsp  페이지를 만들어야 한다.
		return mav;
	}
	
	
	// === #41. 로그인 처리하기 === //
 /*	
	@PostMapping("/loginEnd.action")
	public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request) {
		
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// === 클라이언트의 IP 주소를 알아오는 것 === //
		String clientip = request.getRemoteAddr();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));
		paraMap.put("clientip", clientip);
		
		MemberVO loginuser = service.getLoginMember(paraMap); 
		
		if(loginuser == null) { // 로그인 실패시
			String message = "아이디 또는 암호가 틀립니다.";
			String loc = "javascript:history.back()";
			
			mav.addObject("message", message);
			mav.addObject("loc", loc);
			
			mav.setViewName("msg");
			//  /WEB-INF/views/msg.jsp 파일을 생성한다.
		}
		else { // 아이디와 암호가 존재하는 경우 
			
			if(loginuser.getIdle() == 1) { // 로그인 한지 1년이 경과한 경우
				
				String message = "로그인을 한지 1년이 지나서 휴면상태로 되었습니다.\\n관리자에게 문의 바랍니다.";
				String loc = request.getContextPath()+"/index.action";
				// 원래는 위와 같이 index.action 이 아니라 휴면의 계정을 풀어주는 페이지로 잡아주어야 한다. 
				
				mav.addObject("message", message);
				mav.addObject("loc", loc);
				
				mav.setViewName("msg");
			}
			else{ // 로그인 한지 1년 이내인  경우
			   
				HttpSession session = request.getSession();
				// 메모리에 생성되어져 있는 session 을 불러온다.
				
				session.setAttribute("loginuser", loginuser); 
				// session(세션)에 로그인 되어진 사용자 정보인 loginuser 을 키이름을 "loginuser" 으로 저장시켜두는 것이다. 
				
				if(loginuser.isRequirePwdChange() == true) { // 암호를 마지막으로 변경한 것이 3개월이 경과한 경우
					
					String message = "비밀번호를 변경하신지 3개월이 지났습니다.\\n암호를 변경하시는 것을 추천합니다.";
					String loc = request.getContextPath()+"/index.action";
					// 원래는 위와 같이 index.action 이 아니라 사용자의 비밀번호를 변경해주는 페이지로 잡아주어야 한다.
					
					mav.addObject("message", message);
					mav.addObject("loc", loc);
					
					mav.setViewName("msg");
					
				}
				else { // 암호를 마지막으로 변경한 것이 3개월 이내인 경우
					
					// 로그인을 해야만 접근할 수 있는 페이지에 로그인을 하지 않은 상태에서 접근을 시도한 경우 
					// "먼저 로그인을 하세요!!" 라는 메시지를 받고서 사용자가 로그인을 성공했다라면
					// 화면에 보여주는 페이지는 시작페이지로 가는 것이 아니라
					// 조금전 사용자가 시도하였던 로그인을 해야만 접근할 수 있는 페이지로 가기 위한 것이다.
					String goBackURL = (String) session.getAttribute("goBackURL"); 
					
					if(goBackURL != null) {
						mav.setViewName("redirect:"+goBackURL);
						session.removeAttribute("goBackURL"); // 세션에서 반드시 제거해주어야 한다.
					}
					else {
						mav.setViewName("redirect:/index.action"); // 시작페이지로 이동
					}
				}
			}
		}
		
		return mav;
	}
 */	
	// ==== 또는 ==== //
	@PostMapping("/loginEnd.action")
	public ModelAndView loginEnd(ModelAndView mav, HttpServletRequest request) {
		
		String userid = request.getParameter("userid");
		String pwd = request.getParameter("pwd");
		
		// === 클라이언트의 IP 주소를 알아오는 것 === //
		String clientip = request.getRemoteAddr();
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("userid", userid);
		paraMap.put("pwd", Sha256.encrypt(pwd));
		paraMap.put("clientip", clientip);
		
		mav = service.loginEnd(paraMap, mav, request); // 사용자가 입력한 값들을 Map 에 담아서 서비스 객체에게 넘겨 처리하도록 한다. 
			
		return mav;
	}
	
	
	// === #50. 로그아웃 처리하기 === //
/*	
	@GetMapping("/logout.action")
	public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		session.invalidate();
		
		String message = "로그아웃 되었습니다.";
		String loc = request.getContextPath()+"/index.action";
		
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
*/	
	// === 또는 === //
	@GetMapping("/logout.action")
	public ModelAndView logout(ModelAndView mav, HttpServletRequest request) {
		
		mav = service.logout(mav, request);
		return mav;
	}
	
	// === #51. 게시판 글쓰기 폼페이지 요청 === //
	@GetMapping("/add.action")
	public ModelAndView requiredLogin_add(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		mav.setViewName("board/add.tiles1");
		//  /WEB-INF/views/tiles1/board/add.jsp  페이지를 만들어야 한다.
		
		return mav;
	}
	
	
	// === #54. 게시판 글쓰기 완료 요청 === //
	@PostMapping("/addEnd.action")
 //	public ModelAndView addEnd(ModelAndView mav, BoardVO boardvo) { // <== After Advice 를 사용하기 전
	public ModelAndView pointPlus_addEnd(Map<String, String> paraMap, ModelAndView mav, BoardVO boardvo) { // <== After Advice 를 사용하기
	/*
	    form 태그의 name 명과  BoardVO 의 필드명이 같다라면 
	    request.getParameter("form 태그의 name명"); 을 사용하지 않더라도
	        자동적으로 BoardVO boardvo 에 set 되어진다.
	*/	
		
		int n = service.add(boardvo); // <== 파일첨부가 없는 글쓰기 
		
		if(n==1) {
			mav.setViewName("redirect:/list.action");
		    //  /list.action 페이지로 redirect(페이지이동)해라는 말이다.
		}
		else {
			mav.setViewName("board/error/add_error.tiles1");
			//  /WEB-INF/views/tiles1/board/error/add_error.jsp 파일을 생성한다.
		}
		
		// ===== #104. After Advice 를 사용하기 ====== //
		//             글쓰기를 한 이후에는 회원의 포인트를 100점 증가 
		paraMap.put("userid", boardvo.getFk_userid());
		paraMap.put("point", "100");
		
		return mav;
	}
	
	
	// === #58. 글목록 보기 페이지 요청 === //
	@GetMapping("/list.action")
	public ModelAndView list(ModelAndView mav, HttpServletRequest request) {
		
		List<BoardVO> boardList = null;
		
		//////////////////////////////////////////////////////
		// === #69. 글조회수(readCount)증가 (DML문 update)는
		//          반드시 목록보기에 와서 해당 글제목을 클릭했을 경우에만 증가되고,
		//          웹브라우저에서 새로고침(F5)을 했을 경우에는 증가가 되지 않도록 해야 한다.
		//          이것을 하기 위해서는 session 을 사용하여 처리하면 된다.
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		/*
			session 에  "readCountPermission" 키값으로 저장된 value값은 "yes" 이다.
			session 에  "readCountPermission" 키값에 해당하는 value값 "yes"를 얻으려면 
			반드시 웹브라우저에서 주소창에 "/list.action" 이라고 입력해야만 얻어올 수 있다. 
		*/
	    //////////////////////////////////////////////////////
		
		
		// === 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 === //
		boardList = service.boardListNoSearch();
		
		mav.addObject("boardList", boardList);
		
		mav.setViewName("board/list.tiles1");
		//  /WEB-INF/views/tiles1/board/list.jsp 파일을 생성한다.
		
		return mav;
	}
	
	
	
	// === #62. 글1개를 보여주는 페이지 요청 === //
	@GetMapping("/view.action")
	public ModelAndView view(ModelAndView mav, HttpServletRequest request) {
		
		String seq = "";
		
		Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
		// redirect 되어서 넘어온 데이터가 있는지 꺼내어 와본다.
		
		if(inputFlashMap != null) { // redirect 되어서 넘어온 데이터가 있다 라면 
			
			@SuppressWarnings("unchecked") // 경고 표시를 하지 말라는 뜻이다.
			Map<String, String> redirect_map = (Map<String, String>) inputFlashMap.get("redirect_map");
			// "redirect_map" 값은  /view_2.action 에서  redirectAttr.addFlashAttribute("키", 밸류값); 을 할때 준 "키" 이다. 
			// "키" 값을 주어서 redirect 되어서 넘어온 데이터를 꺼내어 온다. 
			// "키" 값을 주어서 redirect 되어서 넘어온 데이터의 값은 Map<String, String> 이므로 Map<String, String> 으로 casting 해준다.

		  /*	
			System.out.println("~~~ 확인용 seq : " + redirect_map.get("seq"));  
		  */
			
			seq = redirect_map.get("seq");
			
		}
		
		////////////////////////////////////////////////////////////
		
		else { // redirect 되어서 넘어온 데이터가 아닌 경우  
			
			// == 조회하고자 하는 글번호 받아오기 ==
        	
        	// 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회해온 경우  
        	// 또는 
        	// 글목록보기인 /list.action 페이지에서 특정 글제목을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 form 을 사용해서 POST 방식으로 넘어온 경우이므로 "양식 다시 제출 확인" 이라는 대화상자가 뜨게 되며 "계속" 이라는 버튼을 클릭하면 이전에 입력했던 데이터를 자동적으로 입력해서 POST 방식으로 진행된다. 그래서  request.getParameter("seq"); 은 null 이 아닌 번호를 입력받아온다.     
			// 그런데 "이전글제목" 또는 "다음글제목" 을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는 원본이 /view_2.action 을 통해서 redirect 되어진 경우이므로 form 을 사용한 것이 아니라서 "양식 다시 제출 확인" 이라는 alert 대화상자가 뜨지 않는다. 그래서  request.getParameter("seq"); 은 null 이 된다. 
			seq = request.getParameter("seq");
		 // System.out.println("~~~~~~ 확인용 seq : " + seq);
        	// ~~~~~~ 확인용 seq : 213
        	// ~~~~~~ 확인용 seq : null
		}
		
		try {
			Integer.parseInt(seq);
		 /* 
		     "이전글제목" 또는 "다음글제목" 을 클릭하여 특정글을 조회한 후 새로고침(F5)을 한 경우는   
		         원본이 /view_2.action 을 통해서 redirect 되어진 경우이므로 form 을 사용한 것이 아니라서   
		     "양식 다시 제출 확인" 이라는 alert 대화상자가 뜨지 않는다. 
		         그래서  request.getParameter("seq"); 은 null 이 된다. 
		         즉, 글번호인 seq 가 null 이 되므로 DB 에서 데이터를 조회할 수 없게 된다.     
		         또한 seq 는 null 이므로 Integer.parseInt(seq); 을 하면  NumberFormatException 이 발생하게 된다. 
		  */
			
			HttpSession session = request.getSession();
			MemberVO loginuser = (MemberVO) session.getAttribute("loginuser");
			
			String login_userid = null;
			if(loginuser != null) {
				login_userid = loginuser.getUserid();
				// login_userid 는 로그인 되어진 사용자의 userid 이다.
			}
			
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			paraMap.put("login_userid", login_userid);
			
			// === #68. !!! 중요 !!! 
	        //     글1개를 보여주는 페이지 요청은 select 와 함께 
			//     DML문(지금은 글조회수 증가인 update문)이 포함되어져 있다.
			//     이럴경우 웹브라우저에서 페이지 새로고침(F5)을 했을때 DML문이 실행되어
			//     매번 글조회수 증가가 발생한다.
			//     그래서 우리는 웹브라우저에서 페이지 새로고침(F5)을 했을때는
			//     단순히 select만 해주고 DML문(지금은 글조회수 증가인 update문)은 
			//     실행하지 않도록 해주어야 한다. !!! === //
			
			// 위의 글목록보기 #69. 에서 session.setAttribute("readCountPermission", "yes"); 해두었다.
			BoardVO boardvo = null;
			
			if("yes".equals( (String)session.getAttribute("readCountPermission") )) {
			// 글목록보기인 /list.action 페이지를 클릭한 다음에 특정글을 조회해온 경우이다.
				
				boardvo = service.getView(paraMap);
				// 글 조회수 증가와 함께 글 1개를 조회를 해오는 것
			//	System.out.println("~~ 확인용 글내용 : " + boardvo.getContent());
				
				session.removeAttribute("readCountPermission");
				// 중요함!! session 에 저장된 readCountPermission 을 삭제한다. 
			}
			
			else {
				// 글목록에서 특정 글제목을 클릭하여 본 상태에서
			    // 웹브라우저에서 새로고침(F5)을 클릭한 경우이다.
			//	System.out.println("글목록에서 특정 글제목을 클릭하여 본 상태에서 웹브라우저에서 새로고침(F5)을 클릭한 경우");
				
				boardvo = service.getView_no_increase_readCount(paraMap);
				// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
				
			 // 또는 redirect 해주기 (예 : 버거킹 www.burgerking.co.kr 메뉴소개)
			 /*	
				mav.setViewName("redirect:/list.action");
				return mav;
			 */	
				
				if(boardvo == null) {
					mav.setViewName("redirect:/list.action");
					return mav;
				}
			}
			
			mav.addObject("boardvo", boardvo);
			mav.setViewName("board/view.tiles1");
			//  /WEB-INF/views/tiles1/board/view.jsp 파일을 생성한다.
			
		} catch (NumberFormatException e) {
			mav.setViewName("redirect:/list.action");
		}
		
		return mav;
	}
	
	
	@GetMapping("/view_2.action")
	public ModelAndView view_2(ModelAndView mav, HttpServletRequest request, RedirectAttributes redirectAttr) {
		
		// 조회하고자 하는 글번호 받아오기
		String seq = request.getParameter("seq");
		
		HttpSession session = request.getSession();
		session.setAttribute("readCountPermission", "yes");
		
		// ==== redirect(GET방식임) 시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 시작 ==== //
		/////////////////////////////////////////////////////////////////////////////////
		Map<String, String> redirect_map = new HashMap<>();
		redirect_map.put("seq", seq);
		
		redirectAttr.addFlashAttribute("redirect_map", redirect_map);
		// redirectAttr.addFlashAttribute("키", 밸류값); 으로 사용하는데 오로지 1개의 데이터만 담을 수 있으므로 여러개의 데이터를 담으려면 Map 을 사용해야 한다. 
		
		mav.setViewName("redirect:/view.action"); // 실제로 redirect:/view.action 은 POST 방식이 아닌 GET 방식이다.
        /////////////////////////////////////////////////////////////////////////////////
		// ==== redirect(GET방식임) 시 데이터를 넘길때 GET 방식이 아닌 POST 방식처럼 데이터를 넘기려면 RedirectAttributes 를 사용하면 된다. 끝 ==== //
		
		return mav;
	}
	
	
	// === #71. 글을 수정하는 페이지 요청 === //
	@GetMapping("/edit.action")
	public ModelAndView requiredLogin_edit(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 수정해야 할 글번호 가져오기
		String seq = request.getParameter("seq");
		
		String message = "";
		
		try {
			Integer.parseInt(seq);
			
			// 글 수정해야 할 글 1개 내용가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
			
			if(boardvo == null) {
				message = "글 수정이 불가합니다.";
			}
			else {
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO) session.getAttribute("loginuser"); 
				
				if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
					message = "다른 사용자의 글은 수정이 불가합니다.";
				}
				else {
					// 자신의 글을 수정할 경우
					// 가져온 1개글을 글수정할 폼이 있는 view 단으로 보내준다.
					mav.addObject("boardvo", boardvo);
					mav.setViewName("board/edit.tiles1");
					
					return mav;
				}
			}
			
		} catch (NumberFormatException e) {
			message = "글 수정이 불가합니다.";
		}
		
		String loc = "javascript:history.back()";
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	// === #72. 글을 수정하는 페이지 완료하기 === //
	@PostMapping("/editEnd.action")
	public ModelAndView editEnd(ModelAndView mav, BoardVO boardvo, HttpServletRequest request) {
		
		int n = service.edit(boardvo);
		
		if(n==1) {
			mav.addObject("message", "글 수정 성공!!");
			mav.addObject("loc", request.getContextPath()+"/view.action?seq="+boardvo.getSeq());
			mav.setViewName("msg");
		}
		
		return mav;
	}
	
	
	
	// === #76. 글을 삭제하는 페이지 요청 === //
	@GetMapping("/del.action")
	public ModelAndView requiredLogin_del(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		
		// 글 삭제해야 할 글번호 가져오기
		String seq = request.getParameter("seq");
		
		String message = "";
		
		try {
			Integer.parseInt(seq);
			
			// 글 삭제해야 할 글 1개 내용가져오기
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("seq", seq);
			
			BoardVO boardvo = service.getView_no_increase_readCount(paraMap);
			// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
			
			if(boardvo == null) {
				message = "글 삭제가 불가합니다.";
			}
			else {
				HttpSession session = request.getSession();
				MemberVO loginuser = (MemberVO) session.getAttribute("loginuser"); 
				
				if( !loginuser.getUserid().equals(boardvo.getFk_userid()) ) {
					message = "다른 사용자의 글은 삭제가 불가합니다.";
				}
				else {
					// 자신의 글을 삭제할 경우
					// 가져온 1개글을 글삭제할 폼이 있는 view 단으로 보내준다.
					mav.addObject("boardvo", boardvo);
					mav.setViewName("board/del.tiles1");
					
					return mav;
				}
			}
			
		} catch (NumberFormatException e) {
			message = "글 삭제가 불가합니다.";
		}
		
		String loc = "javascript:history.back()";
		mav.addObject("message", message);
		mav.addObject("loc", loc);
		
		mav.setViewName("msg");
		
		return mav;
	}
	
	
	// === #77. 글을 수정하는 페이지 완료하기 === //
	@PostMapping("/delEnd.action")
	public ModelAndView delEnd(ModelAndView mav, HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		
		int n = service.del(seq);
		
		if(n==1) {
			mav.addObject("message", "글 삭제 성공!!");
			mav.addObject("loc", request.getContextPath()+"/list.action");
			mav.setViewName("msg");
		}
		
		return mav;
	}
	
	
	// === #84. 댓글쓰기(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/addComment.action", produces="text/plain;charset=UTF-8") 
	public String addComment(CommentVO commentvo) {
		// 댓글쓰기에 첨부파일이 없는 경우 
		
		int n = 0;
		
		try {
			n = service.addComment(commentvo);
			// 댓글쓰기(insert) 및 원게시물(tbl_board 테이블)에 댓글의 개수 증가(update 1씩 증가)하기 
			// 이어서 회원의 포인트를 50점을 증가하도록 한다. (tbl_member 테이블에 point 컬럼의 값을 50 증가하도록 update 한다.) 
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		JSONObject jsonObj = new JSONObject(); // {}
		jsonObj.put("n", n); // {"n":1} 또는 {"n":0}
		jsonObj.put("name", commentvo.getName()); // {"n":1, "name":"엄정화"} 또는 {"n":0, "name":"서영학"}
		
		return jsonObj.toString(); 
		// "{"n":1, "name":"엄정화"}" 또는 "{"n":0, "name":"서영학"}" 
	}
	
	
	// === #90. 원게시물에 딸린 댓글들을 조회해오기(Ajax 로 처리) === //
	@ResponseBody
	@GetMapping(value="/readComment.action", produces="text/plain;charset=UTF-8") 
	public String readComment(HttpServletRequest request) {
		
		String parentSeq = request.getParameter("parentSeq"); 
		
		List<CommentVO> commentList = service.getCommentList(parentSeq); 
		
		JSONArray jsonArr = new JSONArray(); // [] 
		
		if(commentList != null) {
			for(CommentVO cmtvo : commentList) {
				JSONObject jsonObj = new JSONObject();          // {} 
				jsonObj.put("seq", cmtvo.getSeq());             // {"seq":1}
				jsonObj.put("fk_userid", cmtvo.getFk_userid()); // {"seq":1, "fk_userid":"seoyh"}
				jsonObj.put("name", cmtvo.getName());           // {"seq":1, "fk_userid":"seoyh","name":"서영학"}
				jsonObj.put("content", cmtvo.getContent());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ"}
				jsonObj.put("regdate", cmtvo.getRegDate());     // {"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}
				
				jsonArr.put(jsonObj);
			}// end of for-----------------------
		}
		
		return jsonArr.toString(); // "[{"seq":1, "fk_userid":"seoyh","name":서영학,"content":"첫번째 댓글입니다. ㅎㅎㅎ","regdate":"2024-06-18 15:36:31"}]"
		                           // 또는
		                           // "[]"
	}
	
	
	// === #95. 댓글 수정(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/updateComment.action", produces="text/plain;charset=UTF-8")
	public String updateComment(HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		String content = request.getParameter("content");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("content", content);
		
		int n = service.updateComment(paraMap);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString(); // "{"n":1}"
	}
	
	
	// === #100. 댓글 삭제(Ajax 로 처리) === //
	@ResponseBody
	@PostMapping(value="/deleteComment.action", produces="text/plain;charset=UTF-8") 
	public String deleteComment(HttpServletRequest request) {
		
		String seq = request.getParameter("seq");
		String parentSeq = request.getParameter("parentSeq");
		
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("seq", seq);
		paraMap.put("parentSeq", parentSeq);
		
		int n=0;
		try {
			n = service.deleteComment(paraMap);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("n", n);
		
		return jsonObj.toString(); // "{"n":1}"
	}
	
	
	
}





