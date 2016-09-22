package enigma;

class Machine {

    private Reflector reflector;
    private Rotor fixedRotor;
    private Rotor left;
    private Rotor middle;
    private Rotor right;

    Machine(Reflector reflect, Rotor fixed, Rotor l, Rotor m, Rotor r) {
        reflector = reflect;
        fixedRotor = fixed;
        left = l;
        middle = m;
        right = r;
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting.  */
    void setRotors(String setting) {
        fixedRotor.set(Rotor.toIndex(setting.charAt(0)));
        left.set(Rotor.toIndex(setting.charAt(1)));
        middle.set(Rotor.toIndex(setting.charAt(2)));
        right.set(Rotor.toIndex(setting.charAt(3)));
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] textArray = msg.toCharArray();
        String converted = "";
        for (int i = 0; i < textArray.length; i++) {
            converted += charConverter(textArray[i]);
        }
        return converted;
    }

    char charConverter(char c) {
        // Fix advances, no errors in the code but advances may be wrong
        advanceAll();
        int converted = Rotor.toIndex(c);
        converted = right.convertForward(converted);
        converted = middle.convertForward(converted);
        converted = left.convertForward(converted);
        converted = fixedRotor.convertForward(converted);
        converted = reflector.convertForward(converted);
        converted = fixedRotor.convertBackward(converted);
        converted = left.convertBackward(converted);
        converted = middle.convertBackward(converted);
        converted = right.convertBackward(converted);
        return Rotor.toLetter(converted);
    }

    void advanceAll() {
        if (middle.atNotch()) {
            left.advance();
            middle.advance();
            right.advance();
        } else if (!middle.atNotch() && right.atNotch()) {
            middle.advance();
            right.advance();
        } else if (!middle.atNotch() && !right.atNotch()) {
            right.advance();
        }
    }
}
