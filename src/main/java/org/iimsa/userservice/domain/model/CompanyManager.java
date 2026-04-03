package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CompanyManager {
    @Column(name = "company_id")
    private UUID companyId;

    // 생성자에서 비즈니스 로직에 따른 필드 강제화
    private CompanyManager(UUID companyId) {
        this.companyId = companyId;
    }
}
