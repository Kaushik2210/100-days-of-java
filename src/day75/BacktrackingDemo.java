import java.util.ArrayList;
import java.util.List;

public class BacktrackingDemo {

    public static void main(String[] args) {
        List<List<Integer>> results = new ArrayList<>();
        permute(new int[]{1, 2, 3}, new ArrayList<>(), new boolean[3], results);

        System.out.println("permutations of [1, 2, 3] (" + results.size() + " total):");
        for (List<Integer> permutation : results) {
            System.out.println("  " + permutation);
        }
    }

    static void permute(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> results) {
        if (current.size() == nums.length) {
            results.add(new ArrayList<>(current));
            return;
        }

        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;

            used[i] = true;
            current.add(nums[i]);
            permute(nums, current, used, results);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
