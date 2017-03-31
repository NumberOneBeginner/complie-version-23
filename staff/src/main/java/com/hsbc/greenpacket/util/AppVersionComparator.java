package com.hsbc.greenpacket.util;



/**
 * A utility class for updating app version.
 * @author Cherry
 * 2012-11-08
 * 
 */
public class AppVersionComparator {
    
	/**
	 * Converts a version string (x,y,z) to a int array containing those values. 
	 * 
	 * @param version
	 * @return int[]
	 */
	private static int[] parseVersion(final String version) {
		String[] versionValuesAsStrings = version.split("\\.");
		int[] versionValuesAsInts = new int[versionValuesAsStrings.length];
		for (int i = 0; i < versionValuesAsStrings.length; i++) {
			try {// York [2012-11-21]remove the replaceAll("[^0-9.]","") code for align with iOS.
				versionValuesAsInts[i] = Integer.valueOf(versionValuesAsStrings[i].trim()).intValue();
			} catch (Exception e) {
				versionValuesAsInts[i] = 0;
			}
		}
		return versionValuesAsInts;
	}
	
	/**
	 * Judge the number of a version string (x.y.z) contain point. 
	 * 
	 * @param version
	 * @return count
	 */
	private static int pointCount(final String version){
		int count =0;
		char[] ch = version.toCharArray();
		for(int i=0;i<ch.length;i++){
			if(ch[i] == '.'){
				count++;
			}
		}
		return count;
		
	}
	
	/**
	 * Compares two version strings when the length of these two String array is equal.
	 * 
	 * @param newversion
	 * @param oldversion
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 */
	private static int compareSpecificVersions(final String newversion, final String oldversion) {
		int[] newversionAsInts = parseVersion(newversion);
		int[] oldversionAsInts = parseVersion(oldversion);		
		for(int i=0;i<newversionAsInts.length;i++){
			if (newversionAsInts[i] == oldversionAsInts[i]) {
				continue;
			}
			return newversionAsInts[i] - oldversionAsInts[i];
		}

		return 0;
	}
	
	/**
	 * Compares two version strings when the length of these two String array is not equal.
	 * 
	 * @param newversion
	 * @param oldversion
	 * @param pointnum
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
	 */
	private static int compareSpecificVersions(final String newversion, final String oldversion,int pointnum){
		int[] newversionAsInts = parseVersion(newversion);
		int[] oldversionAsInts = parseVersion(oldversion);
		if(pointnum > 0){
			
			for(int i=0;i<oldversionAsInts.length;i++){
				if (newversionAsInts[i] == oldversionAsInts[i]) {
					continue;
				}
				return newversionAsInts[i] - oldversionAsInts[i];
			}
			
			for(int i=oldversionAsInts.length;i<newversionAsInts.length;i++){
				if (newversionAsInts[i] == 0) {
					continue;
				}
				return newversionAsInts[i] - 0;
			}
			
		}else{
			for(int i=0;i<newversionAsInts.length;i++){
				if (newversionAsInts[i] == oldversionAsInts[i]) {
					continue;
				}
				return newversionAsInts[i] - oldversionAsInts[i];
			}
			
			for(int i=newversionAsInts.length;i<oldversionAsInts.length;i++){
				if (oldversionAsInts[i] == 0) {
					continue;
				}
				return 0-oldversionAsInts[i];
			}
		}
		
		return 0;
		
	}
	
	/**
	 * 
	 * @param versionToCheck
	 * @param versionToCheckAgainst
	 * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to (or in the range of), or greater
	 *         than the second.
	 */
	public static int compareVersion(final String versionToCheck, final String versionToCheckAgainst) {
	    /*
         * York [21-Nov-2012]
         * Preventive mechanism to avoid ArrayIndexOutOfBoundsException during 
         * version checking
         * 
         */
        try {
            int newversionpointNum = pointCount(versionToCheck);
            int oldversionpointNum = pointCount(versionToCheckAgainst);
            if (newversionpointNum == oldversionpointNum) {
                return compareSpecificVersions(versionToCheck, versionToCheckAgainst);
            } else {
                int tempnum = newversionpointNum - oldversionpointNum;
                return compareSpecificVersions(versionToCheck, versionToCheckAgainst, tempnum);
            }
        } catch (ArrayIndexOutOfBoundsException exc) {
            /**
             * return 0 to abort upgrade if exception occured during version
             * checking
             **/
            return 0;

        }catch(Exception e){
            /**
             * York [2012-11-21]
             * return 0 to abort upgrade if exception occured during version
             * checking
             **/
            return 0;
        }
	}
}
