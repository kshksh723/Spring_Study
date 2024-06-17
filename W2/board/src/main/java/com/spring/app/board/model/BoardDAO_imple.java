package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

//import javax.annotation.Resource;
//import javax.inject.Inject;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;




//==== #32. Repository(DAO) 선언 ====
//@Component
@Repository
public class BoardDAO_imple implements BoardDAO {


	// === #33. 의존객체 주입하기(DI: Dependency Injection) ===
	// >>> 의존 객체 자동 주입(Automatic Dependency Injection)은
	//     스프링 컨테이너가 자동적으로 의존 대상 객체를 찾아서 해당 객체에 필요한 의존객체를 주입하는 것을 말한다. 
	//     단, 의존객체는 스프링 컨테이너속에 bean 으로 등록되어 있어야 한다. 

	//     의존 객체 자동 주입(Automatic Dependency Injection)방법 3가지 
	//     1. @Autowired ==> Spring Framework에서 지원하는 어노테이션이다. 
	//                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.

	//     2. @Resource  ==> Java 에서 지원하는 어노테이션이다.
	//                       스프링 컨테이너에 담겨진 의존객체를 주입할때 필드명(이름)을 찾아서 연결(의존객체주입)한다.

	//     3. @Inject    ==> Java 에서 지원하는 어노테이션이다.
	//                       스프링 컨테이너에 담겨진 의존객체를 주입할때 타입을 찾아서 연결(의존객체주입)한다.

	/*
	@Autowired
	private SqlSessionTemplate abc;
	// Type 에 따라 Spring 컨테이너가 알아서 root-context.xml 에 생성된 org.mybatis.spring.SqlSessionTemplate 의 bean 을  abc 에 주입시켜준다. 
    // 그러므로 abc 는 null 이 아니다.
	 */

	/*
	@Inject
	private SqlSessionTemplate abc;
	 */

	/*
	@Resource
	private SqlSessionTemplate sqlsession;//  로컬DB mymvc_user 에 연결
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession 는 null 이 아니다.


	@Resource
	private SqlSessionTemplate sqlsession_2; //// 로컬DB hr 에 연결
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession_2 인 bean을 주입하라는 뜻이다. 
    // 그러므로 sqlsession 는 null 이 아니다.


	 */

	@Autowired
	@Qualifier("sqlsession")
	private SqlSessionTemplate sqlsession;
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession 인 bean을 주입하라는 뜻이다. 
	// 그러므로 sqlsession 는 null 이 아니다.



	@Autowired
	@Qualifier("sqlsession_2")
	private SqlSessionTemplate sqlsession_2;
	// /board/src/main/webapp/WEB-INF/spring/root-context.xml 의  bean에서 id가 sqlsession_2 인 bean을 주입하라는 뜻이다. 
	// 그러므로 sqlsession 는 null 이 아니다.


	// spring_test 테이블에 insert 하기 
	@Override
	public int test_insert() {

		int n1 = sqlsession.insert("board.test_insert");
		int n2 = sqlsession_2.insert("hr.exam_insert");

		return n1*n2;
	} // end of Repository


	@Override
	public List<TestVO> test_select() {

		List<TestVO> testvoList =	sqlsession.selectList("board.test_select");

		return testvoList;
	}


	@Override
	public List<TestVO2> test_select_vo2() {
		List<TestVO2> testvoList =	sqlsession.selectList("board.test_select_vo2");

		return testvoList;
	}


	@Override
	public List<Map<String, String>> test_select_map() {
		List<Map<String, String>> mapList = sqlsession.selectList("board.test_select_map");
		return mapList;
	}




	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 
	@Override
	public int test_insert(TestVO tvo) {

		int n = sqlsession.insert("board.test_insert_vo", tvo);

		return n;
	}


	@Override
	public int test_insert_vo2(TestVO2 tvo) {

		int n = sqlsession.insert("board.test_insert_vo2", tvo);

		return n;
	}



	@Override
	public int test_insert(Map<String, String> paraMap) {
		int n = sqlsession.insert("board.test_insert_map", paraMap);
		return n;
	}

	/// #38
	@Override
	public List<Map<String, String>> getImgfilenameList() {
		List<Map<String, String>> mapList = sqlsession.selectList("board.getImgfilenameList");
		return mapList;
	}



	// #46. 로그인 처리하기 

	@Override
	public MemberVO getLoginMember(Map<String, String> paraMap) {

		MemberVO loginuser = sqlsession.selectOne("board.getLoginMember", paraMap);


		return loginuser;
	} // end of public MemberVO getLoginMember(Map<String, String> paraMap)





	@Override
	public void updateIdle(String userid) {
		sqlsession.update("board.updateIdle", userid);
		//네임스페이스.updateIdle

	} // end of public void updateIdle(String userid)



	// tbl_loginhistory 테이블에 로그인 기록 입력하기 
	@Override
	public void insert_tbl_loginhistory(Map<String, String> paraMap) {
		sqlsession.insert("board.insert_tbl_loginhistory", paraMap );


	}   





	// #56. 파일 첨부가 없는 글쓰기 
	@Override
	public int add(BoardVO boardvo) {

		int n = sqlsession.insert("board.add",boardvo);

		return n;
	} // end of public int add(BoardVO boardvo)


	
	// #60. 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 
	@Override
	public List<BoardVO> boardListNoSearch() {
		
		List<BoardVO> boardList = sqlsession.selectList("board.boardListNoSearch");
		
		return boardList;
	}


	//#64. 글 1개 조회하기 
	@Override
	public BoardVO getView(Map<String, String> paraMap) {
		
		BoardVO boardvo = sqlsession.selectOne("board.getView", paraMap);
		
		return boardvo;
	} // end of public BoardVO getView(Map<String, String> paraMap)


	//#66. 글 조회수 1증가하기 
	@Override
	public int increase_readCount(String seq) {
		
		int n = sqlsession.update("board.increase_readCount", seq);
				
				
				
				
		
		return n;
	} // end of public int increase_readCount(String seq) 















}
