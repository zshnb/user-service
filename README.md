## How to design

- The project is build by Spring Boot framework, MybatisPlus, MySQL and Redis, use Maven as build tool.
- I use logback to log error message in a global error handle controller.
- I use two table implement **friends** which are `follow` and `fan`. the `follow` table store user with people who he/she follow. the `fan` table store user with people who he/she follow it. with this structure only scan in one table can get follower or fan.
- I use Redis geo command to store user's coordinate and search friends who nearby(with radius parameter default 500 mile) the giving user.

## How to run

1. clone the repo then open with Intellij IDEA, wait for download all dependency by Maven.

2. start Redis and MySQL server, if you don't want download you can use `docker` like

   ```shell
   docker run --name mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root -d mysql:5.7 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
   docker run --name redis -p 6379:6379 -d redis
   ```

3. create database `user` and execute `src/main/resources/sql.sql`.
4. if you need deploy to cloud server, set environment `oauth2.server-url=http://ip:8081`
5. run `src/main/java/com/zshnb/userservice/SpringMainApplication.java`.

## API Document

**Note**: You can use `curl` or `postman` to test following api, example:

```shell
curl -d '{"name": "zsh", "address": "100.0, 50.5", "description": ""}' -H "Content-Type: application/json" -X POST 119.45.26.94:8081/api/user
```

every call will get response below

```
HttpStatus.OK with
{
	code: 200,
	message: null,
	data: {
		// return data
	}
}
HttpStatus.FORBIDDEN with no oauth
HttpStatus.BAD_REQUEST with
{
	code: 400,
	message: 'wrong message'
}
```

### Basic user CRUD

| api                | method | data                                                         | description                                                  | response                                                     |
| ------------------ | ------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| /api/user          | POST   | {"name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"} | add a user, address is contains longitude and latitude like "100.0,50.0", longitude between -180.0 and +180.0, latitide between -85.0 and +85.0. dob's format is 'yyyy-MM-dd hh:mm:ss' | {"id": 1, name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"} |
| /api/user/{userId} | PUT    | {"name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"} | modify user with userId, {userId} is path variable which can get by add user api response | {"id": 1, name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"} |
| /api/user/{userId} | GET    |                                                              | get user with userId, {userId} is path variable which can get by add user  api response |                                                              |
| /api/user/{userId} | DELETE |                                                              | delete user with userId, {userId} is path variable which can get by add user  api response |                                                              |

### Friends api

| api           | method | data                             | description          | response |
| ------------- | ------ | -------------------------------- | -------------------- | -------- |
| /api/follow   | POST   | {"userId": 1, "followUserId": 2} | follow target user   |          |
| /api/unfollow | DELETE | {"userId": 1, "followUserId": 2} | unfollow target user |          |

before you call following api, you should pass oauth2 authorization, or you will get a FORBIDDEN http status

1. visit `http://119.45.26.94:8081/oauth2/authorize?response_type=code&client_id=wiredcraft&redirect_uri=http://119.45.26.94:8081/oauth2/redirect-page&scope=userinfo`, i provide test app with name `wiredcraft` and password `password.`
2. after successful login, the page will show two button, click yes then page will show a Json, copy data's string, it's oauth2's code. 
3. visit `http://119.45.26.94:8081/oauth2/token?grant_type=authorization_code&client_id=wiredcraft&client_secret=secret&code=${code}` paste step2's code to replace `${code}`
4. finally you will get Json in page, copy data.access_token to prepare for call api. 

| api                                                          | method | data                                                         | description                                          | response                                                     |
| ------------------------------------------------------------ | ------ | ------------------------------------------------------------ | ---------------------------------------------------- | ------------------------------------------------------------ |
| /api/follow/{userId}/follow-users?page_number=1&page_size=20&access_token=${access_token} | GET    | ${userId}: user id like 1                               ${access_token}: from step4's access token | list target user's follow user                       | {"data": [{id": 1, name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"}]} |
| /api/fan/{userId}/fan-users?page_number=1&page_size=20&access_token=${access_token} | GET    | ${userId}: user id like 1                               ${access_token}: from step4's access token | list target user's fan user                          | {"data": [{id": 1, name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"}]} |
| /api/user/{name}/nearby-friends?radius=500.0&limit=20&access_token=${access_token} | GET    | ${name}: user name like zsh                               ${access_token}: from step4's access token                              radius: search in giving radius, unit is mile                                limit: search at most user count | list friends nearby ${radius} miles at most ${limit} | {"data": [{id": 1, name": "zsh", "address": "100.0, 50.5", "description": "", "dob": "2021-12-01 12:00:00"}]} |