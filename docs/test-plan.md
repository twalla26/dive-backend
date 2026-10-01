# Test 전략

## 목표

* **회귀 방지**: 리팩토링을 자주 하는 프로젝트이므로, 비즈니스 규칙이 깨지면 바로 드러나게 한다.
* **빠른 피드백**: DB나 스프링 컨텍스트 없이 돌아가는 단위 테스트를 기본으로 둔다.

## 테스트 레벨

| 레벨           | 대상                  | 방식                                  |
|:-------------|:--------------------|:------------------------------------|
| **단위 테스트**   | Service 계층의 비즈니스 로직 | Repository를 `mockk`로 대체, 컨텍스트 로딩 없음 |
| **컨텍스트 테스트** | 빈 등록 및 설정 오류        | `@SpringBootTest` + `contextLoads`  |
| **수동 테스트**   | 실제 API 요청/응답        | Postman                             |

컨트롤러·Repository 슬라이스 테스트(`@WebMvcTest`, `@DataJpaTest`)는 아직 작성하지 않았습니다. 현재 컨트롤러는 DTO 변환과 위임만 담당하여 검증할 로직이 적고,
Repository는 대부분 Spring Data가 생성한 쿼리 메서드이기 때문입니다.

## 사용 도구

* **JUnit 5**: 테스트 실행 및 `@Nested` / `@DisplayName` 구조화
* **MockK**: Kotlin 전용 모킹 (`every`, `verify`)
* **AssertJ**: `assertThat`, `assertThatThrownBy`
* **ReflectionTestUtils**: 엔티티의 `id`처럼 JPA가 채우는 값을 테스트에서 주입

## 작성 규칙

* **구조**: 메서드 단위로 `@Nested` 클래스를 두고, 그 안에 상황별 테스트를 모읍니다.
* **네이밍**: 메서드명은 `대상메서드_상황`, `@DisplayName`은 기대 동작을 한국어 문장으로 작성합니다.
* **본문**: `// given` → `// when` → `// then` 주석으로 구간을 나눕니다.
* **픽스처**: `createUser()`, `createPost()` 같은 private 헬퍼로 테스트 데이터를 만듭니다.
* **검증**: 반환값(`assertThat`)뿐 아니라 **호출 여부**(`verify(exactly = 0 or 1)`)도 확인합니다. "저장하지 않고 조기 반환한다"는 규칙은 호출 횟수로만 검증할 수
  있습니다.

## 우선 검증 대상

비즈니스 규칙 중 **분기가 있고 실수하기 쉬운 지점**을 먼저 테스트합니다.

* **권한 검증**: 작성자만 수정/삭제 가능, 본인 게시글 좋아요 불가
* **멱등성**: 중복 좋아요 / 좋아요하지 않은 글 취소 시 카운트가 틀어지지 않는지
* **동시성 방어**: `DataIntegrityViolationException`(중복 삽입), 삭제된 row 수 0인 경우
* **Soft Delete**: 삭제된 리소스가 조회에서 제외되는지 (`findByIdAndDeletedAtIsNull`)
* **역정규화 카운트**: `likeCount` / `commentCount` 증감 쿼리 호출 여부

## 현재 작성된 테스트

| 파일                            | 범위                                     |
|:------------------------------|:---------------------------------------|
| `DiveBackendApplicationTests` | 스프링 컨텍스트 로딩                            |
| `PostServiceTest`             | 좋아요(`likePost`) / 좋아요 취소(`unlikePost`) |

## 실행 방법

```bash
./gradlew test
```