### CORS

- HTTP 헤더를 사용하여, 한 출처에서 실행 중인 웹 애플리케이션이 다른 출처의 선택한 자원에 접근할 수 있는 권한을 부여

- Protocol, Host,Port 동일한지 비교

- 서버에서 Protocol, Host, Port

### CORS 해결

1. 서버에서 Access-Control-Allow-* 세팅

- Access-Control-Allow-Origin : 헤더 작성된 출처만 브라우저 리소스 접근하도록 허용
- Access-Control-Allow-Methods
- Access-Control-Allow-Headers
- Access-Control-Allow-Credentials
- Access-Control-Max-Age

2. CorsConfigurer

- Spring Security 필터 체인에 CorsFilter 추가
-

3. CorsFilter

- CORS 예비 요청을 처리하고 CORS 단순 및 본 요청을 가로채고, 제공된 CorsConfigurationSource 를 통해 일치된 정책에 따라 CORS 응답 헤더와 같은 응답을 업데이트하기 위한 필터

### oauth2Login

- OAuth 2.0 기반의 로그인을 처리하기 위한 메서드

- 사용자 대신 액세스 토큰 얻기 위한 인증 플로우 설정에 사용

- 로그인 완료될 경우 액세스 토큰을 요청하고 사용자를 인증

### oauth2Client

- OAuth 2.0 클라이언트로서의 역할을 설정

- 액세스 토큰을 검증하고, 요청된 리소스에 대한 접근 권한을 부여

- 클라이언트의 권한부여승인까지 처리하고 최종사용자의 인증처리는 하지 않음

- 개발자가 인증 처리를 OAuth2AuthorizedClientManager 를 사용하여 만들어줘야 함

- ? 애플리케이션에서 외부 자원에 접근하기 위한 설정

### oauth2ResourceServer

- OAuth 2.0 리소스 서버로서의 역할을 설정

- 클라이언트가 제공한 액세스 토큰을 검증하고, 요청된 리소스에 대한 접근 권한을 부여하는 역할

- 애플리케이션이 제공하는 자원 (API 엔드포인트)에 대한 인증 및 권한 부여를 관리

## OAuth 2.0 Roles

1. Resource Owner

   보호 자원에 접근 권한 부여하는 주체

2. Resource Server

   타사 어플리케이션에서 접근하는 사용자의 자원이 포함된 서버

   액세스 토큰을 수락 및 검증
   
   타 서비스에 우리의 로그인 정보를 제공하려는 경우 

   issuer-uri : 클라이언트 애플리케이션이 OpenID Connect Provider를 식별하고 인증 서버와의 상호작용을 시작할 때 사용 
   jwk-set-uri : 인가 서버가 발급한 jwt 토큰의 공개키 정보를 검색하기 위한 엔드 포인트

3. Authorization Server

   사용자가 클라이언트에게 권한 부여 요청을 승인

   access token 을 클라이언트에게 부여

4. Client

   사용자의 리소스에 접근

   사용자의 상호 작용 없이 권한 부여 서버로부터 직접 권한 얻을 수 있음


1. Access Token 요청 -> 2. Access Token 응답 ->
3. Access Token 으로 Api 호출 -> 4. Token 검증
5. Token 통과 -> 6.요청 데이터 응답


Realm
: 사용자, 인증, 인가, 권한, 그룹이 관리하는 범위

Realm내의 Client들 SSO 공유

Client
: 인증, 인가 업무를 Keycloak에게 요청할 수 있는 주로 어플리케이션이나 서비스

카카오톡 예시 
- 카카오톡 메일 / 선물하기 / 쇼핑하기 서비스는 같은 Realm 을 사용하는 client

- 따로 가입 없이 카카오톡으로 사용 가능하기 때문에!!!

- 하지만 카카오 뱅크 / 카카오 페이 티스토리 등은 계정을 새로 가입해야함 같은 Realm 아님


[//]: # (docker run -d --name keycloak-sample-container -p 8081:8081 -e KEYCLOAK_ADMIN=admin -e KEYCLOAK_ADMIN_PASSWORD=admin -e DB_VENDOR=mysql -e DB_ADDR=localhost -e DB_PORT=3306 -e DB_DATABASE=test_database -e DB_USER=root -e DB_PASSWORD=test quay.io/keycloak/keycloak:24.0.1 start-dev)
[//]: # username (tester)
[//]: # 아이디 (tester)
[//]: # 비번 (test123!@#)

client_id : 클라이언트에 대해 생성된 고유 키
client_secret : 인가서버에 등록된 특정 클라이언트 
( 카카오의 경우 선택적으로 client_secret를 사용하도록 설정할 수 있음)


엔드포인트
: 네트워크 서비스에 접근할 수 있는 URL
: 웹 서버, 웹 API, 웹 서비스 또는 다른 네트워크 서비스와 상호작용할 때 사용


클라이언트 권한 부여 요청
클라이언트 인가 서버로 권한 부여 요청 => 토큰 요청 클라이언트 정보 및 엔드포인트 정보 참조
 
 Registration
 인가 서버에 등록된 클라이언트 요청 파라미터 정보 
 
 Porvider
 공급자에서 제공하는 정보
 
 