package com.spring.app.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardService {

	int test_insert();

	List<TestVO> test_select();
	
	List<Map<String, String>> test_select_map();
	
	int test_insert(TestVO tvo);
	
	int test_insert_vo2(TestVO2 tvo);
	
	List<TestVO2> test_select_vo2();

	int test_insert(Map<String, String> paraMap);

	///////////////////////////////////////////////
	
	// 시작페이지에서 캐러젤을 보여주는 것
	List<Map<String, String>> getImgfilenameList();

	
	//로그인 처리하기 
	MemberVO getLoginMember(Map<String, String> paraMap);
	
	/////////////////////////////////////////
	
	ModelAndView index(ModelAndView mav);

//파일 첨부가 없는 글쓰기 
	int add(BoardVO boardvo);

	
	
	////////////////////////////////////////////





	



	
	
}
