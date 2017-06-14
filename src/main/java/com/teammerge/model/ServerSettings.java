package com.teammerge.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Server settings represents the settings of the Gitblit server including all
 * setting metadata such as name, current value, default value, description, and
 * directives. It is a model class for serialization and presentation, but not
 * for persistence.
 *
 * @author James Moger
 */
public class ServerSettings implements Serializable {

	private final Map<String, SettingModel> settings;

	private static final long serialVersionUID = 1L;

	public List<String> pushScripts;

	public ServerSettings() {
		settings = new TreeMap<String, SettingModel>();
	}

	public List<String> getKeys() {
		return new ArrayList<String>(settings.keySet());
	}

	public void add(SettingModel setting) {
		if (setting != null) {
			settings.put(setting.name, setting);
		}
	}

	public SettingModel get(String key) {
		return settings.get(key);
	}

	public boolean hasKey(String key) {
		return settings.containsKey(key);
	}

	public SettingModel remove(String key) {
		return settings.remove(key);
	}
}
