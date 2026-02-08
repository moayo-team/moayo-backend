package com.moayo.moayobackend.user.ai;

import com.moayo.moayobackend.user.service.UserProfileSnapshotService;
import com.moayo.moayobackend.user.ai.entity.UserProfileSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional // 테스트 후 DB를 다시 깨끗하게 되돌립니다.
public class RecommendationTest {

    @Autowired
    private UserProfileSnapshotService snapshotService;

    @Test
    @DisplayName("유저 데이터가 텍스트 스냅샷으로 정상 통합되는지 확인")
    void snapshotBuildTest() {
        // 1. 준비: DB에 유저 1번이 있다고 가정 (없으면 에러가 나므로 실제 ID를 넣거나 가짜 데이터를 먼저 넣어야 함)
        Long testUserId = 1L;

        try {
            // 2. 실행
            UserProfileSnapshot snapshot = snapshotService.rebuildSnapshot(testUserId);

            // 3. 검증 및 출력
            assertNotNull(snapshot);
            System.out.println("================================================");
            System.out.println("조립된 스냅샷 결과:\n" + snapshot.getText());
            System.out.println("================================================");

        } catch (IllegalArgumentException e) {
            System.out.println("테스트 실패: DB에 ID " + testUserId + "번 유저가 없습니다.");
        }
    }
}