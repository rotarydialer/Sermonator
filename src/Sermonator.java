import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/**
 * Created by chris on 10/20/16.
 *
 * TODO:
 *  Possibly: remove/prevent duplicates within the chain (test vs leaving in)
 *  Implement two-, three-word chains
 *
 */
public class Sermonator {
	//Hashmap chain
	public static Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
	static Random rng = new Random();

    // The current state of this algorithm relies upon periods in the text to indicate completed sentences.
    // This is goes for generated phrases as well.
	private static char WORD_DELIMITER = '.';

    // temp: to analyze specific words
    private static String lookingFor = "";
    private static int lookingForCount = 0;
    private static int phraseCount = 0;
    private static int phraseWordCount = 0;

    public static void main(String args[]) throws IOException {
		//System.out.println("START ___");

		// initialize the chain with start and end nodes
		markovChain.put("_start", new Vector<String>());
		markovChain.put("_end", new Vector<String>());

		// Get the input from either a file or user entry.
		//getInputFromConsole();
        readFromFile("cs-lewis__spirits-in-bondage.txt");
        //readFromFile("walter-de-la-mare--the-return.txt");
        readFromFile("walter-de-la-mare--the-veil.txt");
        readFromFile("delamare-ghost.txt");
        readFromFile("delamare-remonstrance.txt");
        readFromFile("delamare-hope.txt");
        //readFromFile("shelley-flower.txt");

		//displayChain();

        int nPhrases = rng.nextInt(35);
        //int nPhrases = 20;

        System.out.println(String.format("Generating %1$d phrases from %2$d nodes", nPhrases, markovChain.size()));
        System.out.println();

        //displayChain();

        for (int i=0; i<nPhrases; i++) {
            generateSentence();
        }

		//System.out.println("___   END");
	}

