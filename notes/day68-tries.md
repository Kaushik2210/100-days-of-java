# Day 68: Tries — Prefix Trees for String Search

Day 67's hash table answers "is this exact key present?" in O(1) average time, but it has nothing to offer for "what keys start with this prefix?" — the hash of `"cat"` gives no clue about the hash of `"catalog"`. A **trie** (from re**trie**val, pronounced "try") is a tree built specifically around shared prefixes, where every path from the root spells out a string one character at a time.

## Structure: each edge is a character

Unlike Day 63's binary tree, a trie node can have many children — one per possible next character — stored in a map keyed by character. A node also marks whether the path from the root down to it is a complete word, not just a prefix of a longer one.

```java
class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
}
```

## Insert: walk or create, one character at a time

Inserting a word walks down from the root, following (or creating, if missing) a child for each character in turn, then marks the final node as a word ending.

```java
class Trie {
    private TrieNode root = new TrieNode();

    void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current = current.children.computeIfAbsent(c, k -> new TrieNode()); // Day 25's computeIfAbsent
        }
        current.isEndOfWord = true; // the path spelled out so far is a complete word
    }
}
```

Inserting `"cat"` and then `"car"` shares the path for `'c'` then `'a'` — both words' first two nodes are the *same* nodes — and only diverges at the third character, into separate `'t'` and `'r'` children. This sharing is the whole point: words with a common prefix share the memory for that prefix.

## Search: exact match requires isEndOfWord

Searching walks the same way, but must additionally check `isEndOfWord` at the end — reaching a node by following all the characters only proves the string is a *prefix* of something inserted, not necessarily a complete word itself.

```java
boolean search(String word) {
    TrieNode node = findNode(word);
    return node != null && node.isEndOfWord; // must be an actual word ending, not just a valid path
}

private TrieNode findNode(String s) {
    TrieNode current = root;
    for (char c : s.toCharArray()) {
        current = current.children.get(c);
        if (current == null) return null; // fell off the trie -- no word or prefix matches
    }
    return current;
}
```

If only `"cat"` was ever inserted, `search("ca")` must return `false` even though the path for `'c'` then `'a'` genuinely exists in the trie — `"ca"` was never itself marked as a complete word.
