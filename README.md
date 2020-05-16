# N-gram Word Predictor and Auto-correct
Word Prediction and Auto-correct using NLP tools (n-gram backoff, viterbi decoding as a spell checker, letter-level backoff prediction) in **Java**.

@init_class: GUI.java

@authors: Agranya Pratap Singh, Manish Kaswan

## How To Run

The program is run through the GUI.java file with the following flags determining project functioning:

`java GUI -n <number> (2,3,4) -v <number> (2,3)`

the -n flag will determine the n-gram level, and the v-flag will determine the viterbi decoding level
if no arguments are given, the program will initialize to a bigram model for both prediction and decoding


GUI usage:
- The large textfield is for user input only, the bottom one is used to display predictions

- The first word of each phrase (after program startup, period, or semicolon) will not display any predictions

- After predictions are available (wordcount > 0), each letter typed into the textfield should prune the list
       of possible predictions to only include those with the input stem (if any)

- In order to autocomplete a word to the most likely prediction displayed in bottom textfield,
     either with no letters typed or halfway through a word, press the *ENTER* key. You will notice
     that the letter saved variable updates your letters saved progress on the terminal window every time 
     you use a predicted word in your text

- A period and semicolon resets the word count; grams do not carry through this punctuation

- If a word is not contained in dictionary, which is checked after a *SPACE* key, the viterbi will attempt to decode automatically.

## Project Structure

```
- GUI: text interface to predictos and auto-correct  

- ConvertNGrams: Converts testfiles from http://www.ngrams.info/intro.asp into HashMap lookup tables that are serialized for ease of use 

- BigramPrecdictor / TrigramPredictor / QuadgramPredictor: Predict the next word of a sentence using respective (bi-gram, tri-gram, quad-gram) probabilites

- Listener: Constructs a KeyListener with gram predictors and viterbi decoding at @param levels

- ViterbiBigramDecoder / ViterbitrigramDecoder: Used in Viterbi decoding at letter level of n-gram word prediction model

- Key: Used in the Viterbi Decoding at the letter level in the n-gram model

``` 

## Bi-gram Predictor

Class to predict the next word in a given bi-gram sequence. (Tri-gram / Quad-gram and N-gram have a similar implementation)

```
//Bigram data from n-gram corpus

private final String FILENAME = "bigram_hashmap.ser";

HashMap<String, HashMap<String,Integer>> index;

// Constructor, initializes the hashmap

public BigramPredictor(){

try{

System.out.print("Initializing Bigram HashMap... ");

FileInputStream fis = new FileInputStream(FILENAME);

ObjectInputStream ois = new ObjectInputStream(fis);

index = (HashMap) ois.readObject();

ois.close();

fis.close();

System.out.println("Done.");

} catch (Exception e){

e.printStackTrace();

}

}

// Constructs a priority queue of most probable continuation words a given @param query

public PriorityQueue<Map.Entry<String,Integer>> predict(String query){

Map<String,Integer> map = index.get(query);

PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<Map.Entry<String,Integer>>(map.size(), new pqComparator() );

pq.addAll(map.entrySet());

return pq;

}
```

## ViterbiBigramDecoder

Used in Viterbi decoding at letter level of n-gram word prediction model. (Tri-gram / N-gram has similar implementation)

```

public class ViterbiBigramDecoder implements ViterbiDecoder {

double[][] v;

/** The bigram stats. */

double[][] a = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

/** The observation matrix. */

double[][] b = new double[Key.NUMBER_OF_CHARS][Key.NUMBER_OF_CHARS];

/** Pointers to retrieve the topmost hypothesis. */

int[][] backptr;

/** Reads the bigram probabilities (the 'A' matrix) from a file. */

public void init_a( String filename ) {

try {

InputStreamReader in = new InputStreamReader( new FileInputStream(filename), StandardCharsets.UTF_8 );

Scanner scan = new Scanner( in );

scan.useLocale( Locale.forLanguageTag( "en-US" ));

while ( scan.hasNext() ) {

int i = scan.nextInt();

int j = scan.nextInt();

double d = scan.nextDouble();

a[i][j] = d;

}

}

catch ( Exception e ) {

e.printStackTrace();

}

}

/** Initializes the observation probabilities (the 'B' matrix). */

public void init_b() {

for ( int i=0; i<Key.NUMBER_OF_CHARS; i++ ) {

char[] cs = Key.neighbour[i];

// Initialize all log-probabilities to some small value.

for ( int j=0; j<Key.NUMBER_OF_CHARS; j++ ) {

b[i][j] = Double.NEGATIVE_INFINITY;

}

// All neighbouring keys are assigned the probability 0.1

for ( int j=0; j<cs.length; j++ ) {

b[i][Key.charToIndex(cs[j])] = Math.log( 0.1 );

}

// The remainder of the probability mass is given to the correct key.

b[i][i] = Math.log( (10-cs.length)/10.0 );

}

}

/** Performs the Viterbi decoding and returns the most likely string. */

public String viterbi( String s ) {

// First turn chars to integers, so that 'a' is represented by 0...

int[] index = new int[s.length()];

for ( int i=0; i<s.length(); i++ ) {

index[i] = Key.charToIndex( s.charAt( i ));

}

// The Viterbi matrices

v = new double[index.length][Key.NUMBER_OF_CHARS];

backptr = new int[index.length+1][Key.NUMBER_OF_CHARS];

// Initialization

for ( int i=0; i<Key.NUMBER_OF_CHARS; i++ ) {

v[0][i] = a[Key.START_END][i]+b[index[0]][i];

backptr[0][i] = Key.START_END;

}

// Induction step

// Go through the length of the string

int argMax = -1;

for ( int t = 1; t<index.length; t++ ) {

// Go through the current possible states

for ( int j = 0; j<Key.NUMBER_OF_CHARS; j++ ) {

// Variable of the max prob at state t

double maxProb = Double.NEGATIVE_INFINITY;

// Compared to each of the last possible states

for ( int i = 0; i<Key.NUMBER_OF_CHARS; i++ ) {

// The testMax is the probability of each combination of paths from previous nodes to current state at node j

double testMax = v[t-1][i]+a[i][j]+b[index[t]][j];

// If probability is higher than previous max at state t, update it, and the backptr

if( maxProb < testMax ) {

maxProb = testMax;

argMax = i;

}

}

v[t][j] = maxProb;

backptr[t][j] = argMax;

}

}

// Finally return the result

// Go backwards through the path with highest probability at each state

String result = "";

int letter = backptr[index.length-1][Key.START_END];

for( int back = index.length-2; back>=0; back-- ) {

result = Key.indexToChar( letter ) + result;

letter = backptr[back][letter];

}

return result;

}

/** Constructor: Initializes the A and B matrices. */

public ViterbiBigramDecoder( String filename ) {

init_a( filename );

init_b();

}

}

```
