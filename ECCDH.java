/*
* Project 3 - CS 430
* Authors:
* Shelby Jorgensen
* Nick Newhard
* Miguel Lima
*/

package ECC_DiffieHellman;


public class ECCDH {
	
	/*
	 * Take the provided value A and a mod value, and find the modular inverse of A
	 */
	public static int modInverse(int A, int mod) {
		for(int i = 1; i < mod; i++) {
			// Formula for modular multiplcative inverse: ab mod n == 1
			// In order to properly apply the mod in this formula, mod is applied at each step
			if (((A % mod) * (i % mod)) % mod == 1) {
				return i;
			}
		}
		return 1;
	}
	
	/*
	 * Take in two points, along with the a value from the elliptic curve, along with the mod value of the curve
	 * Return the value of 2P = P + P, or the point double of the provided point
	 */
	public static int[] pointDouble(int[] points, int a, int mod) {
		// To find lamda, we first need the multiplicative inverse of 2y
		int inv = modInverse(2*points[1], mod);
		// With the inverse, we can find lamda with the equation 3x^2+a*inv
		// All expression are done with mod 37 in mind from the provided curve
		int lamda = ((3 * ((int) Math.pow(points[0], 2)) + a) * inv);
		lamda = (lamda % mod + mod) % mod;
		// Our new x value is found by: lamda^2 - 2x
		int newX = (((int) Math.pow(lamda, 2)) - (2 * points[0]));
		newX = (newX % mod + mod) % mod;
		// Our new y value is found by: lamda(x-newX) - y
		int newY = (lamda * (points[0] - newX) - points[1]);
		newY = (newY % mod + mod) % mod;
		
		// Assign our new x and y values
		points[0] = newX;
		points[1] = newY;
		
		return points;
	}
	
	/*
	 * Take in two sets of points, as well as the modular value of the curve
	 * Return the new set of points found by adding the tow provided points
	 */
	public static int[] pointAddition(int x1, int x2, int y1, int y2, int mod) {
		int[] newPoints = {0,0};
		// To find lamda, we first need the multiplicative inverse of x2-x1
		int inv = modInverse((x2-x1), mod);
		// With the inverse, we can find lamda with the equation: (y2-y1) * inv
		// All expression are done with mod 37 in mind from the provided curve
		int lamda = (y2-y1) * inv;
		lamda = (lamda % mod + mod) % mod;
		// Our new x value is found by: lamda^2 - x1 - x2
		int newX = ((int) Math.pow(lamda,2)) - x1 -x2;
		newX = (newX % mod + mod) % mod;
		// Our new y value is found by: lamda(x1-newX) - y1
		int newY = (lamda * (x1 - newX)) - y1;
		newY = (newY % mod + mod) % mod;
		
		// Assign our new x and y values
		newPoints[0] = newX;
		newPoints[1] = newY;
		
		return newPoints;
	}
	
