import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        System.out.println();
        System.out.println("startsWith(\"ca\") = " + trie.startsWith("ca"));
        System.out.println("startsWith(\"do\") = " + trie.startsWith("do"));

        System.out.println();
        Trie autocomplete = new Trie();
        for (String word : new String[]{"car", "care", "career", "cat", "cart", "dog"}) {
            autocomplete.insert(word);
        }
        System.out.println("wordsWithPrefix(\"car\") = " + autocomplete.wordsWithPrefix("car"));
        System.out.println("wordsWithPrefix(\"ca\")  = " + autocomplete.wordsWithPrefix("ca"));
        System.out.println("wordsWithPrefix(\"z\")   = " + autocomplete.wordsWithPrefix("z"));
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

    boolean startsWith(String prefix) {
        return findNode(prefix) != null;
    }

    List<String> wordsWithPrefix(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWords(node, new StringBuilder(prefix), results);
        }
        return results;
    }

    private void collectWords(TrieNode node, StringBuilder current, List<String> results) {
        if (node.isEndOfWord) {
            results.add(current.toString());
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            current.append(entry.getKey());
            collectWords(entry.getValue(), current, results);
            current.setLength(current.length() - 1); // backtrack: remove the character before trying a sibling
        }
    }
}
