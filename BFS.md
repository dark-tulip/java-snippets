```Java
class Node {
  int value;
  Node left;
  Node right;

  public Node(int value) {
    this.value = value;
  }
}

class TreeNode {
  Node node;

  public Node append(int value) {
    if (node == null) {
      this.node = new Node(value);
    }
    return append(this.node, value);
  }

  private Node append(Node parent, int value) {
    if (parent == null) {
      return new Node(value);
    }
    if (value < parent.value) {
      parent.left = append(parent.left, value);
    } else if (value > parent.value) {
      parent.right = append(parent.right, value);
    }
    return parent;
  }

  public void printBfs() {
    Queue<Node> queue = new LinkedList<>();
    queue.offer(this.node);

    StringBuilder str = new StringBuilder();

    while (!queue.isEmpty()) {
      Node now = queue.poll();
      str.append(now.value).append(" ");
      if (now.left != null) {
        queue.offer(now.left);
      }
      if (now.right != null) {
        queue.offer(now.right);
      }
    }

    // 9 8 10 6 4 7 3 5 2 1
    System.out.println(str.toString().equals("9 8 10 6 4 7 3 5 2 1 "));
  }
}

public class BFS {
  public static void main(String[] args) {
    TreeNode treeNode = new TreeNode();
    List<Integer> nums = List.of(9, 8, 6, 10, 4, 3, 2, 5, 1, 7);
    for (var num : nums) {
      treeNode.append(num);
    }
    treeNode.printBfs();
  }
}
```
