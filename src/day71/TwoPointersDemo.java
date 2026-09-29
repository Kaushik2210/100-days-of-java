import java.util.Arrays;

public class TwoPointersDemo {

    public static void main(String[] args) {
        int[] sortedArr = {2, 7, 11, 15, 18, 24};

        int[] result = twoSumSorted(sortedArr, 26);
        System.out.println("twoSumSorted(target=26) -> indices " + Arrays.toString(result)
            + " -> values " + sortedArr[result[0]] + " + " + sortedArr[result[1]]);

        int[] noMatch = twoSumSorted(sortedArr, 100);
        System.out.println("twoSumSorted(target=100) -> " + Arrays.toString(noMatch) + " (no pair found)");

        System.out.println();
        int[] withDuplicates = {1, 1, 2, 2, 2, 3, 4, 4, 5};
        int uniqueCount = removeDuplicates(withDuplicates);
        System.out.println("removeDuplicates: uniqueCount = " + uniqueCount);
        System.out.println("first " + uniqueCount + " elements = "
            + Arrays.toString(Arrays.copyOf(withDuplicates, uniqueCount)));

        System.out.println();
        Node acyclic = buildList(new int[]{1, 2, 3, 4, 5}, -1); // no cycle
        System.out.println("hasCycle(acyclic list) = " + hasCycle(acyclic));

        Node cyclic = buildList(new int[]{1, 2, 3, 4, 5}, 2); // tail points back to index 2 (value 3)
        System.out.println("hasCycle(cyclic list)  = " + hasCycle(cyclic));
    }

    static int removeDuplicates(int[] sortedArr) {
        if (sortedArr.length == 0) return 0;

        int slow = 0;
        for (int fast = 1; fast < sortedArr.length; fast++) {
            if (sortedArr[fast] != sortedArr[slow]) {
                slow++;
                sortedArr[slow] = sortedArr[fast];
            }
        }
        return slow + 1;
    }

    static boolean hasCycle(Node head) {
        Node slow = head;
        Node fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) return true;
        }
        return false;
    }

    // builds a linked list from values; if cycleIndex >= 0, the tail's next points back to that index
    static Node buildList(int[] values, int cycleIndex) {
        Node head = new Node(values[0]);
        Node current = head;
        Node cycleTarget = cycleIndex == 0 ? head : null;
        for (int i = 1; i < values.length; i++) {
            current.next = new Node(values[i]);
            current = current.next;
            if (i == cycleIndex) cycleTarget = current;
        }
        if (cycleTarget != null) current.next = cycleTarget; // wire the tail back, creating a real cycle
        return head;
    }

    static int[] twoSumSorted(int[] sortedArr, int target) {
        int left = 0;
        int right = sortedArr.length - 1;

        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];
            if (sum == target) {
                return new int[]{left, right};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return new int[]{-1, -1};
    }
}

class Node {
    int value;
    Node next;

    Node(int value) {
        this.value = value;
    }
}
