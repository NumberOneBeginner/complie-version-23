package com.hsbc.greenpacket.util.actions;

import java.util.HashMap;
import java.util.Map;

public class HSBCURLResolverHome {

	/**
	 * Holds the available action strategies.
	 */

	private static class StrategiesHolder {

	}

	private final static Map<String, HSBCURLAction> actionStrategies = new HashMap<String, HSBCURLAction>();
	public static HSBCURLAction resolve(final String url) {
		return HSBCURLResolver.resolve(url,HSBCURLResolverHome.actionStrategies);
	}
}
