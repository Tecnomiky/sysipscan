package net.azib.ipscan.config;

import java.util.Locale;
import java.util.UUID;
import java.util.prefs.Preferences;

/**
 * This class encapsulates preferences of the program.
 * It is a singleton class.
 * 
 * @author Anton Keks
 */
public final class Config {
	private Preferences preferences;
	public String language;
	public String uuid;
	public boolean allowReports;

	/** easily accessible scanner configuration */
	private ScannerConfig scannerConfig;

	Config() {
		preferences = Preferences.userRoot().node("ipscan");
		scannerConfig = new ScannerConfig(preferences);
		language = preferences.get("language", "system");
		uuid = preferences.get("uuid", null);
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
			preferences.put("uuid", uuid);
		}
		allowReports = preferences.getBoolean("allowReports", true);
	}

	private static class ConfigHolder {
		static final Config INSTANCE = new Config();
	}

	public static Config getConfig() {
		return ConfigHolder.INSTANCE;
	}

	public void store() {
		preferences.put("language", language);
		preferences.put("uuid", uuid);
		preferences.putBoolean("allowReports", allowReports);
		scannerConfig.store();
	}

	public Preferences getPreferences() {
		return preferences;
	}

	/** 
	 * @return ScannerConfig instance (quick access)
	 */
	public ScannerConfig forScanner() {
		return scannerConfig;
	}

	public Locale getLocale() {
		if (language == null || "system".equals(language)) {
			return System.getProperty("locale") == null ? Locale.getDefault() : createLocale(System.getProperty("locale"));
		}
		else {
			return createLocale(language);
		}
	}

	private Locale createLocale(String locale) {
		return Locale.forLanguageTag(locale.replace('_', '-'));
	}

	public String getUUID() {
		return uuid;
	}
}
