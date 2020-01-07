# SafeIdentity Android Sample

## 소개

 이 프로젝트는 기존 웹에서 구현되었던 SSO(SafeIdentity)를 스마트폰(안드로이드, 아이폰 등)에서도 동일한 SSO 환경을 구현하여 앱, 웹에서 SSO 시스템을 구현한 제품입니다. 본 매뉴얼에서는 제공되는 샘플에서의 사용법을 제공하고 API 관련 내용은 API 문서를 참조하시기 바랍니다.

## 시작하기 전

1. 모바일의 콘텐츠를 서비스하는 서버에 SafeAgent를 한컴 시큐어 담당 엔지니어에 설치를 요청

2. WAS 라이브러리 디렉토리에 ServerAPI 라이브러리(jar) 추가

3. exp_mobilesso.jsp 파일을 WAS 서버의 Web 서비스 경로에 파일 업로드

## 빌드 환경

- Android 4.0.3 (API level 15) 또는 이후 버전
- Java 8 또는 이후 버전

## 시스템 권한

```manifest
<uses-permission android:name="android.permission.READ_PHONE_STATE" />  
<uses-permission android:name="android.permission.INTERNET" />  
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />  
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 모바일 SSO API

모바일 SSO API에 대한 설명입니다.

### 모바일 SSO API 생성

```java
MobileSsoAPI mobileSsoAPI;
...
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ...
    mobileSsoAPI = new MobileSsoAPI(this, 'exp_mobilesso.jsp 주소');
    ...
}
```

### Security ID 생성

스마트폰의 유니크 아이디를 생성할 수 있다.

```java
String securityId = SsoUtil.getSecId(this);
```

### 엔터프라이즈 로그인

암복호화 서비스, 사용자 인증 수행(세션을 유지함), LDAP을 이용한 사용자 신원 확인, 사용자 정보 관리, 권한관리 정보 관리, 사용자 정의 데이터 관리, 계정 정보 관리 등

Client IP는 127.0.0.1 고정으로 하고 덮어쓰기 유무는 "true"로 한다.

```java
String token = mobileSsoAPI.andrsso_authID(로그인아이디, 비밀번호, "true", "127.0.0.1", securityId);
```

### 스탠다드 로그인

암복호화 서비스, 사용자 인증 수행(세션을 유지함) 

Client IP는 127.0.0.1 고정으로 하고 덮어쓰기 유무는 "true"로 한다.

```java
String token = mobileSsoAPI.andrsso_regUserSession(로그인아이디, "127.0.0.1", "true", securityId);
```

### 익스프레스 로그인

암복호화 서비스, 사용자 인증 수행(세션을 유지하지 않음) 

Client IP는 127.0.0.1 고정으로 한다.

덮어쓰기 유무는 "true"로 한다.

```java
String token = mobileSsoAPI.andrsso_makeSimpleToken("3", 로그인아이디, "127.0.0.1", securityId);
```

### 로그아웃

Client IP는 127.0.0.1 고정으로 한다.

```java
mobileSsoAPI.andrsso_unregUserSession(mobileSsoAPI.getToken(), "127.0.0.1");  
if (mobileSsoAPI.deleteToken() == 0) {  
    finish();  
}
```


