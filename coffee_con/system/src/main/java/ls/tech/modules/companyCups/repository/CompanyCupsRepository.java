package ls.tech.modules.companyCups.repository;

import ls.tech.modules.companyCups.domain.CompanyCups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCupsRepository extends JpaRepository<CompanyCups, Integer>, JpaSpecificationExecutor<CompanyCups> {
    // 可以根据需要添加自定义查询方法
}