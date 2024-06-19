package com.spring.app.board.service;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;
import com.spring.app.board.model.BoardDAO;
import com.spring.app.common.AES256;

// ==== #31. Service 선언 ====
// 트랜잭션 처리를 담당하는 곳, 업무를 처리하는 곳, 비지니스(Business)단
// @Component
@Service
public class BoardService_imple implements BoardService {

	// === #34. 의존객체 주입하기(DI: Dependency Injection) ===
	@Autowired  // Type에 따라 알아서 Bean 을 주입해준다.
	private BoardDAO dao;
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
		int n = dao.test_insert();
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


	//////////////////////////////////////////////////////
	
	// === #37. 메인 페이지 요청 === //
	// 시작페이지에서 캐러절을 보여주는것
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
		//  /WEB-INF/views/tiles1/main/index.jsp  페이지를 만들어야 한다.
		
		return mav;
	}


	// === #42. 로그인 처리하기 === //
	@Override
	public MemberVO getLoginMember(Map<String, String> paraMap) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		// === #48. aes 의존객체를 사용하여 로그인 되어진 사용자(loginuser)의 이메일 값을 복호화 하도록 한다. === 
		//          또한 암호변경 메시지와 휴면처리 유무 메시지를 띄우도록 업무처리를 하도록 한다.
		
		if(loginuser != null && loginuser.getPwdchangegap() >= 3) {
			// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
			loginuser.setRequirePwdChange(true); // 로그인시 암호를 변경해라는 alert 를 띄우도록 한다.
		}
		
		if(loginuser != null && loginuser.getLastlogingap() >= 12 && loginuser.getIdle() == 0) {
			// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
			loginuser.setIdle(1);
			
			// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 === // 
			dao.updateIdle(paraMap.get("userid"));
		}
		
		if(loginuser != null && loginuser.getIdle() == 0) {
			try {
				String email = aES256.decrypt(loginuser.getEmail());
				String mobile = aES256.decrypt(loginuser.getMobile()); 
				
				loginuser.setEmail(email);
				loginuser.setMobile(mobile);
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				e.printStackTrace();
			}
			
			dao.insert_tbl_loginhistory(paraMap);
		}
		
		return loginuser;
	}

	
	/////////////////////////////////////////////////////////////
	@Override
	public ModelAndView loginEnd(Map<String, String> paraMap, ModelAndView mav, HttpServletRequest request) {
		
		MemberVO loginuser = dao.getLoginMember(paraMap);
		
		if(loginuser != null && loginuser.getPwdchangegap() >= 3) {
			// 마지막으로 암호를 변경한 날짜가 현재시각으로 부터 3개월이 지났으면 
			loginuser.setRequirePwdChange(true); // 로그인시 암호를 변경해라는 alert 를 띄우도록 한다.
		}
		
		if(loginuser != null && loginuser.getLastlogingap() >= 12 && loginuser.getIdle() == 0) {
			// 마지막으로 로그인 한 날짜시간이 현재시각으로 부터 1년이 지났으면 휴면으로 지정
			loginuser.setIdle(1);
			
			// === tbl_member 테이블의 idle 컬럼의 값을 1로 변경하기 === // 
			dao.updateIdle(paraMap.get("userid"));
		}
		
		if(loginuser != null && loginuser.getIdle() == 0) {
			try {
				String email = aES256.decrypt(loginuser.getEmail());
				String mobile = aES256.decrypt(loginuser.getMobile()); 
				
				loginuser.setEmail(email);
				loginuser.setMobile(mobile);
			} catch (UnsupportedEncodingException | GeneralSecurityException e) {
				e.printStackTrace();
			}
			
			dao.insert_tbl_loginhistory(paraMap);
		}
		
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
	///////////////////////////////////////////////////////////// 
	
	@Override
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
	
	

	// === #55. 파일첨부가 없는 글쓰기 === 
	@Override
	public int add(BoardVO boardvo) {
		
		int n = dao.add(boardvo);
		return n;
	}


	// === #59. 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 === // 
	@Override
	public List<BoardVO> boardListNoSearch() {
		List<BoardVO> boardList = dao.boardListNoSearch();
		return boardList;
	}


	// === #63. 글 조회수 증가와 함께 글 1개를 조회를 해오는 것 === //
	// (먼저, 로그인을 한 상태에서 다른 사람의 글을 조회할 경우에는 글조회수 컬럼의 값을 1증가 해야 한다.)
	@Override
	public BoardVO getView(Map<String, String> paraMap) {
		
		BoardVO boardvo = dao.getView(paraMap); // 글 1개 조회하기
		
		String login_userid = paraMap.get("login_userid");
		// paraMap.get("login_userid") 은 로그인을 한 상태이라면 로그인한 사용자의 userid 이고,
		// 로그인을 하지 않은 상태이라면  paraMap.get("login_userid") 은 null 이다.
		
		if(login_userid != null &&
				boardvo != null &&
		   !login_userid.equals(boardvo.getFk_userid())	) {
			// 글조회수 증가는 로그인을 한 상태에서 다른 사람의 글을 읽을때만 증가하도록 한다.
			
		   int n = dao.increase_readCount(boardvo.getSeq()); // 글조회수 1증가 하기 
		   if(n==1) {
			   boardvo.setReadCount( String.valueOf(Integer.parseInt(boardvo.getReadCount())+1) );
		   }
		}
		
		return boardvo;
	}


	// === #70. 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것 === // 
	@Override
	public BoardVO getView_no_increase_readCount(Map<String, String> paraMap) {
		BoardVO boardvo = dao.getView(paraMap); // 글 1개 조회하기
		return boardvo;
	}


	// === #73. 1개글 수정하기 === //
	@Override
	public int edit(BoardVO boardvo) {
		int n = dao.edit(boardvo);
		return n;
	}

	// === #78. 1개글 삭제하기 === //
	@Override
	public int del(String seq) {
		int n = dao.del(seq);
		return n;
	}


	// === #85. 댓글쓰기(Transaction 처리) === // 
	// tbl_comment 테이블에 insert 된 다음에 
	// tbl_board 테이블에 commentCount 컬럼이 1증가(update) 하도록 요청한다.
	// 이어서 회원의 포인트를 50점을 증가하도록 한다.
	// 즉, 2개이상의 DML 처리를 해야하므로 Transaction 처리를 해야 한다. (여기서는 3개의 DML 처리가 일어남)
	// >>>>> 트랜잭션처리를 해야할 메소드에 @Transactional 어노테이션을 설정하면 된다. 
	// rollbackFor={Throwable.class} 은 롤백을 해야할 범위를 말하는데 Throwable.class 은 error 및 exception 을 포함한 최상위 루트이다. 즉, 해당 메소드 실행시 발생하는 모든 error 및 exception 에 대해서 롤백을 하겠다는 말이다.
	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED, rollbackFor= {Throwable.class})
	public int addComment(CommentVO commentvo) throws Throwable {
		
		int n1=0, n2=0, result=0;
		
		n1 = dao.addComment(commentvo); // 댓글쓰기(tbl_comment 테이블에 insert) 
		System.out.println("~~~~ 확인용 n1 : " + n1);
		
		if(n1 == 1) {
			n2 = dao.updateCommentCount(commentvo.getParentSeq()); // tbl_board 테이블에 commentCount 컬럼이 1증가(update) 
			System.out.println("~~~~ 확인용 n2 : " + n2);
		}
		
		if(n2 == 1) {
			Map<String, String> paraMap = new HashMap<>();
			paraMap.put("userid", commentvo.getFk_userid());
			paraMap.put("point", "50");
			
			result = dao.updateMemberPoint(paraMap); // tbl_member 테이블의 point 컬럼의 값을 50점을 증가(update)
			System.out.println("~~~~ 확인용 result : " + result);
		}
		
		return result;
	}


	// === #91. 원게시물에 딸린 댓글들을 조회해오기 === //
	@Override
	public List<CommentVO> getCommentList(String parentSeq) {
		List<CommentVO> commentList = dao.getCommentList(parentSeq);
		return commentList;
	}

	// === #96. 댓글 수정(Ajax 로 처리) === //
	@Override
	public int updateComment(Map<String, String> paraMap) {
		int n = dao.updateComment(paraMap);
		return n;
	}

	
	// === #101. 댓글 삭제(Ajax 로 처리) Transaction 처리 === //
	@Override
	@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.READ_COMMITTED, rollbackFor= {Throwable.class}) 
	public int deleteComment(Map<String, String> paraMap) {
		int n = dao.deleteComment(paraMap.get("seq"));
		
		int m = 0;
		if(n==1) {
			// 댓글삭제시 tbl_board 테이블에 commentCount 컬럼이 1감소(update)
			m = dao.updateCommentCount_decrease(paraMap.get("parentSeq"));
		//	System.out.println("~~~ 확인용 m : " + m);
			// ~~~ 확인용 m : 1
		}
		
		return n*m;
	}


	// === #106. CommonAop 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것  === //
	@Override
	public void pointPlus(Map<String, String> paraMap) {
		dao.pointPlus(paraMap);
	}


	


	


	


	
	

}
