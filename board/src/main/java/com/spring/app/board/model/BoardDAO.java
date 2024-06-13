package com.spring.app.board.model;

import java.util.List;

import com.spring.app.board.domain.TestVO;

public interface BoardDAO {

	
	// spring_test 테이블에 insert 하기 
	int test_insert();

	// spring_test 테이블에  select 해오기 
	List<TestVO> test_select();

	
	
	// view단의 form 태그에서 입력받은 값을 spring_test 테이블에 insert 하기 
	int test_insert(TestVO tvo);
	
	
	
}
