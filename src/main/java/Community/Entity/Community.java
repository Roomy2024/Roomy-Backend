package Community.Entity;


import java.time.LocalDateTime; //java

import jakarta.persistence.*;

import lombok.Getter; // Lombok
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키 자동 생성
    private long id;

    @Column(length = 200, nullable = false) // 제목
    private String title;

    @Column(length = 500, nullable = false) // 내용
    private String content;

    @Column(length = 50, nullable = false) // 타입
    private String type;

    @Column(name = "Created_at", nullable = false, updatable = false) // 제작날짜
    private LocalDateTime createdAt;

    @Column(name= "updated_at") // 수정날짜
    private LocalDateTime UpdatedAt;


}

/*
*
* ID / int
* Title / VarChar
* Content / VarChar
* Created_at / LocalDateTime
* Updated_at / LocalDateTime
* Type / VarChar
* Like / VarChar
*
*/
