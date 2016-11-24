import java.util.ArrayList;
import java.util.List;

class A {
  
  static List<String> alphabet = new ArrayList(26);
  
  static {
    for (char c = 'A'; c <= 'Z'; c++) {
      alphabet.add(AsciiArt.printChar(c));
    }
  }
  
  static char scanChar(String s) {
    if (s != null) {
      int index = alphabet.indexOf(s);
      if (index >= 0) {
        return (char) ('A' + index);
      }
    }
    return '?';
  }
}