## Roomy Commit Message Convention


-- 구조 -- 

제목 ( 타입 )<br/>
: 제목은 50글자 이내<br/>
: 첫 글자는 대문자로 작성<br/>
: 마침표나 특수기호 사용 금지<br/>
: 과거형 사용 금지<br/>
: 간경하고 요점만<br/>

본문 ( 내용 )<br/>
최대한 상세히 작성 ( 코드 변경 이유가 있으면 좋음 )<br/>
어디 폴더의 어떤 파일에 어떤 코드를 왜 변경했는지 작성<br/>

제목 : Tag<br/>
본문 : 상세히 변경 이유 및 오류 이유 및 무엇을 변경 했는지나 왜 변경 했는지 적기<br/>

Tag : 태그 번호<br/>

예시)

Feat : 회원 가입 기능 추가 <br/>
Body : 카카오톡 로그인 기능 추가 <br/>
Tag : #1 <br/>

## Tag 번호 및 태그 설명

#1 : Feat ( 새로운 기능 추가 )<br/>
#2 : Fix ( 버그 수정 )<br/>
#3 : !BREAKING CHANGE ( API 대량 수정 )<br/>
#4 : Comment ( 필요한 주석 추가 및 변경 )<br/>
#5 : Rename ( 파일 및 폴더 명 수정하거나 옮기는 작업만 있는 경우 )<br/>
#6 : Remove ( 파일을 삭제한 경우만 있는 경우 )<br/>
#7 : Fixes ( 이슈 수정중 )<br/>


---

### Mysql 연결 코드 기본 <br/>
```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver <br/>
spring.datasource.url=jdbc:mysql://[IP]:[포트번호]/[DB이름]?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul <br/>
spring.datasource.username=[본인계정이름] <br/> 
spring.datasource.password=[본인계정비밀번호] <br/>

true 설정 시 jpa 쿼리문 확인 가능 <br/>
spring.jpa.show-sql=true <br/>

DDL 정의시 DB의 고유 기능을 사용 가능 <br/>
spring.jpa.hibernate.ddl-auto=update <br/>

 JPA의 구현체인 Hibernate가 동작하면서 발생한 SQL의 가독성을 높여줌 <br/>
spring.jpa.properties.hibernate.format_sql=true <br/>
```

---

### 프로젝트 Mysql 연결 코드 (로컬로) <br/>
Mysql 설정 Local <br/>
```
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver <br/>
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/roomy?useSSL=false&useUnicode=true&serverTimezone=Asia/Seoul <br/>
spring.datasource.username=root <br/>
spring.datasource.password=1234 <br/>

true 설정 시 jpa 쿼리문 확인 가능 <br/> 
spring.jpa.show-sql=true<br/>

DDL 정의시 DB의 고유 기능을 사용 가능<br/>
spring.jpa.hibernate.ddl-auto=update<br/>

JPA의 구현체인 Hibernate가 동작하면서 발생한 SQL의 가독성을 높여줌<br/>
spring.jpa.properties.hibernate.format_sql=true<br/>
```
