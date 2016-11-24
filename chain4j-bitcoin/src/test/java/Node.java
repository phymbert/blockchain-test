class Node {
    
    Node left, right;
	int value;

    Node findRecursive(int v) {
        Node found = null;
        if (v == value) {
          found = this;
        } else if (v < value) {
          if (left != null) {
            found = left.findRecursive(v);
          }
        } else {
          if (right != null) {
            found = right.findRecursive(v);
          }
        }
        return found;
    }
    
    Node find(int v) {
      Node current = this;
      
      while (current != null && v != current.value) {
        if (v < current.value) {
          current = current.left;
        } else {
          current = current.right;
        }
      }
      return current;
    }
    
}