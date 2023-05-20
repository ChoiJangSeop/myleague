# MY LEAGUE

### Index

1. Intro
2. Tech / Environment
3. How to use
4. License

----

### Intro

**MyLeague**는 스포츠 리그 통합 관리 API입니다. 

#### 선수 Player

- 선수를 생성하고 포지션과 이름을 등록합니다
- 팀에 등록하고 해지합니다

#### 팀 Team

- 팀의 이름과 이니셜 등을 정하고 생성합니다.
- 대회에 참가해 경기를 합니다
- 선수를 등록하고 해제할 수 있습니다.

#### 대회 League

- 대회를 생성해 참가 팀을 등록합니다.
- 경기를 생성해 경기를 진행하고 팀의 전적과 순위를 집계합니다.

#### 경기 Match

- 홈팀과 어웨이팀을 지정하고 경기를 생성합니다
- 경기를 진행하고 경기 결과를 대회 기록에 업데이트합니다.

----

### Tech / Environment

| #         | Tech/Environment                          |
| --------- | ----------------------------------------- |
| Basic     | Java11 / Gradle8.0                                    |
| Framework | Spring3 : Web / HATEOAS / Spring Data JPA |
| Library   | Lombok / QueryDSL5 / Swegger-ui             |
| Test      | junit5                                    |
| DB        | H2 Database                               |

---

### How to Use

#### 실행

1. **application.yml** 파일로가 자신의 DB 정보를 입력
```yml
spring:  
  datasource:  
    url: #db-url 
    username: #username  
    password: #password
    driver-class-name: #db-driver 
  
  jpa:  
    hibernate:  
      ddl-auto: validate  # create
  ...
```

2. **gradle build**를 통해 war파일로 추출


#### API 문서

(서버 URL)/swegger-ui.html 에 접속하면 자세한 API 문서가 존재합니다.

1. **Team**


| Method | URL                  | detail       |
| ------ | -------------------- | ------------ |
| GET    | /teams               | 팀 전체 조회 |
| POST   | /teams               | 팀 생성      |
| DELETE | /teams/{id}          | 팀 단건 삭제 |
| GET    | /teams/{id}          | 팀 단건 조회 |
| PUT    | /teams/{id}          | 팀 수정      |
| PUT    | /teams/{id}/activate | 팀 재복구             |

2. **Player**


| Method | URL                             | Detail         |
| ------ | ------------------------------- | -------------- |
| GET    | /players                        | 선수 전체 조회 |
| POST   | /players                        | 선수 생성      |
| GET    | /players/search                 | 선수 검색      |
| GET    | /players/{id}                   | 선수 단건 조회 |
| PUT    | /players/{id}                   | 선수 수정      |
| PUT    | /players/{id}/register/{teamId} | 선수 팀 등록   |
| PUT    | /players/{id}/deregister        | 선수 팀 등록 해제               |

3. **League**


| Method | URL           | Detail         |
| ------ | ------------- | -------------- |
| GET    | /leagues      | 리그 전체 조회 |
| POST   | /leagues      | 리그 생성      |
| DELETE | /leagues/{id} | 리그 단건 삭제 |
| GET    | /leagues/{id} | 리그 단건 조회 |
| PUT    | /leagues/{id} | 리그 수정               |

4. **Participant**


| Method | URL                                        | Detail                     |
| ------ | ------------------------------------------ | -------------------------- |
| GET    | /participants                              | 참가팀 전체 조회           |
| POST   | /participants                              | 참가팀 생성                |
| GET    | /participants/serach                       | 참가팀 검색                |
| GET    | /participants/{id}                         | 참가팀 단건 조회           |
| POST   | /participants/{id}/records                 | 참가팀 기록 생성           |
| GET    | /participants/{id}/records                 | 참가팀 기록 전체 조회      |
| GET    | /participants/{id}/records/{round}         | 참가팀 기록 단건 조회      |
| GET    | /participants/{id}/records/{round}/matches | 특정 라운드 모든 경기 조회 |
| DELETE | /participants/{id}                         | 참가팀 삭제                           |

5. **Match**


| Method | URL                  | Detail         |
| ------ | -------------------- | -------------- |
| GET    | /matches             | 경기 전체 조회 |
| POST   | /matches             | 경기 생성      |
| GET    | /matches/search      | 경기 검색      |
| DELETE | /matches/{id}        | 경기 단건 삭제 |
| GET    | /matches/{id}        | 경기 단건 조회 |
| PUT    | /matches/{id}/cancel | 경기 결과 취소 |
| PUT    | /matches/{id}/play   | 경기 결과 입력               |



