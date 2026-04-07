package org.iimsa.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.iimsa.userservice.domain.exception.InvalidUserException;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyManager {
    @Column(name = "manage_company_id")
    private UUID companyId;

    private CompanyManager(UUID companyId) {
        this.companyId = companyId;
    }

    public static CompanyManager create(UUID companyId) {
        if (companyId == null) {
            throw new InvalidUserException("업체 ID는 필수입니다.");
        }
        return new CompanyManager(companyId);
    }
}
