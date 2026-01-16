package com.example.pomodori.service;

import org.springframework.stereotype.Service;

import com.example.pomodori.entity.Admin;
import com.example.pomodori.entity.ReportSetting;
import com.example.pomodori.repository.ReportSettingRepository;

@Service
public class ReportSettingService {

	private final ReportSettingRepository settingRepository;

	public ReportSettingService(ReportSettingRepository settingRepository) {
		this.settingRepository = settingRepository;
	}

	public void firstSaveReportSettings(Admin admin) {
		ReportSetting setting = settingRepository.findById(1L).orElse(null);
		if (setting == null) {
			ReportSetting defaultSetting = new ReportSetting();
			defaultSetting.setFrequency("daily");
			defaultSetting.setAdminEmail(admin.getEmail());
			defaultSetting.setTimeoutMinutes(15);
			settingRepository.save(defaultSetting);
		} else if (setting.getAdminEmail() == null || setting.getAdminEmail().isBlank()) {
			setting.setAdminEmail(admin.getEmail());
			settingRepository.save(setting);
		}
	}
}
