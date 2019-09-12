package io.github.ashward.codechallenge.wordchain.worker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkerBidirectional extends AbstractWorker {
  public WorkerBidirectional(Set<String> sameLengthWords, String startWord, String endWord) {
    super(sameLengthWords, startWord, endWord);
  }

  public synchronized List<String> getPath() {
    boolean exhausted = false;

    // Holds a map of a word to the previous word in the shortest chain (from the start)
    Map<String, String> fromStart = new HashMap<>();

    // Holds a map of a word to the next word in the shortest chain (to the end)
    Map<String, String> fromEnd = new HashMap<>();

    // This holds the current set of words we're looking at when traversing from the start
    Set<String> currentWordsFromStart = new HashSet<>();

    // This holds the current set of words we're looking at when traversing from the end
    Set<String> currentWordsFromEnd = new HashSet<>();

    // Populate the current words with the start and end words respectively
    currentWordsFromStart.add(startWord);
    currentWordsFromEnd.add(endWord);

    fromStart.put(startWord, null);
    fromEnd.put(endWord, null);

    // We will populate this if we find a common word between the
    // search from the start and the search from the end
    String foundIntersectingWord = null;

    outer:
    while (!exhausted && (foundIntersectingWord == null)) {
      exhausted = true;

      Set<String> newWordsFromStart = new HashSet<>();
      Set<String> newWordsFromEnd = new HashSet<>();

      for (String word : currentWordsFromStart) {
        // For each of the word in our current search list, we will iterate through the next set of words
        Set<String> nextWords = getNextWords(word);

        for (String nextWord : nextWords) {
          if (!fromStart.containsKey(nextWord)) {
            fromStart.put(nextWord, word);
            newWordsFromStart.add(nextWord);
            if(exhausted) {
              exhausted = false;
            }
          }

          if (currentWordsFromEnd.contains(nextWord)) {
            foundIntersectingWord = nextWord;
            break outer;
          }
        }
      }

      currentWordsFromStart = newWordsFromStart;

      // This is exactly the same as the search from the start, only in reverse
      for (String word : currentWordsFromEnd) {
        Set<String> nextWords = getNextWords(word);

        for (String nextWord : nextWords) {
          if (!fromEnd.containsKey(nextWord)) {
            fromEnd.put(nextWord, word);
            newWordsFromEnd.add(nextWord);
            if(exhausted) {
              exhausted = false;
            }
          }

          if (currentWordsFromStart.contains(nextWord)) {
            foundIntersectingWord = nextWord;
            break outer;
          }
        }
      }

      currentWordsFromEnd = newWordsFromEnd;
    }

    if (foundIntersectingWord != null) {
      // We use a linked list because we're going to be appending stuff to both ends
      LinkedList<String> result = new LinkedList<>();

      String parentWord = foundIntersectingWord;

      // Follow the intersecting word back to the start to construct the first part of the chain
      do {
        result.offerFirst(parentWord);
      } while ((parentWord = fromStart.get(parentWord)) != null);

      parentWord = foundIntersectingWord;

      // Follow the intersecting word forwards to the end to construct the last part of the chain
      while ((parentWord = fromEnd.get(parentWord)) != null) {
        result.offerLast(parentWord);
      }

      return result;
    }

    return null;
  }
}
