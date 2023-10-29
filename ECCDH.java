/*
* Suppose the E and P = (2,7) are used in an ECC Diffie-Hellman key exchange, 
* where Alice chooses the secret value A = 12 and Bob chooses the secret value B = 31.
* What value does Alice send to Bob? What does Bob send to Alice? What is the shared secret? 
* For this problem, write code. Since you may need to apply the mod operator on negative numbers, 
* use the following definition: a mod b = (a % b + b) % b.
*/

package ECC_DiffieHellman;


public class ECCDH {
	
	public static int modInverse(int A, int B) {
		for(int i = 1; i < B; i++) {
			if (((A%B) * (i%B)) % B == 1) {
				return i;
			}
		}
		return 1;
	}
	
	public static int[] pointDouble(int[] points, int a, int mod) {
		// To find lamda, we first need the multiplicative inverse of 2y
		int inv = modInverse(2*points[1], mod);
		// With the inverse, we can find lamda with the equation 3x^2+a*inv
		// All expression are done with mod 37 in mind from the provided curve
		int lamda = ((3 * ((int) Math.pow(points[0], 2)) + a) * inv);
		lamda = (lamda % mod + mod) % mod;
		int newX = (((int) Math.pow(lamda, 2)) - (2 * points[0]));
		newX = (newX % mod + mod) % mod;
		int newY = (lamda * (points[0] - newX) - points[1]);
		newY = (newY % mod + mod) % mod;
		
		points[0] = newX;
		points[1] = newY;
		
		return points;
	}
	
	public static int[] pointAddition(int x1, int x2, int y1, int y2, int mod) {
		int[] newPoints = {0,0};
		
		int inv = modInverse((x2-x1), mod);
		int lamda = (y2-y1) * inv;
		lamda = (lamda % mod + mod) % mod;
		int newX = ((int) Math.pow(lamda,2)) - x1 -x2;
		newX = (newX % mod + mod) % mod;
		int newY = (lamda * (x1 - newX)) - y1;
		newY = (newY % mod + mod) % mod;
		
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
		
		for(int i = 0; i < 2; i++) {
			A4points = pointDouble(A4points, a, mod);
			A8points = pointDouble(A8points, a, mod);
		}
		A8points = pointDouble(A8points, a, mod);
		int[] ASentPoints = {0,0};
		ASentPoints = pointAddition(A4points[0], A8points[0], A4points[1], A8points[1], mod);
		
		System.out.println("Alice sends: (" + ASentPoints[0] + ", " + ASentPoints[1] + ")");
		
		// B = 31 -> 16B + 8B + 4B + 2B + 1B
		int B = 31;
		int[] B16points = {2,7};
		int[] B8points = {2,7};
		int[] B4points = {2,7};
		int[] B2points = {2,7};
		int[] B1points = {2,7};
		
		for(int i = 0; i < 4; i++) {
			B16points = pointDouble(B16points, a, mod);
		}
		for(int i = 0; i < 3; i++) {
			B8points = pointDouble(B8points, a, mod);
		}
		for(int i = 0; i < 2; i++) {
			B4points = pointDouble(B4points, a, mod);
		}
		B2points = pointDouble(B2points, a, mod);
		
		// The arrays are added in such a way that the smaller x value is placed in front
		int[] B24points = pointAddition(B16points[0], B8points[0], B16points[1], B8points[1], mod);
		int[] B28points = pointAddition(B24points[0], B4points[0], B24points[1], B4points[1], mod);
		int[] B30points = pointAddition(B2points[0], B28points[0], B2points[1], B28points[1], mod);
		int[] BSentPoints = pointAddition(B1points[0], B30points[0], B1points[1], B30points[1], mod);

		System.out.println("Bob sends: (" + BSentPoints[0] + ", " + BSentPoints[1] + ")");
		
		// A = 12 -> 8A + 4A
		A8points[0] = BSentPoints[0];
		A8points[1] = BSentPoints[1];
		A4points[0] = BSentPoints[0];
		A4points[1] = BSentPoints[1];
				
		for(int i = 0; i < 2; i++) {
			A4points = pointDouble(A4points, a, mod);
			A8points = pointDouble(A8points, a, mod);
		}
		A8points = pointDouble(A8points, a, mod);
		int[] ASharedPoints = {0,0};
		ASharedPoints = pointAddition(A4points[0], A8points[0], A4points[1], A8points[1], mod);
				
		System.out.println("Alice computes shared key: (" + ASharedPoints[0] + ", " + ASharedPoints[1] + ")");
	
		// B = 31 -> 16B + 8B + 4B + 2B + 1B
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
		
		for(int i = 0; i < 4; i++) {
			B16points = pointDouble(B16points, a, mod);
		}
		for(int i = 0; i < 3; i++) {
			B8points = pointDouble(B8points, a, mod);
		}
		for(int i = 0; i < 2; i++) {
			B4points = pointDouble(B4points, a, mod);
		}
		B2points = pointDouble(B2points, a, mod);
		
		// The arrays are added in such a way that the smaller x value is placed in front
		B24points = pointAddition(B8points[0], B16points[0], B8points[1], B16points[1], mod);
		B28points = pointAddition(B4points[0], B24points[0], B4points[1], B24points[1], mod);
		B30points = pointAddition(B2points[0], B28points[0], B2points[1], B28points[1], mod);
		int[] BSharedPoints = pointAddition(B30points[0], B1points[0], B30points[1], B1points[1], mod);

		System.out.println("Bob computes shared key: (" + BSharedPoints[0] + ", " + BSharedPoints[1] + ")");
		if(ASharedPoints[0] == BSharedPoints[0] && ASharedPoints[1] == BSharedPoints[1]) {
			System.out.println("Shared key established!");
		}
	}
}
