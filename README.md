# N-gram-word-predictor-and-autocorrect
Word Prediction and Auto-correct using NLP tools (n-gram backoff, viterbi decoding as a spell checker, letter-level backoff prediction) in Java.

@init_class: GUI.java
@authors: Agranya Pratap Singh, Manish Kaswan

The program is run through the GUI.java file with the following flags determining project functioning:

`java GUI -n <number> (2,3,4) -v <number> (2,3)`

the -n flag will determine the n-gram level, and the v-flag will determine the viterbi decoding level
if no arguments are given, the program will initialize to a bigram model for both prediction and decoding


GUI usage:
-The large textfield is for user input only, the bottom one is used to display predictions

-The first word of each phrase (after program startup, period, or semicolon) will not display any predictions

-After predictions are available (wordcount > 0), each letter typed into the textfield should prune the list
       of possible predictions to only include those with the input stem (if any)

-In order to autocomplete a word to the most likely prediction displayed in bottom textfield,
     either with no letters typed or halfway through a word, press the *ENTER* key. You will notice
     that the letter saved variable updates your letters saved progress on the terminal window every time 
     you use a predicted word in your text

-A period and semicolon resets the word count; grams do not carry through this punctuation

-If a word is not contained in dictionary, which is checked after a *SPACE* key, the viterbi will attempt to decode automatically.



