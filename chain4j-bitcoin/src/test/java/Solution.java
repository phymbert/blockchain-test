import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Solution {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        String S = in.nextLine();

        boolean b = true;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\(\\)");
        StringBuilder sb = new StringBuilder(S);
        while (b && sb.length() > 0) {
          java.util.regex.Matcher m = p.matcher(sb.toString());
          if (m.find()) {
            sb.delete(m.start(), m.end());
          } else {
            b = false;
          }
        }
        System.out.println(b);
    }
}