## json server 실행방법

1. cd frontend/json-server
2. npm install
3. json-server --watch db.json --routes routes.json --port 3000


### json server 작성방법
1. db.json 에 작성하기
2. routes.json 경로설정하기
    -> 경로 설정이유 : json-server는 1depth 만 지원


### db.json 작성 방법

```aiignore
{
"매핑 경로": [
{   
    "컬럼명": "레코드",
    "컬럼명": "레코드",
    "컬럼명": "레코드",
    "컬럼명": "레코드"
},
{
    "컬럼명": "레코드",
    "컬럼명": "레코드",
    "컬럼명": "레코드",
    "컬럼명": "레코드"
},
],
}
```

```aiignore
{
  "generalUser": [
      {
          "userId": 1,
          "name": "유저1",
          "email": "user1@test.com",
          "phone": "010-0001-0001",
          "enterDate": "2023-01-01",
          "user_status": "정상"
      },
      {
```