	public static void main(String[] args) {
		System.out.println("Diffie-Hellmen key Exchange\n");
		
		
		// E = y² = x³ + 11x + 19 (mod 167)
		int a = 11;
		int b = 19;
		int mod = 167;
		int A = 12;
		// A = 12 -> 8A + 4A
		int[] A8points = {2,7};
		int[] A4points = {2,7};
		
		// Each set of points is double two times, which brings both sets of points to 4A
		for(int i = 0; i < 2; i++) {
			A4points = pointDouble(A4points, a, mod);
			A8points = pointDouble(A8points, a, mod);
		}
		// One more double is preformed to get to 8A
		A8points = pointDouble(A8points, a, mod);
		// Variable to hold the final points to be sent by A
		int[] ASentPoints = {0,0};
		// Add together A4 and A8 to get to A12, the desired value of A
		ASentPoints = pointAddition(A4points[0], A8points[0], A4points[1], A8points[1], mod);
		
		System.out.println("Alice sends: (" + ASentPoints[0] + ", " + ASentPoints[1] + ")");
		
		// B = 31 -> 16B + 8B + 4B + 2B + 1B
		int B = 31;
		int[] B16points = {2,7};
		int[] B8points = {2,7};
		int[] B4points = {2,7};
		int[] B2points = {2,7};
		int[] B1points = {2,7};
		
		// 4 sets of point doubling is preformed, which gets us to 16B
		for(int i = 0; i < 4; i++) {
			B16points = pointDouble(B16points, a, mod);
		}
		// 3 sets of point doubling is preformed, which gets us to 8B
		for(int i = 0; i < 3; i++) {
			B8points = pointDouble(B8points, a, mod);
		}
		// 2 sets of point doubling is preformed, which gets us to 16B
		for(int i = 0; i < 2; i++) {
			B4points = pointDouble(B4points, a, mod);
		}
		// 1 point doubling is preformed, which gets us 2B
		B2points = pointDouble(B2points, a, mod);
		
		// The arrays are added in such a way that the smaller x value is placed in front
		// 24B = 16B + 8B -> 28B = 24B + 4B -> 30B = 28B + 2B -> 31B = 30B + 1B
		int[] B24points = pointAddition(B16points[0], B8points[0], B16points[1], B8points[1], mod);
		int[] B28points = pointAddition(B24points[0], B4points[0], B24points[1], B4points[1], mod);
		int[] B30points = pointAddition(B2points[0], B28points[0], B2points[1], B28points[1], mod);
		int[] BSentPoints = pointAddition(B1points[0], B30points[0], B1points[1], B30points[1], mod);

		System.out.println("Bob sends: (" + BSentPoints[0] + ", " + BSentPoints[1] + ")");
		
		// A = 12 -> 8A + 4A
		// All values in each array are updated with the point sent from B
		A8points[0] = BSentPoints[0];
		A8points[1] = BSentPoints[1];
		A4points[0] = BSentPoints[0];
		A4points[1] = BSentPoints[1];
			
		// Each set of points is double two times, which brings both sets of points to 4A
		for(int i = 0; i < 2; i++) {
			A4points = pointDouble(A4points, a, mod);
			A8points = pointDouble(A8points, a, mod);
		}
		// One more double is preformed to get to 8A
		A8points = pointDouble(A8points, a, mod);
		// Variable to hold the final shared points of A
		int[] ASharedPoints = {0,0};
		// Add together A4 and A8 to get to A12, the desired value of A
		ASharedPoints = pointAddition(A4points[0], A8points[0], A4points[1], A8points[1], mod);
				
		System.out.println("Alice computes shared key: (" + ASharedPoints[0] + ", " + ASharedPoints[1] + ")");
	
		// B = 31 -> 16B + 8B + 4B + 2B + 1B
		// All values in each array are updated with the point sent from A
		B16points[0] = ASentPoints[0];
		B16points[1] = ASentPoints[1];
		B8points[0] = ASentPoints[0];
		B8points[1] = ASentPoints[1];
		B4points[0] = ASentPoints[0];
		B4points[1] = ASentPoints[1];
		B2points[0] = ASentPoints[0];
		B2points[1] = ASentPoints[1];
		B1points[0] = ASentPoints[0];
		B1points[1] = ASentPoints[1];
		
		// 4 sets of point doubling is preformed, which gets us to 16B
		for(int i = 0; i < 4; i++) {
			B16points = pointDouble(B16points, a, mod);
		}
		// 3 sets of point doubling is preformed, which gets us to 8B
		for(int i = 0; i < 3; i++) {
			B8points = pointDouble(B8points, a, mod);
		}
		// 2 sets of point doubling is preformed, which gets us to 16B
		for(int i = 0; i < 2; i++) {
			B4points = pointDouble(B4points, a, mod);
		}
		// 1 point doubling is preformed, which gets us 2B
		B2points = pointDouble(B2points, a, mod);
		
		// The arrays are added in such a way that the smaller x value is placed in front
		// 24B = 16B + 8B -> 28B = 24B + 4B -> 30B = 28B + 2B -> 31B = 30B + 1B
		B24points = pointAddition(B8points[0], B16points[0], B8points[1], B16points[1], mod);
		B28points = pointAddition(B4points[0], B24points[0], B4points[1], B24points[1], mod);
		B30points = pointAddition(B2points[0], B28points[0], B2points[1], B28points[1], mod);
		int[] BSharedPoints = pointAddition(B30points[0], B1points[0], B30points[1], B1points[1], mod);
		
		// If both A and B were able to compute the same point values, then the shared key is established
		System.out.println("Bob computes shared key: (" + BSharedPoints[0] + ", " + BSharedPoints[1] + ")");
		if(ASharedPoints[0] == BSharedPoints[0] && ASharedPoints[1] == BSharedPoints[1]) {
			System.out.println("Shared key established!");
		}
	}
}
