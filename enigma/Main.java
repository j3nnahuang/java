package enigma;

import java.io.*;

public final class Main {

    private static Reflector bReflector;
    private static Reflector cReflector;
    private static FixedRotor betaRotor;
    private static FixedRotor gammaRotor;
    private static Rotor rotor1;
    private static Rotor rotor2;
    private static Rotor rotor3;
    private static Rotor rotor4;
    private static Rotor rotor5;
    private static Rotor rotor6;
    private static Rotor rotor7;
    private static Rotor rotor8;

    /** Process a sequence of encryptions and decryptions, as
     *  specified in the input from the standard input.  Print the
     *  results on the standard output. Exits normally if there are
     *  no errors in the input; otherwise with code 1. */
    public static void main(String[] args) {
        Machine M;
        BufferedReader input = null;
        try {
            input = new BufferedReader(
                    new InputStreamReader(new FileInputStream(args[0])));
        } catch (FileNotFoundException e) {
            throw new RuntimeException("No such file found.");
        }

        String outputFilename;
        if (args.length >= 2) {
            outputFilename = args[1];
        } else {
            outputFilename = "output.txt";
        }

        buildRotors();

        M = null;

        try {
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                if (isConfigurationLine(line)) {
                    M = configure(line);
                } else {
                    if (M == null) {
                        throw new EnigmaException("Machine never instantiated");
                    }
                    writeMessageLine(M.convert(standardize(line)),
                            outputFilename);
                }
            }
        } catch (IOException excp) {
            System.err.printf("Input error: %s%n", excp.getMessage());
            System.exit(1);
        }
    }

    /** Return true iff LINE is an Enigma configuration line. */
    private static boolean isConfigurationLine(String line) {
        if (line.length() <= 0 || line.equals("") || line.equals("\n")) {
            return false;
        } else {
            return line.charAt(0) == '*';
        }
    }

    private static boolean contains(String[] list, String target) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == null) {
                continue;
            } else if (list[i].equals(target)) {
                return true;
            }
        }
        return false;
    }

    /** Configure M according to the specification given on CONFIG,
     *  which must have the format specified in the assignment. */
    private static Machine configure(String config) {
        String[] rotors = new String[]{"I", "II", "III", "IV", "V",
                                          "VI", "VII", "VIII"};
        String[] currRotors = new String[4];
        String[] toTest = config.split(" ");
        if (!toTest[1].equals("B") && !toTest[1].equals("C")) {
            throw new EnigmaException("First Rotor is not a Reflector");
        } else if (!toTest[2].equals("BETA") && !toTest[2].equals("GAMMA")) {
            throw new EnigmaException("No fixed rotors given");
        } else if (toTest.length != 7) {
            throw new EnigmaException("Wrong number of Rotors");
        } else if (toTest[6].length() != 4 || (!toTest[6].matches("[A-Z]+"))) {
            throw new EnigmaException("Rotor settings are wrong");
        }
        int j = 0;
        for (int i = 3; i < 6; i++) {
            if (!contains(rotors, toTest[i])) {
                throw new EnigmaException("Invalid rotor name");
            } else if (contains(currRotors, toTest[i])) {
                throw new EnigmaException("Duplicate Rotors");
            }
            currRotors[j] = toTest[i];
            j++;
        }
        String[] settings = config.split(" ");
        Reflector reflect = getReflector(settings[1]);
        Rotor fixed = getRotor(settings[2]);
        Rotor left = getRotor(settings[3]);
        Rotor middle = getRotor(settings[4]);
        Rotor right = getRotor(settings[5]);
        Machine toReturn = new Machine(reflect, fixed, left, middle, right);
        toReturn.setRotors(settings[6]);
        return toReturn;
    }

    public static Reflector getReflector(String ref) {
        if (ref.equals("B")) {
            return bReflector;
        } else if (ref.equals("C")) {
            return cReflector;
        } else {
            return null;
        }
    }

    public static Rotor getRotor(String rot) {
        if (rot.equals("I")) {
            return rotor1;
        } else if (rot.equals("II")) {
            return rotor2;
        } else if (rot.equals("III")) {
            return rotor3;
        } else if (rot.equals("IV")) {
            return rotor4;
        } else if (rot.equals("V")) {
            return rotor5;
        } else if (rot.equals("VI")) {
            return rotor6;
        } else if (rot.equals("VII")) {
            return rotor7;
        } else if (rot.equals("VIII")) {
            return rotor8;
        }  else if (rot.equals("BETA")) {
            return betaRotor;
        } else if (rot.equals("GAMMA")) {
            return gammaRotor;
        } else {
            return null;
        }
    }

    /** Return the result of converting LINE to all upper case,
     *  removing all blanks and tabs.  It is an error if LINE contains
     *  characters other than letters and blanks. */
    private static String standardize(String line) throws EnigmaException {
        line = line.replaceAll(" ", "");
        line = line.toUpperCase();
        line = line.replaceAll("\t", "");
        line = line.replaceAll("\n", "");
        char[] checkLetters = line.toCharArray();
        for (char c : checkLetters) {
            if (!Character.isLetter(c)) {
                throw new EnigmaException("Bad Character");
            }
        }
        return line;
    }

    /** Write MSG in groups of five to out file (except that the last
     *  group may have fewer letters). */
    private static void writeMessageLine(String msg, String filename) {
        try {
            FileWriter writer = new FileWriter(filename, true);
            String outputString = "";
            for (int i = 0; i < msg.length(); i += 5) {
                outputString += msg.substring(i, Math.min(i + 5, msg.length()));
                if (i + 5 < msg.length()) {
                    outputString += " ";
                }
            }

            writer.write(outputString + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("IOException when writing: " + e);
        }
    }

    /** Create all the necessary rotors. */
    private static void buildRotors() {
        bReflector = new Reflector("ENKQAUYWJICOPBLMDXZVFTHRGS");
        cReflector = new Reflector("RDOBJNTKVEHMLFCWZAXGYIPSUQ");
        betaRotor = new FixedRotor("LEYJVCNIXWPBQMDRTAKZGFUHOS", "RLFOBVUXHDSANGYKMPZQWEJICT");
        gammaRotor = new FixedRotor("FSOKANUERHMBTIYCWLQPZXVGJD", "ELPZHAXJNYDRKFCTSIBMGWQVOU");
        rotor1 = new Rotor("EKMFLGDQVZNTOWYHXUSPAIBRCJ", "UWYGADFPVZBECKMTHXSLRINQOJ", "Q");
        rotor2 = new Rotor("AJDKSIRUXBLHWTMCQGZNPYFVOE", "AJPCZWRLFBDKOTYUQGENHXMIVS", "E");
        rotor3 = new Rotor("BDFHJLCPRTXVZNYEIWGAKMUSQO", "TAGBPCSDQEUFVNZHYIXJWLRKOM", "V");
        rotor4 = new Rotor("ESOVPZJAYQUIRHXLNFTGKDCMWB", "HZWVARTNLGUPXQCEJMBSKDYOIF", "J");
        rotor5 = new Rotor("VZBRGITYUPSDNHLXAWMJQOFECK", "QCYLXWENFTZOSMVJUDKGIARPHB", "Z");
        rotor6 = new Rotor("JPGVOUMFYQBENHZRDKASXLICTW", "SKXQLHCNWARVGMEBJPTYFDZUIO", "Z, M");
        rotor7 = new Rotor("NZJHGRCXMYSWBOUFAIVLPEKQDT", "QMGYVPEDRCWTIANUXFKZOSLHJB", "Z, M");
        rotor8 = new Rotor("FKQHTLXOCBJSPDZRAMEWNIUYGV", "QJINSAYDVKBFRUHMCPLEWZTGXO", "Z, M");
    }

}
