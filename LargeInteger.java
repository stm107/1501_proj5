import java.util.Random;
import java.math.BigInteger;

public class LargeInteger {

    private final byte[] ONE = {(byte) 1};

    private byte[] val;

    /**
     * Construct the LargeInteger from a given byte array
     * @param b the byte array that this LargeInteger should represent
     */
    public LargeInteger(byte[] b) {
        val = b;
    }

    /**
     * Construct the LargeInteger by generatin a random n-bit number that is
     * probably prime (2^-100 chance of being composite).
     * @param n the bitlength of the requested integer
     * @param rnd instance of java.util.Random to use in prime generation
     */
    public LargeInteger(int n, Random rnd) {
        val = BigInteger.probablePrime(n, rnd).toByteArray();
    }

    /**
     * Return this LargeInteger's val
     * @return val
     */
    public byte[] getVal() {
        return val;
    }

    /**
     * Return the number of bytes in val
     * @return length of the val byte array
     */
    public int length() {
        return val.length;
    }

    /**
     * Add a new byte as the most significant in this
     * @param extension the byte to place as most significant
     */
    public void extend(byte extension) {
        byte[] newv = new byte[val.length + 1];
        newv[0] = extension;
        for (int i = 0; i < val.length; i++) {
            newv[i + 1] = val[i];
        }
        val = newv;
    }

    /**
     * If this is negative, most significant bit will be 1 meaning most
     * significant byte will be a negative signed number
     * @return true if this is negative, false if positive
     */
    public boolean isNegative() {
        return (val[0] < 0);
    }

    /**
     * Computes the sum of this and other
     * @param other the other LargeInteger to sum with this
     */
    public LargeInteger add(LargeInteger other) {
        byte[] a, b;
        // If operands are of different sizes, put larger first ...
        if (val.length < other.length()) {
            a = other.getVal();
            b = val;
        }
        else {
            a = val;
            b = other.getVal();
        }

        // ... and normalize size for convenience
        if (b.length < a.length) {
            int diff = a.length - b.length;

            byte pad = (byte) 0;
            if (b[0] < 0) {
                pad = (byte) 0xFF;
            }

            byte[] newb = new byte[a.length];
            for (int i = 0; i < diff; i++) {
                newb[i] = pad;
            }

            for (int i = 0; i < b.length; i++) {
                newb[i + diff] = b[i];
            }

            b = newb;
        }

        // Actually compute the add
        int carry = 0;
        byte[] res = new byte[a.length];
        for (int i = a.length - 1; i >= 0; i--) {
            // Be sure to bitmask so that cast of negative bytes does not
            //  introduce spurious 1 bits into result of cast
            carry = ((int) a[i] & 0xFF) + ((int) b[i] & 0xFF) + carry;

            // Assign to next byte
            res[i] = (byte) (carry & 0xFF);

            // Carry remainder over to next byte (always want to shift in 0s)
            carry = carry >>> 8;
        }

        LargeInteger res_li = new LargeInteger(res);

        // If both operands are positive, magnitude could increase as a result
        //  of addition
        if (!this.isNegative() && !other.isNegative()) {
            // If we have either a leftover carry value or we used the last
            //  bit in the most significant byte, we need to extend the result
            if (res_li.isNegative()) {
                res_li.extend((byte) carry);
            }
        }
        // Magnitude could also increase if both operands are negative
        else if (this.isNegative() && other.isNegative()) {
            if (!res_li.isNegative()) {
                res_li.extend((byte) 0xFF);
            }
        }

        // Note that result will always be the same size as biggest input
        //  (e.g., -127 + 128 will use 2 bytes to store the result value 1)
        return res_li;
    }

    /**
     * Negate val using two's complement representation
     * @return negation of this
     */
    public LargeInteger negate() {
        byte[] neg = new byte[val.length];
        int offset = 0;

        // Check to ensure we can represent negation in same length
        //  (e.g., -128 can be represented in 8 bits using two's
        //  complement, +128 requires 9)
        if (val[0] == (byte) 0x80) { // 0x80 is 10000000
            boolean needs_ex = true;
            for (int i = 1; i < val.length; i++) {
                if (val[i] != (byte) 0) {
                    needs_ex = false;
                    break;
                }
            }
            // if first byte is 0x80 and all others are 0, must extend
            if (needs_ex) {
                neg = new byte[val.length + 1];
                neg[0] = (byte) 0;
                offset = 1;
            }
        }

        // flip all bits
        for (int i  = 0; i < val.length; i++) {
            neg[i + offset] = (byte) ~val[i];
        }

        LargeInteger neg_li = new LargeInteger(neg);

        // add 1 to complete two's complement negation
        return neg_li.add(new LargeInteger(ONE));
    }

