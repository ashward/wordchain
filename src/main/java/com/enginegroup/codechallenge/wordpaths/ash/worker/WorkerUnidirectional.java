package com.enginegroup.codechallenge.wordpaths.ash.worker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated
 */
public class WorkerUnidirectional extends AbstractWorker {
  public WorkerUnidirectional(Set<String> sameLengthWords, String startWord, String endWord) {
    super(sameLengthWords, startWord, endWord);
  }

  public List<String> getPath() {
    boolean exhausted = false;

    Map<String, String> fromStart = new HashMap<>();
    Set<String> currentWordsFromStart;

    currentWordsFromStart = new HashSet<>();
    currentWordsFromStart.add(startWord);

    fromStart.put(startWord, null);

    while (!currentWordsFromStart.isEmpty()) {

      Set<String> newWordsFromStart = new HashSet<>();

      for (String word : currentWordsFromStart) {

        for (String nextWord : getNextWords(word)) {
          if (word.equals(endWord)) {
            LinkedList<String> result = new LinkedList<>();

            String parentWord = word;

            do {
              result.offerFirst(parentWord);
            } while ((parentWord = fromStart.get(parentWord)) != null);

            return result;
          }

          if (!fromStart.containsKey(nextWord)) {
            fromStart.put(nextWord, word);
            newWordsFromStart.add(nextWord);
          }
        }
      }

      currentWordsFromStart = newWordsFromStart;
    }

    return null;
  }
}
