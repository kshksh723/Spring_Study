------ ***** spring 기초 ***** ------

show user;
-- USER이(가) "MYMVC_USER"입니다.

create table spring_test
(no         number
,name       varchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_TEST이(가) 생성되었습니다.


select *
from spring_test;


-------------------------------------------------

show user;
-- USER이(가) "HR"입니다.

create table spring_exam
(no         number
,name       varchar2(100)
,address    Nvarchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_EXAM이(가) 생성되었습니다.

select *
from spring_exam;


--------------------------------------


show user;
-- USER이(가) "MYMVC_USER"입니다.

insert into spring_test(no,name, writeday)
values(102, '박보영', default);


insert into spring_test(no,name, writeday)
values(102, '변우석', default);

commit;

select no, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from spring_test
order by no asc;



--------------------------------------------------------------------------------

show user;
-- USER이(가) "MYMVC_USER"입니다.

create table tbl_main_image_product
(imgno           number not null
,productname    NVARCHAR2(20) not null
,imgfilename     varchar2(100) not null
,constraint PK_tbl_main_image_product primary key(imgno)
);
-- Table TBL_MAIN_IMAGE이(가) 생성되었습니다.

create sequence seq_main_image_product
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;
-- Sequence SEQ_MAIN_IMAGE이(가) 생성되었습니다.

insert into  tbl_main_image_product(imgno, productname, imgfilename) values(seq_main_image_product.nextval,'미샤', '미샤.png');  
insert into  tbl_main_image_product(imgno,productname, imgfilename) values(seq_main_image_product.nextval,'원더플레이스', '원더플레이스.png'); 
insert into  tbl_main_image_product(imgno,productname, imgfilename) values(seq_main_image_product.nextval,'레노보', '레노보.png'); 
insert into tbl_main_image_product(imgno,productname, imgfilename) values(seq_main_image_product.nextval,'동원', '동원.png'); 

commit;

select imgno, productname, imgfilename
from  tbl_main_image_product
order by imgno asc;

select *
from tbl_member
where userid = 'kimsh' and pwd = '86fdff24aec78a01391e48e30b29bfdc0c47abdbbc6a0b9b833c5bc464a4cdbe';

---로그인처리


 SELECT userid, name, coin, point, pwdchangegap, 
		 NVL( lastlogingap, trunc( months_between(sysdate, registerday) ) ) AS lastlogingap, 
					     idle, email, mobile,  postcode, address, detailaddress, extraaddress  
		FROM
		( select userid, name, coin, point,  trunc( months_between(sysdate, lastpwdchangedate) ) AS pwdchangegap, 
				 registerday, idle,   email, mobile,  postcode, address, detailaddress, extraaddress 
                 from tbl_member
					    where status = 1 and userid = 'kimsh' and pwd = '86fdff24aec78a01391e48e30b29bfdc0c47abdbbc6a0b9b833c5bc464a4cdbe') M
                        CROSS JOIN ( select trunc( months_between(sysdate, max(logindate)) ) AS lastlogingap 
					    from tbl_loginhistory 
					     where fk_userid = 'kimsh' ) H;
                         
                         
select userid, lastpwdchangedate, idle
from tbl_member               
where userid in('kimsh','eomjh','leess');


select *
from tbl_loginhistory
order by historyno desc;





    ------- **** spring 게시판(답변글쓰기가 없고, 파일첨부도 없는) 글쓰기 **** -------

show user;
-- USER이(가) "MYMVC_USER"입니다.    
    
    
desc tbl_member;

create table tbl_board
(seq         number                not null    -- 글번호
,fk_userid   varchar2(20)          not null    -- 사용자ID
,name        varchar2(20)          not null    -- 글쓴이 
,subject     Nvarchar2(200)        not null    -- 글제목
,content     Nvarchar2(2000)       not null    -- 글내용   -- clob (최대 4GB까지 허용) 
,pw          varchar2(20)          not null    -- 글암호
,readCount   number default 0      not null    -- 글조회수
,regDate     date default sysdate  not null    -- 글쓴시간
,status      number(1) default 1   not null    -- 글삭제여부   1:사용가능한 글,  0:삭제된글
,constraint PK_tbl_board_seq primary key(seq)
,constraint FK_tbl_board_fk_userid foreign key(fk_userid) references tbl_member(userid)
,constraint CK_tbl_board_status check( status in(0,1) )
);
-- Table TBL_BOARD이(가) 생성되었습니다.

create sequence boardSeq
start with 1
increment by 1
nomaxvalue
nominvalue
nocycle
nocache;

commit;

select *
from tbl_board