    /**
     * Implement subtraction as simply negation and addition
     * @param other LargeInteger to subtract from this
     * @return difference of this and other
     */
    public LargeInteger subtract(LargeInteger other) {
        return this.add(other.negate());
    }

    /**
     * Compute the product of this and other
     * @param other LargeInteger to multiply by this
     * @return product of this and other
     */
    public LargeInteger multiply(LargeInteger other) {
        LargeInteger a, b;
        a = this;
        b = other;

        // decide if the answer will be positive or negative.
        boolean positive = a.isNegative() == b.isNegative();

        // multiply the positive numbers then flip the sign if necessary
        if (a.isNegative()) {
            a = a.negate();
        }
        if (b.isNegative()) {
            b = b.negate();
        }

        // Make a byte array big enough for result and carry
        int total_length = a.length() + b.length();
        LargeInteger result = new LargeInteger(new byte[total_length]);

        // start from the little end!
        for (int i = b.length() - 1; i >= 0; i--) {
            // 8 bits in a byte!
            for (int j = 0; j < 8; j++) {
                // get the bit value that we're multiplying by
                // if it's a 1, add and shift
                if ((b.getVal()[i] >> j & 1) == 1) {
                    result = result.add(a);
                    a = a.shift_left();
                }
                // if it's a 0, just shift, don't add!
                else {
                    a = a.shift_left();
                }
            }
        }

        if (positive) {
            return result.trim_leading_zeros();
        } else {
            return result.negate().trim_leading_zeros();
        }
    }

    /**
     * Run the extended Euclidean algorithm on this and other
     * @param other another LargeInteger
     * @return an array structured as follows:
     *   0:  the GCD of this and other
     *   1:  a valid x value
     *   2:  a valid y value
     * such that this * x + other * y == GCD in index 0
     */
    public LargeInteger[] XGCD(LargeInteger other) {
        LargeInteger[] gcd_arr = new LargeInteger[3];
        byte[] zero = {0};
        boolean zero_r = true;

        // handle the base case
        for (int i = 0; i < other.length(); i++) {
            if (other.getVal()[i] != 0) {
                zero_r = false;
            }
        }
        if (zero_r) {
            gcd_arr[0] = this;
            gcd_arr[1] = new LargeInteger(ONE);
            gcd_arr[2] = new LargeInteger(zero);
            return gcd_arr;
        }

        LargeInteger[] recurse = other.XGCD(this.modulo(other, false));
        gcd_arr[0] = recurse[0];
        gcd_arr[1] = recurse[2];
        gcd_arr[2] = recurse[1].subtract(this.modulo(other, true).multiply(recurse[2]));
        return gcd_arr;

    }

    /**
     * Compute the result of raising this to the power of y mod n
     *
     * @param y exponent to raise this to
     * @param n modulus value to use
     * @return this^y mod n
     */
    public LargeInteger modularExp(LargeInteger y, LargeInteger n) {
        // make copies of everything you manipulate!
        byte[] power = new byte[y.length()];
        for (int i = 0; i < y.length(); i++) {
            power[i] = y.getVal()[i];
        }

        // check if y is negative 1.
        LargeInteger negative_one = new LargeInteger(ONE).negate();
        boolean inverse = true;
        for (int i = 0; i < y.length(); i++) {
            if (y.getVal()[i] != negative_one.getVal()[i]) {
                inverse = false;
                break;
            }
        }
        // Handle the special case for inverse more efficiently
        // This is from the crypto slides.
        if (inverse) {
            LargeInteger[] xgcd = n.XGCD(this);
            return xgcd[2].isNegative() ? n.add(xgcd[2]) : xgcd[2];
        }
        // Start at 1
        LargeInteger result = new LargeInteger(ONE);

        // look at every bit of every byte of the exponent
        for (int i = 0; i < power.length; i++) {
            for (int j = 0; j < 8; j++) {
                result = result.multiply(result).modulo(n, false);
                if (((power[i] >> 7 - j) & 1) == 1) {
                    result = result.multiply(this).modulo(n, false);
                }
            }
        }
        return result;
    }

