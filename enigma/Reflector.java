package enigma;

class Reflector extends Rotor {

    private int[] reflect;
    private int _setting;

    Reflector(String alph) {
        char[] code = alph.trim().toCharArray();
        reflect = new int[26];
        for (int i = 0; i < 26; i++) {
            reflect[i] = toIndex(code[i]);
        }
    }

    boolean hasInverse() {
        return false;
    }

    int convertForward(int p) {
        p = p + _setting;
        if (p >= 26) {
            p -= 26;
        }
        int i = reflect[p];
        i = i - _setting;
        if (i < 0) {
            i += 26;
        }
        return i;
    }

    int convertBackward(int unused) {
        throw new UnsupportedOperationException();
    }

    void advance() {
        // Does not advance
    }

    void set(int posn) {
        // Position/setting does not matter for reflectors
    }

}
