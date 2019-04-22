import java.util.*;
import java.math.BigInteger;

// Test driver that does the calculations with teh BigInteger class and compares the results to the LargeInteger class.
public class LargetIntegerTest {
    public static Random r = new Random();

    public static void main(String args[]) {
        System.out.println("Testing the LargeInteger class...");
        test_addition();
        test_subtraction();
        test_mult_zero();
        test_mult_one();
        test_mult_any();
        test_mult_neg();
        test_xgcd_zero();
        test_xgcd_same();
        test_xgcd_any();
        test_modulo();
        test_divsion();
        test_expmod();
    }

    private static void test_addition() {
        System.out.println("++++++++++++++ ADDITION TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.add(li2);
        System.out.println("This is the result of the add: " + new BigInteger(result.getVal()));

        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).add(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the addition...");
        }
        System.out.println("\n\n");
    }

    private static void test_subtraction() {
        System.out.println("++++++++++++++ SUBTRACTION TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.subtract(li2);
        System.out.println("This is the result of the subtraction: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).subtract(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the subtraction...");
        }
        System.out.println("\n\n");
    }

    private static void test_mult_zero() {
        System.out.println("++++++++++++++ MULTIPLY BY ZERO TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        byte[] zero = {0};
        LargeInteger li2 = new LargeInteger(zero);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.multiply(li2);
        System.out.println("This is the result of the multiplying by zero: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).multiply(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the multiplying by zero...");
        }
        System.out.println("\n\n");
    }

    private static void test_mult_one() {
        System.out.println("++++++++++++++ MULTIPLY BY ONE TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        byte[] one = {1};
        LargeInteger li2 = new LargeInteger(one);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.multiply(li2);
        System.out.println("This is the result of the multiplying by one: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).multiply(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the multiplying by one...");
        }
        System.out.println("\n\n");
    }

    public static void test_mult_neg() {
        System.out.println("++++++++++++++ MULTIPLY NEGATIVE TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        li1 = li1.negate();
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));

        LargeInteger result = li1.multiply(li2);
        System.out.println("This is the result of the multiplying by a negative: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).multiply(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the multiplying by a negative...");
        }
        System.out.println("\n\n");
    }

    public static void test_mult_any() {
        System.out.println("++++++++++++++ MULTIPLY RANDOM TEST ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));

        LargeInteger result = li1.multiply(li2);
        System.out.println("This is the result of the multiplying by any number: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).multiply(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the multiplying...");
        }
        System.out.println("\n\n");
    }

    public static void test_xgcd_zero() {
        System.out.println("++++++++++++++ XGCD ZERO ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        byte[] zero = {0};
        LargeInteger li2 = new LargeInteger(zero);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger[] result = li1.XGCD(li2);
        System.out.println("This is the result of XGCD where li1 is 0: " + new BigInteger(result[0].getVal()) + ", " +
                new BigInteger(result[1].getVal()) + ", " + new BigInteger(result[2].getVal()));
        System.out.println("\n\n");
    }

    public static void test_xgcd_same() {
        System.out.println("++++++++++++++ XGCD OF EQUAL NUMBERS ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        System.out.println("This is the second int: " + new BigInteger(li1.getVal()));
        LargeInteger[] result = li1.XGCD(li1);
        System.out.println("This is the result of XGCD: " + new BigInteger(result[0].getVal()) + ", " +
                new BigInteger(result[1].getVal()) + ", " + new BigInteger(result[2].getVal()));
        System.out.println("\n\n");
    }

    public static void test_xgcd_any() {
        System.out.println("++++++++++++++ XGCD OF ANY NUMBERS ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger[] result = li1.XGCD(li2);
        System.out.println("This is the result of XGCD: " + new BigInteger(result[0].getVal()) + ", " +
                new BigInteger(result[1].getVal()) + ", " + new BigInteger(result[2].getVal()));
        System.out.println("\n\n");
    }

    public static void test_modulo() {
        System.out.println("++++++++++++++ MODULO ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.modulo(li2, false);
        System.out.println("This is the result of modulo: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).mod(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the modulo...");
        }
        System.out.println("\n\n");
    }

    public static void test_divsion() {
        System.out.println("++++++++++++++ DIVISION ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.modulo(li2, true);
        System.out.println("This is the result of dividing: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).divide(new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the division...");
        }
        System.out.println("\n\n");
    }

    public static void test_expmod() {
        System.out.println("++++++++++++++ EXP MOD ++++++++++++++");
        LargeInteger li1 = new LargeInteger(512, r);
        System.out.println("This is the first int: " + new BigInteger(li1.getVal()));
        LargeInteger li2 = new LargeInteger(512, r);
        System.out.println("This is the second int: " + new BigInteger(li2.getVal()));
        LargeInteger result = li1.modularExp(li1, li2);
        System.out.println("This is the result of expmod: " + new BigInteger(result.getVal()));
        if (!new BigInteger(result.getVal()).equals(new BigInteger(li1.getVal()).modPow(new BigInteger(li1.getVal()),
                new BigInteger(li2.getVal())))) {
            System.out.println("There was a problem with the expmod...");
        }
        System.out.println("\n\n");
    }

}
