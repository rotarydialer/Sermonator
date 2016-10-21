import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * Created by chris on 10/20/16.
 */
public class Sermonator {
    //Hashmap chain
    public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
    static Random rng = new Random();
    private static char WORD_DELIMITER = '~';

    public static void main(String args[]) throws IOException {
        System.out.println("START ___");

        // initialize the chain with start and end nodes
        markovChain.put("_start", new Vector<String>());
        markovChain.put("_end", new Vector<String>());

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        int i = 0;

        while (true) {
            i++;
            System.out.print(String.format("Enter some text > ", i));

            String inputString = in.readLine();

            if (inputString=="" || inputString.isEmpty())
                break;

            addWords(inputString + WORD_DELIMITER);
        }

        displayChain();

        generateSentence();

        System.out.println("___   END");
    }

    public static void addWords(String phrase) {
        // put each word into an array
        String[] words = phrase.split(" ");

        // Loop through each word, check if it's already been added
        //   If it HAS, get the following word (suffix vector?) and add that
        //   If it HASN'T, add it
        //   If it's the FIRST or LAST word, select the _start / _end key, respectively.

        for (int i=0; i<words.length; i++) {

            if (i == 0) {
                Vector<String> startWords = markovChain.get("_start");
                startWords.add(words[i]);

                Vector<String> suffix = markovChain.get(words[i]);
                if (suffix == null) {
                    suffix = new Vector<String>();
                    suffix.add(words[i+1]);
                    markovChain.put(words[i], suffix);
                }

            } else if (i == words.length-1) {
                Vector<String> endWords = markovChain.get("_end");
                endWords.add(words[i]);

                //System.out.println(String.format(" words[%1$d] = %2$s", 1, words[i]));

            } else {
                Vector<String> suffix = markovChain.get(words[i]);

                if (suffix == null) {
                    suffix = new Vector<String>();

                    suffix.add(words[i+1]);
                    markovChain.put(words[i], suffix);
                }

            }

        }

    }

    public static void generateSentence() {

        // Make a Vector to hold the phrase
        Vector<String> newPhrase = new Vector<String>();

        // Placeholder string for the next word
        String nextWord = "";

        // Select the first word
        Vector<String> startWords = markovChain.get("_start");
        int startWordsLen = startWords.size();

        System.out.println("startWordsLen = " + startWordsLen);
        int randomNextWordInt = rng.nextInt(startWordsLen);

        System.out.println("randomNextWordInt = " + randomNextWordInt);

        nextWord = startWords.get(randomNextWordInt);
        newPhrase.add(nextWord);

        // comment

        // Loop through the words until we reach the end (?)
        while (nextWord.charAt(nextWord.length()-1) != WORD_DELIMITER ) {
            Vector<String> wordSelector = markovChain.get(nextWord);

            int wordSelectorSize = wordSelector.size();

            nextWord = wordSelector.get(rng.nextInt(wordSelectorSize));
            newPhrase.add(nextWord);
        }

        System.out.println();
        System.out.println("________________ New Phrase: ");
        System.out.println(newPhrase.toString());

    }

    private static void displayChain() {
        for (String key : markovChain.keySet()) {
            System.out.println(String.format("%1$s : %2$s", key, markovChain.get(key) ));
        }
    }

}
