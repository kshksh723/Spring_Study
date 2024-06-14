package com.spring.app.board.model;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardDAO {

	
	// spring_test 테이블에 insert 하기 
	int test_insert();

	// vo에서 
	List<TestVO2> test_select_vo2();
	
	// spring_test 테이블에  select 해오기 
	List<TestVO> test_select();


	List<Map<String, String>> test_select_map();
	
	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 
	int test_insert(TestVO tvo);
	
	int test_insert_vo2(TestVO2 tvo);
	
	int test_insert(Map<String, String> paraMap);

	
	// 시작페이지에서 캐러잴 보여주기
	List<Map<String, String>> getImgfilenameList();

	// 로그인 처리하기
	MemberVO getLoginMember(Map<String, String> paraMap);

	// 휴면 계정 update?
	void updateIdle(String userid);

	// 파일첨부가 없는 글쓰기 
	int add(BoardVO boardvo);



	
	
	
	
}
