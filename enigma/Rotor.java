package enigma;

class Rotor {
    // This needs other methods, fields, and constructors.

    /** My current setting (index 0..25, with 0 indicating that 'A'
     *  is showing). */
    private int _setting;

    /* The int[] for encryption and decryption */
    private int[] encryption;
    private int[] decryption;

    /* Notches */
    private int notch1;
    private int notch2;

    /** Size of alphabet used for plaintext and ciphertext. */
    static final int ALPHABET_SIZE = 26;

    Rotor(String alph, String decyph, String notch) {
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
        char[] notchCode = notch.trim().replace(", ", "").toCharArray();
        if (notchCode.length == 2) {
            notch1 = toIndex(notchCode[0]);
            notch2 = toIndex(notchCode[1]);
        } else if (notchCode.length == 1) {
            notch1 = toIndex(notchCode[0]);
            notch2 = -1;
        } else {
            notch1 = -1;
            notch2 = -1;
        }
    }

    Rotor() {

    }

    /** Assuming that P is an integer in the range 0..25, returns the
     *  corresponding upper-case letter in the range A..Z. */
    static char toLetter(int p) {
        return (char) (p + 'A');
    }

    /** Assuming that C is an upper-case letter in the range A-Z, return the
     *  corresponding index in the range 0..25. Inverse of toLetter. */
    static int toIndex(char c) {
        return c - 'A';
    }

    /** Returns true iff this rotor has a ratchet and can advance. */
    boolean advances() {
        return true;
    }

    /** Returns true iff this rotor has a left-to-right inverse. */
    boolean hasInverse() {
        return true;
    }

    /** Return my current rotational setting as an integer between 0
     *  and 25 (corresponding to letters 'A' to 'Z').  */
    int getSetting() {
        return _setting;
    }

    /** Set getSetting() to POSN.  */
    void set(int posn) {
        assert 0 <= posn && posn < ALPHABET_SIZE;
        _setting = posn;
    }

    /** Return the conversion of P (an integer in the range 0..25)
     *  according to my permutation. */
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

    /** Return the conversion of E (an integer in the range 0..25)
     *  according to the inverse of my permutation. */
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

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return _setting == notch1 || _setting == notch2;
    }

    /** Advance me one position. */
    void advance() {
        _setting = (_setting + 1) % 26;
    }

}
