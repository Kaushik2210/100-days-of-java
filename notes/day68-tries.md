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

## startsWith: the operation a hash table can't do

`startsWith` is exactly `findNode` without the `isEndOfWord` check — it just needs the path to exist, regardless of whether it happens to be a complete word.

```java
boolean startsWith(String prefix) {
    return findNode(prefix) != null; // the path exists -- some inserted word begins with this prefix
}
```

This single method is the trie's whole reason to exist: no reasonable adaptation of Day 67's hash table can answer "does any key start with this prefix?" without scanning every key, since a hash scrambles a string into a number that carries no information about the string's substrings. A trie makes this the *cheapest* possible query — exactly as cheap as an exact-match search.

## Autocomplete: collecting every word under a prefix

Once `findNode(prefix)` locates the node where the prefix's path ends, every complete word in the trie that starts with that prefix lives somewhere in the subtree below it — walk that subtree (Day 63's recursive traversal, applied to a trie instead of a binary tree) and collect every node marked `isEndOfWord`.

```java
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
```

This is exactly how autocomplete/typeahead search works in practice: type a prefix, and every matching word is already sitting in the subtree, ready to be collected.

## Complexity: driven by string length, not collection size

Every trie operation — `insert`, `search`, `startsWith` — costs O(L), where L is the length of the string involved, completely independent of how many *other* words are stored in the trie. This is a genuinely different shape from Day 67's O(1) *average* (but data-dependent) hash table lookup, or Day 64's O(log n) BST search (dependent on the number of stored elements). The tradeoff is space: a trie with many short, dissimilar words can use noticeably more memory than a hash table storing the same strings, since each node carries a full map of children — worthwhile specifically when prefix-based queries matter, not as a general-purpose replacement for Day 67's hash table.
