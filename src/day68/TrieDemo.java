import java.util.HashMap;
import java.util.Map;

public class TrieDemo {

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("cat");
        trie.insert("car");

        System.out.println("search(\"cat\") = " + trie.search("cat"));
        System.out.println("search(\"car\") = " + trie.search("car"));
        System.out.println("search(\"ca\") = " + trie.search("ca") + " (a prefix, but never inserted as a word)");
        System.out.println("search(\"dog\") = " + trie.search("dog"));
    }
}

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}

class Trie {
    private TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode());
        }
        current.isEndOfWord = true;
    }

    boolean search(String word) {
        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    private TrieNode findNode(String s) {
        TrieNode current = root;
        for (char c : s.toCharArray()) {
            current = current.children.get(c);
            if (current == null) return null;
        }
        return current;
    }
}
