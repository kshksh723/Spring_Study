------ ***** spring 湲곗큹 ***** ------

show user;
-- USER�씠(媛�) "MYMVC_USER"�엯�땲�떎.

create table spring_test
(no         number
,name       varchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_TEST�씠(媛�) �깮�꽦�릺�뿀�뒿�땲�떎.


select *
from spring_test;


-------------------------------------------------

show user;
-- USER�씠(媛�) "HR"�엯�땲�떎.

create table spring_exam
(no         number
,name       varchar2(100)
,address    Nvarchar2(100)
,writeday   date default sysdate
);
-- Table SPRING_EXAM�씠(媛�) �깮�꽦�릺�뿀�뒿�땲�떎.

select *
from spring_exam;


--------------------------------------


show user;
-- USER�씠(媛�) "MYMVC_USER"�엯�땲�떎.

insert into spring_test(no,name, writeday)
values(102, '諛뺣낫�쁺', default);


insert into spring_test(no,name, writeday)
values(102, '蹂��슦�꽍', default);

commit;

select no, name, to_char(writeday, 'yyyy-mm-dd hh24:mi:ss') AS writeday
from spring_test
order by no asc;