	public static void addWords(String phrase) {
		// put each word into an array
		String[] words = phrase.split(" ");

		// Loop through each word, check if it's already been added
		//   If it HAS, get the following word (suffix vector?) and add that
		//   If it HASN'T, add it
		//   If it's the FIRST or LAST word, select the _start / _end key, respectively.

		for (int i=0; i<words.length; i++) {

			if (i == 0) { 							// first word
				Vector<String> startWords = markovChain.get("_start");
				startWords.add(words[i]);

				Vector<String> suffix = markovChain.get(words[i]);

				if (suffix == null) {
					suffix = new Vector<String>();
                    try {

                        suffix.add(words[i+1]);

                    } catch (ArrayIndexOutOfBoundsException aiOobe) {
                        //swallow these exceptions; it just means there was a single word/character on a given line and we can just move on.
                        //System.out.println(String.format("OOB ERROR at iteration %1$d, words[i]=%2$s (length: %3$d)", i, words[i], words.length) );
                    }
                    markovChain.put(words[i], suffix);
				}

			} else if (i == words.length-1) {		// last word
				Vector<String> endWords = markovChain.get("_end");
				endWords.add(words[i]);

			} else {								// all middle words
				Vector<String> suffix = markovChain.get(words[i]);

//				if (words[i].equals(lookingFor)) {
//					lookingForCount++;
//					System.out.println(String.format(" --> %1$d. == %2$s", i, words[i]));
//					System.out.println(String.format(" -----> %2$d. Following word == %1$s", words[i+1], lookingForCount));
//				}

				if (suffix == null) {
					suffix = new Vector<String>();

					suffix.add(words[i+1]);
                    markovChain.put(words[i], suffix);
				} else {
                    suffix.add(words[i+1]);
//                    if (words[i].equals(lookingFor))
//                        System.out.println(i + ". word: " + words[i] + " " + suffix);
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
        int randomNextWordInt = rng.nextInt(startWordsLen);

        nextWord = startWords.get(randomNextWordInt);
        newPhrase.add(nextWord);

        // Loop through the words until we reach the end (?)
        int nwLength = nextWord.length()-1;
        if (nwLength <= 0) {
            // just add a blank line in these cases; makes it read more like a poem
            System.out.println();
        }

        if (!nextWord.equals("")) {
            phraseWordCount = 1;

            while (nextWord.charAt(nextWord.length()-1) != WORD_DELIMITER ) {
                phraseWordCount++;
                //System.out.println(" ......." + nextWord.length());
                Vector<String> wordSelector = markovChain.get(nextWord);

                //System.out.println("wordSelector = " + wordSelector);

                int wordSelectorSize=0;
                int rNum=0;

                if (wordSelector!=null && !wordSelector.isEmpty()) {
                    wordSelectorSize = wordSelector.size();
                    rNum = rng.nextInt(wordSelectorSize);

                    try {
                        rNum = rng.nextInt(wordSelectorSize);

                        nextWord = wordSelector.get(rNum);
                    } catch (Exception e) {
                        System.out.println(String.format("WS ERROR: %1$s", e.getStackTrace() ));
                        System.out.println(String.format("   -> rnum: %1$d", rNum));
                        System.out.println(String.format("   -> nextWord: %1$s", nextWord));
                        System.out.println(String.format("   -> wordSelectorSize: %1$s", wordSelectorSize));
                        break;
                    }
                    //newPhrase.add(nextWord);
                    newPhrase.add( reinsertApostrophes(nextWord) );
                } else {
                    //System.out.println(String.format(" !-> Word selector for \"%1$s\" was null...", nextWord));
                    break;
                }
            }

        }

        // hardcode words that cause failures
        //nextWord = "think?’";


//		System.out.println();
//		System.out.println("New Phrase: __________________________");
//		System.out.println(newPhrase.toString());


        phraseCount++;
        displayPhrase(newPhrase);

        System.out.println();

	}

	private static void displayChain() {
        System.out.println("Displaying chain: _____");
        for (String key : markovChain.keySet()) {

            if(key.equals(lookingFor) || lookingFor.isEmpty()) {
                System.out.println(String.format("%1$s : %2$s", key, markovChain.get(key) ));
            }
		}
        System.out.println("_____ end of chain");
    }

    private static void displayPhrase(Vector<String> incPhrase) {
        //System.out.print(phraseCount + ". ");
        for (String word : incPhrase) {
                //System.out.print(word + " ");
                System.out.print( reinsertApostrophes(word) + " ");
        }
        //System.out.print(String.format(" (%1$d words)", phraseWordCount));
    }

	private static void readFromFile(String fileName) {
		// This will reference one line at a time
		String line = null;

		try {
			// FileReader reads text files in the default encoding.
			InputStream res = Sermonator.class.getClassLoader().getResourceAsStream(fileName);

			BufferedReader fileReader =	new BufferedReader(new InputStreamReader(res));
			
			//FileReader fileReader = new FileReader(fileName);

			// Always wrap FileReader in BufferedReader.
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			try {
				while((line = bufferedReader.readLine()) != null) {

                    if (line.trim() != "" && !line.trim().isEmpty()) {
						
						addWords(stripUnwantedCharacters(line));

						//System.out.println(line);
					} else {
						// don't add empty lines
                    }
					
				}   
			} catch (Exception e) {
				System.out.println(fileName + " ERROR: " + e.getLocalizedMessage() );
                e.printStackTrace();
			} finally {
				// Always close files.
				bufferedReader.close(); 
			}
        
		}
		catch(FileNotFoundException ex) {
			System.out.println(
					"Unable to open file '" + 
							fileName + "'");                
		}
		catch(IOException ex) {
			System.out.println(
					"Error reading file '" 
							+ fileName + "'");       
		}
	}
	
	private static void getInputFromConsole() {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		int i = 0;

		while (true) {
			i++;
			System.out.print(String.format("Enter some text > ", i));

			String inputString;
			try {
				inputString = in.readLine();

				if (inputString=="" || inputString.isEmpty())
					break;

				addWords(inputString + WORD_DELIMITER);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static String stripUnwantedCharacters (String inString) {
        String outString = inString;

        outString = outString.replace("'", "~");
        outString = outString.replace("  ", "");
        outString = outString.replace("\n", "");
        //outString = outString.replace(",", "");

        return outString;
    }

    private static String reinsertApostrophes (String inString) {
        String outString = inString;

        outString = outString.replace("~", "\'");

        return outString;
    }

}
