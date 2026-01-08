---
name: "\U0001F680 Deploy"
about: develop → main 배포(서버 반영)를 위한 체크리스트 이슈 템플릿입니다.
title: "[deploy]"
labels: ''
assignees: bsh-ko

---

### 🚀 배포 목적
- 

### 📌 포함 범위
- 포함 이슈/PR:
  - #
  - #

### ✅ 배포 전 체크리스트
- [ ] develop 최신 상태 확인
- [ ] 핵심 API smoke test 완료
- [ ] `./gradlew test` 또는 `./gradlew build` 성공
- [ ] 환경변수/Secret 설정 확인

### 🔀 병합 계획
- [ ] develop → main PR 생성 (파트장)
- [ ] PR 제목 `[deploy] vX.Y.Z`

### ✅ 배포 후 확인
- [ ] 주요 API 정상 동작 확인
- [ ] 서버 로그/에러 확인
- [ ] 필요 시 태그/롤백 정리

### 📝 비고
- 특이사항 기록
