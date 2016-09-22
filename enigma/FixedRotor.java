package enigma;

class FixedRotor extends Rotor {

    private int _setting;
    private int[] encryption;
    private int[] decryption;

    FixedRotor(String alph, String decyph) {
        _setting = 0;
        encryption = new int[26];
        decryption = new int[26];
        char[] code = alph.trim().toCharArray();
        for (int i = 0; i < 26; i++) {
            encryption[i] = toIndex(code[i]);
        }
        code = decyph.trim().toCharArray();
        for (int i = 0; i < 26; i++) {
            decryption[i] = toIndex(code[i]);
        }
    }

    @Override
    int convertForward(int p) {
        p = p + _setting;
        if (p >= 26) {
            p -= 26;
        }
        int i = encryption[p];
        i = i - _setting;
        if (i < 0) {
            i += 26;
        }
        return i;
    }

    @Override
    int convertBackward(int e) {
        e = e + _setting;
        if (e >= 26) {
            e -= 26;
        }
        int i = decryption[e];
        i = i - _setting;
        if (i < 0) {
            i += 26;
        }
        return i;
    }

    @Override
    boolean advances() {
        return false;
    }

    @Override
    boolean atNotch() {
        return false;
    }

    /** Fixed rotors do not advance. */
    @Override
    void advance() {
    }

}