    //////////////////////////////////////// HELPER METHODS ////////////////////////////////////////

    // This does division and returns the result if div is true, and returns the remainder if div is false
    public LargeInteger modulo(LargeInteger other, boolean div) {
        byte[] zero = {0};
        LargeInteger a, b, result, one;
        a = this;
        b = other;
        result = new LargeInteger(zero);
        one = new LargeInteger(ONE);

        // decide if the answer will be positive or negative.
        boolean positive = a.isNegative() == b.isNegative();
        if (a.isNegative()) {
            a = a.negate();
        }
        if (b.isNegative()) {
            b = b.negate();
        }

        // keep track of how much you shift b
        int shift_amt = 0;
        while (!a.subtract(b).isNegative()) {
            shift_amt++;
            b = b.shift_left();
        }
        b = b.shift_right();

        for (int i = 0; i < shift_amt; i++) {
            // do the first shift
            result = result.shift_left();
            if (!a.subtract(b).isNegative()) {
                result = result.add(one);
                a = a.subtract(b);
            }
            b = b.shift_right();
        }

        if (div) {
            return positive ? result.trim_leading_zeros() : result.negate().trim_leading_zeros();
        }
        return this.subtract(other.multiply(result)).trim_leading_zeros();

    }

    public LargeInteger shift_left() {

        byte[] shifted;
        boolean extra_bit = false;

        // check if msb is a 1. If it is, we'll need to add a byte for shifting that 1
        boolean add_byte = (Integer.toBinaryString(val[0]).charAt(0) == '1');

        // You NEED to make a copy or your recursion will be f'd. It took my like 2 hours to figure that out :).
        if (add_byte) {
            //byte[] bigger = new byte[shifted.length+1];
            shifted = new byte[this.length() + 1];
            for (int i = 0; i < this.length(); i++) {
                shifted[i + 1] = val[i];
            }
        } else {
            shifted = new byte[this.length()];
            for (int i = 0; i < this.length(); i++) {
                shifted[i] = val[i];
            }
        }
        for (int i = shifted.length - 1; i >= 0; i--) {
            byte msb = (byte) (shifted[i] & 0x80);
            shifted[i] <<= 1;
            if (extra_bit) {
                shifted[i] |= 1;
            }
            extra_bit = msb != 0;
        }

        return new LargeInteger(shifted);
    }

    public LargeInteger shift_right() {
        // Set it up just like shift_left()
        byte[] shifted = new byte[this.length()];
        for (int i = 0; i < this.length(); i++) {
            shifted[i] = val[i];
        }

        boolean extra_bit = false;

        for (int i = 0; i < shifted.length; i++) {
            // This time, we & by 1 instead of 0x80 because we're looking for the lsb not the msb.
            byte lsb = (byte) (shifted[i] & 1);
            // Shift and & with 11111110
            shifted[i] = (byte) ((shifted[i] >> 1) & 0x7f);
            if (extra_bit) {
                shifted[i] |= 0x80;
            }
            extra_bit = lsb != 0;
        }

        return new LargeInteger(shifted).trim_leading_zeros();
    }

    public LargeInteger trim_leading_zeros() {
        byte[] trimmed;
        byte curr = val[0];
        int num_zeros = 0;
        // if the whole byte is zero, add 8 bits
        for (int i = 1; i < val.length; i++) {
            if (curr == 0) {
                num_zeros += 8;
                curr = val[i];
            }
        }
        // if the whole byte isn't 0, look for any leading bits that are.
        for (int i = 7; i >= 0; i--) {
            if ((curr >> i) == 0) {
                num_zeros++;
            } else {
                break;
            }
        }
        num_zeros--;
        trimmed = new byte[val.length - (num_zeros / 8)];
        if (num_zeros > 8) {
            for (int i = 0; i < trimmed.length; i++) {
                trimmed[i] = val[i + (num_zeros / 8)];
            }
            return new LargeInteger(trimmed);
        }
        return this;
    }

    public boolean equals(LargeInteger other) {
        if (this.length() != other.length()) {
            return false;
        }
        for (int i = 0; i < this.length(); i++) {
            if (val[i] != other.getVal()[i]) {
                return false;
            }
        }
        return true;
    }
}