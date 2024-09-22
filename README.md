# 세빛코드 - KNUV
## 서비스 요약
KNUV(크누브) - 경북대학교 제휴&홍보 통합 정보 제공 서비스 (외국어 지원)

## 주제 구분
-	E타입 경북대에 다니는 다양한 배경의 학우들을 위한 서비스

## 팀원 소개
세빛코드

남기연
- PM
- 디자인
- 프론트엔드
  
박서연
- 백엔드
- 프론트엔드
- DB 설계
  
이서윤
- 백엔드

## 시연 영상
(필수) Youtube 링크
(선택) Github Repository 페이지에서 바로 볼 수 있도록 넣어주셔도 좋습니다.

## 서비스 소개
### 서비스 개요
- 다양한 곳에서 개별적으로 올라오는 경북대학교와 관련된 다양한 제휴 및 홍보 정보들을 통합하여 제공하는 서비스이다.
- 유학생들을 위한 자동 번역 기능이 제공된다.
- 제휴 기간과 같은 주요 정보를 별도로 표시해 편리함을 제공한다.
- 북마크에 저장해 필요한 제휴 정보를 모아볼 수 있고 로그인하면 온라인에 저장된다.
- 인증된 계정은 직접 게시물 업로드를 요청할 수 있다.

### 타서비스와의 차별점
학과별 공지 채팅방을 제외하고는 제휴 정보를 편리하게 접할 수 있는 곳이 없다.
채팅방에서는 다양한 정보가 긴 스크롤 형식으로 제공되어 내용을 받아들이기가 느리고 금방 잊혀지기 쉽다.
이에 반해 이 서비스는 게시물의 제목을 주요하게 배치하여 정보를 쉽게 이해할 수 있도록 하며,
중요한 날짜 정보를 강조한다.

또한, 에브리타임의 홍보 게시판에는 학생들에게 유용하지 않거나 위험할 수 있는 각종 알바 및 광고가 올라오는 경우가 많다.
이 서비스는 홍보 게시물을 올릴 수 있지만, 에브리타임과 달리 익명으로 누구나 게시물을 올리는 방식이 아닌,
사업자 등록증 등으로 업체 인증을 받은 후 게시물 업로드 시 선별 과정을 거쳐 보다 안전하게 서비스를 제공한다.

### 페이지 설계
[(Figma) 페이지 설계](https://www.figma.com/design/Zxm98DHbclEOrcDxeUAZQx/KNUV?node-id=0-1&t=vZYLBGWyGOduaM6v-1)

### 구현 내용 및 결과물



서비스의 실제 구현 내용과 결과물을 기재한다.

ex)
1. 실력별 매칭 시스템
  - 본인이 미리 선택한 탁구 실력에 맞추어 다른 사용자를 매칭해준다.
  - 매칭된 사용자와의 매칭이 종료된 이후, 상대의 실력을 평가할 수 있다.
2. 탁구 용품 구매 페이지

|액티비티|화면|설명|
|:----:|:----------:|:-----:|
|Main(Korean)|![image](https://github.com/user-attachments/assets/19536e69-3c75-4d74-aa6a-472a20f76d61)|설명|
|Main(English)|![image](https://github.com/user-attachments/assets/3268e311-fa17-42ad-b91e-86cb96c41744)|- 영어로 표시되는 메인 화면.|
|Detail(Korean)|![image](https://github.com/user-attachments/assets/c5e978b1-7f5b-4e51-931e-b30ebdbd079d)||
|Detail(English)|![image](https://github.com/user-attachments/assets/cb798126-5636-4415-baf0-450d36ead79c)|- 영어로 표시되는 상세 화면.|
|Bookmark|![image](https://github.com/user-attachments/assets/289d2e4a-93cb-4e09-bfac-e8f40c996b00)|- 북마크 목록이 표시되는 화면 - 실제 북마크 기능은 구현하지 못함|
|Login|![image](https://github.com/user-attachments/assets/34bb6249-2ced-4042-897b-53f1cfd84434)|- 구글 로그인 기능|
|Certi|![image](https://github.com/user-attachments/assets/0e1b9429-2f65-4999-86db-5ee115bdfe4a)|- 프로필 화면(1) - 로그아웃 기능 - 파일 업로드 및 DB 전송 기능 - 사용자는 사업자등록증 등 자신의 신분을 증명할 자료를 업로드한다.|
|Request|![image](https://github.com/user-attachments/assets/a22b2509-8c23-42db-b3c0-764e03aa8a55)|- 프로필 화면(2) - (1)에서 인증된 사용자를 대상으로 하는 페이지로, 게시물을 업로드할 수 있다.|



### 구현 방식

|범위|프레임워크|
|:----:|:----------:|
|FrontEnd|Android SDK, Jetpack Compose, Kotlin, Glide|
|BackEnd|Firestore, Firebase Storage, Firebase Authentication, Papago API|
|Design|Figma|


## 향후 개선 혹은 발전 방안
- 각 게시물은 위치 정보를 포함하고 있다. 위치 정보가 존재하면 게시물의 상세 페이지에서 지도 아이콘이 활성화되며, 현재 위치로부터 길찾기 기능을 이용할 수 있다.
- 현재는 한국어와 영어만 지원하지만, 다양한 언어를 추가할 수 있다.
- 이용 가능한 날짜가 되면 알림을 보내는 기능을 추가하여 제휴 이용률을 높일 수 있다.
- 게시자가 업로드된 게시물을 직접 수정하는 기능을 추가할 수 있다.
- 학생용 페이지를 추가하여 학생들도 직접 홍보글을 올릴 수 있다. 이는 에브리타임과 달리 유기명 또는 학번, 학과 표기 등 제한적으로 정보를 공개하므로 완전한 익명은 아니다.
