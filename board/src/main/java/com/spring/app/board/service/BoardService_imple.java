package com.spring.app.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.spring.app.board.domain.TestVO;
import com.spring.app.board.model.BoardDAO;

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
	public int test_insert(TestVO tvo) {
		int n = dao.test_insert(tvo);
		return n;
	}     

}
