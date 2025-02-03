package com.example.Roomy.Report.Repository;

import com.example.Roomy.Report.Entity.Report;
import com.example.Roomy.SocialLogin.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndReported(User reporterId, User reportedId);
}
