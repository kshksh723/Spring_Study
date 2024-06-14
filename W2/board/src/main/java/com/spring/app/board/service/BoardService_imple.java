package com.spring.app.board.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.model.BoardDAO;
import com.spring.app.common.AES256;

//==== #31. Service 선언 ====
//트랜잭션 처리를 담당하는 곳, 업무(DB)를 처리하는 곳, 비지니스(Business)단 
//@Component
@Service //  BoardService_imple
public class BoardService_imple implements BoardService {
	// === #34. 의존객체 주입하기(DI: Dependency Injection) ===
	
	@Autowired // Type에 따라 알아서 Bean 을 주입해준다.
	private BoardDAO dao; // BoardDAO로 되어진 bean을 만들어야 함 
	// Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.model.BoardDAO_imple 의 bean 을  dao 에 주입시켜준다. 
    // 그러므로 dao 는 null 이 아니다.
	
	
	
	// === #45. 양방향 암호화 알고리즘인 AES256 를 사용하여 복호화 하기 위한 클래스 의존객체 주입하기(DI: Dependency Injection) ===
    @Autowired
    private AES256 aES256;
    // Type 에 따라 Spring 컨테이너가 알아서 bean 으로 등록된 com.spring.board.common.AES256 의 bean 을  aES256 에 주입시켜준다. 
    // 그러므로 aES256 는 null 이 아니다.
   // com.spring.app.common.AES256 의 bean 은 /webapp/WEB-INF/spring/appServlet/servlet-context.xml 파일에서 bean 으로 등록시켜주었음.
    
    
	@Override
	public int test_insert() {
		
		int n =	dao.test_insert();
		
		return n;
	}
	
	@Override
	public List<TestVO> test_select() {
		List<TestVO> testvoList = dao.test_select();
		return testvoList;
	}
	
	

	@Override
	public List<TestVO2> test_select_vo2() {
		List<TestVO2> testvoList = dao.test_select_vo2();
		return testvoList;
	}
	
	@Override
	public List<Map<String, String>> test_select_map() {
		List<Map<String, String>> mapList = dao.test_select_map();
		return mapList;
	}     
	
	
	@Override
	public int test_insert(TestVO tvo) {
		int n = dao.test_insert(tvo);
		return n;
	}
	
	@Override
	public int test_insert_vo2(TestVO2 tvo) {
		int n = dao.test_insert_vo2(tvo);
		return n;
	}

	@Override
	public int test_insert(Map<String, String> paraMap) {
		int n = dao.test_insert(paraMap);
		return n;
	}

	////////////////////
	// #37 메인페이지 요청
	// 시작페이지에서 캐러젤 보여주기 
	@Override
	public List<Map<String, String>> getImgfilenameList() {
		List<Map<String, String>> mapList = dao.getImgfilenameList();
		return mapList;
	}

	@Override
	public ModelAndView index(ModelAndView mav) {
		
		List<Map<String, String>> mapList = dao.getImgfilenameList();

		mav.addObject("mapList", mapList);
		mav.setViewName("main/index.tiles1");
		///WEB-INF/views/tiles1/main/index.jsp
		return mav;
		
	} // end of public ModelAndView index(ModelAndView mav)

	
	
	// #42. 로그인 처리하기 
	@Override
	public MemberVO getLoginMember(Map<String, String> paraMap) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
	      //          또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.
			if(loginuser != null && loginuser.getPwdchangegap() >= 3) {
					// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
					loginuser.setRequirePwdChange(true);// 로그인시 암호를 변경해라는 alert 를 띄우도록 한다.
			}
			
			if(loginuser != null && loginuser.getLastlogingap() >= 12 && loginuser.getIdle() == 1) {
				
				// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년( 12개월)이 지났으면 휴면으로 지정
				
				loginuser.setIdle(1);
				
				// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 === // 
			
		//	String userid =	paraMap.get("userid");
			
			
			
			dao.updateIdle(paraMap.get("userid"));
			}
			
			
			if(loginuser != null) {
				try {
					String email = aES256.decrypt(loginuser.getEmail());
					String mobile =	aES256.decrypt(loginuser.getMobile());
	
					loginuser.setEmail(email);
					loginuser.setMobile(mobile);
	
					} catch (UnsupportedEncodingException | GeneralSecurityException e) {
					e.printStackTrace();
				}
			}
				return loginuser;
	} // end of public MemberVO getLoginMember(Map<String, String> paraMap)

	// == #55. 파일 첨부가 없는 글쓰기
	@Override
	public int add(BoardVO boardvo) {
		
		int n = dao.add(boardvo);
		
		
		
		return n;
	} // end of public int add(BoardVO boardvo)


	
	
	



} // end of 
