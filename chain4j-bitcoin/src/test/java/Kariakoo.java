class Kariakoo {
    
	static int getPositionAt2(int n) {
        int pos = 0;
        int mov = 0;
        int prev0 = 0;
        int prev = 0;
        for (int i = 1; i <= n; i++) {
          switch (i) {
            case 1:
              mov = 1;
              break;
            case 2:
              mov = -2;
              break;
            default:
              mov = prev - prev0;
              break;
          }
          prev0 = prev;
          prev = mov;
          pos += mov;
        }
        return pos;
	}
	

  static int getPositionAt(int n) {
    int mod = n % 6;
    switch (mod) {
      case 0:
        return 0;
      case 1:
        return 1;
      case 2:
        return -1;
      case 3:
        return -4;
      case 4:
        return -5;
      case 5:
        return -3;
      default:
        throw new IllegalStateException("Out of dancefloor " + n);
    }
  }
	
	public static void main(String[] args) {
	  for (int i = 0; i < 2147483647; i++) {
	    int exp = Kariakoo.getPositionAt2(i);
	    int got = Kariakoo.getPositionAt(i);
	    System.out.println(i + "=" + exp + "==" + got);
	    if (got != exp) {
	      System.out.println(i + " " + exp + "==" + got );
	    }
	  }
	  
    System.out.println(Kariakoo.getPositionAt(0)); // 0
    System.out.println(Kariakoo.getPositionAt(1)); // 1
    System.out.println(Kariakoo.getPositionAt(2)); // -1
	  System.out.println(Kariakoo.getPositionAt(3)); // -4
	  System.out.println(Kariakoo.getPositionAt(100000)); // -5
	  System.out.println(Kariakoo.getPositionAt(2147483647)); // 1
  }
}