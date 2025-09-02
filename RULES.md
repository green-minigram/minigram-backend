# Backend Conventions — RULES.md

> Spring Boot 기반 백엔드 컨벤션.

---

## 0) Controller & Service 규칙

* **Controller와 Service 메서드명은 동일**
* **Service 메서드는 도메인 접두어를 생략** (교차 도메인 조작 시에만 접두어 사용)

### 예시

```java
// Controller
@PostMapping("/api/posts")
public ResponseEntity<PostResponse> create(@RequestBody PostRequest req) {
    return ResponseEntity.ok(postService.create(req));
}

// Service
public PostResponse create(PostRequest req) { ...} // PostService, 접두어 생략

// 교차 도메인 조작 시
public void hidePost(Long postId) { ...} // ReportService에서 Post를 숨김
```

---

## 1) 용어 & 표기 규칙

* **메서드명 형식:** 카멜케이스, 소문자 시작.
* **동사 세트(표준):**
  `create / find / findAll / update / delete / search / approve / reject / like / unlike / follow / unfollow / markRead / logout / login / check`
* **Repository 메서드:** Controller/Service와 **일치할 필요 없음** (세분화된 쿼리 전담)

---

## 2) URI & HTTP 설계 규칙

* **리소스 지향 URI + HTTP 메서드**로 의미 표현.

    * 예) `/posts`, `/posts/{postId}/likes`, `/comments/{commentId}`
* **행위성 액션(승인/거절/읽음 등):** 하위 리소스/액션 엔드포인트

    * 예) `PUT /notifications/{id}/read`, `PUT /admin/reports/{id}/approve`
* **Path vs Query:**

    * 식별자 → **PathVariable** (`/{postId}`, `/{userId}` 등) — 단순 `id` 보다는 **도메인명을 포함한 id**를 사용하여 가독성과 명확성을 높임
    * 검색/필터/정렬/페이지 → **QueryString** (`?keyword=&page=`)

**DTO 네이밍:**

* 요청: `XxxRequest.CreateDTO`, `XxxRequest.SearchDTO`
* 응답: `XxxResponse.DetailDTO`, `XxxResponse.UpdateDTO`

---

## 3) Controller / Service 메서드 네이밍 규칙

* 생성: `create`
* 단건 조회: `find`
* 목록 조회: `findAll`, 또는 `findAllByXxx(...)`
* 수정: `update`
* 삭제: `delete`
* 관계/서브리소스: `like/unlike`, `follow/unfollow`, `markRead`
* 검색: `search`
* 관리자 업무: `approve/reject` (필요 시 교차 도메인 메서드명에 대상 도메인 표기)

---

## 4) Repository 메서드 네이밍 규칙

* 단건: `find{엔티티}By{컬럼}` (예: `findPostById`)
* 목록: `findAllBy{컬럼}`, 복수조건은 `And` 결합 (예: `findAllByAuthorIdAndStatus`)
* 조인 필요 시: `find{엔티티}By{테이블1}Join{테이블2}` 또는 `@Query/QueryDSL` 사용 권장
* 변경 쿼리: `update{엔티티}...`, `delete{엔티티}...`

---

## 5) Controller 생성 시 통합 테스트 진행

* **Controller 메서드를 생성할 때마다 통합테스트 진행**

---

## 6) 브랜치, 커밋, PR 규칙

### 6.1 브랜치 전략

* **main**: 운영 배포용 브랜치
* **dev**: 통합 개발 브랜치
* 기능 개발은 dev에서 분기한 **topic 브랜치**에서 수행
* PR 머지는 반드시 **Squash and Merge** 방식 사용

### 6.2 브랜치 명명 규칙

* **기능 개발**: `도메인/기능` 또는 `도메인/상세페이지`

    * 예시: `post/create`, `board/write`, `user/update`
* **버그 수정**: `fix/도메인/기능`

    * 예시: `fix/posts/delete`, `fix/users/update`

### 6.3 커밋 메시지

* 메시지는 **한국어로 간결하고 명확하게 작성**
* 필요 시 특정 기술 용어만 영어 사용 가능
* 의미 없는 메시지 금지
* 예시:

    * `게시글 작성 API 구현`
    * `fix: 댓글 삭제 시 권한 검증 추가`
