/*
 * COPYRIGHT. HSBC HOLDINGS PLC 2011. ALL RIGHTS RESERVED.
 *
 * This software is only to be used for the purpose for which it has been
 * provided. No part of it is to be reproduced, disassembled, transmitted,
 * stored in a retrieval system nor translated in any human or computer
 * language in any way or for any other purposes whatsoever without the
 * prior written consent of HSBC Holdings plc.
 */
package com.hsbc.greenpacket.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class for determining if a give version string is in a given range.
 * 
 */
public class VersionComparator {

	/**
	 * Maximum number of version indices.
	 */
	private final static int MAX_VERSION_INDICES = 3;
	/**
	 * A regex that will capture the major, minor, and maintenance versions in groups 1,2,3 respectively.
	 */
	private final static String CAPTURE_VERSION_DIGITS = "(\\d+)(?:\\.(\\d+))?(?:\\.(\\d+))?";
	/**
	 * A non-capturing regex that will match a string defined as x.y.z, where x.y.z are integers.
	 */
	private final static String CAPTURE_ENTIRE_VERSION = "\\d+(?:\\.\\d+)?(?:\\.\\d+)?";

	/**
	 * Patterns for version checking.
	 */
	private final static Pattern SPECIFIC_VERSION = Pattern.compile("^" + VersionComparator.CAPTURE_VERSION_DIGITS + "$");

	private final static Pattern VERSION_RANGE = Pattern.compile("^(\\[|\\()(" + VersionComparator.CAPTURE_ENTIRE_VERSION + "),("
			+ VersionComparator.CAPTURE_ENTIRE_VERSION + ")(\\]|\\))$");
	/**
	 * Group ID's for sections of a version range.
	 */
	private final static int LOWER_CLOSURE = 1; // [ or (
	private final static int LOWER_VERSION = 2; // x1.y1.z1
	private final static int UPPER_VERSION = 3; // x2.y2.z2
	private final static int UPPER_CLOSURE = 4; // ] or )

	public VersionComparator() {

	}

	/**
	 * 
	 * @param versionToCheck
	 *            A version string of the format x.y.z, x.y, x.
	 * @param versionToCheckAgainst
	 *            Either a version string as defined above, or a version ranged defined as <br>
	 *            {( or [}x1.y1.z1,x2,y2,z3{] or )} where ( and [ are exclusive/inclusive closures<br>
	 *            for example [1.0.0,2.0.0) matches version from 1.0.0 and up to but not including 2.0.0. If this param is a version string
	 *            with no bounds set (1.0.0) it specifies that exact version only.
	 * 
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to (or in the range of), or greater
	 *         than the second.
	 */
	public int compareVersion(final String versionToCheck, final String versionToCheckAgainst) {
		if (!isSpecific(versionToCheck)) {
			throw new IllegalArgumentException(String.format("Version to check is incorrectly formatted [%s]", versionToCheck));
		}

		Matcher versionMatcher = VersionComparator.VERSION_RANGE.matcher(versionToCheckAgainst);
		if (versionMatcher.find()) {
			String lowerClosure = versionMatcher.group(VersionComparator.LOWER_CLOSURE);
			String lowerVersion = versionMatcher.group(VersionComparator.LOWER_VERSION);
			String upperVersion = versionMatcher.group(VersionComparator.UPPER_VERSION);
			String upperClosure = versionMatcher.group(VersionComparator.UPPER_CLOSURE);
			int lowerComparison = compareSpecificVersions(versionToCheck, lowerVersion);
			int upperComparison = compareSpecificVersions(versionToCheck, upperVersion);
			// Below lower bound
			if (lowerComparison < 0 && isInclusiveClosure(lowerClosure) || lowerComparison <= 0 && !isInclusiveClosure(lowerClosure)) {
				return -1;
			}
			// Above upper bound
			else if (upperComparison > 0 && isInclusiveClosure(upperClosure) || upperComparison >= 0 && !isInclusiveClosure(upperClosure)) {
				return 1;
			}
			// Match
			else {
				return 0;
			}

		}
		else {
			return compareSpecificVersions(versionToCheck, versionToCheckAgainst);
		}
	}

	/**
	 * Checks if a given version string specifies a specific version.
	 * 
	 * @param versionString
	 * @return
	 */
	private boolean isSpecific(final String versionString) {
		Matcher versionRangeMatcher = VersionComparator.SPECIFIC_VERSION.matcher(versionString);
		return versionRangeMatcher.matches();
	}

	/**
	 * Converts a version string (x,y,z) to a int array containing those values. Any values not present in the version string will be
	 * considered to be 0.
	 * 
	 * @param version
	 * @return
	 */
	private int[] parseVersion(final String version) {
		String[] versionValuesAsStrings = version.split("\\.");
		int[] versionValuesAsInts = { 0, 0, 0 };
		for (int i = 0; i < versionValuesAsStrings.length; i++) {
			versionValuesAsInts[i] = Integer.valueOf(versionValuesAsStrings[i]).intValue();
		}
		return versionValuesAsInts;
	}

	/**
	 * Compares two version strings.
	 * 
	 * @param version1
	 * @param version2
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 */
	private int compareSpecificVersions(final String version1, final String version2) {
		int[] version1AsInts = parseVersion(version1);
		int[] version2AsInts = parseVersion(version2);
		for (int i = 0; i < VersionComparator.MAX_VERSION_INDICES; i++) {
			if (version1AsInts[i] == version2AsInts[i]) {
				continue;
			}
			return version1AsInts[i] - version2AsInts[i];
		}
		return 0;
	}

	/**
	 * Asserts if a version range closure indicates inclusive or exclusive.
	 * 
	 * @param closure
	 * @return
	 */
	private boolean isInclusiveClosure(final String closure) {
		return closure.matches("\\[|\\]");
	}
}
