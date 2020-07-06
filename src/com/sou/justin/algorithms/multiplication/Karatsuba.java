package com.sou.justin.algorithms.multiplication;

import java.util.ArrayDeque;

public class Karatsuba {
    public static boolean test() {
        String answer = "8539734222673567065463550869546574495034888535765114961879601127067743044893204848617875072216249073013374895871952806582723184";
        return answer.equals(multiply("3141592653589793238462643383279502884197169399375105820974944592", "2718281828459045235360287471352662497757247093699959574966967627"));
    }

    public static String multiply(String x1, String x2) {
        String s1 = prependZeros(x1);
        String s2 = prependZeros(x2);
        long maxLength = Math.max(s1.length(), s2.length());

        // Prepend the shorter string until length is same as the longer string
        s1 = prependZeros(s1, maxLength);
        s2 = prependZeros(s2, maxLength);

        int length = s1.length();

        if (length == 1) {
            return multiply(Integer.parseInt(s1), Integer.parseInt(s2));
        }

        String a = s1.substring(0, length / 2);
        String b = s1.substring(length / 2, length);
        String c = s2.substring(0, length / 2);
        String d = s2.substring(length / 2, length);

        String p = add(a, b);
        String q = add(c, d);

        String ac = multiply(a, c);
        String bd = multiply(b, d);
        String pq = multiply(p, q);
        String adbc = subtract(subtract(pq, ac), bd);

        ac = appendZeros(ac, (long) (length));
        adbc = appendZeros(adbc, (long) (length / 2));
        return add(add(ac, adbc), bd);
    }

    private static String subtract(String x1, String x2) {
        return add(x1, "-" + x2);
    }

    private static String add(String x1, String x2) {
        ArrayDeque<Integer> deque = new ArrayDeque<>();
        StringBuilder builder = new StringBuilder();
        int carries = 0;
        int result = 0;
        boolean isSubtraction = false;

        if (x2.startsWith("-")) {
            isSubtraction = true;
            x2 = x2.substring(1);
        }

        String s1 = prependZeros(x1);
        String s2 = prependZeros(x2);
        long maxLength = Math.max(s1.length(), s2.length());

        // Prepend the shorter string until length is same as the longer string
        s1 = prependZeros(s1, maxLength);
        s2 = prependZeros(s2, maxLength);

        for (int i = s1.length() - 1; i >= 0; i--) {
            int num1 = s1.charAt(i) - '0';
            int num2 = s2.charAt(i) - '0';

            if (isSubtraction) {
                num2 *= -1;
            }

            num1 = add(num1, carries);

            // If after adding the carries the number is greater than 10
            // another invocation of add is required to add this two-digits number
            if (num1 > 9) {
                String resultOfMultiDigits = add(String.valueOf(num1), prependZeros(String.valueOf(num2), 2L));
                result = Integer.parseInt(resultOfMultiDigits);
            } else {
                result = add(Math.abs(num1), num2);
            }

            carries = (num1 < 0 || result < 0) ? -1 : (result > 9) ? 1 : 0;
            deque.push(isSubtraction ? Math.abs(result) : result % 10);
        }

        // Add the carries
        if (carries != 0) {
            deque.push(carries);
        }

        // Pop all the leading zeros
        while (deque.size() > 1 && deque.peek() == 0) {
            deque.pop();
        }

        while (!deque.isEmpty()) {
            builder.append(deque.pop());
        }

        return builder.toString();
    }

    private static int add(int x1, int x2) {
        if (x1 / 10 != 0 || x2 / 10 != 0) {
            throw new RuntimeException("Only addition of single digit can be handled");
        }

        // When need to carry for a subtraction
        if (x2 < 0 && x1 + x2 < 0) {
            return -(x1 + 10 + x2);
        }
        return x1 + x2;
    }

    private static String multiply(int x1, int x2) {
        if (x1 / 10 != 0 || x2 / 10 != 0) {
            throw new RuntimeException("Only multiplication of single digit can be handled");
        }
        return String.valueOf(x1 * x2);
    }

    private static String prependZeros(String num) {
        return prependZeros(num, null);
    }

    private static String prependZeros(String num, Long targetLength) {
        StringBuilder builder = new StringBuilder();
        int length = num.length();

        if (targetLength == null) {
            targetLength = nextPowerOfTwo(length);
        }

        for (int i = length; i < targetLength; i++) {
            builder.append('0');
        }
        builder.append(num);
        return builder.toString();
    }

    private static String appendZeros(String num, Long targetLength) {
        StringBuilder builder = new StringBuilder();
        builder.append(num);

        for (int i = 0; i < targetLength; i++) {
            builder.append('0');
        }
        return builder.toString();
    }

    private static long nextPowerOfTwo(int num) {
        long counter = 1;
        while (counter < num) {
            counter = counter << 1;
        }
        return counter;
    }
}
