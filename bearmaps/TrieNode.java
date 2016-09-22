import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class TrieNode {
    private boolean validWord;
    private String letter;
    private String word;
    private HashMap<String, TrieNode> nextLetters;
    ArrayList<String> prefixWords;

    public TrieNode(String c) {
        letter = c;
        validWord = false;
        word = null;
        prefixWords = new ArrayList<>();
        nextLetters = new HashMap<>();
    }

    public void setValidWord() {
        validWord = true;
    }

    public void setWord(String s) {
        word = s;
    }

    public String getWord() {
        return word;
    }

    public boolean isValidWord() {
        return (validWord && word != null);
    }

    public HashMap<String, TrieNode> getNextLetters() {
        return nextLetters;
    }

    public void getPossibleWordsFromHere() {
        Set<String> nextLetter = nextLetters.keySet();
        for (String c : nextLetter) {
            c = GraphDB.cleanString(c);
            if (nextLetters.get(c).isValidWord()) {
                prefixWords.add(nextLetters.get(c).getWord());
            }
            nextLetters.get(c).getPossibleWordsFromHere();
        }
    }

    public ArrayList<String> getPrefixWords() {
        return prefixWords;
    }
}
