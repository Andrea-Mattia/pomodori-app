package com.example.pomodori.repository;

import com.example.pomodori.entity.ScanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScanRecordRepository extends JpaRepository<ScanRecord, Long>, JpaSpecificationExecutor<ScanRecord> {
}

