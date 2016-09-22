import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode("");
    }

    public void insert(String s) {
        String cleanString = GraphDB.cleanString(s);
        TrieNode current = root;
        if (cleanString.length() == 0) {
            current.setValidWord();
        }
        for (int i = 0; i < cleanString.length(); i++) {
            if (current.getNextLetters().containsKey(String.valueOf(cleanString.charAt(i)))) {
                current = current.getNextLetters().get(String.valueOf(cleanString.charAt(i)));
            } else {
                TrieNode toAdd = new TrieNode(String.valueOf(cleanString.charAt(i)));
                current.getNextLetters().put(String.valueOf(cleanString.charAt(i)), toAdd);
                current = toAdd;
            }
            if (i == cleanString.length() - 1) {
                current.setValidWord();
                current.setWord(s);
            }
        }
    }

    public boolean search(String s) {
        TrieNode current = root;
        String cleanString = GraphDB.cleanString(s);
        while (current != null) {
            for (int i = 0; i < cleanString.length(); i++) {
                if (!current.getNextLetters().containsKey(cleanString.charAt(i))) {
                    return false;
                } else {
                    current = current.getNextLetters().get(cleanString.charAt(i));
                }

                if (current.isValidWord()) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public TrieNode getPrefixNode(String s) {
        TrieNode current = root;
        while (current != null) {
            for (int i = 0; i < s.length(); i++) {
                if (current.getNextLetters().containsKey(String.valueOf(s.charAt(i)))) {
                    current = current.getNextLetters().get(String.valueOf(s.charAt(i)));
                } else {
                    return null;
                }
            }
        }
        return current;
    }
}
