package main.cl.dagserver.domain.core;

import java.security.SecureRandom;

public class RandomGenerator  {


	public static String generateRandomString(Integer targetStringLength) {
		SecureRandom random = new SecureRandom();
		int leftLimit = 97; // letter 'a'
	    int rightLimit = 122; // letter 'z'
	    StringBuilder buffer = new StringBuilder(targetStringLength);
	    for (int i = 0; i < targetStringLength; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}


}
