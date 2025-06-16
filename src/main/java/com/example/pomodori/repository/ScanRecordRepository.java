package com.example.pomodori.repository;

import com.example.pomodori.entity.ScanRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRecordRepository extends JpaRepository<ScanRecord, Long> {
}
