package com.spring.app.board.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.ModelAndView;

import com.spring.app.board.domain.BoardVO;
import com.spring.app.board.domain.CommentVO;
import com.spring.app.board.domain.MemberVO;
import com.spring.app.board.domain.TestVO;
import com.spring.app.board.domain.TestVO2;

public interface BoardService {

	int test_insert();

	List<TestVO> test_select();
	List<TestVO2> test_select_vo2();
	List<Map<String, String>> test_select_map();

	int test_insert(TestVO tvo);
	int test_insert_vo2(TestVO2 tvo);
	int test_insert(Map<String, String> paraMap);

	
	////////////////////////////////////////////////////
	
	// 시작페이지에서 캐러절을 보여주는것
	List<Map<String, String>> getImgfilenameList();

	// 로그인 처리하기
	MemberVO getLoginMember(Map<String, String> paraMap);
	
	/////////////////////////////////////////////////////
	ModelAndView index(ModelAndView mav);
	ModelAndView loginEnd(Map<String, String> paraMap, ModelAndView mav, HttpServletRequest request);
	ModelAndView logout(ModelAndView mav, HttpServletRequest request);
	/////////////////////////////////////////////////////

	// 파일첨부가 없는 글쓰기 
	int add(BoardVO boardvo);

	// 페이징 처리를 안한 검색어가 없는 전체 글목록 보여주기 
	List<BoardVO> boardListNoSearch();

	// 글 조회수 증가와 함께 글 1개를 조회를 해오는 것
	BoardVO getView(Map<String, String> paraMap);

	// 글 조회수 증가는 없고 단순히 글 1개만 조회를 해오는 것
	BoardVO getView_no_increase_readCount(Map<String, String> paraMap);

	// 1개글 수정하기
	int edit(BoardVO boardvo);

	// 1개글 삭제하기
	int del(String seq);

	// 댓글쓰기(Transaction 처리)
	int addComment(CommentVO commentvo) throws Throwable;

	// 원게시물에 딸린 댓글들을 조회해오기
	List<CommentVO> getCommentList(String parentSeq);

	// 댓글 수정(Ajax 로 처리)
	int updateComment(Map<String, String> paraMap);

	// 댓글 삭제(Ajax 로 처리) Transaction 처리
	int deleteComment(Map<String, String> paraMap) throws Throwable;

	// CommonAop 클래스에서 사용하는 것으로 특정 회원에게 특정 점수만큼 포인트를 증가하기 위한 것   
	void pointPlus(Map<String, String> paraMap); 

	

	

	
	
}
